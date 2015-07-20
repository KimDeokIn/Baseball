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

	// ���� ������ ����
	private JPanel contentPane;
	private JTextArea serverState = new JTextArea();
	private JButton serverOpen = new JButton("���� ����");
	private JButton serverClose = new JButton("���� ����");

	// ��Ʈ��ũ ����
	private ServerSocket server_socket;
	private Socket socket;
	private int port = 8888;
	private Vector<UserInfo> user_vc = new Vector<UserInfo>();
	private boolean login_success = false;
	private Vector<RoomInfo> room_vc = new Vector<RoomInfo>();
	private RoomInfo default_room; // ������ ���� ����
	private String tmpRecord = ""; // ���� �ӽ� ������ ���� ����

	// sql����
	private String url = "jdbc:mysql://localhost:3306/baseball";
	private Connection con = null;
	private PreparedStatement ps = null;
	private String db_id = "root";
	private String db_pw = "backkom13";
	private ResultSet rs_id = null;
	private ResultSet rs_pw = null;
	private ResultSet rs_nick = null;

	private StringTokenizer st;

	Server() {// ������
		init();// ȭ�� ���� �޼���
		listener();// ������ ���� �޼���
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

		this.setVisible(true);// ȭ�鿡 ���̰� ��
	}

	private void server_start() {
		try {
			server_socket = new ServerSocket(port);// ��Ʈ ���
			{
				default_room = new RoomInfo();
				room_vc.add(default_room);
			}// ��� ä�ù� ����
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "�̹� ������� ��Ʈ�Դϴ�", "�˸�",
					JOptionPane.ERROR_MESSAGE);
		}

		if (server_socket != null) {// ���������� ��Ʈ�� ������ ���
			connection();
		}
	}

	private void connection() {

		Thread th = new Thread(new Runnable() {
			public void run() {// �����忡�� ó���� ��
				while (true) {
					try {
						serverState.append("����� ���� �����\n");
						serverState.setCaretPosition(serverState.getDocument()
								.getLength());
						socket = server_socket.accept();// ����� ���� �� ������ ���Ѵ��
						serverState.append("����� ����!!\n");
						serverState.setCaretPosition(serverState.getDocument()
								.getLength());

						UserInfo user = new UserInfo(socket);
						user.start();// ��ü�� ������ ����
					} catch (IOException e) {
						serverState.append("���� ����\n");
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
			System.out.println("����̹� �˻� ����");
		} catch (ClassNotFoundException e) {
			System.out.println("����̹� �˻� ����!!");
		}

		try {
			con = DriverManager.getConnection(url, db_id, db_pw);
			System.out.println("db���� ����");
		} catch (SQLException e) {
			System.out.println("db���� ����!!!");
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

			// Ŭ���̾�Ʈ���� �޾ƿ� ���̵� db�� ��������� ��� �н����� ��
			if (Input_id.equals(rs_id.getString(1)))
				return compare_account(rs_pw.getString(1), stringMD5(Input_pw),
						Input_id);

		} catch (SQLException e) {
			serverState.append(Input_id + " ���� ���� ����\n");
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
			System.out.println("�α��� ����!");
			login_success = true;
			ps = con.prepareStatement("select nick_name from xe_member "
					+ "where user_id=\"" + Input_id + "\"");
			rs_nick = ps.executeQuery();
			rs_nick.next();
			return rs_nick.getString(1);
		} else {
			System.out.println("���̵� �Ǵ� ��й�ȣ�� Ȯ���ϼ���");
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
				System.out.println("�ϴ� ����");
				// ������ Null������ ����Ǿ� �ִ� ��� 0������ �ʱ�ȭ �����ִ� �۾�
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
			System.out.println("db���� ����!!!");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Server();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == serverOpen) {
			System.out.println("Server Open");
			server_start();// ���� ���� �� ����� ���� ���
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
	}// �׼� �̺�Ʈ ��

	private class UserInfo extends Thread {
		private OutputStream os;
		private InputStream is;
		private DataOutputStream dos;
		private DataInputStream dis;

		private Socket user_socket;
		private String input_id = "";
		private String input_pw = "";
		private String nickname = "";

		private boolean already_Login;// ������ �̹� ���ӵǾ��ִ����� ����
		private boolean roomCh = true;// ���� ���� �� �ִ����� ����
		private boolean ready = false;// ������ ������ �غ� ���ִ����� ����

		UserInfo(Socket soc) {// ������
			this.user_socket = soc;
			userNetwork();
		}

		private void userNetwork() {// ��Ʈ��ũ ���� ����
			try {
				login_success = false;
				is = user_socket.getInputStream();
				dis = new DataInputStream(is);

				os = user_socket.getOutputStream();
				dos = new DataOutputStream(os);

				input_id = dis.readUTF();// Ŭ���̾�Ʈ���� ���̵� ����
				serverState.append(input_id + "���� ���� �õ�");
				serverState.setCaretPosition(serverState.getDocument()
						.getLength());
				input_pw = dis.readUTF();// Ŭ���̾�Ʈ���� ��й�ȣ ����
				serverState.append(input_pw + "->��й�ȣ �Է�");
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
						// ���� ����ڵ鿡�� ���ο� ����� �˸�
						broadCast("NewUser/" + nickname);
						// �ڽſ��� ���� ����ڸ� �˸�
						for (int i = 0; i < user_vc.size(); i++) {
							UserInfo u = user_vc.elementAt(i);
							send_Message("OldUser/" + u.nickname);
						}
						// ���� ����� Vector�� �߰� ��Ű�� ���� �޼���
						send_Message("ThisUser/" + nickname);
						{
							default_room.add_User(this);
							send_Message("default_Room/ ");
						}
						// �ڽſ��� ���� �� ����� �˸�
						for (int i = 1; i < room_vc.size(); i++) {
							RoomInfo r = room_vc.elementAt(i);
							send_Message("OldRoom/" + r.room_name);
						}
						send_Message("room_list_update/ ");

						user_vc.add(this); // ����ڿ��� �˸� �� Vector�� �߰�
						send_Message("true/ ");
						broadCast("user_list_update/ ");
					}
				} else {
					send_Message("false/ ");
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Stream ���� ����", "�˸�",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		public void run() {// Thread�� ó���� ����
			while (true) {
				try {
					String msg = dis.readUTF();
					serverState
							.append(input_id + "����ڷκ��� ���� �޼���" + msg + "\n");
					serverState.setCaretPosition(serverState.getDocument()
							.getLength());
					inMessage(msg);
				} catch (IOException e) {// Ŭ���̾�Ʈ�� ������ ������ �� ����ó��
					serverState.append(nickname + " ����� ���� ����\n");
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

		private void inMessage(String str) {// Ŭ���̾�Ʈ�κ��� ������ �޼��� ó��
			st = new StringTokenizer(str, "/");
			String protocol = st.nextToken();
			String message = st.nextToken();

			System.out.println("��������: " + protocol);
			System.out.println("�޼���: " + message);

			if (protocol.equals("Note")) {
				// protocol = Note
				// message = user2@����

				String note = st.nextToken();

				System.out.println("�޴� ���: " + message);
				System.out.println("���� ����: " + note);

				// Vector���� ����ڸ� ã�� �޼��� ����
				for (int i = 0; i < user_vc.size(); i++) {
					UserInfo u = user_vc.elementAt(i);
					if (u.nickname.equals(message)) {
						u.send_Message("Note/" + nickname + "/" + note);
						// protocol=Note / ���� ����=Nickname / ��������=note
					}
				}
			} else if (protocol.equals("CreateRoom")) {
				// 1. ���� ���� ���� �����ϴ��� Ȯ��
				for (int i = 0; i < room_vc.size(); i++) {
					RoomInfo r = room_vc.elementAt(i);
					if (r.room_name.equals(message)) {// ������� �ϴ� ���� �̹� ������ ���
						send_Message("CreateRoomFail/ok");
						roomCh = false;
						break;
					}
				}// for�� ��
				if (roomCh) {// ���� ���� �� ���� ��
					RoomInfo new_room = new RoomInfo(message, this);
					room_vc.add(new_room);// ��ü �� Vector�� �� �߰�
					send_Message("CreateRoom/" + message + "/" + this.nickname);

					broadCast("New_Room/" + message);
					default_RoomOut();// ����ä�ù濡�� ����
					send_Message("NewLeader/ ");// �� ����� ���� ���� ������ ����
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
							if (r.room_name.equals(message)) {// ��� ���� ã���� ��
								r.broadCast_Room("Chatting/" + nickname + "/"
										+ msg);
							}
						}
					}
				}
			} else if (protocol.equals("JoinRoom")) {// �濡 �� ��
				for (int i = 1; i < room_vc.size(); i++) {
					RoomInfo r = room_vc.elementAt(i);
					if (r.room_user_vc.size() < r.room_MaxMember) {// �� �ο��� 2��
																	// �̸��� ��
						if (r.room_name.equals(message)) {
							// ���ο� ����ڸ� �˸�
							r.broadCast_Room("Chatting/[�˸�]/******" + nickname
									+ "���� �����ϼ̽��ϴ� ******");

							if (r.room_name.equals("Default"))
								send_Message("default_Room/ ");
							else {
								default_RoomOut();// ����ä�ù濡�� ����

								// �� ���� �ִ� ���� ����ڵ鿡�� ���ο� ����� �˸�
								r.broadCast_Room("RoomNewUser/" + this.nickname);
								// �ڽſ��� �� ���� �ִ� ���� ����ڸ� �˸�
								for (int j = 0; j < r.room_user_vc.size(); j++) {
									UserInfo u = r.room_user_vc.elementAt(j);
									send_Message("RoomOldUser/" + u.nickname);
								}
								// ����Ʈ�� �ڱ� �ڽ��� �߰�
								send_Message("JoinRoom/" + message + "/"
										+ this.nickname);
								// ����Ʈ �ֽ�ȭ
								r.broadCast_Room("room_user_list_update/ ");

								System.out.println(r.room_user_vc.size());
							}
							// ���� ã���� ����� �߰�
							r.add_User(this);
						}
					} else {// �� �ο��� 2���� ��
						send_Message("FullRoom/ ");
					}
				}
			} else if (protocol.equals("RoomOut")) {// �濡�� ���� ��
				for (int i = 1; i < room_vc.size(); i++) {
					RoomInfo r = room_vc.elementAt(i);
					if (r.room_name.equals(message)) {
						r.broadCast_Room("Chatting/[�˸�]/******" + nickname
								+ "���� �����ϼ̽��ϴ� ******");
						r.delete_User(this);
						if (r.room_user_vc.size() == 0) {// �� �ο��� ���� ��(0��)
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
							// ���� ���� ���� ������ ���� ������ ����
							u.send_Message("NewLeader/ ");
							// ���� ��ư Ȱ��ȭ�� ���� �޼���
						}
						default_JoinRoom();// ����ä�ù濡 ����
					}
				}
			} else if (protocol.equals("SetReady")) {// Ŭ���̾�Ʈ���� �غ� ��ư�� ������ ���
				String nick = st.nextToken();
				for (int i = 1; i < room_vc.size(); i++) {
					RoomInfo r = room_vc.elementAt(i);
					if (r.room_name.equals(message)) {
						if (r.room_user_vc.get(1).ready) {
							r.room_user_vc.get(1).ready = false;
							r.broadCast_Room("Chatting/[�˸�]/" + nick
									+ "�Բ��� �غ� ��Ҹ� �ϼ̽��ϴ�.");
							r.room_user_vc.get(0).send_Message("readyCancel/ ");
						} else {
							r.room_user_vc.get(1).ready = true;
							r.broadCast_Room("Chatting/[�˸�]/" + nick
									+ "�� �غ� �Ϸ�!!");
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
			} else if (protocol.equals("record")) {// ������ �����ϱ� ���� ����
				record_update("nick_name", message);
				send_Message("record/" + message + "/" + tmpRecord);
			}
		}

		/*
		 * default(����ä��)������ �ڵ� ����, ������ ���� �޼��� private void JoinRoom(String
		 * message) private void RoomOut(String message)
		 */
		private void default_JoinRoom() {
			RoomInfo r = room_vc.elementAt(0);
			r.add_User(this);
			send_Message("default_Room/ ");

			System.out.println("default_Room�� �ִ� �ο� -> "
					+ r.room_user_vc.size());
		}

		private void default_RoomOut() {
			RoomInfo r = room_vc.elementAt(0);
			r.delete_User(this);

			System.out.println("default_Room�� �ִ� �ο� -> "
					+ r.room_user_vc.size());
		}

		private void broadCast(String str) {// ��ü ����ڿ��� �޼��� ������ �κ�
			for (int i = 0; i < user_vc.size(); i++) {
				UserInfo u = user_vc.elementAt(i);
				u.send_Message(str);
			}
		}

		private void send_Message(String str) {// ���ڿ��� �޾� ����
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

		RoomInfo(String str, UserInfo u) {// �� ������ ���� ������
			this.room_name = str;
			this.room_user_vc.add(u);
		}

		RoomInfo() {// �濡 ���� �ʾ��� �� ä���ϱ� ���� �漳�� ������
			this.room_name = "default";
		}

		private void broadCast_Room(String str) {// ���� ���� ��� ������� �޼����� ����
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
