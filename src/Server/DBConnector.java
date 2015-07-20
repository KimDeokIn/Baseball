package Server;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class DBConnector {
	// sql����
	private String url;
	private Connection con;
	private PreparedStatement ps;
	private String db_id;
	private String db_pw;
	private ResultSet rs_id;
	private ResultSet rs_pw;
	private ResultSet rs_nick;

	private String tmpRecord; // ���� �ӽ� ������ ���� ����
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
		tmpRecord = ""; // ���� �ӽ� ������ ���� ����
	}

	public String db_Connection(String Input_id, String Input_pw) {
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
			InitGUI.setText(Input_id + " ���� ���� ����\n");
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
			network.setLoginInfo(true);
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

	public void record_update(String item, String id) {
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
			
			System.out.println("db���� ����(�����κ�)!!!");
			e.printStackTrace();
		}
	}

	public String toTmpRecord() {
		return tmpRecord;
	}
}
