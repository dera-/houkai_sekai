package sim.tools;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import sim.game.Paramaters;
import sim.game.data.CharaData;
import sim.game.data.Map;
import sim.game.data.MapChip;

public class MapHandling {
	private Map map=null;
    private Point center;  //中心ポイント
    private Point cursor;  //カーソル
    private Point mousePressedPlace;
    
    public MapHandling(){
    	map = null;
    	distanceFirstCursor(null);
    }
    
    public MapHandling(int mapNo){
    	this(mapNo, null);
    }
    
    public MapHandling(int mapNo, Point firstPlace){
    	map=Map.searchMap(mapNo);
    	distanceFirstCursor(firstPlace);
    }
    
    public MapHandling(Map m){
    	map = m;
    	distanceFirstCursor(null);
    }

	public Map getMap() {
		return map;
	}
	
	public MapChip getMapChipOfCursor(){
		return getMapChip(cursor.x,cursor.y);
	}
	
	public MapChip getMapChip(int x, int y){
		if(!isInMap(x, y))return null;
		return map.getChip(x, y);
	}

	public Point getCenter() {
		return center;
	}

	public Point getCursor() {
		return cursor;
	}
	
	public int getMapWidth(){
		return map.getWidth();
	}
	
	public int getMapHeight(){
		return map.getHeight();
	}

	public void setMousePressedPlace(Point mousePressedPlace) {
		this.mousePressedPlace = mousePressedPlace;
	}

	/** cursorとcenterの初期位置を決定するメソッド */
	private void distanceFirstCursor(Point place){
		if(place == null){
			cursor = new Point(0,0);
			center = new Point(0,0);
		}
		else{
			cursor = new Point(place.x, place.y);
			center = new Point(place.x, place.y);
		}
	}
	
	/** 引数の座標がMAP内かどうか判断する */
	private boolean isInMap(int x, int y){
		MapChip mc=map.getChip(x, y);
		if(mc==null)return false;
		else return true;
	}
	
	public void moveCenter(int x,int y){
		if( isInMap(center.x+x, center.y+y) ){
		    moveToAssinedPlace(center, center.x+x, center.y+y);
		}
	}
	
	/** cursorが画面に映るまでcenterを移動させるメソッド */
	private void moveCenterUntilFound(){
		int dx=0;
		if(cursor.x < center.x + Map.HALF_CHIP_WIDTH_MINUS) dx -= (center.x + Map.HALF_CHIP_WIDTH_MINUS - cursor.x);
		else if(cursor.x >= center.x + Map.HALF_CHIP_WIDTH_PLUS) dx -= (center.x + Map.HALF_CHIP_WIDTH_PLUS - cursor.x - 1);
		
		int dy=0;
		if(cursor.y < center.y + Map.HALF_CHIP_HEIGHT_MINUS) dy -= (center.y + Map.HALF_CHIP_HEIGHT_MINUS - cursor.y);
		else if(cursor.y >= center.y + Map.HALF_CHIP_HEIGHT_PLUS) dy -= (center.y + Map.HALF_CHIP_HEIGHT_PLUS - cursor.y - 1);
		
		moveCenter(dx, dy);
	}
	
	public void moveCenterToAssinedPlace(int x, int y){
		moveToAssinedPlace(center, x, y);
	}
	
	public void moveCenterToCursorPlace(){
		moveToAssinedPlace(center, cursor.x, cursor.y);
	}
	
	public void moveCursorToAssinedPlace(int x, int y){
		moveToAssinedPlace(cursor, x, y);
	}
	
	/** 引数のPointオブジェクトを(第2引数、第3引数)の位置に移動させるメソッド */
    private void moveToAssinedPlace(Point pt, int x,int y){
        pt.x = x;
        pt.y = y;
    }
    
    /** cursorを引数の数値の分だけ動かすメソッド */
	public void moveCursor(int x,int y){
		if( isInMap(cursor.x+x, cursor.y+y) ){
		    cursor.x += x;
		    cursor.y += y;
		    moveCenterUntilFound();
		}
	}
	
	/** 実座標を引数にして現在の座標(マップチップ単位)をcursorに与えるメソッド */
    public void moveCursorFromPixelValue(int x, int y){
    	if(x<0 || x>Paramaters.Width || y<0 || y>Paramaters.Height) return;
    	int chipX = x / MapChip.Width_Chip;
    	int chipY = y / MapChip.Height_Chip;
    	int realPlaceX = center.x + Map.HALF_CHIP_WIDTH_MINUS + chipX;
    	int realPlaceY = center.y + Map.HALF_CHIP_HEIGHT_MINUS + chipY;
    	moveToAssinedPlace(cursor, realPlaceX, realPlaceY);
    }
    
    /** 実座標を引数にして現在の座標(マップチップ単位)をcenterに与えるメソッド */
    public void moveCenterFromPixelValue(int x, int y){
    	if(mousePressedPlace == null) return;
    	int dx = (x - mousePressedPlace.x) / MapChip.Width_Chip;
    	int dy = (y - mousePressedPlace.y) / MapChip.Height_Chip;
    	if(dx != 0) mousePressedPlace.x = x;
    	if(dy != 0) mousePressedPlace.y = y;
    	moveCenter(-dx,-dy);
    }
    
    private Point getStartPlace(){
		int x = center.x + Map.HALF_CHIP_WIDTH_MINUS;
		int y = center.y + Map.HALF_CHIP_HEIGHT_MINUS;
    	return new Point(x, y);
    }
    
    private Point getEndPlace(){
		int x = center.x + Map.HALF_CHIP_WIDTH_PLUS;
		int y = center.y + Map.HALF_CHIP_HEIGHT_PLUS;
    	return new Point(x, y);
    }
    
    /** マップのみの描画を行うメソッド */
    public void drawMap(Graphics g, Container con){
    	Point startPlace = getStartPlace();
		Point endPlace = getEndPlace();
		drawMap(g, con, startPlace, endPlace);
    }
    
    /** マップとそのマップ上のキャラとカーソルの描画を行うメソッド */
    public void drawMap(Graphics g, Container con, ArrayList<CharaData> allys, ArrayList<CharaData> enemies){
    	Point startPlace = getStartPlace();
		Point endPlace = getEndPlace();
		drawMap(g, con, startPlace, endPlace);
		drawCharacters(g, con, allys, enemies, startPlace, endPlace);
		drawCursor(g, startPlace);
    }
    
    private void drawMap(Graphics g, Container con, Point startPlace, Point endPlace){
		/* マップチップの描画 */
		for( int y = startPlace.y; y<endPlace.y; y++ ){
			for( int x = startPlace.x; x<endPlace.x; x++ ){
				MapChip mc=map.getChip(x,y);
				if(mc==null)continue;
				mc.draw(g,con,x-startPlace.x,y-startPlace.y);
			}
		}
    }
    
    /** マップ上にいるキャラクターの描画 */
    private void drawCharacters(Graphics g, Container con, ArrayList<CharaData> allys, ArrayList<CharaData> enemies, Point startPlace, Point endPlace){
		for(int i=0;i<2;i++){
			ArrayList<CharaData> list=null;
			boolean ally;
			if(i==0){
				ally=true;
				list = allys;
			}
			else{
				ally=false;
				list = enemies;
			}
			for(CharaData C: list){
				Point pt=C.getPlace();
				if(pt==null)continue;
				if(startPlace.x <= pt.x && pt.x < endPlace.x && startPlace.y <= pt.y && pt.y < endPlace.y){
					C.drawDot(g, con, pt.x-startPlace.x, pt.y-startPlace.y, ally);
				}
			}
		}
    }
    
    /** カーソルの描画 */
    private void drawCursor(Graphics g, Point startPlace){
		int startX = startPlace.x;
		int startY = startPlace.y;
		int cursorX = cursor.x - startX;
		int cursorY = cursor.y - startY;
    	if(cursorX < 0 || cursorX >= Map.CHIP_NUMS_WIDTH || cursorY < 0 || cursorY >= Map.CHIP_NUMS_HEIGHT) return;
		g.setColor(Color.yellow);
		g.drawRect(cursorX * MapChip.Width_Chip , cursorY * MapChip.Height_Chip , MapChip.Width_Chip , MapChip.Height_Chip);
    }
    
    
	
}
