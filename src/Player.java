
import java.util.Random;


public class Player {

    static Random rand = new Random();
    static int ROWS = 10;
    static int COLS = 10;
    static int[][] board = new int[ROWS][COLS];
    
    private int mines;
    private double minechance;
    private double numchance;
    private double[][] odds = new double[ROWS][COLS];
    private boolean isFirst;
    private int[] move;
    private int[] baseTile = {-1, -1, 0};
    
    public Player(int r, int c, int m) {
        ROWS = r;
        COLS = c;
        mines = m;
        isFirst = true;
        move = new int[3];
    }

    // On the hidden board:
    // Any number between 0 and 8 denotes the neighbours.
    //  -1 denotes a Mine.
    //  -2 not marked (not known)
    public void planNextMove(int[][] b) {
        for (int i = 0; i < 3; i++)
            move[i] = -1;
        board = b;
        if (isFirst) {
            chooseRandom(0, 9, 0, 9);
            isFirst = !isFirst;
            return;
        }
        findBaseTile();
        if (baseTile[0] != -1) {
            openNeighbor();
            if (move[0] != -1)
                return;
        }
        
        calculateOdds();
        
        do {
            chooseRandom(0, ROWS, 0, COLS);
        } while (board[move[0]][move[1]] != -2);
    }
    
    private void calculateOdds() {
        //boolean mineUnlikely = mineProbability();
        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] != -2)
                    odds[i][j] = 0;
                else if (knownNeighbor(i, j))
                    odds[i][j] = calculateKnown();
                else
                    odds[i][j] = minechance;
            }
    }
    
    private boolean knownNeighbor(int r, int c) {
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++) {
                boolean rowBounds = r+i < ROWS && r+i >= 0;
                boolean colBounds = c+j < COLS && c+j >= 0;
                if (rowBounds && colBounds && board[r+i][c+j] != -2)
                    return true;
            }
        return false;
    }
    
    /*
    private boolean mineProbability() {
        int minesFound = 0;
        int revealed = 0;
        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] == -1)
                    minesFound++;
                if (board[i][j] != -2)
                    revealed++;
            }
        minechance = ((double) mines-minesFound)/(ROWS*COLS - revealed);
        numchance = 1 - minechance;
        return numchance > minechance;
    }
    */
    
    private void openNeighbor() {
        for (int rmod = -1; rmod < 2; rmod++)
            for (int cmod = -1; cmod < 2; cmod++) {
                boolean rowBounds = baseTile[0]+rmod < ROWS && baseTile[0]+rmod >= 0;
                boolean colBounds = baseTile[1]+cmod < COLS && baseTile[1]+cmod >= 0;
                if (rowBounds && colBounds && board[baseTile[0]+rmod][baseTile[1]+cmod] == -2) {
                    move = new int[] {baseTile[0]+rmod, baseTile[1]+cmod, baseTile[2]};
                    return;
                }
            }
    }
    
    private void chooseRandom(int lowr, int highr, int lowc, int highc) {
        int r = (int) ((highr-lowr)*Math.random() + lowr);
        int c = (int) ((highc-lowc)*Math.random() + lowc);
        move[0] = r;
        move[1] = c;
        move[2] = 1;
    }
    
    private void findBaseTile() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] >= 0) {
                    if (completable(i, j) == 1) {
                        baseTile = new int[] {i, j, 1};
                        return;
                    } else if (completable(i, j) == -1) {
                        baseTile = new int[] {i, j, -1};
                        return;
                    }
                }
            }
        }
    }
    
    /*
    return:
    1 = open all unopened neighbors
    -1 = mark all unopened neighbors as mines
    0 = do nothing (tile not determinable by this method)
    */
    private int completable(int r, int c) {
        int neighborMines = 0;
        int unknownNeighbors = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (r+i < ROWS && r+i >= 0 && c+j < COLS && c+j >= 0) {
                    if (board[r+i][c+j] == -1)
                        neighborMines++;
                    if (board[r+i][c+j] == -2)
                        unknownNeighbors++;
                }
            }
        }
        if (unknownNeighbors == 0)
            return 0;
        int minesNeeded = board[r][c] - neighborMines;
        if (minesNeeded == 0)
            return 1;
        if (minesNeeded == unknownNeighbors)
            return -1;
        return 0;
    }
    
    
    public int getMoveR(){
        return move[0];
    }
    
    public int getMoveC(){
        return move[1];
    }
    
    /**
     * 
     * @return -1 - MINE  ; +1 - Safe
     */
    public int getMoveAction(){
        return move[2];
    }


    


    
}
