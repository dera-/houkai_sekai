package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;

import sim.tools.Select;

/** 終了してもいいかどうか確認を行う画面のクラス */
public class EndCheckScene extends Select implements GlobalScene {
	private String Message;
	
	public EndCheckScene(){
		super(0.425,0.4,0.2,0.15,2,2);
		Message="終了してよろしいですか?";
		createItem();
	}
	
	public EndCheckScene(String messe){
		super(0.425,0.4,0.2,0.15,2,2);
		Message=messe;
		createItem();
	}
	
	/**メニュー項目の作成*/
	private void createItem(){
		Names[0]="はい";
		Names[1]="いいえ";
		Permits[0]=true;
		Permits[1]=true;
	}
	
	@Override
	public void drawing(Graphics g, Container con) {
		g.setColor(new Color(0,0,0,150));
		g.fillRect(0,0,Paramaters.Width,Paramaters.Height);
		g.setColor(Color.white);
		int font=(int)Math.round(0.075*Paramaters.Height);
		g.setFont( new Font("明朝体",Font.BOLD,font) );
		int startX=(Paramaters.Width-(font+1)*Message.length())/2;
		g.drawString(Message,startX,(int)Math.round(Pt.y-0.05*Paramaters.Height));
		Draw(g,con);
	}

	@Override
	public int pushDown() {
		Moving(true);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushEnter() {
		switch(kettei){
		case 0: return Paramaters.DECIDE_YES;
		case 1: return Paramaters.DECIDE_NO;
		default: return Paramaters.NON_EVENT;
		}
	}

	@Override
	public int pushEscape() {
		return Paramaters.DECIDE_NO;
	}

	@Override
	public int pushLeft() {
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushRight() {
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushUp() {
		Moving(false);
		return Paramaters.NON_EVENT;
	}

	@Override
	public void running() {}

    @Override
    public void moveCursor(int x, int y) {
        decideNowSelectedItem(x,y);
    }

}
