/*
 * AI Class
 */

package battleship;

import java.util.ArrayList;
import java.util.Random;

public class AI extends Player {
    
    private Random random;
    private int[][] potentials;
    private int[] lastGuess;
    private ArrayList<int[]> neighbours;
    private int lastNumHits;
    
    public AI(Board b, Ship[] s, Player o) {
        super(b, s);
        this.random = new Random();
        //TODO: change to board.getSize() when board class isn't being worked on.
        this.potentials = new int[b.ships.length][b.ships.length];
        this.setOpponent(o);
        this.lastNumHits = 0;
        this.neighbours = new ArrayList();
    }
    
    public int[] alwaysRandomGuess() {
        int x, y;
        //Keep generating random coordinates until we get one that hasn't already
        //been guessed.
        do {
            //Change these to board.getSize()
            x = this.random.nextInt(this.potentials.length);
            y = this.random.nextInt(this.potentials.length);
        } while (this.getOpponent().getBoard().guesses[x][y]);
        //this.getOpponent().getBoard().guesses[x][y] = true;

        return new int[] {x,y};
    }
    
    //Will guess randomly until it hits something, then continues to guess neighbours
    //while it keeps hitting things.
    public int[] randomGuessUntilHit() {
        boolean hitLast = this.getNumHits() > this.lastNumHits;
        //If this is the first guess or we missed last time, make a new list of neighbours
        if (! hitLast && this.neighbours.size() == 0) {
            this.lastGuess = alwaysRandomGuess();
        //If we hit something last time, go through the list of neighbours and guess
        } else if (hitLast) {
            for (int i=-1; i<=1; i++) {
                for (int j=-1; j<=1; j++) {
                    //We only want to check lattice neighbours, not diagonals.
                    if (Math.abs(i-j) % 2 == 0) {
                        continue;
                    }
                    if (this.lastGuess[0]+i < 0 || this.lastGuess[0]+i >= this.potentials.length) {
                        continue;
                    }
                    if (this.lastGuess[1]+j < 0 || this.lastGuess[1]+j >= this.potentials.length) {
                        continue;
                    }
                    this.neighbours.add(new int[] {this.lastGuess[0]+i, this.lastGuess[1]+j});
                }
            }
            int guessIndex = this.random.nextInt(this.neighbours.size());
            this.lastGuess = this.neighbours.get(guessIndex);
            this.neighbours.remove(lastGuess);
        } else {
            int guessIndex = this.random.nextInt(this.neighbours.size());
            lastGuess = this.neighbours.get(guessIndex);
            this.neighbours.remove(lastGuess);
        }
    }
    
    public static void main(String[] args) {
        Board b1 = new Board(4);
        Board b2 = new Board(4);
        Ship[] s1 = new Ship[1];
        Ship[] s2 = new Ship[1];
        Player p1 = new Player(b1, s1);
        AI p2 = new AI(b2, s2, p1);
        for (int i=0; i<15; i++) {
            int[] coord = p2.alwaysRandomGuess();
            System.out.println("My guess is: " + coord[0] + ", " + coord[1]);
        }
    }
}
