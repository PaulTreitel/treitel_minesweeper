
import java.util.Random;


public class Player {

    static Random rand = new Random();
    static final int ROWS = 10;
    static final int COLS = 10;

    static int[][] board = new int[ROWS][COLS];
    
    public Player() {
        
    }

    // On the hidden board:
    // Any number between 0 and 8 denotes the neighbours.
    //  -1 denotes a Mine.
    //  -2 not marked (not known)
    public void planNextMove(int[][] b){
        
        // copy b to board
        System.out.println("Thinking...");
    }
    
    public int getMoveR(){
        return (int) rand.nextInt(ROWS);
    }
    
    public int getMoveC(){
        return (int) rand.nextInt(COLS);
    }

    
    /**
     * 
     * @return -1 - MINE  ; +1 - Safe
     */
    public int getMoveAction(){
        return 1;
    }


    


    
}
