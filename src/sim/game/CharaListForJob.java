package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;

import sim.game.data.CharaData;
import sim.game.data.StatusData;
import sim.tools.Select;

/** 職業の変更画面(職業を変えるキャラを選択する画面)に関するクラス */
public class CharaListForJob extends Select implements GlobalScene {
	
	public CharaListForJob(){
		super(0.4,0.3,0.5,0.2,8,Paramaters.AllyList.size()+1);
		createMenu();
	}
	
	/** 各項目の作成 */
	private void createMenu(){
		//名前の構築
		for(int i=0;i<Paramaters.AllyList.size();i++){
			CharaData C = Paramaters.AllyList.get(i);
			Names[i] = C.getCharaName();
		}
		Names[Paramaters.AllyList.size()]="戻る";
		
		//選択の可否
		for(int i=0;i<Permits.length;i++){
			Permits[i]=true;
		}
	}


	@Override
	//描画を行うメソッド
	public void drawing(Graphics g, Container con) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
		g.setColor(Color.white);
		String Message="職業の変更";
		int font=(int)Math.round(0.075*Paramaters.Height);
		g.setFont( new Font("明朝体",Font.BOLD,font) );
		int startX=(Paramaters.Width-(font+1)*Message.length())/2;
		g.drawString(Message,startX,(int)Math.round(Pt.y-0.05*Paramaters.Height));
		Draw(g,con);
	}

	@Override
	//↓キーを押したときに呼び出されるメソッド
	public int pushDown() {
		Moving(true);
		return Paramaters.NON_EVENT;
	}

	@Override
	//エンターキーを押したときに呼び出されるメソッド
	public int pushEnter() {
		if(pointToNextScreen(cursorX, cursorY)){
			jumpNextScreen();
			return Paramaters.NON_EVENT;
		}
		else if(kettei==-1) return Paramaters.NON_EVENT;
		else if(kettei==Names.length-1)return Paramaters.STRATEGY_SCENE;
		else return Paramaters.JOBCHANGE_SCENE2;
	}

	@Override
	//エスケープキーを押したときに呼び出されるメソッド
	public int pushEscape() {
		return Paramaters.STRATEGY_SCENE;
	}

	@Override
	//←キーを押したときに呼び出されるメソッド
	public int pushLeft() {
		return Paramaters.NON_EVENT;
	}

	@Override
	//→キーを押したときに呼び出されるメソッド
	public int pushRight() {
		return Paramaters.NON_EVENT;
	}

	@Override
	//↑キーを押したときに呼び出されるメソッド
	public int pushUp() {
		Moving(false);
		return Paramaters.NON_EVENT;
	}

	@Override
	public void running() {}
	
	/** 現在選んでいるキャラクターを返すメソッド */
	public CharaData getChara(){
		if(0<=kettei && kettei<Names.length-1) return Paramaters.AllyList.get(kettei);
		else return null;
	}

    @Override
    public void moveCursor(int x, int y) {
    	cursorX = x;
    	cursorY = y;
        decideNowSelectedItem(x,y);
    }
}
