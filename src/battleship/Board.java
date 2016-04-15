/*
 * 
 */

package battleship;

public class Board {
    Ship[][] ships;
    boolean[][] guesses;
    boolean isFinished;
    
    public Board(int s) {
        this.ships = new Ship[s][s];
        this.guesses = new boolean[s][s];
        this.isFinished = false;
    }
    
    public boolean canBePlaced(Ship s, int x, int y) {
        boolean vertical = s.ySize > s.xSize;
        if (vertical) {
            for (int i=0; i<s.ySize; i++) {
                try {
                    if (this.ships[x][y-i] != null) {
                        return false;
                    }
                } catch (Exception e) {
                    return false;
                }
            }
            return true;
        }
        for (int i=0; i<s.xSize; i++) {
            try {
                if (this.ships[x-i][y] != null) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}
