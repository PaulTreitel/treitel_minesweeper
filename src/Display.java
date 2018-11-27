
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;




public class Display extends JComponent{
    
    private int frameWidth, frameHeight;
    
    private Game game;
    
    private boolean loopMode;
    
    public Display(int w, int h){
        frameWidth = w;
        frameHeight = h;
        
        putButtons();
        init();
    }
    
    
    private void init() {
        System.out.println("In init");
        // new game
        game = new Game();
        loopMode = false;
    }
    
    public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        
        drawCells(g2);
        drawGrid(g2);
        drawButtons(g2);
        
    }
    
    
    
    
    final int CELL_TOP_X = 0;
    final int CELL_TOP_Y = 0;
    final int CELL_SIDE_PIXELS = 50;
    final int COLS = 10;
    final int ROWS = 10;
    
    final Color COLOR_GRID = Color.WHITE;
    final Color COLOR_EMPTY = Color.BLUE;
    final Color COLOR_MINE = Color.RED;
    final Color COLOR_VALUE = Color.GREEN;
    

    
    private void drawGrid(Graphics2D g2){
        
        g2.setColor(COLOR_GRID);
        
        // horizontal lines
        int x1 = CELL_TOP_X;
        int x2 = x1 + COLS * CELL_SIDE_PIXELS;
        int y1, y2;
    
        
        for (int rr=0; rr<=ROWS ; ++rr){
            y1 = CELL_TOP_Y + rr*CELL_SIDE_PIXELS;
            g2.drawLine(x1, y1, x2, y1);
        }
        
        // vertical lines
        y1 = CELL_TOP_Y;
        y2 = y1 + ROWS * CELL_SIDE_PIXELS;    
        
        for (int cc=0; cc<=COLS ; ++cc){
            x1 = CELL_TOP_X + cc*CELL_SIDE_PIXELS;
            g2.drawLine(x1, y1, x1, y2);
        }
        
        
        
    }
    
    
    private void drawCells(Graphics2D g2) {
        for (int rr=0 ; rr<ROWS ; ++rr){
            int ytop = CELL_TOP_Y + rr*CELL_SIDE_PIXELS;
            int ybot = ytop + CELL_SIDE_PIXELS;
            
            for(int cc=0; cc<COLS ; ++cc){
                int xleft = CELL_TOP_X + cc*CELL_SIDE_PIXELS;
                int xright = xleft + CELL_SIDE_PIXELS;
                
                
                Color c;
              
                // ToDO: Call game
                int val = game.getCell(rr,cc);
                switch (val){
                    case 8:
                    case 7:
                    case 6:
                    case 5:
                    case 4:
                    case 3:
                    case 2:
                    case 1:
                    case 0:
                        c = COLOR_VALUE;
                        g2.setColor(c);
                        g2.fillRect(xleft, ytop, CELL_SIDE_PIXELS, CELL_SIDE_PIXELS);
                        g2.setColor(Color.WHITE);
                        g2.drawString(""+val,xleft+10,ytop+10);
                        
                        break;
                        
                    case -1:
                        c = COLOR_MINE;
                        g2.setColor(c);
                        g2.fillRect(xleft, ytop, CELL_SIDE_PIXELS, CELL_SIDE_PIXELS);
                        break;
                    case -2:
                    default:
                        c = COLOR_EMPTY;
                        g2.setColor(c);
                        g2.fillRect(xleft, ytop, CELL_SIDE_PIXELS, CELL_SIDE_PIXELS);
                        break;
                }
                
            
            }
            
        }
        
    }
    
    private void drawButtons(Graphics2D g2) {
    
    }
    
    
    private final JButton resetButton = new JButton();
    private final JButton stepButton = new JButton();
    private final JButton runButton = new JButton();
    private final JButton avgButton = new JButton();
    
    private final Rectangle RESET_RECT = new Rectangle(50,500,50,40);
    private final Rectangle STEP_RECT = new Rectangle(50,530,50,40);
    private final Rectangle RUN_RECT = new Rectangle(130,530,50,40);
    private final Rectangle AVG_RECT = new Rectangle(210,530,100,40);
    private final Rectangle AVG_LABEL_RECT = new Rectangle(320,530,100,40);
    
    
    private JLabel avgLabel = new JLabel();
    private JLabel stepsLabel = new JLabel();
    
    private void putButtons() {
       
       resetButton.setText("Reset");
       resetButton.setBounds(RESET_RECT);
       class ResetListener implements ActionListener {
           public void actionPerformed(ActionEvent e) {
               init();
               repaint();
           }
       }
       resetButton.addActionListener(new ResetListener());
       resetButton.setVisible(true);
       add(resetButton);  // uses the Jcomponent add function
        
 
       
       stepButton.setText("Step");
       stepButton.setBounds(STEP_RECT);
       class StepListener implements ActionListener {
           public void actionPerformed(ActionEvent e) {
               loopMode = false;
               game.play();
               repaint();
           }
       }
       stepButton.addActionListener(new StepListener());
       stepButton.setVisible(true);
       add(stepButton);  // uses the Jcomponent add function
 

       runButton.setText("Run");
       runButton.setBounds(RUN_RECT);
       class RunListener implements ActionListener {
           public void actionPerformed(ActionEvent e) {
               
               
               
               repaint();
           }
       }
       runButton.addActionListener(new RunListener());
       runButton.setVisible(true);
       add(runButton);  // uses the Jcomponent add function
 
       
       
       
       avgLabel.setBounds(AVG_LABEL_RECT);
       avgLabel.setText("---");
       avgLabel.setVisible(true);
       add(avgLabel);
       
       
       avgButton.setText("AVG");
       avgButton.setBounds(AVG_RECT);
       class AvgListener implements ActionListener {
           public void actionPerformed(ActionEvent e) {
               final int NUM_OF_AVGS = 10;
               double sumTurns = 0;
               
               for (int ii=0; ii<NUM_OF_AVGS ; ++ii){
                   Game gg = new Game();
                   while (!gg.play() );
                   sumTurns += gg.getTurns();
               }
               
               System.out.println("sumTurns=" + sumTurns);
               avgLabel.setText(Integer.toString((int)(sumTurns/NUM_OF_AVGS)));
               
               repaint();
           }
       }
       avgButton.addActionListener(new AvgListener());
       avgButton.setVisible(true);
       add(avgButton);  // uses the Jcomponent add function
 
       


       
    }
    
    
    
}
