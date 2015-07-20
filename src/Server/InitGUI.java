package Server;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

class InitGUI implements ActionListener{
	// 서버 프레임 변수
	private JPanel contentPane;
	private static JTextArea serverState;
	private JButton serverOpen;
	private JButton serverClose;
	private JFrame frame;
	
	private Network network;
	
	InitGUI(){
		init();
		listener();
	}
	
	private void listener() {
		serverOpen.addActionListener(this);
		serverClose.addActionListener(this);
	}
	
	private void init(){
		frame = new JFrame("서버 구동기");
		serverState = new JTextArea();
		serverOpen = new JButton("서버 시작");
		serverClose = new JButton("서버 종료");
		
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 320);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(null);
		frame.setLocationRelativeTo(null);
		
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
		
		frame.setVisible(true);//화면에 보이게 함
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == serverOpen){
			System.out.println("Server Open");
			network = new Network();//소켓 생성 및 사용자 접속 대기
			serverOpen.setEnabled(false);
			serverClose.setEnabled(true);
		}
		else if(e.getSource() == serverClose){
			serverClose.setEnabled(false);
			serverOpen.setEnabled(true);		
			network.server_close();
			
			System.out.println("Server Close");
		}
	}//액션 이벤트 끝
	
	public static void setText(String s){
		serverState.append(s);
		serverState.setCaretPosition(serverState.getDocument()
				.getLength());
	}
}
