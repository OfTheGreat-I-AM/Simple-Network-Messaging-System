/**
 * TCPServer.java
 *
 * Simple TCP server that accepts client backups on port 2025.
 * The server expects the first line sent to be the client name, followed by the
 * content of the client's chat history. The server appends the received history
 * to a server-side file and echoes a confirmation message to the client.
 *
 * @author: Musa Nkosi
 * Purpose: Backup receiver for the practical assignment
 */

package csc2b.tcp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCPServer - listens on PORT and accepts client backups.
 */
public class TCPServer {
    private static final int PORT = 2025;
    private static final String SERVER_STORAGE_FILE = "data/server_chat_backup.txt";

    /**
     * main - starts the server that loops, accepting client connections.
     *
     * @param args command line args (unused)
     */
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("TCP Server running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // block until client connects
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Handle the client in the current thread (single-client-per-connection model)
                try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
                ) {
                    String clientName = in.readLine();  // First line = client name
                    String line;
                    StringBuilder fileContent = new StringBuilder();

                    // Read until client closes output stream (readLine returns null)
                    while ((line = in.readLine()) != null) {
                        fileContent.append(line).append(System.lineSeparator());
                    }

                    // Append the backup to the server storage file
                    try (FileWriter writer = new FileWriter(SERVER_STORAGE_FILE, true)) {
                        writer.write("===== Backup from " + clientName + " =====\n");
                        writer.write(fileContent.toString());
                        writer.write("\n");
                    }

                    // Echo confirmation to client
                    out.println("Backup received successfully from " + clientName);
                    System.out.println("Backup saved for client: " + clientName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
