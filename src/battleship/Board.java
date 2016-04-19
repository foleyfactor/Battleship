/*
 * 
 */
// Worked on by James

package battleship;

import java.io.*;
import java.awt.*; //needed for graphics
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

public class Board extends JPanel implements KeyListener, MouseListener{
    private Ship[][] ships;
    private boolean shipsVisible;
    private boolean[][] guesses;
    private boolean isFinished;
    
    private int size;    //in the future - we may need to display more than one board on the screen
    private int squareSize;
  
//    private int xStartPos = 20;//Sets where the board begins drawing
//    private int yStartPos = 50;//Sets where the board begins drawing
//    private int numSquaresX = 10;  These two can be replaced with this.getBoardSize();
//    private int numSquaresY = 10;
    
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
    
    
    public Board(int s, int size, boolean v) {
        this.ships = new Ship[s][s];
        this.guesses = new boolean[s][s];
        this.isFinished = false;
        this.shipsVisible = v;
        this.size = size;
        this.squareSize = size/s;
    }
    
    public boolean canBePlaced(Ship s, int x, int y) {
        boolean vertical = s.vertical;
        if (vertical) {
            for (int i=0; i<s.ySize; i++) {
                try {
                    if (this.ships[y+i][x] != null) {
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
                if (this.ships[y][x+i] != null) {
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
    public void placeShip(Ship s, int x, int y){
        if (s.vertical) {
            for (int i=0; i<s.ySize; i++) {
                this.ships[y+i][x] = s;
            }
        } else {
            for (int i=0; i<s.xSize; i++) {
                this.ships[y][x+i] = s;
                System.out.println((y+i));
            }
        }
    }
    
    //Because the player is going to need to be able to see their ships, but not
    //the opponent's board, we should add an if statement for whether or not
    //the ships are visible (this.shipsVisible)
//    public void paint(Graphics g){
//        int x1, y1, x2, y2, i, j;
//        x1 = xStartPos;
//        x2 = squareWidth;
//        for (i=0; i<numSquaresX; i++){
//            y1 = xStartPos;
//            y2 = squareHeight;
//            
//            for (j=0; j<numSquaresY; j++){
//                if (this.isShip(i, j)){
//                    g.setColor(Color.GRAY);
//                    
//                }
//                else{
//                    g.setColor(Color.BLUE);
//                }
//                g.fillRect(x1, y1, x2, y2);
//                g.setColor(Color.WHITE);
//                g.drawRect(x1, y1, x2, y2);
//                y1 = y1 + squareHeight;
//                y2 = y2 + squareHeight;
//            }
//            x1 = x1 + squareWidth;
//            x2 = x2 + squareWidth;
//        }
//    }
    
    @Override
    public void paintComponent(Graphics g) {
        int x=0, y=0;
        for (int i=this.getBoardSize()-1; i>=0; i--) {
            for (int j=0; j<this.getBoardSize(); j++) {
                if (this.isShip(i, j)) {
                    g.setColor(Color.GRAY);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillRect(x, y, this.squareSize, this.squareSize);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, this.squareSize, this.squareSize);
                
                x += this.squareSize;
            }
            x = 0;
            y += this.squareSize;
        }
    }
    
//    //Sets up the board for the first time
//    public void initializeBoard() throws IOException{
//        setSize(height,width);
//        
//        
//    }
    
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
    
    public int[] getCoords(int x, int y) {
        boolean xLine = (x%this.squareSize) == 0;
        boolean yLine = (y%this.squareSize) == 0;
        
        //Set the current index of the squares to 0
        int squareIndexX = 0;
        int squareIndexY = 0;
        
        if (xLine) squareIndexX = Integer.MAX_VALUE;
        if (yLine) squareIndexY = Integer.MAX_VALUE;
        
        //While the x-coordinate is greater than 0
        while (x > 0 && !xLine) {
            //Subtract the width of the cell
            x -= this.squareSize;
            
            //If x is less than 0, then break from the loop; done with the x
            //component
            if (x < 0) {
                break;
            }
            
            //Increment the x index
            squareIndexX++;
        }
        
        //While the y-coordinate is greater than 0
        while (y > 0 && !yLine) {
            //Subtract the width of the cell
            y -= this.squareSize;
            
            //If y is less than 0, then break from the loop; done with the y
            //component
            if (y < 0) {
                break;
            }
            
            //Increment the y index
            squareIndexY++;
        }
        
        //Return the two as an array
        return new int[] {squareIndexX, this.getBoardSize()-1-squareIndexY};
    }
    
    //Returns the size of the playing board
    public int getBoardSize() {
        return this.ships.length;
    }
    
    public int getSquareSize() {
        return this.squareSize;
    }
    
    public Ship[][] getShips() {
        return this.ships;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (! (e.getX() > this.size || e.getX() < 0)) {
            if (! (e.getY() > this.size || e.getY() < 0)) {
                int[] d = this.getCoords(e.getX(), e.getY());
                System.out.println(d[0]+ " "+ d[1]);
                Ship s = new Ship(4, false);
                if (this.canBePlaced(s, d[0], d[1])) {
                    this.placeShip(s, d[0], d[1]);
                    this.repaint();
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
