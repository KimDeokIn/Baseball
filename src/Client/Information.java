package Client;
import java.util.Vector;

class Information {
	// 그 외 변수들
	private Vector<String> user_list;
	private Vector<String> room_list;
	private Vector<String> room_user_list;
	private String myRoom;
	private String nickname;
	
	Information(){
		user_list = new Vector<String>();
		room_list = new Vector<String>();
		room_user_list = new Vector<String>();
		myRoom = "default";
		nickname = "";		
	}
	
	protected Vector<String> getUserList(){
		return user_list;
	}
	
	protected Vector<String> getRoomList(){
		return room_list;
	}
	
	protected Vector<String> getRoomUserList(){
		return room_user_list;
	}
	
	protected String getMyRoom(){
		return myRoom;
	}
	
	protected String getNick(){
		return nickname;
	}
	
	protected void setRoomName(String room){
		myRoom = room;
	}
	
	protected void setNick(String nick){
		nickname = nick;
	}
}
