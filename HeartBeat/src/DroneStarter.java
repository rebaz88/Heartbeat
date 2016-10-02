import java.util.concurrent.ThreadLocalRandom;

/**
 * Drone starter
 * 
 * @author rebaz
 *
 */
public class DroneStarter {

	static int HEARTBEAT_PORT = 7475;
	static int PROCESS_PORT = 7576;

	public static void main(String[] args) throws Exception {

		int firstProcessDeath = ThreadLocalRandom.current().nextInt(10, 15);
		int secondProcessDeath = ThreadLocalRandom.current().nextInt(20, 25);

		SpaceUI frame = new SpaceUI();
		ZoneModel zm = new ZoneModel(frame.getWidth(), frame.getHeight());

		final DroneMoveModel dmm = new DroneMoveModel(zm);
		Drone tr = new Drone(dmm, firstProcessDeath, secondProcessDeath);

		frame.addKeyListener(tr.new RobotKeyHandler());
		frame.add(tr);

		frame.setVisible(true);

		ObstacleDetector obstacleDetector = new ObstacleDetector(tr.obstacle, dmm, "First Process");
		obstacleDetector.setProcessDeath(firstProcessDeath);
		obstacleDetector.start();

		SecondProcessManipulator secondProcess = new SecondProcessManipulator("localhost", PROCESS_PORT,
				obstacleDetector, dmm, "Second Process");
		secondProcess.setProcessDeath(secondProcessDeath);
		Thread secondProcessThread = new Thread(secondProcess);
		secondProcessThread.start();

		ProcessController pc = new ProcessController(obstacleDetector, secondProcess, dmm);
		pc.start();

		HeartBeatSender heartBeatSender = new HeartBeatSender("localhost", HEARTBEAT_PORT, pc);
		Thread hbSenderThread = new Thread(heartBeatSender);
		hbSenderThread.start();

	}

}
