package sim.tools;

import java.awt.*;

import sim.game.Paramaters;

/** 選択メニューに関するクラスのスーパークラス */
public abstract class Select {
	public static final int NOT_SELECT = -1;
	protected final int Swide,Shigh; //選択メニューの横と縦の長さ
	protected final Point Pt;   //選択メニューの左上の端の座標
	protected final int Desition; //項目間の間隔
	protected final int Nums; //目に見える項目の個数
	protected int kettei = NOT_SELECT;  //今、選んでいる選択肢
	protected int screen=0;  //何スクロール目かを表す
	protected String [] Names; //リストに載っている名前
	protected boolean[] Permits; //選択できるかどうか
	protected GraphicTool Tools;
	protected int cursorX = 0;  //マウスカーソルのx座標
	protected int cursorY = 0;  //マウスカーソルのy座標
	
	/** コンストラクタ
	 *  @param dx 選択メニューの左上端のx座標
	 *  @param dy 選択メニューの左上端のy座標
	 *　  @param main ゲーム画面の横の長さに対する選択メニューの横の長さの割合
	 *  @param sub  ゲーム画面の縦の長さに対する選択メニューの縦の長さの割合
	 *  @param num １度に表示させる事ができる選択肢の数。
	 *  @param Real_Num 実際の選択肢の数
	 **/
    public Select(double dx,double dy,double main,double sub,int num,int Real_Num){
    	int x=(int)Math.round(dx*Paramaters.Width);
    	int y=(int)Math.round(dy*Paramaters.Height);
    	Pt=new Point(x,y);
		Shigh=(int)Math.round(main*Paramaters.Height);
		Swide=(int)Math.round(sub*Paramaters.Width);
		Desition=(int)Math.round(Shigh/num);
		Nums=num;
		Names = new String[Real_Num];
		Permits = new boolean[Real_Num]; 
		Tools=new GraphicTool();
	}

    /** カーソルを動かすメソッド。引数がtrueなら上へ、falseなら下へ動かす。 */
	public void Moving(boolean ahead){
		if(Names.length==0)return;
		if(ahead){
			  if(kettei>=Names.length-1)kettei=0;
			  else kettei++;
		}//前へGO
		else{
			if(kettei<=0)kettei=Names.length-1;
			else kettei--;
		}//後ろへGO
		screen=(int)Math.floor(1.0*kettei/Nums);
	}
	
	/** 選択メニューの描画を行うメソッド */
	public void Draw(Graphics gx,Container con){
	    Draw(gx,con,null);
	}
	
	public void Draw(Graphics gx,Container con,Color[] colors){
	    String[] nowS = new String[Nums];
	    boolean[] nowB = new boolean[Nums];
	    Color[] cols = new Color[Nums];
	    for(int i=0;i<Nums;i++){
	        if(screen*Nums+i>=Names.length){
	            nowS[i] = "";
	            nowB[i] = false;
	            cols[i] = Tools.getMenuItemColor();
	        }
	        else{
	            nowS[i] = Names[screen*Nums+i];
	            nowB[i] = Permits[screen*Nums+i];
	            cols[i] = (colors==null ? Tools.getMenuItemColor() : colors[screen*Nums+i]);
	        }
	    }
	    Tools.drawSelect(gx,con,Pt.x,Pt.y,Swide,Shigh,nowS,nowB,kettei-Nums*screen,cols);
	    if(Names.length > Nums){
	    	int height_mini = (int)Math.round(1.0*Shigh/Nums);
	    	gx.setColor(Color.white);
	    	gx.setFont(new Font("",Font.BOLD,(int)Math.round(0.7*height_mini)));
	    	Tools.drawFrame(gx, con, Pt.x, Pt.y+Shigh, Swide, height_mini);
	    	gx.drawString("次の選択肢", (int)Math.round(Pt.x+0.05*Swide), (int)Math.round(Pt.y+Shigh+0.85*height_mini));
	    	if(pointToNextScreen(cursorX, cursorY)) Tools.setTransmission(gx, Pt.x, Pt.y+Shigh, Swide, height_mini, Color.yellow, 0.5f);
	    }
	}
	
	/** 引数の座標から現在さしている選択肢の番号をフィールドketteiに代入するメソッド */
	protected void decideNowSelectedItem(int x,int y){
	    if(x<Pt.x || x>Pt.x+Swide || y<Pt.y || y>Pt.y+Shigh){
	    	kettei=-1;
	    	return;
	    }
	    int num = -1;
	    for(int i=0;i<Nums;i++){
	        if( Pt.y+Desition*i <= y && y< Pt.y+Desition*(i+1)){
	            num = i;
	            break;
	        }
	    }
	    if(num == -1 || screen*Nums + num >= Names.length) kettei = -1;
	    else kettei = screen*Nums + num;
	}
	
	/** 次の選択肢に飛ぶことを意味する項目をマウスカーソルが示してるかどうかを判定するメソッド */
	protected boolean pointToNextScreen(int x, int y){
		if(Names.length <= Nums)return false;
		return (Pt.x <= x && x < Pt.x+Swide && Pt.y+Shigh <= y && y < (int)Math.round(Pt.y+(1+1.0/Nums)*Shigh));
	}
	
	/** 次の選択肢に飛ぶ処理を行う */
	protected void jumpNextScreen(){
		if(Names.length <= Nums*(screen+1)){
			screen = 0;
			kettei = 0;
		}
		else{
			screen++;
			kettei = Nums * screen;
		}
	}
}
