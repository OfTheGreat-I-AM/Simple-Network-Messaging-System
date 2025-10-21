/**
 * UDPServer.java
 *
 * Simple UDP broadcaster that sends a hardcoded announcement to the broadcast address
 * on the ANNOUNCEMENT_PORT. Run this program to test clients' announcement listeners.
 *
 * Author: Musa Nkosi
 * Purpose: Broadcast announcements (UDP)
 */

package acsse.csc2b.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * UDPServer - broadcasts a single announcement to 255.255.255.255 on ANNOUNCEMENT_PORT.
 */
public class UDPServer {
    private static final int ANNOUNCEMENT_PORT = 2026;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String announcement = "Server maintenance at 17:30 today. Please save your work!";
            byte[] data = announcement.getBytes();

            // Broadcast to the network
            InetAddress broadcastAddr = InetAddress.getByName("255.255.255.255");
            DatagramPacket packet = new DatagramPacket(data, data.length, broadcastAddr, ANNOUNCEMENT_PORT);

            socket.setBroadcast(true);
            socket.send(packet);

            System.out.println("Announcement sent: " + announcement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
