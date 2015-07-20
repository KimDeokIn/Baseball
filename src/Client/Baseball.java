package Client;
import java.util.HashSet;
import java.util.Random;

class Baseball {
	private int[] p_number; // �ڽ��� ����
	private int[] opp_number; // ������ ����
	private int aiLv; // ai����
	private Network net;
	private HashSet<Integer> knownCase;

	Baseball(int[] p_number, int aiLv, Network net) {
		this.p_number = p_number;
		opp_number = new int[3];
		this.aiLv = aiLv;
		this.net = net;
		if (this.aiLv == 9) { // �������� �÷����� ��쿡�� ���濡�� �ڽ��� ���ڸ� �������ִ� ��Ʈ��ŷ
			transmission();
		} else {
			knownCase = new HashSet<Integer>();
			setAINum();
		}
	}

	private void setAINum() {
		HashSet<Integer> input = new HashSet<Integer>();
		Random ran = new Random();
		int index = 0;
		while (input.size() != 3) {
			int number = ran.nextInt(10);
			if (!input.contains(number)) {
				input.add(number);
				opp_number[index++] = number;
			}
		}
		net.setGameTA("������ �����մϴ�!!\n");
		net.setGameTA("�÷��̾��� �������� �����մϴ�.\n");
		// aiLv == 1�� ��� �÷��̾��� ���ڸ� �ϳ��� ��
		if (aiLv == 2) {
			// �÷��̾��� ���� �ϳ��� ��
			knownCase.add(p_number[0]);
		} else if (aiLv == 3) {
			// �÷��̾��� ���� �� ���� ��
			knownCase.add(p_number[0]);
			knownCase.add(p_number[1]);
		}
	}

	private void transmission() {
		String message = "";
		for (int i = 0; i < p_number.length; i++)
			message += p_number[i];
		net.send_message("party'sNum/" + message + "/" + net.getMyRoom() + "/"
				+ net.getNick());
		net.gameFrameEnable(false);
	}

	public void setOppNum(String message) {
		String[] tmp = message.split("");
		for (int i = 0; i < tmp.length; i++)
			opp_number[i] = Integer.parseInt(tmp[i]);
	}
	
	public boolean compare(int[] inputNum) {
		int ball = 0, strike = 0;
		String numString = "";
		for (int i=0; i<inputNum.length; i++) {
			int num = inputNum[i];
			numString += num;
			for (int j=0; j<opp_number.length; j++) {
				if (num == opp_number[j]) {
					if (i == j)
						strike++;
					else
						ball++;
					break;
				}
			}
		}
		
		String ballCount = ball + "ball, " + strike + "strike\n";
		net.setGameTA(net.getNick()+"��: " + numString + "->" +ballCount);
		if (aiLv == 9)
			net.send_message("ballCount/"+net.getNick()+"/"+ballCount);
		if (strike == 3) {
			
			return true;
		}
		else {
			if (aiLv == 9) {
				// ������ �Է��� �� ���� ���� ������ ��Ȱ��ȭ ��Ű�� �κ�
			} else {
				net.gameFrameEnable(false);
				net.setGameTA("��ǻ���� ���Դϴ�.\n");
				aiPlay();
			}
			return false;
		}
	}

	private void aiPlay() {
		
	}
}