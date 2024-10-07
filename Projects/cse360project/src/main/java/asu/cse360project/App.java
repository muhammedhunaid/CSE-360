package asu.cse360project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    public static User user = new User("MOFO","Adain","admin,student");
    

    @Override
    public void start(Stage stage) throws IOException {
        user.setLoginRole("admin");
        Parent root = FXMLLoader.load(App.class.getResource("dashboard.fxml"));
        scene = new Scene(root, 500, 500);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();
        scene.setRoot(root);
    }

    static void setRoot(String fxml, User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();
        SceneController controller = loader.getController();
        controller.setUser(user);
        scene.setRoot(root);
    }    

    public static void main(String[] args) {
        launch();
    }

}