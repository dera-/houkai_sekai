package sim.game.brain;

import java.awt.Point;
import java.util.ArrayList;

import sim.game.Paramaters;
import sim.game.data.CharaData;
import sim.game.data.Map;
import sim.game.data.Skill;

/** 反撃に関するクラス */
public class CounterBrain {
	protected CharaData Counter;
	protected CharaData Attacker;
	protected Skill AttackSkill=null;
	protected Map map;
	protected Point AttackPoint;
	
	public CounterBrain(CharaData ally,CharaData enemy,Skill AS,Map m,boolean a){
		setFields(ally,enemy,AS,m,a,null);
	}
	
	public CounterBrain(CharaData ally,CharaData enemy,Skill AS,Map m,boolean a,Point pt){
		setFields(ally,enemy,AS,m,a,pt);
	}
	
	/** 各フィールドに値を与えるメソッド */
	private void setFields(CharaData ally,CharaData enemy,Skill AS,Map m,boolean a,Point pt){
		if(a){
			Counter=ally;
			Attacker=enemy;
			AttackPoint = enemy.getPlace();
		}
		else{
			Counter=enemy;
			Attacker=ally;
			AttackPoint = ally.getPlace();
		}
		AttackSkill=AS;
		map=m;
		if(pt!=null) AttackPoint=pt;
	}
	
	/** 反撃技を決定する */
	public Skill decideEnemySkill(){
		if(Counter.checkUnusual(1)>0)return null; //気絶しているのなら、反撃不能
		Skill CounterSkill=null;
		ArrayList<Skill> skList=Counter.getCounterSkillList(AttackPoint,map);
		int Type=Counter.getCounterType();
		double T=0;
		double win=0; //勝てる可能性が一定の確率を超えた時0以上になる
		for(int i=0;i<skList.size();i++){
			Skill S=skList.get(i);
			int d0=BattleCalculation.getDamege(S, AttackSkill, Counter, Attacker, map);
			double h0=BattleCalculation.getHitRate(S, AttackSkill, Counter, Attacker, map);
			if(win>0){
				if(h0>win){
					win=h0;
					CounterSkill=S;
				}
			}
			else if(d0>Attacker.getNowHP() && h0>Paramaters.Hit_Probability){
				win=h0;
				CounterSkill=S;
			}
			else{
				switch(Type){
					case 0: if( i==0 || T<d0*h0){
								T=d0*h0;
								CounterSkill=S;
							}
							break;
					case 1: int d1 = BattleCalculation.getDamege(AttackSkill, S , Attacker , Counter , map);
							double h1 = BattleCalculation.getHitRate(AttackSkill, S , Attacker , Counter , map);
							if(i==0 || T>d1*h1){
								T=d1*h1;
								CounterSkill=S;
							}
							break;
					default: break;
				}
			}
		}
		return CounterSkill;
	}
	
	
}
