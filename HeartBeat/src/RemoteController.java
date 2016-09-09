
import java.io.*;
import java.net.*;

/**
 * @author Rebaz
 *
 */

public class RemoteController {
	static int DEFAULT_PORT = 7475;

	public static void main(String[] args) {
		int port = (args.length > 0) ? Integer.parseInt(args[0]) : DEFAULT_PORT;
		Reciever reciever = new Reciever(port);
		reciever.start();
	}
}

class Reciever {
	ServerNetWorkIO IO;
	boolean isAlive = true;

	public Reciever(int port) {
		IO = new ServerNetWorkIO(port);
	}

	void start() {
		System.out.println("Connecting to drone ...");
		while (isAlive) {
			DatagramPacket packet = IO.getPacket();
			if (packet != null) {
				ProccessInput(packet);
			}
		}
	}

	/**
	 * Process the input data received from the drone
	 * 
	 * @param packet
	 */

	public void ProccessInput(DatagramPacket packet) {
		String packetMessage = new String(packet.getData());
		String message = packetMessage.substring(packetMessage.indexOf(':') + 1, packetMessage.indexOf(0));
		String result = "";
		if (message.equals("setup")) {
			System.out.println("Connection established with the drone...");
			result = "connected";
		} else {
			System.out.println(message);
			result = "Message:inzone";
		}

		IO.sendPacket(
				new DatagramPacket(result.getBytes(), result.getBytes().length, packet.getAddress(), packet.getPort()));

		try {
			Thread.sleep(2000); // check every two seconds
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

/**
 * Perform network operations
 * 
 * @author Rebaz
 *
 */

class ServerNetWorkIO {
	private DatagramSocket serverSocket;
	private DatagramPacket Packet;
	private byte[] Data;

	ServerNetWorkIO(int port) {
		try {
			serverSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			System.err.println("Error: Socket could not be created");
			System.exit(1);
		}
	}

	public DatagramPacket getPacket() {
		Data = new byte[1024];
		Packet = new DatagramPacket(Data, Data.length);
		try {
			serverSocket.receive(Packet);
		} catch (IOException e) {
			System.err.println("Error: error while recieving packet");
			return null;
		}
		return Packet;
	}

	public void sendPacket(DatagramPacket packet) {
		try {
			serverSocket.send(packet);
		} catch (IOException e) {
			System.err.println("Error: error while sending packet");
		}
	}
}
