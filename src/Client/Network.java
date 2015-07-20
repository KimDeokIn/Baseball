package Client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

class Network extends MainGUI{
	// 네트워크 변수
	private String ip;
	private int port;
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private String input_id;
	private String input_pw;
	private boolean state;

	private StringTokenizer st;
	private LoginGUI login;
	private GameGUI gameFrame;
	
	Network() {
		ip = "127.0.0.1";
		port = 8888;
		input_id = null;
		input_pw = null;
		state = false;
		super.setNetwork(this);
		gameFrame = new GameGUI();
	}

	public void setLoginGUI(LoginGUI login) {
		this.login = login;
	}

	public boolean getState() {
		return state;
	}

	public void set_ID(String s) {
		input_id = s;
	}

	public void set_PW(String s) {
		input_pw = s;
	}

	public String get_ID() {
		return input_id;
	}

	public String get_PW() {
		return input_pw;
	}

	public String get_IP() {
		return ip;
	}

	public void networkStart() {
		try {
			socket = new Socket(ip, port);
			if (socket != null) {// 정상적으로 소켓이 연결되었을 경우
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

	private void connection() {
		try {
			is = socket.getInputStream();
			dis = new DataInputStream(is);

			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "연결 실패", "알림",
					JOptionPane.ERROR_MESSAGE);
		}// Stream 설정 끝

		// 처음 접속시 id,pw 전송;
		send_message(input_id);
		send_message(input_pw);

		Thread th = new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						String msg = dis.readUTF();// 메세지 수신
						System.out.println("서버로부터 수신된 메세지: " + msg);
						inMessage(msg);
					} catch (IOException e) {
						try {
							os.close();
							is.close();
							dos.close();
							dis.close();
							socket.close();
							JOptionPane.showMessageDialog(null,
									"서버와의 접속이 끊어졌습니다", "알림",
									JOptionPane.ERROR_MESSAGE);

						} catch (IOException e2) {
						}
						break;
					}

				}
			}
		});
		th.start();
	}

	private void inMessage(String str) {
		st = new StringTokenizer(str, "/");
		String protocol = st.nextToken();
		String message = st.nextToken();

		System.out.println(protocol);
		System.out.println(message);

		if (protocol.equals("ThisUser")) {// UserList_list에 사용자 추가
			super.getUserList().add(message);
			super.setNick(message);
			send_message("record/" + message);
		} else if (protocol.equals("NewUser")) {// 새로운 접속자에 대한 메세지라면
			super.getUserList().add(message);
		} else if (protocol.equals("OldUser")) {
			super.getUserList().add(message);
		} else if (protocol.equals("RoomNewUser")) {
			super.getRoomUserList().add(message);
		} else if (protocol.equals("RoomOldUser")) {
			super.getRoomUserList().add(message);
		} else if (protocol.equals("true")) {
			super.setLoginSuccess();
			login.exitGUI();
			state = true;
		} else if (protocol.equals("false")) {
			JOptionPane.showMessageDialog(null, "아이디/비밀번호 를 확인해주세요", "알림",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (protocol.equals("already")) {
			JOptionPane.showMessageDialog(null, "이미 접속중입니다", "알림",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (protocol.equals("Note")) {
			String note = st.nextToken();
			String[] buttons = {"회신", "확인"};
			System.out.println(message + "로부터 온 메세지: " + note);
			int setNum = JOptionPane.showOptionDialog(null, note, message + "님으로부터 쪽지", 0
							,JOptionPane.INFORMATION_MESSAGE, null, buttons, buttons[0]);
			if (setNum == 0)
				super.note(message);
		} else if (protocol.equals("user_list_update")) {
			// UserList_list.updateUI();
			super.userListUpdate();
		} else if (protocol.equals("room_user_list_update")) {
			super.roomUserListUpdate();
		} else if (protocol.equals("CreateRoom")) {// 방을 만들었을 때
			super.setRoomName(message);
			String nick = st.nextToken();
			super.roomJoin_gui("CreateRoom", nick);
		} else if (protocol.equals("CreateRoomFail")) {// 방만들기를 실패했을 때
			JOptionPane.showMessageDialog(null, "같은 이름의 방이 존재합니다", "알림",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (protocol.equals("New_Room")) {// 새로운 방을 만들었을 때
			super.setChatTA("[알림]: ******" + message + "방을 생성하였습니다 ******\n");
			super.getRoomList().add(message);
			super.roomListUpdate();
		} else if (protocol.equals("Chatting")) {
			String msg = st.nextToken();
			super.setChatTA(message + ": " + msg + "\n");
		} else if (protocol.equals("OldRoom")) {
			super.getRoomList().add(message);
		} else if (protocol.equals("room_list_update")) {
			super.roomListUpdate();
		} else if (protocol.equals("JoinRoom")) {// 방에 입장할 때
			super.setRoomName(message);
			String nick = st.nextToken();
			super.roomJoin_gui("JoinRoom", nick);
			super.setChatTA("[알림]: ******" + message + "방에 입장하였습니다 ******\n");
		} else if (protocol.equals("FullRoom")) {
			JOptionPane.showMessageDialog(null, "인원이 가득찼습니다.", "알림",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (protocol.equals("RoomOut")) {// 방에서 나갈 때
			super.setRoomName("default");
			super.roomOut_gui();
			super.setChatTA("[알림]: ****** 방에서 퇴장하였습니다 ******\n");
		} else if (protocol.equals("Delete_Room")) {// 방에 인원이 없어 방을 제거할 때 목록에서
													// 제거
			super.getRoomList().remove(message);
		} else if (protocol.equals("User_out")) {// 사용자가 나갔을 때 목록에서 제거
			super.getUserList().remove(message);
		} else if (protocol.equals("User_room_out")) {// 사용자가 방에서 나갔을 때 방 접속인원
														// 목록에서 제거
			super.getRoomUserList().remove(message);
		} else if (protocol.equals("default_Room")) {
			super.setChatTA("[알림]: ****** 대기실에 입장하였습니다 ******\n");
		} else if (protocol.equals("NewLeader")) {
			super.setReadyBtn(false);
			if(message.equals("2"))
				super.setStartBtn(false);
			else 
				super.setStartBtn(true);
			send_message("ReadyReset/" + super.getNick() + "/ ");
		} else if (protocol.equals("record")) {
			super.record_init();
			int win = Integer.parseInt(st.nextToken());
			int lose = Integer.parseInt(st.nextToken());
			super.setRecordTA(message + "\n");
			super.setRecordTA((win + lose) + "전 " + win + "승 " + lose + "패\n");
			if (win + lose != 0)
				super.setRecordTA("승률: "
						+ String.format("%2f", ((double) win / (win + lose)))
						+ "%");
			else
				super.setRecordTA("승률: 0.00%");
		} else if (protocol.equals("readySuccess")) {
			super.setStartBtn(true);
		} else if (protocol.equals("readyCancel")) {
			super.setStartBtn(false);
		} else if (protocol.equals("InsertPassword")) {
			String password = JOptionPane.showInputDialog("비밀번호를 입력하세요.");
			send_message("secretRoomJoin/" + message + "/" + password);
		} else if (protocol.equals("notEqualPW")) {
			JOptionPane.showMessageDialog(null, "비밀번호를 확인해 주세요", "알림",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (protocol.equals("UserRecord")) {
			UserRecord ur = new UserRecord();
			int win = Integer.parseInt(st.nextToken());
			int lose = Integer.parseInt(st.nextToken());
			ur.setText(message);
			ur.setText((win + lose) + "전 " + win + "승 " + lose + "패");
			if (win + lose != 0)
				ur.setText("승률: "
						+ String.format("%2f", ((double) win / (win + lose)))
						+ "%");
			else
				ur.setText("승률: 0.00%");
		} else if (protocol.equals("StartGame")) {
			gameFrame = new GameGUI(Integer.parseInt(message), this);
			super.gameStart(true);
		} else if (protocol.equals("GameNotice")) {
			super.setChatTA(message);
		} else if (protocol.equals("GUISet")) {
			if(message.equals("clock off"))
				super.startGUISet(true);
			else if(message.equals("clock on")) 
				super.startGUISet(false);
			else if(message.equals("end game")) {
				
			}
		} else if (protocol.equals("Game End")) {
			
		} else if (protocol.equals("settingComp")) {
			gameFrame.setEnabled();
		} else if (protocol.equals("party'sNum")) {
			gameFrame.setPartyNum(message);
		}
	}

	public boolean getOut() {
		return gameFrame.getOut();
	}
	
	public void gameFrameClose() {
		gameFrame.dispose();
	}
	
	public void gameFrameEnable(boolean b) {
		gameFrame.setEnabled(b);
	}
	
	public void send_message(String str) {// 서버에게 메세지를 보내는 부분
		try {
			dos.writeUTF(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
