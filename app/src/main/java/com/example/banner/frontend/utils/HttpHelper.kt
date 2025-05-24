import android.util.Log
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL

//ruta para probar en el navegador: http://localhost:8080/api/
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

    fun postRaw(url: String, body: String): String? {
        return try {
            val connection = URL("$BASE_URL/$url").openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            // Escribir el cuerpo de la solicitud
            val outputStream = connection.outputStream
            outputStream.write(body.toByteArray())
            outputStream.flush()
            outputStream.close()

            // Leer la respuesta
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                // Leer el error stream si hay un código de error
                connection.errorStream?.bufferedReader()?.use { it.readText() }?.also {
                    Log.e("HTTP_ERROR", "Error $responseCode: $it")
                }
                null
            }
        } catch (e: Exception) {
            Log.e("HTTP_EXCEPTION", "Error en postRaw: ${e.message}")
            null
        }
    }

    fun get(endpoint: String): String? {
        return try {
            // Eliminamos barras adicionales al principio del endpoint si existen
            val cleanEndpoint = endpoint.removePrefix("/")
            val fullUrl = "$BASE_URL$cleanEndpoint"

            Log.d("HTTP_GET", "Requesting URL: $fullUrl")

            val connection = URL(fullUrl).openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
                setRequestProperty("Accept", "application/json")
            }

            val responseCode = connection.responseCode
            Log.d("HTTP_GET", "Response code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                Log.d("HTTP_GET", "Response: $response")
                response
            } else {
                val error = connection.errorStream?.bufferedReader()?.use { it.readText() }
                Log.e("HTTP_GET", "Error $responseCode: $error")
                null
            }
        } catch (e: Exception) {
            Log.e("HTTP_GET", "Exception: ${e.message}", e)
            null
        }
    }


    fun postRequest(endpoint: String, requestBody: String): String? {
        return try {
            val url = URL("${BASE_URL}$endpoint")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")

            val outputStream = connection.outputStream
            outputStream.write(requestBody.toByteArray())
            outputStream.flush()
            outputStream.close()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }
                inputStream.close()
                response
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("HTTP_POST", "Error in POST request", e)
            null
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
                setRequestProperty("Connection", "close")
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
                if (response.isNullOrBlank()) {
                    Log.e("HTTP", "Respuesta vacía, no se puede parsear")
                    null
                } else {
                    gson.fromJson(response, responseType)
                }
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