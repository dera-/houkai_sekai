package sim.game.data;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/** スキルに関するクラス */
public class Skill{
	private static ArrayList<Skill> SKILL_LIST = new ArrayList<Skill>();
	protected String Name;  //技の名前
	protected String TypeName;  //技の種類
	protected final int Level;
	protected double[] Revisions={0,0,0,0}; //補正(順に、攻防命避)
	protected int[] Unusuals ={0,0,0};  //状態異常レベル
	protected int[] Recovers ={0,0,0,0};  //回復レベル
	protected ArrayList<String> Strongs = new ArrayList<String>();    //相性のいいスキル
	protected ArrayList<Point> RangeList;  //攻撃範囲を格納している(戦術パートの始まりと終わりに初期化が必要)
	protected int Rest;  //残り弾数
	protected int Max;   //MAXの弾数
	protected int MinRange;  //最低射程距離
	protected int MaxRange;  //最高射程距離
	protected int Limit; //1ターンに使える回数
	protected final int NoLimit=-1;
	protected int NowUsed=0;  //1ターン中にこのスキルを使った回数
	protected Map NowMap=null;
	public final int Type; //技タイプ 
	
	/** 通常スキルを読み込むときに使用するコンストラクタ */
	public Skill(String s,String type,int lv,double[] rs,int[] ps,ArrayList<String> SL){
		Type=0;
		Name=s;
		TypeName=type;
		Level=lv;
		for(int i=0;i<Revisions.length;i++){
			Revisions[i]=rs[i];
		}
		Strongs.addAll(SL);
		Unusuals[0]=ps[0];
		Unusuals[1]=ps[1];
		Unusuals[2]=ps[2];
		Max=ps[3];
		Rest=Max;
		Limit=ps[4];
		MinRange=ps[5];
		MaxRange=ps[6];
		RangeList = new ArrayList<Point>();
	}
	
	/** 回復スキルを読み込む時に使用するコンストラクタ */
	public Skill(String s,String type,int lv,int[] ps){
		Type=1;
		Name=s;
		TypeName=type;
		Level=lv;
		Recovers[0]=ps[0];
		Recovers[1]=ps[1];
		Recovers[2]=ps[2];
		Recovers[3]=ps[3];
		Max=ps[4];
		Rest=Max;
		Limit=-1;
		MinRange=ps[5];
		MaxRange=ps[6];
		RangeList = new ArrayList<Point>();
	}
	
	public Skill(Skill S){
		Type=S.Type;
		Name=S.Name;
		TypeName=S.TypeName;
		Level=S.Level;
		for(int i=0;i<Revisions.length;i++){
			Revisions[i]=S.Revisions[i];
		}
		for(int i=0;i<Unusuals.length;i++){
			Unusuals[i]=S.Unusuals[i];
		}
		for(int i=0;i<Recovers.length;i++){
			Recovers[i]=S.Recovers[i];
		}
		Strongs.addAll(S.Strongs);
		Max=S.Max;
		Rest=Max;
		Limit=S.Limit;
		MinRange=S.MinRange;
		MaxRange=S.MaxRange;
		RangeList = new ArrayList<Point>();
	}
	
	/** 外部にSKILL_LISTを返すメソッド */
	public static ArrayList<Skill> getSKILL_LIST(){
		return SKILL_LIST;
	}
	
	/** 文字列からSKILLの検索を行う */
	public static Skill searchSkill(String str){
		for(Skill sk : SKILL_LIST){
			if(str.equals(sk.Name))return new Skill(sk);
		}
		return null;
	}
	
	/** 引数のSkillオブジェクトと同一のデータをもつSkillオブジェクトを作成して、返り値として返す */
	public static Skill getCloneSkill(Skill S){
		if(S==null)return null;
		return new Skill(S);
	}
	
	/** 引数のmapに対して、射程距離はどれくらいかを返す */
	public ArrayList<Point> getRealRange(Map map,Point now){
		ArrayList<Point> list=new ArrayList<Point>();
		NowMap=map;
		list = getRealRange(now,0);
		
		//この時listは射程範囲外の座標を含んでいる可能性があるので、その可能性を除く必要がある
		for(int i=0;i<list.size();){
			Point elem=list.get(i);
			int distance=Math.abs(elem.x-now.x)+Math.abs(elem.y-now.y);
			if(MinRange<=distance && distance<=MaxRange)i++;
			else list.remove(elem);
		}
		NowMap=null;
		return list;
	}
	
	/** 射程範囲の計算をする。再帰的メソッド。 */
	private ArrayList<Point> getRealRange(Point p,int n){
		ArrayList<Point> list = new ArrayList<Point>();
		if(n>MaxRange)return list;
		MapChip mc=NowMap.getChip(p.x, p.y);
		if(mc==null /*|| mc.isMovable()==false*/)return list;
		list.add(p);
		for(int i=0;i<4;i++){
			int x=0;
			int y=0;
			switch(i){
				case 0: x++;
						break;
				case 1: y++;
						break;
				case 2: x--;
						break;
				case 3: y--;
						break;
			}
			Point Next = new Point( p.x+x , p.y+y );
			list.addAll( getRealRange(Next,n+1) );
		}
		//同じ座標をリスト上から削除する処理
		for(int i=0;i<list.size();){
			Point TP=list.get(i);
			int j;
			for(j=0;j<i;j++){
				Point elem=list.get(j);
				if(TP.x==elem.x && TP.y==elem.y)break;
			}
			if(j==i) i++;
			else list.remove(TP);
		}
		return list;
	}
	
	/** このスキル(対人専用)が使えるかどうか */
	public boolean isUsable_CharaData(ArrayList<CharaData> tergets,Map map,Point now){
		ArrayList<CharaData> list=getTergetList_CharaData(tergets,map,now);
		if(list.size()==0)return false;
		else return true;
	}
	
	/** スキル(対人専用)の射程範囲内にいる技の対象キャラのリストを返す */
	public ArrayList<CharaData> getTergetList_CharaData(ArrayList<CharaData> tergets,Map map,Point now){
		ArrayList<CharaData> list = new ArrayList<CharaData>();
		if( !isRestSkill() )return list;
		ArrayList<Point> ptList = getTergetList(changeListToPoint(tergets),map,now);
		list = changeListToCharaData(tergets,ptList);
		return list;
	}
	
	/** このスキルが使えるかどうか */
	public boolean isUsable_Point(ArrayList<Point> tergets,Map map,Point now){
		List<Point> list=getTergetList_Point(tergets,map,now);
		if(list.size()==0)return false;
		else return true;
	}
	
	/** スキルの射程範囲内にいる技の対象のリストを返す */
	public ArrayList<Point> getTergetList_Point(ArrayList<Point> tergets,Map map,Point now){
		ArrayList<Point> list = new ArrayList<Point>();
		if( !isRestSkill() )return list;
		list = getTergetList(tergets,map,now);
		return list;
	}
	
	/** 弾数が残っているかどうかと1ターンに使える回数を超えていないかどうか */
	public boolean isRestSkill(){
		if(Rest<=0 || (Limit!=NoLimit && NowUsed >= Limit)){
			return false;
		}
		else return true;
	}
	
	/** 引数のキャラが回復する必要があるキャラかどうか */
	private boolean isNeedRecover(CharaData C){
		if(Recovers[0]!=0 && C.getHP()!=C.getNowHP())return true;
		if(C.checkUnusual(0)!=0 && Recovers[1]>=C.checkUnusual(0))return true;
		if(C.checkUnusual(1)!=0 && Recovers[2]>=C.checkUnusual(1))return true;
		if(C.checkUnusual(2)!=0 && Recovers[3]>=C.checkUnusual(2))return true;
		return false;
	}
	
	/** スキルの射程範囲内にいる技の対象のリストを返す */
	private ArrayList<Point> getTergetList(ArrayList<Point> tergets,Map map,Point now){
		ArrayList<Point> list = new ArrayList<Point>();
		ArrayList<Point> ptList=getRealRange(map,now);
		for(Point pt : ptList){
			for(Point C:tergets){
				if( pt.x==C.x && pt.y==C.y )list.add(C);
			}
		}
		return list;
	}
	
	/** キャラデータリストを座標リストに変換する */
	private ArrayList<Point> changeListToPoint(ArrayList<CharaData> tergets){
		ArrayList<Point> list = new ArrayList<Point>();
		for(CharaData C : tergets){
			Point pt=C.getPlace();
			list.add(pt);
		}
		return list;
	}
	
	/** 引数の座標リストをキャラデータリストに変換する */
	private ArrayList<CharaData> changeListToCharaData(ArrayList<CharaData> tergets,ArrayList<Point> pts){
		ArrayList<CharaData> list = new ArrayList<CharaData>();
		for(Point elem : pts){
			for(CharaData C : tergets){
				Point pt=C.getPlace();
				if(elem.x==pt.x && elem.y==pt.y){
					if(!(Type==1 && !isNeedRecover(C)))list.add(C);
					break;
				}
			}
		}
		return list;
	}
	
	/** RangeListに値を与えるメソッド。placesは移動後到着地点のリスト。 */
	public void setRangeList(ArrayList<Point> places,Map map){
		ArrayList<Point> pts = new ArrayList<Point>();
		for(Point p : places){
			ArrayList<Point> list = getRealRange(map,p);
			for(int i=0;i<list.size();i++){
				Point elem=list.get(i);
				int j;
				for(j=0;j<pts.size();j++){
					Point e=pts.get(j);
					if(elem.x==e.x && elem.y==e.y)break;
				}
				if(j==pts.size()){
					pts.add(elem);
				}
			}
		}
		RangeList.clear();
		RangeList.addAll(pts);
	}
	
	/** この技の攻撃可能範囲に入っているかどうか */
	public boolean isInRange(Point pt){
		for(Point R : RangeList){
			if(R.x==pt.x && R.y==pt.y)return true;
		}
		return false;
	}
	
	/** この技で反撃可能かどうか返す */
	public boolean isCounterAttack(Point usr,Point ene,Map map){
		if( Type!=0 || !isRestSkill() )return false;
		ArrayList<Point> ptList=getRealRange(map,usr);
		for(Point pt: ptList){
			if(pt.x==ene.x && pt.y==ene.y){
				return true;
			}
		}
		return false;
	}
	
	/** スキルの名前を返す */
	public String getName(){
		return Name;
	}
	
	/** スキルの種類を返す */
	public String getTypeName(){
		return TypeName;
	}
	
	/** スキルのレベルを返す */
	public int getLevel(){
		return Level;
	}
	
	/** 技補正の値を返す */
	public double getRevisions(int i){
		return Revisions[i];
	}
	
	/** 相性値を返す */
	public int getAffinity(String str){
		for(String s : Strongs){
			if(str.equals(s))return 1;
		}
		return 0;
	}
	
	/** 一回攻撃するたびに呼び出されるメソッド */
	public void onceUse(){
		Rest--;
		if(Limit!=NoLimit){
			NowUsed++;
		}
	}
	
	/** ターンが始まる毎に呼び出すメソッド */
	public void startTurn(){
		if(Limit!=NoLimit){
			NowUsed=0;
		}
	}
	
	/** 技の使用範囲を文字列として返すメソッド */
	public String getRange_String(){
		return "範囲:"+MinRange+"～"+MaxRange;
	}
	
	/** 技の使用可能回数を文字列として返す */
	public String getRest_String(){
		return "使用可能数:"+Rest;
	}
	
	/** 技補正の文字列を返すメソッド */
	public String getRevisions_String(){
		String[] Revisions_String ={"攻撃","防御","命中","回避"};
		String effect="";
		for(int i=0;i<Revisions.length;i++){
			if(Revisions[i]==0)continue;
			if(!effect.equals(""))effect+=",";
			String UpOrDown;
			if(Revisions[i]>0)UpOrDown="UP";
			else UpOrDown="DOWN";
			effect+=(Revisions_String[i]+(int)(100*Revisions[i])+"%"+UpOrDown);
		}
		return effect;
	}
	
	
	/** 回復効果の文字列配列を返すメソッド */
	public String getRecovers_String(){
		String[] Recovers_String ={"HP回復","毒回復","失神回復","怪我回復"};
		String effect="";
		for(int i=0;i<Recovers.length;i++){
			if(Recovers[i]>0) {
				if(!effect.equals(""))effect+=",";
				effect+=(Recovers_String[i]+"LV"+Recovers[i]);
			}
		}
		//if(effect.equals(""))effect="回復効果なし";
		return effect;
	}
	
	/** 状態異常の文字列を返すメソッド */
	public String getUnusuals_String(){
		String[] Unusuals_String ={"毒","失神","怪我"};
		String effect="";
		for(int i=0;i<Unusuals.length;i++){
			if(Unusuals[i]>0){
				if(!effect.equals(""))effect+=",";
				effect+=(Unusuals_String[i]+"LV"+Unusuals[i]);
			}
		}
		//if(effect.equals(""))effect="状態異常効果なし";
		return effect;
	}
	
	/** 1ターンに使用できる回数を文字列として返すメソッド */
	public String getLimit_String(){
		String limit;
		if(Limit==NoLimit)limit="∞";
		else limit=Integer.toString(Limit);
		return "1ターンに使用できる回数:"+limit;
	}
	
	
	/** 回復効果を返すメソッド */
	public int[] getRecovers(){
		return Recovers;
	}
	
	/** HPの回復レベルを返すメソッド */
	public int getRecover_HP(){
		return Recovers[0];
	}
	
	/** 状態異常を返すメソッド */
	public int[] getUnusuals(){
		return Unusuals;
	}
	
	/** 使用回数を初期状態に戻すメソッド */
	public void recover(){
		Rest=Max;
		NowUsed=0;
	}
	
}
