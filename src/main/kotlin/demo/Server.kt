/**
* @author  Stefano Fedeli
* @version 1.0
* @since   2021-06-17 
*/
package demo

import demo.ConcurrentQ
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess


/**
 * ClientHandler class handles the client connection, commands and its interaction with the queue
 * Note that this object is handle by a single thread.
 * @param client Socket object that keeps track of the client connection.
 * @param queue Singleton queue object. 
 */
class ClientHandler(client: Socket, queue: ConcurrentQ) {

    /**
    * Socket object, keeps track of client connection
    */
    private val client: Socket = client
    /**
    * Object for reading data coming from the client
    */
    private val reader: Scanner = Scanner(client.getInputStream())
    /**
    * Object for sending data to client
    */
    private val writer: OutputStream = client.getOutputStream()
    /**
    * A Thread-safe singleton object that keeps the state of the queue
    */
    private val storage: ConcurrentQ = queue
    /**
    * Whetever or not keep listening to client
    */
    private var running: Boolean = false



    /**
    * Thread socket listening loop.
    */
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
                shutdown()
                exitProcess(-1)
            } finally {

            }

        }
        exitProcess(0)
    }
    
    /**
    * Decode message and execute the right logic
    *
    * @param cmd unparsed command to execute.
    * @param arguments unparsed arguments string.
    */
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

    /**
    * Send bytestream into socket
    *
    * @param message string to send.
    */
    private fun write(message: String) {
        writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }

    /**
    * Shutdown routine, close connection
    */
    private fun shutdown() {
        running = false
        client.close()
        println("${client.inetAddress.hostAddress} closed the connection")
    }

}



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