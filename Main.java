import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
public class Main
{
    public static void main(String[] args) {
    	// Attempts to set look and feel to Nimbus
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(info.getName())) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
				break;
			}
		}
		
		// Starts the GUI in the event dispatch thread
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new DisplayFrame();
			}
		});
	}
}