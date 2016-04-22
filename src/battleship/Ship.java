/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package battleship;

public class Ship {
    private int xSize, ySize;
    private boolean vertical, isSunk, isPlaced;
    private int numHits;
    
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
        this.numHits = 0;
    }
    
    public static Ship copy(Ship s) {
        if (s == null) return null;
        return new Ship(Math.max(s.xSize, s.ySize), s.vertical);
    }
    
    public Ship rotate() {
        return new Ship(Math.max(this.xSize, this.ySize), !this.vertical);
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
    
    public boolean isSunk() {
        return this.isSunk;
    }
    
    public int getXSize() {
        return this.xSize;
    }
    
    public int getYSize() {
        return this.ySize;
    }
    
    public boolean isVertical() {
        return this.vertical;
    }
}
