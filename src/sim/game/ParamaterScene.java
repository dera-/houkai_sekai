package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import sim.game.data.CharaData;
import sim.game.data.StatusData;
import sim.tools.Select;

/** キャラクターを羅列しておくクラス */
public class ParamaterScene extends Select implements GlobalScene {
	private ArrayList<CharaData> CharaList; 
	private String MainMessage;
	private StatusData Status;  //選んだキャラのステータス
	private final int nextScene;
	
	public ParamaterScene(ArrayList<CharaData> list, String str){
		this(list, str, Paramaters.STRATEGY_SCENE);
	}
	
	public ParamaterScene(ArrayList<CharaData> list, String str, int sceneNum){
		super(0.4,0.3,0.5,0.2,8,list.size()+1);
		CharaList=list;
		MainMessage=str;
		nextScene = sceneNum;
		createMenu();
	}
	
	/** 各項目の作成 */
	private void createMenu(){
		//名前の構築
		for(int i=0;i<CharaList.size();i++){
			CharaData C = CharaList.get(i);
			Names[i] = C.getCharaName();
		}
		Names[CharaList.size()]="戻る";
		
		//選択の可否
		for(int i=0;i<Permits.length;i++){
			Permits[i]=true;
		}
	}

	@Override
	public void drawing(Graphics g, Container con) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
		if(Status!=null){
			Status.drawScene(g, con);
		}
		else{
			g.setColor(Color.white);
			int font=(int)Math.round(0.075*Paramaters.Height);
			g.setFont( new Font("明朝体",Font.BOLD,font) );
			int startX=(Paramaters.Width-(font+1)*MainMessage.length())/2;
			g.drawString(MainMessage,startX,(int)Math.round(Pt.y-0.05*Paramaters.Height));
			Draw(g,con);
		}
	}
	

	@Override
	public int pushDown() {
		if(Status==null) Moving(true);
		else Status.ViewSkill(true);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushEnter() {
		if(Status==null){
			if(pointToNextScreen(cursorX, cursorY)){
				jumpNextScreen();
				return Paramaters.NON_EVENT;
			}
			else if(kettei==-1) return Paramaters.NON_EVENT;
			else if(kettei==Names.length-1)return nextScene;
			else Status = new StatusData(CharaList.get(kettei));
		}
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushEscape() {
		if(Status!=null){
			Status=null;
			return Paramaters.NON_EVENT;
		}
		else return nextScene;
	}

	@Override
	public int pushLeft() {
		if(Status!=null) Status.changeScene(false);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushRight() {
		if(Status!=null) Status.changeScene(true);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushUp() {
		if(Status==null) Moving(false);
		else Status.ViewSkill(false);
		return Paramaters.NON_EVENT;
	}
	
	/** キャラリストを外部に渡すメソッド */
	public ArrayList<CharaData> getCharaList(){
		return CharaList;
	}
	
	/** メイドさんの解説コメント番号を返すメソッド */
	public int getComment(){
		if(Status==null)return 1000;
		return Status.getComment();
	}
	

	@Override
	public void running() {}

    @Override
    public void moveCursor(int x, int y) {
    	cursorX = x;
    	cursorY = y;
        if(Status==null) decideNowSelectedItem(x,y);
        else Status.moveCursorInSkillMenu(x, y);
    }

}
