package sim.game.brain;

import java.awt.Point;
import java.util.ArrayList;

/** 移動時のルートに関するクラス */
public class Root {
	private ArrayList<Point> Loads;
	private final double Cost;
	
	public Root(ArrayList<Point> ls,double c){
		Loads=ls;
		Cost=c;
	}
	
	public double getCost(){
		return Cost;
	}
	
	public Point getReach(){
		return Loads.get(Loads.size()-1);
	}
	
	/** ルートを渡す */
	public ArrayList<Point> getLoads(){
		ArrayList<Point> list = new ArrayList<Point>();
		list.addAll(Loads);
		return list;
	}
}
