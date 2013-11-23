package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;

import sim.game.data.CharaData;

/** 名前の編集画面に関するクラス */
public class DecideName implements GlobalScene {
	private final int MaxLength=5;  //キャラ名の最大文字数
	private CharaData NowChara;
	private char[][] keyArrays;
	private String Name="";  //名前
	private int CursorX=0; //カーソルの指している位置(x座標)
	private int CursorY=0; //カーソルの指している位置(y座標)
	private final int START_X = (int)Math.round(0.15*Paramaters.Width);
	private final int START_Y = (int)Math.round(0.2*Paramaters.Height);
	private final int DX = (int)Math.round(0.07*Paramaters.Width);
	private final int DY = (int)Math.round(0.07*Paramaters.Height);
	
	
	private String[] strList={"アカサタナハマヤラワ",
							  "イキシチニヒミ　リ　",
							  "ウクスツヌフムユルヲ",
							  "エケセテネヘメ　レ　",
							  "オコソトノホモヨロン",
							  "ァガザダ　バパャーC",
							  "ィギジヂ　ビピ　　　",
							  "ゥグズヅッブプュ　　",
							  "ェゲゼデ　べペ　　R",
							  "ォゴゾド　ボポョ　E"};
	
	public DecideName(CharaData C){
		NowChara=C;
		Name = NowChara.getCharaName();
		keyArrays = new char[strList.length][strList[0].length()];
		for(int y=0;y<keyArrays.length;y++){
			for(int x=0;x<keyArrays[y].length;x++){
				keyArrays[y][x]=strList[y].charAt(x);
			}
		}
	}
	
	@Override
	public void drawing(Graphics g, Container con) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
		g.setColor(Color.yellow);
		g.setFont(new Font("明朝体",Font.BOLD,(int)Math.round(0.08*Paramaters.Height)));
		g.drawString("名前："+Name,(int)Math.round(0.15*Paramaters.Width),(int)Math.round(0.15*Paramaters.Height));
		g.setColor(Color.white);
		g.setFont(new Font("明朝体",Font.BOLD,(int)Math.round(0.05*Paramaters.Height)));
		double startX=0.15;
		double startY=0.2;
		double dx=0.07;
		double dy=0.07;
		for(int y=0;y<keyArrays.length;y++){
			for(int x=0;x<keyArrays[y].length;x++){
				if(keyArrays[y][x]=='　')continue;
				g.drawString(Character.toString(keyArrays[y][x]),(int)Math.round((x*dx+0.01+startX)*Paramaters.Width),
						(int)Math.round(((y+1)*dy-0.01+startY)*Paramaters.Height));
				g.drawRect((int)Math.round((x*dx+startX)*Paramaters.Width),(int)Math.round((y*dy+startY)*Paramaters.Height),
						(int)Math.round(dx*Paramaters.Width),(int)Math.round(dy*Paramaters.Height));
			}
		}
		g.setColor(new Color(0,150,0,150));
		g.fillRect((int)Math.round((CursorX*dx+startX)*Paramaters.Width), (int)Math.round((CursorY*dy+startY)*Paramaters.Height),
				(int)Math.round(dx*Paramaters.Width),(int)Math.round(dy*Paramaters.Height));
	}

	@Override
	public int pushDown() {
		Moving(0,1);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushEnter() {
		if(keyArrays[CursorY][CursorX]=='E'){
			if(Name.equals(""))return Paramaters.NON_EVENT;
			NowChara.changeName(Name);
			return Paramaters.DELETE_NOW_SCENE;
		}
		else if(keyArrays[CursorY][CursorX]=='C'){
			if(!Name.equals(""))Name=Name.substring(0, Name.length()-1);
		}
		else if(keyArrays[CursorY][CursorX]=='R'){
			Name=Paramaters.getRandomName(NowChara.isMeal());
		}
		else {
			if(Name.length()<MaxLength) Name+=keyArrays[CursorY][CursorX];
		}
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushEscape() {
		return Paramaters.DELETE_NOW_SCENE;
	}

	@Override
	public int pushLeft() {
		Moving(-1,0);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushRight() {
		Moving(1,0);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushUp() {
		Moving(0,-1);
		return Paramaters.NON_EVENT;
	}

	@Override
	public void running() {}
	
	/** カーソルを動かすメソッド */
	private void Moving(int x,int y){
		CursorX+=x;
		CursorY+=y;
		if(CursorX>=keyArrays[0].length)CursorX=0;
		if(CursorX<0)CursorX=keyArrays[0].length-1;
		if(CursorY>=keyArrays.length)CursorY=0;
		if(CursorY<0)CursorY=keyArrays.length-1;
		if(keyArrays[CursorY][CursorX]=='　')Moving(x,y);
	}

    @Override
    public void moveCursor(int x, int y) {
    	int indexX = (x-START_X)/DX;
    	int indexY = (y-START_Y)/DY;
    	if(indexX<0 || indexY<0 || indexY>=keyArrays.length || indexX>=keyArrays[0].length)return;
    	if(keyArrays[indexY][indexX] != '　'){
    		CursorX = indexX;
    		CursorY = indexY;
    	}
        
    }

}
