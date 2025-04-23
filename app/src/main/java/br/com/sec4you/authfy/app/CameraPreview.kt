import android.Manifest
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.camera.core.ImageProxy


@Composable
fun QrCodeScanner(
    modifier: Modifier = Modifier,
    onQrCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    // --- Gerenciamento de Permissões ---
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            hasCameraPermission = isGranted
            if (!isGranted) {
                Log.w("QrCodeScanner", "Permissão da câmera negada.")
                Toast.makeText(context, "Permissão da câmera negada.", Toast.LENGTH_LONG).show()
            }
        }
    )

    // Verifica e solicita a permissão quando o Composable é iniciado
    LaunchedEffect(key1 = true) {
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
            android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                hasCameraPermission = true
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    // --- Fim Gerenciamento de Permissões ---

    Box(modifier = modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    // Configuração do CameraX
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        // Configuração do Preview
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        // Configuração da Análise de Imagem
                        val imageAnalyzer = ImageAnalysis.Builder()
                            // Definir estratégia de backpressure. KEEP_ONLY_LATEST descarta frames antigos.
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { imageAnalysis ->

                                // 1. Crie a instância do seu Analyzer explicitamente
                                val analyzerInstance = QrCodeAnalyzer(
                                    context = ctx,
                                    onQrCodeScanned = { url ->
                                        onQrCodeScanned(url) // Chama o callback externo
                                        // cameraProvider.unbindAll() // Opcional: parar scan
                                    }
                                    // Se tiver o callback de erro, passe-o aqui também:
                                    // onQrCodeError = { error -> /* Lida com erro */ }
                                )
                                // 2. Passe a instância criada para setAnalyzer
                                imageAnalysis.setAnalyzer(cameraExecutor, analyzerInstance)
                            }

                        // Selecionar a câmera traseira
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            // Desvincular use cases antes de revincular
                            cameraProvider.unbindAll()

                            // Vincular use cases à câmera e ao ciclo de vida
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner, cameraSelector, preview, imageAnalyzer
                            )
                        } catch (exc: Exception) {
                            Log.e("QrCodeScanner", "Falha ao vincular use cases da câmera", exc)
                        }

                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Mostrar um texto ou indicador enquanto a permissão não é concedida
            Toast.makeText(context,
                "Permissão da câmera necessária.",
                Toast.LENGTH_LONG).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}

// --- Classe Analisadora de QR Code ---
private class QrCodeAnalyzer(
    private val context: Context,
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    // Configurar o scanner para detectar apenas QR Codes
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    private val scanner = BarcodeScanning.getClient(options)
    // Flag para evitar chamadas múltiplas rápidas do callback
    private var isScanning = true

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (!isScanning) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        if (barcode.valueType == Barcode.TYPE_URL) {
                            barcode.url?.url?.let { url ->
                                Log.d("QrCodeAnalyzer", "URL encontrada: $url")
                                isScanning = false
                                onQrCodeScanned(url)



                                // ***** MOSTRAR TOAST NA MAIN THREAD *****
                                ContextCompat.getMainExecutor(context).execute {
                                    // Usar applicationContext é mais seguro aqui
                                    Toast.makeText(
                                        context.applicationContext, // Use Application Context
                                        "URL (Analyzer): $url",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                // ******************************************

                                return@addOnSuccessListener
                            }
                        } else {
                            Log.d("QrCodeAnalyzer", "QR Code encontrado (não URL): ${barcode.rawValue}")

                            // ***** MOSTRAR TOAST NA MAIN THREAD *****
                            ContextCompat.getMainExecutor(context).execute {
                                // Usar applicationContext é mais seguro aqui
                                Toast.makeText(
                                    context.applicationContext, // Use Application Context
                                    "QR Code encontrado (não URL): ${barcode.rawValue}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            // ******************************************

                            // Se fosse aceitar qualquer valor:
                            // barcode.rawValue?.let { value ->
                            //     isScanning = false
                            //     onQrCodeScanned(value)
                            //     return@addOnSuccessListener
                            // }
                        }
                    }
                    // Se o loop terminar sem encontrar uma URL, isScanning continua true
                    // para o próximo frame.
                }
                .addOnFailureListener { e ->
                    Log.e("QrCodeAnalyzer", "Falha na leitura do Barcode", e)
                    // Garante que paramos de escanear mesmo em caso de falha neste frame específico,
                    // Opcional: resetar isScanning aqui se necessário.
                }
                .addOnCompleteListener {
                    imageProxy.close()
                    // Se nenhum código foi achado no success E não houve falha,
                }
        } else {
            imageProxy.close()
        }
    }
}
