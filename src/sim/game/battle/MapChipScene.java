package sim.game.battle;

import java.awt.Container;
import java.awt.Graphics;

import sim.game.data.MapChip;
import sim.tools.MapHandling;

public class MapChipScene implements SceneWithMouseDragged {
	private MapHandling mapHandling;
	
	public MapChipScene(MapHandling handle){
		mapHandling = handle;
	}

	@Override
	public void draw(Graphics g, Container con) {
	    MapChip chip = mapHandling.getMapChipOfCursor();
	    if( chip != null )chip.drawInfo(g, con);
	}

	@Override
	public boolean keyEventDown() {
		mapHandling.moveCursor(0, 1);
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
