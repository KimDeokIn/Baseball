package Client_back;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class Client extends JFrame implements ActionListener, KeyListener{

	//Login Gui 변수
	private JFrame login_frame = new JFrame();
	private JPanel login_Pane;
	private JPanel panel;
	private JTextField id_tf = null;
	private JPasswordField pw_tf = null;
	private JButton login_btn = new JButton("로그인");
	private JButton account_btn = new JButton("회원가입");
	private JButton home_btn = new JButton("홈페이지");
	private JButton search_btn = new JButton("아이디/패스워드 찾기");
	
	//Main Gui 변수
	private JPanel contentPane;
	private JPanel pagePane;
	private JTextField chat_tf;
	private JButton messegeSend_btn = new JButton("쪽지 보내기");
	private JButton gameJoin_btn = new JButton("방 입장");
	private JButton textSend_btn = new JButton("전송");
	private JButton roomCreate_btn = new JButton("방 만들기");
	private JButton roomOut_btn = new JButton("방 나가기");
	private JButton exit_btn = new JButton("종료");
	private JButton ready_btn = new JButton("준비");
	private JButton start_btn = new JButton("게임 시작");
	private JList<String> userList_list = new JList<String>();
	private JScrollPane userList_sP = new JScrollPane();
	private JList<String> roomList_list = new JList<String>();
	private JScrollPane roomList_sp = new JScrollPane();
	private JList<String> roomUserList_list = new JList<String>();
	private JTextArea chat_ta = new JTextArea();
	private JTextArea game_ta = new JTextArea();
	private JTextArea record_ta; // 전적 출력
	private JLabel userList_lb = new JLabel("전  체  접  속  자");
	private JLabel RoomList_lb = new JLabel("방  목  록");
	
	//Confirm Gui 변수
	private JPanel confirm_pane;
	private JFrame confirm_frame = new JFrame();
	private JButton confirm_btn = new JButton("확인");
	
	//네트워크 변수
	private String ip = "59.152.186.246";
	private int port = 8888;
	private Socket socket ;
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private String input_id = null;
	private String input_pw = null;
	private boolean state = false;
	
	//그 외 변수들
	private Vector<String> user_list = new Vector<String>();
	private Vector<String> room_list = new Vector<String>();
	private Vector<String> room_user_list = new Vector<String>();
	private StringTokenizer st; 
	private String myRoom = "default";//내가 있는 방 이름
	private String nickname = "";//닉네임
	
	Client(){
		login_init(); // 로그인 프레임 생성 메서드
		main_init(); // 메인 프레임 생성 메서드
		confirm_init(); // 로그인 오류 프레임 생성 메서드
		listener(); // 버튼 활성화를 위한 리스너 메서드
	}

	// 메인 UI 초기화
	private void main_init(){
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 700, 560);
		pagePane = new JPanel();
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(contentPane);
		contentPane.setLayout(null);
		
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
		
		userList_list.setListData(user_list);
		userList_sP.setViewportView(userList_list);
		
		roomList_sp.setBounds(12, 289, 120, 149);
		contentPane.add(roomList_sp);
		
		roomList_list.setListData(room_list);
		roomList_sp.setViewportView(roomList_list);
		
		record_init();
		record_ta.setVisible(false);
		
		start_btn.setEnabled(false);
		start_btn.setBounds(275, 489, 120, 23);
		contentPane.add(start_btn);
		
		ready_btn.setEnabled(false);
		ready_btn.setBounds(407, 489, 120, 23);
		contentPane.add(ready_btn);
		
		this.setVisible(false);		
	}
	
	private void record_init() {
		record_ta = new JTextArea();
		contentPane.add(record_ta);
		record_ta.setDragEnabled(false);
		record_ta.setEditable(false);
		record_ta.setVisible(false);
		record_ta.setBounds(12, 289, 120, 149);
		record_ta.setBackground(Color.black);
		record_ta.setForeground(Color.white);
	}

	// 리스너
	private void listener(){
		login_btn.addActionListener(this);
		account_btn.addActionListener(this);
		home_btn.addActionListener(this);
		search_btn.addActionListener(this);
		messegeSend_btn.addActionListener(this);
		gameJoin_btn.addActionListener(this);
		roomOut_btn.addActionListener(this);
		textSend_btn.addActionListener(this);
		roomCreate_btn.addActionListener(this);
		exit_btn.addActionListener(this);
		id_tf.addKeyListener(this);
		pw_tf.addKeyListener(this);
		chat_tf.addKeyListener(this);
		confirm_btn.addActionListener(this);
		ready_btn.addActionListener(this);
		start_btn.addActionListener(this);
	}
	
	// 로그인 UI 초기화
	private void login_init(){
		login_frame.setResizable(false);
		login_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login_frame.setBounds(100, 100, 340, 130);
		login_Pane = new JPanel();
		login_Pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		login_frame.setContentPane(login_Pane);
		login_Pane.setLayout(null);
		
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
		
		login_frame.setVisible(true);//화면에 보이게함
	}
	
	// 확인창 UI 초기화
	private void confirm_init(){
		confirm_frame.setResizable(false);
		//Confirm_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

	// 네트워크
	private void Network(){
		try {
			socket = new Socket(ip, port);
			if(socket != null){//정상적으로 소켓이 연결되었을 경우
				connection();
			}
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "연결 실패", "알림",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "연결 실패", "알림",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	// 연결
	private void connection(){
		try{
			is = socket.getInputStream();
			dis = new DataInputStream(is);
		
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
		}
		catch(IOException e){
			JOptionPane.showMessageDialog(null, "연결 실패", "알림",
					JOptionPane.ERROR_MESSAGE);
		}//Stream 설정 끝
		
		//처음 접속시 id,pw 전송;
		send_message(input_id);
		send_message(input_pw);
		
		Thread th = new Thread(new Runnable(){
			public void run(){
				while(true){
					try {
						String msg = dis.readUTF();//메세지 수신
						System.out.println("서버로부터 수신된 메세지: "+ msg);
						inMessage(msg);
					} catch (IOException e) {
						try{
							os.close();
							is.close();
							dos.close();
							dis.close();
							socket.close();
							JOptionPane.showMessageDialog(null, "서버와의 접속이 끊어졌습니다", 
									"알림",JOptionPane.ERROR_MESSAGE);
							
						}
						catch(IOException e2){}
						break;
					}
					
				}
			}
		});	
		th.start();
	}
		
	//서버로부터 들어오는  메세지
	private void inMessage(String str){
		st = new StringTokenizer(str,"/");
		String protocol = st.nextToken();
		String message = st.nextToken();
		
		System.out.println(protocol);
		System.out.println(message);
		
		if(protocol.equals("ThisUser")){// UserList_list에 사용자 추가
			user_list.add(message);
			nickname = message;
			send_message("record/"+nickname);
		}
		else if(protocol.equals("NewUser")){//새로운 접속자에 대한 메세지라면
			user_list.add(message);
		}
		else if(protocol.equals("OldUser")){
			user_list.add(message);
		}
		else if(protocol.equals("RoomNewUser")){
			room_user_list.add(message);
		}
		else if(protocol.equals("RoomOldUser")){
			room_user_list.add(message);
		}
		else if(protocol.equals("true")){
			this.setVisible(true);
			login_frame.setVisible(false);
			state = true;
		}
		else if(protocol.equals("false")){
			confirm_frame.setVisible(true);
		}
		else if(protocol.equals("already")){
			JOptionPane.showMessageDialog(null, "이미 접속중입니다"
					, "알림",JOptionPane.INFORMATION_MESSAGE);
		}
		else if(protocol.equals("Note")){
			String note = st.nextToken();
			
			System.out.println(message+"로부터 온 메세지: "+note);
			JOptionPane.showMessageDialog(null, note, message+"님으로부터 쪽지",
					JOptionPane.CLOSED_OPTION);
		}
		else if(protocol.equals("user_list_update")){
			//UserList_list.updateUI();
			userList_list.setListData(user_list);
		}
		else if(protocol.equals("room_user_list_update")){
			roomUserList_list.setListData(room_user_list);
		}
		else if(protocol.equals("CreateRoom")){//방을 만들었을 때
			myRoom = message;
			roomJoin_gui("CreateRoom");
			
			String Nick = st.nextToken();
			room_user_list.add(Nick);
			roomUserList_list.setListData(room_user_list);
			userList_sP.setViewportView(roomUserList_list);
		}
		else if(protocol.equals("CreateRoomFail")){//방만들기를 실패했을 때
			JOptionPane.showMessageDialog(null, "방 만들기 실패", "알림",
					JOptionPane.ERROR_MESSAGE);
		}
		else if(protocol.equals("New_Room")){//새로운 방을 만들었을 때
			chat_ta.append("[알림]: ******"+ message + "방을 생성하였습니다 ******\n");
			chat_ta.setCaretPosition(chat_ta.getDocument().getLength());

			room_list.add(message);
			roomList_list.setListData(room_list);
		}
		else if(protocol.equals("Chatting")){
			String msg = st.nextToken();
			chat_ta.append(message+": "+msg+"\n");
			chat_ta.setCaretPosition(chat_ta.getDocument().getLength());
		}
		else if(protocol.equals("OldRoom")){
			room_list.add(message);
		}
		else if(protocol.equals("room_list_update")){
			roomList_list.setListData(room_list);
		}
		else if(protocol.equals("JoinRoom")){//방에 입장할 때
			myRoom = message;
			roomJoin_gui("JoinRoom");
			
			String nick = st.nextToken();
			room_user_list.add(nick);
			roomUserList_list.setListData(room_user_list);
			userList_sP.setViewportView(roomUserList_list);
			
			chat_ta.append("[알림]: ******"+ message + "방에 입장하였습니다 ******\n");
			chat_ta.setCaretPosition(chat_ta.getDocument().getLength());
		}
		else if(protocol.equals("FullRoom")){
			JOptionPane.showMessageDialog(null, "인원이 가득찼습니다."
					, "알림",JOptionPane.INFORMATION_MESSAGE);
		}
		else if(protocol.equals("RoomOut")){//방에서 나갈 때
			myRoom = "default";
			roomOut_gui();
			
			JOptionPane.showMessageDialog(null, "채팅방에서 퇴장했습니다"
					, "알림",JOptionPane.INFORMATION_MESSAGE);
		}
		else if(protocol.equals("Delete_Room")){//방에 인원이 없어 방을 제거할 때 목록에서 제거
			room_list.remove(message);
		}
		else if(protocol.equals("User_out")){//사용자가 나갔을 때 목록에서 제거
			user_list.remove(message);
		}
		else if(protocol.equals("User_room_out")){//사용자가 방에서 나갔을 때 방 접속인원 목록에서 제거
			room_user_list.remove(message);
		}
		else if(protocol.equals("default_Room")){
			chat_ta.append("[알림]: ****** 자유 채팅방에 입장하였습니다 ******\n");
			chat_ta.setCaretPosition(chat_ta.getDocument().getLength());
		}
		else if(protocol.equals("NewLeader")){
			ready_btn.setEnabled(false);
			send_message("ReadyReset/"+myRoom+"/ ");
		}
		else if(protocol.equals("record")){
			if(message.equals(nickname)){
				record_init();
				int win = Integer.parseInt(st.nextToken());
				int lose = Integer.parseInt(st.nextToken());
				record_ta.append(nickname+"\n");
				record_ta.append((win+lose)+"전 "+win+"승 "+lose+"패\n");
				if(win+lose != 0)
					record_ta.append("승률: "+String.format("%2f", ((double)win/(win+lose)))+"%");
				else
					record_ta.append("승률: 0.00%");
			}else{
				userRecord_init();
			}	
		}
		else if(protocol.equals("readySuccess")){
			start_btn.setEnabled(true);
		}
		else if(protocol.equals("readyCancel")){
			start_btn.setEnabled(false);
		}
	}
	
	private void userRecord_init(){
		
	}
	
	private void roomJoin_gui(String s){
		gameJoin_btn.setEnabled(false);
		roomCreate_btn.setEnabled(false);
		roomOut_btn.setEnabled(true);
		exit_btn.setEnabled(false);
		userList_lb.setText("방 인 원");
		roomList_list.setVisible(false);
		RoomList_lb.setText("전 적 창");
		roomList_sp.setVisible(false);
		record_ta.setVisible(true);
		if(s.equals("JoinRoom"))
			ready_btn.setEnabled(true); // 입장이면 true
	}
	
	private void roomOut_gui(){
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
		
		room_user_list.removeAllElements();
		userList_list.setListData(user_list);
		userList_sP.setViewportView(userList_list);
	}
	
	private void send_message(String str){//서버에게 메세지를 보내는 부분
		try {
			dos.writeUTF(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == login_btn){
			System.out.println("Login");
			input_id = id_tf.getText().trim();//아이디 받아오는 부분
			input_pw = pw_tf.getText().trim();//비밀번호 받아오는 부분
			if(input_id.length()!=0 && input_pw.length()!=0){
				Network();		
			}
			else{
				confirm_frame.setVisible(true);
				confirm_btn.requestFocus();
				
			}
		}
		else if(e.getSource() == account_btn){
			System.out.println("Account");
		}
		else if(e.getSource() == home_btn){
			String url = "http://127.0.0.1/index.php";
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
		else if(e.getSource() == messegeSend_btn){
			System.out.println("MessegeSend_btn");
			
			String select_user;//쪽지 받을 유저
			select_user = (String)userList_list.getSelectedValue();
			
			String note = JOptionPane.showInputDialog("보낼쪽지");
			if(note!=null){
				send_message("Note/"+select_user+"/"+note);
				//protocol=Note / 선택된 유저=select_user / 보낼내용=note
			}
			System.out.println("받는 사람:" + select_user + "보낼 내용: "+note);
		}
		else if(e.getSource() == gameJoin_btn){
			String JoinRoom = (String)roomList_list.getSelectedValue();
			send_message("JoinRoom/"+JoinRoom);
			System.out.println("GameJoin_btn");
		}
		else if(e.getSource() == textSend_btn){
			send_message("Chatting/"+myRoom+"/"+chat_tf.getText().trim());
			//protocol=Chatting / 접속할 방 이름=My_Room / 보낼내용=Chat_tf.getText().trim()
			chat_tf.setText("");
			chat_tf.requestFocus();
			System.out.println("TextSend_btn");
		}
		else if(e.getSource() == roomCreate_btn){
			String RoomName = JOptionPane.showInputDialog("방 이름을 입력하세요");
			if(RoomName != null){
				send_message("CreateRoom/"+RoomName);
			}
			System.out.println("RoomCreate_btn");
		}
		else if(e.getSource() == roomOut_btn){
			System.out.println("RoomOut_btn");
			send_message("RoomOut/"+myRoom);
		}
		else if(e.getSource() == exit_btn){
			System.out.println("Exit_btn");
			send_message("RoomOut/"+myRoom);
			System.exit(0);
		}
		else if(e.getSource() == confirm_btn){
			System.out.println("Confirm_btn");
			confirm_frame.setVisible(false);
		}
		else if(e.getSource() == ready_btn){
			send_message("SetReady/"+myRoom+"/"+nickname);
			System.out.println("Ready_btn");
		}
		else if(e.getSource() == start_btn){
			System.out.println("Start_btn");
		}
	}
	@Override
	public void keyPressed(KeyEvent k) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()==10&&state==false){
			login_btn.doClick();
		}
		else if(e.getKeyCode()==10&&state==true){
			send_message("Chatting/"+myRoom+"/"+chat_tf.getText().trim());
			chat_tf.setText("");
			chat_tf.requestFocus();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
		      public void run() {
		    	 new Client();
		      }
		    });	
	}
}
