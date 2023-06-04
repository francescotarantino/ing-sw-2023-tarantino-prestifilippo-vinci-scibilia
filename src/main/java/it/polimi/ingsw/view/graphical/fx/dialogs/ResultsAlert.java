package it.polimi.ingsw.view.graphical.fx.dialogs;

import it.polimi.ingsw.viewmodel.GameView;
import it.polimi.ingsw.viewmodel.PlayerInfo;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Comparator;
import java.util.List;

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

            List<PlayerInfo> orderedPlayers = gameView.getPlayerInfo()
                    .stream()
                    .sorted(Comparator.comparingInt(PlayerInfo::points).reversed())
                    .toList();

            scores.append("Final points:\n");
            orderedPlayers.forEach(x -> scores.append(x).append("\n"));
            scores.append("The winner is: ").append(orderedPlayers.get(0).username()).append("!");
            setContentText(scores.toString());
        } else {
            setContentText("You were the only player left in the game. You win!");
        }

        getDialogPane().getButtonTypes().add(ButtonType.OK);
    }
}
