package sim.edit;

import java.awt.Container;
import java.awt.Graphics;

import sim.game.Paramaters;
import sim.tools.Select;

public class AllyPlaceMenu extends Select implements SceneForEditor{

	public AllyPlaceMenu() {
		super(0.5, 0.5, 0.2, 0.2, 2, 2);
		Names[0] = "座標移動";
		Names[1] = "座標削除";
		for(int i=0;i<Permits.length;i++){
			Permits[i] = true;
		}
	}

	@Override
	public int pushEnter() {
		switch(kettei){
		case 0:
			return Paramaters.EDITOR_MOVE_ALLY_PLACE;
		case 1:
			return Paramaters.EDITOR_DELETE_ALLY_PLACE;
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
		decideNowSelectedItem(x, y);
	}
	
}
