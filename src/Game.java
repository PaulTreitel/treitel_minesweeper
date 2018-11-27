
import java.util.Arrays;
import java.util.Collections;


public class Game {
    
    static final int ROWS = 10;
    static final int COLS = 10;
    
    static final int MINES = 20;
    
    private int[][] hiddenBoard;
    private int[][] gameBoard;
    
    // On the hidden board:
    // Any number between 0 and 8 denotes the neighbours.
    //  -1 denotes a Mine.
    //  -2 not marked (not known)
    static final int MINE = -1;
    static final int NOT_MARKED = -2;
    
    
    private int numOfTurns;
    private Player p;
    
    
    
    public Game(){
        hiddenBoard = new int[ROWS][COLS];
        gameBoard = new int[ROWS][COLS];
        
        p = new Player();
        numOfTurns = 0;
        
        for (int rr=0; rr<ROWS; ++rr)
            for (int cc=0; cc<COLS; ++cc)
                gameBoard[rr][cc] = NOT_MARKED;
                
    }
    
    public int getCell(int r, int c){
        //return gameBoard[r][c];
        return hiddenBoard[r][c];
    }
    
    
    public boolean play() {

        boolean steppedOnMine = false;
        boolean markedWrongMine = false;

        ++numOfTurns;
        p.planNextMove(gameBoard);
        int r = p.getMoveR();
        int c = p.getMoveC();
        int action = p.getMoveAction(); // -1-Mine, +1-safe

        System.out.println("" + numOfTurns + " :: Move guessed: (r"
                + r + "," + c + ") --> action=" + action);

        if (numOfTurns == 1) {
            populateBoard(r, c);
        }

        // action==1 safe
        if (action == 1 && hiddenBoard[r][c] == MINE) {
            steppedOnMine = true;
        }
        if (action == -1 && hiddenBoard[r][c] != MINE) {
            markedWrongMine = true;
        }

        // If all is Kosher, just mark here:
        gameBoard[r][c] = hiddenBoard[r][c];

        if (steppedOnMine) {
            // do something special
            System.out.println("Stepped on Mine. Boom!");
        }
        if (markedWrongMine) {
            // do something special
            System.out.println("Marked wrong Mine. Boom!");
        }

        return steppedOnMine || markedWrongMine || done();
    }

    public int getTurns(){
        return numOfTurns;
    }
    
    public boolean done(){
        return numOfTurns == ROWS*COLS;
    }
    
    // Populating the hidden board
    private void populateBoard(int r, int c){

        int clickedSpot = r*COLS + c;
        
        Integer[] a = new Integer[ROWS*COLS];
        for (int ii=0; ii<ROWS*COLS; ++ii)
            a[ii] = ii;
        
        Collections.shuffle(Arrays.asList(a));
        
        int ii=0; 
        int cnt = 0;
        while (cnt<MINES){
            int n = a[ii++];
            if (n==clickedSpot) continue;
            cnt++;
            
            int rr = n/COLS;
            int cc = n%COLS;
            hiddenBoard[rr][cc] = MINE;

            for (int kk=-1; kk<=1 ; ++kk){
                if (rr+kk<0 || rr+kk>=ROWS) continue;
                for (int ll=-1; ll<=1 ; ++ll){
                    if (cc+ll<0 || cc+ll>=COLS) continue;
                    if ( hiddenBoard[rr+kk][cc+ll]!=MINE )
                        hiddenBoard[rr+kk][cc+ll]++;
                    
                }
            }

            
        }
        
        System.out.println("Created board:");
        printBoard(hiddenBoard);
        
    }
    
    private void printBoard(int[][] a){
        for (int rr=0; rr<a.length ; ++rr){
            for (int cc=0; cc<a[0].length; ++cc){
                System.out.printf("%3d",a[rr][cc]);
            }
            System.out.println();
        }
    }
}

