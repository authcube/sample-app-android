package br.com.sec4you.authfy.app

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object NetworkUtils {

  suspend fun doGet(urlString: String, headers: Map<String, String>): Map<String, Any>? {
    return withContext(Dispatchers.IO) {
      val url = URL(urlString)
      val connection = url.openConnection() as HttpURLConnection

      // Configurar headers
      for ((key, value) in headers) {
        connection.setRequestProperty(key, value)
      }

      try {
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {

          val inputStream = connection.inputStream
//          inputStream.bufferedReader().use { it.readText() }
          val gson = Gson() // Create a Gson instance
          val result: Map<String, Any> = gson.fromJson(
            inputStream.bufferedReader().use { it.readText() },
            Map::class.java
          ) as Map<String, Any>
          result
        } else {
          // Lidar com erro na requisição
          null
        }
      } finally {
        connection.disconnect()
      }
    }
  }


  suspend fun doPost(urlString: String, headers: Map<String, String>, body: String): String? {
    return withContext(Dispatchers.IO) {
      val url = URL(urlString)
      val connection = url.openConnection() as HttpURLConnection

      // Configurar headers
      for ((key, value) in headers) {
        connection.setRequestProperty(key, value)
      }

      try {
        connection.requestMethod = "POST"
        connection.doOutput = true

        // Escrever o body da requisição
        val outputStream = connection.outputStream
        outputStream.write(body.toByteArray())
        outputStream.flush()

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
          val inputStream = connection.inputStream
          inputStream.bufferedReader().use { it.readText() }
        } else {
          // Lidar com erro na requisição
          null
        }
      } finally {
        connection.disconnect()
      }
    }
  }
}
