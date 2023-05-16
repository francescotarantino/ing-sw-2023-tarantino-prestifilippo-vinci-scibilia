package it.polimi.ingsw.view.graphical.fx;

import javafx.application.Application;
import javafx.stage.Stage;

import java.awt.*;

public class MainApplication extends Application {
    private static MainApplication instance;
    private static final Object lock = new Object();

    @Override
    public void start(Stage stage) throws Exception {
        // If OS is MacOS then set dock icon
        if(System.getProperty("os.name").contains("Mac OS X")){
            Taskbar.getTaskbar().setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/icons/icon.png")));
        }

        synchronized (lock) {
            instance = this;
            lock.notifyAll();
        }
    }

    /**
     * This method starts the JavaFX application and waits until it is launched.
     */
    public static void startAndWaitUntilLaunch() {
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
    }
}
