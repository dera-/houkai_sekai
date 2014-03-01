package sim.game.battle;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;

import sim.game.data.Map;
import sim.tools.Select;

public class MenuAfterMoving extends Select implements Scene {
	protected Map map;
	
	public MenuAfterMoving(Map m){
		super(0.5,0.5,0.24,0.1,3,3);
		formatArrays();
		map=m;
	}
	
	/** 配列の各要素の初期化 */
	protected void formatArrays(){
		Names[0]="スキル";
		Names[1]="能力";
		Names[2]="待機";
		Permits[0]=true;
		Permits[1]=true;
		Permits[2]=true;
	}
	
	/** 選択したメニューの番号を返す */
	public int getDicideNum(){
		return kettei;
	}

	@Override
	public void draw(Graphics g,Container con) {
		Draw(g,con);
	}

	@Override
	public boolean keyEventDown() {
		Moving(true);
		return false;
	}

	@Override
	public boolean keyEventEnter() {
		if( kettei==-1 || !Permits[kettei] )return false;
		else return true;
	}

	@Override
	public boolean keyEventEscape() {
		return true;
	}

	@Override
	/** ここでは使用しない */
	public boolean keyEventLeft() {
		return false;
	}

	@Override
	/** ここでは使用しない */
	public boolean keyEventRight() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyEventUp() {
		// TODO Auto-generated method stub
		Moving(false);
		return false;
	}
	
	/** コメント番号を返すメソッド */
	public int getComment(){
		switch(kettei){
		case -1: return 46;
		case 0: return 47;
		case 1: return 48;
		case 2: return 49;
		default: return 1000;
		}
	}

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void moveCursor(int x, int y) {
		decideNowSelectedItem(x,y);
	}

}
