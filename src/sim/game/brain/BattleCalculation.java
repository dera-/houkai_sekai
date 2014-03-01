package sim.game.brain;

import java.awt.Point;
import sim.game.data.*;

/** 戦闘結果の計算(ダメージ量や命中率など)に関するクラス */
public class BattleCalculation {
	private static final double AffinityRate=0.2;
	private static final double RuckAffect1=0.15;
	
	/** ダメージの計算を行う */
	public static int getDamege(Skill AS,Skill DS,CharaData AC,CharaData DC,Map map){
		return getDamege(AS,DS,AC,DC,map,AC.getPlace(),DC.getPlace());
	}
	
	/** 攻撃するキャラの位置を指定してダメージの計算を行う */
	public static int getDamege(Skill AS,Skill DS,CharaData AC,CharaData DC,Map map,Point AttackPlace,Point DiffencePlace){
		if(AS==null)return 0;
		//攻撃力の計算を行う
		int RealAttack=0;
		int Attack = AC.getAttack(); //基本攻撃力
		int A_Map = (int)Math.round( Attack*EffectMap(map,AttackPlace,1) );
		int A_Skill = (int)Math.round( Attack*AS.getRevisions(0) );
		int Affinity=0;
		if(DS!=null)Affinity = getAffinity(Attack,AS,DS.getName());
		RealAttack=Attack+A_Map+A_Skill+Affinity;
		
		//防御力の計算を行う
		int RealDeffence=0;
		int Deffence = DC.getDiffence(); //基本防御力
		int D_Map = (int)Math.round( Deffence*EffectMap(map,DiffencePlace,2) );
		int D_Skill = 0;
		if(DS!=null)D_Skill = (int)Math.round( Deffence*DS.getRevisions(1) );
		RealDeffence=Deffence+D_Map+D_Skill;
		
		int Damege=(int)Math.round( Math.pow(RealAttack, 2)/(RealAttack+RealDeffence) );
		if(Damege<0)Damege=0;
		return Damege;
	}
	/** 命中率の計算を行う */
	public static double getHitRate(Skill AS,Skill DS,CharaData AC,CharaData DC,Map map){
		return getHitRate(AS,DS,AC,DC,map,AC.getPlace(),DC.getPlace());
	}
	
	/** 敵キャラの位置を指定して命中率の計算を行う */
	public static double getHitRate(Skill AS,Skill DS,CharaData AC,CharaData DC,Map map,Point AttackPlace,Point DiffencePlace){
		if(AS==null)return 0;
		/* 命中力の計算 */
		int RealHit=0;
		int Hit = AC.getHit();
		Hit+=(int)Math.round(RuckAffect1*AC.getRuck());;
		int H_Map = (int)Math.round( Hit*EffectMap(map,AttackPlace,3) );
		int H_Skill = (int)Math.round( Hit*AS.getRevisions(2) );
		int Affinity=0;
		if(DS!=null) Affinity = getAffinity(Hit,AS,DS.getName());
		RealHit=Hit+H_Map+H_Skill+Affinity;
		if(RealHit<=0)RealHit=1;
		
		/* 回避力の計算 */
		int RealAvoid=0;
		int Avoid = DC.getAvoid();
		Avoid+=(int)Math.round(RuckAffect1*DC.getRuck());
		int A_Map = (int) Math.round( Avoid*EffectMap(map,DiffencePlace,4) );
		int A_Skill = 0;
		if(DS!=null)A_Skill = (int)Math.round( Avoid*DS.getRevisions(3) );
		RealAvoid= Avoid+A_Map+A_Skill;
		if(RealAvoid<=0)RealAvoid=1;
		double HitRate = 1.5 * (1 + Math.log(1.0*(RealHit+RealAvoid)/(2*RealAvoid))) * RealHit/(RealHit+RealAvoid) + (Math.sqrt(RealHit)-Math.sqrt(RealAvoid))/100;
//		System.out.println("HitRate:" + HitRate);
//		System.out.println("hit:"+RealHit);
//		System.out.println("avoid:"+RealAvoid);
//		System.out.println("log:" + (1 + Math.log(1.0*(RealHit+RealAvoid)/(2*RealAvoid))));
//		double HitRate = Math.log1p(1.0*(RealHit+RealAvoid)/(2*RealAvoid))*RealHit/(RealHit+RealAvoid)/Math.log(2);
		if(HitRate<0)HitRate=0;
		else if(HitRate>1)HitRate=1;
		return HitRate;
	}
	/** マップ効果を返す */
	private static double EffectMap(Map map,Point pt,int n){
		MapChip mc=map.getChip(pt.x, pt.y);
		return mc.getRevisionsElement(n);
	}
	
	/** 相性による補正を返す */
	private static int getAffinity(int A,Skill AS,String DN){
		return (int)Math.round(AffinityRate*AS.getAffinity(DN)*A);
	}
	
	/** 状態異常にかかる確率を返す */
	public static double getUnusual(CharaData AC,CharaData DC){
		int Intel=AC.getIntel();
		if(Intel<=0)Intel=1;
		int Ruck=(int)Math.round(1.0*(DC.getRuck()+DC.getIntel())/2);    //知力と運を足して2で割った値
		if(Ruck<=0)Ruck=1;
		double HitRate = 0.5*Math.log1p(1.0*(Intel+Ruck)/(2*Ruck))*Intel/(Intel+Ruck);
		return HitRate;
	}
	
	/** 回復量を計算して返すメソッド */
	public static int getRecover(int lv,CharaData AC,CharaData DC){
		double Parsent=lv;
		int Intel=AC.getIntel();
		int Recover;
		if(Parsent*Intel+DC.getNowHP()>DC.getHP())Recover=DC.getHP()-DC.getNowHP();
		else Recover=(int)Math.round(Parsent*Intel);
		return Recover;
	}
}
