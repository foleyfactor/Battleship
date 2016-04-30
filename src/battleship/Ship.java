/*
 * Ship Class
 * 
 * Written by  Alex Foley & James Milne for the 
 * ICS4UI Software Design Project
 */

package battleship;

//Class declaration
public class Ship {
    
    //Class variables
    private int xSize, ySize;
    private boolean vertical, isSunk, isPlaced;
    private int numHits;
    
    //Class constructor
    public Ship(int s, boolean v) {
        //Set the ship's orientation
        this.vertical = v;
        
        //Set the x and y sizes based on the length and orientation of the ship
        if (v) {
            this.xSize = 1;
            this.ySize = s;
        } else {
            this.xSize = s;
            this.ySize = 1;
        }
        //Initialize the ship's state to not sunk and not placed
        this.isSunk = false;
        this.isPlaced = false;
        
        //Initialize the ship to have no hits
        this.numHits = 0;
    }
    
    //Function for copying (deep copy) a ship
    public static Ship copy(Ship s) {
        //If the ship being copied doesn't exist, return null
        if (s == null) return null;
        //Otherwise, return a new ship with the same size and orientation as this
        //one
        return new Ship(Math.max(s.xSize, s.ySize), s.vertical);
    }
    
    //Returns a rotated copy of this ship
    public Ship rotate() {
        return new Ship(Math.max(this.xSize, this.ySize), !this.vertical);
    }
    
    //Increments the number of times the ship has been hit, sinks the ship 
    //if necessary
    public void hit() {
        this.numHits ++;
        if (this.numHits == Math.max(xSize, ySize)) this.sink();
    }
    
    //Sinks the ship (sets the sunk variable to true)
    public void sink() {
        this.isSunk = true;
    }
    
    //Places the ship (sets the placed variable to true)
    public void place() {
        this.isPlaced = true;
    }
    
    //Returns whether or not the ship is placed
    public boolean isPlaced() {
        return this.isPlaced;
    }
    
    //Returns whether or not the ship is sunk
    public boolean isSunk() {
        return this.isSunk;
    }
    
    //Returns the size of the ship in the x direction
    public int getXSize() {
        return this.xSize;
    }
    
    //Returns the size of the ship in the y direction
    public int getYSize() {
        return this.ySize;
    }
    
    //Returns whether or not this ship is vertical
    public boolean isVertical() {
        return this.vertical;
    }
}
