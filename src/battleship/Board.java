/*
 * 
 */
// Worked on by James

package battleship;

import java.io.*;
import java.awt.*; //needed for graphics
import javax.swing.*;

public class Board extends JFrame {
    Ship[][] ships;
    boolean[][] guesses;
    boolean isFinished;
    int height = 400;
    int width = 400;
    int xStartPos = 20;//Sets where the board begins drawing
    int yStartPos = 50;//Sets where the board begins drawing
    int numSquaresX = 10;
    int numSquaresY = 10;
    int squareWidth = 40;
    int squareHeight = 40;
    boolean isShip[][] = new boolean[10][10];
    
    
    public Board(int s) {
        this.ships = new Ship[s][s];
        this.guesses = new boolean[s][s];
        this.isFinished = false;
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
    public void placeShip(int startX, int startY, int numColumns, int numRows){
        int endCol = Math.min(startX+numColumns, numSquaresX);
        int endRow = Math.min(startY+numRows, numSquaresY);
        for (int i = startX; i<endCol; i++){
            for (int j = startY; j<endRow; j++){
                isShip[i][j] = true;
            }
        }
    }
    
    public void paint(Graphics g){
        int x1, y1, x2, y2, i, j;
        x1 = xStartPos;
        x2 = squareWidth;
        for (i=0; i<numSquaresX; i++){
            y1 = xStartPos;
            y2 = squareHeight;
            
            for (j=0; j<numSquaresY; j++){
                if (isShip[i][j] == true){
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
}
