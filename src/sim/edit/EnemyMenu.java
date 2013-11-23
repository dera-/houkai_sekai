package sim.edit;

import java.awt.Container;
import java.awt.Graphics;

import sim.game.Paramaters;
import sim.game.data.CharaData;
import sim.tools.Select;

/**
 * マップ上で敵を選択した時に出てくるメニュー
 * @author onodera
 *
 */
public class EnemyMenu extends Select implements SceneForEditor{
	
	public EnemyMenu() {
		super(0.5, 0.5, 0.2, 0.2, 3, 3);
		createMenu();
	}
	
	private final void createMenu(){
		Names[0] = "レベル変更";
		Names[1] = "配置変更";
		Names[2] = "配置解除";
		for(int i=0;i<Permits.length;i++){
			Permits[i] = true;
		}
	}

	@Override
	public int pushEnter() {
		switch(kettei){
		case 0:
			return Paramaters.EDITOR_SELECT_CHARA_LEVEL;
		case 1:
			return Paramaters.EDITOR_MOVE_ENEMY_PLACE;
		case 2:
			return Paramaters.EDITOR_DELETE_ENEMY_PLACE;
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
