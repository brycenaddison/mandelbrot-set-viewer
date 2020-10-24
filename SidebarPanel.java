// Panel for displaying info and controls
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SidebarPanel extends JPanel {

	private final JLabel title;

	private final JLabel reals = new JLabel("Real Boundaries");
	private final JLabel imags = new JLabel("Imaginary Boundaries");
	private final JLabel hotkeys = new JLabel("Controls");
	private final JLabel minReal = new JLabel();
	private final JLabel minImag = new JLabel();
	private final JLabel maxReal = new JLabel();
	private final JLabel maxImag = new JLabel();
	private final JLabel scale = new JLabel();

	private final SpinnerModel hueFactorModel = new SpinnerNumberModel(100, 1, 2000, 10);
	private final JSpinner hueFactor = new JSpinner(hueFactorModel);

	private final SpinnerModel initialHueModel = new SpinnerNumberModel(0.33, 0.01, 0.99, 0.01);
	private final JSpinner initialHue = new JSpinner(initialHueModel);
	
	private final Mandelbrot set;
	private final DisplayFrame frame;

	private final JButton update = new JButton("Update");
	private final JButton reset = new JButton("Reset Zoom");

	public SidebarPanel(Mandelbrot set, DisplayFrame frame) {
		this.frame = frame;
		this.set = set;

		title = new JLabel("Mandelbrot Explorer");
		title.setFont(new Font(title.getFont().getName(), Font.BOLD, 18));

		updateLabels();

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(frame.getHeight()/4,frame.getHeight()));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		add(title, c);

		reals.setFont(new Font(reals.getFont().getName(), Font.BOLD, reals.getFont().getSize()));
		c.gridy = 1;
		add(reals, c);

		c.gridy = 2;
		add(minReal, c);

		c.gridy = 3;
		add(maxReal, c);

		imags.setFont(new Font(imags.getFont().getName(), Font.BOLD, imags.getFont().getSize()));
		c.gridy = 4;
		add(imags, c);

		c.gridy = 5;
		add(minImag, c);
		
		c.gridy = 6;
		add(maxImag, c);

		c.gridy = 7;
		add(scale, c);

		c.gridy = 8;
		c.gridwidth = 1;
		add(new JLabel("Hue Factor:"), c);

		c.gridx = 1;
		add(hueFactor, c);
		
		c.gridy = 9;
		c.gridx = 0;
		add(new JLabel("Initial Hue:"), c);
		
		c.gridx = 1;
		add(initialHue, c);
		
		c.gridx = 0;
		c.gridy = 10;
		add(update, c);

		c.gridx = 1;
		add(reset, c);

		c.gridy = 11;
		c.gridwidth = 2;
		c.gridx = 0;
		hotkeys.setFont(new Font(hotkeys.getFont().getName(), Font.BOLD, hotkeys.getFont().getSize()));
		add(hotkeys, c);

		c.gridy = 12;
		c.gridwidth = 1;
		add(new JLabel("S"), c);

		c.gridx = 1;
		add(new JLabel("Take a screenshot"), c);

		c.gridy = 13;
		c.gridx = 0;
		add(new JLabel("R"), c);

		c.gridx = 1;
		add(new JLabel("Reset zoom"), c);

		c.gridy = 14;
		c.gridx = 0;
		add(new JLabel("\u2190/\u2191/\u2192/\u2193"), c);

		c.gridx = 1;
		add(new JLabel("Shift the selection"), c);

		c.gridx = 0;
		c.gridy = 15;
		add(new JLabel("Click+Drag"), c);

		c.gridx = 1;
		add(new JLabel("Zoom in"), c);

		update.addActionListener(new ButtonListener());
		reset.addActionListener(new ButtonListener());
	}

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == update) { 
				set.setHueFactor((int) hueFactor.getValue());
				set.setInitialHue(((Double) initialHue.getValue()).floatValue());
				frame.updateImage();
			}
			if (e.getSource() == reset) {
				frame.resetImage();
			}
		}
	}

	private void init() {
		
	}

	private void updateLabels() {
		minReal.setText("Min: " + set.getRealStart());
		minImag.setText("Min: " + set.getImaginaryStart());
		maxReal.setText("Max: " + set.getRealEnd());
		maxImag.setText("Max: " + (set.getImaginaryStart() + (set.getRealEnd() - set.getRealStart())));
		scale.setText("Scale: " + set.getStep());
	}

	public void update() {
		updateLabels();
		revalidate();
	}

}