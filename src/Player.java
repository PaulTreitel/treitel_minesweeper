
import java.util.Random;


public class Player {

    static Random rand = new Random();
    static final int ROWS = 10;
    static final int COLS = 10;

    static int[][] board = new int[ROWS][COLS];
    private boolean isFirst;
    private int[] move;
    private int[] baseTile = {-1, -1};
    
    public Player() {
        isFirst = true;
        move = new int[3];
    }

    // On the hidden board:
    // Any number between 0 and 8 denotes the neighbours.
    //  -1 denotes a Mine.
    //  -2 not marked (not known)
    public void planNextMove(int[][] b){
        System.out.println("Thinking...");
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
                    if (completable(i, j) == 0) {
                        //ToDo
                    } else if (completable(i, j) == 1) {
                        //ToDo
                    }
                }
            }
        }
    }
    
    private int completable(int r, int c) {
        int neighborMines = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (board[i][j] == -1)
                    neighborMines++;
            }
        }
        int minesNeeded = board[r][c] - neighborMines;
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
