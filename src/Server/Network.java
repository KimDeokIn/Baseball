package Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JOptionPane;

class Network {
	// 네트워크 변수
	private ServerSocket server_socket;
	private Socket socket;
	private int port;
	private Vector<UserInfo> user_vc;
	private boolean login_success;//접속 성공 여부
	private Vector<RoomInfo> room_vc;
	private RoomInfo default_room; // 대기방을 위한 변수
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
			server_socket = new ServerSocket(port);//포트 사용
			{
				default_room = new RoomInfo();
				room_vc.add(default_room);
			}//대기 채팅방 생성
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "이미 사용중인 포트입니다", 
					"알림",JOptionPane.ERROR_MESSAGE);
		}
		
		if(server_socket != null){//정상적으로 포트가 열렸을 경우
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
			public void run() {// 스레드에서 처리할 일
				while (true) {
					try {
						InitGUI.setText("사용자 접속 대기중\n");				
						socket = server_socket.accept();// 사용자 접속 할 때까지 무한대기
						InitGUI.setText("사용자 접속!!\n");
						UserInfo user = new UserInfo(socket, thisNet);
						user.start();// 객체의 스레드 실행
					} catch (IOException e) {
						InitGUI.setText("서버 종료\n");
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
