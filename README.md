# Simple-Network-Messaging-System
This project is a simple messaging system that demonstrates key network programming concepts in Java. It includes a TCP server for message storage and backup, a UDP server for broadcasting announcements, and a P2P client for direct chat between users. I'd make a joke about UDP but you wouldn't get it... ba dum tss. Moving on, the system features a graphical user interface (GUI) where users can send and receive messages, view announcements, and back up their local chat history to the server.

Features:
🧠 TCP Server: Accepts client messages, saves them to a text file, and echoes them back.
📢 UDP Server: Broadcasts announcements to all listening clients.
💬 Peer-to-Peer Chat: Clients can connect directly to exchange messages.
💾 Chat Backup: Upload local chat history to the server.
🪟 Client GUI: Displays announcements, chat window, and backup controls.

Technologies:
- Java (Sockets, Threads, File I/O, GUI – JavaFX)
- TCP/UDP networking
- Text-based message storage

⚙️ Setup & Running
Requirements
- Java 17+ (or your version) installed and configured in PATH
- JavaFX SDK installed on your system (not included in the project files)
- Any Java IDE (Eclipse, IntelliJ IDEA, or NetBeans)

1️⃣ Install and Configure JavaFX
This project uses JavaFX for its GUI.
You’ll need to download and set up JavaFX for your IDE before running the project.
Follow the official guide here:
👉 https://openjfx.io/openjfx-docs/#introduction

2️⃣ Clone the Repository
git clone https://github.com/YourUsername/Simple-Network-Messaging-System.git
cd Simple-Network-Messaging-System

3️⃣ Run the Servers
Start the TCP Server (port 2025)
Start the UDP Server (for announcements)
You can run these from your IDE or directly from the command line:
  java TCPServer
  java UDPServer

4️⃣ Run the Client
Run the client application (GUI) twice to simulate two different users:
  java ClientApp

Each client can send and receive messages directly (P2P).
Both can receive UDP announcements.
Use the Backup button in the GUI to upload chat history to the TCP Server.

5️⃣ Stop the Servers
When done, simply close the server consoles or stop the processes in your IDE.
