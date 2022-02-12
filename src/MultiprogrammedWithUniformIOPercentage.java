import java.util.ArrayList;
import java.util.Collections;

public class MultiprogrammedWithUniformIOPercentage {
	private ArrayList<String> list = new ArrayList<String>();
	private ArrayList<Double> b = new ArrayList<Double>();
	private ArrayList<Process> copyOne;
	private int whichTable;
	private double perc;
	private double table[][] = { 
		new double[] {0, 0.8, 0.96, 0.992, 0.9984, 0.99968, 0.999936, 0.9999872, 0.99999744, 0.999999488, 0.999999898},
		new double[] {0, 0.5, 0.75, 0.875, 0.9375, 0.96875, 0.984375, 0.9921875, 0.99609375, 0.998046875, 0.999023438},
		new double[] {0, 0.2, 0.36, 0.488, 0.5904, 0.67232, 0.737856, 0.7902848, 0.83222784, 0.865782272, 0.892625818},
		new double[] {0, 0.1, 0.19, 0.271, 0.3439, 0.40951, 0.468559, 0.5217031, 0.56953279, 0.612579511, 0.651321560}
	};

	MultiprogrammedWithUniformIOPercentage(ArrayList<Process> ar, int percentage) {
		copyOne = ar;
		perc = (double) percentage;
		for (int i = 0; i < ar.size(); i++) {
			b.add(modifyAndSet(ar.get(i).getBurstTime() * ((double) (100 - perc) / 100)));
		}		
		// mapping between percentages and table.
		if (percentage == 20) {
			whichTable = 0;
		} else if (percentage == 50) {
			whichTable = 1;
		} else if (percentage == 80) {
			whichTable = 2;
		} else if (percentage == 90) {
			whichTable = 3;
		} else { // default
			whichTable = 0;
		}
	}

	// run the processes in the CPU.
	public ArrayList<Process> run() {
		// for ganttChart in UI
		boolean done[] = new boolean[1000_000];
		
		// modifying and editing the cpuTime
		for (int i = 0; i < copyOne.size(); i++) {
			copyOne.get(i).setBurstTime(modifyAndSet(copyOne.get(i).getBurstTime() * ((double) (100 - perc) / 100)));
			copyOne.get(i).setBurstTime(modifyAndSet(copyOne.get(i).getBurstTime()));
			copyOne.get(i).setStartTime(modifyAndSet(copyOne.get(i).getStartTime()));
		}
		
		for (int i = 1; i < copyOne.size(); i++) {
			double prevTime = copyOne.get(i - 1).getStartTime();
			double curTime = copyOne.get(i).getStartTime();
			double interval = curTime - prevTime;
			int cntOfProcess = 0;
			// count number of processes in the interval.
			for (int j = 0; j < copyOne.size() && copyOne.get(j).getStartTime() < curTime; j++) {
				if (!done[j]) {
					cntOfProcess += 1;
				}
			}
			if (cntOfProcess > 10) {
				showError();
			}
			if (cntOfProcess == 0) {
				continue;
			}
			double timeForEachProcess = modifyAndSet(table[whichTable][cntOfProcess] * interval / cntOfProcess);
			for (int j = 0; j < copyOne.size() && copyOne.get(j).getStartTime() < curTime; j++) {
				if (!done[j]) {
					double start = copyOne.get(j).getStartTime();
					double burst = copyOne.get(j).getBurstTime();
					copyOne.get(j).setBurstTime(modifyAndSet(burst - timeForEachProcess));
					if (copyOne.get(j).getBurstTime() <= 0.0) {
						done[j] = true;
						copyOne.get(j).setFinishTime(curTime);
						copyOne.get(j).setTurnAroundTime(modifyAndSet(copyOne.get(j).getFinishTime() - start));
						copyOne.get(j).setWaitTurnAroundTime(modifyAndSet(copyOne.get(j).getTurnAroundTime() / (b.get(j) * (100.0 / (100 - perc)))));
						copyOne.get(j).setWaitingTime(modifyAndSet(copyOne.get(j).getTurnAroundTime() - (b.get(j) * (100.0 / (100 - perc)))));
					}
					if (done[j]) {
						makeCell(copyOne.get(j).getIdNumber(), modifyAndSet(curTime), true, modifyAndSet(copyOne.get(j).getBurstTime()));
					} else {
						makeCell(copyOne.get(j).getIdNumber(), modifyAndSet(curTime), false, modifyAndSet(copyOne.get(j).getBurstTime()));
					}
				}
			}
			prevTime = curTime;
		}

		// add the remainder processes to rem arrayList to finish them.
		ArrayList<Process> rem = new ArrayList<>();
		for (int i = 0; i < copyOne.size(); i++) {
			if (!done[i]) {
				rem.add(copyOne.get(i));
			}
		}
		// ascending sorting according to burst time.
		Collections.sort(rem, (Process p1, Process p2) -> {
			if (p1.getBurstTime() == p2.getBurstTime()) {
				return 0;
			} else if (p1.getBurstTime() < p2.getBurstTime()) {
				return -1;
			} else {
				return 1;
			}
		});

		// check if there is an error
		if (rem.size() > 10) {
			showError();
		}

		double curTime = copyOne.get(copyOne.size() - 1).getStartTime();
		for (int i = 0; i < rem.size(); i++) {
			double smallestBurst = rem.get(i).getBurstTime();
			// 1 hour ------> (cpuTime when n = numberOfProcess)
			// x hour ------> the smallest burst time.
			int numberOfProcess = 0;
			for (int q = 0; q < copyOne.size(); q++) {
				if (!done[q]) {
					numberOfProcess += 1;
				}
			}
			if (numberOfProcess > 10) {
				showError();
			}
			if (table[whichTable][numberOfProcess] == 0) {
				continue;
			}
			double neededHours = modifyAndSet(smallestBurst / (table[whichTable][numberOfProcess] / numberOfProcess));
			for (int j = 0; j < copyOne.size(); j++) {
				if (!done[j]) {
					double start = copyOne.get(j).getStartTime();
					double burst = copyOne.get(j).getBurstTime();
					copyOne.get(j).setBurstTime(modifyAndSet(burst - smallestBurst));
					if (copyOne.get(j).getBurstTime() <= 0) {
						done[j] = true;
						copyOne.get(j).setFinishTime(modifyAndSet(curTime + neededHours));
						copyOne.get(j).setTurnAroundTime(modifyAndSet(copyOne.get(j).getFinishTime() - start));
						copyOne.get(j).setWaitTurnAroundTime(modifyAndSet(copyOne.get(j).getTurnAroundTime() / (b.get(j) * (100.0 / (100 - perc)))));
						copyOne.get(j).setWaitingTime(modifyAndSet(copyOne.get(j).getTurnAroundTime() - (b.get(j) * (100.0 / (100 - perc)))));
					}
					if (done[j]) {
						makeCell(copyOne.get(j).getIdNumber(), modifyAndSet(curTime + neededHours), true, modifyAndSet(copyOne.get(j).getBurstTime()));
					} else {
						makeCell(copyOne.get(j).getIdNumber(), modifyAndSet(curTime + neededHours), false, modifyAndSet(copyOne.get(j).getBurstTime()));
					}
				}
			}
			curTime += (neededHours);
		}
		for (int i = 0; i < copyOne.size(); i++) {
			copyOne.get(i).setBurstTime(b.get(i));
		}
		return copyOne;
	}
	
	// method for GUI GanttChart.
	private void makeCell(int id, double counterOfTime, boolean finish, double burst) {
		if (finish) {
			list.add(new String("P" + id + " finished\nat " + counterOfTime));
		} else {
			list.add(new String("P" + id + "'s burst=" + burst + "\n time=" + counterOfTime));			
		}
	}
	
	public ArrayList<String> getListView() {
		return list;
	}

	// method to format the double number
	private double modifyAndSet(double number) {
		String str = String.format("%.3f", number);
		return Double.parseDouble(str);
	}
	
	
	// print the error when happening.
	private void showError() {
		System.out.println("Error: This algorithim cannot manipulate more than 10 processes.");
		System.exit(1);
	}
}
