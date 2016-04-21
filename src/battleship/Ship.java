/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package battleship;

public class Ship {
    public int xSize, ySize;
    public boolean vertical, isSunk, isPlaced;
    public int numHits;
    
    public Ship(int s, boolean v) {
        this.vertical = v;
        if (v) {
            this.xSize = 1;
            this.ySize = s;
        } else {
            this.xSize = s;
            this.ySize = 1;
        }
        this.isSunk = false;
        this.isPlaced = false;
    }
    
    public void hit() {
        this.numHits ++;
        if (this.numHits == Math.max(xSize, ySize)) this.sink();
    }
    
    public void sink() {
        this.isSunk = true;
    }
    
    public void place() {
        this.isPlaced = true;
    }
    
    public boolean isPlaced() {
        return this.isPlaced;
    }
}
