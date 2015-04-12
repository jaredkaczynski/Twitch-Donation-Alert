package Donation;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Main extends Application {


    public Main() {

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        //FXMLLoader root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        // root.setRoot(this);
        //root.setController(this);
        primaryStage.setTitle("Razrswords Twitch Donation Checker");
        primaryStage.setScene(new Scene(root, 600, 305));
        primaryStage.show();
        Controller myController = loader.getController();
        myController.setData();
        //Controller initialize = new Controller();
        //initialize.removeScrollBar();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                try {
                    stop();
                    //Runtime.getRuntime().halt(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
