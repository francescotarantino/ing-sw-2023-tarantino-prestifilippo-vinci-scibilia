package it.polimi.ingsw.view.graphical.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Objects;

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

        stage.setTitle("MyShelfie");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();

        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });

        // Set icons
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icons/icon.png"))));
        if(System.getProperty("os.name").contains("Mac OS X")){
            // If MacOS set dock icon
            Taskbar.getTaskbar().setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/icons/icon.png")));
        }

        synchronized (lock) {
            instance = this;
            lock.notifyAll();
        }
    }

    /**
     * Returns the instance of the application.
     * If the instance is not yet created, it launches the application and waits for it to be created.
     * @return the instance of the application
     */
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
