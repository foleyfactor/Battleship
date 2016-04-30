/*
 * Battleship (Main) Class
 * 
 * Instructions:
 * 1. Select your difficulty
 * 2. Place your ships on the big board, you can click space to rotate or right-click
 *    to skip to the next ship
 * 3. Click on the big board to guess where the AI placed its ships; the AI will
 *    guess on the small board to try to hit your ships
 * 4. Once you or the AI have hit all of the squares, the game is over. Choose whether
 *    or not you want to play again.
 * 
 * Written by  Alex Foley & James Milne for the 
 * ICS4UI Software Design Project
 */
package battleship;

//Class declaration
public class Battleship {
    
    public static void main(String[] args) {
        //Create and show the start screen
        StartScreen s1 = new StartScreen();
        s1.setVisible(true);
    }
}
