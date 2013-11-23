package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;

import sim.game.data.CharaData;
import sim.tools.Select;

public class DecideSex extends Select implements GlobalScene {
	private CharaData NowChara; 
	private boolean Before;  //変更する前の性別
	
	public DecideSex(CharaData C){
		super(0.4,0.4,0.2,0.2,2,2);
		NowChara=C;
		Before=NowChara.isMeal();
		Names[0]="男";
		Names[1]="女";
		Permits[0]=true;
		Permits[1]=true;
	}
	
	@Override
	public void drawing(Graphics g, Container con) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
		Draw(g,con);
	}

	@Override
	public int pushDown() {
		Moving(true);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushEnter() {
		if(kettei==-1)return Paramaters.NON_EVENT;
		else{
			NowChara.changeSex(kettei==0);
			if(Before!=NowChara.isMeal()){
				NowChara.changeFace(Paramaters.Image_BOX.getRandomFaceName(!Before));
			}
			return Paramaters.DELETE_NOW_SCENE;
		}
	}

	@Override
	public int pushEscape() {
		return Paramaters.DELETE_NOW_SCENE;
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
	public void running() {
		// TODO Auto-generated method stub

	}

    @Override
    public void moveCursor(int x, int y) {
        decideNowSelectedItem(x,y);
    }

}
