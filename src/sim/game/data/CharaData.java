package sim.game.data;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.*;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import sim.game.Paramaters;
import sim.game.battle.BattleField;
import sim.game.brain.BrainParameter;
import sim.game.brain.MovingBrain;
import sim.tools.GraphicTool;

public class CharaData {
	private static ArrayList<CharaData> CharaList;  //CharaListファイルに登録されているもののみ格納する
	private static String[] UnusualList={"毒","失神","怪我"};
	private static Image[] UnusualImageSet={Paramaters.Image_BOX.getImage("毒"),Paramaters.Image_BOX.getImage("失神"),Paramaters.Image_BOX.getImage("ケガ")};
	private String Name;
	private String PictureName;
	private int status[]=new int[11];//0はHP,1は攻撃力,2は防御力,3は命中率,4は回避率,5は知力,6は運,7は移動力,8はキャラクタータイプ(固有かモブか),9は反撃タイプ,10は限界職業LV
	private int Propers[]=new int[10]; //職業適正。0は剣士、1は槍術士、2は斧使い、3は毒薬剤師、4は格闘家、5は暗殺者、6は射手、7は狙撃手、8は医者、9は兵器開発者
	private final int[] EarlyStatus = {150,80,80,80,80,80,80,5};
	private int BasicProperPoint;  //基本的な適正ポイント
	private int ProperPoint=0;
	private final int MaxProper=30;
	private final int NormalProper=10;
	private final int FirstProper = 18;  //本来は15
	private final int MinProper = 8;
	private String[] JobNameArray = new String[0];   //このキャラの職業履歴(ファイルから生成されたキャラにだけ必要なフィールド)
	private int[] JobLevelArray =new int[0];     //このキャラの職業レベルの履歴(ファイルから生成されたキャラにだけ必要なフィールド)
	private ArrayList<Skill> skillList = new ArrayList<Skill>();
	private ArrayList<String> SelfLands = new ArrayList<String>();
	private ArrayList<Job> JobList = new ArrayList<Job>();  //経験した職業リスト(先頭の職業は現在の職業)
	private double FaintParsent=0;
	private int[] StateList = {0,0,0};
	private int Experiment; //今の経験値
	private int MaxE;  //レベルアップに必要な経験値
	private int NowHP; //今のHP
	private Point Place=null; //MAP中において今いる場所
	private boolean Usable=true; //行動できる状態にあるかどうか
	private BrainParameter Parameter_Brain=null;
	private int Level=1;
	public static final int MaxLevel = 30;  //最大レベル
	private boolean Meal; //trueなら男、falseなら女
	private final double Rate_LevelUp = 1.2;
	private final double NecessaryPoint=100;
	private GraphicTool Tool_G = new GraphicTool();
	private Integer AllyNumber = null;  //このキャラが味方になった時の番号 
	private final int FirstMaxJobLV = 8;
	private HashMap<Integer,Integer> Likability = new HashMap<Integer,Integer>();  //他の味方キャラへの好感度を表すハッシュマップ
	private int FaceNumber = Paramaters.Normal;  //顔画像の差分番号
	private MovingBrain moveRange = null; //移動範囲
	
	//初期登録キャラの初期状態
	public CharaData(){
		double random = Paramaters.getRandom();
		Meal = (random<0.5);
		Name = Paramaters.getRandomName(Meal);
		PictureName = Paramaters.Image_BOX.getRandomFaceName(Meal);
		Level=1;
		for(int i=0;i<EarlyStatus.length;i++){
			status[i]=EarlyStatus[i];
		}
		status[8]=Paramaters.Friend_Chara;
		status[9]=0;
		status[10]=FirstMaxJobLV;
		Experiment=0;
		MaxE=(int)NecessaryPoint; //(int)Math.round( NecessaryPoint*Math.pow(Rate_LevelUp, Level-1) );
		NowHP=getHP();
		BasicProperPoint=FirstProper;
		ProperPoint=FirstProper;
	}
	
	//子供の初期能力
	public CharaData(CharaData[] parents){
		PictureName=Gene.getChildFace(parents[0].PictureName,parents[1].PictureName);
		if(PictureName.lastIndexOf("男")!=-1)Meal=true;
		else Meal=false;
		Name = Paramaters.getRandomName(Meal);
		Level=1;
		//能力の設定
		for(int i=0;i<EarlyStatus.length;i++){
			status[i]=EarlyStatus[i];
		}
		status[8]=Paramaters.Friend_Chara;
		status[9]=0;
		
		//親の職業レベルに従って能力をアップさせる
		for(int i=0;i<parents.length;i++){
		    for(int j=0; j<Propers.length; j++){
		        Propers[j] = (int)Math.round(0.8 * parents[i].Propers[j]);
		    }
			updateStatusForChild( (int)Math.round( 1.0 * parents[i].getLV()/2) );
		}
		//Propersの要素を全て0にする
		for(int i=0;i<Propers.length;i++) Propers[i]=0;
		
		//親の職業レベルに従って限界職業LVを決める
		status[10]=parents[0].getLimitJobLV(parents[1].getAllyNumber())+parents[1].getLimitJobLV(parents[0].getAllyNumber());
		//親の適正ポイントに従って適正ポイントを決める
		BasicProperPoint=parents[0].getProperPoint(parents[1].getAllyNumber())+parents[1].getProperPoint(parents[0].getAllyNumber());
		ProperPoint=BasicProperPoint;
		Experiment=0;
		MaxE=(int)NecessaryPoint; //(int)Math.round( NecessaryPoint*Math.pow(Rate_LevelUp, Level-1) );
		NowHP=getHP();
	}
	
	/** 初期ファイルから読み込むときに使用 */
	public CharaData(String name,String name_P,int[] ss,int[] ps,ArrayList<String> jobs,ArrayList<Integer> levels,ArrayList<String> ls){
		Name=name;
		PictureName=name_P;
		Level=1;
		for(int i=0;i<status.length;i++){
			status[i]=ss[i];
		}
		if(status[8]!=Paramaters.Oniginal_Chara) status[8]=Paramaters.Mob_Chara;
		Meal = (ss[ss.length-2]==1);
		Parameter_Brain = BrainParameter.getParameter(ss[ss.length-1]);
		for(int i=0;i<ps.length;i++){
			Propers[i]=ps[i];
		}
		//職業の仮登録
		JobNameArray = new String[jobs.size()];
		JobLevelArray = new int[levels.size()];
		for(int i=0;i<JobNameArray.length;i++){
			JobNameArray[i] = jobs.get(i);
			JobLevelArray[i] = levels.get(i);
		}
		//得意マップの登録
		for(String C: ls){
			SelfLands.add(C);
		}
		Experiment=0;
		MaxE=(int)NecessaryPoint; //(int)Math.round( NecessaryPoint*Math.pow(Rate_LevelUp, Level-1) );
		NowHP=getHP();
		BasicProperPoint=getBasicProperPoint();
	}
	
	/** CharaListのキャラを複製する時に使用。引数のbooleanはこのキャラが味方キャラかどうか*/
	public CharaData(CharaData C,boolean ally){
		Name=C.Name;
		PictureName=C.PictureName; 
		Level=C.Level;
		//ステータスの読み込み
		for(int i=0;i<status.length;i++){
			status[i]=C.status[i];
		}
		//適正ポイントの読み込み
		for(int i=0;i<Propers.length;i++){
			Propers[i]=C.Propers[i];
		}
		Meal = C.Meal;
		Parameter_Brain = C.Parameter_Brain;
		if(ally){
			AllyNumber = C.AllyNumber;
			for(Job elem :C.JobList){
				Job job=Job.getJob(elem.getJobName());
				job.setLevel(elem.getLevel());
				job.setExperience(elem.getExperience());
				JobList.add(job);
			}
			//好感度のセットのし直し
			Likability.putAll(C.Likability);
		}
		else{
			for(int i=0;i<C.JobNameArray.length;i++){
				Job job=Job.getJob(C.JobNameArray[i]);
				job.setLevel(C.JobLevelArray[i]);
				JobList.add(job);
			}
		}
		SelfLands.addAll(C.SelfLands);
		Experiment=C.Experiment;
		MaxE=C.MaxE;
		NowHP=C.NowHP;
		ProperPoint=C.ProperPoint;
		BasicProperPoint=C.BasicProperPoint;
		createSkillList();  //スキルリストの作成を行う
	}
	
	/** ロード画面で呼び出されるコンストラクタ */
	public CharaData(String[] data){
		setBasicData(data[0]);
		setJobData(data[1]);
		setSelfLands(data[2]);
		setLikability(data[3]);
		NowHP=getHP();
		createSkillList();
	}
	
	/** このキャラのBasicProperPointを計算するメソッド */
	private int getBasicProperPoint(){
		int total=0;
		for(int i=0;i<Propers.length;i++){
			total+=Propers[i];
		}
		if(SelfLands.size()>0)
			total+=(int)Math.round( Math.pow(2, SelfLands.size()+1)-1 );
		return total;
	}
	
	/** JobListに従ってskillListを作成するメソッド。 */
	public void createSkillList(){
		for(Job j: JobList){
			ArrayList<Skill> list = j.getSkillList();
			skillList.addAll(list);
		}
	}
	
	/**キャラクターの名前を返すメソッド*/
	public String getCharaName(){
		return Name;
	}
	
	/** キャラクターの名前を引数の名前に変更するメソッド */
	public void changeName(String str){
		Name=str;
	}
	
	/** キャラクターの顔画像を変更するメソッド */
	public void changeFace(String face){
		PictureName=face;
	}
	
	/** キャラクターの顔画像の名前を返り値として返すメソッド */
	public String getPictureName(){
		return PictureName;
	}
	
	/** キャラクターの性別を返すメソッド。男ならtrue、女ならfalseを返す. */
	public boolean isMeal(){
		return Meal;
	}
	
	/** キャラクターの性別を変更するメソッド */
	public void changeSex(boolean meal){
		Meal=meal;
	}
	
	/**HPの値を返す*/
	public int getHP(){
		double rev=1.0;
		if(JobList.size()>0)rev=JobList.get(0).getRevision(0);
		return (int)Math.round(rev*status[0]);
	}
	
	/** 攻撃力の値を返す */
	public int getAttack(){
		return getStatus(1);
	}
	
	/** 防御力の値を返す */
	public int getDiffence(){
		return getStatus(2);
	}
	
	/** 命中率の値を返す */
	public int getHit(){
		return getStatus(3);
	}
	
	/** 回避率の値を返す */
	public int getAvoid(){
		return getStatus(4);
	}
	
	/**知力の値を返す*/
	public int getIntel(){
		return getStatus(5);
	}
	
	/** 運の値を返す */
	public int getRuck(){
		return getStatus(6);
	}

	/** 移動力の値を返す */
	public int getMove(){
		double rev=1.0;
		if(JobList.size()>0)rev=JobList.get(0).getRevision(7);
		return (int)Math.round(rev*status[7]);
	}
	
	/** レベルの値を返す */
	public int getLV(){
		return Level;
	}
	
	/**職業レベルの値を返す*/
	public int getJobLV(){
		if(JobList.size() == 0) return 0;
		return JobList.get(0).getLevel();
	}
	
	/** 合計職業レベルの値を返す */
	public int getAllJobLV(){
		int lv=0;
		for(Job j :JobList){
			lv+=j.getLevel();
		}
		return lv;
	}
	
	/** このキャラの各職業のレベルを配列にまとめたものを返すメソッド */
	public int[] getJobLVArray(){
		int[] array={0,0,0,0,0,0,0,0,0,0};
		for(Job job :JobList){
			int index=job.Number;
			array[index]=job.getLevel();
		}
		return array;
	}
	
	/** 子供の能力アップ用メソッド。引数の職業レベル配列(親の職業レベル)に従って能力を上げる */
	public void updateStatusForChild(int count){
		for(int i=0; i<count; i++){
		    updataStates();
		}
	}
	
	/** 固有キャラかどうかを返す */
	public boolean isOriginal(){
		return (status[8]==Paramaters.Oniginal_Chara);
	}
	
	/** キャラタイプを味方キャラに変更する */
	public void typeToFriend(){
		status[8]=Paramaters.Friend_Chara;
	}
	
	/** 反撃タイプを返す */
	public int getCounterType(){
		return status[9];
	}
	
	/** 戦闘力を計算して返すメソッド。現在の残りHPも考慮する。 */
	public int getStrongScore(){
		if(StateList[1]>0)return 0;
		int total=0;
		for(int i=0;i<=6;i++){
			double p=1.0;
			if( i==0)p=0.5;
			int pram;
			if( i==0 )pram=getHP();
			else pram=getStatus(i);
			total+=(int)Math.round(p*pram);
		}
		return (int)Math.round(1.0*total*NowHP/getHP());
	}
	
	/** 指定されたパラメーターの値を返すメソッド */
	private int getStatus(int i){
		double rev=1.0;
		if(JobList.size()>0)rev=JobList.get(0).getRevision(i);
		int param=(int)Math.round(rev*status[i]);
		int minus=(int)Math.round(getInjured()*param);
		return param-minus;
	}
	
	/** このキャラが引数の職業についた場合のパラメータ配列を返すメソッド */
	public int[] getStatuses(Job job){
		int[] prams = new int[8];
		for(int i=0;i<prams.length;i++){
			double rev = job.getRevision(i);
			prams[i]=(int)Math.round(rev*status[i]);
		}
		return prams;
	} 
	
	/** このキャラの職業を引数の職業に変更する */
	public void changeJob(Job job){
		//既に経験した職業かどうか調べる
		int index;
		for(index=0;index<JobList.size();index++){
			Job j=JobList.get(index);
			if(job.Number==j.Number){
				JobList.remove(j);
				JobList.add(0,j);
				break;
			}
		}
		//新規の職業ならば、新たに職業リストに追加する
		if(index==JobList.size())JobList.add(0,job);
		NowHP=getHP();  //HPを今の職業のHPに合わせる
	}
	
	/** ケガの度合いを返す */
	private double getInjured(){
		if(StateList[2]==0)return 0;
		else return ( 1.0 * (20 * (StateList[2]-1) + 10) / 100 );
	}
	
	/** このキャラの持つ脳内パラメーターを返す */
	public BrainParameter getBrainParameter(){
		return Parameter_Brain;
	}
	
	/** レベルを設定する処理 */
	public void setLV(int lv){
		setLV(lv, true);
	}
	
	public void setLV(int lv,boolean onlyUp){
	    int up=lv-Level;
	    if(up<=0 && onlyUp)return;
	    for(int i=0;i<up;i++){
	        updataStates();
	    }
	    for(int i = 0; i > up; i--){
	    	updataStates(false);
	    }
	    Level=lv;
	}
	
//	public void setLV(int lv){
//		int all=0;
//		int up=lv-Level;
//		if(up<=0)return;
//		double[] rates=new double[JobList.size()];
//		for(Job job : JobList){
//			all+=job.getLevel();
//		}
//		for(int i=0;i<rates.length;i++){
//			Job job=JobList.get(i);
//			rates[i]=1.0*job.getLevel()/all;
//		}
//		for(int i=0;i<up;i++){
//			double random = Paramaters.getRandom();
//			double now=0;
//			for(int j=0;j<rates.length;j++){
//				now+=rates[j];
//				if(now>=random){
//					Job job=JobList.get(j);
//					updataStates(job.Number);
//					break;
//				}
//			}
//		}
//		Level=lv;
//	}
	
	/** 現在の経験値の取得 */
	public int getExperiment(){
		return Experiment;
	}
	
	/** 現在の経験値をリセットするメソッド */
	public void resetExperiment(){
		Experiment=0;
	}
	
	/** 引数の経験値を得るメソッド.返り値は上昇したレベル。 */
	public int getExp(int e){
		if(Level >= MaxLevel) return 0;
		if(Experiment+e>=MaxE){
			int exp=Experiment;
			Experiment=0;
			updataStates();
			upLevel();
			return getExp(e-(MaxE-exp))+1;
		}
		else{
			Experiment+=e;
			return 0;
		}
	}
	
	/** 現在の職業に従ってレベルアップもしくはレベルダウン時の処理を行う */
	private void updataStates(){
		updataStates(true);
	}
	
	/** 現在の職業に従ってレベルアップもしくはレベルダウン時の処理を行う */
	private void updataStates(boolean up){
		if(Level>=MaxLevel) return;
		for(int index=0; index<Propers.length; index++){
		    if(Propers[index] == 0) continue;
		    updateStatusByJob(index,up);
		}
	}
	
	/** レベルアップを行うメソッド */
	private void upLevel(){
		if(Level<MaxLevel)Level++;
	}
	
	/** レベルアップ時の処理。引数は職業番号 */
	private void updateStatusByJob(int number, boolean up){
		if(Level>=MaxLevel || number<0 || number>=Propers.length)
			return;
		Job job = Job.getJob(number);
		double rate = 1.0*Propers[number]/NormalProper;
		for(int i=0;i<7;i++){
			int sign = up ? 1 : -1;
			status[i]+=(int)Math.round(sign*rate*job.getRise(i));
		}
	}
	
	/** 引数の職業経験値を得るメソッド。 */
	public int getJobExp(double e){
		if(JobList.size()==0)return 0;
		Job NowJ=JobList.get(0);
		double rate = 1.0*Propers[NowJ.Number]/NormalProper;
		return NowJ.getJobExp(e*rate,0);
	}
	
	/** 職業レベルが上がった時の処理 */
	public void levelupJob(int l){
		if(JobList.size()==0)return;
		Job NowJ=JobList.get(0);
		int jobLV=getAllJobLV();
		int up;
		if(status[10]-jobLV<l) up=status[10]-jobLV;
		else up=l;
		NowJ.levelup(up, skillList);
	}
	
	/** (移動後に)技の使用可能範囲を求めるメソッド */	
	public void setSkillsRange(ArrayList<CharaData> AL,ArrayList<CharaData> EL,Map m,boolean a){
		MovingBrain brain = new MovingBrain(AL,EL,m,this,a);
		ArrayList<Point> list = brain.getReachs();
		for(Skill S : skillList){
			S.setRangeList(list, m);
		}
	}
	
	public static void setCharaList(ArrayList<CharaData> list){
		CharaList = list;
	}
	
	public static ArrayList<CharaData> getCharaList(){
		return CharaList;
	}
	
	/** キャラクターの検索 */
	public static CharaData searchChara(String str){
		for(CharaData C : CharaList){
			if(str.equals(C.Name)){
				return new CharaData(C,false);
			}
		}
		return null;
	}
	
	/** 引数キャラクターのクローンの作成 */
	public static CharaData getClone(CharaData C){
		if(C==null)return null;
		else return new CharaData(C,true);
	}
	
	/** 得意マップチップかどうか */
	public boolean isSuccessChip(String chip){
		for(String elem : SelfLands){
			if(elem.equals(chip))return true;
		}
		return false;
	}
	
	/** 行動できるかどうか */
	public boolean isMovable(){
		return (Usable && StateList[1]==0);
	}
	
	/** 変数Usableに値を与える */
	public void setPermition(boolean b){
		if(StateList[1]==0) Usable=b;
	}
	
	/** 引数の座標とフィールドの座標が一致するかどうか */
	public boolean isPlace(Point pt){
		if( Place.x==pt.x && Place.y==pt.y )return true;
		else return false;
	}
	
	/** キャラクターを引数の座標に移動させる */
	public void setPlace(int x,int y){
		Place=new Point(x,y);
	}
	
	/** フィールドPlaceにnullを代入するメソッド */
	public void setNullPlace(){
		Place=null;
	}
	
	/** このキャラのスキルリストの取得 */
	public ArrayList<Skill> getSkillList(){
		return skillList;
	}
	
	/** このキャラの今使用できる反撃用のスキルリストの取得 */
	public ArrayList<Skill> getCounterSkillList(Point pt,Map map){
		ArrayList<Skill> list = new ArrayList<Skill>();
		for(Skill S : skillList){
			if(S.isCounterAttack(Place,pt,map))list.add(S);
		}
		return list;
	}
	
	/** このキャラクターの現在の座標を返す */
	public Point getPlace(){
		return Place;
	}
	
	/** このキャラクターが撃破されたかどうか */
	public boolean isDie(){
		return (NowHP<=0);
	}
	
	/** 現在のHPを引数の分だけ減らす */
	public void downHP(int d){
		NowHP-=d;
		int max=getHP();
		if(NowHP>max)NowHP=max;
		if(NowHP<0)NowHP=0;
	}
	
	/** このキャラクターの残りHPを返す */
	public int getNowHP(){
		return NowHP;
	}
	
	/** このキャラのドット絵を指定された場所に描く */
	public void drawDot(Graphics g,Container con,int x,int y,boolean ally){
		int index;
		if(Usable && StateList[1]==0) index=1;
		else index=3;
		BufferedImage img=Paramaters.Image_BOX.getBufferdImage(PictureName+Paramaters.DotPicture+index);
		Color col;
		if(ally) col=new Color(0,30,150,120);
		else col=new Color(150,0,0,120);
		g.setColor(col);
		g.fillRect(x*MapChip.Width_Chip, y*MapChip.Height_Chip, MapChip.Width_Chip, MapChip.Height_Chip);
		g.drawImage(img,x*MapChip.Width_Chip,y*MapChip.Height_Chip,con);
		
		//以下、状態異常の描画
		int dx=0;
		for(int i=0;i<UnusualImageSet.length;i++){
			if(StateList[i]>0){
				g.drawImage(UnusualImageSet[i],x*MapChip.Width_Chip+dx,(int)Math.round(y*MapChip.Height_Chip-0.5*MapChip.Height_Chip),con);
				dx+=(int)Math.round(0.3*MapChip.Width_Chip);
			}
		}
	}
	
	/** このキャラの顔グラを指定された所に描く */
	public void drawFace(Graphics g,Container con,int x,int y){
		BufferedImage img=Paramaters.Image_BOX.getBufferdImage(PictureName+Paramaters.FacePicture+FaceNumber);
		g.drawImage(img,x,y,con);
		Tool_G.drawFrame(g,con,x,y,img.getWidth(),img.getHeight());
	}
	
	/** このキャラクターのステータス画面を表示する */
	public void drawStatus(Graphics g,Container con,int x,int y,int width,int height){
		int f;
		if(width>height) f=height;
		else f=width;
		int font=(int)Math.round(0.05*f);
		g.setColor(new Color(0,0,0,150));
		g.fillRect(x, y, width, height);
		drawFace(g,con,(int)Math.round(x+0.05*width),(int)Math.round(y+0.05*height));
		g.setColor(Color.white);
		g.setFont( new Font("明朝体",Font.BOLD,font) );
		String s;
		if(Meal)s="男";
		else s="女";
		g.drawString("性別:"+s, (int)Math.round(x+0.05*width), (int)Math.round(y+0.34*height));
		String J;
		if(JobList.size()==0)J="無職";
		else J=JobList.get(0).getJobName();
		g.drawString("LV:"+Level, (int)Math.round(x+0.05*width), (int)Math.round(y+0.42*height));
		g.drawString("職業:"+J, (int)Math.round(x+0.05*width), (int)Math.round(y+0.50*height));
		g.drawString("名前:"+Name, (int)Math.round(x+0.05*width), (int)Math.round(y+0.26*height));
		g.drawString("状態異常:"+StateString(),(int)Math.round(x+0.45*width),(int)Math.round(y+0.16*height));
		drawBarGraph(g,(int)Math.round(x+0.55*width), (int)Math.round(y+0.21*height),(int)Math.round(0.4*width), (int)Math.round(0.16*height),"HP",getHP(),NowHP,font);
		drawBarGraph(g,(int)Math.round(x+0.55*width), (int)Math.round(y+0.39*height),(int)Math.round(0.4*width), (int)Math.round(0.16*height),"EXP",MaxE,Experiment,font);
		g.setFont( new Font("明朝体",Font.BOLD,font) );
		String spec1="攻撃:"+getAttack()+"　防御:"+getDiffence()+"　命中:"+getHit()+"　回避:"+getAvoid();
		g.drawString(spec1, (int)Math.round(x+0.05*width), (int)Math.round(y+0.72*height));
		String spec2="知力:"+getIntel()+"　運:"+getRuck()+"　移動:"+getMove();
		g.drawString(spec2, (int)Math.round(x+0.05*width), (int)Math.round(y+0.85*height));
		String spec3="得意MAP:"+MapchipsString();
		g.drawString(spec3, (int)Math.round(x+0.05*width), (int)Math.round(y+0.98*height));
	}
	
	/** 1つの職業に関するパラメーターを描画する */
	private void drawJobStatus(Graphics g,Container con,int x,int y,int width,int height,Job job){
		g.setFont(new Font("明朝体",Font.BOLD,(int)Math.round(0.2*height)));
		g.setColor(new Color(0,0,0,150));
		g.fillRect(x,y,width,height);
		Tool_G.drawFrame(g,con,x,y,width,height);
		g.setColor(Color.blue);
		g.fillRect((int)Math.round(x+0.45*width),(int)Math.round(y+0.42*height),
				(int)Math.round(0.5*width*job.getExperience()/job.getExpToNextLv()),(int)Math.round(0.15*height));
		g.setColor(Color.white);
		g.drawRect((int)Math.round(x+0.45*width),(int)Math.round(y+0.42*height),(int)Math.round(0.5*width),(int)Math.round(0.15*height));
		g.drawString(job.getJobName(),(int)Math.round(x+0.05*width),(int)Math.round(y+0.21*height));
		g.drawString("LV"+job.getLevel(),(int)Math.round(x+0.05*width),(int)Math.round(y+0.54*height));
		g.drawString("適正度:"+Propers[job.Number],(int)Math.round(x+0.05*width),(int)Math.round(y+0.87*height));
	}
	
	/**　このキャラクターの職業に関するパラメーターを表示する　*/
	public void drawAboutJob(Graphics g,Container con,int x,int y,int width,int height){
		g.setColor(new Color(0,0,0,150));
		g.fillRect(x, y, width, height);
		int half=5;
		double W=0.4;
		double H=0.15;
		double DX=0.05;
		for(int i=0;i<2;i++){
			double DY=0.02;
			for(int j=0;j<half;j++){
				Job job=getJobData(half*i+j);
				drawJobStatus(g,con,(int)Math.round(x+DX*width),(int)Math.round(y+DY*height),
						(int)Math.round(W*width),(int)Math.round(H*height),job);
				DY+=0.18;
			}
			DX+=0.5;
		}
		g.setColor(Color.white);
		g.setFont(new Font("明朝体",Font.BOLD,(int)Math.round(0.05*height)));
		String lv="限界職業LV:"+status[10]+"(現在の職業LV:"+getAllJobLV()+")";
		g.drawString(lv, (int)Math.round(x+0.3*width), (int)Math.round(y+0.98*height));
	}
	
	/** このキャラクターの簡易パラメーターを描画するメソッド */
	public void drawEasyParamater(Graphics g,Container con,int x,int y,int width,int height){
		g.setColor(new Color(0,0,0,150));
		g.fillRect(x, y, width, height);
		Tool_G.drawFrame(g,con,x,y,width,height);
		drawFace(g,con,(int)Math.round(x+0.05*width),(int)Math.round(y+0.05*height));//顔グラフィックの描画
		g.setColor(Color.white);
		int font=(int)Math.round(0.15*height);
		g.setFont( new Font("明朝体",Font.BOLD,font) );
		String J;
		if(JobList.size()==0)J="無職";
		else J=JobList.get(0).getJobName();
		g.drawString("名前:"+Name, (int)Math.round(x+0.22*width), (int)Math.round(y+0.3*height));
		g.drawString("LV:"+Level, (int)Math.round(x+0.22*width), (int)Math.round(y+0.55*height));
		g.drawString("職業:"+J, (int)Math.round(x+0.22*width), (int)Math.round(y+0.8*height));
		g.drawString("状態異常:"+StateString(),(int)Math.round(x+0.5*width),(int)Math.round(y+0.3*height));
		drawBarGraph(g,(int)Math.round(x+0.55*width), (int)Math.round(y+0.4*height),(int)Math.round(0.4*width), (int)Math.round(0.2*height),"HP",getHP(),NowHP,font);
		drawBarGraph(g,(int)Math.round(x+0.55*width), (int)Math.round(y+0.7*height),(int)Math.round(0.4*width), (int)Math.round(0.2*height),"EXP",MaxE,Experiment,font);
	}
	
	/** 棒グラフ表示のパラメーターの描画 */
	public void drawBarGraph(Graphics g,int x,int y,int width,int height,String name,int max,int now,int font){
		g.setFont( new Font("明朝体",Font.BOLD,font) );
		g.drawString(name,x-font*name.length(),(int)Math.round(y+0.9*height));
		g.setColor(Color.blue);
		g.fillRect(x, y, (int)Math.round(width*now/max),height);
		g.setColor(Color.yellow);
		g.drawString(now+"/"+max,(int)Math.round(x+0.3*width),(int)Math.round(y+0.9*height));
		g.setColor(Color.white);
		g.drawRect(x, y, width, height);
	}
	
	/** このキャラクターの引数の職業に関するデータを取得するメソッド */
	private Job getJobData(int num){
		for(Job j :JobList){
			if(num==j.Number)return j;
		}
		return Job.getJob(num);
	}
	
	/** HPを全回復させるメソッド */
	public void recoverHP(){
		NowHP=getHP();
	}
	
	/** ステータスを全て回復させるメソッド */
	public void recoverAll(){
		FaceNumber=Paramaters.Normal;
		FaintParsent=0;
		for(int i=0;i<StateList.length;i++){
			StateList[i]=0;
		}
		NowHP=getHP();
		Place=null;
		moveRange = null;
		Usable=true;
		for(Skill S: skillList){
			S.recover();
		}
	}
	
	/** MoveRangeに新たにMovingBrainインスタンスを代入するメソッド */
	public void setMoveRange(ArrayList<CharaData> allyList, ArrayList<CharaData> enemyList, Map map, boolean ally){
	    moveRange = new MovingBrain(allyList,enemyList,map,this,ally);
	}
	
	/** moveRangeの持つ全到着点をリストとして返すメソッド */
	public ArrayList<Point> getReachList(){
	    if(moveRange == null) return null;
	    ArrayList<Point> list = moveRange.getReachs();
	    return list;
	}
	
	/** moveRangeを返すメソッド */
	public MovingBrain getMoveRange(){
	    return moveRange;
	}
	
	/** 得意MAPリストを文字列に変換するメソッド */
	public String MapchipsString(){
		String str="";
		for(String land : SelfLands){
			if(!str.equals(""))str+=",";
			str+=land;
		}
		return str;
	}
	
	/** 現在の状態を文字列に変換するメソッド */
	public String StateString(){
		String state="";
		for(int i=0;i<StateList.length;i++){
			if(StateList[i]>0){
				if(!state.equals(""))state+=",";
				state+=(UnusualList[i]+"LV"+StateList[i]);
			}
		}
		if(state.equals(""))state="なし";
		return state;
	}
	
	/** 状態異常のセットを行う */
	public void setUnusual(int i,int num){
		StateList[i]=num;
		if( i==1 ){
			if(num>0){
				FaintParsent=1;
			}
			else{
				FaintParsent=0;				
			}
		}
	}
	
	/** 状態異常の回復を行うメソッド */
	public void recoverUnusual(int i,int num){
		if(num>=StateList[i]) StateList[i]=0;
		if(StateList[1]==0){
			FaintParsent=0;
		}
	}
	
	/** 現在の状態異常の度合いを閲覧するメソッド */
	public int checkUnusual(int i){
		if(i>=StateList.length)return 0;
		return StateList[i];
	}
	
	/** 状態異常リストを返すメソッド */
	public int[] getUnusuals(){
		return StateList;
	}
	
	/** 状態異常の効果を発動するメソッド */
	public void runUnusual(){
		runPoison();
		runFaint();
	}
	
	/** 毒の効果を発動するメソッド */
	private void runPoison(){
		if(StateList[0]==0)return;
		double[] parsentArray={0.05,0.15,0.25};
		double par=parsentArray[StateList[0]-1];
		int Minus=(int)Math.round(par*getHP());
		if(Minus==0)Minus=1;
		if(NowHP-Minus<=0)NowHP=1;
		else NowHP-=Minus;
	}
	
	/** 失神の効果を発動するメソッド */
	private void runFaint(){
		if(StateList[1]==0)return;
		double[] parsentArray={0.5,0.3,0.15};
		double par=parsentArray[StateList[1]-1];
		FaintParsent-=par;
		if(FaintParsent<0)FaintParsent=0;
		if(Paramaters.getRandom()>=FaintParsent){
			FaintParsent=0;
			StateList[1]=0;
		}
	}
	
	/** AllyNumberに値を与えるメソッド */
	public void setAllyNumber(int n){
		AllyNumber=n;
	}
	
	/** AllyNumberを返すメソッド */
	public Integer getAllyNumber(){
		return AllyNumber;
	}
	
	/** 引数番号の職業に就けるかどうか */
	public boolean isBecome(int num){
		return (Propers[num]>=MinProper);
	}
	
	/** 適正度の配列の大きさを返すメソッド */
	public int getPropersSize(){
		return Propers.length;
	}
	
	/** 残っているProperPointが引数の値以上かどうか */
	public boolean canAddPropers(int i){
		return (ProperPoint>=i);
	}
	
	/** 新たに得意MAPチップを覚えるのに必要なProperPointを返すメソッド */
	public int NecessaryPointToChip(){
		return (int)Math.round( Math.pow(2, SelfLands.size()+2)-1 );
	}
	
	/** 適正度の値を1アップさせる */
	public void addPropers(int i){
		int after=ProperPoint;
		if(Propers[i]==0)after-=MinProper;
		else after--;
		if(Propers[i]>=MaxProper || after<0)return;
		Propers[i]+=(ProperPoint-after);
		ProperPoint=after;
	}
	
	/** 新たに得意MAPチップを覚えさせるメソッド */
	public void addSelfLand(String land){
		int threshold=NecessaryPointToChip();
		if(ProperPoint<threshold)return;
		SelfLands.add(land);
		ProperPoint-=threshold;
	}
	
	/** このキャラの現在の職業の適性度を返す */
	public int getProper(){
		if(JobList.size()==0)return 0;
		return getProper(JobList.get(0).Number);
	}
	
	
	/** 指定された職業の適正度を返す */
	public int getProper(int i){
		if(i>=Propers.length)return 0;
		return Propers[i];
	}
	
	/** JobListとskillListの中身を空にするメソッド */
	public void clearList(){
		JobList.clear();
		skillList.clear();
	}
	
	/** 現在の職業のレベルを引数の数だけ上げるメソッド */
	public void upJobLV(int num){
		if(JobList.size()==0)return;
		Job job=JobList.get(0);
		job.setLevel(job.getLevel()+num);
	}
	
	/** フィールドProperPointの値を取得するメソッド */
	public int getProperPoint(){
		return ProperPoint;
	}
	
	/** フィールドProperPointに引数の値を代入するメソッド */
	public void resetProperPoint(){
		ProperPoint=BasicProperPoint;
	}
	
	/** フィールドのPropersの各要素を引数の配列の要素に置き換えるメソッド */
	public void setPropers(int[] ps){
		for(int i=0;i<Propers.length;i++){
			Propers[i]=ps[i];
		}
	}
	
	/** 適正度アップのための必要値を返すメソッド */
	public int getProperThreshold(int index){
		if(index<0 || index>=Propers.length)return 0;
		if(Propers[index]==0)return MinProper;
		else return 1;
	}
	
	/** フィールドのSelfLandsの各要素を引数のリストの要素に置き換えるメソッド*/
	public void setSelfLands(ArrayList<String> list){
		SelfLands.clear();
		SelfLands.addAll(list);
	}
	
	/** 引数の文字列に書いてある全てのマップチップをSelfLandsに格納するメソッドメソッド*/
	private void setSelfLands(String str){
		String array[]=str.split(",");
		ArrayList<String> list = new ArrayList<String>();
		for(int i=0;i<array.length;i++){
			list.add(array[i]);
		}
		setSelfLands(list);
	}
	
	/** 1ターン中に回数制限のある技の回復を行うメソッド */
	public void startTurnToSkill(){
		for(Skill S : skillList){
			S.startTurn();
		}
	}
	
	/** 異性の味方キャラをハッシュマップに追加する処理 */
	public void addLikability(Integer num,boolean meal){
		if(Meal==meal)return;
		Likability.put(num,0);  //好感度は0からスタート
	}
	
	/** 引数の味方キャラをハッシュマップから除外するメソッド */
	public void removeLikability(Integer num){
		Likability.remove(num);
	}
	
	/** 特定のキャラへの好感度を更新するメソッド */
	public void updateLikability(int p,Integer num,boolean meal){
		if(Meal==meal)return;
		int score=Likability.get(num)+p;
		if(score>Paramaters.THRESHOLD_MAX)score=Paramaters.THRESHOLD_MAX;
		Likability.put(num,score);
	}
	
	/** 特定のキャラへの好感度を取得するメソッド */
	public int getLikability(Integer num){
		if(num == null)return 0;
		return Likability.get(num);
	}
	
	/** 顔差分を引数のものに変更する */
	public void setFaceIndex(int num){
		FaceNumber=num;
	}
	
	/** 引数の値に従って、顔画像の差分を変更するメソッド */
	public void changeFaceIndex(int num){
		if(num>=Paramaters.THRESHOLD_LOVE)FaceNumber=Paramaters.LOVE;
		else if(num>=Paramaters.THRESHOLD_LIKE)FaceNumber=Paramaters.LIKE;
		else if(num>=0)FaceNumber=Paramaters.Normal;
		else FaceNumber=Paramaters.Bad;
	}
	
	/** 引数の番号のキャラへの好感度に従って、顔画像の差分を変更するメソッド */
	public void changeFaceIndex(int num,boolean meal){
		if(Meal==meal){
			FaceNumber=Paramaters.Normal;
		}
		else{
			int score=Likability.get(num);
			changeFaceIndex(score);
		}
	}
	
	/** 顔画像の差分を初期状態に戻すメソッド */
	public void formatFaceIndex(){
		FaceNumber=Paramaters.Normal;
	}
	
	
	/** このキャラが子供に与える限界職業LVを返す */
	public int getLimitJobLV(int num){
		double rate=getLikabilityRate(num);
		if(rate>1.0)rate=1.0;
		return (int)Math.round(rate * status[10]);  //rate*getAllJobLV()
	}
	
	/** このキャラが子供に与える適正ポイントを返す */
	private int getProperPoint(int num){
		int basic=2*ProperPoint+(BasicProperPoint-ProperPoint);
		double rate=getLikabilityRate(num);
		return (int)Math.round(rate*basic);
	}
	
	/** 好感度から得られる倍率の計算を行うメソッド */
	private double getLikabilityRate(int num){
		int like=getLikability(num);
		int min=Paramaters.THRESHOLD_LIKE;
		return 0.7 + 0.5*(like-min)/min;
	}
	
	/** 親の職業レベルに従って、このキャラの職業レベルを決定する */
	public void setJobLV(int[] joblvs){
		for(int i=0;i<joblvs.length;i++){
			int lv=joblvs[i];
			if(lv<=0)continue;
			int j;
			for(j=0;j<JobList.size();j++){
				Job job=JobList.get(j);
				if(i==job.Number){
					job.levelup(lv);
					break;
				}
			}
			if(j==JobList.size()){
				Job job=Job.getJob(i);
				job.levelup(lv);
				JobList.add(job);
			}
		}
	}
	
	/** このキャラクターのセーブデータを返すメソッド */
	public String[] getDataArray(){
		//順番は、基本データ、職業リスト、得意マップ、好感度
		String[] datas={getBasicData(),getJobData(),MapchipsString(),getLikability_String()};
		return datas;
	}
	
	/** このキャラクターの基本データを文字列として返すメソッド */
	private String getBasicData(){
		//順番はName,PictureName,status,Propers,BasicProperPoint,ProperPoint,Experiment,MaxE,Level,AllyNumber,Meal
		String str="";
		str+=Name;
		str+=","+PictureName;
		for(int i=0;i<status.length;i++)str+=","+status[i];
		for(int i=0;i<Propers.length;i++)str+=","+Propers[i];
		str+=","+BasicProperPoint;
		str+=","+ProperPoint;
		str+=","+Experiment;
		str+=","+MaxE;
		str+=","+Level;
		str+=","+AllyNumber;
		int m;
		if(Meal)m=1;
		else m=0;
		str+=","+m;
		return str;
	}
	
	/** 引数の文字列から、このキャラクターの基本データをロードするメソッド */
	private void setBasicData(String str){
		String data[]=str.split(",");
		Name=data[0];
		PictureName=data[1];
		int start=2;
		for(int i=start;i<status.length+start;i++)status[i-start]=Integer.parseInt(data[i]);
		for(int i=status.length+start;i<Propers.length+status.length+start;i++)
			Propers[i-(status.length+start)]=Integer.parseInt(data[i]);
		int index=Propers.length+status.length+start;
		BasicProperPoint = Integer.parseInt(data[index]);
		ProperPoint = Integer.parseInt(data[index+1]);
		Experiment = Integer.parseInt(data[index+2]);
		MaxE = Integer.parseInt(data[index+3]);
		Level = Integer.parseInt(data[index+4]);
		AllyNumber = Integer.parseInt(data[index+5]);
		int meal=Integer.parseInt(data[index+6]);
		Meal=(meal==1);
	}
	
	/** このキャラクターの職業履歴を文字列として返すメソッド */
	private String getJobData(){
		String str="";
		for(int i=0;i<JobList.size();i++){
			Job job=JobList.get(i);
			if(i!=0)str+=",";
			str+=job.getJobName();
			str+=","+job.getLevel();
			str+=","+job.getExperience();
		}
		return str;
	}
	
	/** 引数の文字列から、このキャラクターのJobListにJobオブジェクトを格納していく */
	private void setJobData(String str){
		JobList.clear();
		if(str.equals(""))return;
		String[] JobArray = str.split(",");
		for(int i=0;i<JobArray.length;i+=3){
			Job job=Job.getJob(JobArray[i]);
			job.setLevel(Integer.parseInt(JobArray[i+1]));
			job.setExperience(Double.parseDouble(JobArray[i+2]));
			JobList.add(job);
		}
	}
	
	/** このキャラクターの好感度を文字列として返すメソッド */
	private String getLikability_String(){
		String str="";
		for(int num : Likability.keySet()){
			str+=num+",";
			str+=Likability.get(num)+",";
		}
		if(str.length()==0) return str;
		else return str.substring(0,str.length()-1);
	}
	
	/** 引数のデータから、このキャラクターの好感度ハッシュマップLikabilityに値を与えていくメソッド */
	private void setLikability(String str){
		Likability.clear();
		if(str.equals(""))return;
		String[] data = str.split(",");
		for(int i=0;i<data.length;i+=2){
			int key = Integer.parseInt(data[i]);
			int value = Integer.parseInt(data[i+1]);
			Likability.put(key, value);
		}
	}
	
	/** JobListのサイズを返すメソッド */
	public int getJobListSize(){
		return JobList.size();
	}
	
}