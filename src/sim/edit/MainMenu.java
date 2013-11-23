package sim.edit;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;

import sim.game.Paramaters;
import sim.tools.Select;

public class MainMenu extends Select implements SceneForEditor{
	private static final int SELECT_ENEMY = 0;
	private static final int SET_ALLY_PLACE = 1;
	
	public MainMenu() {
		super(0.5, 0.5, 0.2, 0.2, 2, 2);
		createMenu();
	}
	
	private final void createMenu(){
		Names[SELECT_ENEMY] = "敵キャラの配置";
		Names[SET_ALLY_PLACE] = "味方位置の配置";
		for(int i=0;i<Permits.length;i++){
			Permits[i] = true;
		}
	}

	@Override
	public int pushEnter() {
		if(pointToNextScreen(cursorX, cursorY)){
			jumpNextScreen();
			return Paramaters.NON_EVENT;
		}
		switch(kettei){
		case SELECT_ENEMY:
			return Paramaters.EDITOR_SELECT_ENEMY;
		case SET_ALLY_PLACE:
			return Paramaters.EDITOR_SET_ALLY_PLACE;
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
		Draw(g,con);
	}

	@Override
	public void moveCursor(int x, int y) {
		decideNowSelectedItem(x,y);
	}

}
