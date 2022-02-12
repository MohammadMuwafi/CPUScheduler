import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SecondWindow extends Application {

	private double xOffset = 0;
	private double yOffset = 0;

	@Override
	public void start(Stage primaryStage) throws IOException {

		Parent root = FXMLLoader.load(getClass().getResource("SecondWindow.fxml"));

		Scene scene = new Scene(root, 1300, 680);
		scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
		scene.setFill(Color.TRANSPARENT);

		primaryStage.initStyle(StageStyle.TRANSPARENT);

		/*
		 *  method to save the location of clicked point of 
		 *  cursor to smooth the movement of stage of program.
		 */
		root.setOnMousePressed(event -> {
			xOffset = event.getSceneX();
			yOffset = event.getSceneY();
		});

		// method to move the stage of program in the screen.
		root.setOnMouseDragged(event -> {
			primaryStage.setX(event.getScreenX() - xOffset);
			primaryStage.setY(event.getScreenY() - yOffset);
		});

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) throws InterruptedException {
		launch(args);
	}
}