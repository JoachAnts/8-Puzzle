import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Main extends JPanel {

	public final int TILE_WIDTH = 150;
	public final int TILE_HEIGHT = TILE_WIDTH;
	public final int WINDOW_WIDTH = 3*TILE_WIDTH;
	public final int WINDOW_HEIGHT = 3*TILE_HEIGHT;
	
	private BufferedImage imageBuffer;
	private Graphics graphics;
	
    private SwingWorker gameLooper;
    private boolean stop;
    
	private int seconds;

	private TilePuzzle tilePuzzle;
	LinkedList<TilePuzzle.BoardNode> list;
	int[][] tileBoard;
	BufferedImage[] tileImages;
    
    public Main() {
		tilePuzzle = new TilePuzzle(3);
		tilePuzzle.randomizeBoard(1000, tilePuzzle.tileBoard);
		list = tilePuzzle.getSolveList();
		tileBoard = list.poll().tileBoard;

		tileImages = new BufferedImage[9];
		loadTileImages();
		
		setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		
		imageBuffer = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
		graphics = imageBuffer.getGraphics();
        
        seconds = 0;
        stop = false;
        
        gameLooper = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                while(!stop) {
                    update();
                    repaint();
                    Thread.sleep(1000); //lazy 1 FPS & 1 update per second
                }
                return null;
            }
        };
        
        gameLooper.execute();
    }
	
	public void loadTileImages()
	{
		for (int i = 0; i < tileImages.length; i++)
		{
			try
			{
				// Grab the InputStream for the image.                    
				InputStream in = getClass().getResourceAsStream("img/"+i+".png");

				// Then read it in.
				tileImages[i] = ImageIO.read(in);
			}
			catch (IOException e)
			{
				System.out.println("The image was not loaded.");
				//System.exit(1);
			}
		}
	}

    @Override
    public void paint(Graphics grphcs) {
        //super.paint(grphcs); //do not remove
        graphics.setColor(Color.WHITE);
		graphics.fillRect(0,0,WINDOW_WIDTH,WINDOW_HEIGHT);
        graphics.setColor(Color.WHITE);
		for (int r = 0; r < tileBoard.length; r++)
		{
			for (int c = 0; c < tileBoard[0].length; c++)
			{	
				if (tileBoard[r][c] != 0)
					graphics.drawImage(tileImages[tileBoard[r][c]], 0+c*TILE_WIDTH, 0+r*TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT, null);
			}
		}
		grphcs.drawImage(imageBuffer, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, null);
    }
    
    public void update() { //DON'T update the UI in this method. Game logic ONLY!
		tileBoard = list.poll().tileBoard;
        seconds++;
    }

    public static void main(String[] args) {
        Main f = new Main();
		JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.add(f);
		frame.setVisible(true);
		frame.pack();
    }
}