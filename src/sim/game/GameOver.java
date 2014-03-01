package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;

import sim.game.GlobalScene;
import sim.game.Paramaters;

/** ゲームオーバー時の画面に関するクラス */
public class GameOver implements GlobalScene {
	private final long WaitTime;  //待ち時間
	private final long StartTime;  //開始時間
	
	public GameOver(long w){
		WaitTime=w;
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
	public void drawing(Graphics g, Container con) {
		//以下、GAMEOVERのテスト画面
		g.setColor(Color.black);
		g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
		g.setColor(Color.white);
		g.setFont(new Font("明朝体",Font.BOLD,40));
		g.drawString("GAME OVER",(int)Math.round(0.25*Paramaters.Width),(int)Math.round(0.4*Paramaters.Height));
	}

	@Override
	public void running() {
        String bgm = Paramaters.BGM_GAMEOVER;
        if( !bgm.equals(Paramaters.Music_Box.getNowBgmName()) )
            Paramaters.Music_Box.playMusic(bgm);
	}

    @Override
    public void moveCursor(int x, int y) {}

}
