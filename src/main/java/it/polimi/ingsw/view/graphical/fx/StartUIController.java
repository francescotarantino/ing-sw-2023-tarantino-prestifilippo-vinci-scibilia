package it.polimi.ingsw.view.graphical.fx;

import it.polimi.ingsw.AppClient;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.viewmodel.GameDetailsView;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.IOException;

import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

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

    public ToggleGroup numberOfPlayers = new ToggleGroup();
    public ToggleGroup numberOfCGCs = new ToggleGroup();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logoImage.fitHeightProperty().bind(mainGridPane.heightProperty().multiply(0.15));

        versionText.setText("v" + Constants.version);

        connectionMethodText.setText(AppClient.getIP() + ":" + AppClient.getPort() + " (" + AppClient.getConnectionType() + ")");
        githubButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(Desktop.isDesktopSupported()){
                    Desktop desktop = Desktop.getDesktop();

                    if(desktop.isSupported(Desktop.Action.BROWSE)){
                        try {
                            desktop.browse(URI.create("https://github.com/francescotarantino/ing-sw-2023-tarantino-prestifilippo-vinci-scibilia"));
                        } catch (IOException ignored) {}
                    }
                }
            }
        });

        for(int i = Constants.playersLowerBound; i <= Constants.playersUpperBound; i++){
            RadioButton radioButton = new RadioButton();
            radioButton.setText(Integer.toString(i));
            radioButton.setToggleGroup(numberOfPlayers);
            if(i == Constants.playersLowerBound){
                radioButton.setSelected(true);
            }
            numberOfPlayersHbox.getChildren().add(radioButton);
            HBox.setMargin(radioButton, new Insets(5, 5, 5, 5));
        }

        for(int i = Constants.minCommonGoalCards; i <= Constants.maxCommonGoalCards; i++){
            RadioButton radioButton = new RadioButton();
            radioButton.setText(Integer.toString(i));
            radioButton.setToggleGroup(numberOfCGCs);
            if(i == Constants.maxCommonGoalCards){
                radioButton.setSelected(true);
            }
            numberOfCommonGoalCardsHbox.getChildren().add(radioButton);
            HBox.setMargin(radioButton, new Insets(5, 5, 5, 5));
        }
    }
}