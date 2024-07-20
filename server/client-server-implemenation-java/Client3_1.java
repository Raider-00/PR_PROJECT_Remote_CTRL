import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client3_1 {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Change this to the IP address or hostname of your server
        int port = 55555;

        try {
            // Connect to the server
            Socket socket = new Socket(serverAddress, port);
            System.out.println("Connected to server");

            // Set up input and output streams for communication
            InputStream serverInput = socket.getInputStream();
            OutputStream serverOutput = socket.getOutputStream();

            Thread stringcommand = new Thread(() -> {
               try {
                    byte[] b="ls\n".getBytes();
                    serverOutput.write(b);
                    serverOutput.flush();
               }
               catch(IOException e){
                   e.printStackTrace();
                }
            });


            // Create threads to handle input and output streams in both directions
            Thread inputThread = new Thread(() -> {
                try {
                    int character;
                    while ((character = System.in.read()) != -1) {
                        serverOutput.write(character);
                        serverOutput.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Thread outputThread = new Thread(() -> {
                try {
                    int character;
                    while ((character = serverInput.read()) != -1) {
                        System.out.write(character);
                        System.out.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Start the input and output threads
//            stringcommand.start();
            inputThread.start();
            stringcommand.start();
            outputThread.start();



            // Wait for the threads to finish
            stringcommand.join();
            inputThread.join();
            outputThread.join();

            // Close the socket
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
