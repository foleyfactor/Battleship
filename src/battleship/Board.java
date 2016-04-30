/*
 * Board Class
 * 
 * Written by  Alex Foley & James Milne for the 
 * ICS4UI Software Design Project
 */

package battleship;

//Imports
import java.awt.*; //needed for graphics
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

//Class declaration
public class Board extends JPanel implements MouseListener, MouseMotionListener {
    
    //Class variables
    private Ship[][] ships;
    private Ship[] toBePlaced;
    private boolean shipsVisible, isFinished, isBeingPlaced, mouseInPanel;
    private Ship currShip;
    private boolean[][] guesses;
    private int mouseX=0, mouseY=0;
    private int numHits, numHitsNeeded;
    
    private Board oBoard;
    private AI AI;
    private GameScreen screen;
    
    private int size;    
    private int squareSize;    
    
    //Class constructor
    public Board(int s, int size, boolean v, GameScreen g) {
        //Create a new array of ships and guesses
        this.ships = new Ship[s][s];
        this.guesses = new boolean[s][s];
        
        //Initialize the board to not be defeated
        this.isFinished = false;
        
        //Initialize whether or not the ships are visible, and whether or not
        //the user is placing ships
        this.shipsVisible = v;
        this.isBeingPlaced = v;
        
        //Initialize the size of the board (pixels), and calculate the number size
        //(pixels) of each individual square.
        this.size = size;
        this.squareSize = size/s;
        
        //Initialize the variable keeping track of whether or not the mouse is 
        //on the board and the GameScreen parent of the board
        this.mouseInPanel = false;
        this.screen = g;
        
        //Initialize the number of hits and the number required to win to 0
        this.numHits = 0;
        this.numHitsNeeded = 0;
    }
    
    //Method for copying one board to another
    public void copyBoard(Board o) {
        //Copy all of the Board Class's specific values to the other board
        this.ships = copy(o.ships);
        this.guesses = copy(o.guesses);
        this.isFinished = o.isFinished;
        this.shipsVisible = o.shipsVisible;
        this.isBeingPlaced = o.isBeingPlaced;
        this.mouseInPanel = o.mouseInPanel;
        this.oBoard = o.oBoard;
        this.AI = o.AI;
        
        //Then, repaint the board
        this.repaint();
    }
    
    //Function that determines whether or not a ship can be placed at a certain
    //location
    public boolean canBePlaced(Ship s, int x, int y) {
        //If the ship isn't a real ship, we cannot place it
        if (s == null) return false;
        
        //Find out whether or not the ship is vertical
        if (s.isVertical()) {
            //If it's vertical, loop through the vertical squares that it would
            //occupy
            for (int i=0; i<s.getYSize(); i++) {
                try {
                    //If there is already a ship occupying that position, then
                    //this ship cannot be placed there
                    if (this.ships[y+i][x] != null) {
                        return false;
                    }
                } catch (Exception e) {
                    //Will catch an ArrayOutOfBoundsException (which would indicate
                    //that the ship would not fit on the board at the specified
                    //position), so return false
                    return false;
                }
            }
            //If it passes all of the checks, then it can be placed here!
            return true;
        }
        //Otherwise, loop through the ship horizontally
        for (int i=0; i<s.getXSize(); i++) {
            try {
                //If there is already a ship occupying that position, then
                //this ship cannot be placed there
                if (this.ships[y][x+i] != null) {
                    return false;
                }
            } catch (Exception e) {
                //Will catch an ArrayOutOfBoundsException (which would indicate
                //that the ship would not fit on the board at the specified
                //position), so return false
                return false;
            }
        }
        //Again, if it passes all of the checks, then the ship will fit
        return true;
    }
    
    //Method for placing the ship on the board
    public void placeShip(Ship s, int x, int y){
        //Update the ship's status to be placed
        s.place();
        
        //Check the ship's orientation
        if (s.isVertical()) {
            //Loop through and set all of the squares to be equal to the ship
            for (int i=0; i<s.getYSize(); i++) {
                this.ships[y+i][x] = s;
            }
        } else {
            //Loop through and set all of the squares to be equal to the ship
            for (int i=0; i<s.getXSize(); i++) {
                this.ships[y][x+i] = s;
            }
        }
        
        //If all of the ships are placed, update the board's status so that 
        //it is no longer placing ships
        if (this.allShipsPlaced()) {
            this.isBeingPlaced = false;
        } else {
        //If there are still more ships to place, find out which ship is next
            this.currShip = this.getNextShip(s);
        }
    }
    
    //Function for copying a board's array of ships (our implementation of a deepcopy,
    //if you will)
    public static Ship[][] copy(Ship[][] s) {
        //Create another array to copy the ships into
        Ship[][] copy = new Ship[s.length][s[0].length];
        
        //Loop through all of the ships in the array
        for (int i=0; i<s.length; i++) {
            for (int j=0; j<s[0].length; j++) {
                
                //Copy the ship (using the ship's copy method) to the copy array
                copy[i][j] = Ship.copy(s[i][j]);
            }
        }
        //Return the copy array
        return copy;
    }
    
    //Function for copying an array of booleans (again, our creation of a deepcopy)
    public static boolean[][] copy(boolean[][] b) {
        //Create a new array to copy into
        boolean[][] copy = new boolean[b.length][b[0].length];
        
        //Loop through all of the arrays in the old array
        for (int i=0; i<b.length; i++) {
            //Copy the old array into the new array
            copy[i] = Arrays.copyOf(b[i], b[i].length);
        }
        //Return the copy array
        return copy;
    }  
    
    //Method for swapping two boards (i.e. flipping the big and small boards on
    //the screen once the user has placed all of their ships)
    public void swapBoards(Board o) {
        //Create a temporary board
        Board temp = new Board(this.getBoardSize(), this.size, this.shipsVisible, null);
        
        //Copy this board to the temp, the other board to this and then the temp
        //board the the other board
        temp.copyBoard(this);
        this.copyBoard(o);
        o.copyBoard(temp);
        
        //Then, flip the other board references
        this.oBoard = o;
        o.oBoard = this;
    }
    
    //JPanel's paint method for painting the boards
    @Override
    public void paintComponent(Graphics g) {
        //This gets rid of any old graphics that we're done with
        super.paintComponent(g);
        
        //Create a new buffered image (to prevent screen flickering)
        BufferedImage img = new BufferedImage(this.size+1, this.size+1, BufferedImage.TYPE_INT_ARGB);
        
        //Get the graphics reference for the buffered image
        Graphics2D gr = (Graphics2D) img.getGraphics();
        
        //Initialize the x and y values for the painting to 0
        int xP=0, yP=0;
        
        //Loop through all of the indices in the board array
        //Note: the i (y-value) loop goes down because we reference (0,0) as the
        //bottom left corner as humans so this way (0,0) is drawn as the bottom
        //left corner
        for (int i=this.getBoardSize()-1; i>=0; i--) {
            for (int j=0; j<this.getBoardSize(); j++) {
                //If the square has been guessed and there is no ship there, color
                //ita light grey (indicating a missed guess)
                if (this.isGuessed(i, j) && !this.isShip(i, j)) {
                    gr.setColor(Color.LIGHT_GRAY);
                //If the square has been guessed and there is a ship there, color
                //if red (indicating a hit guess)
                } else if (this.isGuessed(i, j)) {
                    gr.setColor(Color.RED);
                //If there is a ship there, and the board is supposed to display
                //the ships on the board, color it grey (indicating a ship)
                } else if (this.isShip(i, j) && this.shipsVisible) {
                        gr.setColor(Color.GRAY);
                //Otherwise, color it plain old white
                } else {
                    gr.setColor(Color.WHITE);
                }
                //Fill a rectangle with the specified color
                gr.fillRect(xP, yP, this.squareSize, this.squareSize);
                
                //Then, outline a rectangle with black (draws the borders for all
                //of the squares)
                gr.setColor(Color.BLACK);
                gr.drawRect(xP, yP, this.squareSize, this.squareSize);
                
                //Increment the x position
                xP += this.squareSize;
            }
            //Reset the x position and increment the y position
            xP = 0;
            yP += this.squareSize;
        }
        
        //Draw the image to the JPanel
        g.drawImage(img, 0, 0, null);
    }
    
    //Method for painting the JPanel with a ship overlayed (for while the user
    //is placing their ships)
    public void paintComponent(Graphics g, int x, int y, Ship s) {
        //Only draw the ships if the ships are visible (the user can see the ships),
        if (this.shipsVisible) {
            
            //Create a new buffered image (to prevent screen flickering)
            BufferedImage img = new BufferedImage(this.size+1, this.size+1, BufferedImage.TYPE_INT_ARGB);

            //Get the graphics reference for the buffered image
            Graphics2D gr = (Graphics2D) img.getGraphics();

            //Initialize the x and y values for the painting to 0
            int xP=0, yP=0;

            //Loop through all of the indices in the board array
            //Note: the i (y-value) loop goes down because we reference (0,0) as the
            //bottom left corner as humans so this way (0,0) is drawn as the bottom
            //left corner
            for (int i=this.getBoardSize()-1; i>=0; i--) {
                for (int j=0; j<this.getBoardSize(); j++) {
                    //If the square has been guessed and there is no ship there, color
                    //ita light grey (indicating a missed guess)
                    if (this.isGuessed(i, j) && !this.isShip(i, j)) {
                        gr.setColor(Color.LIGHT_GRAY);
                    //If the square has been guessed and there is a ship there, color
                    //if red (indicating a hit guess)
                    } else if (this.isGuessed(i, j)) {
                        gr.setColor(Color.RED);
                    //If there is a ship there, and the board is supposed to display
                    //the ships on the board, color it grey (indicating a ship)
                    } else if (this.isShip(i, j) && this.shipsVisible) {
                            gr.setColor(Color.GRAY);
                    //Otherwise, color it plain old white
                    } else {
                        gr.setColor(Color.WHITE);
                    }
                    //Fill a rectangle with the specified color
                    gr.fillRect(xP, yP, this.squareSize, this.squareSize);

                    //Then, outline a rectangle with black (draws the borders for all
                    //of the squares)
                    gr.setColor(Color.BLACK);
                    gr.drawRect(xP, yP, this.squareSize, this.squareSize);

                    //Increment the x position
                    xP += this.squareSize;
                }
                //Reset the x position and increment the y position
                xP = 0;
                yP += this.squareSize;
            }

            //If ships are still being placed (if we have to overlay the current
            //ship being placed)
            if (this.isBeingPlaced) {
                
                //Subtract half of the size of the ship's square so that it looks
                //prettier
                x -= this.squareSize/2;
                y -= this.squareSize/2;
                
                //Initialize the size of the ship
                int currSize = Math.max(this.currShip.getYSize(), this.currShip.getXSize());

                //Loop through the squares on the ship that need to be overlayed
                for (int i=0; i<currSize; i++) {
                    //Fill a dark gray square at the current position
                    gr.setColor(Color.GRAY);
                    gr.fillRect(x, y, this.squareSize, this.squareSize);
                    
                    //Outline a rectangle with black (draws the black border)
                    //at the same position
                    gr.setColor(Color.BLACK);
                    gr.drawRect(x, y, this.squareSize, this.squareSize);
                    
                    //Increment the x/y position depending on the ship's orientation
                    if (this.currShip.isVertical()) {
                        y -= this.squareSize;
                    } else {
                        x += this.squareSize;
                    }
                }
            }
            //Draw the image to the JPanel
            g.drawImage(img, 0, 0, null);
        }
    }
    
    
    //Function that returns true if there is a ship on the board at (x,y) and 
    //false if not
    public boolean isShip(int x, int y) {
        return this.ships[x][y] != null;
    }
    
    //Function for guessing the position (x,y) on the board
    //Note: while this method could have the void return type, the boolean return
    //allows us to evaluate whether or not the guess was a hit right away (useful
    //for the AI class)
    public boolean guess(int x, int y) {
        //Update the guesses array to reflect this square is guessed
        this.guesses[x][y] = true;
        
        //If there is a ship where it 
        if (this.ships[x][y] != null) {
            //Hit the ship
            this.ships[x][y].hit();
            
            //Increment the number of ships that have been hit
            this.numHits++;
            
            //If the number of hits on the board is equal to the total number of 
            //ship segments on the board, update the board to be finished
            if (this.numHits == this.numHitsNeeded) {
                this.isFinished = true;
            }
            
            //Return true (indicating the guess was a hit)
            return true;
        }
        //Return false (indicating the guess was a miss)
        return false;
    }
    
    //Returns true if the square (x,y) has already been guessed and false otherwise
    public boolean isGuessed(int x, int y) {
        return this.guesses[x][y];
    }
    
    //Returns the array of ships that are on the board
    public Ship[] getPlacingShips() {
        return this.toBePlaced;
    }
    
    //Function to get the coordinate of the squares from the coordinates of mouse
    //interaction
    public int[] getCoords(int x, int y) {
        boolean xLine = (x%this.squareSize) == 0;
        boolean yLine = (y%this.squareSize) == 0;
        
        //Set the current index of the squares to 0
        int squareIndexX = 0;
        int squareIndexY = 0;
        
        //Make the returns negative if the user clicked on the edge of a square
        if (xLine) squareIndexX = -1;
        if (yLine) squareIndexY = this.getBoardSize();
        
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
    
    //Sets the array of the board equal to the given array
    public void setAI(AI a) {
        this.AI = a;
    }
    
    //Sets the opposing board to be the given board and vice versa
    public void setOBoard(Board o) {
        this.oBoard = o;
        o.oBoard = this;
    }
    
    //Returns the size of the playing board
    public int getBoardSize() {
        return this.ships.length;
    }
    
    //Returns the size of the squares
    public int getSquareSize() {
        return this.squareSize;
    }
    
    //Returns the opposing board for this board
    public Board getOBoard() {
        return this.oBoard;
    }
    
    //Returns the AI for this board
    public AI getAI() {
        return this.AI;
    }
    
    //Sets the ships that are in play for the board
    public void setShipsToBePlaced(Ship[] s) {
        //Set the array of ships to the given array
        this.toBePlaced = s;
        
        //Update the current ship
        this.currShip = this.getNextShip(this.toBePlaced[this.toBePlaced.length-1]);
        
        //Increment the number of hits needed based on the size of the ship
        for (Ship sh : s) {
            this.numHitsNeeded += Math.max(sh.getXSize(), sh.getYSize());
        }
    }
    
    //Function for getting the user's next ship (to be placed)
    public Ship getNextShip(Ship s) {
        //Add 1 to the index of the current ship (wrapping to 0 if necessary)
        int index = (Arrays.asList(this.toBePlaced).indexOf(s)+1)%this.toBePlaced.length;
        
        //While the ship is already placed, increment the current ship (again,
        //wrapping to 0 if necessary)
        while (this.toBePlaced[index].isPlaced()) {
            index = (index+1)%this.toBePlaced.length;
        }
        //Return the next ship to be placed
        return this.toBePlaced[index];
    }
    
    //Function for determining whether or not all of the ships have been placed
    public boolean allShipsPlaced() {
        //Loop through all of the ships
        for (Ship s : this.toBePlaced) {
            //If the current ship isn't placed, then all of the ships aren't placed
            //so return false
            if (! s.isPlaced()) {
                return false;
            }
        }
        //If all of the ships are placed, return true
        return true;
    }
    
    //Method called when the game has been won
    public void endGame(boolean userWin) {
        //Save the data from the user's ship placement for the AI
        this.oBoard.getAI().saveData();
        
        String message;
        //Initialize the message based on whether or not the user has won
        if (userWin) {
            message = "You have won!";
        } else {
            message = "The AI has won :(";
        }
        
        //Show a dialog box with the message and ask if the user wants to play again
        int response = JOptionPane.showConfirmDialog(this.screen, "Would you like to play again?", message, JOptionPane.YES_NO_OPTION);

        //If they do want to play again
        if (response == JOptionPane.YES_OPTION){
            //Create a new starting screen, and make it visible
            StartScreen s2 = new StartScreen();
            s2.setVisible(true);

            //Set the window's closing action to be disposed, then close the window
            this.screen.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.screen.dispatchEvent(new WindowEvent(screen, WindowEvent.WINDOW_CLOSING));

        }
        
        //If they don't want to play again :(
        if (response == JOptionPane.NO_OPTION){
            //Exit the program
            System.exit(0);
        }
    }

    //Method called when the mouse is clicked (Abstract - from MouseListener)
    @Override
    public void mouseClicked(MouseEvent e) {
        //If the user left-clicked
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (this.mouseInPanel) {
                //Get the coordinates on the board from the mouse coordinates
                int[] d = this.getCoords(e.getX(), e.getY());
                //If the coordinates are good (non-negative => not on an edge)
                if (d[0] >= 0 && d[1] >= 0) {
                    //If the user is placing ships and the ship can be placed here
                    if (this.isBeingPlaced && this.canBePlaced(this.currShip, d[0], d[1])) {
                        //Place the ship
                        this.placeShip(this.currShip, d[0], d[1]);
                        //Repaint the board
                        this.repaint();

                        //If the user is done placing now, switch the boards in the screen
                        if (! this.isBeingPlaced) {
                            this.swapBoards(this.oBoard);
                            this.oBoard.AI.placeShipsRandomly();
                            
                            //Caused a bug without this
                            this.mouseInPanel = true;
                        }

                    //If the user isn't placing ships (i.e. they're guessing
                    //ships), and this spot hasn't been guessed yet
                    } else if (! this.isBeingPlaced && !this.isGuessed(d[1], d[0])) {
                        //Guess the current square
                        this.guess(d[1], d[0]);

                        //Repaint the board
                        this.repaint();

                        //Draw the stats on the screen
                        this.screen.drawStats();

                        //If the user has now won
                        if (this.isFinished) {
                            //End the game
                            this.endGame(true);
                        }

                        //Have the AI guess
                        this.oBoard.AI.guess();

                        //Redraw the AI's board
                        this.oBoard.repaint();

                        //Draw the stats on the screen
                        this.screen.drawStats();

                        //If the AI has just won
                        if(this.oBoard.isFinished){
                            //End the game
                            this.endGame(false);
                        }
                    }
                }
            }
        //If the user right-clicked
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            //If the user is still placing ships
            if (this.isBeingPlaced) {
                //Update the current ship to be the next ship (skip 1 ahead)
                this.currShip = this.getNextShip(this.currShip);
                //Repaint the ship overlay
                this.paintComponent(this.getGraphics(), e.getX(), e.getY(), this.currShip);
            }
        }
    }

    //Abstract method that comes with the MouseListener implementation - 
    //Java doesn't like if we don't have it
    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    //Abstract method that comes with the MouseListener implementation - 
    //Java doesn't like if we don't have it
    @Override
    public void mouseReleased(MouseEvent e) {
        
    }
    
    //Method called when the mouse enters the JPanel
    @Override
    public void mouseEntered(MouseEvent e) {
        //Set the mouse to be in the panel
        this.mouseInPanel = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //Set the mouse to be out of the panel
        this.mouseInPanel = false;
        
        //Repaint the ship overlay to be waaaay off screen
        this.paintComponent(this.getGraphics(), Integer.MAX_VALUE, Integer.MAX_VALUE, this.currShip);
    }

    //Abstract method that comes with the MouseMotionListener implementation - 
    //Java doesn't like if we don't have it
    @Override
    public void mouseDragged(MouseEvent e) {
        
    }

    //Method called when the mouse is moved
    @Override
    public void mouseMoved(MouseEvent e) {
        //If the mouse is in the JPanel
        if (this.mouseInPanel) {
            //If ships are still being placed, redraw the ship overlay
            if (this.isBeingPlaced) this.paintComponent(this.getGraphics(), e.getX(), e.getY(), this.currShip);
            
            //Update the mouse's position
            this.mouseX = e.getX();
            this.mouseY = e.getY();
        }
    }
    
    //Method for setting the key bindings (so that space can be pressed to 
    //rotate the ship)
    public void setKeyBindings() {
        //Get the input map for the JPanel when it is focused
        InputMap in = this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        
        //Get the action map for the JPanel
        ActionMap action = this.getActionMap();
        
        //Add the "space" object to the input map with the key being the keystroke
        //of space being pressed
        in.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "space");
        
        //Initialize a board (so that it can be referenced in the action)
        final Board b = this;
        
        //Add an action to the action map with the key of "space"
        action.put("space", new AbstractAction() {
            //Method called when the action happens
            @Override
            public void actionPerformed(ActionEvent a) {
                //If the mouse is in the board and ships are still being placed
                if (b.mouseInPanel && b.isBeingPlaced) {
                    //Get the index of the current ship in the board's ships
                    int index = Arrays.asList(b.toBePlaced).indexOf(b.currShip);
                    
                    //Set the current ship to be the rotated version of itself
                    b.currShip = b.currShip.rotate();
                    
                    //Update the ship in the array to be the rotated version
                    b.toBePlaced[index] = b.currShip;
                    
                    //Repaint the screen with the ship overlay
                    b.paintComponent(b.getGraphics(), b.mouseX, b.mouseY, b.currShip);
                }
            }
        });
    }
}
