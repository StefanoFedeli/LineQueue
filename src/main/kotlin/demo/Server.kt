package demo

import demo.ConcurrentQ
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess


fun main(args: Array<String>) { 
    val server = ServerSocket(10042)
    val storage = ConcurrentQ
    println("Server is running on port ${server.localPort}")

    while (true) {
        val client = server.accept()
        println("Client connected: ${client.inetAddress.hostAddress}")

        // Run client in it's own thread.
        thread { ClientHandler(client, storage).run() }
    }

}

class ClientHandler(client: Socket, queue: ConcurrentQ) {
    private val client: Socket = client
    private val reader: Scanner = Scanner(client.getInputStream())
    private val writer: OutputStream = client.getOutputStream()
    private val storage: ConcurrentQ = queue
    private var running: Boolean = false

    fun run() {
        running = true

        while (running) {
            try {
                val text = reader.nextLine()

                if (text.contains("SHUTDOWN")){
                    shutdown()
                    exitProcess(0)
                }

                if (text.length > 2) {
                    val values = text.split('|')
                    println(values)
                    decodeCommand(values[0],values[1])
                }
            } catch (ex: Exception) {
                // TODO: Implement exception handling
                shutdown()
                exitProcess(-1)
            } finally {

            }

        }
        exitProcess(0)
    }

    private fun decodeCommand(cmd: String, arguments: String) {
        when (cmd) {
            "GET" -> {
                val results: MutableList<String> = storage.extract(arguments.toInt())
                if (results.size == 0) {
                    println("[SERVER LOG] BAD REQUEST")
                    write("ERR\r")
                } else {
                    results.forEach { msg -> write(msg) }
                }
                write("*END*")
            }
            "PUT" -> {
                storage.put(arguments)
            }
            "SHUTDOWN" -> shutdown()
            else -> print("COMMAND NOT AVAILABLE")
        }
    }

    private fun write(message: String) {
        writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }

    private fun shutdown() {
        running = false
        client.close()
        println("${client.inetAddress.hostAddress} closed the connection")
    }

}