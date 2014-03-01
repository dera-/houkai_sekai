package sim.game.brain;

import java.awt.Point;
import java.util.ArrayList;

import sim.game.Paramaters;
import sim.game.brain.Root;
import sim.game.data.CharaData;
import sim.game.data.Map;
import sim.game.data.MapChip;

/** 移動に関するクラス */
public class MovingBrain {
	private ArrayList<CharaData> Allys;
	private ArrayList<CharaData> Enemies;
	private CharaData NowChara;
	private boolean ally;
	private Map map;
	private ArrayList<Root> RootList = new ArrayList<Root>();

	public MovingBrain(ArrayList<CharaData> AL,ArrayList<CharaData> EL,Map m,CharaData CD,boolean a){
		setFields(AL,EL,m,CD,a,CD.getPlace());
	}
	
	public MovingBrain(ArrayList<CharaData> AL,ArrayList<CharaData> EL,Map m,CharaData CD,boolean a,Point pt){
		setFields(AL,EL,m,CD,a,pt);
	}
	
	/** 各フィールドに値を与えるメソッド */
	private void setFields(ArrayList<CharaData> AL,ArrayList<CharaData> EL,Map m,CharaData CD,boolean a,Point pt){
		Allys=AL;
		Enemies=EL;
		map=m;
		NowChara=CD;
		ally=a;
		calculateAllRoots(new Point(pt.x,pt.y),0,-1);
	}
	
	/** キャラの全ての移動ルートを計算する */
	private void calculateAllRoots(Point pt,double cost,int before){
		MapChip mc=map.getChip(pt.x, pt.y);
		if(isInMap(pt)==false || mc==null || isThereEnemy(pt) || mc.isMovable()==false)return;
		if( NowChara.isSuccessChip(mc.getName())==false )cost+=mc.getRevisionsElement(6);
		if(cost>NowChara.getMove())return;
		ArrayList<Point> list;
		if(before<0) list = new ArrayList<Point>();
		else list = RootList.get(before).getLoads();
		list.add(pt);
		Integer num = addRoot(list,cost);
		if(num == null) return;   //このルートの追加
		int LastNum = num;
		//以下でルートを広げる処理
		for(int i=0;i<4;i++){
			int x=0;
			int y=0;
			switch(i){
			case 0: x++;
					break;
			case 1: x--;
					break;
			case 2: y++;
					break;
			case 3: y--;
					break;
			}
			Point P=new Point(pt.x+x,pt.y+y);
			calculateAllRoots(P,cost+1,LastNum);
		}
	}
	
	/** 引数のオブジェクトをRootオブジェクトとしてRootListに保存する */
	private Integer addRoot(ArrayList<Point>places, double cost){
	    Root root = getRoot(places.get(places.size()-1));
	    if(root == null){
	        RootList.add(new Root(places,cost));
	        return RootList.size()-1;
	    }
	    else if(cost < root.getCost()){
	        int index = RootList.indexOf(root);
	        RootList.remove(index);
	        RootList.add(index,new Root(places,cost));
	        return index;
	    }
	    return null;
	}
	
	/** 引数の座標を目的地とするRootを返すメソッド */
	public Root getRoot(Point goal){
	    for(Root R : RootList){
	        Point p=R.getReach();
	        if(p.x == goal.x && p.y == goal.y)return R;
	    }
	    return null;
	}
	
	/** 引数の座標がMAP内かどうか判断する */
	private boolean isInMap(Point pt){
		MapChip mc=map.getChip(pt.x, pt.y);
		if(mc==null)return false;
		else return true;
	}
	
	/** 敵が引数の座標に存在するかどうか判定 */
	private boolean isThereEnemy(Point pt){
		ArrayList<CharaData> CharaList;
		if(ally) CharaList=Enemies;
		else CharaList=Allys;
		for(CharaData C : CharaList){
			if(C.isPlace(pt))return true;
		}
		return false;
	}
	
	/** 求められたルートのリストを返すメソッド */
	public ArrayList<Root> getRoots(){
		return RootList;
	}
	
	/** 求められたルートの到着点の集合 */
	public ArrayList<Point> getReachs(){
		ArrayList<Point> list=new ArrayList<Point>();
		ArrayList<Point> friends=getFriendPoints();
		for(Root R : RootList){
			Point p=R.getReach();
			if(isInclude(friends,p))continue;  //友軍がその座標に存在するなら、その座標には移動しない
			int j;
			for(j=0;j<list.size();j++){
				Point elem=list.get(j);
				if(p.x==elem.x && p.y==elem.y)break;
			}
			if(j==list.size())list.add(p);
		}
		return list;
	}
	
	/** 移動しようとしているキャラの友軍の座標リストを返す */
	private ArrayList<Point> getFriendPoints(){
		ArrayList<Point> ptList = new ArrayList<Point>();
		ArrayList<CharaData> charaList;
		if(ally) charaList=Allys;
		else charaList=Enemies;
		for(CharaData C : charaList){
			if(NowChara==C)continue;
			ptList.add(C.getPlace());
		}
		return ptList;
	}
	
	/** 引数のリストの中に引数の座標が含まれているかどうか返すメソッド */
	private boolean isInclude(ArrayList<Point> list,Point p){
		for(Point pt : list){
			if( p.x==pt.x && p.y==pt.y )return true;
		}
		return false;
	}
	
	/** 到着点の集合の中に第1引数の座標と第2引数の距離だけ近くなるものがあるかどうかを判定するメソッド */
	public boolean isNear(Point pt,int threshold){
		ArrayList<Point> PointList=getReachs();
		for(Point elem : PointList){
			int D=Math.abs(elem.x-pt.x)+Math.abs(elem.y-pt.y);
			if(D<=threshold)return true;
		}
		return false;
	}
	
}
