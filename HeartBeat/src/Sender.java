import java.io.*;
import java.net.*;

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

	Sender(String address, int port) {
		IO = new HearbeatNetWorkIO(address, port);
		inFromUser = new BufferedReader(new InputStreamReader(System.in));
	}
}

class HeartBeatSender extends Sender implements Runnable {

	ProcessController pc;

	HeartBeatSender(String address, int port, ProcessController pc) {
		super(address, port);
		this.pc = pc;
	}

	@Override
	public void run() {
		IO.sendMessage("HearbeatConnection:setup");

		if (!IO.getMessage().isEmpty()) {
			System.out.println("Connected to remote controller...");
			while (true) {
				IO.sendMessage("Message:" + getLine());
				new String(ProcessCommand(IO.getMessage()));
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		IO.sendMessage("Connection:quit");

	}

	private String getLine() {
		return "" + pc.getRunningProcesses();
	}

	private String ProcessCommand(String Command) {
		String external = Command.substring(Command.indexOf(":") + 1, Command.indexOf(0));
		return external;
	}

}

class SecondProcessManipulator extends Sender implements Runnable, ObstacleDetectImpl {

	ObstacleDetector obstacleDetector;
	DroneMoveModel dmm;
	boolean isRunning = true;
	boolean isApproachingDrone = false;
	String processName;

	int processDeath;

	public int getProcessDeath() {
		return processDeath;
	}

	public void setProcessDeath(int processDeath) {
		this.processDeath = processDeath;
	}

	SecondProcessManipulator(String address, int port, ObstacleDetector obstacleDetector, DroneMoveModel dmm,
			String processName) {
		super(address, port);
		this.obstacleDetector = obstacleDetector;
		this.dmm = dmm;
		this.processName = processName;
	}

	@Override
	public void run() {
		IO.sendMessage("Connection:setup");

		if (!IO.getMessage().isEmpty()) {
			System.out.println("Connected to remote controller...");
			int counter = 0;
			while (isRunning) {
				IO.sendMessage("Message:" + getLine());
				ProcessCommand(IO.getMessage());

				try {
					Thread.sleep(1000);

					counter++;
					if (counter == this.getProcessDeath()) {
						this.isRunning = false;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		IO.sendMessage("Connection:quit");

	}

	private String getLine() {
		String data = new ObstacleDetectModel(obstacleDetector.obstacle, dmm).serializeData();
		return data;
	}

	private boolean ProcessCommand(String Command) {

		if (Command.startsWith("ApproachingDrone")) {
			isApproachingDrone = true;
			return true;
		} else {
			isApproachingDrone = false;
			return false;
		}

	}

	@Override
	public boolean detectedObstacle() {
		return isApproachingDrone;
	}

	@Override
	public boolean isRunning() {
		return this.isRunning;
	}

	@Override
	public void print() {
		// TODO Auto-generated method stub
		System.out.println("Current running process is " + processName);
	}

}

class HearbeatNetWorkIO {

	DatagramSocket clientSocket;
	int port;
	DatagramPacket Packet;
	InetAddress ServerIPAddress;
	byte[] Data;

	HearbeatNetWorkIO(String address, int port) {

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
