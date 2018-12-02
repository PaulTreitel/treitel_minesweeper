
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;
/* 
import javax.swing.JSlider;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
 */

import java.awt.Color;
import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import javax.swing.ImageIcon;

import java.util.*;

/*
 * 
 * 
 *   We will keep things NOT too flexible here,
 *   so as to make the design and code simpler.
 * 
 *  Things will be in fixed-location on screen.
 *  This is called Absolute-positioning (as compared to using layouts)
 *  http://docs.oracle.com/javase/tutorial/uiswing/layout/none.html
 *  All the definition are grouped below
 *  The graphic tries to follow the look of:
 *  http://www.bitstorm.org/gameoflife/ 
 * 
 *   Display is responsible for drawing the game.
 *   Display :
 *   	Creates and manages interactions with the game object
 *      Creates and manages the items display (getting the Frame from main)
 *      Handles all non-button events. That means, getting mouse clicks, 
 *      determining which cell they hit, and managing response.
 * 
 * 
 */
//@SuppressWarnings("serial")
public class Display extends JComponent {

    private int frameWidth, frameHeight;
    private boolean loopMode;

    private final static long STEP_TIME_MILLIS = 150; // time in milliseconds

    // We can define this in one place as game-rules, and use it here
    // and in game.java. But let's just keep things a little bit more
    // compact.
    private final static int ROWS = 10;
    private final static int COLS = 10;

    private final JButton resetButton = new JButton();
    private final JButton stepButton = new JButton();
    private final JButton runButton = new JButton();
    private final JButton avgButton = new JButton();
    private final JLabel avgLabel = new JLabel();

    private final JLabel stepsLabel = new JLabel();

    // Unlike my usual habit, i pushed many of the constants defs to the very bottom.
    // Too many graphic stuff...
    private final static int CELL_SIDE_PIXELS = 50;
    private final static int CELL_TOP_X = 50;
    private final static int CELL_TOP_Y = 50;

    private final static Color COLOR_EMPTY = Color.CYAN;
    private final static Color COLOR_GRID = Color.WHITE;
    private final static Color COLOR_VALUE = Color.GREEN;
    private final static Color COLOR_MINE = Color.RED;
    private final static Color COLOR_FONT_VAL = Color.WHITE;


    static private ArrayList<JLabel> mineLabels = new ArrayList<>();

    // Graphics locations
    // Should be as a function of frameWidth and frameHeight
    // and created in the constructor.
    // Let's save trouble for now and just put these based on:
    // (Width, Height) = (600,800)
    private final static Rectangle RESET_RECT = new Rectangle(50, 550, 75, 40);
    private final static Rectangle STEP_RECT = new Rectangle(50, 600, 75, 40);
    private final static Rectangle RUN_RECT = new Rectangle(130, 600, 75, 40);
    private final static Rectangle AVG_RECT = new Rectangle(210, 600, 100, 40);
    private final static Rectangle AVG_LABEL_RECT = new Rectangle(320, 600, 40, 40);
    private final static Rectangle STEPS_RECT = new Rectangle(500, 600, 40, 40);

    
    String imageFileName = "Mine50x50.png";
    private ImageIcon image = new ImageIcon(imageFileName);
    
    
    private Game game;

    public Display(int frameWidth, int frameHeight) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;

        putButtons();
        init();
    }

    private void init() {
        loopMode = false;
        //System.out.println("in init: size = "+ mineLabels.size());
        for (int ii=0; ii<mineLabels.size(); ++ii) {
            remove(mineLabels.remove(0));
            validate();
            repaint();
        }
        game = new Game();
        
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        drawCells(g2);
        drawGrid(g2);
        drawButtons();

        if (loopMode) {
            try {
                Thread.sleep(STEP_TIME_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean done = game.play();
            if (done) loopMode = false;
            repaint();
        }

    }

    private void drawGrid(Graphics2D g2) {
        g2.setColor(COLOR_GRID);

        int x1 = CELL_TOP_X;
        int x2 = CELL_TOP_X + COLS * CELL_SIDE_PIXELS;
        int y1, y2;

        for (int ii = 0; ii <= ROWS; ii++) {
            y1 = CELL_TOP_Y + ii * CELL_SIDE_PIXELS;
            g2.drawLine(x1, y1, x2, y1);
        }

        y1 = CELL_TOP_Y;
        y2 = CELL_TOP_Y + ROWS * CELL_SIDE_PIXELS;
        for (int jj = 0; jj <= COLS; jj++) {
            x1 = CELL_TOP_X + jj * CELL_SIDE_PIXELS;
            g2.drawLine(x1, y1, x1, y2);
        }

    }

    private void drawCells(Graphics2D g2) {

        for (int ii = 0; ii < ROWS; ii++) {
            int ytop = CELL_TOP_Y + ii * CELL_SIDE_PIXELS;
            int ybot = ytop + CELL_SIDE_PIXELS;

            for (int jj = 0; jj < COLS; jj++) {

                int xleft = CELL_TOP_X + jj * CELL_SIDE_PIXELS;
                int xright = xleft + CELL_SIDE_PIXELS;

                Color c;
                
                int fontSize = 40;
                g2.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));

                int val = game.getCell(ii,jj);
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
                        g2.setColor(COLOR_FONT_VAL);
                        int dx = CELL_SIDE_PIXELS/4 ;
                        int dy = 3*CELL_SIDE_PIXELS/4 ;
                        g2.drawString("" + val, xleft+dx, ytop+dy);
                        break;
                    case -1:
                        // MINE
                        c = COLOR_MINE;
                        g2.setColor(c);
                        g2.fillRect(xleft, ytop, CELL_SIDE_PIXELS, CELL_SIDE_PIXELS);
                        JLabel label = new JLabel();
                        label.setBounds(xleft + 1, ytop + 1, CELL_SIDE_PIXELS - 2, CELL_SIDE_PIXELS - 2);
                        label.setIcon(image);
                        add(label);
                        mineLabels.add(label);
                        //System.out.println("in adding label mine: size = "+ mineLabels.size());
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

    private void drawButtons() {
        stepsLabel.setText(Integer.toString(game.getTurns()));
    }

    
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
        add(resetButton);

        
        
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
        add(stepButton);

        runButton.setText("Run");
        runButton.setBounds(RUN_RECT);
        class RunListener implements ActionListener {

            public void actionPerformed(ActionEvent e) {
                loopMode = !loopMode;
                String str = (loopMode) ? "Stop" : "Run";
                runButton.setText(str);
                repaint();
            }
        }
        runButton.addActionListener(new RunListener());
        runButton.setVisible(true);
        add(runButton);

        avgButton.setText("Calc. Avg.");
        avgButton.setBounds(AVG_RECT);

        avgLabel.setBounds(AVG_LABEL_RECT);
        avgLabel.setText("--");
        avgLabel.setVisible(true);
        add(avgLabel);

        class AvgListener implements ActionListener {

            public void actionPerformed(ActionEvent e) {
                final int NUM_OF_ITERATIONS = 1000;
                double sumTurns = 0;

                for (int ii = 0; ii < NUM_OF_ITERATIONS; ++ii) {
                    Game gg = new Game();
                    while(! gg.play());
                    sumTurns += gg.getTurns();
                }

                System.out.println("sumTurns=" + sumTurns);
                avgLabel.setText( Integer.toString((int)(sumTurns / NUM_OF_ITERATIONS)) );

                repaint();
            }
        }
        
        avgButton.addActionListener(new AvgListener());
        avgButton.setVisible(true);
        add(avgButton);


        //stepLabel.setText(Integer.toString(g.getStepCounter()));
        stepsLabel.setBounds(STEPS_RECT);
        stepsLabel.setText("0");
        stepsLabel.setVisible(true);
        add(stepsLabel);

    }

}