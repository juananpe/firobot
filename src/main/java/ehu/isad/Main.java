package ehu.isad;

import ehu.isad.controllers.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception{

    FXMLLoader loader = new FXMLLoader(getClass().getResource(
            "/main.fxml"));
    Parent root = (Parent) loader.load();
    Controller controller = loader.getController();

//    Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
    primaryStage.setTitle("FI Robot");
    primaryStage.setScene(new Scene(root, 600, 550));
    primaryStage.show();

    primaryStage.setOnCloseRequest(event -> {
      System.out.println("Stage is closing");
      controller.stop(null);
    });

  }

  public static void main(String[] args) {
    launch(args);
  }
}
