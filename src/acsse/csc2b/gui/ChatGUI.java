/**
 * ChatGUI.java
 *
 * JavaFX GridPane component that contains the client UI for:
 *  - Announcement display (UDP)
 *  - P2P chat window (UDP)
 *  - Backup button (sends local chat history via TCP)
 *
 * @author: Musa Nkosi
 * Purpose: Provide the UI component for the networking client assignment
 */

package acsse.csc2b.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;

/**
 * ChatGUI is a reusable UI component (extends GridPane).
 * It handles UI construction, local file saving, and starts background threads
 * for announcement listening (UDP), P2P listening (UDP), and triggers backup (TCP).
 */
public class ChatGUI extends GridPane {

    // UI controls
    private TextArea announcementsArea;
    private TextArea chatArea;
    private TextField inputField;
    private Button sendButton;
    private Button backupButton;

    // Local file to store chat history (relative path)
    private final String CHAT_HISTORY_FILE = "data/chat_history.txt";

    // Ports used by the application
    private static final int ANNOUNCEMENT_PORT = 2026; // port for UDP announcements
    private static final int P2P_PORT = 3030; // port for P2P UDP chat

    // Default peer info (can be made configurable later)
    private static final String PEER_IP = "127.0.0.1";
    private static final int PEER_PORT = 3030;

    /**
     * Default constructor sets up UI and starts network listeners on background threads.
     */
    public ChatGUI() {
        // Layout configuration
        setPadding(new Insets(10));
        setHgap(10);
        setVgap(10);

        // Announcements text area (non-editable)
        announcementsArea = new TextArea();
        announcementsArea.setEditable(false);
        announcementsArea.setPromptText("Announcements will appear here...");
        announcementsArea.setPrefHeight(100);

        // Chat text area (non-editable)
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setPromptText("Chat messages will appear here...");
        chatArea.setPrefHeight(200);

        // Input field and control buttons
        inputField = new TextField();
        inputField.setPromptText("Enter message...");

        sendButton = new Button("Send");
        backupButton = new Button("Backup");

        // Add controls to the GridPane
        add(new Label("Announcements:"), 0, 0, 2, 1);
        add(announcementsArea, 0, 1, 2, 1);

        add(new Label("Chat:"), 0, 2, 2, 1);
        add(chatArea, 0, 3, 2, 1);

        add(inputField, 0, 4);
        add(sendButton, 1, 4);
        add(backupButton, 1, 5);

        // Button behavior
        sendButton.setOnAction(e -> sendMessage());
        backupButton.setOnAction(e -> backupChatHistory());

        // Start background listeners
        startAnnouncementListener();
        startP2PListener();
    }

    /**
     * sendMessage() - called when the user presses Send.
     * - Appends timestamped message to the chat area
     * - Saves locally to CHAT_HISTORY_FILE
     * - Sends the message to the configured peer via UDP
     */
    private void sendMessage() {
        String msg = inputField.getText().trim();
        if (msg.isEmpty()) return;

        // Build and display time-stamped message locally
        String timeStamped = "[" + LocalDateTime.now() + "] Me: " + msg;
        chatArea.appendText(timeStamped + "\n");
        saveChatLocally(timeStamped);

        // Send via UDP to peer on background thread (non-blocking UI)
        Thread sender = new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket();
                byte[] buffer = msg.getBytes();
                InetAddress peerAddress = InetAddress.getByName(PEER_IP);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, peerAddress, PEER_PORT);
                socket.send(packet);
                socket.close();
            } catch (Exception e) {
                // Update UI with error on JavaFX thread
                Platform.runLater(() -> chatArea.appendText("[ERROR] Failed to send message: " + e.getMessage() + "\n"));
            }
        });
        sender.setDaemon(true);
        sender.start();

        inputField.clear();
    }

    /**
     * backupChatHistory() - Initiates a TCP backup to the TCP server.
     * The actual socket/communication is performed on a background thread to avoid UI blocking.
     */
    private void backupChatHistory() {
        // The actual TCP connection code is wired from Client class or added here.
        // For clarity the GUI triggers the backup thread.
        Thread backupThread = new Thread(() -> {
            try (java.net.Socket socket = new java.net.Socket("localhost", 2025);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                String clientName = "Client1"; // replace with configurable username if desired
                out.println(clientName);

                // Stream file contents to server, if file exists
                File historyFile = new File(CHAT_HISTORY_FILE);
                if (historyFile.exists()) {
                    try (BufferedReader fileReader = new BufferedReader(new FileReader(historyFile))) {
                        String line;
                        while ((line = fileReader.readLine()) != null) {
                            out.println(line);
                        }
                    }
                }

                socket.shutdownOutput(); // signal end of data to server

                // Receive confirmation from the server and show it in UI
                String response = in.readLine();
                Platform.runLater(() -> chatArea.appendText("[SYSTEM] " + response + "\n"));

            } catch (IOException e) {
                Platform.runLater(() -> chatArea.appendText("[ERROR] Backup failed: " + e.getMessage() + "\n"));
            }
        });

        backupThread.setDaemon(true);
        backupThread.start();
    }

    /**
     * saveChatLocally - append a single message line to the local chat history file.
     *
     * @param message String line to append
     */
    private void saveChatLocally(String message) {
        // Ensure parent directory exists (optional)
        File f = new File(CHAT_HISTORY_FILE);
        File parent = f.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (FileWriter writer = new FileWriter(CHAT_HISTORY_FILE, true)) {
            writer.write(message + System.lineSeparator());
        } catch (IOException e) {
            // Print stack trace for debug; also update UI
            e.printStackTrace();
            Platform.runLater(() -> chatArea.appendText("[ERROR] Could not save chat locally: " + e.getMessage() + "\n"));
        }
    }

    /**
     * startAnnouncementListener - background thread that listens for UDP broadcast announcements.
     * When a packet is received, it updates announcementsArea on the JavaFX thread.
     */
    private void startAnnouncementListener() {
        Thread listener = new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(ANNOUNCEMENT_PORT)) {
                byte[] buffer = new byte[1024];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet); // blocking call
                    String announcement = new String(packet.getData(), 0, packet.getLength());

                    // Use Platform.runLater to update UI from background thread
                    Platform.runLater(() -> announcementsArea.appendText("[Announcement] " + announcement + "\n"));
                }
            } catch (Exception e) {
                // Log and show error in GUI
                e.printStackTrace();
                Platform.runLater(() -> announcementsArea.appendText("[ERROR] Announcement listener stopped: " + e.getMessage() + "\n"));
            }
        });
        listener.setDaemon(true);
        listener.start();
    }

    /**
     * startP2PListener - background thread that listens for peer UDP messages on P2P_PORT.
     * Received messages are shown in the chat area and saved locally.
     */
    private void startP2PListener() {
        Thread listener = new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(P2P_PORT)) {
                byte[] buffer = new byte[1024];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength());

                    String timeStamped = "[" + LocalDateTime.now() + "] Peer: " + received;
                    Platform.runLater(() -> chatArea.appendText(timeStamped + "\n"));
                    saveChatLocally(timeStamped);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> chatArea.appendText("[ERROR] P2P listener error: " + e.getMessage() + "\n"));
            }
        });
        listener.setDaemon(true);
        listener.start();
    }
}
