package it.polimi.ingsw.view.graphical.fx;

import it.polimi.ingsw.Constants;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class GameUIController implements Initializable {
    @FXML
    public GridPane mainGrid;

    public GridPane livingRoomBoardGridPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/background.jpg"))),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        mainGrid.setBackground(new Background(backgroundImage));

        livingRoomBoardGridPane = new GridPane();

        BackgroundImage livingRoomBoardBackground = new BackgroundImage(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/livingRoomBoard.png"))),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false)
        );
        livingRoomBoardGridPane.setBackground(new Background(livingRoomBoardBackground));

        livingRoomBoardGridPane.setGridLinesVisible(false);
        livingRoomBoardGridPane.setAlignment(Pos.CENTER);
        mainGrid.add(livingRoomBoardGridPane, 0, 0);

        for(int i = 0; i < Constants.livingRoomBoardX + 2; i++) {
            ColumnConstraints c = new ColumnConstraints();
            c.prefWidthProperty().bind(Bindings.min(mainGrid.widthProperty().multiply(0.5/(Constants.livingRoomBoardX + 2)), mainGrid.heightProperty().divide(Constants.livingRoomBoardY + 2)));

            c.setHalignment(HPos.CENTER);

            livingRoomBoardGridPane.getColumnConstraints().add(c);
        }

        for(int i = 0; i < Constants.livingRoomBoardY + 2; i++) {
            RowConstraints r = new RowConstraints();
            r.prefHeightProperty().bind(Bindings.min(mainGrid.widthProperty().multiply(0.5/(Constants.livingRoomBoardX + 2)), mainGrid.heightProperty().divide(Constants.livingRoomBoardY + 2)));

            r.setValignment(VPos.CENTER);

            livingRoomBoardGridPane.getRowConstraints().add(r);
        }
    }
}
