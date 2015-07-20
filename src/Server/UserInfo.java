package Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

class UserInfo extends Thread {
	private OutputStream os;
	private InputStream is;
	private DataOutputStream dos;
	private DataInputStream dis;

	private Socket socket;
	private String input_id = "";
	private String input_pw = "";
	private String nickname = "";
	private DBConnector connector;

	private boolean already_Login;// 계정이 이미 접속되어있는지의 여부
	private boolean roomCh;// 방을 만들 수 있는지의 여부
	private boolean ready;// 게임을 시작할 준비가 되있는지의 여부

	private StringTokenizer st;
	private Network network;

	UserInfo(Socket socket, Network network) {// 생성자
		connector = new DBConnector(network);
		roomCh = true;
		ready = false;
		this.socket = socket;
		this.network = network;
		userNetwork();
	}

	private synchronized void userNetwork() {// 네트워크 지원 설정
		try {
			network.setLoginInfo(false);
			is = socket.getInputStream();
			dis = new DataInputStream(is);

			os = socket.getOutputStream();
			dos = new DataOutputStream(os);

			input_id = dis.readUTF();// 클라이언트에서 아이디 받음
			InitGUI.setText(input_id + "에서 접속 시도");
			input_pw = dis.readUTF();// 클라이언트에서 비밀번호 받음
			InitGUI.setText(input_pw + "->비밀번호 입력");
			nickname = connector.db_Connection(input_id, input_pw);

			if (network.getLoginInfo() == true) {
				already_Login = true;
				for (int i = 0; i < network.getUserVector().size(); i++) {
					UserInfo u = network.getUserVector().elementAt(i);
					if (u.nickname.equals(nickname))
						already_Login = false;
				}
				if (!already_Login) {
					send_Message("already/ ");
				} else {
					// 기존 사용자들에게 새로운 사용자 알림
					broadCast("NewUser/" + nickname);
					// 자신에게 기존 사용자를 알림
					for (int i = 0; i < network.getUserVector().size(); i++) {
						UserInfo u = network.getUserVector().elementAt(i);
						send_Message("OldUser/" + u.nickname);
					}
					// 접속 사용자 Vector에 추가 시키기 위한 메세지
					send_Message("ThisUser/" + nickname);
					{
						network.getDefault().add_User(this);
						send_Message("default_Room/ ");
					}
					// 자신에게 기존 방 목록을 알림
					for (int i = 1; i < network.getRoomVector().size(); i++) {
						RoomInfo r = network.getRoomVector().elementAt(i);
						send_Message("OldRoom/" + r.getRoomName());
					}
					send_Message("room_list_update/ ");

					network.getUserVector().add(this); // 사용자에게 알린 후 Vector에 추가
					send_Message("true/ ");
					broadCast("user_list_update/ ");
				}
			} else {
				send_Message("false/ ");
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Stream 설정 에러", "알림",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void run() {// Thread로 처리할 내용
		while (true) {
			try {
				String msg = dis.readUTF();
				InitGUI.setText(input_id + "사용자로부터 들어온 메세지" + msg + "\n");
				inMessage(msg);
			} catch (IOException e) {// 클라이언트가 접속을 끊었을 때 에러처리
				InitGUI.setText(nickname + " 사용자 접속 종료\n");
				try {
					dos.close();
					dis.close();
					socket.close();
					network.getUserVector().remove(this);
					broadCast("User_out/" + nickname);
					broadCast("user_list_update/ ");
					broadCast("User_room_out/" + nickname);
					broadCast("room_user_list_update/ ");
					default_RoomOut();
				} catch (IOException e2) {
					System.out.println("서버의 통신이 끝나기 전에 클라이언트 종료");
				}
				break;
			}
		}
	}

	private synchronized void inMessage(String str) {// 클라이언트로부터 들어오는 메세지 처리
		st = new StringTokenizer(str, "/");
		String protocol = st.nextToken();
		final String message = st.nextToken();

		System.out.println("프로토콜: " + protocol);
		System.out.println("메세지: " + message);

		if (protocol.equals("Note")) {
			// protocol = Note
			// message = user2@내용
			
			String note = st.nextToken();

			System.out.println("받는 사람: " + message);
			System.out.println("보낼 내용: " + note);

			// Vector에서 사용자를 찾아 메세지 전송
			for (int i = 0; i < network.getUserVector().size(); i++) {
				UserInfo u = network.getUserVector().elementAt(i);
				if (u.nickname.equals(message)) {
					u.send_Message("Note/" + nickname + "/" + note);
					// protocol=Note / 보낸 유저=Nickname / 보낸내용=note
				}
			}
		} else if (protocol.equals("CreateRoom")) {
			// 1. 현재 같은 방이 존재하는지 확인
			for (int i = 0; i < network.getRoomVector().size(); i++) {
				RoomInfo r = network.getRoomVector().elementAt(i);
				if (r.getRoomName().equals(message)) {// 만들고자 하는 방이 이미 존재할 경우
					send_Message("CreateRoomFail/ok");
					roomCh = false;
					break;
				}
			}// for문 끝
			if (roomCh) {// 방을 만들 수 있을 때
				int aiLv = Integer.parseInt(st.nextToken());
				String secret = st.nextToken();
				String password = "";
				if (secret.equals("Y"))
					password = st.nextToken();
				RoomInfo new_room = new RoomInfo(message, this, aiLv, password);
				network.getRoomVector().add(new_room);// 전체 방 Vector에 방 추가
				send_Message("CreateRoom/" + message + "/" + this.nickname);

				broadCast("New_Room/" + message);
				default_RoomOut();// 자유채팅방에서 나옴
				send_Message("NewLeader/"+new_room.getMax());// 방 만들고 나서 방장 권한을 가짐
			}
			roomCh = true;
		} else if (protocol.equals("Chatting")) {
			if (st.hasMoreTokens()) {
				String msg = st.nextToken();
				if (message.equals("default")) {
					RoomInfo r = network.getRoomVector().elementAt(0);
					r.broadCast_Room("Chatting/" + nickname + "/" + msg);
				} else {
					for (int i = 1; i < network.getRoomVector().size(); i++) {
						RoomInfo r = network.getRoomVector().elementAt(i);
						if (r.getRoomName().equals(message)) {// 헤당 방을 찾았을 때
							r.broadCast_Room("Chatting/" + nickname + "/" + msg);
						}
					}
				}
			}
		} else if (protocol.equals("JoinRoom")) {// 방에 들어갈 때
			for (int i = 1; i < network.getRoomVector().size(); i++) {
				RoomInfo r = network.getRoomVector().elementAt(i);
				if (r.getRoomName().equals(message)) { // 들어갈 방을 찾으면
					if (!r.getPassword().equals("")) { // 패스워드가 존재할 경우
						send_Message("InsertPassword/"+message);
					} else { // 패스워드가 존재하지 않을 경우
						joinRoom(r, message);
					}
				}
			}
		} else if (protocol.equals("RoomOut")) {// 방에서 나갈 때
			for (int i = 1; i < network.getRoomVector().size(); i++) {
				RoomInfo r = network.getRoomVector().elementAt(i);
				if (r.getRoomName().equals(message)) {
					r.broadCast_Room("Chatting/[알림]/******" + nickname
							+ "님이 퇴장하셨습니다 ******");
					r.delete_User(this);
					this.ready = false;
					if (r.getRoomUserVector().size() == 0) {// 방 인원이 없을 때(0명)
						network.getRoomVector().remove(i);

						send_Message("RoomOut/" + message);
						broadCast("Delete_Room/" + message);
						broadCast("room_list_update/ ");
						broadCast("User_room_out/" + nickname);
						broadCast("room_user_list_update/ ");
					} else {
						send_Message("RoomOut/" + message);
						broadCast("User_room_out/" + nickname);
						broadCast("room_user_list_update/ ");

						UserInfo u = r.getRoomUserVector().elementAt(0);
						// 가장 먼저 들어온 순으로 방장 권한을 가짐
						u.send_Message("NewLeader/"+r.getMax());
						// 시작 버튼 활성화를 위한 메세지
					}
					default_JoinRoom();// 자유채팅방에 입장
				}
			}
		} else if (protocol.equals("SetReady")) {// 클라이언트에서 준비 버튼이 눌렸을 경우
			String nick = st.nextToken();
			for (int i = 1; i < network.getRoomVector().size(); i++) {
				RoomInfo r = network.getRoomVector().elementAt(i);
				if (r.getRoomName().equals(message)) {
					if (r.getRoomUserVector().get(1).ready) {
						r.getRoomUserVector().get(1).ready = false;
						r.broadCast_Room("Chatting/[알림]/" + nick
								+ "님께서 준비 취소를 하셨습니다.");
						r.getRoomUserVector().get(0)
								.send_Message("readyCancel/ ");
					} else {
						r.getRoomUserVector().get(1).ready = true;
						r.broadCast_Room("Chatting/[알림]/" + nick + "님 준비 완료!!");
						r.getRoomUserVector().get(0)
								.send_Message("readySuccess/ ");
					}
					break;
				}
			}
		} else if (protocol.equals("ReadyReset")) {
			for (int i = 1; i < network.getRoomVector().size(); i++) {
				RoomInfo r = network.getRoomVector().elementAt(i);
				if (r.getRoomName().equals(message)) {
					r.getRoomUserVector().get(0).ready = false;
					break;
				}
			}
		} else if (protocol.equals("record")) {// 전적을 갱신하기 위한 전송
			connector.record_update("nick_name", message);
			send_Message("record/" + message + "/" + connector.toTmpRecord());
		} else if (protocol.equals("secretRoomJoin")) {
			String password = st.nextToken();
			for (int i = 1; i < network.getRoomVector().size(); i++) { // 들어갈 방 찾기
				RoomInfo r = network.getRoomVector().elementAt(i);
				if (r.getRoomName().equals(message)) { // 들어갈 방을 찾으면
					if (r.getPassword().equals(password)) { // 입력된 비밀번호와 일치할 경우
						joinRoom(r, message);
					} else { // 일치하지 않을 경우
						send_Message("notEqualPW/ ");
					}
				}
			}
		} else if (protocol.equals("UserRecord")) {
			connector.record_update("nick_name", message);
			send_Message("UserRecord/" + message + "/" + connector.toTmpRecord());
		} else if (protocol.equals("StartClock")) {
			Thread t = new Thread(new Runnable(){
				public void run(){
					for (int i = 1; i < network.getRoomVector().size(); i++) {
						RoomInfo r = network.getRoomVector().elementAt(i);
						if (r.getRoomName().equals(message)) {
							final int DELAY = 1000;
							r.broadCast_Room("GUISet/" + "clock on");
							for(int j=5; j>=0; j--){
								try {
									System.out.println(j+"초 후 시삭합니다");
									r.broadCast_Room("GameNotice/"+j+"초 후 시작합니다\n");
									Thread.sleep(DELAY);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							r.broadCast_Room("GameNotice/"+"게임 시작!!\n");
							r.broadCast_Room("StartGame/" + r.getAi_Lv());
							r.broadCast_Room("GUISet/" + "clock off");
							break;
						}
					}
				}
			});
			t.start();
		} else if (protocol.equals("party'sNum")) { 
			/*
			 * 프로토콜: party'sNum
			 * 메세지: 상대방에게 보낼 자신의 숫자
			 * 추가1: 방이름
			 * 추가2: 자신의 닉네임 ( 자신이 아닌 다른 유저에게만 전달하기 위한 닉네임 )
			 */
			String roomName = st.nextToken();
			String nick = st.nextToken();
			boolean success = false;
			for (int i = 1; i< network.getRoomVector().size(); i++) {
				RoomInfo r = network.getRoomVector().elementAt(i);
				if (r.getRoomName().equals(roomName)) {
					r.setEnterComp();
					for(int j = 0; j< r.getRoomUserVector().size(); j++) {
						UserInfo u = r.getRoomUserVector().elementAt(i);
						if (!u.nickname.equals(nick)) {
							u.send_Message("party'sNum/"+message);
							success = true;
							break;
						}
					}
					if (r.getEnterComp() == r.getMax())
						r.broadCast_Room("settingComp/ ");
					if (success)
						break;
				}
			}
		} else if (protocol.equals("ballCount")) {
			/*
			 * 프로토콜: ballCount
			 * 메세지: 보낸 사용자
			 * 추가1: 사용자가 입력한 볼 카운트
			 */
		}
	}
	
	private void joinRoom(RoomInfo r, String message) {
		if (r.getRoomUserVector().size() < r.getMax()) {// 방 인원이 2명 미만일 때
			// 새로운 사용자를 알림
			r.broadCast_Room("Chatting/[알림]/******" + nickname
					+ "님이 입장하셨습니다 ******");
			default_RoomOut();// 자유채팅방에서 나옴

			// 방 내에 있던 기존 사용자들에게 새로운 사용자 알림
			r.broadCast_Room("RoomNewUser/" + this.nickname);
			// 자신에게 방 내에 있던 기존 사용자를 알림
			for (int j = 0; j < r.getRoomUserVector().size(); j++) {
				UserInfo u = r.getRoomUserVector().elementAt(j);
				send_Message("RoomOldUser/" + u.nickname);
			}
			// 리스트에 자기 자신을 추가
			send_Message("JoinRoom/" + message + "/"
					+ this.nickname);
			// 리스트 최신화
			r.broadCast_Room("room_user_list_update/ ");

			System.out.println(r.getRoomUserVector().size());

			// 방을 찾으면 사용자 추가
			r.add_User(this);
		} else {// 방 인원이 2명일 때
			send_Message("FullRoom/ ");
		}
	}

	/*
	 * default(자유채팅)방으로 자동 입장, 퇴장을 위한 메서드 private void JoinRoom(String message)
	 * private void RoomOut(String message)
	 */
	private void default_JoinRoom() {
		RoomInfo r = network.getRoomVector().elementAt(0);
		r.add_User(this);
		send_Message("default_Room/ ");

		System.out.println("default_Room에 있는 인원 -> "
				+ r.getRoomUserVector().size());
	}

	private void default_RoomOut() {
		RoomInfo r = network.getRoomVector().elementAt(0);
		r.delete_User(this);

		System.out.println("default_Room에 있는 인원 -> "
				+ r.getRoomUserVector().size());
	}

	private void broadCast(String str) {// 전체 사용자에게 메세지 보내는 부분
		for (int i = 0; i < network.getUserVector().size(); i++) {
			UserInfo u = network.getUserVector().elementAt(i);
			u.send_Message(str);
		}
	}

	public void send_Message(String str) {// 문자열을 받아 전송
		try {
			dos.writeUTF(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
