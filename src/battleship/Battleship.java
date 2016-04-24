/*
 * Written by  Alex Foley & James Milne for the 
 * ICS4UI Software Design Project
 */
package battleship;

import javax.swing.JFrame;

public class Battleship {
    
    public static void main(String[] args) {
        StartScreen s1 = new StartScreen();
        s1.setVisible(true);
        
        if (s1.canStart()==true){
            GameScreen g1 = new GameScreen();
            g1.getBigBoard().setShipsToBePlaced(new Ship[] {new Ship(2, true), new Ship(3, false), new Ship(4, false), new Ship(5, true)});
            g1.getSmallBoard().setShipsToBePlaced(new Ship[] {new Ship(2, true), new Ship(3, false), new Ship(4, false), new Ship(5, true)});
            g1.getBigBoard().setOBoard(g1.getSmallBoard());
            //g1.getSmallBoard().setAI(new AI(g1.getBigBoard()));
            g1.getBigBoard().setAI(new AI(g1.getSmallBoard()));
            g1.getBigBoard().setKeyBindings();
            g1.setVisible(true);
        } else {
        }
        
        
    }
}
