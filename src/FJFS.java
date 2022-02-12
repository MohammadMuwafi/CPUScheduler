import java.util.ArrayList;

public class FJFS {
	private ArrayList<Process> copy = new ArrayList<>();

	FJFS(ArrayList<Process> p) {
		copy = p;
	}
	
	// run the processes in the CPU.
	public ArrayList<Process> run() {
		for (int i = 0; i < copy.size(); i++) {
			double start = copy.get(i).getStartTime();
			double burst = copy.get(i).getBurstTime();
			if (i == 0) {
				copy.get(i).setFinishTime(start + burst);
			} else {
				copy.get(i).setFinishTime(Math.max(copy.get(i - 1).getFinishTime(), copy.get(i).getStartTime()) + burst);
			}
			copy.get(i).setTurnAroundTime(copy.get(i).getFinishTime() - start);
			copy.get(i).setWaitTurnAroundTime(modifyAndSet(copy.get(i).getTurnAroundTime() / burst));
			copy.get(i).setWaitingTime(modifyAndSet(copy.get(i).getTurnAroundTime() - burst));
		}
		return copy;
	}
	
	// method to format the double number
	private double modifyAndSet(double number) {
		String str = String.format("%.3f", number);
		return Double.parseDouble(str);
	}
}