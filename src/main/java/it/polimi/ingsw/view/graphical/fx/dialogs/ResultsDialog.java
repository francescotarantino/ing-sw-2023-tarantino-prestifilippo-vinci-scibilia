package it.polimi.ingsw.view.graphical.fx.dialogs;

import it.polimi.ingsw.viewmodel.GameView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ResultsDialog extends Alert {
    public ResultsDialog(GameView gameView) {
        super(AlertType.NONE);

        setTitle("Game result");
        setHeaderText("Game has ended.");

        if(!gameView.isWalkover()){
            StringBuilder scores = new StringBuilder();
            scores.append("Final points:\n");
            gameView.getPlayerInfo().forEach(x -> scores.append(x).append("\n"));
            scores.append("The winner is: ").append(gameView.getPlayerInfo().get(0).username()).append("!");
            setContentText(scores.toString());
        } else {
            setContentText("You were the only player left in the game. You win!");
        }

        getDialogPane().getButtonTypes().add(ButtonType.OK);
    }
}
