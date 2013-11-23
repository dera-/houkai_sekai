package sim.game;

import java.awt.*;

import sim.MainFrame;
import sim.tools.Select;

public class Start extends Select implements GlobalScene{
	
	public Start(double x,double y,double main,double sub,int num){
		super(x,y,main,sub,3,3);
		Names[0]="はじめから";
		Names[1]="続きから";
		Names[2]="ゲーム終了";
		Permits[0]=true;
		Permits[1]=true;
		Permits[2]=true;
	}
	
	//カーソルを上に動かす
	public int pushUp(){
		Moving(false);
		return Paramaters.NON_EVENT;
	}
	
	//カーソルを下に動かす
	public int pushDown(){
		Moving(true);
		return Paramaters.NON_EVENT;
	}
	
	//ここではあまり意味の無いメソッド
	public int pushRight(){
		return Paramaters.NON_EVENT;
	}
	
	//ここではあまり意味の無いメソッド
	public int pushLeft(){
		return Paramaters.NON_EVENT;
	}
	
	//ここではあまり意味の無いメソッド
	public int pushEscape(){
		return Paramaters.NON_EVENT;
	}
	
	public int pushEnter(){
		if(kettei==-1 || !Permits[kettei])return Paramaters.NON_EVENT;
		switch(kettei){
			case 0: return Paramaters.OPENNING_SCENE;
			case 1: return Paramaters.LOAD_SCENE;
			case 2: System.exit(0);
					break;
			default: break;
		}
		return Paramaters.NON_EVENT;
	}
	
	public void drawing(Graphics gx,Container con){
		gx.setColor(Color.black);
		gx.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
		gx.setFont(new Font("明朝体",Font.BOLD,40));
		gx.setColor(Color.white);
		Draw(gx, con);
		Image img=Paramaters.Image_BOX.getImage("タイトル");
		gx.drawImage(img,70,(int)Math.round(0.1*Paramaters.Height),con);
	}
	
	/** 変数ketteiが今指している場所を返すメソッド */
	public int getNowSelect(){
		return kettei;
	}
	
	public void running(){
	    String bgm = Paramaters.BGM_OPENNING;
	    if( !bgm.equals(Paramaters.Music_Box.getNowBgmName()) )
	       Paramaters.Music_Box.playMusic(bgm);
	}

    @Override
    public void moveCursor(int x, int y) {
        decideNowSelectedItem(x,y);
    }

}
