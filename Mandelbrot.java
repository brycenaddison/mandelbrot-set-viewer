// Creates a BufferedImage of a MandelbrotSet based on the smooth coloring algorithm.
// https://en.wikipedia.org/wiki/Mandelbrot_set#Continuous_(smooth)_coloring

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Mandelbrot {

	private static final float ESCAPE_RADIUS = 2f;
	private static final int MAX_ITERATIONS_CAP = 550;
	
	private int maxIterations = 100;
	private final int width;
	private final int height;

	public static final double REAL_START_DEFAULT = -2.15;
	public static final double IMAGINARY_START_DEFAULT = 1.50;
	public static final double REAL_END_DEFAULT = 0.85;
	
	private double realStart = REAL_START_DEFAULT;
	private double imaginaryStart = IMAGINARY_START_DEFAULT;
	private double realEnd = REAL_END_DEFAULT;
	private double step;

	private double realC;
	private double imaginaryC;
	private double realZ;
	private double imaginaryZ;

	private int iterationCounter;

	private Color[] colors;
	private double modulus;
	private int hueFactor = 100;
	private int brightnessFactor = 1000;
	private float initialHue = 0.33f;

	public double getRealStart() {
		return realStart;
	}

	public double getRealEnd() {
		return realEnd;
	}

	public double getImaginaryStart() {
		return imaginaryStart;
	}

	// Sets the size of the image, in pixels, and determines the size of a pixel
	public Mandelbrot(int width, int height) {
		this.width = width;
		this.height = height;
		this.step = (realEnd - realStart) / width;
		initColors();

	}

	// Initializes the color pallet
	private void initColors() {
		int numOfColors = maxIterations;
		colors = new Color[numOfColors];
		float hue = initialHue;
		float brightness = 1;
		float saturation = 1;
		for (int i = 0; i < colors.length; i++) {
			float steph = (float) (1 / (Math.log(i + 2) * hueFactor));
			float stepb = (float) (1 / (Math.log(i + 2) * brightnessFactor));
			hue += steph;
			brightness += stepb;
			colors[i] = new Color(Color.HSBtoRGB(hue, saturation, brightness));
		}
	}

	// Determines the color for a given pixel
	private Color getColor(double mu) {
		int clr1 = (int) Math.floor(mu);
		float t2 = (float) (mu - clr1);
		float t1 = 1 - t2;
		clr1 = clr1 % colors.length;
		int clr2 = (clr1 + 1) % colors.length;
		int red = (int) (colors[clr1].getRed() * t1 + colors[clr2].getRed()
				* t2);
		int green = (int) (colors[clr1].getGreen() * t1 + colors[clr2]
				.getGreen() * t2);
		int blue = (int) (colors[clr1].getBlue() * t1 + colors[clr2].getBlue()
				* t2);
		return new Color(red, green, blue);
	}

	// Calculates number of iterations to escape
	private void calculateF() {
		iterationCounter = 0;
		do {
			iterationStep();
			modulus = absoluteValue(realZ, imaginaryZ);
		} while (modulus <= ESCAPE_RADIUS && iterationCounter < maxIterations);

		iterationStep();
		iterationStep();
	}

	// Calculates another iteration of the Mandelbrot Set
	private void iterationStep() {
		iterationCounter++;
		double tmp = realZ;
		realZ = realZ * realZ - imaginaryZ * imaginaryZ + realC;
		imaginaryZ = 2 * tmp * imaginaryZ + imaginaryC;
	}

	// Returns the absolute value of a complex number
	private double absoluteValue(double real, double imaginary) {
		return Math.sqrt(real * real + imaginary * imaginary);
	}

	// Builds the image
	private BufferedImage createImage() {
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		// Calculates color for each pixel
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				// Calculates the value for complex constant C
				realC = realStart + step * x;
				imaginaryC = imaginaryStart - step * y;
				
				// Calculates iterations
				realZ = 0;
				imaginaryZ = 0;
				calculateF();

				// Sets pixel to black if it is likely to be in set
				if (iterationCounter >= maxIterations) {
					image.setRGB(x, y, Color.black.getRGB());
				} else {
					// Determines color based on the algorithm for smooth coloring
					// https://en.wikipedia.org/wiki/Mandelbrot_set#Continuous_(smooth)_coloring
					modulus = absoluteValue(realZ, imaginaryZ);
					double mu = iterationCounter + 1 - Math.log(Math.log(modulus))
							/ Math.log(ESCAPE_RADIUS);
					mu = mu / maxIterations * colors.length;
					image.setRGB(x, y, getColor(mu).getRGB());
				}
			}
		}
		return image;
	}

	// Sets variables and creates and image
	public BufferedImage getImage(double realStart, double imaginaryStart, double realEnd) {
		this.realStart = realStart;
		this.imaginaryStart = imaginaryStart;
		this.realEnd = realEnd;
		this.step = (realEnd - realStart) / width;
		double scale = 1 / (realEnd - realStart);
		calculateMaxIterations(scale);
		if(maxIterations > MAX_ITERATIONS_CAP) maxIterations = MAX_ITERATIONS_CAP;
		initColors();
		return createImage();
	}

	// A function found on https://math.stackexchange.com/questions/16970/a-way-to-determine-the-ideal-number-of-maximum-iterations-for-an-arbitrary-zoom
	// that will determine the max number of iterations to run to get sufficient detail based on the scale. 
	private void calculateMaxIterations(double scale) {
		this.maxIterations = (int) (Math.sqrt(Math.abs(2 * Math.sqrt(Math.abs(1 - Math
				.sqrt(5 * scale))))) * 66.5);
	}

	// Returns the image
	public BufferedImage getImage() {
		initColors();
		return createImage();
	}

	// Returns the size of each pixel
	public double getStep() {
		return step;
	}

	public void setHueFactor(int value) {
		this.hueFactor = value;
	}
	
	public void setBrightnessFactor(int value) {
		this.brightnessFactor  = value;
	}

	public void setInitialHue(float value) {
		this.initialHue  = value;
	}
}