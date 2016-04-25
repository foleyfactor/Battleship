/*
 * Written by  Alex Foley & James Milne for the 
 * ICS4UI Software Design Project
 */
package battleship;

import javax.swing.JFrame;

public class Battleship {
    
    public static final Ship[] battleships = new Ship[] {new Ship(5, true), new Ship(4, true), new Ship(3, true), new Ship(3, true), new Ship(2, true)};
    
    public static void main(String[] args) {
        StartScreen s1 = new StartScreen();
        s1.setVisible(true);
        
        if (s1.canStart()==true){
            GameScreen g1 = new GameScreen("medium");
            g1.getBigBoard().setShipsToBePlaced(battleships);
            g1.getSmallBoard().setShipsToBePlaced(battleships);
            g1.getBigBoard().setKeyBindings();
            g1.setVisible(true);
        } else {
        }
        
        
    }
}
