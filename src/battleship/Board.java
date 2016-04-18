/*
 * 
 */
// Worked on by James

package battleship;

import java.io.*;
import java.awt.*; //needed for graphics
import javax.swing.*;

public class Board extends JFrame {
    private Ship[][] ships;
    private boolean shipsVisible;
    private boolean[][] guesses;
    private boolean isFinished;
    
    private int height = 400;   //I think these are going to need to be constructor fields 
    private int width = 400;    //in the future - we may need to display more than one board on the screen
    
    private int xStartPos = 20;//Sets where the board begins drawing
    private int yStartPos = 50;//Sets where the board begins drawing
//    private int numSquaresX = 10;  These two can be replaced with this.getBoardSize();
//    private int numSquaresY = 10;
    private int squareWidth = 40;
    private int squareHeight = 40;
    
    /* James, sorry to mess with your code, but here was what I was planning on
     * doing to keep track of the ships:
     * The array for ships (this.ships) has all values initialized to null
     * when the array is created. So basically when we place a ship, we set
     * loop through and set a bunch of references to be that ship (i.e. if 
     * we have a ship of length 4, we set those four spots = that ship.
     * Then, when we need to hit a ship, we can call this.ship[x][y].hit() and update
     * the spot to be already guessed in this.guesses.
     * 
     * To check if there is a ship, simply check if this.ships[x][y] != null, and
     * if so, then there is a ship. I have added a method below (see isShip()).
     */
    
    
    public Board(int s, boolean v) {
        this.ships = new Ship[s][s];
        this.guesses = new boolean[s][s];
        this.isFinished = false;
        this.shipsVisible = v;
    }
    
    public boolean canBePlaced(Ship s, int x, int y) {
        boolean vertical = s.vertical;
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
    //Places solid block (a ship), image of ship could possibly be put over top
    
    /*
     * Could you change this method to be more like the canBePlaced method? Here's why:
     * if you read the comment above, the ships array can be used to keep track of the 
     * placed ships' positions. Also, we can always reference ships' positions based
     * on their bottom left coordinate, orientation and size. Basically, we are going
     * to check whether or not a ship can be placed before it is actually placed, so 
     * we don't have to worry about any error checking, all this method needs to do
     * is to loop through based on the ship's size and orientation and size and set 
     * this.ships[i][j] = the ship.
     */
    public void placeShip(int startX, int startY, int numColumns, int numRows){
        int endCol = Math.min(startX+numColumns, numSquaresX);
        int endRow = Math.min(startY+numRows, numSquaresY);
        for (int i = startX; i<endCol; i++){
            for (int j = startY; j<endRow; j++){
                isShip[i][j] = true;
            }
        }
    }
    
    //Because the player is going to need to be able to see their ships, but not
    //the opponent's board, we should add an if statement for whether or not
    //the ships are visible (this.shipsVisible)
    public void paint(Graphics g){
        int x1, y1, x2, y2, i, j;
        x1 = xStartPos;
        x2 = squareWidth;
        for (i=0; i<numSquaresX; i++){
            y1 = xStartPos;
            y2 = squareHeight;
            
            for (j=0; j<numSquaresY; j++){
                if (this.isShip(i, j)){
                    g.setColor(Color.GRAY);
                    
                }
                else{
                    g.setColor(Color.BLUE);
                }
                g.fillRect(x1, y1, x2, y2);
                g.setColor(Color.WHITE);
                g.drawRect(x1, y1, x2, y2);
                y1 = y1 + squareHeight;
                y2 = y2 + squareHeight;
            }
            x1 = x1 + squareWidth;
            x2 = x2 + squareWidth;
        }
    }
    
    //Sets up the board for the first time
    public void initializeBoard() throws IOException{
        setSize(height,width);
        
        
    }
    
    public boolean isShip(int x, int y) {
        return this.ships[x][y] != null;
    }
    
    public boolean guess(int x, int y) {
        this.guesses[x][y] = true;
        if (this.ships[x][y] != null) {
            this.ships[x][y].hit();
            return true;
        }
        return false;
    }
    
    public boolean isGuessed(int x, int y) {
        return this.guesses[x][y];
    }
    
    //Returns the size of the playing board
    public int getBoardSize() {
        return this.ships.length;
    }
    
    public Ship[][] getShips() {
        return this.ships;
    }
}
