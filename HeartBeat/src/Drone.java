import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

public class Drone extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int ROBOT_W = 30;
	private int ROBOT_H = 40;

	private DroneMoveModel rmm;
	private ZoneModel zm;

	private ArrayList<Color> legColors = new ArrayList<Color>();
	int colorChangeInterval = 0;
	int blinkChangeInterval = 0;
	boolean warningBlinking = false;
	Color warningColor = Color.RED;

	public Drone(DroneMoveModel rmm) {

		this.rmm = rmm;
		this.zm = rmm.zm;

		legColors.add(Color.BLUE);
		legColors.add(Color.GREEN);
		legColors.add(Color.YELLOW);
		legColors.add(Color.WHITE);

		Thread animationThread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					repaint();
					try {
						Thread.sleep(20);
					} catch (Exception ex) {
					}
				}
			}
		});

		animationThread.start();

	}

	public void paintComponent(Graphics g) {

		g.setColor(Color.PINK);
		g.fillRect(zm.getZoneMinX(), zm.getZoneMinY(), zm.zoneWidth, zm.zoneHeight);

		Graphics2D gg = (Graphics2D) g;

		if (rmm.isRobotMoving() == true && rmm.isInterruptMove() == false) {
			if (rmm.getX() < zm.getMinX() || (rmm.getX() + ROBOT_W) > zm.getMaxX()) {
				rmm.setVelX();
			}

			if (rmm.getY() < zm.getMinY() || (rmm.getY() + ROBOT_H) > zm.getMaxY()) {
				rmm.setVelY();
			}

			rmm.setX(rmm.getX() + rmm.getVelX());
			rmm.setY(rmm.getY() + rmm.getVelY());

			rmm.setDistanceTraveled(rmm.getDistanceTraveled() + Math.abs(rmm.getX()));
		}

		Ellipse2D circle = new Ellipse2D.Double(rmm.getX(), rmm.getY(), ROBOT_W, ROBOT_H);
		Ellipse2D leftEar = new Ellipse2D.Double(rmm.getX() + 10, rmm.getY() + 35, 10, 10);
		Ellipse2D rightEar = new Ellipse2D.Double(rmm.getX() + 10, rmm.getY() - 5, 10, 10);

		Ellipse2D leftNose = new Ellipse2D.Double(rmm.getX() - 8, rmm.getY() + 15, 10, 10);
		Ellipse2D rightNose = new Ellipse2D.Double(rmm.getX() + ROBOT_W - 2, rmm.getY() + 15, 10, 10);

		gg.setColor(Color.BLACK);
		gg.fill(circle);
		gg.setColor(legColors.get(0));
		gg.fill(leftEar);
		gg.setColor(legColors.get(1));
		gg.fill(rightEar);
		gg.setColor(legColors.get(2));
		gg.fill(leftNose);
		gg.setColor(legColors.get(3));
		gg.fill(rightNose);

		blinkChangeInterval += 10;
		if (rmm.isInterruptMove()) {
			turnOnAlarm(gg, blinkChangeInterval);
		}

		// Check if zone is out of area
		blinkChangeInterval += 20;
		if (!zm.isDroneInZone) {
			turnOnAlarm(gg, blinkChangeInterval);
		}

		rmm.setLastLocation(rmm.getX(), rmm.getY());

		colorChangeInterval += 10;

		if (colorChangeInterval % 200 == 0) {

			Color firstColor = legColors.get(0);
			legColors.remove(0);
			legColors.add(firstColor);

			colorChangeInterval = 0;
		}
	}

	public void turnOnAlarm(Graphics2D gg, int interval) {

		if (blinkChangeInterval % 750 == 0) {
			warningBlinking = !warningBlinking;
			warningColor = (warningBlinking) ? Color.RED : Color.BLACK;
			blinkChangeInterval = 0;
		}
		gg.setColor(warningColor);
		Ellipse2D warning = new Ellipse2D.Double(rmm.getX() + 8, rmm.getY() + 12, 15, 15);
		gg.fill(warning);
	}

	class RobotKeyHandler implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_T) {
				rmm.setRobotMoving(true);
				rmm.setInterruptMove(false);
			}

			if (e.getKeyCode() == KeyEvent.VK_I)
				rmm.setInterruptMove(true);

			if (e.getKeyCode() == KeyEvent.VK_R)
				rmm.setInterruptSenderThread(true);

			if (e.getKeyCode() == KeyEvent.VK_S)
				zm.switchBounds();
		}

		@Override
		public void keyReleased(KeyEvent e) {
			
		}

	}

}
