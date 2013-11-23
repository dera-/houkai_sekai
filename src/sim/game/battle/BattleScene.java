package sim.game.battle;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import sim.game.Paramaters;
import sim.game.brain.BattleCalculation;
import sim.game.brain.CounterBrain;
import sim.game.data.CharaData;
import sim.game.data.Map;
import sim.game.data.Skill;
import sim.tools.MapHandling;
import sim.tools.Select;

public class BattleScene extends Select implements Scene {
	protected MapHandling mapHandling;
	protected CharaData Ally;
	protected CharaData Enemy;
	protected Skill AllySkill=null;
	protected Skill EnemySkill=null;
	public final boolean First;
	protected int Situation;  //場面番号
	protected int[] SituationArray;  //場面配列
	protected final int[] FirstArrays={0,1,2,3,4,5};
	protected final int[] SecondArrays={0,3,4,1,2,5};
	protected boolean hit=true; //技がhitしたかどうか
	protected int Damege=0; //技のダメージの値
	protected boolean Unusuals_Hit[]={false,false,false}; //状態異常にかかったかどうか
	protected Integer Damege_Estimate=null;
	protected Double HitRate_Estimate=null;
	protected int LV_UP=0;
	protected int JOB_LV_UP=0;
	protected int Exp=0;     //戦って得られた経験値
	protected final int NowExp;  //戦う前のプレイヤーキャラの経験値
	private int[] GapLV={-90,-60,-30,31,61,91};
	private int[] ExpList={1,5,10,20,35,50,80}; //変更前は、1,3,5,10,20,30,50
	private final int DefeatBonus=3;
	private final double NotHit = 0.5; //本来は0.2
	private final int JobExp_Normal=1;
	private final int JobExp_Bonus=5;
	private long intervalTime = 600;
	private static final long quickTime = 10;
	private long beforeActionTime = System.currentTimeMillis();
	private boolean flags[] = {false,false};
	private static final int ANIMATION_START_FLAG = 0;
	private static final int SCENE_FINISH_FLAG = 1;
	
	public BattleScene(CharaData A,Skill AS,CharaData E, MapHandling handle,boolean first){
		super(0.4,0.65,0.2,0.2,2,2);
		mapHandling = handle;
		First=first;
		if(First){
			Ally=A;
			Enemy=E;
			AllySkill=AS;
			CounterBrain Counter = new CounterBrain(A, E, AS, mapHandling.getMap(), false);
			EnemySkill = Counter.decideEnemySkill();
			SituationArray=FirstArrays;
		}
		else{
			Ally=E;
			Enemy=A;
			EnemySkill=AS;
			CounterBrain Counter = new CounterBrain(E, A, AS, mapHandling.getMap(), true);
			AllySkill = Counter.decideEnemySkill();
			SituationArray=SecondArrays;
		}
		formatArrays();
		Situation=0;
		NowExp=Ally.getExperiment();
	}
	
	/** 配列の初期化 */
	public void formatArrays(){
		Names[0]="戦闘開始";
		Names[1]="スキル選択";
		Permits[0]=true;
		Permits[1]=true;
	}
	
	/** Damege_Estimateに値を与える */
	public void setEstimateDamege(int d){
		Damege_Estimate=d;
	}
	
	/** HitRate_Estimateに値を与える */
	public void setEstimateHitRate(double h){
		HitRate_Estimate=h;
	}
	
	/** メイドさんの解説文の番号を取得するメソッド */
	public int getComment(){
		if(Situation>=SituationArray.length)return 1000;
		switch(SituationArray[Situation]){
		case 0: if(First)return 56;
				else return 78;
		case 5: return 60;
		case 6: return 61;
		case 7: return 62;
		default: return 1000;
		}
	}

	@Override
	public void draw(Graphics g,Container con) {
		if(Situation>=SituationArray.length)return;
		switch(SituationArray[Situation]){
		/* 戦闘結果の予測 */
		case 0: drawBattleEstimate(g,con);
				break;
		/* 味方の攻撃 */
		case 1: drawAttack(g);
				break;
		/* 味方の攻撃結果 */
		case 2: drawAttackResult(g);
				break;
		/* 敵の攻撃 */
		case 3: drawAttack(g);
				break;
		/* 敵の攻撃結果 */
		case 4: drawAttackResult(g);
				break;
		/*戦闘結果の表示*/
		case 5: drawResult(g,con);
				break;
		/* 敵を撃破した時に表示 */
		case 6: drawResult(g,con);
				break;
		/* 敵に撃破された時に表示 */
		case 7: drawResult(g,con);
				break;
		default: break;
		}
	}
	
	/** 戦闘結果の予測を描画 */
	protected void drawBattleEstimate(Graphics g,Container con){
		Draw(g,con);
		String items[]={"所属","キャラ名","技","残りHP","ダメージ","命中率","状態異常"};
		Tools.drawTable(g, con, (int)Math.round(0.4*Paramaters.Width), (int)Math.round(0.25*Paramaters.Height), 
				(int)Math.round(0.2*Paramaters.Width), (int)Math.round(0.4*Paramaters.Height), items);
		
		/* 自軍に関する表の作成 */
		String AS="反撃不能";
		String Unusual_A="-";
		if(AllySkill!=null){
			AS=AllySkill.getName();
			Unusual_A=AllySkill.getUnusuals_String();
		}
		String NowHP_A=Integer.toString(Ally.getNowHP());
		String Damege_A=Integer.toString(BattleCalculation.getDamege(AllySkill, EnemySkill , Ally , Enemy , mapHandling.getMap()));
		String Hit_A=Integer.toString( (int)Math.round(100*BattleCalculation.getHitRate(AllySkill, EnemySkill , Ally , Enemy , mapHandling.getMap())) );
		String AllyItems[]={"自軍",Ally.getCharaName(),AS,NowHP_A,Damege_A,Hit_A+"%",Unusual_A};
		Tools.drawTable(g,con,(int)Math.round(0.1*Paramaters.Width),(int)Math.round(0.25*Paramaters.Height),
				(int)Math.round(0.3*Paramaters.Width), (int)Math.round(0.4*Paramaters.Height), AllyItems);
		Ally.drawFace(g, con, (int)Math.round(0.1*Paramaters.Width), (int)Math.round(0.08*Paramaters.Height));
		
		/* 敵軍に関する表の作成 */
		String ES="反撃不能";
		String Unusual_E="-";
		if(EnemySkill!=null){
			ES=EnemySkill.getName();
			Unusual_E=EnemySkill.getUnusuals_String();
		}
		String NowHP_E=Integer.toString(Enemy.getNowHP());
		String Damege_E=Integer.toString(BattleCalculation.getDamege(EnemySkill, AllySkill, Enemy, Ally, mapHandling.getMap()));
		String Hit_E=Integer.toString((int)Math.round(100*BattleCalculation.getHitRate(EnemySkill, AllySkill, Enemy, Ally, mapHandling.getMap())));
		String EnemyItems[]={"敵軍",Enemy.getCharaName(),ES,NowHP_E,Damege_E,Hit_E+"%",Unusual_E};
		Tools.drawTable(g, con,(int)Math.round(0.6*Paramaters.Width),(int)Math.round(0.25*Paramaters.Height),
				(int)Math.round(0.3*Paramaters.Width), (int)Math.round(0.4*Paramaters.Height), EnemyItems);
		Enemy.drawFace(g, con, (int)Math.round(0.6*Paramaters.Width), (int)Math.round(0.08*Paramaters.Height));
	}
	
	/** 攻撃予告の描画 */
	protected void drawAttack(Graphics g){
		String messe="";
		Color col=Color.black;
		int num=SituationArray[Situation];
		if(num==1){
			messe="Ally's Attack";
			col=Color.blue;
		}
		else if(num==3){
			messe="Enemy's Attack";
			col=Color.red;
		}
		g.setColor(col);
		g.setFont(new Font("明朝体",Font.BOLD,30));
		g.drawString(messe,(int)Math.round(0.4*Paramaters.Width),(int)Math.round(0.5*Paramaters.Height));
	}
	
	/** 攻撃結果の描画 */
	protected void drawAttackResult(Graphics g){
		g.setFont(new Font("明朝体",Font.BOLD,30));
		int num=SituationArray[Situation];
		Color col=Color.black;
		if(num==2){
			col=Color.blue;
		}
		else if(num==4){
			col=Color.red;
		}
		g.setColor(col);
		if(hit){
			/*
			int damege=0;
			if(num==2)damege=BattleCalculation.getDamege(AllySkill, EnemySkill , Ally , Enemy , map);
			else if(num==4)damege=BattleCalculation.getDamege(EnemySkill, AllySkill, Enemy, Ally, map);
			*/
			g.drawString("HIT!", (int)Math.round(0.4*Paramaters.Width), (int)Math.round(0.4*Paramaters.Height));
			g.drawString(Integer.toString(Damege), (int)Math.round(0.45*Paramaters.Width), (int)Math.round(0.45*Paramaters.Height));
			String[] strs={"毒発症","失神","骨折"};
			double dy=0.5;
			double val=0.05;
			for(int i=0;i<strs.length;i++){
				if(Unusuals_Hit[i]){
					g.drawString(strs[i], (int)Math.round(0.4*Paramaters.Width), (int)Math.round(dy*Paramaters.Height));
					dy+=val;
				}
			}
		}
		else{
			g.drawString("Miss...", (int)Math.round(0.4*Paramaters.Width), (int)Math.round(0.4*Paramaters.Height));
		}
	}
	
	/**戦闘結果の表示*/
	protected void drawResult(Graphics g,Container con){
		int font=(int)Math.round(0.03*Paramaters.Height);
		g.setColor(Color.white);
		Ally.drawFace(g, con,(int)Math.round(0.1*Paramaters.Width), (int)Math.round(0.03*Paramaters.Height));
		Enemy.drawFace(g, con,(int)Math.round(0.7*Paramaters.Width),(int)Math.round(0.03*Paramaters.Height));		
		Ally.drawBarGraph(g, (int)Math.round(0.15*Paramaters.Width), (int)Math.round(0.25*Paramaters.Height), 
				(int)Math.round(0.2*Paramaters.Width), (int)Math.round(0.05*Paramaters.Height), "残りHP", Ally.getHP(), Ally.getNowHP(),font);
		Enemy.drawBarGraph(g, (int)Math.round(0.72*Paramaters.Width), (int)Math.round(0.25*Paramaters.Height), 
				(int)Math.round(0.2*Paramaters.Width), (int)Math.round(0.05*Paramaters.Height), "残りHP", Enemy.getHP(), Enemy.getNowHP(),font);
		g.setFont(new Font("明朝体",Font.BOLD,font));
		g.setColor(Color.white);
		g.drawString("名前:"+Ally.getCharaName(),(int)Math.round(0.1*Paramaters.Width),(int)Math.round(0.22*Paramaters.Height));
		g.drawString("名前:"+Enemy.getCharaName(),(int)Math.round(0.7*Paramaters.Width),(int)Math.round(0.22*Paramaters.Height));
		g.drawString("状態異常:"+Ally.StateString(),(int)Math.round(0.1*Paramaters.Width),(int)Math.round(0.35*Paramaters.Height));
		g.drawString("状態異常:"+Enemy.StateString(),(int)Math.round(0.7*Paramaters.Width),(int)Math.round(0.35*Paramaters.Height));
		
		int Length=2;
		if(LV_UP>0)Length++;
		if(JOB_LV_UP>0)Length++;
		String[] messages = new String[Length];
		messages[0]=getMessage();
		messages[1]="経験値獲得："+NowExp+"→"+Ally.getExperiment();
		if(LV_UP>0) messages[2]="LVアップ："+(Ally.getLV()-LV_UP)+"→"+Ally.getLV();
		if(JOB_LV_UP>0) messages[messages.length-1]="職業LVアップ："+(Ally.getJobLV()-JOB_LV_UP)+"→"+Ally.getJobLV();
		Tools.drawTable(g, con,(int)Math.round(0.4*Paramaters.Width),(int)Math.round(0.5*Paramaters.Height),
				(int)Math.round(0.2*Paramaters.Width), (int)Math.round(0.05*messages.length*Paramaters.Height), messages);
	    g.setFont(new Font("明朝体",Font.BOLD,font));
	    g.setColor(Color.white);
		g.drawString("エンターキーを押してください", (int)Math.round(0.5*Paramaters.Width), (int)Math.round(0.9*Paramaters.Height));
	}
	
	/** 戦闘終了後のメッセージの取得 */
	protected String getMessage(){
		String mes="";
		int num=SituationArray[Situation];
		if(num==5){
			mes="戦闘終了";
		}
		else if(num==6){
			mes="敵撃破";
		}
		else if(num==7){
			mes="味方撃破";
		}
		return mes;
	}

	@Override
	public boolean keyEventDown() {
		if(Situation==0) Moving(true);
		return false;
	}

	@Override
	public boolean keyEventEnter() {
		if(kettei==-1 || !Permits[kettei])return false;
		else if(kettei==1 || flags[SCENE_FINISH_FLAG])return true;
		else if(!flags[ANIMATION_START_FLAG]){
		    flags[ANIMATION_START_FLAG] = true;
		}
		else{
		    intervalTime = quickTime;
		}
		return false;
	}

	@Override
	public boolean keyEventEscape() {
		if(Situation==0)return First;
		else return false;
	}

	@Override
	/** ここでは使用しない */
	public boolean keyEventLeft() {
		return false;
	}

	@Override
	/** ここでは使用しない */
	public boolean keyEventRight() {
		return false;
	}

	@Override
	public boolean keyEventUp() {
		// TODO Auto-generated method stub
		if(Situation==0) Moving(false);
		return false;
	}
	
	/** 変数ketteiの値を返す */
	public int getNumber(){
		return kettei;
	}
	
	/** 次の場面に飛ぶ */
	protected void nextSituation(){
		if( Situation!=0 || (Situation==0 && kettei==0) ){
			Situation++;
			if(Situation>=SituationArray.length)return;
			Point point = null;
			if(SituationArray[Situation]==1 || SituationArray[Situation]==4){
				point = Ally.getPlace();
			}
			else if(SituationArray[Situation]==2 || SituationArray[Situation]==3){
				point = Enemy.getPlace();
			}
			if(point == null)return;
	        mapHandling.moveCenterToAssinedPlace(point.x, point.y);
	        mapHandling.moveCursorToAssinedPlace(point.x, point.y);
		}
	}
	
	/** 何かしらの処理を行う */
	protected void happenEvent(){
		int now=SituationArray[Situation];
		switch(now){
			//敵への攻撃
			case 2: detailBattle(true);
					detailUnusual(true,hit);
					getExp();
					getJobExp();
					if(Enemy.isDie())SituationArray[Situation+1]=6;
					else if(EnemySkill==null || Enemy.checkUnusual(1)>0)SituationArray[Situation+1]=5;
					break;
			//敵からの攻撃
			case 4: detailBattle(false);
					detailUnusual(false,hit);
					if(Ally.isDie())SituationArray[Situation+1]=7;
					else if(AllySkill==null || Ally.checkUnusual(1)>0)SituationArray[Situation+1]=5;
					break;
			//戦闘終了時
			case 5: Situation=SituationArray.length-1;
					SituationArray[Situation]=5;
					break;
			//撃破した時
			case 6: Situation=SituationArray.length-1;
					SituationArray[Situation]=6;
					break;
			//撃破された時
			case 7: Situation=SituationArray.length-1;
					SituationArray[Situation]=7;
					break;
			default: break;
		}
	}
	
	/** 経験値を得るメソッド */
	protected void getExp(){
		int LV_ally=Ally.getLV();
		int proper_ally=Ally.getProper();
		int LV_enemy=Enemy.getLV();
		int proper_enemy=Enemy.getProper();
		int gap = LV_enemy*proper_enemy - LV_ally*proper_ally;
		if(gap<GapLV[0])Exp=ExpList[0];
		else if(gap<GapLV[1])Exp=ExpList[1];
		else if(gap<GapLV[2])Exp=ExpList[2];
		else if(gap<GapLV[3])Exp=ExpList[3];
		else if(gap<GapLV[4])Exp=ExpList[4];
		else if(gap<GapLV[5])Exp=ExpList[5];
		else Exp=ExpList[6];
		if(Enemy.isDie())Exp*=DefeatBonus;
		else if(!hit)Exp=(int)Math.round(NotHit*Exp);
		LV_UP=Ally.getExp(Exp);
	}
	
	/** 職業経験値を得るメソッド */
	protected void getJobExp(){
		int job;
		if(Enemy.isDie())job=JobExp_Bonus;
		//else if(hit)job=JobExp_Normal;
		else job = JobExp_Normal;
		JOB_LV_UP=Ally.getJobExp(job);
		Ally.levelupJob(JOB_LV_UP);
	}
	
	/** この画面を閉じるかどうか */
	protected boolean isFinish(){
		if(Situation == SituationArray.length-1)return true;
		else return false;
	}
	
	/** 技が命中したか、どれくらいのダメージを受けたかを調べる */
	protected void detailBattle(boolean ally){
		if(ally){
			AllySkill.onceUse();
			double rate=BattleCalculation.getHitRate(AllySkill, EnemySkill , Ally , Enemy , mapHandling.getMap());
			if(HitRate_Estimate!=null)Paramaters.updateErrorHit(HitRate_Estimate, rate);  //命中率の予測誤差の更新
			int damege=BattleCalculation.getDamege(AllySkill, EnemySkill , Ally , Enemy , mapHandling.getMap());
			if(Damege_Estimate!=null)Paramaters.updateErrorAttack(Damege_Estimate, damege);  //ダメージの予測誤差の更新
			if(rate>Paramaters.getRandom()){
				hit=true;
				Damege=damege;
				Enemy.downHP(Damege);
				//if(rate<Paramaters.Hit_Probability)Paramaters.updateHitProbability(rate);  //閾値の更新
			}
			else{
				hit=false;
				Damege=0;
				//if(rate>Paramaters.Hit_Probability)Paramaters.updateHitProbability(rate);   //閾値の更新
			}
		}
		else{
			EnemySkill.onceUse();
			double rate=BattleCalculation.getHitRate(EnemySkill, AllySkill, Enemy, Ally, mapHandling.getMap());
			if(rate>Paramaters.getRandom()){
				hit=true;
				Damege=BattleCalculation.getDamege(EnemySkill, AllySkill, Enemy, Ally, mapHandling.getMap());
				Ally.downHP(Damege);
				//if(rate<Paramaters.Hit_Probability)Paramaters.updateHitProbability(rate);  //閾値の更新
			}
			else{
				hit=false;
				Damege=0;
				//if(rate>Paramaters.Hit_Probability)Paramaters.updateHitProbability(rate);   //閾値の更新
			}
		}
	}
	
	/** 状態異常になったかどうか */
	protected void detailUnusual(boolean ally,boolean hit){
		if(!hit){
			for(int i=0;i<Unusuals_Hit.length;i++){
				Unusuals_Hit[i]=false;
			}
			return;
		}
		Skill Attack,Diffence;
		CharaData Attacker,Diffencer;
		if(ally){
			Attack=AllySkill;
			Diffence=EnemySkill;
			Attacker=Ally;
			Diffencer=Enemy;
		}
		else{
			Attack=EnemySkill;
			Diffence=AllySkill;
			Attacker=Enemy;
			Diffencer=Ally;
		}
		int[] unusuals=Attack.getUnusuals();
		for(int i=0;i<unusuals.length;i++){
			Unusuals_Hit[i]=false;
			if(unusuals[i]>0){
				double rate=BattleCalculation.getUnusual(Attacker, Diffencer);
				if(rate>Paramaters.getRandom()){
					Unusuals_Hit[i]=true;
					Diffencer.setUnusual(i,unusuals[i]);
					//if(rate<Paramaters.Hit_Probability)Paramaters.updateHitProbability(rate);  //閾値の更新
				}
				else{
					//if(rate>Paramaters.Hit_Probability)Paramaters.updateHitProbability(rate);   //閾値の更新
				}
			}
		}
	}
	
	/** 現在戦っている敵キャラを返す */
	public CharaData getNowEnemy(){
		return Enemy;
	}
	
	/** 現在戦っている味方キャラを返す */
	public CharaData getNowAlly(){
		return Ally;
	}

	/** 味方キャラの技をセットする */
	public void setAllySkill(Skill sk){
		AllySkill=sk;
	}

    @Override
    public void run() {
        long nowTime = System.currentTimeMillis();
        if(!flags[ANIMATION_START_FLAG] || nowTime-beforeActionTime < intervalTime || flags[SCENE_FINISH_FLAG]) return;
        if(isFinish()){
            flags[SCENE_FINISH_FLAG] = true;
        }
        else{
            nextSituation();
            happenEvent();
        }
        beforeActionTime = System.currentTimeMillis();
    }

	@Override
	public void moveCursor(int x, int y) {
		if(Situation==0) decideNowSelectedItem(x,y);
	}
}
