package sim.game.battle;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;

import sim.game.Paramaters;
import sim.game.brain.BattleCalculation;
import sim.game.data.CharaData;
import sim.game.data.Skill;
import sim.tools.MapHandling;

public class RecoverScene extends BattleScene implements Scene {
	protected int RecoverValue=0;
	private int[] ExpList={25,25,25};
	private int JobExp_Normal=2;
	
	public RecoverScene(CharaData A,Skill AS,CharaData E,MapHandling handle,boolean first){
		super(A,AS,E,handle,first);
	}
	
	/** 配列の初期化 */
	public void formatArrays(){
		Names[0]="回復開始";
		Names[1]="スキル選択";
		Permits[0]=true;
		Permits[1]=First;
	}
	
	/** 戦闘結果の予測を描画 */
	protected void drawBattleEstimate(Graphics g,Container con){
		Draw(g,con);
		String items[]={"キャラ名","回復技","残りHP","HP回復量","効果"};
		Tools.drawTable(g, con, (int)Math.round(0.4*Paramaters.Width), (int)Math.round(0.25*Paramaters.Height), 
				(int)Math.round(0.2*Paramaters.Width), (int)Math.round(0.4*Paramaters.Height), items);
		
		/* 自軍に関する表の作成 */
		String AS="-";
		String ES="-";
		String Recover_A="-";
		String Recover_E="-";
		String Recover_A_HP="-";
		String Recover_E_HP="-";
		
		if(First){
			AS=AllySkill.getName();
			Recover_A=AllySkill.getRecovers_String();
			Recover_A_HP = Integer.toString(BattleCalculation.getRecover(AllySkill.getRecover_HP(),Ally,Enemy));
		}
		else{
			ES=EnemySkill.getName();
			Recover_E=EnemySkill.getRecovers_String();
			Recover_E_HP=Integer.toString(BattleCalculation.getRecover(EnemySkill.getRecover_HP(),Enemy,Ally));
		}
		String NowHP_A=Integer.toString(Ally.getNowHP());
		String AllyItems[]={Ally.getCharaName(),AS,NowHP_A,Recover_A_HP,Recover_A};
		Tools.drawTable(g,con,(int)Math.round(0.1*Paramaters.Width),(int)Math.round(0.25*Paramaters.Height),
				(int)Math.round(0.3*Paramaters.Width), (int)Math.round(0.4*Paramaters.Height), AllyItems);
		Ally.drawFace(g, con, (int)Math.round(0.1*Paramaters.Width), (int)Math.round(0.08*Paramaters.Height));
		
		/* 敵軍に関する表の作成 */
		String NowHP_E=Integer.toString(Enemy.getNowHP());
		String EnemyItems[]={Enemy.getCharaName(),ES,NowHP_E,Recover_E_HP,Recover_E};
		Tools.drawTable(g, con,(int)Math.round(0.6*Paramaters.Width),(int)Math.round(0.25*Paramaters.Height),
				(int)Math.round(0.3*Paramaters.Width), (int)Math.round(0.4*Paramaters.Height), EnemyItems);
		Enemy.drawFace(g, con, (int)Math.round(0.6*Paramaters.Width), (int)Math.round(0.08*Paramaters.Height));
	}
	
	/** 攻撃予告の描画 */
	protected void drawAttack(Graphics g){
		String messe="";
		Color col=Color.black;
		if(First){
			messe="Ally's Recover";
			col=Color.blue;
		}
		else{
			messe="Enemy's Recover";
			col=Color.red;
		}
		g.setColor(col);
		g.setFont(new Font("明朝体",Font.BOLD,30));
		g.drawString(messe,(int)Math.round(0.4*Paramaters.Width),(int)Math.round(0.5*Paramaters.Height));
	}
	
	/** 攻撃結果の描画 */
	protected void drawAttackResult(Graphics g){
		g.setFont(new Font("明朝体",Font.BOLD,30));
		Color col;
		int[] recovers;
		if(First){
			recovers=AllySkill.getRecovers();
			col=Color.blue;
		}
		else{
			recovers=EnemySkill.getRecovers();
			col=Color.red;
		}
		g.setColor(col);
		String[] strs={"HP回復:","解毒","意識回復","治療"};
		double dy=0.4;
		double val=0.05;
		for(int i=0;i<strs.length;i++){
			if(recovers[i]>0){
				String S;
				if(i==0)S=strs[0]+RecoverValue;
				else S=strs[i];
				g.drawString(S, (int)Math.round(0.4*Paramaters.Width), (int)Math.round(dy*Paramaters.Height));
				dy+=val;
			}
			
		}
	}
	
	/** メイドさんのセリフ番号の取得 */
	public int getComment(){
		if(Situation>=SituationArray.length)return 1000;
		switch(SituationArray[Situation]){
		case 0: if(First)return 64;
				else return 81;
		case 5: return 66;
		default: return 1000;
		}
	}
	
	/** 何かしらの処理を行う */
	protected void happenEvent(){
		int now=SituationArray[Situation];
		switch(now){
			case 2: detailRecover(AllySkill,Ally,Enemy);
					getExp();
					getJobExp();
					SituationArray[Situation+1]=5;
					break;
			case 4: detailRecover(EnemySkill,Enemy,Ally);
					Situation=SituationArray.length-1;
					break;
			case 5: Situation=SituationArray.length-1;
					break;
			default: break;
		}
	}
	
	/** 経験値を得るメソッド */
	protected void getExp(){
		Exp=ExpList[AllySkill.getLevel()-1];
		LV_UP=Ally.getExp(Exp);
	}
	
	/** 職業経験値を得るメソッド */
	protected void getJobExp(){
		JOB_LV_UP=Ally.getJobExp(JobExp_Normal);
		Ally.levelupJob(JOB_LV_UP);
	}
	
	/** 戦闘終了後のメッセージの取得 */
	protected String getMessage(){
		return "回復完了";
	}
	/** 実際の回復の処理を行う */
	protected void detailRecover(Skill S,CharaData Heeler,CharaData P){
		S.onceUse();
		RecoverValue=0;
		int[] recovers=S.getRecovers();
		for(int i=0;i<recovers.length;i++){
			if(recovers[i]>0){
				if(i==0){
					RecoverValue=BattleCalculation.getRecover(recovers[i], Heeler, P);
					int DownHp=-1*RecoverValue;
					P.downHP(DownHp);
				}
				else P.recoverUnusual(i-1,recovers[i]);
			}
		}	
	}
	
}
