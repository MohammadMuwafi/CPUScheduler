public class Process implements Comparable<Process> {
	private int idNumber;
	private int priority;
	private double startTime;
	private double burstTime;
	private double finishTime;
	private double waitingTime;
	private double turnAroundTime;
	private double WaitTurnAroundTime;

	// constructor with priority.
	public Process(int idNumber, double startTime, double burstTime, int priority) {
		this.idNumber = idNumber;
		this.startTime = startTime;
		this.burstTime = burstTime;
		this.priority = priority;
	}

	// constructor without priority.
	public Process(int idNumber, double startTime, double burstTime) {
		this(idNumber, startTime, burstTime, 1);
	}

	public int getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(int idNumber) {
		this.idNumber = idNumber;
	}

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public double getBurstTime() {
		return burstTime;
	}

	public void setBurstTime(double burstTime) {
		this.burstTime = burstTime;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public double getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(double finishTime) {
		this.finishTime = finishTime;
	}

	public double getTurnAroundTime() {
		return turnAroundTime;
	}

	public void setTurnAroundTime(double turnAroundTime) {
		this.turnAroundTime = turnAroundTime;
	}

	public double getWaitTurnAroundTime() {
		return WaitTurnAroundTime;
	}

	public void setWaitTurnAroundTime(double WaitTurnAroundTime) {
		this.WaitTurnAroundTime = WaitTurnAroundTime;
	}

	public double getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(double waitingTime) {
		this.waitingTime = waitingTime;
	}

	// ascending order according to start time of process 
	// then according to idNumber(with unique id)
	public int compareTo(Process p) {
		if (this.startTime != p.startTime) {
			if (this.startTime < p.startTime) {
				return -1;
			}
			return 1;
		} else if (this.idNumber < p.idNumber) {
			return -1;
		}
		return 1;
	}

	@Override
	public String toString() {
		return "Process [id=" + idNumber + ", ST=" + (startTime) + ", BT=" + (burstTime) + ", P=" + priority
				+ ", FT=" + (finishTime) + ", TA=" + (turnAroundTime) + ", WTA=" + (WaitTurnAroundTime) + ", W=" + (waitingTime) + "]";
	}
}