package Client;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class GameGUI implements ActionListener, KeyListener, WindowListener{

	private JPanel contentPane;
	private JTextField textField;
	private JButton[] btns;
	private Stack<Integer> numStack;
	private int aiLv;
	private boolean start;
	private boolean out;
	private Baseball baseball;
	private Network net;
	private JFrame frame;
	
	/**
	 * Create the frame.
	 */
	public GameGUI(int aiLv, Network net) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					numStack = new Stack<Integer>();
					initialize();
					listener();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		this.aiLv = aiLv;
		this.start = false;
		this.out = true;
		this.net = net;
	}
	
	/**
	 * Create
	 */
	public GameGUI() {
		this.out = false;
	}
	
	private void listener() {
		for(int i=0; i<btns.length; i++){
			btns[i].addActionListener(this);
			btns[i].addKeyListener(this);
		}
		frame.addWindowListener(this);
	}

	private void initialize() {
		frame = new JFrame("숫자 입력기");
		frame.setResizable(false);
		btns = new JButton[13];
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 278, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(null);

		btnCreate();
		
		Font font = new Font("돋움", Font.BOLD, 24);
		textField = new JTextField();
		textField.setColumns(10);
		textField.setBounds(12, 10, 235, 42);
		textField.setEditable(false);
		textField.setFont(font);
		contentPane.add(textField);
		
		net.setGameTA("숫자를 입력해주세요!\n상대플레이어가 숫자를 입력할 때까지 대기합니다.\n");
	}

	private void btnCreate() {
		for(int i=0; i<btns.length; i++) {
			if(i<10)
				btns[i] = new JButton(""+i);
			switch(i){
			case 0:
				btns[i].setBounds(12, 182, 70, 30);
				break;
			case 1:
				btns[i].setBounds(12, 62, 70, 30);
				break;
			case 2:
				btns[i].setBounds(95, 62, 70, 30);
				break;
			case 3:
				btns[i].setBounds(177, 62, 70, 30);
				break;
			case 4:
				btns[i].setBounds(12, 102, 70, 30);
				break;
			case 5:
				btns[i].setBounds(95, 102, 70, 30);
				break;
			case 6:
				btns[i].setBounds(177, 102, 70, 30);
				break;
			case 7:
				btns[i].setBounds(12, 142, 70, 30);
				break;
			case 8:
				btns[i].setBounds(95, 142, 70, 30);
				break;
			case 9:
				btns[i].setBounds(177, 142, 70, 30);
				break;
			case 10: //remove all
				btns[i] = new JButton("X");
				btns[i].setBounds(177, 182, 70, 30);
				break;
			case 11: // back sp
				btns[i] = new JButton("<-");
				btns[i].setBounds(95, 182, 70, 30);
				break;
			case 12: // confirm
				btns[i] = new JButton("확인")	;
				btns[i].setBounds(12, 222, 235, 30);
				break;
			}
			contentPane.add(btns[i]);
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// 방을 나갈것인지 물어보는 알림창
		JOptionPane.showMessageDialog(null, 
				"프로그램을 종료합니다.\n 게임 결과는 패로 기록이 됩니다.", 
				"알림", JOptionPane.INFORMATION_MESSAGE);
		net.send_message("RoomOut/" + net.getMyRoom());
		// 현재 전적에서 패에 대한 값을 1 증가시키는 부분 
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

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = 0;
		if((e.getKeyCode()>=48 && e.getKeyCode()<=57)||
				(e.getKeyCode()>=96 && e.getKeyCode()<=105)||
				e.getKeyCode() == 8){
			key = e.getKeyCode();
			if(setKey(key)){
				if(key != 8 && !numStack.contains(key)){
					
				} else if (key == 8) {
					
				} 
			}
		}
	}

	private boolean setKey(int key) {
		if(!numStack.contains(key) && numStack.size() < 3) {
			numStack.add(key);
			return true;
		}
		return false;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btns[0]) {
			if(setKey(0))
				setText(0);
		} else if (e.getSource() == btns[1]) {
			if(setKey(1))
				setText(1);
		} else if (e.getSource() == btns[2]) {
			if(setKey(2))
				setText(2);
		} else if (e.getSource() == btns[3]) {
			if(setKey(3))
				setText(3);
		} else if (e.getSource() == btns[4]) {
			if(setKey(4))
				setText(4);
		} else if (e.getSource() == btns[5]) {
			if(setKey(5))
				setText(5);
		} else if (e.getSource() == btns[6]) {
			if(setKey(6))
				setText(6);
		} else if (e.getSource() == btns[7]) {
			if(setKey(7))
				setText(7);
		} else if (e.getSource() == btns[8]) {
			if(setKey(8))
				setText(8);
		} else if (e.getSource() == btns[9]) {
			if(setKey(9))
				setText(9);
		} else if (e.getSource() == btns[10]) { // remove all
			textField.setText("");
			numStack.clear();
		} else if (e.getSource() == btns[11]) { // back sp
			String tmpString = textField.getText();
			int first = 0, last = numStack.size();
			if(last-1 >= 0){
				System.out.println(tmpString.substring(first, last-1));
				textField.setText(tmpString.substring(first, last-1));
				numStack.pop();
			}
		} else if (e.getSource() == btns[12]) {	// confirm
			if(numStack.size() == 3) {
				int[] userNumber = new int[3];
				for(int i=2; i>=0; i--)
					userNumber[i] = numStack.pop();
				if(!start) { // 처음 자신의 숫자를 입력하는 부분
					baseball = new Baseball(userNumber, aiLv, net);
					start = true;
				} else { // 게임에서 사용되는 부분
					baseball.compare(userNumber);
				}
				textField.setText("");
			} else {
				JOptionPane.showMessageDialog(null, "세자리 숫자를 입력해주세요!!", "알림",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
	
	private void setText(int i) {
		String tmpText = "";
		tmpText = textField.getText();
		tmpText = tmpText + i;
		textField.setText(tmpText);
	}
	
	public void setEnabled() {
		frame.setEnabled(true);
		// 본격적인 게임의 시작을 알리는 부분 - gmaeTextArea에 출력될 부분
		net.setGameTA("게임을 시작하도록 하겠습니다!!\n");
	}
	
	public void setEnabled(boolean b) {
		frame.setEnabled(b);
	}
	
	public void setPartyNum(String message) {
		baseball.setOppNum(message);
	}
	
	public boolean getOut() {
		return out;
	}
	
	public void dispose() {
		frame.dispose();
		out = false;
	}
}
