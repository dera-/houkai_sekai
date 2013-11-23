package sim.edit;

import java.awt.Container;
import java.awt.Graphics;

import sim.game.Paramaters;
import sim.tools.Select;

public class DecideCharaNumsScene extends Select implements SceneForEditor{
	public DecideCharaNumsScene(int maxCharaNum) {
		super(0.6, 0.2, 0.6, 0.15, 10, maxCharaNum);
		createMenu();
	}
	
	private final void createMenu(){
		for(int i=0;i<Names.length;i++){
			Names[i] = (i+1)+"人";
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
		return Paramaters.EDITOR_SET_CHARA_NUMS;
	}

	@Override
	public int pushEscape() {
		return Paramaters.DELETE_NOW_SCENE;
	}

	@Override
	public void drawing(Graphics g, Container con) {
		Draw(g,con);
	}

	@Override
	public void moveCursor(int x, int y) {
		decideNowSelectedItem(x,y);
    	cursorX = x;
    	cursorY = y;
	}
	
	public int getCharaNum(){
		return kettei+1;
	}
}
