package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.model.Tile;

import static it.polimi.ingsw.utils.GameUtils.checkMatrixSize;

/**
 * This class represents the common goal card Lines.
 */
public class CGCLines extends CommonGoalCard {
    /**
     * Direction of the lines in the bookshelf.
     */
    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    private final Direction direction;
    private final int maxDifferent;
    private final int repetitions;

    /**
     * @param maxDifferent 0 means that all the tiles must be different
     *                      X > 0 means that there can be at most X > 0 tile of each type
     */
    public CGCLines(int numberOfPlayers, int ID, Direction direction, int maxDifferent, int repetitions) {
        super(numberOfPlayers, ID);

        if(direction == null || maxDifferent < 0 || repetitions < 1){
            throw new IllegalArgumentException("Invalid parameters");
        } else if(direction == Direction.HORIZONTAL && (maxDifferent > Constants.bookshelfX || repetitions > Constants.bookshelfY)){
            throw new IllegalArgumentException("Arguments are too big for the horizontal direction");
        } else if(direction == Direction.VERTICAL && (maxDifferent > Constants.bookshelfY || repetitions > Constants.bookshelfX)){
            throw new IllegalArgumentException("Arguments are too big for the vertical direction");
        }

        this.direction = direction;
        this.maxDifferent = maxDifferent;
        this.repetitions = repetitions;
    }

    @Override
    public boolean check(Tile[][] matrix) {
        if (matrix == null) {
            throw new NullPointerException();
        }
        checkMatrixSize(matrix);

        int count_repetitions = 0;

        if(direction == Direction.HORIZONTAL){
            for(int j = 0; j < Constants.bookshelfY; j++){
                Tile[] tmp = new Tile[Constants.bookshelfX];
                for(int i = 0; i < Constants.bookshelfX; i++){
                    tmp[i] = matrix[i][j];
                }

                if(checkLine(tmp)){
                    count_repetitions++;
                }
            }
        } else {
            for(int i = 0; i < Constants.bookshelfX; i++){
                if(checkLine(matrix[i])){
                    count_repetitions++;
                }
            }
        }

        return count_repetitions == this.repetitions;
    }

    private boolean checkLine(Tile[] line){
        int count_different = 0;
        for(int i = 0; i < line.length; i++){
            if (line[i] == null){
                return false;
            }

            boolean check = true;
            for(int k = 0; k < i; k++){
                if(line[k].sameType(line[i])){
                    check = false;
                    break;
                }
            }

            if(check){
                count_different++;
            }
        }

        if(this.maxDifferent == 0){
            return count_different == line.length;
        } else {
            return count_different <= this.maxDifferent;
        }
    }
}
