/*
 * Written by  Alex Foley & James Milne for the 
 * ICS4UI Software Design Project
 */
package battleship;

import javax.swing.JFrame;

public class Battleship {

    
    public static void main(String[] args) {
        SampleScreen s = new SampleScreen();
        s.getPanel1().setShipsToBePlaced(new Ship[] {new Ship(2, true), new Ship(3, false), new Ship(4, false), new Ship(5, true)});
        s.getPanel2().setShipsToBePlaced(new Ship[] {new Ship(2, true), new Ship(3, false), new Ship(4, false), new Ship(5, true)});
        s.getPanel1().setAI(new AI(s.getPanel1()));
        s.getPanel1().setOBoard(s.getPanel2());
        s.setVisible(true);
    }
}
