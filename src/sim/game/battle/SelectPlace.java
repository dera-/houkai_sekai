package sim.game.battle;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import sim.game.data.CharaData;
import sim.game.data.Map;
import sim.game.data.MapChip;
import sim.tools.MapHandling;

/** キャラを配置する場所を決定するクラス */
public class SelectPlace implements SceneWithMouseDragged {
	private ArrayList<CharaData> Allys;
	private ArrayList<CharaData> Enemies;
	private MapHandling mapHandling; 
	
	public SelectPlace(ArrayList<CharaData> Alist,ArrayList<CharaData> Elist,MapHandling handle){
		Allys=Alist;
		Enemies=Elist;
		mapHandling = handle;
	}
	
	@Override
	public boolean keyEventUp() {
		mapHandling.moveCursor(0, -1);
		return false;
	}

	@Override
	public boolean keyEventDown() {
		mapHandling.moveCursor(0, 1);
		return false;
	}

	@Override
	public boolean keyEventRight() {
		mapHandling.moveCursor(1, 0);
		return false;
	}

	@Override
	public boolean keyEventLeft() {
		mapHandling.moveCursor(-1, 0);
		return false;
	}

	@Override
	public boolean keyEventEnter() {
		return true;
	}

	@Override
	public boolean keyEventEscape() {
		return true;
	}

	@Override
	public void draw(Graphics g, Container con) {

	}

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void moveCursor(int x, int y) {
		mapHandling.moveCursorFromPixelValue(x, y);
	}

	@Override
	public void mouseDragged(int x, int y) {
		mapHandling.moveCenterFromPixelValue(x, y);
	}

}
