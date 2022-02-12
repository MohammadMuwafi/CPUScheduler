import java.util.ArrayList;

// Explicit Priority Without Preemption With Aging.
public class ExplicitPriorityWithoutPreemption {
	private ArrayList<Integer> originalPriority = new ArrayList<>();
	private ArrayList<Process> copy = new ArrayList<>();
	private boolean visited[] = new boolean[1000_000];
	private double prevFinishinh, prevFinishinhOfLastFinishinh;

	ExplicitPriorityWithoutPreemption(ArrayList<Process> ar) {
		for (Process p : ar) {
			originalPriority.add(p.getPriority());
		}
		copy = ar;
		prevFinishinh = prevFinishinhOfLastFinishinh = 0;
	}

	// run processes in the CPU.
	public ArrayList<Process> run() {
		if (copy.size() > 0) {

			// in case there are n processes arriving in the start time and
			// then the first one is not the highest one in terms of priority.
			double timeOfFirstProcess = copy.get(0).getStartTime(), maximum = copy.get(0).getPriority();
			int indexOfFirstProcess = 0;
			for (int i = 0; i < copy.size(); i++) {
				if (copy.get(i).getStartTime() != timeOfFirstProcess) {
					break;
				}
				if (copy.get(i).getPriority() > maximum) {
					maximum = copy.get(i).getBurstTime();
					indexOfFirstProcess = i;
				}
			}
			fillData(indexOfFirstProcess);

			for (int i = 0; i < copy.size(); i++) {
				int index = -1;
				double mx = -999_999_999;
				for (int j = 0; j < copy.size() && copy.get(j).getStartTime() <= prevFinishinh; j++) {
					if (i == 0) {
						prevFinishinhOfLastFinishinh = copy.get(j).getStartTime();
					}
					if (!visited[j]) {
						int priorityAfterAging = 0;
						if (prevFinishinh >= prevFinishinhOfLastFinishinh) {
							priorityAfterAging = (int) (copy.get(j).getPriority() + prevFinishinh - Math.max(prevFinishinhOfLastFinishinh, copy.get(j).getStartTime()));
						} else {
							priorityAfterAging = (int) (copy.get(j).getPriority()); // there is no overlapping.
						}
						copy.get(j).setPriority(priorityAfterAging);
						if (priorityAfterAging > mx) {
							mx = priorityAfterAging;
							index = j;
						}
					}

				}

				// in case that there is no overlapping so index = -1.
				if (index == -1) {
					int idx = i;
					while (idx < copy.size() && visited[idx]) {
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

	public ArrayList<Integer> getOriginalPriority() {
		return originalPriority;
	}

	private void fillData(int index) {
		visited[index] = true;
		double start = copy.get(index).getStartTime();
		double burst = copy.get(index).getBurstTime();
		
		copy.get(index).setFinishTime(Math.max(prevFinishinh, start) + burst);
		copy.get(index).setTurnAroundTime(copy.get(index).getFinishTime() - start);
		copy.get(index).setWaitTurnAroundTime(modifyAndSet(copy.get(index).getTurnAroundTime() / burst));
		copy.get(index).setWaitingTime(modifyAndSet(copy.get(index).getTurnAroundTime() - burst));
		
		prevFinishinhOfLastFinishinh = prevFinishinh;
		prevFinishinh = copy.get(index).getFinishTime();
	}

	// method to format the double number
	private double modifyAndSet(double number) {
		String str = String.format("%.3f", number);
		return Double.parseDouble(str);
	}
}