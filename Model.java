import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

/**
 * Model of the game of breakout
 * @author Mike Smith University of Brighton
 */

public class Model extends Observable
{
    // Boarder
    private static final int B              = 6;  // Border offset
    private static final int M              = 40; // Menu offset

    // Size of things
    private static final float BALL_SIZE    = 20; // Ball side
    private static final float BRICK_WIDTH  = 98; // Brick size
    private static final float BRICK_HEIGHT = 50;

    private static final int BAT_MOVE       = 5; // Distance to move bat
    // Scores
    private static final int HIT_BRICK      = 50;  // Score
    private static final int HIT_BOTTOM     = -200;// Score

    private GameObj ball;          // The ball
    private List<GameObj> bricks;  // The bricks
    private GameObj powerup;       // The power-up
    private GameObj bat;           // The bat
    
    

    private boolean runGame = true; // Game running
    private boolean fast = false;   // Sleep in run loop

    private int score = 0;
    private int lives = 3;         //The amount of lives the player has at the start of the game
    private final float W;         // Width of area
    private final float H;         // Height of area

    public Model( int width, int height )
    {
        this.W = width; this.H = height;
    }

    /**
     * Create in the model the objects that form the game
     */

    public void createGameObjects()
    {
        synchronized( Model.class )
        {
            ball   = new GameObj(W/2, H/2, BALL_SIZE, BALL_SIZE, Colour.PINK, 1);
            bat    = new GameObj(W/2, H - BRICK_HEIGHT*1.5f, BRICK_WIDTH*3, 
                BRICK_HEIGHT/5, Colour.WHITE, 1);
            bricks = new ArrayList<>();
            powerup = new GameObj(W/20, H/1.5f, BALL_SIZE, 
                BALL_SIZE, Colour.WHITE, 1);

            /*GameObj brick = new GameObj(W/2, H - BRICK_HEIGHT*1.5f, BRICK_WIDTH*3, BRICK_HEIGHT/4, Colour.BLUE);
            bricks.add(brick);*/

            // *[1]******************************************************[1]*
            // * Fill in code to place the bricks on the board  
            for(int i = 0; i < 14; i++)
            {
                GameObj brick= new GameObj(i * 100, 90, BRICK_WIDTH, BRICK_HEIGHT, Colour.BLUE, 2);
                bricks.add(brick);

                // **************************************************************
            }
            for(int i = 0; i < 14; i++)
            {
                GameObj brick = new GameObj(i * 100, 140, BRICK_WIDTH, BRICK_HEIGHT, Colour.GREEN, 2);
                bricks.add(brick);

                // **************************************************************
            }
            for(int i = 0; i < 14; i++)
            {
                GameObj brick = new GameObj(i * 100, 190, BRICK_WIDTH, BRICK_HEIGHT, Colour.YELLOW, 2);
                bricks.add(brick);

                // **************************************************************
            }
            for(int i = 0; i < 14; i++)
            {
                GameObj brick = new GameObj(i * 100, 240, BRICK_WIDTH, BRICK_HEIGHT, Colour.ORANGE, 2);
                bricks.add(brick);

                // **************************************************************
            }
            for(int i = 0; i < 14; i++)
            {
                GameObj redbrick = new GameObj(i * 100, 290, BRICK_WIDTH, BRICK_HEIGHT, Colour.RED, 2);
                bricks.add(redbrick);

                // **************************************************************
            }

        }
    }

    private ActivePart active  = null;

    /**
     * Start the continuous updates to the game
     */
    public void startGame()
    {
        synchronized ( Model.class )
        {
            stopGame();
            active = new ActivePart();
            Thread t = new Thread( active::runAsSeparateThread );
            t.setDaemon(true);   // So may die when program exits
            t.start();
        }
    }

    /**
     * Stop the continuous updates to the game
     * Will freeze the game, and let the thread die.
     */
    public void stopGame()
    {  
        synchronized ( Model.class )
        {
            if ( active != null ) { active.stop(); active = null; }
        }
    }

    public GameObj getBat()             { return bat; }

    public GameObj getBall()            { return ball; }

    public List<GameObj> getBricks()    { return bricks; }

    public GameObj getPowerup() {return powerup;}  

    /**
     * Add to score n units
     * @param n units to add to score
     */
    protected void addToScore(int n)    { score += n; }

    protected void addToLives(int n)    { lives -= n;}
    public int getScore()               { return score; }
    
    public int getLives()               { return lives; }
    

    /**
     * Set speed of ball to be fast (true/ false)
     * @param fast Set to true if require fast moving ball
     */
    public void setFast(boolean fast)   
    { 
        this.fast = fast; 
    }

    /**
     * Move the bat. (-1) is left or (+1) is right
     * @param direction - The direction to move
     */
    public void moveBat( int direction )
    {

        if ((bat.getX()>=W-B-BRICK_WIDTH*3)&&(direction==1))
        {
            direction=0;
        }
        if ((bat.getX()<=B)&&(direction==-1))
        {
            direction=0;
        }

        // *[2]******************************************************[2]*
        // * Fill in code to prevent the bat being moved off the screen *
        // **************************************************************
        float dist = direction * BAT_MOVE;    // Actual distance to move
        Debug.trace( "Model: Move bat = %6.2f", dist );
        bat.moveX(dist);

    }

    /**
     * This method is run in a separate thread
     * Consequence: Potential concurrent access to shared variables in the class
     */
    class ActivePart
    {
        private boolean runGame = true;

        public void stop()
        {
            runGame = false;
        }

        public void runAsSeparateThread()
        {
            final float S = 3; // Units to move (Speed)
            try
            {
                synchronized ( Model.class ) // Make thread safe
                {
                    GameObj       ball   = getBall();     // Ball in game
                    GameObj       bat    = getBat();      // Bat
                    GameObj       powerup = getPowerup();
                    List<GameObj> bricks = getBricks();   // Bricks

                }

                while (runGame)
                {
                    synchronized ( Model.class ) // Make thread safe
                    {
                        float x = ball.getX();  // Current x,y position
                        float y = ball.getY();


                        // Deal with possible edge of board hit
                        if (x >= W - B - BALL_SIZE)  ball.changeDirectionX();
                        if (x <= 0 + B            )  ball.changeDirectionX();
                        if (y >= H - B - BALL_SIZE)  // Bottom
                        { 
                            if((score >= 0  && score <= 199) &&  getLives() > 0)
                            { 

                                ball.changeDirectionY(); score = 0;
                                addToLives(1);
                                ball   = new GameObj(W/2, H/2, BALL_SIZE, BALL_SIZE, Colour.PINK, 1);
                                bat = new GameObj(W/2, H - BRICK_HEIGHT*1.5f, BRICK_WIDTH*3, 
                                    BRICK_HEIGHT/4, Colour.WHITE, 1);
                                
                            }
                            
                            else if((score >= 0  && score <= 199) && getLives() == 0)
                            { 
                                
                                

                                score = 0;
                                
                                lives = 3;
                                createGameObjects();
                            } 
                            else if((score >= 200) &&  getLives() > 0)
                            {
                                ball.changeDirectionY(); addToScore( HIT_BOTTOM );
                                addToLives(1);
                                ball   = new GameObj(W/2, H/2, BALL_SIZE, BALL_SIZE, Colour.PINK, 1);
                                bat = new GameObj(W/2, H - BRICK_HEIGHT*1.5f, BRICK_WIDTH*3, 
                                    BRICK_HEIGHT/4, Colour.WHITE, 1);
                                    
                                    
                            }
                            else if((score >= 200) && getLives() == 0)
                            {
                            score = 0;
                            lives = 3;
                            createGameObjects();
                            }
                            }
                       
                        if (y <= 0 + M            )  ball.changeDirectionY();

                        // As only a hit on the bat/ball is detected it is 
                        //  assumed to be on the top or bottom of the object.
                        // A hit on the left or right of the object
                        //  has an interesting affect

                        boolean hit = false;
                        // *[3]******************************************************[3]*
                        // * Fill in code to check if a visible brick has been hit      *
                        // *      The ball has no effect on an invisible brick          *
                        // **************************************************************
                        if (hit)
                            ball.changeDirectionY();

                        if ( ball.hitBy(bat) )
                            ball.changeDirectionY();

                        if ( powerup.hitBy(ball) )
                        {
                            score += 75;
                            powerup.setVisibility(false);
                        }
                            
                        for(GameObj brick: bricks) {
                            

                            
                                if( brick.hitBy(ball) && brick.isVisible() && brick.getColour() == Colour.BLUE)
                                    { 

                                        ball.changeDirectionY();
                                        score += (HIT_BRICK - 40);

                                        brick.setColour(Colour.GREEN);

                                    }
                                
                                    else if( brick.hitBy(ball) && brick.isVisible() && brick.getColour() == Colour.GREEN){
                                        ball.changeDirectionY();
                                        score += (HIT_BRICK - 30);
                                        
                                        brick.setColour(Colour.YELLOW);

                                    }
                                    else if( brick.hitBy(ball) && brick.isVisible() && brick.getColour() == Colour.YELLOW){
                                        ball.changeDirectionY();
                                        score += (HIT_BRICK - 20);
                                        
                                        brick.setColour(Colour.ORANGE);

                                }
                                else if( brick.hitBy(ball) && brick.isVisible() && brick.getColour() == Colour.ORANGE)
                                {
                                    ball.changeDirectionY();
                                    score += (HIT_BRICK - 10);
                                    
                                    brick.setColour(Colour.RED);
                             
                            }
                            else if( brick.hitBy(ball) && brick.isVisible() && brick.getColour() == Colour.RED)
                            {
                               ball.changeDirectionY();
                                    score += HIT_BRICK;
                                    
                                    brick.setVisibility(false);  
                            }
                                               
                        }
                        modelChanged();      // Model changed refresh screen
                        Thread.sleep( fast ? 18 : 10 );
                        ball.moveX(S);  ball.moveY(S);
                    }
                }
                } catch (Exception e) 
            
                { 
                Debug.error("Model.runAsSeparateThread - Error\n%s", 
                    e.getMessage() );
            }
        }
    }

    /**
     * Model has changed so notify observers so that they
     *  can redraw the current state of the game
     */
    public void modelChanged()
    {
        setChanged(); notifyObservers();
    }

}
