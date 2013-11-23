package sim.edit;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;

import sim.game.Paramaters;
import sim.tools.MapHandling;

public class DecideSetPlace implements EditorSceneForDragged{
	private final int nextEvent;
	private MapHandling mapHandling;
	
	
	public DecideSetPlace(MapHandling handle, int eventNum){
		nextEvent = eventNum;
		mapHandling = handle;
	}
	
	@Override
	public int pushEnter() {
		return nextEvent;
	}

	@Override
	public int pushEscape() {
		return Paramaters.DELETE_NOW_SCENE;
	}

	@Override
	public void drawing(Graphics g, Container con) {}

	@Override
	public void moveCursor(int x, int y) {
		mapHandling.moveCursorFromPixelValue(x, y);
		
	}

	@Override
	public void mouseDragged(int x, int y) {
		mapHandling.moveCenterFromPixelValue(x, y);
	}

	@Override
	public void mousePressed(int x, int y) {
		mapHandling.setMousePressedPlace(new Point(x,y));
	}

	@Override
	public void mouseReleased(int x, int y) {
		mapHandling.setMousePressedPlace(null);
	}
	
}
