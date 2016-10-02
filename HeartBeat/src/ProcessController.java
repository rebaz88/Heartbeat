import java.util.ArrayList;
import java.util.List;

public class ProcessController extends Thread {

	List<ObstacleDetectImpl> processes;
	ObstacleDetectImpl runningProcess;
	DroneMoveModel dmm;

	ProcessController(ObstacleDetector firstProcess, SecondProcessManipulator secondProcess, DroneMoveModel dmm) {

		processes = new ArrayList<ObstacleDetectImpl>();
		processes.add(firstProcess);
		processes.add(secondProcess);

		this.dmm = dmm;

	}

	public void run() {

		while (processes.size() > 0) {

			for (int i = 0; i < processes.size(); i++) {
				if (processes.get(i).isRunning())
					runningProcess = processes.get(i);
			}

			if (runningProcess.isRunning()) {
				if (runningProcess.detectedObstacle()) {

					System.err.println("Approaching obstales");
					runningProcess.print();
					this.dmm.setRobotMoving(false);
				}
			}

			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public int getRunningProcesses() {

		int runningProcessCount = 0;

		for (int i = 0; i < processes.size(); i++) {
			if (processes.get(i).isRunning())
				runningProcessCount++;
		}
		return runningProcessCount;
	}

}
