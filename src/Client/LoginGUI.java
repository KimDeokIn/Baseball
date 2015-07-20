package Client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoginGUI implements ActionListener, KeyListener {
	// Login Gui 변수
	private JFrame login_frame = new JFrame();
	private JPanel login_Pane;
	private JPanel panel;
	private JTextField id_tf = null;
	private JPasswordField pw_tf = null;
	private JButton login_btn;
	private JButton account_btn;
	private JButton home_btn;
	private JButton search_btn;

	private Network network;
	
	LoginGUI() {
		network = new Network();
		login_frame = new JFrame();
		id_tf = null;
		pw_tf = null;
		login_btn = new JButton("로그인");
		account_btn = new JButton("회원가입");
		home_btn = new JButton("홈페이지");
		search_btn = new JButton("아이디/패스워드 찾기");
		init();
		listener();
		network.setLoginGUI(this);
	}
	
	private void init() {
		login_frame.setResizable(false);
		login_frame.setBounds(100, 100, 340, 130);
		login_Pane = new JPanel();
		login_Pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		login_frame.setContentPane(login_Pane);
		login_Pane.setLayout(null);
		login_frame.setLocationRelativeTo(null);

		panel = new JPanel();
		panel.setBounds(0, 0, 197, 92);
		login_Pane.add(panel);
		panel.setLayout(null);

		JLabel Id_lb = new JLabel("아이디");
		Id_lb.setBounds(12, 10, 57, 15);
		panel.add(Id_lb);

		id_tf = new JTextField();
		id_tf.setBounds(70, 7, 116, 21);
		panel.add(id_tf);
		id_tf.setColumns(10);

		JLabel Pw_lb = new JLabel("비밀번호");
		Pw_lb.setBounds(12, 35, 57, 15);
		panel.add(Pw_lb);

		pw_tf = new JPasswordField();
		pw_tf.setBounds(70, 32, 116, 21);
		panel.add(pw_tf);
		pw_tf.setColumns(10);

		login_btn.setBounds(209, 6, 103, 23);
		login_Pane.add(login_btn);

		account_btn.setBounds(209, 31, 103, 23);
		login_Pane.add(account_btn);

		home_btn.setBounds(209, 64, 103, 23);
		login_Pane.add(home_btn);

		search_btn.setBounds(12, 63, 185, 23);
		panel.add(search_btn);

		login_frame.setVisible(true);// 화면에 보이게함
		login_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void listener() {
		login_btn.addActionListener(this);
		account_btn.addActionListener(this);
		home_btn.addActionListener(this);
		search_btn.addActionListener(this);
		id_tf.addKeyListener(this);
		pw_tf.addKeyListener(this);
	}
	
	public void exitGUI(){
		login_frame.dispose();
	}
	
	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == login_btn){
			System.out.println("Login");
			network.set_ID(id_tf.getText().trim());//아이디 받아오는 부분
			network.set_PW(pw_tf.getText().trim());//비밀번호 받아오는 부분
			if(network.get_ID().length()!=0 && network.get_PW().length()!=0){
				network.networkStart();		
			}
			else{
				JOptionPane.showMessageDialog(null, "아이디/비밀번호 를 확인해주세요", "알림",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if(e.getSource() == account_btn){
			System.out.println("Account");
		}
		else if(e.getSource() == home_btn){
			String url = "http://"+network.get_IP()+"/index.php";
			String cmd = "explorer " + url;
			try {
				Runtime.getRuntime().exec(cmd);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println("home");
		}
		else if(e.getSource() == search_btn){
			System.out.println("Search");
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == 10 && !network.getState()) {
			login_btn.doClick();
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}
}
