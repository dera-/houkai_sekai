package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;

import sim.game.data.CharaData;
import sim.tools.Select;

/** キャラクリエイトに関するクラス */
public class CharaCreate extends Select implements GlobalScene {
	private CharaData Before;  //編集前のデータ
	private CharaData NowChara; //編集中のキャラのデータ
	private final boolean CanReset;
	private GlobalScene NowScene=null;  //現在、どの部分の編集を行っているか
			
	public CharaCreate(CharaData C){
		super(0.05,0.3,0.5,0.3,6,6);
		CanReset=true;
		if(C==null) NowChara=new CharaData();
		else NowChara=C;
		Before = CharaData.getClone(C);
		Names[0]="名前の決定";
		Names[1]="性別の決定";
		Names[2]="キャラの顔の決定";
		Names[3]="能力の割り振り";
		Names[4]="職業の決定";
		Names[5]="終了";
		for(int i=0;i<Permits.length;i++){
			Permits[i]=true;
		}
	}

	//描画メソッド
	public void drawing(Graphics g, Container con) {
		if(NowScene==null){
			g.setColor(Color.black);
			g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
			Draw(g,con);
			if(NowChara==null)return;
			NowChara.drawStatus(g, con, (int)Math.round(0.4*Paramaters.Width), 0,
				(int)Math.round(0.6*Paramaters.Width), Paramaters.Height);
		}
		else NowScene.drawing(g, con);
	}
	
	@Override
	//↓キーを押した時に呼び出されるメソッド
	public int pushDown() {
		if(NowScene==null)Moving(true);
		else NowScene.pushDown();
		return Paramaters.NON_EVENT;
	}
	
	@Override
	//エンターキーを押した時に呼び出されるメソッド
	public int pushEnter() {
		if(NowScene==null){
			switch(kettei){
			case 0: //キャラの名前の編集
				NowScene=new DecideName(NowChara);
				return Paramaters.NON_EVENT;
			case 1: //キャラの性別の決定
				NowScene=new DecideSex(NowChara);
				return Paramaters.NON_EVENT;
			case 2:  //キャラの顔グラフィックの決定
				NowScene=new DecideFace(NowChara);
				return Paramaters.NON_EVENT;
			case 3:  //職業適正度等の編集
				NowScene=new DecidePropers(NowChara);
				return Paramaters.NON_EVENT;
			case 4:  //職業の決定
				NowScene=new DecideJob(NowChara);
				return Paramaters.NON_EVENT;
			case 5:  //クリエイト終了
				if(NowChara.getJobListSize()>0) return Paramaters.DECIDE_CHARACTER;
			default:return Paramaters.NON_EVENT;
			}
		}
		//キャラクリエイトに戻る
		else if(NowScene.pushEnter() == Paramaters.DELETE_NOW_SCENE){
			NowScene=null;
		}
		return Paramaters.NON_EVENT;
	}

	@Override
	//エスケープキーを押した時に呼び出されるメソッド
	public int pushEscape() {
		//キャラ登録シーンに戻る
		if(NowScene==null && CanReset){
			NowChara=Before;
			return Paramaters.DECIDE_CHARACTER;
		}
		//各編集画面からキャラクリエイトに戻る
		else if(NowScene!=null && NowScene.pushEscape() == Paramaters.DELETE_NOW_SCENE){
			NowScene=null;
			return Paramaters.NON_EVENT;
		}
		else return Paramaters.NON_EVENT;
	}

	@Override
	//←キーを押した時に呼び出されるメソッド
	public int pushLeft() {
		if(NowScene!=null)NowScene.pushLeft();
		return Paramaters.NON_EVENT;
	}

	@Override
	//→キーを押した時に呼び出されるメソッド
	public int pushRight() {
		if(NowScene!=null)NowScene.pushRight();
		return Paramaters.NON_EVENT;
	}

	@Override
	//↑キーを押した時に呼び出されるメソッド
	public int pushUp() {
		if(NowScene==null)Moving(false);
		else NowScene.pushUp();
		return Paramaters.NON_EVENT;
	}
	
	/** フィールドNowCharaを返すメソッド */
	public CharaData getNowChara(){
		return NowChara;
	}

	@Override
	//別スレッドから呼び出されるメソッド
	public void running(){
		if(NowScene!=null)NowScene.running();
		else if(NowChara!=null) Permits[5]=(NowChara.getJobListSize()>0);
	}
	
	/** NowSceneに格納されているオブジェクトを返すメソッド */
	public GlobalScene getNowScene(){
		return NowScene;
	}
	
	/** kettei変数が指している場所を取得するメソッド */
	public int getNowSelect(){
		return kettei;
	}
	
	/** クリエイト画面を終了できるかどうか(対象のキャラが職業についているかどうか) */
	public boolean isFinish(){
		return Permits[5];
	}

    @Override
    public void moveCursor(int x, int y) {
        if(NowScene != null) NowScene.moveCursor(x,y);
        else decideNowSelectedItem(x,y);
    }

}
