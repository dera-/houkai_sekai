package sim.game.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import sim.game.Paramaters;

/** 職業に関するクラス */
public class Job {
	private static ArrayList<Job> JobList = new ArrayList<Job>();
	private final int MaxLevel=6; //職業レベルの上限
	private String Name;  //職業名
	public final int Number;
	private double Revisions[]=new double[8];  //キャラクターの各パラメーターの補正
	private double Rises[] = new double[7]; //レベルアップ時の上昇値
	private Skill[] SkillList = new Skill[MaxLevel]; //覚えることができるスキル
	private int Level=0;  //職業レベル
	private double Experience=0;  //職業経験値
	private int[] NecessaryExperiments={20,30,40,50,60,70}; //30,50,70,90,110,130//レベルアップに必要な経験値
	
	public Job(String name,int n,double[] revs,double[] rises,String[] skills){
		Name=name;
		Number=n;
		for(int i=0;i<revs.length;i++){
			Revisions[i]=revs[i];
		}
		for(int i=0;i<rises.length;i++){
			Rises[i]=rises[i];
		}
		for(int i=0;i<skills.length;i++){
			SkillList[i]=Skill.searchSkill(skills[i]);
		}
	}
	
	public Job(Job j){
		Name=j.Name;
		Number=j.Number;
		for(int i=0;i<Revisions.length;i++){
			Revisions[i]=j.Revisions[i];
		}
		for(int i=0;i<Rises.length;i++){
			Rises[i]=j.Rises[i];
		}
		for(int i=0;i<SkillList.length;i++){
			SkillList[i]=Skill.getCloneSkill(j.SkillList[i]);
		}
		Level=j.Level;
		Experience=j.Experience;
	}
	
	/** 外部にJobListを渡すメソッド */
	public static ArrayList<Job> getJobList(){
		return JobList;
	}
	
	/** 引数の名前のJobオブジェクトを返り値として返す */
	public static Job getJob(String n){
		for(Job j : JobList){
			if(n.equals(j.Name))return new Job(j);
		}
		return null;
	}
	
	/** 引数の番号のJobオブジェクトを返り値として返す */
	public static Job getJob(int num){
		for(Job j : JobList){
			if(num==j.Number)return new Job(j);
		}
		return null;
	}
	
	/** 職業の名前を返すメソッド */
	public String getJobName(){
		return Name;
	}
		
	/**職業レベルを返り値として返すメソッド*/
	public int getLevel(){
		return Level;
	}
	
	/** 引数のレベルで取得できるスキルを全て返すメソッド */
	public ArrayList<Skill> getSkillList(){
		ArrayList<Skill> list = new ArrayList<Skill>();
		for(int i=0;i<Level;i++){
			Skill S=SkillList[i];
			if(S==null)continue;
			int index;
			for(index=0;index<list.size();index++){
				Skill elem = list.get(index);
				if(S.getTypeName().equals(elem.getTypeName())){
					if(S.getLevel()>elem.getLevel()){
						list.remove(index);
						list.add(index,S);
					}
					break;
				}
			}
			if(index==list.size())list.add(S);
		}
		return list;
	}
	
	/** 各パラメーターの補正の値を返すメソッド */
	public double getRevision(int i){
		return Revisions[i];
	}
	
	/** 各パラメーターの上昇値を返すメソッド */
	public int getRise(int i){
		double rate=0.8;
		double param=0.4;
		double Rate_Truth=rate+param*Paramaters.getRandom();
		return (int)Math.round(Rate_Truth*Rises[i]);
	}
	
	/** 引数の職業経験値を得るメソッド。 */
	public int getJobExp(double e,int l){
		int NowLV=Level+l;
		if(NowLV>=MaxLevel)return 0;
		if(Experience+e>=NecessaryExperiments[NowLV]){
			double exp=Experience;
			Experience=0;
			return getJobExp(e-(NecessaryExperiments[NowLV]-exp),l+1)+1;
		}
		else{
			Experience+=e;
			return 0;
		}
	}
	
	/** 現在の職業経験値を返すメソッド */
	public double getExperience(){
		if(Level>=MaxLevel)return 0;
		return Experience;
	}
	
	/** 職業経験値を引数の値にするメソッド */
	public void setExperience(double exp){
		Experience=exp;
	}
	
	/** 次のLVに上がるまで必要な経験値を返すメソッド */
	public int getExpToNextLv(){
		if(Level>=MaxLevel)return NecessaryExperiments[NecessaryExperiments.length-1];
		return NecessaryExperiments[Level];
	}
	
	/** 引数の数だけ職業レベルをアップさせるメソッド */
	public void levelup(int i){
		Level+=i;
		if(Level>MaxLevel)Level=MaxLevel;
	}
	
	/** 職業レベルを引数の値にする */
	public void setLevel(int i){
		Level=i;
		if(Level>MaxLevel)Level=MaxLevel;
	}
	
	/**　引数の数だけ職業レベルをアップすると同時に新たな技を覚える。*/
	public void levelup(int l,ArrayList<Skill> list){
		for(int index=0;index<l;index++){
			Level++;
			if(Level>MaxLevel){
				Level=MaxLevel;
				break;
			}
			Skill S=SkillList[Level-1];
			if(S==null)continue;
			int I;
			for(I=0;I<list.size();I++){
				Skill elem=list.get(I);
				if( S.getTypeName().equals(elem.getTypeName()) ){
					if(S.getLevel()>elem.getLevel()){
						list.remove(I);
						list.add(I,S);
					}
					break;
				}
			}
			if(I==list.size())list.add(S);
		}
	}
	
}
