package Client;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

class MainGUI extends Information implements ActionListener, KeyListener, WindowListener {
	// Main Gui 변수
	private JPanel contentPane;
	private JPanel pagePane;
	private JTextField chat_tf;
	private JButton messegeSend_btn;
	private JButton gameJoin_btn;
	private JButton textSend_btn;
	private JButton roomCreate_btn;
	private JButton roomOut_btn;
	private JButton exit_btn;
	private JButton ready_btn;
	private JButton start_btn;
	private JList<String> userList_list;
	private JScrollPane userList_sP;
	private JList<String> roomList_list;
	private JScrollPane roomList_sp;
	private JList<String> roomUserList_list;
	private JTextArea chat_ta;
	private JTextArea game_ta;
	private JTextArea record_ta; // 전적 출력
	private JLabel userList_lb;
	private JLabel RoomList_lb;
	private JFrame frame;

	private Network network;

	MainGUI() {
		messegeSend_btn = new JButton("쪽지 보내기");
		gameJoin_btn = new JButton("방 입장");
		textSend_btn = new JButton("전송");
		roomCreate_btn = new JButton("방 만들기");
		roomOut_btn = new JButton("방 나가기");
		exit_btn = new JButton("종료");
		ready_btn = new JButton("준비");
		start_btn = new JButton("게임 시작");
		userList_list = new JList<String>();
		userList_sP = new JScrollPane();
		roomList_list = new JList<String>();
		roomList_sp = new JScrollPane();
		roomUserList_list = new JList<String>();
		chat_ta = new JTextArea();
		game_ta = new JTextArea();
		userList_lb = new JLabel("전  체  접  속  자");
		RoomList_lb = new JLabel("방  목  록");
		frame = new JFrame("Baseball Online 실행기");
		init();
		listener();
	}

	protected void roomListUpdate() {
		roomList_list.setListData(super.getRoomList());
	}

	protected void roomUserListUpdate() {
		roomUserList_list.setListData(super.getRoomUserList());
	}

	protected void userListUpdate() {
		userList_list.setListData(super.getUserList());
	}

	protected void setChatTA(String s) {
		chat_ta.append(s);
		chat_ta.setCaretPosition(chat_ta.getDocument().getLength());
	}
	
	protected void setGameTA(String s) {
		game_ta.append(s);
		game_ta.setCaretPosition(game_ta.getDocument().getLength());
	}

	protected void setLoginSuccess() {
		frame.setVisible(true);
	}

	protected void setReadyBtn(boolean b) {
		ready_btn.setEnabled(b);
	}

	protected void setRecordTA(String s) {
		record_ta.append(s);
	}

	protected void setStartBtn(boolean b) {
		start_btn.setEnabled(b);
	}

	protected void setNetwork(Network network) {
		this.network = network;
	}

	protected void roomJoin_gui(String s, String nick) {
		gameJoin_btn.setEnabled(false);
		roomCreate_btn.setEnabled(false);
		roomOut_btn.setEnabled(true);
		exit_btn.setEnabled(false);
		userList_lb.setText("방 인 원");
		roomList_list.setVisible(false);
		RoomList_lb.setText("전 적 창");
		roomList_sp.setVisible(false);
		record_ta.setVisible(true);
		super.getRoomUserList().add(nick);
		roomUserList_list.setListData(super.getRoomUserList());
		userList_sP.setViewportView(roomUserList_list);
		messegeSend_btn.setText("전적 보기");
		
		if (s.equals("JoinRoom"))
			ready_btn.setEnabled(true); // 입장이면 true
	}

	protected void roomOut_gui() {
		gameJoin_btn.setEnabled(true);
		roomCreate_btn.setEnabled(true);
		roomOut_btn.setEnabled(false);
		exit_btn.setEnabled(true);
		userList_lb.setText("전 체 접 속 자");
		roomList_list.setVisible(true);
		RoomList_lb.setText("방 목 록");
		roomList_sp.setVisible(true);
		record_ta.setVisible(false);
		start_btn.setEnabled(false);
		ready_btn.setEnabled(false);
		messegeSend_btn.setText("쪽지 보내기");

		super.getRoomUserList().removeAllElements();
		userList_list.setListData(super.getUserList());
		userList_sP.setViewportView(userList_list);
	}

	private void init() {
		frame.setResizable(false);
		frame.setBounds(100, 100, 700, 560);
		pagePane = new JPanel();
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(null);
		frame.setLocationRelativeTo(null);

		userList_lb.setBounds(12, 10, 120, 15);
		contentPane.add(userList_lb);

		JSeparator separator = new JSeparator();
		separator.setBounds(12, 35, 120, 2);
		contentPane.add(separator);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(12, 240, 120, 2);
		contentPane.add(separator_1);

		messegeSend_btn.setBounds(12, 207, 120, 23);
		contentPane.add(messegeSend_btn);

		RoomList_lb.setBounds(12, 252, 120, 15);
		contentPane.add(RoomList_lb);

		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(12, 277, 120, 2);
		contentPane.add(separator_2);

		JSeparator separator_3 = new JSeparator();
		separator_3.setBounds(12, 449, 120, 2);
		contentPane.add(separator_3);

		gameJoin_btn.setBounds(12, 461, 120, 23);
		contentPane.add(gameJoin_btn);

		JScrollPane Chat_ta_sP = new JScrollPane();
		Chat_ta_sP.setBounds(144, 240, 529, 211);
		contentPane.add(Chat_ta_sP);

		Chat_ta_sP.setViewportView(chat_ta);
		chat_ta.setEditable(false);
		chat_ta.setDragEnabled(false);

		textSend_btn.setBounds(582, 461, 90, 23);
		contentPane.add(textSend_btn);

		roomCreate_btn.setBounds(12, 489, 120, 23);
		contentPane.add(roomCreate_btn);

		roomOut_btn.setBounds(143, 489, 120, 23);
		contentPane.add(roomOut_btn);
		roomOut_btn.setEnabled(false);

		exit_btn.setBounds(582, 489, 90, 23);
		contentPane.add(exit_btn);

		chat_tf = new JTextField();
		chat_tf.setBounds(144, 462, 426, 21);
		contentPane.add(chat_tf);
		chat_tf.setColumns(10);

		JScrollPane Game_ta_sP = new JScrollPane();
		Game_ta_sP.setBounds(144, 10, 528, 224);
		contentPane.add(Game_ta_sP);

		Game_ta_sP.setViewportView(game_ta);
		game_ta.setEditable(false);
		game_ta.setDragEnabled(false);
		game_ta.setVisible(false);

		pagePane.setBounds(144, 10, 528, 224);
		contentPane.add(pagePane);

		userList_sP.setBounds(12, 46, 120, 151);
		contentPane.add(userList_sP);

		userList_list.setListData(super.getUserList());
		userList_sP.setViewportView(userList_list);

		roomList_sp.setBounds(12, 289, 120, 149);
		contentPane.add(roomList_sp);

		roomList_list.setListData(super.getRoomList());
		roomList_sp.setViewportView(roomList_list);

		record_init();
		record_ta.setVisible(false);

		start_btn.setEnabled(false);
		start_btn.setBounds(275, 489, 120, 23);
		contentPane.add(start_btn);

		ready_btn.setEnabled(false);
		ready_btn.setBounds(407, 489, 120, 23);
		contentPane.add(ready_btn);

		frame.setVisible(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	protected void record_init() {
		record_ta = new JTextArea();
		contentPane.add(record_ta);
		record_ta.setDragEnabled(false);
		record_ta.setEditable(false);
		record_ta.setVisible(false);
		record_ta.setBounds(12, 289, 120, 149);
		record_ta.setBackground(Color.black);
		record_ta.setForeground(Color.white);
	}

	private void listener() {
		messegeSend_btn.addActionListener(this);
		gameJoin_btn.addActionListener(this);
		roomOut_btn.addActionListener(this);
		textSend_btn.addActionListener(this);
		roomCreate_btn.addActionListener(this);
		exit_btn.addActionListener(this);
		chat_tf.addKeyListener(this);
		ready_btn.addActionListener(this);
		start_btn.addActionListener(this);
		frame.addWindowListener(this);
	}

	protected void startGUISet(boolean b) {
		roomOut_btn.setEnabled(b);
		if(!b){
			ready_btn.setEnabled(b);
			start_btn.setEnabled(b);
		}
	}
	
	protected void endGUISet() {
		ready_btn.setEnabled(true);
		start_btn.setEnabled(true);
	}
	
	protected void gameStart(boolean b) {
		game_ta.setDragEnabled(b);
		game_ta.setVisible(b);
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == 10) {
			network.send_message("Chatting/" + super.getMyRoom() + "/"
					+ chat_tf.getText().trim());
			chat_tf.setText("");
			chat_tf.requestFocus();
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == messegeSend_btn) {
			System.out.println("MessegeSend_btn");
			String select_user;// 쪽지 받을 유저
			if (messegeSend_btn.getText().equals("쪽지 보내기")) {
				select_user = (String) userList_list.getSelectedValue();
				note(select_user);
			} else if (messegeSend_btn.getText().equals("전적 보기")) {
				select_user = roomUserList_list.getSelectedValue();
				if(select_user != null)
					network.send_message("UserRecord/" + select_user);
			}
		} else if (e.getSource() == gameJoin_btn) {
			String joinRoom = (String) roomList_list.getSelectedValue();
			if(joinRoom != null)
				network.send_message("JoinRoom/" + joinRoom);
			System.out.println("GameJoin_btn");
		} else if (e.getSource() == textSend_btn) {
			network.send_message("Chatting/" + super.getMyRoom() + "/"
					+ chat_tf.getText().trim());
			// protocol=Chatting / 접속할 방 이름=My_Room /
			// 보낼내용=Chat_tf.getText().trim()
			chat_tf.setText("");
			chat_tf.requestFocus();
			System.out.println("TextSend_btn");
		} else if (e.getSource() == roomCreate_btn) {
			new CreateRoomGUI(this);
			System.out.println("RoomCreate_btn");
		} else if (e.getSource() == roomOut_btn) {
			System.out.println("RoomOut_btn");
			if (network.getOut()) {
				String[] buttons = {"Yes" , "No"};
				int setNumber = JOptionPane.showOptionDialog(null, 
						"현재 방에서 나가시겠습니까?\n 나가시게 된다면 이번 게임의 기록은 패로 기록됩니다.", 
						"방 나가기", 0, JOptionPane.INFORMATION_MESSAGE, null, buttons, buttons[0]);
				if (setNumber == 0) {
					network.send_message("RoomOut/" + super.getMyRoom());
					network.gameFrameClose();
				}
			} else {
				network.send_message("RoomOut/" + super.getMyRoom());
			}
			
		} else if (e.getSource() == exit_btn) {
			System.out.println("Exit_btn");
			network.send_message("RoomOut/" + super.getMyRoom());
			System.exit(0);
		} else if (e.getSource() == ready_btn) {
			network.send_message("SetReady/" + super.getMyRoom() + "/"
					+ super.getNick());
			System.out.println("Ready_btn");
		} else if (e.getSource() == start_btn) {
			network.send_message("StartClock/" + super.getMyRoom());
			System.out.println("Start_btn");
		}
	}

	protected void note(String select_user) {
		String note = JOptionPane.showInputDialog("보낼쪽지");
		if (select_user != null && !note.equals("")) {
			network.send_message("Note/" + select_user + "/" + note);
			// protocol=Note / 선택된 유저=select_user / 보낼내용=note
		}
		System.out.println("받는 사람:" + select_user + "보낼 내용: " + note);
	}
	
	public void createRoom(String createRoomName) {
		network.send_message("CreateRoom/" + createRoomName);
	}

	public JFrame getFrame() {
		return frame;
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosing(WindowEvent e) {
		network.send_message("RoomOut/" + super.getMyRoom());
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}