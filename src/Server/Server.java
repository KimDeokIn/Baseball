package Server;
public class Server {
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new InitGUI();
			}
		});
	}
	
}