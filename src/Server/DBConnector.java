package Server;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class DBConnector {
	// sql변수
	private String url;
	private Connection con;
	private PreparedStatement ps;
	private String db_id;
	private String db_pw;
	private ResultSet rs_id;
	private ResultSet rs_pw;
	private ResultSet rs_nick;

	private String tmpRecord; // 전적 임시 저장을 위한 변수
	private Network network;

	DBConnector(Network network) {
		this.network = network;
		url = "jdbc:mysql://localhost:3306/baseball";
		con = null;
		ps = null;
		db_id = "root";
		db_pw = "backkom13";
		rs_id = null;
		rs_pw = null;
		rs_nick = null;
		tmpRecord = ""; // 전적 임시 저장을 위한 변수
	}

	public String db_Connection(String Input_id, String Input_pw) {
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
			InitGUI.setText(Input_id + " 계정 접속 실패\n");
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
			network.setLoginInfo(true);
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

	public void record_update(String item, String id) {
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
			
			System.out.println("db접근 실패(전적부분)!!!");
			e.printStackTrace();
		}
	}

	public String toTmpRecord() {
		return tmpRecord;
	}
}
