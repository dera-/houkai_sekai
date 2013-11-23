package sim.edit;

import java.awt.Container;
import java.awt.Graphics;
import java.util.ArrayList;

import sim.game.Paramaters;
import sim.game.data.CharaData;
import sim.tools.Select;

/**
 * 配置する敵キャラをリストから選択するクラス
 * @author onodera
 *
 */
public class EnemySelectScene extends Select implements HavingCharaData{
	private ArrayList<CharaData> charaList = new ArrayList<CharaData>();

	public EnemySelectScene(ArrayList<CharaData> charalist) {
		super(0.5, 0.3, 0.5, 0.2, 10, charalist.size());
		charaList = charalist;
		createMenu();
	}
	
	private final void createMenu(){
		for(int i=0;i<Names.length;i++){
			CharaData chara = charaList.get(i);
			Names[i] = chara.getCharaName()+" LV"+chara.getLV();
			Permits[i] = true;
		}
	}

	@Override
	public int pushEnter() {
		if(pointToNextScreen(cursorX, cursorY)){
			jumpNextScreen();
			return Paramaters.NON_EVENT;
		}
		if(kettei == NOT_SELECT) return Paramaters.NON_EVENT;
		return Paramaters.EDITOR_SET_ENEMY_PLACE;
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
		decideNowSelectedItem(x, y);
    	cursorX = x;
    	cursorY = y;
	}
	
	public CharaData getCharaData(){
		return charaList.get(kettei);
	}
	
}
