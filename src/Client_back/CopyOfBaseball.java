package Client_back;
import java.util.HashSet;
import java.util.Scanner;

class CopyOfBaseball {
	private String inNumber; // 사용자 숫자
	private int[] numArr; // 숫자 하나하나 저장할 배열 
	private boolean aiPlay; // false: player, true: AIplayer
	private int lv; // ai 난이도 선택
	
	CopyOfBaseball(String inNumber, boolean aiPlay, int lv){
		this.inNumber = inNumber;
		this.aiPlay = aiPlay;
		this.lv = lv;
		numArr = new int[inNumber.length()];
		try {
			startUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void startUp() throws Exception{
		int size = numArr.length;
		HashSet<Integer> hs = new HashSet<Integer>();
		char[] number = inNumber.toCharArray();
		for(int i=0; i<size; i++){
			if(hs.contains(number[i]))
				throw new Exception("중복된 숫자 입력됨");
			numArr[i] = number[i]-48;
			hs.add((int)number[i]-48);
		}
		if(!aiPlay)
			gameStart();
		else
			ai_gameStart();
	}
	
	private void gameStart(){

	}
	private void ai_gameStart(){
		int[] comNumber = initNum();
		int strike = 0, ball = 0;
		while(strike != comNumber.length){
			
		}
	}
	private int[] initNum(){
		int numCount = 0; // 숫자가 몇 개 생성 되었는지 확인
		int[] tmpArr = new int[numArr.length];
		for(int i=0; i<tmpArr.length; i++)
			tmpArr[i] = -1;
		while(numCount != numArr.length){
			int tmp = (int)(Math.random()*9+0);
			if(!checkNum(tmpArr, tmp))
				tmpArr[numCount++] = tmp;
		}
		return tmpArr;
	}
	private boolean checkNum(int[] tmpArr, int num){
		int height = tmpArr.length-1;
		int low = 0, mid;
		while(height >= low){
			mid = (height + low) / 2;
			if(num == tmpArr[mid])
				return true;
			if(num < tmpArr[mid])
				height = mid - 1;
			else
				low = mid +1;
		}
		return false;
	}
	public static void main(String[] args){
		Scanner input = new Scanner(System.in);
		String number = input.next();
		CopyOfBaseball b = new CopyOfBaseball(number,true,1);
		input.close();
	}
}