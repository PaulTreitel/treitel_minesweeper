
import java.util.Random;


public class Player {

    static Random rand = new Random();
    private final int ROWS;
    private final int COLS;
    private final int DEPTH = 5;
    private final int[][] board;
    private final int mines;
    private double[][] odds;
    private boolean isFirst;
    private int[] move;
    private int[] baseTile;
    
    public Player(int r, int c, int m) {
        this.baseTile = new int[]{-1, -1, 0};
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
        for (int i = 0; i < ROWS; i++)
            System.arraycopy(b[i], 0, board[i], 0, COLS);
        
        if (isFirst) {
            move = new int[] {5, 5, 1};
            isFirst = !isFirst;
            return;
        }
        
        findBaseTile(board);
        if (baseTile[0] != -1) {
            openNeighbor(board);
            if (move[0] != -1)
                return;
        }
        
        predictDeterministic();
        baseTile = new int[] {-1, -1, 0};
        if (move[0] != -1)
            return;
        
        odds = new double[ROWS][COLS];
        calculateOdds(board);
        getBestOdds();
    }
    
    private void predictDeterministic() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (hasNumNeighbor(r, c) && board[r][c] == -2) {
                    for (int act = -1; act < 3; act +=2) {
                        if (attemptPrediction(r, c, act) == -1) {
                            move[0] = r;
                            move[1] = c;
                            move[2] = act == -1 ? 1 : -1;
                            return;
                        }
                        for (int i = 0; i < 3; i++)
                            move[i] = -1;
                        baseTile = new int[] {-1, -1, 0};
                    }
                }
            }
        }
    }
    
    private int attemptPrediction(int r, int c, int act) {
        int[][] b = new int[ROWS][COLS];
        for (int i = 0; i < ROWS; i++)
            System.arraycopy(board[i], 0, b[i], 0, COLS);
        b[r][c] = (act == -1) ? -1 : 10;
        
        for (int turns = 0; turns < DEPTH; turns++) {
            for (int i = 0; i < 3; i++)
                move[i] = -1;
            findBaseTile(b);
            if (baseTile[0] != -1) {
                openNeighbor(b);
                if (move[0] != -1)
                    b[move[0]][move[1]] = (move[2] == -1) ? -1 : 10;
            } else
                return 0;
            if (!validBoard(b))
                return -1;
        }
        return 0;
    }
    
    private boolean validBoard(int[][] b) {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                if (completable(b, r, c) == -2)
                    return false;
        return true;
    }
    
    private boolean hasNumNeighbor(int r, int c) {
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++) {
                boolean rowBounds = r+i < ROWS && r+i >= 0;
                boolean colBounds = c+j < COLS && c+j >= 0;
                if (rowBounds && colBounds && board[r+i][c+j] != -1 && board[r+i][c+j] != -2)
                    return true;
            }
        return false;
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
                } else if (maxx != -1 && odds[r][c] > odds[maxx][maxy]) {
                    maxx = r;
                    maxy = c;
                }
            }
        }
        
        if (maxx == -1) {
            setMoveUnopened();
            return;
        }
        
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (!invert && odds[r][c] != 0 && (1-odds[r][c]) > odds[maxx][maxy]) {
                    maxx = r;
                    maxy = c;
                    invert = true;
                } else if (invert && odds[r][c] != 0 && (1-odds[r][c]) > (1-odds[maxx][maxy])) {
                    maxx = r;
                    maxy = c;
                }
            }
        }
        if (maxx == -1) {
            setMoveUnopened();
            return;
        }
        move[0] = maxx;
        move[1] = maxy;
        move[2] = invert ? 1 : -1;
    }
    
    private void setMoveUnopened() {
        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLS; j++)
                if(board[i][j] == -2) {
                    move[0] = i;
                    move[1] = j;
                    move[2] = getBaseMineChance() == 1 ? -1 : 1;
                    return;
                }
    }
    
    private void calculateOdds(int[][] b) {
        double minechance = getBaseMineChance();
        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] != -2)
                    odds[i][j] = 0;
                else {
                    double chanceIfKnown = calculateKnown(b, i, j);
                    odds[i][j] = chanceIfKnown > 0 ? chanceIfKnown : minechance;
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
    
    private double calculateKnown(int[][] b, int r, int c) {
        double topchance = 0;
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++) {
                boolean rowBounds = r+i < ROWS && r+i >= 0;
                boolean colBounds = c+j < COLS && c+j >= 0;
                if (rowBounds && colBounds && board[r+i][c+j] != -2 && board[r+i][c+j] != -1) {
                    double chance = getNeighborChance(b, r+i, c+j);
                    topchance = chance > topchance ? chance : topchance;
                }
            }
        return topchance;
    }
    
    private double getNeighborChance(int[][] b, int r, int c) {
        int minesNeeded = board[r][c] - getNeighbors(b, r, c, -1);
        return ((double) minesNeeded) / getNeighbors(b, r, c, -2);
    }
    
    private void openNeighbor(int[][] b) {
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++) {
                boolean rowBounds = baseTile[0]+i < ROWS && baseTile[0]+i >= 0;
                boolean colBounds = baseTile[1]+j < COLS && baseTile[1]+j >= 0;
                if (rowBounds && colBounds && b[baseTile[0]+i][baseTile[1]+j] == -2) {
                    move = new int[] {baseTile[0]+i, baseTile[1]+j, baseTile[2]};
                    return;
                }
            }
    }
    
    private void findBaseTile(int[][] b) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (b[i][j] >= 0 && b[i][j] != 10) {
                    if (completable(b, i, j) == 1) {
                        baseTile = new int[] {i, j, 1};
                        return;
                    } else if (completable(b, i, j) == -1) {
                        baseTile = new int[] {i, j, -1};
                        return;
                    }
                }
            }
        }
    }
    
    /**
     * @return:
     * 1  - open all neighbors 
     * -1 - all neighbors are mines 
     * 0  - continue search
     * -2 - invalid board
     */
    private int completable(int[][] b, int r, int c) {
        if (b[r][c] == 10 || b[r][c] < 0)
            return 0;
        int neighborMines = getNeighbors(b,r, c, -1);
        int unknownNeighbors = getNeighbors(b, r, c, -2);
        if (unknownNeighbors == 0)
            return 0;
        int minesNeeded = b[r][c] - neighborMines;
        if (minesNeeded == 0)
            return 1;
        else if (minesNeeded == unknownNeighbors)
            return -1;
        else if (minesNeeded < 0 || minesNeeded > unknownNeighbors)
            return -2;
        return 0;
    }
    
    private int getNeighbors(int[][] b, int r, int c, int val) {
        int neighbors = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (r+i < ROWS && r+i >= 0 && c+j < COLS && c+j >= 0) {
                    if (b[r+i][c+j] == val)
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