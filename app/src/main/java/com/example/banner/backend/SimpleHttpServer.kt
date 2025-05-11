package com.example.banner.backendS
import com.example.backend_banner.backend.Controllers.CareerController
import com.google.gson.Gson
import java.net.ServerSocket
import java.net.Socket
import java.io.*

class SimpleHttpServer(private val port: Int) {
    fun start() {
        Thread {
            val serverSocket = ServerSocket(port)
            println("Servidor iniciado en puerto $port")

            while (true) {
                val clientSocket = serverSocket.accept()
                handleRequest(clientSocket)
            }
        }.start()
    }

    private fun handleRequest(clientSocket: Socket) {
        val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
        val writer = PrintWriter(clientSocket.getOutputStream(), true)

        val request = reader.readLine()
        val path = request?.split(" ")?.get(1) ?: "/"

        when (path) {
            "/api/careers" -> {
                val careers = CareerController().getAllCareers()
                sendJsonResponse(writer, careers)
            }
            else -> {
                writer.println("HTTP/1.1 404 Not Found")
                writer.println()
            }
        }
        clientSocket.close()
    }

    private fun sendJsonResponse(writer: PrintWriter, data: Any) {
        val json = Gson().toJson(data)
        writer.println("HTTP/1.1 200 OK")
        writer.println("Content-Type: application/json")
        writer.println("Connection: close")
        writer.println()
        writer.println(json)
    }
}