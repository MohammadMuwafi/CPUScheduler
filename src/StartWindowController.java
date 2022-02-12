import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class StartWindowController implements Initializable {

	// to move the stage of program in the screen without any glitch.
	private double xOffset = 0, yOffset = 0;

	@FXML
	private AnchorPane rootPane;

	@FXML
	private Button exitProgram;

	
	@FXML 
	private void close(ActionEvent event) throws IOException {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();		
	}


	@FXML // method to move from the current screen to main screen.
	public void loadSecondScreen(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("SecondWindow.fxml"));
		Scene scene = new Scene(root, 1300, 680);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setX(300);
		stage.setY(200);

		root.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});
		scene.setFill(Color.TRANSPARENT);

		root.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				stage.setX(event.getScreenX() - xOffset);
				stage.setY(event.getScreenY() - yOffset);
			}
		});
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}
}