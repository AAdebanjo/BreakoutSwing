import java.awt.Color;

/**
 * Hide the specific internal representation of colours
 *  from most of the program.
 * Map to Swing color when required.
 */
public enum Colour  
{ 
  RED(Color.RED), BLUE(Color.BLUE), GRAY(Color.GRAY), GREEN(Color.GREEN), YELLOW(Color.YELLOW), ORANGE(Color.ORANGE), WHITE(Color.WHITE), PINK(Color.PINK), CYAN(Color.CYAN);

  private final Color c;

  Colour( Color c ) { this.c = c; }

  public Color forSwing() { return c; }
}


