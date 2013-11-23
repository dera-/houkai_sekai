package sim.edit;

import java.awt.Container;
import java.awt.Graphics;

import sim.game.Paramaters;
import sim.tools.Select;

public class SubMenu extends Select implements SceneForEditor {
	
	private static final int CREATE_ENEMY_LIST = 0;
	private static final int SAVE = 1;
	private static final int EXIT = 2;
	

	public SubMenu() {
		super(0.75, 0.1, 0.5, 0.2, 3, 3);
		createMenu();
	}

	private final void createMenu(){
		Names[CREATE_ENEMY_LIST] = "敵キャラリスト";
		Names[SAVE] = "セーブ";
		Names[EXIT] = "閉じる";
		for(int i=0;i<Permits.length;i++){
			Permits[i] = true;
		}
	}
	
	@Override
	public int pushEnter() {
		switch(kettei){
		case CREATE_ENEMY_LIST:
			return Paramaters.EDITOR_CREATE_ENEMY_LIST;
		case SAVE: 
			return Paramaters.EDITOR_STAGE_SAVE;
		case EXIT:
			return Paramaters.DELETE_NOW_SCENE;
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
