package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Tile;

import static it.polimi.ingsw.Utils.checkMatrixSize;

public class CGCLines extends CommonGoalCard {
    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    private final Direction direction;
    private final int max_different;
    private final int repetitions;

    /**
     * @param max_different 0 means that all the tiles must be different
     *                      X > 0 means that there can be at most X > 0 tile of each type
     */
    public CGCLines(int numberOfPlayers, int ID, Direction direction, int max_different, int repetitions) {
        super(numberOfPlayers, ID);

        if(direction == null || max_different < 0 || repetitions < 1){
            throw new IllegalArgumentException("Invalid parameters");
        } else if(direction == Direction.HORIZONTAL && (max_different > Constants.bookshelfX || repetitions > Constants.bookshelfY)){
            throw new IllegalArgumentException("Arguments are too big for the horizontal direction");
        } else if(direction == Direction.VERTICAL && (max_different > Constants.bookshelfY || repetitions > Constants.bookshelfX)){
            throw new IllegalArgumentException("Arguments are too big for the vertical direction");
        }

        this.direction = direction;
        this.max_different = max_different;
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

        return count_repetitions >= this.repetitions; // TODO CHECK IF >= OR ==
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

        if(this.max_different == 0){
            return count_different == line.length;
        } else {
            return count_different <= this.max_different;
        }
    }
}
