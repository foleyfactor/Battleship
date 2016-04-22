/*
 * 
 */
// Worked on by James

package battleship;


import java.awt.*; //needed for graphics
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Arrays;
import javax.swing.*;
import battleship.Ship;
import java.awt.event.ActionEvent;

public class Board extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private Ship[][] ships;
    private Ship[] toBePlaced;
    private boolean shipsVisible, isFinished, isBeingPlaced, mouseInPanel;
    private Ship currShip;
    private boolean[][] guesses;
    private int mouseX=0, mouseY=0;
    
    //Want to test how my AI does :)
    private Board oBoard;
    private AI AI;
    private static Random testRandom = new Random();
    
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
        this.isBeingPlaced = v;
        this.mouseInPanel = false;
        //this.currShip = new Ship(testRandom.nextInt(4)+2, testRandom.nextBoolean());
    }
    
    public void copyBoard(Board o) {
        this.ships = copy(o.ships);
        this.guesses = copy(o.guesses);
        this.isFinished = o.isFinished;
        this.shipsVisible = o.shipsVisible;
        this.isBeingPlaced = o.isBeingPlaced;
        this.mouseInPanel = o.mouseInPanel;
        this.oBoard = o.oBoard;
        this.AI = o.AI;
        this.repaint();
    }
    
    public Board getOBoard() {
        return this.oBoard;
    }
    
    public boolean canBePlaced(Ship s, int x, int y) {
        if (s == null) return false;
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
        s.place();
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
        if (this.allShipsPlaced()) {
            this.isBeingPlaced = false;
        } else {
            this.currShip = this.getNextShip(s);
        }
    }
    
    public void setAI(AI a) {
        this.AI = a;
    }
    
    public void setOBoard(Board o) {
        this.oBoard = o;
        o.oBoard = this;
    }
    
    public static Ship[][] copy(Ship[][] s) {
        Ship[][] copy = new Ship[s.length][s[0].length];
        for (int i=0; i<s.length; i++) {
            for (int j=0; j<s[0].length; j++) {
                copy[i][j] = Ship.copy(s[i][j]);
            }
        }
        return copy;
    }
    
    public static boolean[][] copy(boolean[][] b) {
        boolean[][] copy = new boolean[b.length][b[0].length];
        for (int i=0; i<b.length; i++) {
            for (int j=0; j<b[0].length; j++) {
                copy[i][j] = b[i][j];
            }
        }
        return copy;
    }
    
    public void testAI() {
        for (int i=0; i<30; i++) {
            int[] guess = this.AI.randomGuessUntilHit();
            if (this.guess(guess[0], guess[1])) {
                this.AI.hit();
            }
            this.oBoard.repaint();
        }
    }    
    
    public void swapBoards(Board o) {
        Board temp = new Board(this.getBoardSize(), this.size, this.shipsVisible);
        temp.copyBoard(this);
        this.copyBoard(o);
        o.copyBoard(temp);
        
        this.oBoard = o;
        o.oBoard = this;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage img = new BufferedImage(this.size+1, this.size+1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gr = (Graphics2D) img.getGraphics();
        int xP=0, yP=0;
        for (int i=this.getBoardSize()-1; i>=0; i--) {
            for (int j=0; j<this.getBoardSize(); j++) {
                if (this.isGuessed(i, j) && !this.isShip(i, j)) {
                    gr.setColor(Color.LIGHT_GRAY);
                } else if (this.isGuessed(i, j)) {
                    gr.setColor(Color.RED);
                } else if (this.isShip(i, j) && this.shipsVisible) {
                        gr.setColor(Color.GRAY);
                } else {
                    gr.setColor(Color.WHITE);
                }
                gr.fillRect(xP, yP, this.squareSize, this.squareSize);
                gr.setColor(Color.BLACK);
                gr.drawRect(xP, yP, this.squareSize, this.squareSize);

                xP += this.squareSize;
            }
            xP = 0;
            yP += this.squareSize;
        }
        g.drawImage(img, 0, 0, null);
    }
    
    public void paintComponent(Graphics g, int x, int y, Ship s) {
        if (this.shipsVisible) {
            BufferedImage img = new BufferedImage(this.size+1, this.size+1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gr = (Graphics2D) img.getGraphics();

            int xP=0, yP=0;
            for (int i=this.getBoardSize()-1; i>=0; i--) {
                for (int j=0; j<this.getBoardSize(); j++) {
                    if (this.isGuessed(i, j) && !this.isShip(i, j)) {
                        gr.setColor(Color.LIGHT_GRAY);
                    } else if (this.isGuessed(i, j)) {
                        gr.setColor(Color.RED);
                    } else if (this.isShip(i, j) && this.shipsVisible) {
                            gr.setColor(Color.GRAY);
                    } else {
                        gr.setColor(Color.WHITE);
                    }
                    gr.fillRect(xP, yP, this.squareSize, this.squareSize);
                    gr.setColor(Color.BLACK);
                    gr.drawRect(xP, yP, this.squareSize, this.squareSize);

                    xP += this.squareSize;
                }
                xP = 0;
                yP += this.squareSize;
            }

            if (this.isBeingPlaced) {
                x -= this.squareSize/2;
                y -= this.squareSize/2;
                int currSize = Math.max(this.currShip.ySize, this.currShip.xSize);

                for (int i=0; i<currSize; i++) {
                    gr.setColor(Color.GRAY);
                    gr.fillRect(x, y, this.squareSize, this.squareSize);
                    gr.setColor(Color.BLACK);
                    gr.drawRect(x, y, this.squareSize, this.squareSize);
                    if (this.currShip.vertical) {
                        y -= this.squareSize;
                    } else {
                        x += this.squareSize;
                    }
                }
            }

            g.drawImage(img, 0, 0, null);
            try {
                Thread.sleep(40);
            } catch (Exception e) {

            }
        }
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
    
    public Ship[] getPlacingShips() {
        return this.toBePlaced;
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
    
    public void setShipsToBePlaced(Ship[] s) {
        this.toBePlaced = s;
        this.currShip = this.getNextShip(this.toBePlaced[0]);
        System.out.println("initialized it.");
        System.out.println(this.currShip.xSize + " " + this.currShip.vertical);
    }
    
    public Ship getNextShip(Ship s) {
        int index = (Arrays.asList(this.toBePlaced).indexOf(s)+1)%this.toBePlaced.length;
        while (this.toBePlaced[index].isPlaced()) {
            index = (index+1)%this.toBePlaced.length;
        }
        return this.toBePlaced[index];
    }
    
    public boolean allShipsPlaced() {
        for (Ship s : this.toBePlaced) {
            if (! s.isPlaced) {
                return false;
            }
        }
        return true;
    }
    
    public boolean allSunk() {
        for (Ship s : this.toBePlaced) {
            System.out.println("Ship sunk: " + s.isSunk);
            if (!s.isSunk) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("hello");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println("yolo");
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            this.toBePlaced[Arrays.asList(this.toBePlaced).indexOf(this.currShip)] = this.currShip.rotate();
            this.currShip = this.currShip.rotate();
            this.paintComponent(this.getGraphics(), this.mouseX, this.mouseY, this.currShip);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (! (e.getX() > this.size || e.getX() < 0)) {
                if (! (e.getY() > this.size || e.getY() < 0)) {
                    int[] d = this.getCoords(e.getX(), e.getY());
                    if (this.canBePlaced(this.currShip, d[0], d[1]) && this.isBeingPlaced) {
                        this.placeShip(this.currShip, d[0], d[1]);
                        this.repaint();
                        if (! this.isBeingPlaced) {
                            this.swapBoards(this.oBoard);
                            this.oBoard.AI.placeShipsRandomly();
                        }
                    } else if (! this.isBeingPlaced && !this.shipsVisible) {
                        this.guess(d[1], d[0]);
                        this.repaint();
                        this.oBoard.AI.guess("medium");
                        this.oBoard.repaint();
                        if (this.isFinished || this.oBoard.isFinished) {
                            System.out.println("The winner has won!");
                        }
                    }
                }
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            this.currShip = this.getNextShip(this.currShip);
            this.paintComponent(this.getGraphics(), e.getX(), e.getY(), this.currShip);
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
        this.mouseInPanel = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.mouseInPanel = false;
        this.paintComponent(this.getGraphics(), Integer.MAX_VALUE, Integer.MAX_VALUE, this.currShip);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (this.mouseInPanel) {
            this.paintComponent(this.getGraphics(), e.getX(), e.getY(), this.currShip);
            this.mouseX = e.getX();
            this.mouseY = e.getY();
        }
    }
    
    public void setKeyBindings() {
        InputMap in = getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap action  = getActionMap();
        in.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "space");
        final Board b = this;
        action.put("space", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent a) {
                int index = Arrays.asList(b.toBePlaced).indexOf(b.currShip);
                b.currShip = b.currShip.rotate();
                b.toBePlaced[index] = b.currShip;
                b.paintComponent(b.getGraphics(), b.mouseX, b.mouseY, b.currShip);
            }
        });
    }
}
