package sim.game.battle;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.util.ArrayList;

import sim.game.Paramaters;
import sim.game.data.CharaData;
import sim.tools.Select;

public class CharacterSelect extends Select implements Scene {
	private ArrayList<CharaData> Allys;  //BattleFieldオブジェクトのフィールドAllysと同期している
	private SkillMenu Menu_Skill;
	private int ParamNum;

	public CharacterSelect(ArrayList<CharaData> list){
		super(0.05,0.2,0.5,0.2,8,Paramaters.AllyList.size()+1);
		Allys=list;
		ParamNum=0;
		Menu_Skill = new SkillMenu(0.5,0.2,0.5,0.2);
		createItems();
	}
	
	/** 各項目の作成 */
	private final void createItems(){	
		int i;
		for(i=0;i<Names.length-1;i++){
			CharaData elem = Paramaters.AllyList.get(i);
			Names[i]=elem.getCharaName();
			int j;
			for(j=0;j<Allys.size();j++){
				CharaData C=Allys.get(j);
				if(elem==C)break;
			}
			Permits[i]=(j==Allys.size());
		}
		Names[i]="終了";
		Permits[i]=false;
	}
	
	//キャラの切り替え
	public boolean keyEventUp() {
		Moving(false);
		setNowCharaSkill();
		return false;
	}

	//キャラの切り替え
	public boolean keyEventDown() {
		Moving(true);
		setNowCharaSkill();
		return false;
	}

	//パラメーターを右にスライドするメソッド
	public boolean keyEventRight() {
		changeScene(true);
		return false;
	}

	//パラメーターを左にスライドするメソッド
	public boolean keyEventLeft() {
		changeScene(false);
		return false;
	}

	//キャラクターの決定
	public boolean keyEventEnter() {
		if(pointToNextScreen(cursorX, cursorY)){
			jumpNextScreen();
			return false;
		}
		else if(0<=kettei && kettei<Names.length-1) return true;
		else if(kettei==Names.length-1 && Permits[kettei]) return true;
		else return false;
	}
	
	/** コメント番号を取得するメソッド */
	public int getComment(){
		if(kettei==-1)return 40;
		else if(kettei==Names.length-1) return 42; 
		else if(0<=kettei && kettei<Names.length-1 && !Permits[kettei])return 41;
		return 40;
	}

	//準備シーンに戻る
	public boolean keyEventEscape() {
		// TODO Auto-generated method stub
		return true;
	}
	
	/** 選んだキャラクターを配置できるかどうか */
	public boolean isPermit(){
		return Permits[kettei];
	}
	
	/** 選んだキャラクターを返すメソッド */
	public CharaData getSelectedCharacter(){
		if(kettei==Names.length-1)return null;
		return Paramaters.AllyList.get(kettei);
	}
	
	/** 選んだキャラクターを配置可能(或いは不可能)にするメソッド */
	public void setPermit(boolean p){
		Permits[kettei]=p;
		okFinish();
	}
	
	
	@Override
	public void draw(Graphics g, Container con) {
		Tools.setTransmission(g,0,0,Paramaters.Width,Paramaters.Height,Color.black,(float)0.5);
		Draw(g,con);
		if(kettei==-1 || kettei==Names.length-1)return;
		CharaData chara = Paramaters.AllyList.get(kettei);
		switch(ParamNum){
		case Paramaters.BasicStatus:
			chara.drawStatus(g, con, (int)Math.round(0.4*Paramaters.Width), 0,
					(int)Math.round(0.6*Paramaters.Width), Paramaters.Height);
			break;
		case Paramaters.JobStatus:
			chara.drawAboutJob(g, con, (int)Math.round(0.4*Paramaters.Width), 0,
					(int)Math.round(0.6*Paramaters.Width), Paramaters.Height);
			break;
		case Paramaters.SkillStatus:
			Menu_Skill.Draw(g, con);
			break;
		}
	}
	
	/** ステータス画面の切り替え */
	private void changeScene(boolean ahead){
		if(kettei==-1 || kettei==Names.length-1)return;
		if(ahead){
			if(ParamNum>=Paramaters.LastNumber)ParamNum=Paramaters.FirstNumber;
			else ParamNum++;
		}
		else{
			if(ParamNum<=Paramaters.FirstNumber)ParamNum=Paramaters.LastNumber;
			else ParamNum--;
		}
		setNowCharaSkill();
	}
	
	/** 現在選んでいるキャラの技をMenu_Skillに渡すメソッド */
	private void setNowCharaSkill(){
		if(kettei==-1 || kettei==Names.length-1)return;
		if(ParamNum==Paramaters.SkillStatus){
			Menu_Skill.formatArrays(Paramaters.AllyList.get(kettei));
		}
	}
	
	/** 終了ボタンを選べるかどうかの判定を行うメソッド */
	private void okFinish(){
		for(int i=0;i<Permits.length-1;i++){
			if(!Permits[i]){
				Permits[Permits.length-1]=true;
				return;
			}
		}
		Permits[Permits.length-1]= false;
	}

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void moveCursor(int x, int y) {
    	cursorX = x;
    	cursorY = y;
		decideNowSelectedItem(x,y);
		setNowCharaSkill();
	}

}
