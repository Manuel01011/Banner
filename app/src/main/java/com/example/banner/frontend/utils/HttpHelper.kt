import android.util.Log
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL

object HttpHelper {
    private const val BASE_URL = "http://10.0.2.2:8080/api/"
    private val gson = Gson()

    // Método genérico para solicitudes GET
    fun <T> getRequest(endpoint: String, responseType: Type): T? {
        val url = URL("$BASE_URL$endpoint")
        val connection = url.openConnection() as HttpURLConnection
        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                gson.fromJson(reader, responseType)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            connection.disconnect()
        }
    }

    // Método para enviar datos (POST/PUT)
    fun <T> sendRequest(
        endpoint: String,
        method: String,
        requestBody: Any?,
        responseType: Class<T>
    ): T? {
        val url = URL("$BASE_URL$endpoint")
        val connection = url.openConnection() as HttpURLConnection
        var reader: BufferedReader? = null

        return try {
            connection.apply {
                requestMethod = method
                connectTimeout = 15000 // Aumenta timeout a 15 segundos
                readTimeout = 15000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Connection", "close") // Añade esto
            }

            // Escribir cuerpo
            requestBody?.let {
                connection.outputStream.use { os ->
                    os.write(gson.toJson(it).toByteArray())
                    os.flush()
                }
            }

            val responseCode = connection.responseCode
            Log.d("HTTP", "Response code: $responseCode")

            // Leer respuesta
            val inputStream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            reader = BufferedReader(InputStreamReader(inputStream))
            val response = reader.readText()
            Log.d("HTTP", "Raw response: $response")

            if (responseType == String::class.java) {
                response as T
            } else {
                gson.fromJson(response, responseType)
            }

        } catch (e: Exception) {
            Log.e("HTTP", "Request failed: ${e.message}")
            null
        } finally {
            reader?.close()
            connection.disconnect()
        }
    }

    fun getRawResponse(endpoint: String): String? {
        val url = URL("$BASE_URL$endpoint")
        val connection = url.openConnection() as HttpURLConnection
        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(connection.inputStream)).use {
                    it.readText()
                }
            } else {
                Log.e("HTTP", "GET failed with code: ${connection.responseCode}")
                null
            }
        } catch (e: Exception) {
            Log.e("HTTP", "GET failed", e)
            null
        } finally {
            connection.disconnect()
        }
    }

    // Método para DELETE
    fun deleteRequest(endpoint: String): Boolean {
        val url = URL("$BASE_URL$endpoint")
        val connection = url.openConnection() as HttpURLConnection
        return try {
            connection.requestMethod = "DELETE"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            connection.disconnect()
        }
    }
}