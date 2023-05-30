package it.polimi.ingsw.view.graphical.fx;

import it.polimi.ingsw.AppClient;
import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.viewmodel.GameDetailsView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.IOException;

import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * JavaFX FXML controller for startUI.fxml.
 */
public class StartUIController implements Initializable {
    @FXML
    public GridPane mainGridPane;
    @FXML
    public ImageView logoImage;
    @FXML
    public ListView<GameDetailsView> gameListView;
    @FXML
    public Text joinText;
    @FXML
    public Text createText;
    @FXML
    public Button startButton;
    @FXML
    public Text versionText;
    @FXML
    public VBox createGamePanel;
    @FXML
    public VBox waitingForPlayersPanel;
    @FXML
    public ListView<String> waitingForPlayersList;
    @FXML
    public ImageView githubButton;
    @FXML
    public HBox numberOfPlayersHbox;
    @FXML
    public HBox numberOfCommonGoalCardsHbox;
    @FXML
    public TextField usernameField;
    @FXML
    public Text connectionMethodText;

    /**
     * Toggle group for the number of players radio buttons.
     */
    public ToggleGroup numberOfPlayers = new ToggleGroup();
    /**
     * Toggle group for the number of common goal cards radio buttons.
     */
    public ToggleGroup numberOfCGCs = new ToggleGroup();

    /**
     * Initializes the main components of the Graphical StartUI.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the background image
        BackgroundImage backgroundImage = new BackgroundImage(
                ImageCache.getImage("/images/background.jpg"),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        mainGridPane.setBackground(new Background(backgroundImage));

        // Bind the logo image size to the window size
        logoImage.fitHeightProperty().bind(mainGridPane.heightProperty().multiply(0.15));

        // Show version and connection method
        versionText.setText("v" + Constants.version);
        connectionMethodText.setText(AppClient.getIP() + ":" + AppClient.getPort() + " (" + AppClient.getConnectionType() + ")");

        // Set click listener to GitHub button
        githubButton.setOnMouseClicked(e -> {
            if(Desktop.isDesktopSupported()){
                Desktop desktop = Desktop.getDesktop();

                if(desktop.isSupported(Desktop.Action.BROWSE)){
                    try {
                        desktop.browse(URI.create("https://github.com/francescotarantino/ing-sw-2023-tarantino-prestifilippo-vinci-scibilia"));
                    } catch (IOException ignored) {}
                }
            }
        });

        // Dynamically create the radio buttons for the number of players and the number of common goal cards
        for(int i = Constants.playersLowerBound; i <= Constants.playersUpperBound; i++){
            RadioButton radioButton = new RadioButton();
            radioButton.setText(Integer.toString(i));
            radioButton.setToggleGroup(numberOfPlayers);
            if(i == Constants.playersLowerBound){
                radioButton.setSelected(true); // Select the first radio button by default
            }
            numberOfPlayersHbox.getChildren().add(radioButton);
            HBox.setMargin(radioButton, new Insets(5, 5, 5, 5));
        }

        for(int i = Constants.minCommonGoalCards; i <= Constants.maxCommonGoalCards; i++){
            RadioButton radioButton = new RadioButton();
            radioButton.setText(Integer.toString(i));
            radioButton.setToggleGroup(numberOfCGCs);
            if(i == Constants.maxCommonGoalCards){
                radioButton.setSelected(true); // Select the last radio button by default
            }
            numberOfCommonGoalCardsHbox.getChildren().add(radioButton);
            HBox.setMargin(radioButton, new Insets(5, 5, 5, 5));
        }
    }
}