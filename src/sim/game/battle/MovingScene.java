package sim.game.battle;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import sim.game.brain.MovingBrain;
import sim.game.brain.Root;
import sim.game.data.CharaData;
import sim.game.data.Map;
import sim.game.data.MapChip;
import sim.tools.MapHandling;

public class MovingScene implements SceneWithMouseDragged {
	private ArrayList<CharaData> Allys;
	private ArrayList<CharaData> Enemies;
	private CharaData NowChara;
	private MapHandling mapHandling;
	private Point Before;
	private ArrayList<Point> ReachList;
	private boolean flags[] = {false,false};
	private static final int ANIMATION_START_FLAG = 0;
	private static final int SCENE_FINISH_FLAG = 1;
	private long beforeTime = System.currentTimeMillis();
	private final long intervalTime = 200;
	private int nowIndex = 0;
	private ArrayList<Point> placesOnRoot;
	MovingBrain brainForMove;
	private final boolean isAlly;

	public MovingScene(ArrayList<CharaData> AL,ArrayList<CharaData> EL,CharaData CD, MapHandling handle){
		Allys=AL;
		Enemies=EL;
		mapHandling = handle;
		NowChara=CD;
		Point cursor = mapHandling.getCursor();
		Before=new Point(cursor.x, cursor.y);
		brainForMove = new MovingBrain(AL, EL, mapHandling.getMap(), CD, true);
		ReachList = brainForMove.getReachs();
		isAlly = true;
	}
	
	public MovingScene(CharaData CD,Point goal, MapHandling handle){
	    NowChara=CD;
	    mapHandling = handle;
	    brainForMove = NowChara.getMoveRange();
	    ReachList = brainForMove.getReachs();
	    isAlly = false;
        nowIndex = 0;
        Root root = brainForMove.getRoot(goal);
        placesOnRoot = root.getLoads();
        flags[ANIMATION_START_FLAG] = true;
	}
	
	@Override
	public boolean keyEventDown() {
	    if(!isAlly || flags[ANIMATION_START_FLAG]) return false;
	    mapHandling.moveCursor(0, 1);
		return false;
	}

	@Override
	public boolean keyEventEnter() {
	    if(flags[ANIMATION_START_FLAG]){
	        flags[SCENE_FINISH_FLAG] = true;
	        Point reachPlace = placesOnRoot.get(placesOnRoot.size()-1);
	        NowChara.setPlace(reachPlace.x,reachPlace.y);
	        mapHandling.moveCenterToAssinedPlace(reachPlace.x, reachPlace.y);
	        mapHandling.moveCursorToAssinedPlace(reachPlace.x, reachPlace.y);
	        return true;
	    }
	    else if(isReach() && !isAllyPoint()){
		    setMovingFlag(false);
		    nowIndex = 0;
		    Point cursor = mapHandling.getCursor();
		    Root root = brainForMove.getRoot(cursor);
		    placesOnRoot = root.getLoads();
		    flags[ANIMATION_START_FLAG] = true;
		}
		return false;
	}

	@Override
	public boolean keyEventEscape() {
	    if(!isAlly) return false;
		setMovingFlag(false);
		setBeforePlace();
		return true;
	}

	@Override
	public boolean keyEventLeft() {
	    if(!isAlly || flags[ANIMATION_START_FLAG]) return false;
	    mapHandling.moveCursor(-1, 0);
		return false;
	}

	@Override
	public boolean keyEventRight() {
	    if(!isAlly || flags[ANIMATION_START_FLAG]) return false;
	    mapHandling.moveCursor(1, 0);
		return false;
	}

	@Override
	public boolean keyEventUp() {
	    if(!isAlly || flags[ANIMATION_START_FLAG]) return false;
	    mapHandling.moveCursor(0, -1);
		return false;
	}
	
	//ここでは何もしない
	public void draw(Graphics g,Container con){}

	/** 引数の座標がMAP内かどうか判断する */
	private boolean isInMap(int x,int y){
		MapChip mc = mapHandling.getMapChip(x, y);
		if(mc==null)return false;
		else return true;
	}
	
	/** 今指している座標が到着点かどうか */
	private boolean isReach(){
		Point cursor = mapHandling.getCursor();
		for(Point p : ReachList){
			if(p.x == cursor.x && p.y == cursor.y){
				return true;
			}
		}
		return false;
	}
	
	/** 今さしている座標に他の味方がいるかどうか */
	private boolean isAllyPoint(){
		Point cursor = mapHandling.getCursor();
		for(CharaData C : Allys){
			if(C.isPlace(cursor) && !C.isPlace(Before))return true;
		}
		return false;
	}
	
	/** キャラの座標を元の位置に戻す */
	public void setBeforePlace(){
		mapHandling.moveCenterToAssinedPlace(Before.x, Before.y);
		mapHandling.moveCursorToAssinedPlace(Before.x, Before.y);
		NowChara.setPlace(Before.x,Before.y);
		flags[ANIMATION_START_FLAG] = false;
		flags[SCENE_FINISH_FLAG] = false;
	}
	
	/** 移動可能なマスにフラグを付与あるいは消去する */
	public void setMovingFlag(boolean setFlag){
		Map map = mapHandling.getMap();
	    map.setFlagsForReachs(ReachList, true, true, setFlag);
	}
	
	/** アニメーション中かどうか */
	public boolean isMoving(){
	    return (flags[ANIMATION_START_FLAG] && !flags[SCENE_FINISH_FLAG] );
	}
	
	/** 移動シーンが終わったかどうか */
	public boolean isFinishMoving(){
	    return flags[SCENE_FINISH_FLAG];
	}

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        if(!flags[ANIMATION_START_FLAG] || flags[SCENE_FINISH_FLAG] || now - beforeTime < intervalTime)return;
        nowIndex++;
        if(nowIndex >= placesOnRoot.size()) flags[SCENE_FINISH_FLAG]=true;
        else{
            Point reachPlace = placesOnRoot.get(nowIndex);
            NowChara.setPlace(reachPlace.x,reachPlace.y);
	        mapHandling.moveCenterToAssinedPlace(reachPlace.x, reachPlace.y);
	        mapHandling.moveCursorToAssinedPlace(reachPlace.x, reachPlace.y);
        }
        beforeTime = System.currentTimeMillis();
    }

	@Override
	public void moveCursor(int x, int y) {
		if(!isAlly || flags[ANIMATION_START_FLAG]) return;
		mapHandling.moveCursorFromPixelValue(x, y);
	}

	@Override
	public void mouseDragged(int x, int y) {
		if(!isAlly || flags[ANIMATION_START_FLAG]) return;
		mapHandling.moveCenterFromPixelValue(x, y);
	}

}