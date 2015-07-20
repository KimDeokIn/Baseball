package Client;
import java.util.HashSet;
import java.util.Random;

class Baseball {
	private int[] p_number; // 자신의 숫자
	private int[] opp_number; // 상대방의 숫자
	private int aiLv; // ai레벨
	private Network net;
	private HashSet<Integer> knownCase;

	Baseball(int[] p_number, int aiLv, Network net) {
		this.p_number = p_number;
		opp_number = new int[3];
		this.aiLv = aiLv;
		this.net = net;
		if (this.aiLv == 9) { // 유저간의 플레이일 경우에만 상대방에게 자신의 숫자를 전달해주는 네트워킹
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
		net.setGameTA("게임을 시작합니다!!\n");
		net.setGameTA("플레이어의 선공으로 시작합니다.\n");
		// aiLv == 1일 경우 플레이어의 숫자를 하나도 모름
		if (aiLv == 2) {
			// 플레이어의 숫자 하나를 앎
			knownCase.add(p_number[0]);
		} else if (aiLv == 3) {
			// 플레이어의 숫자 두 개를 앎
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
		net.setGameTA(net.getNick()+"님: " + numString + "->" +ballCount);
		if (aiLv == 9)
			net.send_message("ballCount/"+net.getNick()+"/"+ballCount);
		if (strike == 3) {
			
			return true;
		}
		else {
			if (aiLv == 9) {
				// 상대방이 입력할 때 까지 게임 프레임 비활성화 시키는 부분
			} else {
				net.gameFrameEnable(false);
				net.setGameTA("컴퓨터의 턴입니다.\n");
				aiPlay();
			}
			return false;
		}
	}

	private void aiPlay() {
		
	}
}