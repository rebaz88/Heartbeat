/**
 * Drone starter
 * 
 * @author rebaz
 *
 */
public class DroneStarter {

	public static void main(String[] args) throws Exception {

		SpaceUI frame = new SpaceUI();
		ZoneModel zm = new ZoneModel(frame.getWidth(), frame.getHeight());

		final DroneMoveModel rmm = new DroneMoveModel(zm);
		Drone tr = new Drone(rmm);

		frame.addKeyListener(tr.new RobotKeyHandler());
		frame.add(tr);

		frame.setVisible(true);

		Sender sender = new Sender("localhost", 7475, rmm);
		sender.start();

	}

}
