import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SecondWindowController implements Initializable {
	// list of process's number for all algorithm expect MpIo.
	private ObservableList<Integer> algorithimsExpectMpIo = FXCollections.observableArrayList(3, 5, 10, 20, 30, 50, 100);

	// list of process's number for only MpIo algorithm.
	private ObservableList<Integer> MpIo = FXCollections.observableArrayList(3, 5, 10);

	// list of percentages of wait time of IO.
	private ObservableList<Integer> percentages = FXCollections.observableArrayList(20, 50, 80, 90);

	// input file to read from.
	private File inputFile;

	// output file to write in.
	private PrintWriter outputFile;

	@FXML // a table and its columns.
	private TableView<Process> table;
	@FXML
	private TableColumn<Process, Double> st, bt, ft, ta, wta, wait;
	@FXML
	private TableColumn<Process, Integer> id, pri;

	@FXML // all radio buttons to choose from their toggleGroups
	private RadioButton fromRandomGenerator, fromFile, clearTAble, SaveInFile;

	@FXML // the invisible label
	private Label avg_ta, avg_wait, ganttChart;

	@FXML // all buttons that we used in the UI
	private Button startButton, fileButton, doIt, exitProgram;

	@FXML // list that contains the number of processes.
	private ComboBox<Integer> numOfProcess, ratioOfWait;

	@FXML // list that contains the algorithm.
	private ComboBox<String> combobox;

	@FXML // all toggle groups of radioButtons.
	private ToggleGroup tg, tg2;

	@FXML // ListView for the ganttChart
	ListView<Label> list;

	@FXML // method to choose the algorithm.
	public void chooseAlgorithim(ActionEvent event) {
		/*
		 * this conditions because MpIo algorithm cannot manipulate more than 10
		 * processes.
		 */
		if (combobox.getValue().equals("Multiprogrammed with IO percentage")) {
			turnOff();
			ratioOfWait.setItems(percentages);
			ratioOfWait.setDisable(true);
			numOfProcess.setItems(MpIo);
		} else {
			turnOff();
			numOfProcess.setItems(algorithimsExpectMpIo);
			ratioOfWait.setDisable(true);
			ratioOfWait.setValue(null);
		}
		// turn on all radioButtons of toggleGroup1
		fromRandomGenerator.setDisable(false);
		fromRandomGenerator.setSelected(false);

		fromFile.setSelected(false);
		fromFile.setDisable(false);
	}

	@FXML // method to choose the number of processes (1-10 in MpIo algorithm or 1-100 in
				// the others).
	public void chooseNumberOfProcesses(ActionEvent event) {
		if (combobox.getValue() != null) {
			if (combobox.getValue().equals("Multiprogrammed with IO percentage")) {
				ratioOfWait.setDisable(false);
			} else {
				startButton.setDisable(false);
			}
		}
	}

	@FXML // choose either from an input file or from random generator function of
				// processes.
	public void chooseInputPath(ActionEvent event) {
		RadioButton temp = (RadioButton) tg.getSelectedToggle();
		if (temp.getId().toString().equals("fromRandomGenerator")) {
			numOfProcess.setDisable(false);
			fileButton.setDisable(true);
		} else if (temp.getId().toString().equals("fromFile")) {

			numOfProcess.setValue(null);
			numOfProcess.setDisable(true);

			ratioOfWait.setValue(null);
			ratioOfWait.setDisable(true);

			fileButton.setDisable(false);
			startButton.setDisable(true);
		}
	}

	@FXML // choose the path of input file.
	public void chooseInputFile(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Choose File");
		Stage stage = new Stage();
		inputFile = fc.showOpenDialog(stage);
		if (inputFile != null) {
			if (combobox.getValue().equals("Multiprogrammed with IO percentage")) {
				ratioOfWait.setDisable(false);
			} else {
				startButton.setDisable(false);
			}
			fileButton.setDisable(true);
		}
	}

	@FXML
	private void exit(ActionEvent event) throws IOException {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML // method to call the chosen algorithm and run it.
	public void runTheAlgorithm(ActionEvent event) {
		id.setCellValueFactory(new PropertyValueFactory<Process, Integer>("idNumber"));
		pri.setCellValueFactory(new PropertyValueFactory<Process, Integer>("priority"));
		st.setCellValueFactory(new PropertyValueFactory<Process, Double>("startTime"));
		bt.setCellValueFactory(new PropertyValueFactory<Process, Double>("burstTime"));
		ft.setCellValueFactory(new PropertyValueFactory<Process, Double>("finishTime"));
		ta.setCellValueFactory(new PropertyValueFactory<Process, Double>("turnAroundTime"));
		wta.setCellValueFactory(new PropertyValueFactory<Process, Double>("WaitTurnAroundTime"));
		wait.setCellValueFactory(new PropertyValueFactory<Process, Double>("waitingTime"));

		RadioButton temp = (RadioButton) tg.getSelectedToggle();
		if (temp.getId().toString().equals("fromRandomGenerator")) {
			runAllThing(readFromGenerator());
		} else if (temp.getId().toString().equals("fromFile")) {
			runAllThing(readFromInputFile());
		}
	}

	@FXML
	public void showInformation() {
		Alert message = new Alert(Alert.AlertType.INFORMATION);
		message.setHeaderText("About Program.");
		message.setContentText("This program was created to simulate the CPU scheduling algorithms");
		message.showAndWait();
	}

	@FXML // method to choose between clearing data or saving it in a file.
	public void clearOrSaveData(ActionEvent event) {
		RadioButton temp2 = (RadioButton) tg2.getSelectedToggle();
		if (temp2.getId().toString().equals("clearTAble") || (temp2.getId().toString().equals("SaveInFile"))) {
			doIt.setDisable(false);
		}
	}

	@FXML // method to choose the ratio of io wait time.
	public void chooseRatioOfWaitTime(ActionEvent event) {
		if (numOfProcess.getValue() != null && ratioOfWait.getValue() != null) {
			startButton.setDisable(false);
		} else if (combobox.getValue().equals("Multiprogrammed with IO percentage") && inputFile != null) {
			startButton.setDisable(false);
		}
	}

	@FXML // method to clear the table to test the program again.
	public void clearTable(ActionEvent event) throws FileNotFoundException {
		RadioButton temp2 = (RadioButton) tg2.getSelectedToggle();

		if (temp2.getId().toString().equals("SaveInFile")) {
			outputFile = new PrintWriter(new FileOutputStream("ProcessOutput.txt", true));
			PrintWriter inputFile = new PrintWriter(new FileOutputStream("ProcessesInput.txt", false));
			outputFile.println("given that " + table.getItems().size()
					+ " processes, with the following data <each row of data contains: start time, then burst time, then with/without priority>");
			outputFile.println();
			if (combobox.getValue().equals("EP with aging")) {
				for (int i = 0; i < table.getItems().size(); i++) {
					outputFile.print(table.getItems().get(i).getStartTime() + " " + table.getItems().get(i).getBurstTime() + " ");
					outputFile.println(table.getItems().get(i).getPriority() + " ");
					inputFile.print(table.getItems().get(i).getStartTime() + " " + table.getItems().get(i).getBurstTime() + " ");
					inputFile.println(table.getItems().get(i).getPriority() + " ");
				}
			} else if (combobox.getValue().equals("Multiprogrammed with IO percentage")) {
				for (int i = 0; i < table.getItems().size(); i++) {
					outputFile.print(table.getItems().get(i).getStartTime() + " ");
					outputFile
							.println(Math.ceil(table.getItems().get(i).getBurstTime() * 100.0 / (100 - ratioOfWait.getValue())));
					inputFile.print(table.getItems().get(i).getStartTime() + " ");
					inputFile.println(Math.ceil(table.getItems().get(i).getBurstTime() * 100.0 / (100 - ratioOfWait.getValue())));
				}
			} else {
				for (int i = 0; i < table.getItems().size(); i++) {
					outputFile.println(table.getItems().get(i).getStartTime() + " " + table.getItems().get(i).getBurstTime());
					inputFile.println(table.getItems().get(i).getStartTime() + " " + table.getItems().get(i).getBurstTime());
				}
			}

			outputFile.println();
			outputFile.print("The solution of the below table is solved by the " + combobox.getValue() + " algorithm.");
			if (combobox.getValue().equals("Multiprogrammed with IO percentage")) {
				outputFile.println(" And the percentage of IO waiting time is " + ratioOfWait.getValue() + "%");
			} else {
				outputFile.println();
			}

			TableGenerator tableGenerator = new TableGenerator();
			List<String> headersList = new ArrayList<>();
			headersList.add("Pid");
			headersList.add("Start Time");
			headersList.add("Burst Time");
			headersList.add("Priority");
			headersList.add("Finish Time");
			headersList.add("TurnAround Time");
			headersList.add("WTA Time");
			headersList.add("Wait Time");

			List<List<String>> rowsList = new ArrayList<>();
			for (int i = 0; i < table.getItems().size(); i++) {
				List<String> row = new ArrayList<>();
				row.add(table.getItems().get(i).getIdNumber() + "");
				row.add(table.getItems().get(i).getStartTime() + "");
				row.add(table.getItems().get(i).getBurstTime() + "");
				row.add(table.getItems().get(i).getPriority() + "");
				row.add(table.getItems().get(i).getFinishTime() + "");
				row.add(table.getItems().get(i).getTurnAroundTime() + "");
				row.add(table.getItems().get(i).getWaitTurnAroundTime() + "");
				row.add(table.getItems().get(i).getWaitingTime() + "");
				rowsList.add(row);
			}
			outputFile.println(tableGenerator.generateTable(headersList, rowsList));
			outputFile.close();
			inputFile.close();
		}

		// clear table and the listView.
		for (int i = 0; i < table.getItems().size(); i++) {
			table.getItems().clear();
		}
		for (int i = 0; i < list.getItems().size(); i++) {
			list.getItems().clear();
		}

		combobox.setValue("Choose Algorithim");
		combobox.setDisable(false);
		turnOff();
	}

	// method to generate a specific number of processes randomly.
	public ArrayList<Process> readFromGenerator() {
		int n = numOfProcess.getValue();
		int bonus = 0;
		if (n == 100) {
			bonus = 20;
		} else if (n == 50 || n == 30) {
			bonus = 10;
		} else if (n < 30) {
			bonus = 5;
		}
		Set<Double> startTimeSet = new HashSet<>();

		while (startTimeSet.size() < n) {
			double startTime = (int) (Math.random() * (n + bonus));
			startTimeSet.add(startTime);
		}

		ArrayList<Process> ar = new ArrayList<>();
		int i = 0;
		for (Double startTime : startTimeSet) {
//		for (int i = 0; i < n; i++) {
//			double startTime = 5 + (double) ((int) ((Math.random() * 5)));
			double burstTime = 5 + (double) ((int) ((Math.random() * 5)));
			int priority = 5 + ((int) ((Math.random() * 5)));
			if (combobox.getValue().equals("EP with aging")) {
				ar.add(new Process(i + 1, modifyAndSet(startTime), modifyAndSet(burstTime), priority));
			} else {
				ar.add(new Process(i + 1, modifyAndSet(startTime), modifyAndSet(burstTime), 1));
			}
			i += 1;
		}
		// sort the processes according to compareTo function in Process Class.
		Collections.sort(ar);
		return ar;
	}

	public ArrayList<Process> readFromInputFile() {
		ArrayList<Process> ar = new ArrayList<>();
		int i = 0;
		boolean flag = true;
		try {
			Scanner in = new Scanner(inputFile);
			if (!combobox.getValue().equals("EP with aging")) {
				while (in.hasNext()) {
					double startTime = Double.parseDouble(in.next());
					double burstTime = Double.parseDouble(in.next());
					if (burstTime != 0) {
						ar.add(new Process(++i, startTime, burstTime));
					}
				}
			} else {
				while (in.hasNext()) {
					double startTime = Double.parseDouble(in.next());
					double burstTime = Double.parseDouble(in.next());
					int priority = Integer.parseInt(in.next());
					if (burstTime != 0) {
						ar.add(new Process(++i, startTime, burstTime, priority));
					}
				}
			}
			in.close();
		} catch (Exception e) {
			flag = false;
		}

		if (flag) {
			return ar;
		} else {
			return null;
		}
	}

	public void runAllThing(ArrayList<Process> ar) {
		if (ar == null || ar.isEmpty()) {
			Alert message = new Alert(Alert.AlertType.ERROR);
			message.setHeaderText("Error.");
			message.setContentText("File either is Empty or has a wrong format.");
			message.showAndWait();
			combobox.setValue("Choose Algorithim");
			combobox.setDisable(false);
			turnOff();
		} else if (ar.size() > 10000 && !combobox.getValue().equals("Multiprogrammed with IO percentage")
				|| (ar.size() > 10 && combobox.getValue().equals("Multiprogrammed with IO percentage"))) {
			Alert message = new Alert(Alert.AlertType.ERROR);
			message.setHeaderText("Error.");
			message.setContentText("File containts more the limit of algorithim");
			message.showAndWait();
			combobox.setValue("Choose Algorithim");
			combobox.setDisable(false);
			turnOff();
		} else {
			MultiprogrammedWithUniformIOPercentage tempMPIO = null;
			// call the specific algorithm.
			Collections.sort(ar);
			if (combobox.getValue().equals("FJFS")) {
				ar = new FJFS(ar).run();
			} else if (combobox.getValue().equals("SJF")) {
				ar = new SJF(ar).run();
			} else if (combobox.getValue().equals("EP with aging")) {
				ExplicitPriorityWithoutPreemption temp = new ExplicitPriorityWithoutPreemption(ar);
				ar = temp.run();
			} else if (combobox.getValue().equals("Multiprogrammed with IO percentage")) {
				int percentageOfWaitIO = 50;
				if (ratioOfWait.getValue() != null) {
					percentageOfWaitIO = ratioOfWait.getValue();
					tempMPIO = new MultiprogrammedWithUniformIOPercentage(ar, percentageOfWaitIO);
					ar = tempMPIO.run();
				}
			}

			// calculate the averages of values.
			double averageOfTurnAroundTime = 0, averageOfWaitTime = 0;
			for (int i = 0; i < ar.size(); i++) {
				table.getItems().add(ar.get(i));
				averageOfTurnAroundTime += ar.get(i).getTurnAroundTime();
				averageOfWaitTime += ar.get(i).getWaitingTime();
			}

			averageOfWaitTime /= ar.size();
			averageOfTurnAroundTime /= ar.size();
			avg_wait.setText("Average Wait Time: " + modifyAndSet(averageOfWaitTime) + " ms");
			avg_ta.setText("Average TA Time: " + modifyAndSet(averageOfTurnAroundTime) + " ms");

			// here we make the GanttChart in a listView.
			ganttChart.setText("GanttChart:");
			ArrayList<Process> tempList = new ArrayList<>();
			for (int i = 0; i < table.getItems().size(); i++) {
				tempList.add(table.getItems().get(i));
			}
			String algorithm = combobox.getValue();
			if (algorithm.equals("FJFS") || algorithm.equals("SJF") || algorithm.equals("EP with aging")) {
				// sort according to the finish time.
				Collections.sort(tempList, (Process p1, Process p2) -> {
					if (p1.getFinishTime() < p2.getFinishTime()) {
						return -1;
					} else {
						return 1;
					}
				});

				int counterOfTime = (int) tempList.get(0).getStartTime();
				for (int i = 0; i < tempList.size(); i++) {
					while (counterOfTime < (int) tempList.get(i).getFinishTime()) {
						if ((int) tempList.get(i).getStartTime() <= counterOfTime) {
							Label l = new Label(new String("P" + tempList.get(i).getIdNumber() + "\n" + counterOfTime));
							l.setBackground(new Background(new BackgroundFill(Color.MEDIUMSLATEBLUE, null, null)));
							l.setTextFill(Color.WHITE);
							l.setPadding(new Insets(10, 10, 10, 10));
							l.setMaxSize(400, 400);
							list.getItems().add(l);
						} else {
							Label l = new Label(new String("" + counterOfTime));
							l.setBackground(new Background(new BackgroundFill(Color.DARKKHAKI, null, null)));
							l.setTextFill(Color.WHITE);
							l.setPadding(new Insets(10, 10, 10, 10));
							l.setMaxSize(400, 400);
							list.getItems().add(l);
						}
						counterOfTime += 1;
					}
					list.refresh();
				}
			} else if (algorithm.equals("Multiprogrammed with IO percentage")) {
				ArrayList<String> temp = tempMPIO.getListView();
				for (int i = 0; i < temp.size(); i++) {
					Label l = new Label(temp.get(i));
					l.setTextAlignment(TextAlignment.CENTER);
					l.setTextFill(Color.WHITE);
					if (l.getText().contains("finished")) {
						l.setBackground(new Background(new BackgroundFill(Color.DARKKHAKI, null, null)));
					} else {
						l.setBackground(new Background(new BackgroundFill(Color.MEDIUMSLATEBLUE, null, null)));
					}
					l.setPadding(new Insets(10, 10, 10, 10));
					l.setMaxSize(500, 500);
					list.getItems().add(l);
				}
			}

			// turn off the previous component in UI.
			fromRandomGenerator.setDisable(true);
			numOfProcess.setDisable(true);
			ratioOfWait.setDisable(true);
			startButton.setDisable(true);
			fileButton.setDisable(true);
			fromFile.setDisable(true);
			combobox.setDisable(true);

			clearTAble.setDisable(false);
			SaveInFile.setDisable(false);
		}
	}

	// method to format the double number
	private double modifyAndSet(double number) {
		String str = String.format("%.3f", number);
		return Double.parseDouble(str);
	}

	public void turnOff() {
		// clear all toggleGroups.
		fromFile.setSelected(false);
		fromRandomGenerator.setSelected(false);
		clearTAble.setSelected(false);
		SaveInFile.setSelected(false);

		fromRandomGenerator.setDisable(true);
		fromFile.setDisable(true);
		clearTAble.setDisable(true);
		SaveInFile.setDisable(true);

		// turn off all toggleGroups.
		tg2.selectToggle(null);
		tg.selectToggle(null);

		// turn off all buttons.
		startButton.setDisable(true);
		fileButton.setDisable(true);
		doIt.setDisable(true);

		// turn off all comboBoxes.
		numOfProcess.setDisable(true);
		ratioOfWait.setDisable(true);

		// clear all comboBoxes.
		numOfProcess.setValue(null);
		ratioOfWait.setValue(null);

		// make the below labels inviable.
		ganttChart.setText("");
		avg_wait.setText("");
		avg_ta.setText("");
	}

	@Override // method to turn off the ability of controlling in the components of interface.
	public void initialize(URL arg0, ResourceBundle arg1) {
		combobox.setItems(
				FXCollections.observableArrayList("SJF", "FJFS", "EP with aging", "Multiprogrammed with IO percentage"));
		combobox.setDisable(false);
		turnOff();
	}
}