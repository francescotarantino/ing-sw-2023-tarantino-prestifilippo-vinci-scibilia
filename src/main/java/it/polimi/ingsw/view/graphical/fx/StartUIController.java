package it.polimi.ingsw.view.graphical.fx;

import it.polimi.ingsw.viewmodel.GameDetailsView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logoImage.fitHeightProperty().bind(mainGridPane.heightProperty().multiply(0.1));

        joinText.wrappingWidthProperty().bind(mainGridPane.widthProperty().multiply(0.5));
        createText.wrappingWidthProperty().bind(mainGridPane.widthProperty().multiply(0.5));
    }
}
