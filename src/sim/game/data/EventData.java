package sim.game.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import sim.game.Paramaters;
import sim.game.battle.BattleField;

public class EventData {
	private static ArrayList<EventData> EventList=new ArrayList<EventData>();
	private static ArrayList<Integer> FinishedEvents = new ArrayList<Integer>();
	private static EventData NowEvent;
	public static ArrayList<CharaData> Friends = new ArrayList<CharaData>();  //新たに仲間に加わるキャラクターのリスト
	private static HashMap<Integer,Integer> SentenceNumbers = new HashMap<Integer,Integer>(); //この番号のイベントをクリアしたら、エンディング。
	
	public final int EventNumber;
	private String EventName;         //イベントのタイトル
	private String eventDetails;      //イベントの説明文
	private Integer ConditionEvent;   //このイベントを発生させるためにこなす必要があるイベント
	private Integer DeleteEvent;  //このイベントを抹消するのに必要なイベント
	private Integer commonEventNum;  //このイベントが終了した時にFinishedEventsに追加される整数値
	private final boolean Main;  //メインイベントかどうか
	private int RundamNumber;  //このイベントをクリアした時に仲間になる人数
	private ArrayList<String> Reinforcements = new ArrayList<String>();  //このイベントをクリアすると絶対に仲間になるキャラクターのリスト
	private int MapNumber;  //このイベントにおける戦場
	private String bgmFileName;   //このイベントにおけるBGMのファイル名
	private Issue Victory;     //勝利条件
	private Issue Defeat;     //敗北条件
	private Point[] Places;   //味方が配置可能な場所
	private ArrayList<CharaData_Actually> Allys = new ArrayList<CharaData_Actually>();
	private ArrayList<CharaData_Actually> Enemies = new ArrayList<CharaData_Actually>();
	public EventData(ArrayList<String> list){
		String identity[]=list.get(0).split(",");
		EventNumber=Integer.parseInt( identity[0] );
		EventName=identity[1];
		eventDetails = identity[2];
		int sentenceNum = Integer.parseInt(identity[3]);
		if(sentenceNum > 0){
			SentenceNumbers.put(EventNumber, sentenceNum);
		}
		if(list.get(1).equals(""))ConditionEvent = null;
		else ConditionEvent=Integer.parseInt( list.get(1) );
		if(list.get(2).equals(""))DeleteEvent = null;
		else DeleteEvent = Integer.parseInt( list.get(2) );
	    if(list.get(3).equals(""))commonEventNum = null;
	    else commonEventNum = Integer.parseInt( list.get(3) );
		String[] type=list.get(4).split(",");
		Main=type[0].equals("1");
		if(type.length>1)RundamNumber=Integer.parseInt(type[1]);
		for(int i=2;i<type.length;i++){
			Reinforcements.add(type[i]);
		}
		//マップ情報の取得
		MapNumber = Integer.parseInt(list.get(5));
		//BGMのファイル名取得
		bgmFileName = list.get(6);
		//勝利条件
		String[] Victory_String=list.get(7).split(",");
		int Num_V=Integer.parseInt(Victory_String[0]);
		String Detail_V;
		if(Victory_String.length==1)Detail_V="敵軍";
		else Detail_V=Victory_String[1];
		Victory = new Issue(Num_V,Detail_V,true);
		
		//敗北条件
		String[] Defeat_String=list.get(8).split(",");
		int Num_D=Integer.parseInt(Defeat_String[0]);
		String Detail_D;
		if(Defeat_String.length==1)Detail_D="自軍";
		else Detail_D=Defeat_String[1];
		Defeat = new Issue(Num_D,Detail_D,false);
		
		String[] allys = list.get(9).split(",");
		Places = new Point[Integer.parseInt(allys[0])];
		for(int i=0;i<Places.length;i++){
			int x=Integer.parseInt(allys[2*i+1]);
			int y=Integer.parseInt(allys[2*i+2]);
			Places[i] = new Point(x,y);
		}
		//既に配置されている味方の登録
		for(int i=2*Places.length+1;i<allys.length;i+=4){
			String name=allys[i];
			int lv=Integer.parseInt(allys[i+1]);
			int x=Integer.parseInt(allys[i+2]);
			int y=Integer.parseInt(allys[i+3]);
			Allys.add(new CharaData_Actually(name,lv,x,y));
		}
		//敵の登録
		for(int i=10;i<list.size();i++){
			String data[] = list.get(i).split(",");
			String name=data[0];
			int lv=Integer.parseInt(data[1]);
			int x=Integer.parseInt(data[2]);
			int y=Integer.parseInt(data[3]);
			Enemies.add(new CharaData_Actually(name,lv,x,y));
		}
		
	}
	
	/** EventListを外部に渡すメソッド */
	public static ArrayList<EventData> getEventList(){
		return EventList;
	}
	
	/** イベントの検索 */
	public static EventData searchEvent(String name){
		for(EventData E : EventList){
			if(name.equals(E.EventName))return E;
		}
		return null;
	}
	
	/** 選べるイベントのリストを返す */
	public static ArrayList<EventData> getUsableEvent(){
		ArrayList<EventData> list = new ArrayList<EventData>();
		for(EventData E : EventList){
			boolean[] parmits={false,false};
			if(E.ConditionEvent==null)parmits[0]=true;
			for(int num : FinishedEvents){
				if(E.EventNumber==num || (E.DeleteEvent!=null && E.DeleteEvent==num)){
					parmits[1]=true;
					break;
				}
				if(E.ConditionEvent!=null && E.ConditionEvent==num)parmits[0]=true;
			}
			if(parmits[0] && !parmits[1]) list.add(E);
		}
		return list;
	}
	
	/** 引数の名前のイベントをNowEventにセットするメソッド */
	public static void setNowEvent(String name){
		EventData event=searchEvent(name);
		NowEvent=event;
	}
	
	/** 引数のイベントをNowEventにセットするメソッド */
	public static void setNowEvent(EventData data){
		NowEvent=data;
	}
	
	/** 現在のイベントに対応するBattleFieldオブジェクトを返すメソッド */
	public static BattleField createBattleField(){
		int mapN=NowEvent.MapNumber;
		ArrayList<CharaData> allys = getCharaList(true);
		ArrayList<CharaData> enemies = getCharaList(false);
		return new BattleField(mapN,Paramaters.PlayerNumber,Paramaters.AINumber,allys,enemies,NowEvent.Places,NowEvent.bgmFileName);
	}
	
	/** 現在のイベントのキャラリストを返すメソッド */
	public static ArrayList<CharaData> getCharaList(boolean ally){
		ArrayList<CharaData> CharaList = new ArrayList<CharaData>();
		ArrayList<CharaData_Actually> list;
		if(ally) list=NowEvent.Allys;
		else list=NowEvent.Enemies;
		for(CharaData_Actually C : list){
			CharaData chara = C.getCharaData();
			CharaList.add(chara);
		}
		return CharaList;
	}
	
	/** 現在のイベントの勝利条件と敗北条件の文字列を返すメソッド */
	public static String[] getConditions(){
	    return NowEvent.getThisConditions();
	}
	
	/** 現在のイベントの勝利条件と敗北条件の文字列を返すメソッド */
    public String[] getThisConditions(){
        String[] conditions=new String[2];
        conditions[0] = Victory.getCondition();
        conditions[1] = Defeat.getCondition();
        return conditions;
    }
	
	/** 勝ったかどうか判断するメソッド */
	public static boolean isWin(BattleField bf){
		return NowEvent.Victory.isConcurrence(bf);
	}
	
	/** 負けたかどうか判断するメソッド */
	public static boolean isLose(BattleField bf){
		return NowEvent.Defeat.isConcurrence(bf);
	}
	
	/** イベント名を返すメソッド */
	public String getEventName(){
		return EventName;
	}
	
	/** イベントの説明文を返すメソッド */
	public String getEventDetais(){
	    return eventDetails;
	}
	
	/** このイベントがメインイベントかどうかを返すメソッド */
	public boolean isMain(){
		return Main;
	}
	
	/** 次に表示するシーン番号を返すメソッド */
	public static int getNextScene(){
		addFriends();
		FinishedEvents.add(NowEvent.EventNumber);
		if(NowEvent.commonEventNum != null)FinishedEvents.add(NowEvent.commonEventNum);
		Paramaters.recoverAllys(); //全味方キャラの回復
		Integer sentenceNum = getSentenceNum();
		if(Friends.size() > 0 && sentenceNum != null){
			return Paramaters.FRIEND_SENTENCE_SCENE;
		}
		else if(Friends.size() > 0){
			return Paramaters.FRIEND_SCENE;
		}
		else if(sentenceNum !=null){
			return Paramaters.SENTENCE_SCENE;
		}
		NowEvent=null;
		return Paramaters.STRATEGY_SCENE;
	}
	
	public static Integer getSentenceNum(){
		return SentenceNumbers.get(NowEvent.EventNumber);
	}
	
	/** 新たに仲間を加える処理をする(フィールドFirendsにキャラクターを加える)メソッド */
	private static void addFriends(){
		Friends.clear();
		addAlly();  //Allysに登録されているキャラを加える
		addReinforcement(); //Reinforcementsに登録されているキャラを加える
		addNormalEnemy();  //敵のモブキャラを仲間に加える
	}
	
	/** Allysに登録されているキャラを加えるメソッド */
	private static void addAlly(){
		ArrayList<CharaData> list = new ArrayList<CharaData>();
		int num=NowEvent.Allys.size();
		int all=Paramaters.getAllyNumbers();
		for(int i=all-num; i<all; i++){
			CharaData C=Paramaters.getAllyChara(i);
			if(C!=null)list.add(C);
		}
		Friends.addAll(list);
	}
	
	/** Reinforcementsに登録されているキャラを加えるメソッド */
	private static void addReinforcement(){
		for(String name : NowEvent.Reinforcements){
			for(CharaData_Actually C : NowEvent.Enemies){
				if(name.equals(C.Name)){
					CharaData chara=C.getCharaData();
					chara.setNullPlace();
					Friends.add(chara);
					break;
				}
			}
		}
	}
	
	/** 敵のモブキャラを仲間に加えるメソッド。加える数はインスタンスフィールドRundamNumberに従う */
	private static void addNormalEnemy(){
		ArrayList<CharaData> list = getCharaList(false);
		ArrayList<CharaData> candidates = new ArrayList<CharaData>();  //候補キャラリスト
		//candidatesリストに候補キャラを入れる
		for(CharaData C: list){
			if(!C.isOriginal()){
				int index;
				//顔画像が同じキャラはcandidatesリストに格納しない
				for(index=0;index<candidates.size();index++){
					CharaData elem=candidates.get(index);
					if(C.getPictureName().equals(elem.getPictureName()))break;
				}
				if(index==candidates.size())candidates.add(C);
			}
		}
		for(int i=0;i<NowEvent.RundamNumber;i++){
			double random = Paramaters.getRandom();
			for(int j=0;j<candidates.size();j++){
				if(random<1.0*(j+1)/candidates.size()){
					CharaData C=candidates.remove(j);  //candidatesリストからこのキャラを消す
					C.setNullPlace();
					//キャラの名前の決定
					C.changeName(Paramaters.getRandomName(C.isMeal()));
					Friends.add(C);
					break;
				}
			}
		}
	}
	
	public static void formatNowEvent(){
		NowEvent = null;
	}
	
	/** ゲームの最初の画面で初めからを選んだ時に呼び出されるメソッド */
	public static void format(){
		NowEvent = null;
		FinishedEvents.clear();
	}
	
	/** FinishedEventsを文字列表現にして返り値として返す */
	public static String getFinishedEvents_String(){
		String str="";
		for(int num :FinishedEvents){
			str+=num+",";
		}
		if(str.length()==0)return str;
		else return str.substring(0, str.length()-1);
	}
	
	/** 引数の文字列をFinishedEventsに格納するメソッド */
	public static void setFinishedEvents(String str){
		if(str.equals(""))return;
		String[] array=str.split(",");
		for(String data : array){
			FinishedEvents.add(Integer.parseInt(data));
		}
	}
}

/** 勝敗条件に関するクラス */
class Issue{
	int Type;
	String Detail;
	boolean Win; //勝利条件ならtrue,敗北条件ならfalse
	
	public Issue(int t,String d,boolean w){
		Type=t;
		Detail=d;
		Win=w;
	}
	
	/** 条件を文字列に変換して返すメソッド */
	public String getCondition(){
		String condition="";
		switch(Type){
			case Paramaters.ABOUT_PLACE: 
				MapChip m=MapChip.searchChip(Detail);
				if(m!=null)condition=m.getName();
				condition+="への到達";
				break;
			case Paramaters.ABOUT_CHARACTER:
				condition=Detail+"の撃破";
				break;
			case Paramaters.ABOUT_ALL:
				condition=Detail+"の全滅";
				break;
			default: break;
		}
		return condition;
	}
	
	/** この条件を満たしているかどうかの判定 */
	public boolean isConcurrence(BattleField bf){
		switch(Type){
		case Paramaters.ABOUT_PLACE:
			ArrayList<CharaData> list1=bf.getList_Chara(Win);
			Map map=bf.getNowMap();
			for(CharaData C: list1){
				if(C.isMovable())continue;
				Point pt=C.getPlace();
				MapChip m=map.getChip(pt.x, pt.y);
				if(Detail.equals(m.getName()))return true;
			}
			return false;
		case Paramaters.ABOUT_CHARACTER:
			ArrayList<CharaData> list2=bf.getList_Chara(!Win);
			for(CharaData C: list2){
				if(Detail.equals(C.getCharaName()) && C.isOriginal())return false;
			}
			return true;
		case Paramaters.ABOUT_ALL:
			ArrayList<CharaData> list3=bf.getList_Chara(!Win);
			if(list3.size()==0)return true;
			else return false;
		default: return false;
		}
	}
}

/** キャラの必要最低限のデータを保存するオブジェクト */
class CharaData_Actually{
	String Name;
	int LV;
	Point Place;
	
	public CharaData_Actually(String name,int lv,int x,int y){
		Name=name;
		LV=lv;
		Place=new Point(x,y);
	}
	
	public CharaData getCharaData(){
		CharaData C=CharaData.searchChara(Name);
		C.setLV(LV);
		C.resetExperiment();
		C.recoverAll();
		C.setPlace(Place.x, Place.y);
		return C;
	}
}
