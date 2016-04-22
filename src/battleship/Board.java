/*
 * 
 */
// Worked on by James and Alex

package battleship;


import java.awt.*; //needed for graphics
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class Board extends JPanel implements MouseListener, MouseMotionListener {
    private Ship[][] ships;
    private Ship[] toBePlaced;
    private boolean shipsVisible, isFinished, isBeingPlaced, mouseInPanel;
    private Ship currShip;
    private boolean[][] guesses;
    private int mouseX=0, mouseY=0;
    private int numHits, numHitsNeeded;
    
    private Board oBoard;
    private AI AI;
    
    private int size;    
    private int squareSize;    
    
    public Board(int s, int size, boolean v) {
        this.ships = new Ship[s][s];
        this.guesses = new boolean[s][s];
        this.isFinished = false;
        this.shipsVisible = v;
        this.size = size;
        this.squareSize = size/s;
        this.isBeingPlaced = v;
        this.mouseInPanel = false;
        
        this.numHits = 0;
        this.numHitsNeeded = 0;
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
        boolean vertical = s.isVertical();
        if (vertical) {
            for (int i=0; i<s.getYSize(); i++) {
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
        for (int i=0; i<s.getXSize(); i++) {
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

    public void placeShip(Ship s, int x, int y){
        s.place();
        if (s.isVertical()) {
            for (int i=0; i<s.getYSize(); i++) {
                this.ships[y+i][x] = s;
            }
        } else {
            for (int i=0; i<s.getXSize(); i++) {
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
                int currSize = Math.max(this.currShip.getYSize(), this.currShip.getXSize());

                for (int i=0; i<currSize; i++) {
                    gr.setColor(Color.GRAY);
                    gr.fillRect(x, y, this.squareSize, this.squareSize);
                    gr.setColor(Color.BLACK);
                    gr.drawRect(x, y, this.squareSize, this.squareSize);
                    if (this.currShip.isVertical()) {
                        y -= this.squareSize;
                    } else {
                        x += this.squareSize;
                    }
                }
            }

            g.drawImage(img, 0, 0, null);
            try {
                Thread.sleep(10);
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
            this.numHits++;
            if (this.numHits == this.numHitsNeeded) {
                this.isFinished = true;
            }
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
        for (Ship sh : s) {
            this.numHitsNeeded += Math.max(sh.getXSize(), sh.getYSize());
        }
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
            if (! s.isPlaced()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean allSunk() {
        for (Ship[] s : this.ships) {
            for (Ship sh : s) {
                if (sh != null) {
                    if (!sh.isSunk()) {
                        return false;
                    }
                }
            }
        }
        return true;
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
                    } else if (! this.isBeingPlaced && !this.isGuessed(d[1], d[0])) {
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
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
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
                if (b.mouseInPanel) {
                    int index = Arrays.asList(b.toBePlaced).indexOf(b.currShip);
                    b.currShip = b.currShip.rotate();
                    b.toBePlaced[index] = b.currShip;
                    b.paintComponent(b.getGraphics(), b.mouseX, b.mouseY, b.currShip);
                }
            }
        });
    }
}
