package sim.edit;

import java.awt.Container;
import java.awt.Graphics;
import java.util.ArrayList;

import sim.game.Paramaters;
import sim.game.data.CharaData;
import sim.game.data.Map;
import sim.tools.Select;

public class CreateEnemyListScene extends Select implements HavingCharaData {
	private static ArrayList<CharaData> charaList;
	
	public CreateEnemyListScene(ArrayList<CharaData> charaAll) {
		super(0.35, 0.2, 0.6, 0.3, 10, charaAll.size());
		charaList = charaAll;
		createMenu();
	}
	
	private final void createMenu(){
		for(int i=0;i<Names.length;i++){
			Names[i] = charaList.get(i).getCharaName();
			Permits[i] = true;
		}
	}

	@Override
	public int pushEnter() {
		if(pointToNextScreen(cursorX, cursorY)){
			jumpNextScreen();
			return Paramaters.NON_EVENT;
		}
		if(kettei == Select.NOT_SELECT) return Paramaters.NON_EVENT;
		return Paramaters.EDITOR_CHARA_DECIDE_MENU;
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
    	cursorX = x;
    	cursorY = y;
	}

	@Override
	public CharaData getCharaData() {
		return charaList.get(kettei);
	}

}
