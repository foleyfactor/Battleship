/*
 * Written by Islam Elmallah, Alex Foley, James Milne for the 
 * ICS4UI Software Design Project
 */
package battleship;

import javax.swing.JFrame;

public class Battleship {

    
    public static void main(String[] args) {
        Board b = new Board(10, 100, true);
        Ship s = new Ship(4, true);
        Ship s1 = new Ship(5, false);
        b.placeShip(s, 1,1);
        if (b.canBePlaced(s1, 1, 1)) {
            b.placeShip(s1, 1, 1);
        }
        
        JFrame f = new JFrame();
        f.setSize(400, 400);
        f.getContentPane().add(b);
        b.addMouseListener(b);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        
//        //Board b1 = new Board(4, true);
//        Board b2 = new Board(10, 100, true);
//        //Ship[] s1 = new Ship[1];
//        Ship[] s2 = new Ship[1];
//        Player p1 = new Player(b, new Ship[]{s});
//        AI p2 = new AI(b2, s2, p1);
//        for (int i=0; i<15; i++) {
//            int[] coord = p2.randomGuessUntilHit();
//            System.out.println("My guess is: " + coord[0] + ", " + coord[1]);
//        }
    }
}
