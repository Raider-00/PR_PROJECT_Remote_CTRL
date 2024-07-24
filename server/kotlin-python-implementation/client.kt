import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import kotlin.system.exitProcess
import java.util.Scanner


fun read_from_server(inputstream: BufferedReader) : String{
    val sb = StringBuilder()
    var line: String?
    while (true) {
        line = inputstream.readLine()
        if (line != "NULL_MARKER" && line != null) {
            //println(line)
            sb.append(line+"\n")
        }
        else if (line == null){
            //println("Server exited.")
            sb.append("Server Exited.")
            break
        }
        else {
            break
        }
    }
    return sb.toString()
}

fun command(cmd : String,outputStream: OutputStreamWriter,inputStream: BufferedReader) : String {
    outputStream.write(cmd+"\n")
    outputStream.flush()
    val output = read_from_server(inputStream)
    //println(output)
    return output
}

fun main() {
    val serverAddress = "localhost"
    val serverPort = 12345

    try {
        // Create a socket to connect to the server
        val socket = Socket(serverAddress, serverPort)
        println("Connected to the server at $serverAddress on port $serverPort")

        // Create input and output streams
        val outputStream = OutputStreamWriter(socket.getOutputStream())
        val inputStream = BufferedReader(InputStreamReader(socket.getInputStream()))

        val scanner = Scanner(System.`in`)
        do {
            var cmd = scanner.nextLine()
            val output = command(cmd, outputStream, inputStream)
            println(output)
        }while(cmd != "exit")

        outputStream.close()
        inputStream.close()
        socket.close()
        println("Connection closed")

    } catch (e: Exception) {
        println("Socket is Closed.\nOR Server not started or running.\nExiting now.")
        exitProcess(-1)
        //e.printStackTrace()
    }
}
