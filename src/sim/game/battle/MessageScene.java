package sim.game.battle;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;

import sim.game.Paramaters;
import sim.tools.GraphicTool;

public class MessageScene implements Scene {
	private final int Number;  //メッセージの種類
	private String Message;
	private final long WaitTime;
	private final long StartTime;
	private GraphicTool Tools;
	private final char nextChar= '>';
	private final int maxLength = 3;
	private final long intervalTime = 500;
	private String nowString = "";
	private long beforeTime = System.currentTimeMillis();
	
	
	public MessageScene(int num,String mes,long time){
		Number=num;
		Message=mes;
		WaitTime=time;
		StartTime=System.currentTimeMillis();
		Tools=new GraphicTool();
	}

	@Override
	public boolean keyEventUp() {
		// TODO Auto-generated method stub
		return isOk();
	}

	@Override
	public boolean keyEventDown() {
		// TODO Auto-generated method stub
		return isOk();
	}

	@Override
	public boolean keyEventRight() {
		// TODO Auto-generated method stub
		return isOk();
	}

	@Override
	public boolean keyEventLeft() {
		// TODO Auto-generated method stub
		return isOk();
	}

	@Override
	public boolean keyEventEnter() {
		// TODO Auto-generated method stub
		return isOk();
	}

	@Override
	public boolean keyEventEscape() {
		// TODO Auto-generated method stub
		return isOk();
	}
	
	private boolean isOk(){
		long now=System.currentTimeMillis();
		return (now-StartTime>=WaitTime);
	}
	
	/** メッセージの種類を返すメソッド */
	public int getNumber(){
		return Number;
	}
	
	@Override
	public void draw(Graphics g, Container con) {
		// TODO Auto-generated method stub
		Tools.setTransmission(g,0,0,Paramaters.Width,Paramaters.Height,Color.black,(float)0.5);
		g.setColor(Color.white);
		g.setFont(new Font("明朝体",Font.BOLD,30));
		g.drawString(Message,(int)Math.round(0.3*Paramaters.Width),(int)Math.round(0.5*Paramaters.Height));
		g.setFont(new Font("明朝体",Font.BOLD,20));
		if(isOk())g.drawString(nowString,(int)Math.round(0.85*Paramaters.Width),(int)Math.round(0.9*Paramaters.Height));
	}

    @Override
    public void run(){      
        String bgm = Paramaters.BGM_WIN;
        if( !bgm.equals(Paramaters.Music_Box.getNowBgmName()) && Number==Paramaters.VICTORY_MASSAGE )
            Paramaters.Music_Box.playMusic(bgm);
        
        if(!isOk())return;
        long now = System.currentTimeMillis();
        if(now-beforeTime < intervalTime)return;
        createNowString();
        beforeTime = System.currentTimeMillis();
    }
    
    private void createNowString(){
        int length = nowString.length();
        if(length >= maxLength) nowString="";
        else nowString += nextChar;
    }

	@Override
	public void moveCursor(int x, int y) {}

}
