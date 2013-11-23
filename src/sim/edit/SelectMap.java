package sim.edit;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import sim.game.Paramaters;
import sim.game.data.EventData;
import sim.game.data.Map;
import sim.game.data.MapChip;
import sim.tools.MapHandling;
import sim.tools.Select;

public class SelectMap extends Select implements SceneForEditor {
	private ArrayList<Map> mapList;
	private MapHandling mapHandling = null;
	
	public SelectMap(ArrayList<Map> maps) {
		super(0.2, 0.15, 0.7, 0.2, 10, maps.size());
		mapList = maps;
		createMenu();
	}
	
	private final void createMenu(){
		Names = new String[mapList.size()];
		Permits = new boolean[mapList.size()];
		for(int i=0;i<mapList.size();i++){
			Map map = mapList.get(i);
			Names[i] = "マップ" + map.Number;
			Permits[i] = true;
		}
	}

	@Override
	public int pushEnter() {
		if(pointToNextScreen(cursorX, cursorY)){
			jumpNextScreen();
			return Paramaters.NON_EVENT;
		}
		if(kettei == -1)return Paramaters.NON_EVENT;
		return Paramaters.EDITOR_DECIDE_MAP;
	}

	@Override
	public int pushEscape() {
		return Paramaters.EDITOR_NOT_DECIDE_MAP;
	}

	@Override
	public void drawing(Graphics g, Container con) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
		drawMap(g, con);
		Draw(g,con);
	}
	
	private void drawMap(Graphics g, Container con){
		if(kettei == Select.NOT_SELECT)return;
		mapHandling.drawMap(g, con);
	}
	
	private void selectMapHandling(){
		if(kettei==-1) return;
		mapHandling = new MapHandling(mapList.get(kettei));
	}

	@Override
	public void moveCursor(int x, int y) {
		decideNowSelectedItem(x,y);
		selectMapHandling();
    	cursorX = x;
    	cursorY = y;
	}
	
	public int getSelectedMapNumber(){
		Map map = mapHandling.getMap();
		return map.Number;
	}

}
