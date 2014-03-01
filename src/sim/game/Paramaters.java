package sim.game;

import java.awt.Container;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import sim.game.brain.BrainParameter;
import sim.game.data.CharaData;
import sim.game.data.EventData;
import sim.game.data.Gene;
import sim.game.data.Job;
import sim.game.data.Map;
import sim.game.data.MapChip;
import sim.game.data.Skill;
import sim.io.ReadFile;
import sim.tools.ImageSet;
import sim.tools.MTRandom;
import sim.tools.MusicSet;

public class Paramaters {
	//パネルの横と縦の長さ（外部アクセス可能）
	public static final int Width=800; 
	public static final int Height=600;
	
	//メイドシステムの横と縦の長さ
	public static final int Width_MaidSystem=900;
	public static final int Height_MaidSystem=200;
	
	//フレームの枠の大きさ
	public static final int defaultFrameSize = 0;
	public static int frameTop = defaultFrameSize;
	public static int frameLeft = defaultFrameSize;
	
	public static ImageSet Image_BOX;
	
	public static MusicSet Music_Box;
	
	public static final int PlayerNumber=0;
	
	public static final int AINumber=1;
	
	public static String FacePicture="Face";
	
	public static String DotPicture="Dot";
	
	/** 以下、イベントナンバー */
	
	public static final int NON_EVENT=-2;  //何もイベントが無い
	
	public static final int DELETE_NOW_SCENE=-1;  ////現在のシーンの終了
	
	public static final int START_SCENE=0;  //タイトル画面に遷移する
	
	public static final int STRATEGY_SCENE=1;  //戦闘準備画面に遷移する
	
	public static final int BATTLE_SCENE=2;  //戦闘MAPに遷移する
	
	public static final int LOAD_SCENE=3;   //ロード画面に遷移する
	
	public static final int SAVE_EVENT=4;   //セーブ画面に遷移する
	
	public static final int SELECT_STAGE=5;  //戦闘準備画面に遷移する
	
	public static final int MATING_SCENE=6;   //継承システムのシーンに遷移する
	
	public static final int PARAMATER_SCENE=7;  //キャラのパラメータ-の閲覧画面に遷移する
	
	public static final int GAMEOVER_SCENE=8;  //ゲームオーバー画面に遷移
	
	public static final int FRIEND_SCENE=9;  //新規加入キャラのお知らせのシーンに遷移
	
	public static final int JOBCHANGE_SCENE1=10; //職業の変更画面(職業を変えるキャラを選択する画面)に遷移する
	
	public static final int JOBCHANGE_SCENE2=11;   //１キャラの職業の変更画面に遷移する
	
	public static final int FIRST_REGISTER_SCENE_ALL=12;  //タイトル画面で「はじめから」を選択した直後のシーン(キャラ登録シーン)に遷移する
	
	public static final int FIRST_REGISTER_SCENE_ONE=13;  //１体のキャラの作成画面に遷移する
	
	public static final int FIRST_REGISTER_FINISH=14;  //キャラ登録シーンを終了して、戦闘準備シーンに遷移する
	
	public static final int DECIDE_CHARACTER=15;  //１体のキャラの作成画面からキャラ登録シーンに戻る
	
	public static final int DECIDE_YES=16;  //２択のうちYESを選択した時
	
	public static final int DECIDE_NO=17;  //２択のうちNOを選択した時
	
	public static final int CHILD_REGISTER_SCENE=18;   //子供のキャラクリエイト画面に移る
	
	public static final int DECIDE_CHILD=19;  //子供のキャラクリエイトを終了して戦闘準備画面に戻る
	
	public static final int GAME_CLEAR=20;   //エンディングに遷移
	
	public static final int CHECK_SAVE = 21; //セーブするかどうかの確認
	
	public static final int CHECK_BATTLE = 22; //戦闘MAPに移動するかどうかの確認
	
	public static final int EXECUTE_SAVE = 23; //セーブを行う
	
	public static final int CHECK_MOVE_TITLE =24; //タイトル画面に移動するかどうかの確認
	
	public static final int OPENNING_SCENE = 25; //オープニング
	
	public static final int SAVE_AND_START_SCENE = 26; //セーブしてタイトル画面に飛ぶ
	
	public static final int SENTENCE_SCENE = 27;
	
	public static final int FRIEND_SENTENCE_SCENE = 28;
	
	public static final int EDITOR_DECIDE_MAP = 501;
	
	public static final int EDITOR_NOT_DECIDE_MAP = 502;
	
	public static final int EDITOR_CREATE_ENEMY_LIST = 503;
	
	public static final int EDITOR_STAGE_SAVE = 504;
	
	public static final int EDITOR_SELECT_ENEMY = 505;
	
	public static final int EDITOR_SET_ENEMY_PLACE = 506;
	
	public static final int EDITOR_SET_ALLY_PLACE = 507;
	
	public static final int EDITOR_MOVE_ENEMY_PLACE = 508;
	
	public static final int EDITOR_DELETE_ENEMY_PLACE = 509;
	
	public static final int EDITOR_MOVE_ALLY_PLACE = 510;
	
	public static final int EDITOR_DELETE_ALLY_PLACE = 511;
	
	public static final int EDITOR_CHARA_DECIDE_MENU = 512;
	
	public static final int EDITOR_SELECT_CHARA_LEVEL = 513;
	
	public static final int EDITOR_SELECT_CHARA_NUMS = 514;
	
	public static final int EDITOR_CREATED_ENEMY = 515;
	
	public static final int EDITOR_SET_LEVEL = 516;
	
	public static final int EDITOR_SET_CHARA_NUMS = 517;
	
	public static final int EDITOR_STAGE_LOAD = 518;
	
	/** 以下、BGMのファイル名 */
	public static final String BGM_OPENNING = "openning2.mp3";
	
	public static final String BGM_STRATEGY = "strategy.mp3";
	
	public static final String BGM_Normal = "normal.mp3";
	
	public static final String BGM_LOVERS = "lovers.mp3";
	
	public static final String BGM_GAMEOVER = "gameover.mp3";
	
	public static final String BGM_WIN = "win.mp3";
	
	public static final String BGM_START = "start.mp3";
	
	public static final String BGM_ENDING = "gisiki.mp3";
	
	/**  以下、sentenceナンバー */

	public static final int SENTENCE_OPENNING = 0;
	
	public static final int SENTENCE_ENDING = 1;
	
	/** 以下、マスチップナンバー */
	public static final int MOVABLE_CHIP=0;  //移動可能範囲内のマップチップ
	
	public static final int ACTIVE_CHIP=1;  //スキルの射程範囲内のマップチップ
	
	public static final int NONEVENT_CHIP=2;  //範囲外のマップチップ
	
	public static final int USABLE_CHIP=3;  //キャラを配置可能なマップチップ
	
	/** 以下、メッセージの種類*/
	public static final int TURN_MASSAGE=0;   //自軍あるいは敵軍のターン開始時のメッセージ
	
	public static final int VICTORY_MASSAGE=1;  //戦闘MAPで勝利した時のメッセージ
	
	/** 以下、２択の種類 */
	public static final int TWO_BATTLE=0;  //戦闘MAPにおいてキャラの配置を終了していいかどうかの確認
	
	public static final int TWO_RETRY=1;  //戦闘MAPで敗北した後に出てくるリトライするかどうかの確認
	
	public static final int IS_RETURN=2;  //メニュー選択画面に戻るかどうかの確認
	
	public static final int IS_FINISH_TURN=3;  //ターンを終了するかどうかの確認
	
	/** 以下、AIに関するパラメーター */
	public static double Hit_Probability = 0.5;  //この値を超えた時、必ず当たると認識する。 
	
	public static double Hit_Probability_By_Player = 0.7;  //この値を超えた時、プレイヤーキャラからの攻撃が必ず当たると認識する。 
	
	public static double Error_Attack=1.0;  //相手の攻撃力に関する誤差率
	
	public static double Error_Hit=1.0;  //相手の命中率に関する誤差率
	
	public static double alpha1=0.01; //命中確率を更新する時のみ使用するパラメーター
	
	private static final double alpha2_Attack=0.01; //相手の攻撃力に関する誤差率を更新する時のみ使用するパラメーター
	
	private static final double alpha2_Hit=0.01; //相手の命中率に関する誤差率を更新する時のみ使用するパラメーター
	
	/** 以下、勝利/敗北条件に関するパラメーター */
	public static final int ABOUT_PLACE=1; //勝敗条件が場所に関するものの時
	
	public static final int ABOUT_CHARACTER=2; //勝敗条件が特定のキャラクターの生存に関する時
	
	public static final int ABOUT_ALL=3;  //勝敗条件が全滅の時
	
	/** 以下、能力参照に関するパラメーター */
	public static final int BasicStatus=0;  //基本ステータス
	public static final int JobStatus=1;    //職業に関するステータス
	public static final int SkillStatus=2;  //取得スキル
	public static final int FirstNumber=BasicStatus;
	public static final int LastNumber=SkillStatus;
	
	/** 以下、キャラタイプの番号 */
	public static final int Oniginal_Chara = -1;  //固有キャラ
	public static final int Friend_Chara=0;  //味方キャラ
	public static final int Mob_Chara = 1;  //モブキャラ
	
	
	/** 以下、好感度に関するパラメーター */
	public static final int NearPoint = 3;  //隣あっている事で得られるポイント
	public static final int BeatPoint = 7;  //敵を倒す事で得られるポイント
	public static final int RecoverPoint = 5;  //回復を行う事で得られるポイント
	public static final int LosePoint=0;  //敗北することで得られるポイント
	
	public static final int THRESHOLD_LIKE=70; //魂合可能最低値
	public static final int THRESHOLD_LOVE=THRESHOLD_LIKE*2; //魂合推奨値
	public static final int THRESHOLD_MAX=THRESHOLD_LIKE*3; //最高値
	
	public static final int Bad=1;  //好感度低い時の添え字
	public static final int Normal=0;  //好感度普通の時の添え字
	public static final int LIKE=6;  //好感度が高いときの添え字
	public static final int LOVE=4;  //好感度が非常に高いときの添え字
	
	/** 味方キャラクターのリスト */
	public static ArrayList<CharaData> AllyList = new ArrayList<CharaData>();
	
	private static int NUMBER_ALLY=0; //今までに登録した味方キャラの総数
	
	/** 以下、キャラの名前リスト */
	//個々のリストに要素を入れてあげる
	private static ArrayList<String> ManNameList1 = new ArrayList<String>();   //男キャラの名前リストその１
	private static ArrayList<String> ManNameList2 = new ArrayList<String>();   //男キャラの名前リストその２
	private static ArrayList<String> WomanNameList1 = new ArrayList<String>();   //女キャラの名前リストその１
	private static ArrayList<String> WomanNameList2 = new ArrayList<String>();   //女キャラの名前リストその２
	
	/** 乱数生成オブジェクト */
	private static MTRandom RandomCreater = new MTRandom(System.currentTimeMillis());
	
	/** 以下、セーブとロードの時に使用する フィールド*/
	public static String FileFoldr=".";
	public static String FileName_Head = "Save";
	public static String FileName_Tail = ".odf";
	public static final int FileNumbers=30;  //セーブ場所数
	
	
	/** 各パラメーターの初期化メソッド */
	public static void format(Container con){
		Image_BOX=new ImageSet();
		Image_BOX.addImage("ImageFile.csv", con);
		Image_BOX.addBafferedImage("BafferedImageFile.csv", con);
		Music_Box=new MusicSet();
		ReadFile read = new ReadFile();
		read.createMapChipLIST();
		read.createMapList();
		read.createSkillList();
		read.createJobList();
		read.createPrameterList();
		read.createCharaList();
		read.createEventList();
		read.createGeneList();
		read.createAllCharaNameLists();
		read.createSentenceList();
	}
	
	/** ゲームの最初の画面で呼び出すメソッド */
	public static void whenStart(){
		AllyList.clear();
		NUMBER_ALLY=0;	
		EventData.format();
	}
	
	/** ManNameList1を外部に渡すメソッド */
	public static ArrayList<String> getManNameList1(){
		return ManNameList1;
	}

	/** ManNameList2を外部に渡すメソッド */
	public static ArrayList<String> getManNameList2(){
		return ManNameList2;
	}
	
	/** WomanNameList1を外部に渡すメソッド */
	public static ArrayList<String> getWomanNameList1(){
		return WomanNameList1;
	}
	
	/** WomanNameList2を外部に渡すメソッド */
	public static ArrayList<String> getWomanNameList2(){
		return WomanNameList2;
	}
	
	/** フィールドHit_Probabilityの更新を行うメソッド */
	/*
	public static void updateHitProbability(double r){
		Hit_Probability+=Hit_Probability+alpha1*(r-Hit_Probability);
		if(Hit_Probability>1)Hit_Probability=1;
		else if(Hit_Probability<0)Hit_Probability=0;
	}
	*/
	
	/** フィールドError_Attackの更新を行うメソッド */
	public static void updateErrorAttack(double e,double r){
		Error_Attack+=alpha2_Attack*(r-e)/e;
		if(Error_Attack<0)Error_Attack=0;
	}
	
	/** フィールドError_Hitの更新を行うメソッド */
	public static void updateErrorHit(double e,double r){
		Error_Hit+=alpha2_Hit*(r-e)/e;
		if(Error_Hit<0)Error_Hit=0;
	}
	
	/** 味方キャラを全回復させるメソッド */
	public static void recoverAllys(){
		for(CharaData C : AllyList){
			C.recoverAll();
		}
	}
	
	/** AllyListにキャラクターを追加するメソッド */
	public static void addAlly(CharaData C){
		if(isIndetify(C))return;    //同一のキャラがAllyList内に存在する場合は追加処理を行わない
		C.setAllyNumber(NUMBER_ALLY);
		for(CharaData chara : AllyList){
			chara.addLikability(C.getAllyNumber(), C.isMeal());  //各異性キャラの好感度ハッシュにこのキャラの番号を追加しておく
			C.addLikability(chara.getAllyNumber(), chara.isMeal());   //このキャラの好感度ハッシュに各異性キャラの番号を追加しておく
		}
		NUMBER_ALLY++;
		AllyList.add(C);
	}
	
	/** AllyListにキャラクターのリストを追加するメソッド */
	public static void addAlly(ArrayList<CharaData> list){
		for(CharaData C : list){
			addAlly(C);
		}	
	}
	
	/** AllyListから引数のキャラを除外するメソッド */
	public static void removeAlly(CharaData C){
		for(CharaData elem : AllyList){
			if(C.isMeal()==elem.isMeal())continue;
			elem.removeLikability(C.getAllyNumber());  //引数のキャラに対する好感度を削除する
		}
		AllyList.remove(C);
	}
	
	
	/** NUMBER_ALLYを返り値として返すメソッド */
	public static int getAllyNumbers(){
		return NUMBER_ALLY;
	}
	
	/** NUMBER_ALLYに引数の値を代入するメソッド */
	public static void setAllyNumbers(int num){
		NUMBER_ALLY=num;
	}
	
	/**引数のAllyNumberを持つキャラを取得するメソッド*/
	public static CharaData getAllyChara(int num){
		for(CharaData C :AllyList){
			Integer I=C.getAllyNumber();
			if(I==null)continue;
			if(num==I.intValue())return C;
		}
		return null;
	}
	
	/** 引数のキャラと同一のキャラがAllyList内に存在する場合trueを返す */
	public static boolean isIndetify(CharaData C){
		for(CharaData elem :AllyList){
			if(C==elem)return true;
		}
		return false;
	}
	
	/** キャラの名前を決定するメソッド。引数はキャラの性別*/
	public static String getRandomName(boolean meal){
		ArrayList<String> strList1;
		ArrayList<String> strList2;
		final double threshold=0.5;
		String name=""; //返り値
		//男キャラ用の名前リスト
		if(meal){
			strList1=ManNameList1;
			strList2=ManNameList2;
		}
		//女キャラ用の名前リスト
		else{
			strList1=WomanNameList1;
			strList2=WomanNameList2;
		}
		
		//ここで名前を決定する。
		//味方キャラの名前の被りができるだけないようにするけど、50回連続で被った場合はその名前で決定する
		int num=0;
		while(num<50){
			//乱数の値に従って参照するリストを決定する
			double random = Paramaters.getRandom();
			ArrayList<String> strList;
			if(random<threshold)strList = strList1;
			else strList = strList2;
			//名前を次の乱数に従って決定する
			random = Paramaters.getRandom();
			name="";
			for(int i=0;i<strList.size();i++){
				if(random<1.0*(i+1)/strList.size()){
					name=strList.get(i);
					break;
				}
			}
			//決定した名前が他のキャラと被ってないかどうかのチェック
			int index;
			for(index=0;index<AllyList.size();index++){
				if(name.equals(AllyList.get(index).getCharaName()))break;
			}
			if(index==AllyList.size())break;
			num++;
		}
		return name;
	}
	
	/** 乱数を返すメソッド */
	public static double getRandom(){
		return RandomCreater.nextDouble();
	}
	
}
