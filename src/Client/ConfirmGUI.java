package Client;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

class ConfirmGUI implements ActionListener {

	private JPanel confirm_pane;
	private JFrame confirm_frame = new JFrame();
	private JButton confirm_btn = new JButton("확인");

	ConfirmGUI() {
		confirm_frame = new JFrame();
		confirm_btn = new JButton("확인");
		init();
		listener();
	}

	private void init() {
		confirm_frame.setResizable(false);
		// Confirm_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		confirm_frame.setBounds(100, 100, 230, 106);
		confirm_pane = new JPanel();
		confirm_pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		confirm_frame.setContentPane(confirm_pane);
		confirm_pane.setLayout(null);

		JLabel Confirm_lb = new JLabel("아이디/비밀번호를 확인하세요");
		Confirm_lb.setFont(new Font("굴림", Font.PLAIN, 14));
		Confirm_lb.setBounds(12, 10, 205, 15);
		confirm_pane.add(Confirm_lb);

		confirm_btn.setBounds(60, 35, 97, 23);
		confirm_pane.add(confirm_btn);
	}

	private void listener() {
		confirm_btn.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == confirm_btn){
			System.out.println("Confirm_btn");
			confirm_frame.setVisible(false);
		}
	}
	
	public void setVisible(boolean b){
		confirm_frame.setVisible(b);
		confirm_btn.requestFocus();
	}
}
