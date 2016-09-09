import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Send the data to the server (Heartbeat sender)
 * 
 * @author rebaz
 *
 */

class Sender {
	HearbeatNetWorkIO IO;
	BufferedReader inFromUser;
	boolean sendMessage = true;
	boolean connectionLost = false;

	DroneMoveModel rmm;
	ZoneModel zm;
	CheckDroneConnection chkc;

	Sender(String address, int port, DroneMoveModel rmm) {
		IO = new HearbeatNetWorkIO(address, port, zm);
		inFromUser = new BufferedReader(new InputStreamReader(System.in));
		this.rmm = rmm;
		this.zm = rmm.zm;
		this.chkc = new CheckDroneConnection(this.rmm);

	}

	void start() {
		IO.sendMessage("Connection:setup");

		if (!IO.getMessage().isEmpty()) {
			System.out.println("Connected to remote controller...");
			startGame();
		}

		IO.sendMessage("Connection:quit");
	}

	private void startGame() {
		chkc.start();
		while (true) {
			if (!this.rmm.connectionLost) {
				IO.sendMessage("Message:" + getLine());
				String ms = new String(ProcessCommand(IO.getMessage()));

				if (ms.equals("inzone")) {
					this.rmm.setConnectionLost(false);
				} else {
					this.rmm.setConnectionLost(true);
				}
			}
		}
	}

	private String getLine() {
		return "Drone location: X = " + this.zm.getLastX() + " Y = " + this.zm.getLastY();
	}

	private String ProcessCommand(String Command) {
		String external = Command.substring(Command.indexOf(":") + 1, Command.indexOf(0));
		return external;
	}
}

class HearbeatNetWorkIO {
	DatagramSocket clientSocket;
	int port;
	DatagramPacket Packet;
	InetAddress ServerIPAddress;
	byte[] Data;

	int lastUpdateTime;

	HearbeatNetWorkIO(String address, int port, ZoneModel zm) {
		this.port = port;
		try {
			ServerIPAddress = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			System.err.println("Error: Host invalid");
			System.exit(1);
		}
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			System.err.println("Error: Socket could not be created");
			System.exit(1);
		}
	}

	public String getMessage() {

		Data = new byte[1024];
		Packet = new DatagramPacket(Data, Data.length);
		try {
			clientSocket.receive(Packet);
		} catch (IOException e) {
			System.err.println("Error: error while recieving packet");
		}
		return new String(Packet.getData());
	}

	public void sendMessage(String line) {

		Data = line.getBytes();
		Packet = new DatagramPacket(Data, Data.length, ServerIPAddress, port);
		try {
			clientSocket.send(Packet);
		} catch (IOException e) {
			System.err.println("Error: error while sending packet");
		}
	}

}

class CheckDroneConnection extends Thread {

	DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	DroneMoveModel rmm;

	CheckDroneConnection(DroneMoveModel rmm) {
		this.rmm = rmm;
	}

	public void run() {
		while (true) {
			try {
				sleep(3000);
				if (rmm.connectionLost || !rmm.zm.isDroneInZone) {
					printError();
					rmm.setConnectionLost(true);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void printError() {
		System.err.println("***** [ Drone ] FAIL: Connection Lost at " + df.format(new Date()) + "*****\n");
	}
}
