package sim.edit;

import java.awt.Container;
import java.awt.Graphics;

import sim.game.Paramaters;
import sim.game.data.CharaData;
import sim.tools.Select;

public class DecidedCharaMenu extends Select implements HavingCharaData{
	private CharaData charaData;
	private int charaNum = 1;
	private static final int CHARA_LEVEL = 0;
	private static final int CHARA_NUMS =1;
	private static final int CHARA_FINISH =2;
	
	public DecidedCharaMenu(CharaData chara){
		super(0.6, 0.3, 0.3, 0.15, 3, 3);
		createMenu();
		charaData = chara;
	}
	
	private final void createMenu(){
		Names[CHARA_LEVEL] = "LEVEL";
		Names[CHARA_NUMS] = "キャラ数";
		Names[CHARA_FINISH]="完了";
		for(int i=0;i<Permits.length;i++){
			Permits[i] = true;
		}
	}

	@Override
	public int pushEnter() {
		switch(kettei){
		case CHARA_LEVEL:
			return Paramaters.EDITOR_SELECT_CHARA_LEVEL;
		case CHARA_NUMS:
			return Paramaters.EDITOR_SELECT_CHARA_NUMS;
		case CHARA_FINISH:
			return Paramaters.EDITOR_CREATED_ENEMY;
		default:
			return Paramaters.NON_EVENT;
		}
	}

	@Override
	public int pushEscape() {
		return Paramaters.DELETE_NOW_SCENE;
	}

	@Override
	public void drawing(Graphics g, Container con) {
		Draw(g, con);
	}

	@Override
	public void moveCursor(int x, int y) {
		decideNowSelectedItem(x,y);
	}

	@Override
	public CharaData getCharaData() {
		return new CharaData(charaData, false);
	}

	public int getCharaNums() {
		return charaNum;
	}
	
	public void setCharaNums(int num) {
		charaNum = num;
	}

}
