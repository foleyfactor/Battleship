/*
 * AI Class: In charge of generating the moves for the AI in the Battleship game
 */

package battleship;

//Imports
import java.util.ArrayList;
import java.util.Random;

public class AI {
    
    //Class variables
    private Random random;
    private int[][] potentials;
    private int[] lastGuess;
    private ArrayList<int[]> neighbours;
    private int lastNumHits, numHits;
    private Board board;
    private String difficulty;
    
    //Class constructor
    public AI(Board b, String d) {
        //Create a new random object for random operations
        this.random = new Random();
        
        //Create the array for the AI that uses probability distribution
        this.potentials = new int[b.getBoardSize()][b.getBoardSize()];
        
        //Create the ArrayList for the AI that guesses around all squares
        //that turn out to be hits
        this.neighbours = new ArrayList();
        
        //Initialize the number of hits and previous number of hits to 0
        this.lastNumHits = 0;
        this.numHits = 0;
        
        //Set the AI's board and difficulty based on the constructor arguments
        this.board = b;
        this.difficulty = d;
    }
    
    public int[] alwaysRandomGuess() {
        int x, y;
        //Keep generating random coordinates until we get one that hasn't already
        //been guessed.
        do {
            //Randomly pick an x and y on the board
            x = this.random.nextInt(this.board.getBoardSize());
            y = this.random.nextInt(this.board.getBoardSize());
        //Rinse and repeat if the coordinates have already been guessed
        } while (this.board.isGuessed(x, y));

        //Return the randomly generated coordinates
        return new int[] {x,y};
    }
    
    //Will guess randomly until it hits something, then continues to guess neighbours
    //while it keeps hitting things.
    public int[] randomGuessUntilHit() {
        //Figure out whether or not the last guess was a hit
        boolean hitLast = this.numHits > this.lastNumHits;
        
        //If this is the first guess or we missed and have no other squares to guess,
        //make a new list of neighbours
        if (! hitLast && this.neighbours.isEmpty()) {
            this.lastGuess = alwaysRandomGuess();
            
        //Otherwise, we need to guess from the list of neighbours    
        } else {
            //Check if the last guess was a hit
            if (hitLast) {
                //Loop through all of the neighbours to the square we hit and 
                //add them to the ArrayList
                for (int i=-1; i<=1; i++) {
                    for (int j=-1; j<=1; j++) {
                        //We only want to check lattice neighbours, not diagonals.
                        if (Math.abs(i-j) % 2 == 0) {
                            continue;
                        }
                        
                        //Error checking (can't be out of array's bounds)
                        if (this.lastGuess[0]+i < 0 || this.lastGuess[0]+i >= this.board.getBoardSize()) {
                            continue;
                        }
                        if (this.lastGuess[1]+j < 0 || this.lastGuess[1]+j >= this.board.getBoardSize()) {
                            continue;
                        }
                        
                        //If the arraylist doesn't already have these coordinates
                        //and it isn't already guessed, add it to the neighbours
                        if (! this.containsPair(this.neighbours, this.lastGuess[0]+i, this.lastGuess[1]+j) && !this.board.isGuessed(this.lastGuess[0]+i, this.lastGuess[1]+j)) {
                            this.neighbours.add(new int[] {this.lastGuess[0]+i, this.lastGuess[1]+j});
                        }
                    }
                }
            }
            //Randomly pick from the list of neighbours
            int guessIndex = this.random.nextInt(this.neighbours.size());
            this.lastGuess = this.neighbours.get(guessIndex);
            
            //Remove the pair from the ArrayList because now it will have been
            //guessed
            this.neighbours.remove(lastGuess);
            
        }
        
        //Update the previous number of hits to be equal to the current amount
        this.lastNumHits = this.numHits;
        
        //Return the pair that is generated
        return this.lastGuess;
    }
    
    //Check's if a ship could be placed at a pair of coordinates (based on the 
    //coordinates that have already been guessed
    public boolean couldBePlaced(Ship s, int x, int y) {
        //Check the ship's orientation
        if (s.isVertical()) {
            
            //Loop through the vertical ship's coordinates
            for (int i=0; i<s.getYSize(); i++) {
                try {
                    //If this coordinate has already been guessed then the 
                    //ship can't be placed here
                    if (this.board.getGuesses()[y+i][x]) {
                        return false;
                    }
                } catch (Exception e) {
                    //Will catch an ArrayOutOfBoundsException (which would indicate
                    //that the ship would not fit on the board at the specified
                    //position), so return false
                    return false;
                }
            }
            return true;
        }
        
        //Loop through the vertical ship's coordinates
        for (int i=0; i<s.getXSize(); i++) {
            try {
                //If this coordinate has already been guessed then the 
                //ship can't be placed here
                if (this.board.getGuesses()[y][x+i]) {
                    return false;
                }
            } catch (Exception e) {
                //Will catch an ArrayOutOfBoundsException (which would indicate
                //that the ship would not fit on the board at the specified
                //position), so return false
                return false;
            }
        }
        //Otherwise, the ship can be placed here!
        return true;
    }
    
    //Method that "theoretically places" a ship for the probability distribution AI.
    //Increments the coordinates' potential value so that the AI can keep track
    //of how many ships could possibly be placed on the board at that position
    public void theoreticallyPlace(Ship s, int x, int y) {
        //Check the ship's orientation
        if (s.isVertical()) {
            //Loop through the vertical ship's theoretical position and add 1 to the
            //coordinates at which it would be placed
            for (int i=0; i<s.getYSize(); i++) {
                this.potentials[y+i][x] ++;
            }
        } else {
            //Loop through the horizontal ship's theoretical position and add 1 to the
            //coordinates at which it would be placed
            for (int i=0; i<s.getXSize(); i++) {
                this.potentials[y][x+i] ++;
            }
        }
    }
    
    //Uses the probability that there will be a ship at a specific square to
    //generate its guess
    public int[] probabilityDistributionGuess() {
        //Reset the list of potential candidates for guessing
        this.potentials = new int[this.board.getBoardSize()][this.board.getBoardSize()];
        
        //Loop through all of the squares on the board
        for (int i=0; i<this.board.getBoardSize(); i++) {
            for (int j=0; j<this.board.getBoardSize(); j++) {
                
                //Loop through all of the ships that are on the board
                for (Ship s : this.board.getPlacingShips()) {
                    
                    //If the ship is already sunk, skip it
                    if (s.isSunk()) continue;
                    
                    //If the ship could be placed here, theoretically place it there
                    if (this.couldBePlaced(s, i, j)) {
                        this.theoreticallyPlace(s, i, j);
                    }
                    
                    //If the ship (rotated 90 degrees) could be placed here, theoretically
                    //place the ship there
                    if (this.couldBePlaced(s.rotate(), i, j)) {
                        this.theoreticallyPlace(s.rotate(), i, j);
                    }
                    
                    //If the coordinate has already been guessed, and it was a hit
                    if (this.board.getGuesses()[i][j] && this.board.getShips()[i][j] != null) {
                        //Loop through all of the neighbours and add a bonus value
                        //to its current probability
                        for (int x=-1; x<=1; x++) {
                            for (int y=-1; y<=1; y++) {
                                
                                //We only want to check lattice neighbours, not diagonals.
                                if (Math.abs(x-y) % 2 == 0) {
                                    continue;
                                }
                                
                                //Error checking
                                if (i+x < 0 || i+x >= this.board.getBoardSize()) {
                                    continue;
                                }
                                if (j+y < 0 || j+y >= this.board.getBoardSize()) {
                                    continue;
                                }
                                
                                //Add the bonus value to the neighbour
                                if (!this.board.isGuessed(i+x, j+y)) {
                                    this.potentials[i+x][j+y] += 5;
                                }
                            }
                        }
                    }
                }
            }
        }
        //Printing the current distribution (for development)
        for (int[] i : this.potentials) {
            for (int j : i) {
                System.out.print(j + " ");
            }
            System.out.println("");
        }
        System.out.println("");
        
        //Return the coordinates that have the most potential for having a ship
        return getMaxIndex(this.potentials);
    }
    
    //Method that guesses for the AI, based on its difficulty
    public void guess() {
        int[] coords;
        //If the AI is on medium, use the random guesses until something is hit approach
        if (this.difficulty.equalsIgnoreCase("medium")) {
            coords = this.randomGuessUntilHit();
            
        //If the AI is on hard, use the probability distribution strategy
        } else if (this.difficulty.equalsIgnoreCase("hard")) {
            coords = this.probabilityDistributionGuess();
        
        //Otherwise, if the AI is on easy, just guess randomly
        } else {
            coords = this.alwaysRandomGuess();
        }
        
        //Guess the generated coordinates on the board and increase the number
        //hit if the guess is a hit
        if (this.board.guess(coords[0], coords[1])) {
            this.numHits++;
        }
    }
    
    //Method that randomly places the ships for the AI
    public void placeShipsRandomly() {
        
        //Loop through all of the ships that need to be placed
        for (Ship s : this.board.getPlacingShips()) {
            
            //Randomly decide if the ship needs to be rotated
            boolean rotated = this.random.nextBoolean();
            
            //Rotate the ship, if necessary
            Ship afterRotate;
            if (rotated) {
                afterRotate = s.rotate();
            } else {
                afterRotate = s;
            }
            
            int x, y;
            
            do {
                //Randomly generate x and y coordinates to place the ship at
                x = this.random.nextInt(this.board.getOBoard().getBoardSize());
                y = this.random.nextInt(this.board.getOBoard().getBoardSize());
            //Rinse and repeat if the ship can't be placed where we randomly chose
            } while (! this.board.getOBoard().canBePlaced(afterRotate, x, y));
            
            //Place the ship on the AI's opposing board
            this.board.getOBoard().placeShip(afterRotate, x, y);
        }
    }
    
    //Function that determines whether or not an ArrayList contains a specific
    //pair of values
    public static boolean containsPair(ArrayList<int[]> a, int x, int y) {
        //Loop through the int arrays in the ArrayList
        for (int[] i : a) {
            //If the x and y coordinates of the array match the specified ones, 
            //return true
            if (i[0] == x && i[1] == y) {
                return true;
            }
        }
        //If nothing matched, return false
        return false;
    }
    
    //Function that returns the maximum value's index in a 2D array
    public static int[] getMaxIndex(int[][] a) {
        
        //Initialize the current maximum to the minimum possible value
        int currMax = Integer.MIN_VALUE;
        
        //Initialize the current index to arbitrary values
        int[] currMaxIndex = new int[] {Integer.MIN_VALUE, Integer.MIN_VALUE};
        
        //Loop through all of the elements in the array
        for (int i=0; i<a.length; i++) {
            for (int j=0; j<a[0].length; j++) {
                //If the current element is bigger than the current maximum
                if (a[i][j] > currMax) {
                    //Update the index and current maximum value
                    currMaxIndex[0] = i;
                    currMaxIndex[1] = j;
                    currMax = a[i][j];
                }
            }
        }
        //Return the index of the largest element
        return currMaxIndex;
    }
    
    //Method called when the AI gets a hit; increments the number of hits by 1
    public void hit() {
        this.numHits++;
    }
    
    //Function that returns the number of hits that the AI has
    public int getNumHit() {
        return this.numHits;
    }
}
