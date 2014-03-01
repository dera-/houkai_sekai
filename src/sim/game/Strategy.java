package sim.game;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import sim.tools.Select;;

public class Strategy extends Select implements GlobalScene{
	
	/** コンストラクタ */
	public Strategy(){
		super(0.3,0.15,0.7,0.4,6,6);
		createMenuItems();
	}
	
	/** メニュー項目の作成 */
	private final void createMenuItems(){
		Names[0]="戦場選択";
		Names[1]="能力確認";
		Names[2]="職業変更";
		Names[3]="継承システム";
		Names[4]="セーブ";
		Names[5]="ロード";
		for(int i=0;i<Permits.length;i++)Permits[i]=true;
	}
	
	/** カーソルを左に動かす */
	public int pushLeft(){
		return Paramaters.NON_EVENT;
	}
	
	/** カーソルを右に動かす */
	public int pushRight(){
		return Paramaters.NON_EVENT;
	}
	
	/** カーソルを下に動かす */
	public int pushUp(){
		Moving(false);
		return Paramaters.NON_EVENT;
	}
	
	/** カーソルを上に動かす */
	public int pushDown(){
		Moving(true);
		return Paramaters.NON_EVENT;
	}
	
	/** エンターキーを押した時の処理 */
	public int pushEnter(){
		switch(kettei){
		case 0: return Paramaters.SELECT_STAGE;
		case 1: return Paramaters.PARAMATER_SCENE;
		case 2: return Paramaters.JOBCHANGE_SCENE1;
		case 3: return Paramaters.MATING_SCENE;
		case 4: return Paramaters.SAVE_EVENT;
		case 5: return Paramaters.LOAD_SCENE;
		default: return Paramaters.NON_EVENT;
		}
	}
	
	/** エスケープキーを押した時の処理 */
	public int pushEscape(){
		return Paramaters.NON_EVENT;
	}
	
	public void drawing(Graphics g,Container con){
		g.setColor(Color.black);
		g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
		super.Draw(g, con);
		g.setColor(Color.white);
		g.setFont(new Font("明朝体",Font.BOLD,30));
		g.drawString("メニュー選択画面",Pt.x,Pt.y);
	}
	
	/** 変数ketteiに従ったコメントの番号を返すメソッド */
	public int getComment(){
		switch(kettei){
		case -1: return 33;
		case 0: return 34;
		case 1: return 35;
		case 2: return 36;
		case 3: return 37;
		case 4: return 38;
		case 5: return 99;
		default: return 1000; 
		}
	}
	
	public void running(){
	    String bgm = Paramaters.BGM_Normal;
	    if( !bgm.equals(Paramaters.Music_Box.getNowBgmName()) )
	        Paramaters.Music_Box.playMusic(bgm);
	}

    @Override
    public void moveCursor(int x, int y) {
        decideNowSelectedItem(x,y);
    }

}