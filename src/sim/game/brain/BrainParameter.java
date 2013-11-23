package sim.game.brain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/** 脳内パラメータークラス */
public class BrainParameter {
	private static ArrayList<BrainParameter> ParameterList = new ArrayList<BrainParameter>();  //脳内パラメーターリスト
	public final int Number;  //このオブジェクトの番号
	public final int Defeat;  //撃破した時の評価値
	public final int Dead;    //撃破された時の評価値
	public final boolean Injured;  //被ダメージ計算をするかどうか
	public final int NearHeeler;  //近くに回復できるキャラがいる場合の評価値
	public final int NearAI;    //近くに味方がいる場合の評価値
	public final int NearPlayer;  //プレイヤーキャラに近づいた場合の評価値
	public final int Mapchip_Satisfaction;   //得意のマップチップにいる時の評価値
	public final int BravePoint;  //戦闘を仕掛けた時に得られる得点
	public final int Special_Attack_Faint;
	public final int NearStartDistance;
	
	public BrainParameter(int[] nums){
		Number = nums[0];
		Defeat = nums[1];
		Dead = nums[2];
		Injured = (nums[3]==1);
		NearHeeler = nums[4];
		NearAI = nums[5];
		NearPlayer = nums[6];
		Mapchip_Satisfaction = nums[7];
		BravePoint = nums[8];
		Special_Attack_Faint = Defeat-1;
		NearStartDistance = nums[9];
	}
	
	/** ParameterListを外部に渡すメソッド */
	public static ArrayList<BrainParameter> getParameterList(){
		return ParameterList;
	}
	
	/** 脳内パラメーターの検索 */
	public static BrainParameter getParameter(int N){
		for(BrainParameter brain : ParameterList){
			if(N==brain.Number)return brain;
		}
		return null;
	}
	
	
}
