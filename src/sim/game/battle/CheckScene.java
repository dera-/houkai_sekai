package sim.game.battle;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;

import sim.game.Paramaters;
import sim.tools.Select;

public class CheckScene extends Select implements Scene {
	private String Message;
	private final int Number;
	
	public CheckScene(int num,String mes){
		super(0.45,0.5,0.1,0.1,2,2);
		Names[0]="はい";
		Names[1]="いいえ";
		Permits[0]=true;
		Permits[1]=true;
		Number=num;
		Message=mes;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyEventLeft() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyEventEnter() {
		return (0<=kettei && kettei<Names.length);
	}

	@Override
	public boolean keyEventEscape() {
		// TODO Auto-generated method stub
		return true;
	}
	
	/** 「はい」を選んだかどうか */
	public boolean isGoAhead(){
		return (kettei==0);
	}
	
	/** ２択の種類を返す */
	public int getNumber(){
		return Number;
	}

	@Override
	public void draw(Graphics g, Container con) {
		Tools.setTransmission(g,0,0,Paramaters.Width,Paramaters.Height,Color.black,(float)0.5);
		Draw(g,con);
		g.setColor(Color.white);
		int font=30;
		g.setFont(new Font("明朝体",Font.BOLD,font));
		int startX=(Paramaters.Width-(font+1)*Message.length())/2;
		g.drawString(Message,startX,(int)Math.round(Pt.y-0.05*Paramaters.Height));
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
