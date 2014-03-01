package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import sim.game.data.EventData;
import sim.tools.Select;

public class StageSelect extends Select implements GlobalScene {
    private static final int ABSTRACT = 0;  //タイトル名のみの表示を意味する
    private static final int DETAILS = 1;   //ステージの詳細の表示を意味する
    private static final int NUMBERS_OF_TYPE = 2;    //表示形式の総数
    private int type;  //現在のの表示形式を表す
	private ArrayList<EventData> EventDataList = new ArrayList<EventData>();
	private Color[] ItemColors;
	
	public StageSelect(){
		super(0.3,0.15,0.7,0.4,10,10);
		type = ABSTRACT;
		createMenu();
	}
	
	private final void createMenu(){
		EventDataList.addAll(EventData.getUsableEvent());
		Names = new String[EventDataList.size()+1];
		Permits = new boolean[EventDataList.size()+1];
		ItemColors = new Color[EventDataList.size()+1];
		for(int i=0;i<EventDataList.size();i++){
			EventData elem = EventDataList.get(i);
			Names[i]=elem.getEventName();
			Permits[i]=true;
			if(elem.isMain())ItemColors[i]=Color.red;
			else ItemColors[i]=Color.green;
		}
		Names[EventDataList.size()]="戻る";
		Permits[EventDataList.size()]=true;
		ItemColors[EventDataList.size()]=Color.yellow;
	}

	@Override
	public int pushUp() {
		// TODO Auto-generated method stub
		Moving(false);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushDown() {
		// TODO Auto-generated method stub
		Moving(true);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushRight() {
		changeType(1);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushLeft() {
		changeType(-1);
		return Paramaters.NON_EVENT;
	}
	
	//typeの値を変更するメソッド
	private void changeType(int num){
	    if(kettei==-1){
	        type = ABSTRACT;
	        return;
	    }
	    type += num;
	    if(type >= NUMBERS_OF_TYPE) type = 0;
	    else if(type < 0) type = NUMBERS_OF_TYPE-1;
	}

	@Override
	public int pushEnter() {
		if(type == ABSTRACT && pointToNextScreen(cursorX, cursorY)){
			jumpNextScreen();
			return Paramaters.NON_EVENT;
		}
		else if(kettei==-1)return Paramaters.NON_EVENT;
		else if(kettei==EventDataList.size())return Paramaters.DELETE_NOW_SCENE;
		else{
			EventData.setNowEvent(EventDataList.get(kettei));
			return Paramaters.CHECK_BATTLE;
		}
	}

	@Override
	public int pushEscape() {
		// TODO Auto-generated method stub
		return Paramaters.DELETE_NOW_SCENE;
	}

	@Override
	public void drawing(Graphics g, Container con) {
	    switch(type){
	    case ABSTRACT:
	        drawAbstract(g, con);
	        break;
	    case DETAILS:
	        drawDetails(g, con, kettei);
	        break;
	    default:
	        break;
	    }
	}
	
	/** 概要画面の描画 */
	private void drawAbstract(Graphics g, Container con){
	    g.setColor(Color.black);
	    g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
	    super.Draw(g, con, ItemColors);
	    g.setColor(Color.white);
	    g.setFont(new Font("明朝体",Font.BOLD,30));
	    g.drawString("ステージ選択画面",Pt.x,Pt.y);
	    g.setFont(new Font("明朝体",Font.BOLD,24));
	    g.drawString("STAGE詳細=>", (int)Math.round(0.75*Paramaters.Width), (int)Math.round(0.5*Paramaters.Height));
	}
	
	/** ステージの詳細を描画。第3引数は現在さしているステージ。 */
	private void drawDetails(Graphics g, Container con, int num){
	    g.setColor(Color.black);
	    g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
	    if(num == -1) return;
	    if(num == Names.length-1){
	        g.setFont(new Font("",Font.BOLD,50));
	        g.setColor(Color.yellow);
	        g.drawString("戻る",0,(int)Math.round(0.4*Paramaters.Height));
	        return;
	    }
	    int fontTitle = 40;
	    int fontDetails = 25;
	    int fontConditions = 32;
	    EventData data = EventDataList.get(num);
	    String title   = data.getEventName();
	    String details = data.getEventDetais();
	    String[] conditions = data.getThisConditions();
	    Tools.drawHorizonLine(g,con,0,(int)Math.round(0.3*Paramaters.Height),Paramaters.Width);
	    Tools.drawHorizonLine(g,con,0,(int)Math.round(0.7*Paramaters.Height),Paramaters.Width);
	    g.setFont(new Font("",Font.BOLD,fontTitle));
	    g.setColor(ItemColors[num]);
	    g.drawString(title,0,(int)Math.round(0.2*Paramaters.Height));
	    g.setFont(new Font("",Font.PLAIN,fontDetails));
	    
	    /** メインイベントかサブイベントであることの表示 */
	    String eventType;
	    if(data.isMain()) eventType = "メインイベント";
	    else eventType = "サブイベント";
	    g.drawString(eventType,0,(int)Math.round(0.3*Paramaters.Height));
	    
	    g.setFont(new Font("",Font.PLAIN,fontDetails));
	    g.setColor(Color.yellow);
	    char[] detailsArray = details.toCharArray();
	    int charNumsOneLine = (int) Math.round(0.8 * Paramaters.Width / fontDetails);
	    int lineNums = detailsArray.length / charNumsOneLine +1;
	    int startY = (int)Math.round(0.35*Paramaters.Height);
	    for(int line=0; line<lineNums; line++){
	        String subString;
	        if(line == lineNums-1) subString = details.substring(line*charNumsOneLine);
	        else subString = details.substring(line*charNumsOneLine, (line+1)*charNumsOneLine);
	        g.drawString(subString, (int) Math.round(0.1 * Paramaters.Width), startY+line*fontDetails);
	    }
	    g.setFont(new Font("",Font.PLAIN,fontConditions));
	    g.setColor(Color.white);
	    g.drawString("勝利条件：" + conditions[0],0,(int)Math.round(0.8*Paramaters.Height));
	    g.drawString("敗北条件：" + conditions[1],0,(int)Math.round(0.9*Paramaters.Height));
	}

	@Override
	public void running() {
	    String bgm = Paramaters.BGM_STRATEGY;
	    if( !bgm.equals(Paramaters.Music_Box.getNowBgmName()) )
	        Paramaters.Music_Box.playMusic(bgm);
	}

    @Override
    public void moveCursor(int x, int y) {
        if(type == ABSTRACT){
        	cursorX = x;
        	cursorY = y;        	
        	decideNowSelectedItem(x,y);
        }
    }

}
