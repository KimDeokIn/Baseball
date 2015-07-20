package Server_back;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class Server extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -201499807143767262L;

	// 서버 프레임 변수
	private JPanel contentPane;
	private JTextArea serverState = new JTextArea();
	private JButton serverOpen = new JButton("서버 시작");
	private JButton serverClose = new JButton("서버 종료");

	// 네트워크 변수
	private ServerSocket server_socket;
	private Socket socket;
	private int port = 8888;
	private Vector<UserInfo> user_vc = new Vector<UserInfo>();
	private boolean login_success = false;
	private Vector<RoomInfo> room_vc = new Vector<RoomInfo>();
	private RoomInfo default_room; // 대기방을 위한 변수
	private String tmpRecord = ""; // 전적 임시 저장을 위한 변수

	// sql변수
	private String url = "jdbc:mysql://localhost:3306/baseball";
	private Connection con = null;
	private PreparedStatement ps = null;
	private String db_id = "root";
	private String db_pw = "backkom13";
	private ResultSet rs_id = null;
	private ResultSet rs_pw = null;
	private ResultSet rs_nick = null;

	private StringTokenizer st;

	Server() {// 생성자
		init();// 화면 구성 메서드
		listener();// 리스터 설정 메서드
	}

	private void listener() {
		serverOpen.addActionListener(this);
		serverClose.addActionListener(this);
	}

	private void init() {
		this.setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 320);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 410, 210);
		contentPane.add(scrollPane);

		scrollPane.setViewportView(serverState);
		serverState.setEditable(false);

		serverOpen.setBounds(12, 235, 130, 23);
		contentPane.add(serverOpen);

		serverClose.setBounds(278, 235, 130, 23);
		contentPane.add(serverClose);
		serverClose.setEnabled(false);

		this.setVisible(true);// 화면에 보이게 함
	}

	private void server_start() {
		try {
			server_socket = new ServerSocket(port);// 포트 사용
			{
				default_room = new RoomInfo();
				room_vc.add(default_room);
			}// 대기 채팅방 생성
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "이미 사용중인 포트입니다", "알림",
					JOptionPane.ERROR_MESSAGE);
		}

		if (server_socket != null) {// 정상적으로 포트가 열렸을 경우
			connection();
		}
	}

	private void connection() {

		Thread th = new Thread(new Runnable() {
			public void run() {// 스레드에서 처리할 일
				while (true) {
					try {
						serverState.append("사용자 접속 대기중\n");
						serverState.setCaretPosition(serverState.getDocument()
								.getLength());
						socket = server_socket.accept();// 사용자 접속 할 때까지 무한대기
						serverState.append("사용자 접속!!\n");
						serverState.setCaretPosition(serverState.getDocument()
								.getLength());

						UserInfo user = new UserInfo(socket);
						user.start();// 객체의 스레드 실행
					} catch (IOException e) {
						serverState.append("서버 종료\n");
						serverState.setCaretPosition(serverState.getDocument()
								.getLength());
						break;
					}
				}
			}
		});
		th.start();
	}

	private String db_Connection(String Input_id, String Input_pw) {
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			System.out.println("드라이버 검색 성공");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 검색 실패!!");
		}

		try {
			con = DriverManager.getConnection(url, db_id, db_pw);
			System.out.println("db접근 성공");
		} catch (SQLException e) {
			System.out.println("db접근 실패!!!");
		}

		try {
			ps = con.prepareStatement("select user_id from xe_member "
					+ "where user_id=\"" + Input_id + "\"");
			rs_id = ps.executeQuery();
			rs_id.next();

			ps = con.prepareStatement("select password from xe_member "
					+ "where user_id=\"" + Input_id + "\"");
			rs_pw = ps.executeQuery();
			rs_pw.next();

			// 클라이언트에서 받아온 아이디가 db에 저장되있을 경우 패스워드 비교
			if (Input_id.equals(rs_id.getString(1)))
				return compare_account(rs_pw.getString(1), stringMD5(Input_pw),
						Input_id);

		} catch (SQLException e) {
			serverState.append(Input_id + " 계정 접속 실패\n");
			serverState.setCaretPosition(serverState.getDocument().getLength());
		}
		return null;
	}

	private String stringMD5(String key) {
		byte[] hash = null;
		MessageDigest md;

		try {
			md = MessageDigest.getInstance("MD5");
			md.update(key.getBytes());
			hash = md.digest();
		} catch (NoSuchAlgorithmException e) {
			//
		}
		return hashMD5(hash);
	}

	private String hashMD5(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			if ((0xff & hash[i]) < 0x10) {
				hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
			} else {
				hexString.append(Integer.toHexString(0xFF & hash[i]));
			}
		}
		return hexString.toString();
	}

	private String compare_account(String User_pw, String Input_pw,
			String Input_id) throws SQLException {
		if (User_pw.equals(Input_pw)) {
			System.out.println("로그인 성공!");
			login_success = true;
			ps = con.prepareStatement("select nick_name from xe_member "
					+ "where user_id=\"" + Input_id + "\"");
			rs_nick = ps.executeQuery();
			rs_nick.next();
			return rs_nick.getString(1);
		} else {
			System.out.println("아이디 또는 비밀번호를 확인하세요");
			return null;
		}
	}

	private void record_update(String item, String id) {
		StringBuilder s = new StringBuilder();
		try {
			ps = con.prepareStatement("select win from xe_member where " + item
					+ " =\"" + id + "\"");
			rs_id = ps.executeQuery();
			rs_id.next();
			if (rs_id.getString(1) == null) {
				System.out.println("일단 들어옴");
				// 전적이 Null값으로 저장되어 있는 경우 0값으로 초기화 시켜주는 작업
				String sql = "update xe_member set win = 0, lose = 0 where "
						+ item + " = \"" + id + "\"";
				ps.executeUpdate(sql);
				tmpRecord = "0/0";
			} else {
				s.append(rs_id.getString(1) + "/");
				String sql = "select lose from xe_member where " + item
						+ " =\"" + id + "\"";
				rs_id = ps.executeQuery(sql);
				rs_id.next();
				s.append(rs_id.getString(1));
				tmpRecord = s.toString();
			}

		} catch (SQLException e) {
			System.out.println("db접근 실패!!!");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Server();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == serverOpen) {
			System.out.println("Server Open");
			server_start();// 소켓 생성 및 사용자 접속 대기
			serverOpen.setEnabled(false);
			serverClose.setEnabled(true);
		} else if (e.getSource() == serverClose) {
			serverClose.setEnabled(false);
			serverOpen.setEnabled(true);
			try {
				server_socket.close();
				user_vc.removeAllElements();
				room_vc.removeAllElements();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println("Server Close");
		}
	}// 액션 이벤트 끝

	private class UserInfo extends Thread {
		private OutputStream os;
		private InputStream is;
		private DataOutputStream dos;
		private DataInputStream dis;

		private Socket user_socket;
		private String input_id = "";
		private String input_pw = "";
		private String nickname = "";

		private boolean already_Login;// 계정이 이미 접속되어있는지의 여부
		private boolean roomCh = true;// 방을 만들 수 있는지의 여부
		private boolean ready = false;// 게임을 시작할 준비가 되있는지의 여부

		UserInfo(Socket soc) {// 생성자
			this.user_socket = soc;
			userNetwork();
		}

		private void userNetwork() {// 네트워크 지원 설정
			try {
				login_success = false;
				is = user_socket.getInputStream();
				dis = new DataInputStream(is);

				os = user_socket.getOutputStream();
				dos = new DataOutputStream(os);

				input_id = dis.readUTF();// 클라이언트에서 아이디 받음
				serverState.append(input_id + "에서 접속 시도");
				serverState.setCaretPosition(serverState.getDocument()
						.getLength());
				input_pw = dis.readUTF();// 클라이언트에서 비밀번호 받음
				serverState.append(input_pw + "->비밀번호 입력");
				serverState.setCaretPosition(serverState.getDocument()
						.getLength());
				nickname = db_Connection(input_id, input_pw);

				if (login_success == true) {
					already_Login = true;
					for (int i = 0; i < user_vc.size(); i++) {
						UserInfo u = user_vc.elementAt(i);
						if (u.nickname.equals(nickname))
							already_Login = false;
					}
					if (!already_Login) {
						send_Message("already/ ");
					} else {
						// 기존 사용자들에게 새로운 사용자 알림
						broadCast("NewUser/" + nickname);
						// 자신에게 기존 사용자를 알림
						for (int i = 0; i < user_vc.size(); i++) {
							UserInfo u = user_vc.elementAt(i);
							send_Message("OldUser/" + u.nickname);
						}
						// 접속 사용자 Vector에 추가 시키기 위한 메세지
						send_Message("ThisUser/" + nickname);
						{
							default_room.add_User(this);
							send_Message("default_Room/ ");
						}
						// 자신에게 기존 방 목록을 알림
						for (int i = 1; i < room_vc.size(); i++) {
							RoomInfo r = room_vc.elementAt(i);
							send_Message("OldRoom/" + r.room_name);
						}
						send_Message("room_list_update/ ");

						user_vc.add(this); // 사용자에게 알린 후 Vector에 추가
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
					serverState
							.append(input_id + "사용자로부터 들어온 메세지" + msg + "\n");
					serverState.setCaretPosition(serverState.getDocument()
							.getLength());
					inMessage(msg);
				} catch (IOException e) {// 클라이언트가 접속을 끊었을 때 에러처리
					serverState.append(nickname + " 사용자 접속 종료\n");
					serverState.setCaretPosition(serverState.getDocument()
							.getLength());
					try {
						dos.close();
						dis.close();
						user_socket.close();
						user_vc.remove(this);
						broadCast("User_out/" + nickname);
						broadCast("user_list_update/ ");
						broadCast("User_room_out/" + nickname);
						broadCast("room_user_list_update/ ");
						default_RoomOut();
					} catch (IOException e2) {
					}
					break;
				}
			}
		}

		private void inMessage(String str) {// 클라이언트로부터 들어오는 메세지 처리
			st = new StringTokenizer(str, "/");
			String protocol = st.nextToken();
			String message = st.nextToken();

			System.out.println("프로토콜: " + protocol);
			System.out.println("메세지: " + message);

			if (protocol.equals("Note")) {
				// protocol = Note
				// message = user2@내용

				String note = st.nextToken();

				System.out.println("받는 사람: " + message);
				System.out.println("보낼 내용: " + note);

				// Vector에서 사용자를 찾아 메세지 전송
				for (int i = 0; i < user_vc.size(); i++) {
					UserInfo u = user_vc.elementAt(i);
					if (u.nickname.equals(message)) {
						u.send_Message("Note/" + nickname + "/" + note);
						// protocol=Note / 보낸 유저=Nickname / 보낸내용=note
					}
				}
			} else if (protocol.equals("CreateRoom")) {
				// 1. 현재 같은 방이 존재하는지 확인
				for (int i = 0; i < room_vc.size(); i++) {
					RoomInfo r = room_vc.elementAt(i);
					if (r.room_name.equals(message)) {// 만들고자 하는 방이 이미 존재할 경우
						send_Message("CreateRoomFail/ok");
						roomCh = false;
						break;
					}
				}// for문 끝
				if (roomCh) {// 방을 만들 수 있을 때
					RoomInfo new_room = new RoomInfo(message, this);
					room_vc.add(new_room);// 전체 방 Vector에 방 추가
					send_Message("CreateRoom/" + message + "/" + this.nickname);

					broadCast("New_Room/" + message);
					default_RoomOut();// 자유채팅방에서 나옴
					send_Message("NewLeader/ ");// 방 만들고 나서 방장 권한을 가짐
				}
				roomCh = true;
			} else if (protocol.equals("Chatting")) {
				if (st.hasMoreTokens()) {
					String msg = st.nextToken();
					if (message.equals("default")) {
						RoomInfo r = room_vc.elementAt(0);
						r.broadCast_Room("Chatting/" + nickname + "/" + msg);
					} else {
						for (int i = 1; i < room_vc.size(); i++) {
							RoomInfo r = room_vc.elementAt(i);
							if (r.room_name.equals(message)) {// 헤당 방을 찾았을 때
								r.broadCast_Room("Chatting/" + nickname + "/"
										+ msg);
							}
						}
					}
				}
			} else if (protocol.equals("JoinRoom")) {// 방에 들어갈 때
				for (int i = 1; i < room_vc.size(); i++) {
					RoomInfo r = room_vc.elementAt(i);
					if (r.room_user_vc.size() < r.room_MaxMember) {// 방 인원이 2명
																	// 미만일 때
						if (r.room_name.equals(message)) {
							// 새로운 사용자를 알림
							r.broadCast_Room("Chatting/[알림]/******" + nickname
									+ "님이 입장하셨습니다 ******");

							if (r.room_name.equals("Default"))
								send_Message("default_Room/ ");
							else {
								default_RoomOut();// 자유채팅방에서 나옴

								// 방 내에 있던 기존 사용자들에게 새로운 사용자 알림
								r.broadCast_Room("RoomNewUser/" + this.nickname);
								// 자신에게 방 내에 있던 기존 사용자를 알림
								for (int j = 0; j < r.room_user_vc.size(); j++) {
									UserInfo u = r.room_user_vc.elementAt(j);
									send_Message("RoomOldUser/" + u.nickname);
								}
								// 리스트에 자기 자신을 추가
								send_Message("JoinRoom/" + message + "/"
										+ this.nickname);
								// 리스트 최신화
								r.broadCast_Room("room_user_list_update/ ");

								System.out.println(r.room_user_vc.size());
							}
							// 방을 찾으면 사용자 추가
							r.add_User(this);
						}
					} else {// 방 인원이 2명일 때
						send_Message("FullRoom/ ");
					}
				}
			} else if (protocol.equals("RoomOut")) {// 방에서 나갈 때
				for (int i = 1; i < room_vc.size(); i++) {
					RoomInfo r = room_vc.elementAt(i);
					if (r.room_name.equals(message)) {
						r.broadCast_Room("Chatting/[알림]/******" + nickname
								+ "님이 퇴장하셨습니다 ******");
						r.delete_User(this);
						if (r.room_user_vc.size() == 0) {// 방 인원이 없을 때(0명)
							room_vc.remove(i);

							send_Message("RoomOut/" + message);
							broadCast("Delete_Room/" + message);
							broadCast("room_list_update/ ");
							broadCast("User_room_out/" + nickname);
							broadCast("room_user_list_update/ ");
						} else {
							send_Message("RoomOut/" + message);
							broadCast("User_room_out/" + nickname);
							broadCast("room_user_list_update/ ");

							UserInfo u = r.room_user_vc.elementAt(0);
							// 가장 먼저 들어온 순으로 방장 권한을 가짐
							u.send_Message("NewLeader/ ");
							// 시작 버튼 활성화를 위한 메세지
						}
						default_JoinRoom();// 자유채팅방에 입장
					}
				}
			} else if (protocol.equals("SetReady")) {// 클라이언트에서 준비 버튼이 눌렸을 경우
				String nick = st.nextToken();
				for (int i = 1; i < room_vc.size(); i++) {
					RoomInfo r = room_vc.elementAt(i);
					if (r.room_name.equals(message)) {
						if (r.room_user_vc.get(1).ready) {
							r.room_user_vc.get(1).ready = false;
							r.broadCast_Room("Chatting/[알림]/" + nick
									+ "님께서 준비 취소를 하셨습니다.");
							r.room_user_vc.get(0).send_Message("readyCancel/ ");
						} else {
							r.room_user_vc.get(1).ready = true;
							r.broadCast_Room("Chatting/[알림]/" + nick
									+ "님 준비 완료!!");
							r.room_user_vc.get(0)
									.send_Message("readySuccess/ ");
						}
						break;
					}
				}
			} else if (protocol.equals("ReadyReset")) {
				for (int i = 1; i < room_vc.size(); i++) {
					RoomInfo r = room_vc.elementAt(i);
					if (r.room_name.equals(message)) {
						r.room_user_vc.get(0).ready = false;
						break;
					}
				}
			} else if (protocol.equals("record")) {// 전적을 갱신하기 위한 전송
				record_update("nick_name", message);
				send_Message("record/" + message + "/" + tmpRecord);
			}
		}

		/*
		 * default(자유채팅)방으로 자동 입장, 퇴장을 위한 메서드 private void JoinRoom(String
		 * message) private void RoomOut(String message)
		 */
		private void default_JoinRoom() {
			RoomInfo r = room_vc.elementAt(0);
			r.add_User(this);
			send_Message("default_Room/ ");

			System.out.println("default_Room에 있는 인원 -> "
					+ r.room_user_vc.size());
		}

		private void default_RoomOut() {
			RoomInfo r = room_vc.elementAt(0);
			r.delete_User(this);

			System.out.println("default_Room에 있는 인원 -> "
					+ r.room_user_vc.size());
		}

		private void broadCast(String str) {// 전체 사용자에게 메세지 보내는 부분
			for (int i = 0; i < user_vc.size(); i++) {
				UserInfo u = user_vc.elementAt(i);
				u.send_Message(str);
			}
		}

		private void send_Message(String str) {// 문자열을 받아 전송
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private class RoomInfo {

		private String room_name = "";
		private Vector<UserInfo> room_user_vc = new Vector<UserInfo>();
		private final int room_MaxMember = 2;

		RoomInfo(String str, UserInfo u) {// 방 생성을 위한 생성자
			this.room_name = str;
			this.room_user_vc.add(u);
		}

		RoomInfo() {// 방에 들어가지 않았을 때 채팅하기 위한 방설정 생성자
			this.room_name = "default";
		}

		private void broadCast_Room(String str) {// 현재 방의 모든 사람에게 메세지를 보냄
			for (int i = 0; i < room_user_vc.size(); i++) {
				UserInfo u = room_user_vc.elementAt(i);
				u.send_Message(str);
			}
		}

		private void add_User(UserInfo u) {
			this.room_user_vc.add(u);
		}

		private void delete_User(UserInfo u) {
			this.room_user_vc.remove(u);
		}
	}

}
