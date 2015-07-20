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
	// ���� ������ ����
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
		frame = new JFrame("���� ������");
		serverState = new JTextArea();
		serverOpen = new JButton("���� ����");
		serverClose = new JButton("���� ����");
		
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
		
		frame.setVisible(true);//ȭ�鿡 ���̰� ��
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == serverOpen){
			System.out.println("Server Open");
			network = new Network();//���� ���� �� ����� ���� ���
			serverOpen.setEnabled(false);
			serverClose.setEnabled(true);
		}
		else if(e.getSource() == serverClose){
			serverClose.setEnabled(false);
			serverOpen.setEnabled(true);		
			network.server_close();
			
			System.out.println("Server Close");
		}
	}//�׼� �̺�Ʈ ��
	
	public static void setText(String s){
		serverState.append(s);
		serverState.setCaretPosition(serverState.getDocument()
				.getLength());
	}
}
