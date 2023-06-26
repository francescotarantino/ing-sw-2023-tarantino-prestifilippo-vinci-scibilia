package it.polimi.ingsw.view.graphical.fx;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.stage.Stage;

import java.awt.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is required by JavaFX to launch the GUI of the game.
 * It also provides the {@link #execute(Runnable)} method to execute tasks such as calls to the server in separate threads.
 */
public class FXApplication extends Application {
    /**
     * This latch is used to wait until FX Application it is launched and ready.
     * @see #waitUntilLaunch()
     */
    private static final CountDownLatch latch = new CountDownLatch(1);

    /**
     * This executor service is used to execute tasks such as calls to the server.
     * @see #execute(Runnable)
     */
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void init() {
        latch.countDown();
    }

    @Override
    public void start(Stage stage) {
        // If OS supports taskbar icons (e.g. macOS), set the icon
        if(Taskbar.isTaskbarSupported() && Taskbar.getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE)){
            Taskbar.getTaskbar().setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/icons/icon.png")));
        }
    }

    /**
     * This method waits until FX Application it is launched and ready.
     */
    public static void waitUntilLaunch() throws InterruptedException {
        latch.await();
    }

    /**
     * This method executes a task in the executor service.
     * @param task the task to execute
     */
    public static void execute(Runnable task) {
        executor.execute(new Task<Void>() {
            @Override
            protected Void call() {
                task.run();
                return null;
            }
        });
    }
}
