package sim.game.battle;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;

import sim.game.Paramaters;
import sim.game.data.EventData;
import sim.tools.Select;

//TODO 修正
public class EscapeMenu extends Select implements Scene {
	
	public EscapeMenu(){
		super(0.75,0.2,0.2,0.2,3,3);
		Names[0] = "勝敗条件";
		Names[1] = "ターン終了";
		Names[2] = "メニュー選択画面へ戻る";
		
		Permits[0] = true;
		Permits[1] = true;
		Permits[2] = true;
	}

	@Override
	public boolean keyEventUp() {
		Moving(false);
		return false;
	}

	@Override
	public boolean keyEventDown() {
		Moving(true);
		return false;
	}

	@Override
	public boolean keyEventRight() {
		return false;
	}

	@Override
	public boolean keyEventLeft() {
		return false;
	}

	@Override
	public boolean keyEventEnter() {
	    return (kettei!=-1);
	}

	@Override
	public boolean keyEventEscape() {
	    return true;
	}
	
	/** メイドさんのセリフの番号を返すメソッド */
	public int getComment(){
		//if(check!=null)return 70;
		switch(kettei){
		case -1: return 67;
		case 0: return 68;
		case 1: return 69;	
		default: return 1000;	
		}
	}

	@Override
	public void draw(Graphics g, Container con) {
		g.setColor(new Color(0,0,0,150));
		g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
		Draw(g, con);
	}
	
	public int getNowSelect(){
	    return kettei;
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
