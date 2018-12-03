
import java.util.Random;


public class Player {

    static Random rand = new Random();
    private final int ROWS;
    private final int COLS;
    private int[][] board;
    private final int mines;
    private double[][] odds;
    private boolean isFirst;
    private int[] move;
    private int[] baseTile = {-1, -1, 0};
    
    public Player(int r, int c, int m) {
        ROWS = r;
        COLS = c;
        board = new int[r][c];
        mines = m;
        isFirst = true;
        move = new int[3];
        odds = new double[r][c];
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
        
        odds = new double[ROWS][COLS];
        calculateOdds();
        
        System.out.println("Randomize!");
        getBestOdds();
    }
    
    private void getBestOdds() {
        int maxx = -1;
        int maxy = -1;
        boolean invert = false;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if ((maxx == -1 && odds[r][c] != 0)) {
                    maxx = r;
                    maxy = c;
                    invert = 1-odds[r][c] > odds[r][c];
                } else if (odds[r][c] != 0) {
                    double maxval;
                    if (invert)
                        maxval = 1-odds[maxx][maxy];
                    else
                        maxval = odds[maxx][maxy];
                    boolean higher = odds[r][c] > maxval;
                    boolean invertHigher = (1-odds[r][c]) > maxval;
                    if (higher || invertHigher) {
                        maxx = r;
                        maxy = c;
                        invert = invertHigher;
                    }
                }
            }
        }
        move[0] = maxx;
        move[1] = maxy;
        move[2] = invert ? 1 : -1;
    }
    
    private void calculateOdds() {
        double minechance = getBaseMineChance();
        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] != -2)
                    odds[i][j] = 0;
                else {
                    double chanceIfKnown = calculateKnown(i, j);
                    odds[i][j] = chanceIfKnown > minechance ? chanceIfKnown: minechance;
                }
            }
    }
    
    private double getBaseMineChance() {
        int tilesRemaining = 0;
        int minesFound = 0;
        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] == -2)
                    tilesRemaining++;
                else if (board[i][j] == -1)
                    minesFound++;
            }
        return ((double) (mines-minesFound)) / tilesRemaining;
    }
    
    private double calculateKnown(int r, int c) {
        double topchance = 0;
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++) {
                boolean rowBounds = r+i < ROWS && r+i >= 0;
                boolean colBounds = c+j < COLS && c+j >= 0;
                if (rowBounds && colBounds && board[r+i][c+j] != -2 && board[r+i][c+j] != -1) {
                    double chance = getNeighborChance(r+i, c+j);
                    topchance = chance > topchance ? chance : topchance;
                }
            }
        return topchance;
    }
    
    private double getNeighborChance(int r, int c) {
        int minesNeeded = board[r][c] - getNeighbors(r, c, -1);
        return ((double) minesNeeded) / getNeighbors(r, c, -2);
    }
    
    private void openNeighbor() {
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++) {
                boolean rowBounds = baseTile[0]+i < ROWS && baseTile[0]+i >= 0;
                boolean colBounds = baseTile[1]+j < COLS && baseTile[1]+j >= 0;
                if (rowBounds && colBounds && board[baseTile[0]+i][baseTile[1]+j] == -2) {
                    move = new int[] {baseTile[0]+i, baseTile[1]+j, baseTile[2]};
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
    
    /**
     * @return: 1 - open all neighbors ; -1 - all neighbors are mines ; 0 - continue search
     */
    private int completable(int r, int c) {
        int neighborMines = getNeighbors(r, c, -1);
        int unknownNeighbors = getNeighbors(r, c, -2);
        if (unknownNeighbors == 0)
            return 0;
        int minesNeeded = board[r][c] - neighborMines;
        if (minesNeeded == 0)
            return 1;
        if (minesNeeded == unknownNeighbors)
            return -1;
        return 0;
    }
    
    private int getNeighbors(int r, int c, int val) {
        int neighbors = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (r+i < ROWS && r+i >= 0 && c+j < COLS && c+j >= 0) {
                    if (board[r+i][c+j] == val)
                        neighbors++;
                }
            }
        }
        return neighbors;
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