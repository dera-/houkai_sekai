package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import sim.game.data.CharaData;
import sim.tools.Select;

/** キャラクターを新規登録する画面 */
public class FirstScene extends Select implements GlobalScene {
	private static final int Numbers=3; //登録できるキャラクターの数
	private CharaData[] CharaArrays = new CharaData[Numbers];
	
	public FirstScene(){
		super(0.4,0.3,0.5,0.2,Numbers+1,Numbers+1);
		for(int i=0;i<Numbers+1;i++){
			if(i==Numbers)Names[i]="終了";
			else Names[i]="未登録";
			Permits[i]=true;
		}
	}
	
	/** 項目の名前の変更 */
	private void changeNames(){
		for(int i=0;i<CharaArrays.length;i++){
			if(CharaArrays[i]==null)Names[i]="未登録";
			else Names[i]=CharaArrays[i].getCharaName();
		}
	}
	
	@Override
	public void drawing(Graphics g, Container con) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
		g.setColor(Color.white);
		String Message="「天空の暁」に入隊する新兵の登録";
		int font=(int)Math.round(0.075*Paramaters.Height);
		g.setFont( new Font("明朝体",Font.BOLD,font) );
		int startX=(Paramaters.Width-(font+1)*Message.length())/2;
		g.drawString(Message,startX,(int)Math.round(Pt.y-0.05*Paramaters.Height));
		Draw(g,con);
	}

	@Override
	public int pushDown() {
		Moving(true);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushEnter() {
		if(0<=kettei && kettei<Numbers)return Paramaters.FIRST_REGISTER_SCENE_ONE;
		else if(kettei==Numbers){
			boolean NoRegister=true;
			for(CharaData C: CharaArrays){
				if(C!=null){
					NoRegister=false;
					C.upJobLV(1);  //現在の職業のレベルを1つ上げる
					C.createSkillList();  //職業履歴に対応した技をセットする
				}
			}
			if(!NoRegister)return Paramaters.FIRST_REGISTER_FINISH;
		}
		return Paramaters.NON_EVENT;
	}
	
	/** 現在選んでいるキャラのデータを返すメソッド */
	public CharaData getCharaData(){
		if(0<=kettei && kettei<Numbers) return CharaArrays[kettei];
		else return null;
	}

	@Override
	public int pushEscape() {
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushLeft() {
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushRight() {
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushUp() {
		Moving(false);
		return Paramaters.NON_EVENT;
	}

	@Override
	public void running() {
		changeNames();
	}
	
	/** 作成したキャラをリストにして返すメソッド */
	public ArrayList<CharaData> getNewCharaList(){
		ArrayList<CharaData> list = new ArrayList<CharaData>();
		for(CharaData C : CharaArrays){
			if(C!=null)list.add(C);
		}
		return list;
	}
	
	/** 現在指しているインデックスに引数のキャラクターを代入する */
	public void setCharacter(CharaData C){
		CharaArrays[kettei]=C;
	}

    @Override
    public void moveCursor(int x, int y) {
        decideNowSelectedItem(x,y);
    }

}
