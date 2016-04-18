/*
 * Written by Islam Elmallah, Alex Foley, James Milne for the 
 * ICS4UI Software Design Project
 */
package battleship;

public class Battleship {

    
    public static void main(String[] args) {
        Board b = new Board(10);
        Ship s = new Ship(4, true);
        b.placeShip(50,40,43,50);
        
        System.out.println(b.canBePlaced(s, 7, 7));
    }
}
