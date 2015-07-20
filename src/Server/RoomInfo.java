package Server;
import java.util.Vector;

class RoomInfo {

	private String room_name = "";
	private String password = "";
	private Vector<UserInfo> room_user_vc = new Vector<UserInfo>();
	private int aiLv;
	private int room_MaxMember;
	private int enterComp;

	RoomInfo(String str, UserInfo u, int aiLv, String password) {// �� ������ ���� ������
		this.room_name = str;
		this.room_user_vc.add(u);
		this.aiLv = aiLv;
		this.password = password;
		if(aiLv == 9){
			room_MaxMember = 2;
		} else {
			room_MaxMember = 1;
		}
	}

	RoomInfo() {// �濡 ���� �ʾ��� �� ä���ϱ� ���� �漳�� ������
		this.room_name = "default";
	}

	public void broadCast_Room(String str) {// ���� ���� ��� ������� �޼����� ����
		for (int i = 0; i < room_user_vc.size(); i++) {
			UserInfo u = room_user_vc.elementAt(i);
			u.send_Message(str);
		}
	}

	public Vector<UserInfo> getRoomUserVector() {
		return room_user_vc;
	}
	
	public void add_User(UserInfo u) {
		this.room_user_vc.add(u);
	}

	public void delete_User(UserInfo u) {
		this.room_user_vc.remove(u);
	}
	
	public String getRoomName() {
		return room_name;
	}
	
	public int getMax() {
		return room_MaxMember;
	}
	
	public int getAi_Lv(){
		return aiLv;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setEnterComp() {
		enterComp++;
	}
	
	public int getEnterComp() {
		return enterComp;
	}
}