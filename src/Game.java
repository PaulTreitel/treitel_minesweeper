
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;

public class Game {
    
    static Random rand = new Random() ;
        
    // See comment in Display
    static final int ROWS = 10;
    static final int COLS = 10;
    
    // 
    static final int MINES = 20 ;
    
    // board can be a class by itself etc.
    // BUT, we'll keep it VERY simple with two 2D arrays.    
    // one for the board itself, and one to represent hits/tries
    private int[][]     hiddenBoard ;
    private int[][]     gameBoard ;
    
    
    // I do not want to use enum, as want to allow simple values/numbers
    // There are more fancy ways to do it (enum and calling functions).
    // BUT, would be MUCH easier to get rolling like this.
    static final int MINE = -1;
    static final int NOT_MARKED = -2;    
    
    private Player p;
    private int numOfTurns;
    
    // one variable with various possibilities, or multiple variables
    private boolean steppedOnMine;
    private boolean markedWrongMine;

    public Game()
    {
        numOfTurns = 0;
        steppedOnMine = false;
        hiddenBoard = new int[ROWS][COLS];
        gameBoard   = new int[ROWS][COLS];

        p = new Player(ROWS, COLS, MINES);        
        
        // We will populateBoard AFTER the first click
        // gameBoard can be done here
        for (int ii=0; ii<ROWS;++ii)
            for(int jj=0; jj<COLS ; ++jj)
                gameBoard[ii][jj] = NOT_MARKED;

        //System.out.println("gameBoard Initialized");
        //printBoard(gameBoard);

        
    }
    
    public int getCell(int r, int c) {
        return gameBoard[r][c];
        //return hiddenBoard[r][c];
    }
    
    public boolean play() {

        boolean doneBoolean = false;
        
        numOfTurns++;
        p.planNextMove(gameBoard);
        int r = p.getMoveR();
        int c = p.getMoveC();
        int action = p.getMoveAction(); // -1-Mine, +1-Safe
        //System.out.println("" + numOfTurns + "::  Move guessed (" + r + "," + c + ") --> Action = " + action);

        if (numOfTurns == 1) {
            populateBoard(r, c);
        }

        if (action == 1 && hiddenBoard[r][c] == MINE) {
            steppedOnMine = true;
        }

        // unique for our AI game
        if (action == -1 && hiddenBoard[r][c] != MINE) {
            markedWrongMine = true;
        }

        // All Kosher if we got here
        gameBoard[r][c] = hiddenBoard[r][c];

        /* Let's skip this!

            // Just need to expose as relevant
            // This will give incentive to the AI to find '0's.
            boolean madeChange = true;
            
            // No need to work if this was a mine, or if the Value is not 0.
            if ( action == -1 || gameBoard[r][c]!=0) madeChange = false;
            while (madeChange){
                madeChange = false;
                for (int rr=0; rr<ROWS; ++rr){
                    for (int cc=0; cc<COLS; ++cc){
            
            QQQQ Need to correct here.
                        int val = 0;
                        for (int ii=-1; ii<=1; ii++){
                            if (rr+ii<0 ||  rr+ii>=ROWS) continue;
                            for (int ll=-1; ll<=1; ll++){
                                if (cc+ll<0 ||  cc+ll>=COLS) continue;
                                if (ii==0 && ll==0) continue;
                                val +=  (hiddenBoard[rr+ii][cc+ll]==MINE) ? 1 : 0 ;
                            }
                        }
     
                    }
                }
            }
         */
        //printBoard(gameBoard);
        if (steppedOnMine) {
            // do something special
            System.out.println("Stepped on a mine. You're out!");
            doneBoolean = true;
        }
        if (markedWrongMine) {
            // do something special
            System.out.println("Wrong marking of a mine. You're out!");
            doneBoolean = true;
        }

        return doneBoolean || done() ;
    }
    
    private boolean done()
    {
        // in our mode, you need to clic on ALL!!
        return numOfTurns == ROWS*COLS ;
/*
        for (int rr=0; rr<ROWS; ++rr)                     // fill it with empty
            for (int cc=0; cc<COLS; ++cc)
//                if (hiddenBoard[rr][cc] !=EMPTY && gameBoard[rr][cc] !=HIT)
                    return false;
        return true;
*/
    }
    
    public int getTurns()
    {
        return numOfTurns;
    }
    
    
    // populating the hidden board
    private void populateBoard(int rClicked, int cClicked)
    {
        /* 
        Put Mines totally random, as long as it's not on the clicked spot.
        We'll do it by shuffeling an array, and picking as many locations as we need.
        Just skipping the clicked position
        */
        
        // on why you have to use Integer
        // https://stackoverflow.com/questions/3981420/why-does-collections-shuffle-fail-for-my-array
        int clickedSpot = rClicked*COLS + cClicked;
        Integer[] a = new Integer[ROWS*COLS];
        for (int ii=0; ii<ROWS*COLS ; ++ii)
            a[ii] = ii;
        Collections.shuffle(Arrays.asList(a)) ;
        int ii = 0;
        int m = 0;
        //for (int ii=0; ii<MINES; ++ii){ 
        while (m < MINES) {
            int n = a[ii++];
            if (n==clickedSpot)  continue ;
            int r = n / COLS;
            int c = n % COLS;
            if (Math.abs(rClicked-r) <= 1 && Math.abs(cClicked-c) <= 1)
                continue;
            hiddenBoard[r][c] = MINE;
            m++;
            // Fill in the neighbours-value
            for (int kk=-1; kk<=1; kk++){
                if (r+kk<0 ||  r+kk>=ROWS ) continue;
                for (int ll=-1; ll<=1; ll++){
                    if (c+ll<0 ||  c+ll>=COLS) continue;
                    if (kk==0 && ll==0) continue;
                    if (hiddenBoard[r+kk][c+ll] !=MINE)
                        hiddenBoard[r+kk][c+ll]++;
                }
            }
        
        
        }
        
        // populate array with the neighbours-count
        /*
        we can actually doing it now WITHIN the creation loop
        
        for (int rr=0; rr<ROWS; ++rr){                     
            for (int cc=0; cc<COLS; ++cc){
                if (hiddenBoard[rr][cc]==MINE) continue;
                int val = 0;
                for (int ii=-1; ii<=1; ii++){
                    if (rr+ii<0 ||  rr+ii>=ROWS) continue;
                    for (int ll=-1; ll<=1; ll++){
                        if (cc+ll<0 ||  cc+ll>=COLS) continue;
                        if (ii==0 && ll==0) continue;
                        val +=  (hiddenBoard[rr+ii][cc+ll]==MINE) ? 1 : 0 ;
                    }
                }
                hiddenBoard[rr][cc] = val;
            }
        }
        */
        
        //System.out.println("hiddenBoard Initialized");
        //printBoard(hiddenBoard);

    }
    
    public void printBoard(int[][] a)
    {
        System.out.println("Printing board!!");
        for (int rr=0; rr<ROWS; ++rr)
        {
            for (int cc=0; cc<COLS; ++cc)
            {
                System.out.printf("%3d ", a[rr][cc]);
            }
            System.out.println();
        }
        
    }
    
}