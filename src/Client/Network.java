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
	// ��Ʈ��ũ ����
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
			if (socket != null) {// ���������� ������ ����Ǿ��� ���
				connection();
			}
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "���� ����", "�˸�",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "���� ����", "�˸�",
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
			JOptionPane.showMessageDialog(null, "���� ����", "�˸�",
					JOptionPane.ERROR_MESSAGE);
		}// Stream ���� ��

		// ó�� ���ӽ� id,pw ����;
		send_message(input_id);
		send_message(input_pw);

		Thread th = new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						String msg = dis.readUTF();// �޼��� ����
						System.out.println("�����κ��� ���ŵ� �޼���: " + msg);
						inMessage(msg);
					} catch (IOException e) {
						try {
							os.close();
							is.close();
							dos.close();
							dis.close();
							socket.close();
							JOptionPane.showMessageDialog(null,
									"�������� ������ ���������ϴ�", "�˸�",
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

		if (protocol.equals("ThisUser")) {// UserList_list�� ����� �߰�
			super.getUserList().add(message);
			super.setNick(message);
			send_message("record/" + message);
		} else if (protocol.equals("NewUser")) {// ���ο� �����ڿ� ���� �޼������
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
			JOptionPane.showMessageDialog(null, "���̵�/��й�ȣ �� Ȯ�����ּ���", "�˸�",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (protocol.equals("already")) {
			JOptionPane.showMessageDialog(null, "�̹� �������Դϴ�", "�˸�",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (protocol.equals("Note")) {
			String note = st.nextToken();
			String[] buttons = {"ȸ��", "Ȯ��"};
			System.out.println(message + "�κ��� �� �޼���: " + note);
			int setNum = JOptionPane.showOptionDialog(null, note, message + "�����κ��� ����", 0
							,JOptionPane.INFORMATION_MESSAGE, null, buttons, buttons[0]);
			if (setNum == 0)
				super.note(message);
		} else if (protocol.equals("user_list_update")) {
			// UserList_list.updateUI();
			super.userListUpdate();
		} else if (protocol.equals("room_user_list_update")) {
			super.roomUserListUpdate();
		} else if (protocol.equals("CreateRoom")) {// ���� ������� ��
			super.setRoomName(message);
			String nick = st.nextToken();
			super.roomJoin_gui("CreateRoom", nick);
		} else if (protocol.equals("CreateRoomFail")) {// �游��⸦ �������� ��
			JOptionPane.showMessageDialog(null, "���� �̸��� ���� �����մϴ�", "�˸�",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (protocol.equals("New_Room")) {// ���ο� ���� ������� ��
			super.setChatTA("[�˸�]: ******" + message + "���� �����Ͽ����ϴ� ******\n");
			super.getRoomList().add(message);
			super.roomListUpdate();
		} else if (protocol.equals("Chatting")) {
			String msg = st.nextToken();
			super.setChatTA(message + ": " + msg + "\n");
		} else if (protocol.equals("OldRoom")) {
			super.getRoomList().add(message);
		} else if (protocol.equals("room_list_update")) {
			super.roomListUpdate();
		} else if (protocol.equals("JoinRoom")) {// �濡 ������ ��
			super.setRoomName(message);
			String nick = st.nextToken();
			super.roomJoin_gui("JoinRoom", nick);
			super.setChatTA("[�˸�]: ******" + message + "�濡 �����Ͽ����ϴ� ******\n");
		} else if (protocol.equals("FullRoom")) {
			JOptionPane.showMessageDialog(null, "�ο��� ����á���ϴ�.", "�˸�",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (protocol.equals("RoomOut")) {// �濡�� ���� ��
			super.setRoomName("default");
			super.roomOut_gui();
			super.setChatTA("[�˸�]: ****** �濡�� �����Ͽ����ϴ� ******\n");
		} else if (protocol.equals("Delete_Room")) {// �濡 �ο��� ���� ���� ������ �� ��Ͽ���
													// ����
			super.getRoomList().remove(message);
		} else if (protocol.equals("User_out")) {// ����ڰ� ������ �� ��Ͽ��� ����
			super.getUserList().remove(message);
		} else if (protocol.equals("User_room_out")) {// ����ڰ� �濡�� ������ �� �� �����ο�
														// ��Ͽ��� ����
			super.getRoomUserList().remove(message);
		} else if (protocol.equals("default_Room")) {
			super.setChatTA("[�˸�]: ****** ���ǿ� �����Ͽ����ϴ� ******\n");
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
			super.setRecordTA((win + lose) + "�� " + win + "�� " + lose + "��\n");
			if (win + lose != 0)
				super.setRecordTA("�·�: "
						+ String.format("%2f", ((double) win / (win + lose)))
						+ "%");
			else
				super.setRecordTA("�·�: 0.00%");
		} else if (protocol.equals("readySuccess")) {
			super.setStartBtn(true);
		} else if (protocol.equals("readyCancel")) {
			super.setStartBtn(false);
		} else if (protocol.equals("InsertPassword")) {
			String password = JOptionPane.showInputDialog("��й�ȣ�� �Է��ϼ���.");
			send_message("secretRoomJoin/" + message + "/" + password);
		} else if (protocol.equals("notEqualPW")) {
			JOptionPane.showMessageDialog(null, "��й�ȣ�� Ȯ���� �ּ���", "�˸�",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (protocol.equals("UserRecord")) {
			UserRecord ur = new UserRecord();
			int win = Integer.parseInt(st.nextToken());
			int lose = Integer.parseInt(st.nextToken());
			ur.setText(message);
			ur.setText((win + lose) + "�� " + win + "�� " + lose + "��");
			if (win + lose != 0)
				ur.setText("�·�: "
						+ String.format("%2f", ((double) win / (win + lose)))
						+ "%");
			else
				ur.setText("�·�: 0.00%");
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
	
	public void send_message(String str) {// �������� �޼����� ������ �κ�
		try {
			dos.writeUTF(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
