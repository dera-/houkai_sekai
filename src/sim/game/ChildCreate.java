package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;

import sim.game.data.CharaData;
import sim.tools.Select;

/** キャラ(子供)のクリエイトに関するメソッド */
public class ChildCreate extends Select implements GlobalScene {
	private CharaData Child;  //対象のキャラ(子供)
	private int[] LevelUp={0,0,0,0,0,0,0,0,0,0};  //親から引き継がれたボーナス職業レベル
	private int[] Propers={0,0,0,0,0,0,0,0,0,0};  //親から引き継がれたボーナス適正値
	private GlobalScene NowScene=null;   //現在、どの部分の編集を行っているか
	
	public ChildCreate(CharaData[] array){
		super(0.05,0.3,0.3,0.3,4,4);
		Child = new CharaData(array);
		setArrays(array);
		Names[0]="名前の決定";
		Names[1]="能力の割り振り";
		Names[2]="職業の決定";
		Names[3]="終了";
		for(int i=0;i<Permits.length;i++){
			Permits[i]=true;
		}
		//Childの適正度にフィールドPropertiesの値をコピーする
		Child.setPropers(Propers);
		//ここで、両親をParamatersクラスのAllyList中から除外する
		for(CharaData C : array)Paramaters.removeAlly(C);
	}
	
	/** LevelUpとPropersに値を与えるメソッド */
	private void setArrays(CharaData[] array){
		int joblv1[]=array[0].getJobLVArray();
		int joblv2[]=array[1].getJobLVArray();
		int thresholds[]={4,10,12};
		for(int i=0;i<Propers.length;i++){
			int j1=joblv1[i];
			int j2=joblv2[i];
			int total=j1+j2;
			if(total>=thresholds[1]){
			    int diff = (total - thresholds[1]);
				Propers[i] = (int) Math.floor(15 + 2.5*diff);
				LevelUp[i] = 2 + diff;
//				if(total==thresholds[2])LevelUp[i]=3;
//				else LevelUp[i]=2;
			}
			else{
				if(j1>=thresholds[0]){
					Propers[i]+=5;
					LevelUp[i]+=(j1-thresholds[0]);
				}
				if(j2>=thresholds[0]){
					Propers[i]+=5;
					LevelUp[i]+=(j2-thresholds[0]);
				}
			}
		}	
	}

	@Override
	//描画メソッド
	public void drawing(Graphics g, Container con) {
		if(NowScene==null || NowScene instanceof EndCheckScene){
			//黒で全体を塗りつぶし
			g.setColor(Color.black);
			g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
			//選択メニューの描画
			Draw(g,con);
			
			//「新規キャラの能力設定」という文字列の描画
			g.setColor(Color.white);
			String Message="新規キャラの能力設定";
			int font=(int)Math.round(0.04*Paramaters.Height);
			g.setFont( new Font("明朝体",Font.BOLD,font) );
			int startX=(int)Math.round((0.35*Paramaters.Width-(font+1)*Message.length())/2);
			g.drawString(Message,startX,(int)Math.round(Pt.y-0.05*Paramaters.Height));
			
			if(Child==null)return;
			//子供の基本ステータスの描画
			Child.drawStatus(g, con, (int)Math.round(0.4*Paramaters.Width), 0,
				(int)Math.round(0.6*Paramaters.Width), Paramaters.Height);
			if(NowScene!=null)NowScene.drawing(g, con);
		}
		else NowScene.drawing(g, con);
	}

	@Override
	//↓キーを押したときに呼び出されるメソッド
	public int pushDown() {
		if(NowScene == null)Moving(true);
		else NowScene.pushDown();
		return Paramaters.NON_EVENT;
	}

	@Override
	//エンターキーを押したときに呼び出されるメソッド
	public int pushEnter() {
		if(NowScene!=null){
			int enter=NowScene.pushEnter();
			if(enter == Paramaters.DELETE_NOW_SCENE || enter==Paramaters.DECIDE_NO){
				NowScene=null;
			}
			else if(enter == Paramaters.DECIDE_YES){
				Child.setJobLV(LevelUp);
				Child.upJobLV(1);  //現在の職業レベルを6つ上げる
				Child.createSkillList();  //職業履歴に対応した技をセットする
				return Paramaters.DECIDE_CHILD;
			}
		}
		else{
			if(kettei==-1 || !Permits[kettei])return Paramaters.NON_EVENT;
			switch(kettei){
			case 0: 
				NowScene=new DecideName(Child);
				return Paramaters.NON_EVENT;
			case 1:
				NowScene=new DecidePropers(Child,Propers);
				return Paramaters.NON_EVENT;
			case 2:
				NowScene=new DecideJob(Child);
				return Paramaters.NON_EVENT;
			case 3:
				NowScene=new EndCheckScene();
				return Paramaters.NON_EVENT;
			default:return Paramaters.NON_EVENT;
			}
		}
		return Paramaters.NON_EVENT;
	}

	@Override
	//エスケープキーを押したときに呼び出されるメソッド
	public int pushEscape() {
		if(NowScene!=null){
			NowScene.pushEscape();
			NowScene=null;
		}
		return Paramaters.NON_EVENT;
	}

	@Override
	//←キーを押したときに呼び出されるメソッド
	public int pushLeft() {
		if(NowScene != null)NowScene.pushLeft();
		return Paramaters.NON_EVENT;
	}

	@Override
	//→キーを押したときに呼び出されるメソッド
	public int pushRight() {
		if(NowScene != null)NowScene.pushRight();
		return Paramaters.NON_EVENT;
	}

	@Override
	//↑キーを押したときに呼び出されるメソッド
	public int pushUp() {
		if(NowScene==null) Moving(false);
		else NowScene.pushUp();
		return Paramaters.NON_EVENT;
	}
	
	/** 作成した子供を返り値として返す */
	public CharaData getChild(){
		return Child;
	}
	
	/** フィールドNowSceneを返すメソッド */
	public GlobalScene getNowScene(){
		return NowScene;
	}
	
	/** ketteiの値を返すメソッド */
	public int getNowSelect(){
		return kettei;
	}
	
	@Override
	public void running(){
		if(NowScene != null) NowScene.running();
		else if(Child!=null) Permits[3]=(Child.getJobListSize()>0);
	}
	
	/** クリエイト画面を終了できるかどうか(対象のキャラが職業についているかどうか) */
	public boolean isFinish(){
		return Permits[3];
	}

    @Override
    public void moveCursor(int x, int y) {
        if(NowScene != null) NowScene.moveCursor(x, y);
        decideNowSelectedItem(x,y);
    }

}
