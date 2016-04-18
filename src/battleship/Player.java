/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package battleship;

public class Player {
    private Board board;
    private Ship[] ships;
    private Player opponent;
    private int numHits;
    
    //Player constructor
    public Player(Board b, Ship[] s) {
        this.board = b;
        this.ships = s;
        this.numHits = 0;
    }
    
    //Sets the player's opponent so that we know whose board to fire on
    public void setOpponent(Player o) {
        this.opponent = o;
    }
    
    //Fires at the board of the player's opponent
    public void fire(int x, int y) {
        if (this.opponent != null) {
            //If the square hasn't already been guessed
            if (! this.opponent.board.isGuessed(x, y)) {
                if (this.opponent.board.guess(x, y)) {
                    System.out.println("You hit a ship!");
                    this.numHits ++;
                }
            }
        }
    }
    
    //Getters
    
    public Board getBoard() {
        return this.board;
    }
    
    public Player getOpponent() {
        return this.opponent;
    }
    
    public int getNumHits() {
        return this.numHits;
    }
}
