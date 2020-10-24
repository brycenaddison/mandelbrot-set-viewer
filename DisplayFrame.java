import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class DisplayFrame extends JFrame {

	// Launch Preferences
	private static final int HEIGHT = 1000;
	private static final int SHIFT = 80;

	private Point start;
	private Point end;
	private Mandelbrot set;
	private ImagePanel imagePanel;
	private SidebarPanel sidebar;

	private Rectangle rectangle;
	

	public DisplayFrame() {
		setTitle("Mandelbrot Set Explorer");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(new Dimension(HEIGHT*5/4, HEIGHT));
		setResizable(false);
		
		// Grabs Mandelbrot set and image
		set = new Mandelbrot(HEIGHT, HEIGHT);
		BufferedImage image = set.getImage();
		imagePanel = new ImagePanel(image);
		sidebar = new SidebarPanel(set, this);

		// Initializes listeners
		imagePanel.addKeyListener(new MandelbrotKeyListener());
		imagePanel.addMouseMotionListener(new MandelbrotMouseMotionListener());
		imagePanel.addMouseListener(new MandelbrotMouseListener());

		// Builds GUI
		this.setLayout(new BorderLayout());
		add(sidebar, BorderLayout.LINE_START);
		add(imagePanel, BorderLayout.CENTER);
		setVisible(true);
		imagePanel.setFocusable(true);
		imagePanel.requestFocusInWindow();
	}


	// Panel for displaying image + viewfinder rectangle
	private class ImagePanel extends JPanel {

		private BufferedImage image;

		public ImagePanel(BufferedImage image) {
			this.image = image;
		}

		public void setImage(BufferedImage image) {
			this.image = image;
			repaint();
		}

		public BufferedImage getImage() {
			return image;
		}

		// Draws Mandelbrot image and rectangle from instance variable if it exists
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(image, 0, 0, null);
			g.setColor(Color.blue);
			if (rectangle != null) {
				g.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
			}
		}
	}
	
	private class MandelbrotKeyListener implements KeyListener {

		public void keyTyped(KeyEvent e) {
			// Screenshot hotkey
			if (e.getKeyChar() == 's') {
				try {
					saveImage(imagePanel.getImage());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			// Reset zoom
			if (e.getKeyChar() == 'r') {
				resetImage();
			}

		}

		// Frame movements
		public void keyReleased(KeyEvent e) {
			double step = set.getStep();
			// Go right
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				setImage(set.getRealStart() + step * SHIFT,
						set.getImaginaryStart(), set.getRealEnd() + step
								* SHIFT);
			}
			// Go left
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				setImage(set.getRealStart() - step * SHIFT,
						set.getImaginaryStart(), set.getRealEnd() - step
								* SHIFT);
			}
			// Go up
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				setImage(set.getRealStart(),
						set.getImaginaryStart() + step * SHIFT, set.getRealEnd());
			}
			// Go down
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				setImage(set.getRealStart(),
						set.getImaginaryStart() - step * SHIFT, set.getRealEnd());
			}
		}

		public void keyPressed(KeyEvent e) {
		}
	}
		
	private class MandelbrotMouseMotionListener implements MouseMotionListener {
		
		// Draws rectangle when mouse is dragged
		public void mouseDragged(MouseEvent e) {

			Point topleft = getTopLeftPoint(start, e.getPoint());
			Point bottomright = getBottomRightPoint(start, e.getPoint());

			rectangle = new Rectangle(topleft.x, 
				topleft.y, bottomright.x - topleft.x, 
				bottomright.y - topleft.y);
			imagePanel.repaint();
		}
		
		// Attemps to grab control of the input
		public void mouseMoved(MouseEvent e){
			imagePanel.requestFocusInWindow();
		}
	}

	private class MandelbrotMouseListener implements MouseListener {

		// Grabs first point used to draw the rectangle
		public void mousePressed(MouseEvent e) {
			start = e.getPoint();
		}

		public void mouseReleased(MouseEvent e) {
			// Deletes the rectangle
			rectangle = null;
			// Grabs the point
			Point p = e.getPoint();
			// Grabs rectangle corners, calls zoom
			if (p.x != start.x) {
				Point temp = getTopLeftPoint(start, p);
				end = getBottomRightPoint(start, p);
				start = temp;
				DisplayFrame.this.zoom();
			}
		}

		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
	}

	// Returns top left point of the rectangle
	private Point getTopLeftPoint(Point p1, Point p2) {
		Point p = new Point();

		if (p1.x < p2.x) p.x = p1.x;
		else p.x = p2.x;
	
		if (p1.y < p2.y) p.y = p1.y;
		else p.y = p2.y;

		return p;
	}

	// Returns bottom right point of the rectangle
	private Point getBottomRightPoint(Point p1, Point p2) {
		Point p = new Point();

		if (p1.x < p2.x) p.x = p2.x;
		else p.x = p1.x;

		if (p1.y < p2.y) p.y = p2.y;
		else p.y = p1.y;

		return p;
	}

	private void zoom() {
		double currentRealStart = set.getRealStart();
		double currentImaginaryStart = set.getImaginaryStart();
		double step = set.getStep();

		double newRealStart = currentRealStart + start.x * step;
		double newImaginaryStart = currentImaginaryStart - start.y * step;

		double newRealEnd = currentRealStart + end.x * step;

		setImage(newRealStart, newImaginaryStart, newRealEnd);
	}

	// Grabs a new Mandelbrot image and updates the UI
	private void setImage(double newRealStart, double newImaginaryStart, double newRealEnd) {
		BufferedImage image = set.getImage(newRealStart, newImaginaryStart, newRealEnd);
		imagePanel.setImage(image);
		sidebar.update();
	}

	// Grabs default Mandelbrot image and updates UI
	public void resetImage() {
		setImage(Mandelbrot.REAL_START_DEFAULT,
				 Mandelbrot.IMAGINARY_START_DEFAULT,
				 Mandelbrot.REAL_END_DEFAULT);
	}
	
	// Grabs current Mandelbrot image
	public void updateImage() {
		BufferedImage image = set.getImage();
		imagePanel.setImage(image);
	}

	// Saves the BufferedImage as a png file
	private void saveImage(BufferedImage image) throws IOException {
		File outputfile = new File(System.currentTimeMillis() + ".png");
		ImageIO.write(image, "png", outputfile);
	}

	public int getHeight() {
		return HEIGHT;
	}

}