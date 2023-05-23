package it.polimi.ingsw.view.graphical.fx.dialogs;

import it.polimi.ingsw.viewmodel.GameView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * This alert is used to display the results of the game at the end.
 * It shows the final points of each player and the winner.
 */
public class ResultsAlert extends Alert {
    public ResultsAlert(GameView gameView) {
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
