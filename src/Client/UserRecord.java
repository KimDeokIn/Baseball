package Client;
import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class UserRecord implements WindowListener {

	private JFrame frame;
	private JTextArea userRecord_ta;

	public UserRecord() {
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 230, 140);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.addWindowListener(this);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);

		userRecord_ta = new JTextArea();
		userRecord_ta.setBounds(12, 10, 190, 81);
		userRecord_ta.setDragEnabled(false);
		userRecord_ta.setEditable(false);
		userRecord_ta.setBackground(Color.black);
		userRecord_ta.setForeground(Color.white);
		frame.getContentPane().add(userRecord_ta);
	}

	public void setText(String s) {
		userRecord_ta.append(s + "\n");
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		frame.dispose();
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}
}
