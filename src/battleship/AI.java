/*
 * AI Class
 */

package battleship;

import java.util.ArrayList;
import java.util.Random;

public class AI {
    
    private Random random;
    private int[][] potentials;
    private int[] lastGuess;
    private ArrayList<int[]> neighbours;
    private int lastNumHits, numHits;
    private Board board;
    private String difficulty;
    
    public AI(Board b, String d) {
        this.random = new Random();
        this.potentials = new int[b.getBoardSize()][b.getBoardSize()];
        this.lastNumHits = 0;
        this.numHits = 0;
        this.neighbours = new ArrayList();
        this.board = b;
        this.difficulty = d;
    }
    
    public int[] alwaysRandomGuess() {
        int x, y;
        //Keep generating random coordinates until we get one that hasn't already
        //been guessed.
        do {
            //Change these to board.getSize()
            x = this.random.nextInt(this.board.getBoardSize());
            y = this.random.nextInt(this.board.getBoardSize());
        } while (this.board.isGuessed(x, y));

        return new int[] {x,y};
    }
    
    //Will guess randomly until it hits something, then continues to guess neighbours
    //while it keeps hitting things.
    public int[] randomGuessUntilHit() {
        boolean hitLast = this.numHits > this.lastNumHits;
        System.out.println(hitLast);
        //If this is the first guess or we missed last time, make a new list of neighbours
        if (! hitLast && this.neighbours.isEmpty()) {
            this.lastGuess = alwaysRandomGuess();
        //If we hit something last time, add all of the neighbours to the possibilities
        //Guess one of the neighbours
        } else {
            if (hitLast) {
                for (int i=-1; i<=1; i++) {
                    for (int j=-1; j<=1; j++) {
                        //We only want to check lattice neighbours, not diagonals.
                        if (Math.abs(i-j) % 2 == 0) {
                            continue;
                        }
                        if (this.lastGuess[0]+i < 0 || this.lastGuess[0]+i >= this.board.getBoardSize()) {
                            continue;
                        }
                        if (this.lastGuess[1]+j < 0 || this.lastGuess[1]+j >= this.board.getBoardSize()) {
                            continue;
                        }
                        if (! this.containsPair(this.neighbours, this.lastGuess[0]+i, this.lastGuess[1]+j) && !this.board.isGuessed(this.lastGuess[0]+i, this.lastGuess[1]+j)) {
                            System.out.println((this.lastGuess[0]+i) + " " + (this.lastGuess[1]+j));
                            this.neighbours.add(new int[] {this.lastGuess[0]+i, this.lastGuess[1]+j});
                        }
                    }
                }
            }
            int guessIndex = this.random.nextInt(this.neighbours.size());
            this.lastGuess = this.neighbours.get(guessIndex);
            this.neighbours.remove(lastGuess);
            
        }
        this.lastNumHits = this.numHits;
        //this.board.guess(this.lastGuess[0], this.lastGuess[1]);
        
        return this.lastGuess;
    }
    
    public boolean containsPair(ArrayList<int[]> a, int x, int y) {
        for (int[] i : a) {
            if (i[0] == x && i[1] == y) {
                return true;
            }
        }
        return false;
    }
    
    public void hit() {
        this.numHits++;
    }
    
    public int getNumHit() {
        return this.numHits;
    }
    
    public void placeShipsRandomly() {
        for (Ship s : this.board.getPlacingShips()) {
            int x, y;
            do {
                x = this.random.nextInt(this.board.getOBoard().getBoardSize());
                y = this.random.nextInt(this.board.getOBoard().getBoardSize());
            } while (! this.board.getOBoard().canBePlaced(s, x, y));
            this.board.getOBoard().placeShip(s, x, y);
        }
    }
    
    public void guess() {
        int[] coords;
        if (this.difficulty.equalsIgnoreCase("medium")) {
            coords = this.randomGuessUntilHit();
        } else {
            coords = this.alwaysRandomGuess();
        }
        if (this.board.guess(coords[0], coords[1])) {
            this.numHits++;
        }
    }
}
