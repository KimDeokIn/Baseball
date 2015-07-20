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

	private boolean already_Login;// ������ �̹� ���ӵǾ��ִ����� ����
	private boolean roomCh;// ���� ���� �� �ִ����� ����
	private boolean ready;// ������ ������ �غ� ���ִ����� ����

	private StringTokenizer st;
	private Network network;

	UserInfo(Socket socket, Network network) {// ������
		connector = new DBConnector(network);
		roomCh = true;
		ready = false;
		this.socket = socket;
		this.network = network;
		userNetwork();
	}

	private synchronized void userNetwork() {// ��Ʈ��ũ ���� ����
		try {
			network.setLoginInfo(false);
			is = socket.getInputStream();
			dis = new DataInputStream(is);

			os = socket.getOutputStream();
			dos = new DataOutputStream(os);

			input_id = dis.readUTF();// Ŭ���̾�Ʈ���� ���̵� ����
			InitGUI.setText(input_id + "���� ���� �õ�");
			input_pw = dis.readUTF();// Ŭ���̾�Ʈ���� ��й�ȣ ����
			InitGUI.setText(input_pw + "->��й�ȣ �Է�");
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
					// ���� ����ڵ鿡�� ���ο� ����� �˸�
					broadCast("NewUser/" + nickname);
					// �ڽſ��� ���� ����ڸ� �˸�
					for (int i = 0; i < network.getUserVector().size(); i++) {
						UserInfo u = network.getUserVector().elementAt(i);
						send_Message("OldUser/" + u.nickname);
					}
					// ���� ����� Vector�� �߰� ��Ű�� ���� �޼���
					send_Message("ThisUser/" + nickname);
					{
						network.getDefault().add_User(this);
						send_Message("default_Room/ ");
					}
					// �ڽſ��� ���� �� ����� �˸�
					for (int i = 1; i < network.getRoomVector().size(); i++) {
						RoomInfo r = network.getRoomVector().elementAt(i);
						send_Message("OldRoom/" + r.getRoomName());
					}
					send_Message("room_list_update/ ");

					network.getUserVector().add(this); // ����ڿ��� �˸� �� Vector�� �߰�
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
				InitGUI.setText(input_id + "����ڷκ��� ���� �޼���" + msg + "\n");
				inMessage(msg);
			} catch (IOException e) {// Ŭ���̾�Ʈ�� ������ ������ �� ����ó��
				InitGUI.setText(nickname + " ����� ���� ����\n");
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
					System.out.println("������ ����� ������ ���� Ŭ���̾�Ʈ ����");
				}
				break;
			}
		}
	}

	private synchronized void inMessage(String str) {// Ŭ���̾�Ʈ�κ��� ������ �޼��� ó��
		st = new StringTokenizer(str, "/");
		String protocol = st.nextToken();
		final String message = st.nextToken();

		System.out.println("��������: " + protocol);
		System.out.println("�޼���: " + message);

		if (protocol.equals("Note")) {
			// protocol = Note
			// message = user2@����
			
			String note = st.nextToken();

			System.out.println("�޴� ���: " + message);
			System.out.println("���� ����: " + note);

			// Vector���� ����ڸ� ã�� �޼��� ����
			for (int i = 0; i < network.getUserVector().size(); i++) {
				UserInfo u = network.getUserVector().elementAt(i);
				if (u.nickname.equals(message)) {
					u.send_Message("Note/" + nickname + "/" + note);
					// protocol=Note / ���� ����=Nickname / ��������=note
				}
			}
		} else if (protocol.equals("CreateRoom")) {
			// 1. ���� ���� ���� �����ϴ��� Ȯ��
			for (int i = 0; i < network.getRoomVector().size(); i++) {
				RoomInfo r = network.getRoomVector().elementAt(i);
				if (r.getRoomName().equals(message)) {// ������� �ϴ� ���� �̹� ������ ���
					send_Message("CreateRoomFail/ok");
					roomCh = false;
					break;
				}
			}// for�� ��
			if (roomCh) {// ���� ���� �� ���� ��
				int aiLv = Integer.parseInt(st.nextToken());
				String secret = st.nextToken();
				String password = "";
				if (secret.equals("Y"))
					password = st.nextToken();
				RoomInfo new_room = new RoomInfo(message, this, aiLv, password);
				network.getRoomVector().add(new_room);// ��ü �� Vector�� �� �߰�
				send_Message("CreateRoom/" + message + "/" + this.nickname);

				broadCast("New_Room/" + message);
				default_RoomOut();// ����ä�ù濡�� ����
				send_Message("NewLeader/"+new_room.getMax());// �� ����� ���� ���� ������ ����
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
						if (r.getRoomName().equals(message)) {// ��� ���� ã���� ��
							r.broadCast_Room("Chatting/" + nickname + "/" + msg);
						}
					}
				}
			}
		} else if (protocol.equals("JoinRoom")) {// �濡 �� ��
			for (int i = 1; i < network.getRoomVector().size(); i++) {
				RoomInfo r = network.getRoomVector().elementAt(i);
				if (r.getRoomName().equals(message)) { // �� ���� ã����
					if (!r.getPassword().equals("")) { // �н����尡 ������ ���
						send_Message("InsertPassword/"+message);
					} else { // �н����尡 �������� ���� ���
						joinRoom(r, message);
					}
				}
			}
		} else if (protocol.equals("RoomOut")) {// �濡�� ���� ��
			for (int i = 1; i < network.getRoomVector().size(); i++) {
				RoomInfo r = network.getRoomVector().elementAt(i);
				if (r.getRoomName().equals(message)) {
					r.broadCast_Room("Chatting/[�˸�]/******" + nickname
							+ "���� �����ϼ̽��ϴ� ******");
					r.delete_User(this);
					this.ready = false;
					if (r.getRoomUserVector().size() == 0) {// �� �ο��� ���� ��(0��)
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
						// ���� ���� ���� ������ ���� ������ ����
						u.send_Message("NewLeader/"+r.getMax());
						// ���� ��ư Ȱ��ȭ�� ���� �޼���
					}
					default_JoinRoom();// ����ä�ù濡 ����
				}
			}
		} else if (protocol.equals("SetReady")) {// Ŭ���̾�Ʈ���� �غ� ��ư�� ������ ���
			String nick = st.nextToken();
			for (int i = 1; i < network.getRoomVector().size(); i++) {
				RoomInfo r = network.getRoomVector().elementAt(i);
				if (r.getRoomName().equals(message)) {
					if (r.getRoomUserVector().get(1).ready) {
						r.getRoomUserVector().get(1).ready = false;
						r.broadCast_Room("Chatting/[�˸�]/" + nick
								+ "�Բ��� �غ� ��Ҹ� �ϼ̽��ϴ�.");
						r.getRoomUserVector().get(0)
								.send_Message("readyCancel/ ");
					} else {
						r.getRoomUserVector().get(1).ready = true;
						r.broadCast_Room("Chatting/[�˸�]/" + nick + "�� �غ� �Ϸ�!!");
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
		} else if (protocol.equals("record")) {// ������ �����ϱ� ���� ����
			connector.record_update("nick_name", message);
			send_Message("record/" + message + "/" + connector.toTmpRecord());
		} else if (protocol.equals("secretRoomJoin")) {
			String password = st.nextToken();
			for (int i = 1; i < network.getRoomVector().size(); i++) { // �� �� ã��
				RoomInfo r = network.getRoomVector().elementAt(i);
				if (r.getRoomName().equals(message)) { // �� ���� ã����
					if (r.getPassword().equals(password)) { // �Էµ� ��й�ȣ�� ��ġ�� ���
						joinRoom(r, message);
					} else { // ��ġ���� ���� ���
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
									System.out.println(j+"�� �� �û��մϴ�");
									r.broadCast_Room("GameNotice/"+j+"�� �� �����մϴ�\n");
									Thread.sleep(DELAY);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							r.broadCast_Room("GameNotice/"+"���� ����!!\n");
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
			 * ��������: party'sNum
			 * �޼���: ���濡�� ���� �ڽ��� ����
			 * �߰�1: ���̸�
			 * �߰�2: �ڽ��� �г��� ( �ڽ��� �ƴ� �ٸ� �������Ը� �����ϱ� ���� �г��� )
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
			 * ��������: ballCount
			 * �޼���: ���� �����
			 * �߰�1: ����ڰ� �Է��� �� ī��Ʈ
			 */
		}
	}
	
	private void joinRoom(RoomInfo r, String message) {
		if (r.getRoomUserVector().size() < r.getMax()) {// �� �ο��� 2�� �̸��� ��
			// ���ο� ����ڸ� �˸�
			r.broadCast_Room("Chatting/[�˸�]/******" + nickname
					+ "���� �����ϼ̽��ϴ� ******");
			default_RoomOut();// ����ä�ù濡�� ����

			// �� ���� �ִ� ���� ����ڵ鿡�� ���ο� ����� �˸�
			r.broadCast_Room("RoomNewUser/" + this.nickname);
			// �ڽſ��� �� ���� �ִ� ���� ����ڸ� �˸�
			for (int j = 0; j < r.getRoomUserVector().size(); j++) {
				UserInfo u = r.getRoomUserVector().elementAt(j);
				send_Message("RoomOldUser/" + u.nickname);
			}
			// ����Ʈ�� �ڱ� �ڽ��� �߰�
			send_Message("JoinRoom/" + message + "/"
					+ this.nickname);
			// ����Ʈ �ֽ�ȭ
			r.broadCast_Room("room_user_list_update/ ");

			System.out.println(r.getRoomUserVector().size());

			// ���� ã���� ����� �߰�
			r.add_User(this);
		} else {// �� �ο��� 2���� ��
			send_Message("FullRoom/ ");
		}
	}

	/*
	 * default(����ä��)������ �ڵ� ����, ������ ���� �޼��� private void JoinRoom(String message)
	 * private void RoomOut(String message)
	 */
	private void default_JoinRoom() {
		RoomInfo r = network.getRoomVector().elementAt(0);
		r.add_User(this);
		send_Message("default_Room/ ");

		System.out.println("default_Room�� �ִ� �ο� -> "
				+ r.getRoomUserVector().size());
	}

	private void default_RoomOut() {
		RoomInfo r = network.getRoomVector().elementAt(0);
		r.delete_User(this);

		System.out.println("default_Room�� �ִ� �ο� -> "
				+ r.getRoomUserVector().size());
	}

	private void broadCast(String str) {// ��ü ����ڿ��� �޼��� ������ �κ�
		for (int i = 0; i < network.getUserVector().size(); i++) {
			UserInfo u = network.getUserVector().elementAt(i);
			u.send_Message(str);
		}
	}

	public void send_Message(String str) {// ���ڿ��� �޾� ����
		try {
			dos.writeUTF(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
