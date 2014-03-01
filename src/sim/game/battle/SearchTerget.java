package sim.game.battle;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import sim.game.Paramaters;
import sim.game.data.Map;
import sim.game.data.MapChip;
import sim.tools.MapHandling;

public class SearchTerget implements SceneWithMouseDragged {
	private ArrayList<Point> Tergets;
	private MapHandling mapHandling;
	
	public SearchTerget(ArrayList<Point> ts,MapHandling handle){
		Tergets = ts;
		mapHandling = handle;
	}

	@Override
	//ここでは使用しない
	public void draw(Graphics g,Container con) {}

	@Override
	public boolean keyEventDown() {
		mapHandling.moveCursor(0, 1);
		return false;
	}

	@Override
	public boolean keyEventEnter() {
		if(isThereTerget()){
			setTergetFlag(false);
			return true;
		}
		return false;
	}

	@Override
	public boolean keyEventEscape() {
		// TODO Auto-generated method stub
		setTergetFlag(false);
		return true;
	}

	@Override
	public boolean keyEventLeft() {
		mapHandling.moveCursor(-1, 0);
		return false;
	}

	@Override
	public boolean keyEventRight() {
		mapHandling.moveCursor(1, 0);
		return false;
	}

	@Override
	public boolean keyEventUp() {
		mapHandling.moveCursor(0, -1);
		return false;
	}
	
	/** 今指している座標にターゲットは存在するかどうか */
	protected boolean isThereTerget(){
		Point cursor = mapHandling.getCursor();
		for(Point p : Tergets){
			if(cursor.x == p.x && cursor.y == p.y)return true;
		}
		return false;
	}
	
	/** 各マップチップにフラグを立てたり折ったりする */
	public void setTergetFlag(boolean t){
		int Width = mapHandling.getMapWidth();
		int Height = mapHandling.getMapHeight();
		for(int y=0;y<Height;y++){
			for(int x=0;x<Width;x++){
				boolean event=false;
				for(Point pt : Tergets){
					if(x==pt.x && y==pt.y){
						event=true;
						break;
					}
				}
				int num;
				if(event) num=Paramaters.ACTIVE_CHIP;
				else num=Paramaters.NONEVENT_CHIP;
				MapChip mapChip = mapHandling.getMapChip(x, y);
				mapChip.setColorFlag(num,t);
			}
		}
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
