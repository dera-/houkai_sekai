package sim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JFrame;

import sim.game.*;
import sim.game.battle.BattleField;
import sim.game.data.CharaData;
import sim.tools.GraphicTool;

/** メイドさんシステム(ゲームの説明用のUI)に関するクラス */
public class MaidSystem extends JFrame{
	private ArrayList<Serif> SerifList = new ArrayList<Serif>();  //セリフリスト
	private Image offscreenImg;  //イメージオブジェクト
	private GraphicTool Tools;  //GraphicToolオブジェクト
	private CharaData Maid; //メイドさん。キャラオブジェクト。
	private Serif NowSerif=null;  //現在使用しているセリフ
	
	public MaidSystem(){
		super("メイドさんシステム");
		setLocation(0,Paramaters.Height+getInsets().top);  //出現位置
        setPreferredSize(new Dimension(Paramaters.Width_MaidSystem, Paramaters.Height_MaidSystem));  //ウィンドウの大きさを設定
		setResizable(false);  //ウィンドウサイズ変更不可
		pack();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        setVisible(true);
		setBackground(Color.black);
		Maid=CharaData.searchChara("メイドさん"); //メイドさんのデータを取ってくる
		createSerifList(); //セリフリストの作成
		Tools = new GraphicTool();
	}
	
	//repaintメソッドが呼び出すメソッド
	public void update(Graphics g){
		paint(g);
	}
	
	/** 大元の描画処理 */
	public void paint(Graphics g){
		//裏イメージを作成する
		if(offscreenImg==null){
			offscreenImg=createImage(Paramaters.Width_MaidSystem, Paramaters.Height_MaidSystem);
			Paramaters.Image_BOX.loadWait(offscreenImg, this);
		}
		//裏イメージを背景色で塗りつぶす
		Graphics offscreenG;
		offscreenG = offscreenImg.getGraphics();
		offscreenG.setColor(getBackground());
        offscreenG.fillRect(0, 0, Paramaters.Width_MaidSystem, Paramaters.Height_MaidSystem);
        
        //裏イメージにゲーム画面の描画を行う。
        offscreenG.setColor(Color.white);
        draw(offscreenG);
        
        //裏イメージを表イメージに貼り付ける
        g.drawImage(offscreenImg,0,0,this);
	}
	
	/** 実際の描画処理 */
	private void draw(Graphics g){
		if(NowSerif==null || Maid==null)return;
		//セリフ枠に関する変数群
		int fx = (int)Math.round(0.14*Paramaters.Width_MaidSystem);  //セリフ枠の左上端のx座標
		int fy = (int)Math.round(0.125*Paramaters.Height_MaidSystem);  //セリフ枠の左上端のy座標
		int fwidth = (int)Math.round(0.84*Paramaters.Width_MaidSystem);  //セリフ枠の横の長さ
		int fheight = (int)Math.round(0.75*Paramaters.Height_MaidSystem);  //セリフ枠の縦の長さ
		
		//メイドさんの画像の描画に関する処理
		Maid.setFaceIndex(NowSerif.FaceNumber);  //メイドさんの顔差分の決定
		Maid.drawFace(g, this,(int)Math.round(0.02*Paramaters.Width_MaidSystem) , (int)Math.round(0.25*Paramaters.Height_MaidSystem));  //メイドさんの顔画像の貼り付け
		Tools.drawFrame(g, this, fx, fy, fwidth, fheight);
		
		//メイドさんのセリフの描画に関する処理
		int font=20;
		g.setColor(Color.white);
		g.setFont(new Font("明朝体",Font.BOLD,font));
		String[] MessageArray=changeArray(NowSerif.Message,font+3,fwidth);
		for(int i=0;i<MessageArray.length;i++){
			g.drawString(MessageArray[i],(int)Math.round(fx+0.03*fwidth),(int)Math.round(fy+(0.3*(i+1))*fheight));
		}	
	}
	
	/**メイドさんのセリフを文字列配列に変換するメソッド
	 * @param str メイドさんのセリフ
	 * @param f １文字の大きさ
	 * @param w セリフ枠の横の長さ
	 * @return 文字列配列
	 * */
	private String[] changeArray(String str,int f,int w){
		int line=(int)Math.round(1.0*w/f);  //1行の文字数
		int size=str.length(); //セリフの文字数
		if(size==0)return new String[0];
		int R=(size-1)/line+1;  //行数
		String strs[]=new String[R];
		for(int i=0;i<R;i++){
			int last;
			if(i==R-1)last=size;
			else last=(i+1)*line;
			strs[i]=str.substring(i*line , last);
		}
		return strs;
	}
		
	/** セリフリストの作成 */
	private void createSerifList(){
		SerifList.clear();
		try{
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("MaidText.csv"))); //テキストファイルからの読み込み
            br.readLine(); //ヘッダの読み飛ばし
            String line;
			while((line=br.readLine())!=null){
				String strs[]=line.split(",");
				SerifList.add(new Serif(Integer.parseInt(strs[0]),Integer.parseInt(strs[1]),strs[2]));
			}
			br.close();
		}catch(Exception ex){
			System.out.println("セリフ読み込みエラー");
		}
	}
	
	/** セリフの取得 */
	public void setNowSerif(GlobalScene scene){
		int num=1000;
		if(scene instanceof Start){ //タイトル画面
			num = whenStart((Start)scene);
		}
		else if(scene instanceof FirstScene){  //タイトル画面で「はじめから」を選択した直後のキャラ登録画面
			num = 4;
		}
		else if(scene instanceof CharaCreate){ //１体のキャラの作成画面
			num = whenCharaCreate((CharaCreate)scene);
		}
		else if(scene instanceof Strategy){ //戦闘準備画面
			num = whenStrategy((Strategy)scene);
		}
		else if(scene instanceof StageSelect){ //戦闘MAPを選択する画面
			num = 39;
		}
		else if(scene instanceof BattleField){  //戦闘MAP
			num = whenBattle((BattleField)scene);
		}
		else if(scene instanceof ParamaterScene){  //パラメーターの閲覧時
			num = whenParamaterScene((ParamaterScene)scene);
		}
		else if(scene instanceof DecideJob){  //職業の変更画面
			num = whenDecideJob((DecideJob) scene);
		}
		else if(scene instanceof MatchScene){  //継承システム
			num = whenMatchScene((MatchScene)scene);
		}
		else if(scene instanceof ChildCreate){  //キャラ(子供)の作成画面
			num = whenChildCreate((ChildCreate)scene);
		}
		else if(scene instanceof SaveScene){ //セーブ画面
			num = 92;
		}
		else if(scene instanceof LoadScene){  //ロード画面
			num = 93;
		}
		else if(scene instanceof GameClear){  //ゲームクリア時。エンディング？
			num = 96;
		}
		else if(scene instanceof GameOver){ //ゲームオーバー画面
			num=98;
		}
		NowSerif = getSerif(num);
	}
	
	/** スタート画面の時のセリフ */
	private int whenStart(Start scene){
		switch(scene.getNowSelect()){
		case -1: return 0;
		case 0: return 1;
		case 1: return 2;
		case 2: return 3;
		default:return 1000;
		}
	}
	
	/** キャラ作成画面におけるセリフ */
	private int whenCharaCreate(CharaCreate scene){
		GlobalScene global=scene.getNowScene();
		if(global==null){
			switch(scene.getNowSelect()){
				case -1: return 5;
				case 0: return 6;
				case 1: return 7;
				case 2: return 8;
				case 3: return 9;
				case 4: return 10;
				case 5: if(scene.isFinish())return 11;
						else return 100;
				default: return 1000;
			}
		}
		else{
			if( global instanceof DecideName )return 12;
			if( global instanceof DecideSex)return 13;
			if( global instanceof DecideFace)return 14;
			if( global instanceof DecidePropers)
				return whenDecidePropers((DecidePropers) global);
			if( global instanceof DecideJob)
				return whenDecideJob((DecideJob)global);			
		}
		return 1000;
	}
	
	/** 適正値を決定する画面におけるセリフの取得 */
	private int whenDecidePropers(DecidePropers propers){
		return propers.getComment();
	}
	
	/** 職業を決定する画面におけるセリフの取得 */
	private int whenDecideJob(DecideJob job){
		return job.getComment();
	}
	
	/** メニュー画面におけるセリフの取得　*/
	private int whenStrategy(Strategy stra){
		return stra.getComment();
	}
	
	/** 戦闘画面におけるセリフの取得 */
	private int whenBattle(BattleField battle){
		return battle.getComment();
	}
	
	/** ステータス参照画面におけるセリフの取得 */
	private int whenParamaterScene(ParamaterScene pram){
		if(Paramaters.AllyList!=pram.getCharaList())return 85;
		return pram.getComment();
	}
	
	/** 継承システムの画面におけるセリフの取得 */
	private int whenMatchScene(MatchScene match){
		return match.getComment();
	}
	
	/** 子供の能力設定画面におけるセリフの取得 */
	private int whenChildCreate(ChildCreate child){
		GlobalScene global=child.getNowScene();
		if(global==null){
			switch(child.getNowSelect()){
			case -1: return 91;
			case 0: return 6;
			case 1: return 9;
			case 2: return 10;
			case 3: if(child.isFinish())return 11;
					else return 100;
			}
		}
		else{
			if( global instanceof DecideName )return 12;
			if( global instanceof DecidePropers)
				return whenDecidePropers((DecidePropers) global);
			if( global instanceof DecideJob)
				return whenDecideJob((DecideJob)global);
		}
		return 1000;
	}
	
	/** セリフ番号からセリフを取得するメソッド */
	private Serif getSerif(int num){
		for(Serif ser : SerifList){
			if(num==ser.Number)return ser;
		}
		return new Serif(1000,0,"");
	}
	
}

/** メイドさんのセリフに関するクラス */
class Serif{
	String Message;  //セリフ
	int FaceNumber; //顔画像の差分番号
	int Number;	  //セリフの番号
	public Serif(int n,int f,String mes){
		Number=n;
		FaceNumber=f;
		Message=mes;
	}
	
}