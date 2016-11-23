
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Encryption extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			// load the FXML resource
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Encryption.fxml"));
			BorderPane root = (BorderPane) loader.load();
			// set a whitesmoke background
			root.setStyle("-fx-background-color: whitesmoke;");
			// create and style the scene
			Scene scene = new Scene(root);
			// create the stage with the given title and the previously created scene
			primaryStage.setTitle("Bild Ver- und Entschlüsselung");
			primaryStage.setScene(scene);
			//show the gui
			primaryStage.show();
			EncryptionController controller = loader.getController();
			controller.init();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
