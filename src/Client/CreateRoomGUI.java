package Client;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class CreateRoomGUI implements ActionListener, ItemListener, WindowListener{
	private JFrame frame;
	private JTextField roomName_tf, password_tf;
	private final ButtonGroup playTypeGroup = new ButtonGroup();
	private final ButtonGroup aiLevelGroup = new ButtonGroup();
	private JRadioButton pvpPlay_rbtn, aiLv1_rbtn, aiLv2_rbtn, aiLv3_rbtn;
	private JCheckBox secret_cb;
	
	private int aiLevel;
	private MainGUI mg;
	
	public CreateRoomGUI(MainGUI mg) {
		this.mg = mg;
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
					rbtnListener();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			private void rbtnListener() {
				pvpPlay_rbtn.addActionListener(CreateRoomGUI.this);
				aiLv1_rbtn.addActionListener(CreateRoomGUI.this);
				aiLv2_rbtn.addActionListener(CreateRoomGUI.this);
				aiLv3_rbtn.addActionListener(CreateRoomGUI.this);
				secret_cb.addItemListener(CreateRoomGUI.this);
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("방 만들기");
		frame.setBounds(100, 100, 400, 226);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);

		JLabel createInfo_lb = new JLabel("방 이름을 입력하세요");
		createInfo_lb.setBounds(12, 10, 237, 25);
		frame.getContentPane().add(createInfo_lb);

		roomName_tf = new JTextField();
		roomName_tf.setBounds(12, 45, 360, 25);
		frame.getContentPane().add(roomName_tf);
		roomName_tf.setColumns(10);

		JLabel password_lb = new JLabel("비밀번호를 입력하세요");
		password_lb.setBounds(12, 80, 237, 25);
		frame.getContentPane().add(password_lb);
		
		password_tf = new JTextField();
		password_tf.setColumns(10);
		password_tf.setBounds(12, 115, 360, 25);
		frame.getContentPane().add(password_tf);
		password_tf.setEnabled(false);
		
		final JRadioButton aiPlay_rbtn = new JRadioButton("AI 플레이");
		playTypeGroup.add(aiPlay_rbtn);
		aiPlay_rbtn.setBounds(12, 146, 121, 34);
		frame.getContentPane().add(aiPlay_rbtn);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(aiPlay_rbtn, popupMenu);

		aiLv3_rbtn = new JRadioButton("AI Lv 3");
		aiLevelGroup.add(aiLv3_rbtn);
		popupMenu.add(aiLv3_rbtn);

		aiLv2_rbtn = new JRadioButton("AI Lv 2");
		aiLevelGroup.add(aiLv2_rbtn);
		popupMenu.add(aiLv2_rbtn);

		aiLv1_rbtn = new JRadioButton("AI Lv 1");
		aiLevelGroup.add(aiLv1_rbtn);
		popupMenu.add(aiLv1_rbtn);

		pvpPlay_rbtn = new JRadioButton("유저들과의 대전");
		playTypeGroup.add(pvpPlay_rbtn);
		pvpPlay_rbtn.setBounds(137, 146, 121, 34);
		frame.getContentPane().add(pvpPlay_rbtn);
		
		secret_cb = new JCheckBox("비밀방");
		secret_cb.setBounds(257, 81, 115, 23);
		frame.getContentPane().add(secret_cb);
		
		JButton create_btn = new JButton("방 생성");
		create_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(roomName_tf.getText().replaceAll("\\s", "").isEmpty()){
					confirmMessage("방 이름을 확인해주세요");
				} else if(!(aiPlay_rbtn.isSelected()&&aiLv1_rbtn.isSelected()
						||aiLv2_rbtn.isSelected()||aiLv3_rbtn.isSelected())&&
						!pvpPlay_rbtn.isSelected()) {
					confirmMessage("방 설정을 확인해주세요");
				} else if(secret_cb.isSelected() && password_tf.getText().replaceAll("\\s", "").isEmpty()){
					confirmMessage("비밀번호를 입력해주세요");
				} else {
					if(secret_cb.isSelected())
						mg.createRoom(roomName_tf.getText() + "/" + aiLevel + "/" 
									+"Y"+"/"+password_tf.getText());
					else
						mg.createRoom(roomName_tf.getText() + "/" + aiLevel + "/" +"N");
					frame.dispose();
				}
			}
			private void confirmMessage(String s){
				JOptionPane.showMessageDialog(null, s, 
						"알림",JOptionPane.INFORMATION_MESSAGE);
			}
		});
		create_btn.setBounds(266, 150, 106, 26);
		frame.getContentPane().add(create_btn);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(this);
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		
		component.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (!e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == aiLv1_rbtn)
			aiLevel = 1;
		else if(e.getSource() == aiLv2_rbtn)
			aiLevel = 2;
		else if(e.getSource() == aiLv3_rbtn)
			aiLevel = 3;
		else if(e.getSource() == pvpPlay_rbtn)
			aiLevel = 9;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if(e.getStateChange() == ItemEvent.SELECTED)
			password_tf.setEnabled(true);
		else 
			password_tf.setEnabled(false);
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void windowClosed(WindowEvent e) {
		mg.getFrame().setEnabled(true);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void windowOpened(WindowEvent e) {
		mg.getFrame().setEnabled(false);
	}
}
