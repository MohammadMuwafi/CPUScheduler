import java.util.ArrayList;

public class SJF {
	private boolean visited[] = new boolean[1000_000];
	private ArrayList<Process> copy = new ArrayList<>();
	private double prevFinishinh; // to save the last finishing process time.

	SJF(ArrayList<Process> p) {
		copy = p;
		prevFinishinh = 0;
	}
	
	// run the processes in the CPU.
	public ArrayList<Process> run() {
		if (copy.size() > 0) {

			/*
			 * in case there are n processes arriving in the start time and the first one is
			 * not the shortest burst time.
			 */
			double timeOfFirstProcess = copy.get(0).getStartTime(), mninimum = copy.get(0).getBurstTime();
			int indexOfFirstProcess = 0;
			for (int i = 0; i < copy.size(); i++) {
				if (copy.get(i).getStartTime() != timeOfFirstProcess) {
					break;
				}
				if (copy.get(i).getBurstTime() < mninimum) {
					mninimum = copy.get(i).getBurstTime();
					indexOfFirstProcess = i;
				}
			}
			fillData(indexOfFirstProcess);

			for (int i = 0; i < copy.size(); i++) {
				double mn = 999_999_999;
				int index = -1;

				for (int j = 0; j < copy.size() && copy.get(j).getStartTime() <= prevFinishinh; j++) {
					if (visited[j] == false && copy.get(j).getBurstTime() < mn) {
						mn = copy.get(j).getBurstTime();
						index = j;
					}
				}

				// in case that there is no overlapping between the jobs
				if (index == -1) {
					int idx = i;
					while (idx < copy.size() && visited[idx] == true) {
						idx++;
					}
					if (idx < copy.size()) {
						index = idx;
					} else { // finish all processes.
						return copy;
					}
				}

				fillData(index);
			}
		}
		return copy;
	}

	// method to fill data of the finishing processes.
	private void fillData(int index) {
		visited[index] = true;

		double start = copy.get(index).getStartTime();
		double burst = copy.get(index).getBurstTime();

		copy.get(index).setFinishTime(Math.max(prevFinishinh, start) + burst);
		copy.get(index).setTurnAroundTime(copy.get(index).getFinishTime() - start);
		copy.get(index).setWaitTurnAroundTime(modifyAndSet(copy.get(index).getTurnAroundTime() / burst));
		copy.get(index).setWaitingTime(modifyAndSet(copy.get(index).getTurnAroundTime() - burst));

		prevFinishinh = copy.get(index).getFinishTime();
	}

	// method to format the double number
	private double modifyAndSet(double number) {
		String str = String.format("%.3f", number);
		return Double.parseDouble(str);
	}
}