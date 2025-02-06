package br.com.sec4you.authfy.app

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL


object NetworkUtils {
  const val TAG = "AUTHCUBE:NET_UTILS"


  sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(
      val code: Int,
      val message: String,
      val errorBody: String? = null,
    ) : NetworkResult<Nothing>()

    data class Exception(val e: Throwable) : NetworkResult<Nothing>()
  }

  suspend fun doGet(
    urlString: String, headers: Map<String, String>?
  ): NetworkResult<Map<String, Any>> {
    return withContext(Dispatchers.IO) {
      try {
      val url = URL(urlString)
      val connection = url.openConnection() as HttpURLConnection

      // Configurar headers
          if (headers != null) {
              for ((key, value) in headers) {
                  connection.setRequestProperty(key, value)
              }
          }

      try {
        connection.requestMethod = "GET"
        connection.connectTimeout = 15000
        connection.readTimeout = 15000

        val responseCode = connection.responseCode
        Log.d(TAG, "Response code: $responseCode")

        when {
          responseCode in 200..299 -> {
            // success resp
            val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
            Log.d(TAG, "Success response: $responseBody")

            try {
              val result = Gson().fromJson<Map<String, Any>>(
                responseBody,
                object : TypeToken<Map<String, Any>>() {}.type
              )
              NetworkResult.Success(result)
            } catch (e: JsonSyntaxException) {
              Log.e(TAG, "JSON parsing error", e)
              NetworkResult.Error(
                responseCode,
                "Invalid JSON response",
                responseBody
              )
            }
          }

          else -> {
            val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() }
            Log.e(TAG, "Error response: $errorBody")
            NetworkResult.Error(
              responseCode,
              connection.responseMessage ?: "Unknown error",
              errorBody
            )
          }
        }
      } catch (e: Exception) {
        Log.e(TAG, "Connection error", e)
        NetworkResult.Exception(e)
      } finally {
        connection.disconnect()
      }
      } catch (e: Exception) {
        Log.e(TAG, "URL connection error", e)
        NetworkResult.Exception(e)
      }
    }
  }


  suspend fun doPost(
    urlString: String,
    headers: Map<String, String>,
    body: String
  ): NetworkResult<Map<String, Any>> {
    return withContext(Dispatchers.IO) {
      try {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection

        // Configurar headers
        for ((key, value) in headers) {
          connection.setRequestProperty(key, value)
        }

        try {
          connection.requestMethod = "POST"
          connection.doOutput = true
          connection.doInput = true
          connection.connectTimeout = 15000
          connection.readTimeout = 15000

          // Escrever o body da requisição
          val outputStream = connection.outputStream
          outputStream.write(body.toByteArray())
          outputStream.flush()

          val responseCode = connection.responseCode
          Log.d(TAG, "Response code: $responseCode")

          when {
            responseCode in 200..299 -> {
              // success resp
              val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
              Log.d(TAG, "Success response: $responseBody")

              try {
                val result = Gson().fromJson<Map<String, Any>>(
                  responseBody,
                  object : TypeToken<Map<String, Any>>() {}.type
                )
                NetworkResult.Success(result)
              } catch (e: JsonSyntaxException) {
                Log.e(TAG, "JSON parsing error", e)
                NetworkResult.Error(
                  responseCode,
                  "Invalid JSON response",
                  responseBody
                )
              }
            }

            else -> {
              val errorBody = connection.errorStream?.bufferedReader()?.use {
                it.readText()
              }
              Log.e(TAG, "Error response: $errorBody")
              NetworkResult.Error(
                responseCode,
                connection.responseMessage ?: "Unknown error",
                errorBody
              )
            }
          }
        } catch (e: Exception) {
          Log.e(TAG, "Connection error", e)
          NetworkResult.Exception(e)

        } finally {
          connection.disconnect()
        }

      } catch (e: Exception) {
        Log.e(TAG, "URL connection error", e)
        NetworkResult.Exception(e)
      }
    }
  }
}

