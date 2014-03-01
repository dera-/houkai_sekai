package sim.game.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import sim.game.Paramaters;

/** 子の顔を決めるためのクラス */
public class Gene {
	private static ArrayList<Gene> GeneList = new ArrayList<Gene>();
	private String Parent; //親の顔画像名
	private String Child;  //子の顔画像名
	private int Score;     //類似得点
	
	public Gene(String parent,String child,int score){
		Parent=parent;
		Child=child;
		Score=score;
	}
	
	/** GeneListを外部に渡すメソッド */
	public static ArrayList<Gene> getGeneList(){
		return GeneList;
	}
	
	/** 親の顔から子供の顔を決定して、決定した顔を返り値として返すメソッド */
	public static String getChildFace(String p1,String p2){
		ArrayList<String> NameList = new ArrayList<String>();
		ArrayList<Integer> ScoreList = new ArrayList<Integer>();
		for(Gene gene : GeneList){
			String parent = gene.Parent;
			int score=0;
			if(p1.equals(parent))score+=gene.Score;
			if(p2.equals(parent))score+=gene.Score;
			if(score>0){
				NameList.add(gene.Child);
				ScoreList.add(score);
			}
		}
		return decideChild(NameList,ScoreList);
	}
	
	/** 実際に,子供の顔をするメソッド。顔を決定する際、ルーレット選択を用いることにする。 */
	private static String decideChild(ArrayList<String> names,ArrayList<Integer> scores){
		int Total=0;
		for(int s :scores)Total+=s;
		double random = Paramaters.getRandom();
		int now=0;
		for(int i=0;i<names.size();i++){
			now+=scores.get(i);
			if(random<1.0*now/Total)return names.get(i);
		}
		return null;
	}

}
