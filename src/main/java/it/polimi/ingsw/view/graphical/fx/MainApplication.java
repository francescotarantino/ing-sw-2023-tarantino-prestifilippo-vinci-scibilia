package it.polimi.ingsw.view.graphical.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;

public class MainApplication extends Application {
    public static MainApplication instance;

    private static final Object lock = new Object();

    public StartUIController controller;

    @Override
    public void start(Stage stage) throws Exception {
        // Load the fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/startUI.fxml"));

        Parent root = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(root);

        stage.setTitle("MyShelfie - Start");
        stage.setScene(scene);
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.show();

        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });

        // Set icon on the window and the taskbar
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icons/icon.png")));
        Taskbar.getTaskbar().setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/icons/icon.png")));

        instance = this;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public static MainApplication getInstance() {
        if(instance == null){
            new Thread(() -> launch(MainApplication.class)).start();
            synchronized (lock) {
                while(instance == null){
                    try {
                        lock.wait();
                    } catch (InterruptedException ignored) {}
                }
            }
        }

        return instance;
    }
}
