/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package battleship;

public class Ship {
    public int xSize, ySize;
    public boolean vertical;
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
    }
    
    public void hit() {
        this.numHits ++;
    }
}
