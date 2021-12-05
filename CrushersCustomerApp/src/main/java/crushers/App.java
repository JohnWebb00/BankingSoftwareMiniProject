package crushers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import crushers.model.Bank;
import crushers.model.Customer;


public class App extends Application {

    public static Customer currentCustomer;
    public static String currentToken;
    
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("crushers/views/MainView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        /*
        stylesheet
        */

        scene.getStylesheets().add(getClass().getClassLoader().getResource("crushers/stylesheets/main.css").toExternalForm());

        configStage(stage, "Crushers Bank");
    }

    public static void configStage(Stage stage, String title){
        stage.getIcons().add(new Image("crushers/imgs/logo.jpg"));
        stage.setTitle(title);
        stage.show();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        launch(args);
    }

}