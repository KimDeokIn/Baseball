package Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JOptionPane;

class Network {
	// ��Ʈ��ũ ����
	private ServerSocket server_socket;
	private Socket socket;
	private int port;
	private Vector<UserInfo> user_vc;
	private boolean login_success;//���� ���� ����
	private Vector<RoomInfo> room_vc;
	private RoomInfo default_room; // ������ ���� ����
	private Network thisNet;
	
	Network(){
		port = 8888;
		login_success = false;
		user_vc = new Vector<UserInfo>();
		room_vc = new Vector<RoomInfo>();
		server_start();
	}
	
	private void server_start(){
		try {
			server_socket = new ServerSocket(port);//��Ʈ ���
			{
				default_room = new RoomInfo();
				room_vc.add(default_room);
			}//��� ä�ù� ����
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "�̹� ������� ��Ʈ�Դϴ�", 
					"�˸�",JOptionPane.ERROR_MESSAGE);
		}
		
		if(server_socket != null){//���������� ��Ʈ�� ������ ���
			connection();
		}
	}

	public void server_close(){
		close();
	}
	
	private void close(){
		try {
			server_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		user_vc.removeAllElements();
		room_vc.removeAllElements();
	}
	
	private synchronized void connection() {
		thisNet = this;
		Thread th = new Thread(new Runnable() {
			public void run() {// �����忡�� ó���� ��
				while (true) {
					try {
						InitGUI.setText("����� ���� �����\n");				
						socket = server_socket.accept();// ����� ���� �� ������ ���Ѵ��
						InitGUI.setText("����� ����!!\n");
						UserInfo user = new UserInfo(socket, thisNet);
						user.start();// ��ü�� ������ ����
					} catch (IOException e) {
						InitGUI.setText("���� ����\n");
						break;
					}
				}
			}
		});
		th.start();
	}
	
	public boolean getLoginInfo(){
		return login_success;
	}
	
	public void setLoginInfo(boolean loginInfo){
		login_success = loginInfo;
	}
	
	public Vector<UserInfo> getUserVector(){
		return user_vc;
	}
	
	public Vector<RoomInfo> getRoomVector(){
		return room_vc;
	}
	
	public RoomInfo getDefault(){
		return default_room;
	}
}
