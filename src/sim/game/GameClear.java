package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

/** ゲームクリアした時の画面に関するクラス */
public class GameClear implements GlobalScene {
	private final long WaitTime=1000;  //待ち時間
	private final long StartTime;  //開始時間
	Image img_penguin=Paramaters.Image_BOX.getImage("ペンギン");

	public GameClear(){
		StartTime=System.currentTimeMillis();
	}
	
	@Override
	public int pushUp() {
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushDown() {
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushRight() {
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushLeft() {
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushEnter() {
		long now=System.currentTimeMillis();
		if(now-StartTime>WaitTime)return Paramaters.START_SCENE;
		else return Paramaters.NON_EVENT;
	}

	@Override
	public int pushEscape() {
		return Paramaters.NON_EVENT;
	}

	@Override
	//描画メソッド
	public void drawing(Graphics g, Container con) {
		for(int i=0; i<2; i++){
			for(int j=0; j<5; j++)
				g.drawImage(img_penguin,160*j,(int)Math.round(0.5*Paramaters.Height+120*i),con);
		}
		g.setColor(Color.black);
		String Message="Congratulations!!";
		int font=(int)Math.round(0.075*Paramaters.Height);
		g.setFont( new Font("明朝体",Font.BOLD,font) );
		int startX=(Paramaters.Width-(font+1)*Message.length())/2;
		g.drawString(Message,startX,(int)Math.round(0.5*Paramaters.Height-0.05*Paramaters.Height));
	}

	@Override
	public void running() {}

    @Override
    public void moveCursor(int x, int y) {}

}
