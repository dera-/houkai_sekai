package sim.game;

import java.awt.Container;
import java.awt.Graphics;
import java.util.ArrayList;

import sim.BaseApplication;
import sim.game.data.CharaData;
import sim.game.data.EventData;

/** ゲーム本体のクラス */
public class Game implements BaseApplication{
	private ArrayList<GlobalScene> GlobalSceneList = new ArrayList<GlobalScene>();  //各画面(シーン)を格納するためのリスト。１番目のシーンが現在のシーンとなる。スタックのように利用する。
	
	public Game(){
		setWaitScene();
	}
	
	/** 決定ボタンを押した時の挙動 
	 * 連続押し防止のためにsynchronized修飾子をつけた。
	 * 返り値は、シーンが切り替わったかどうか
	 **/
	public synchronized boolean decide(){
		if(GlobalSceneList.size()==0)return false;
		int num=GlobalSceneList.get(0).pushEnter();  //エンターキーを押した時に起こるイベントの番号
		if(num==Paramaters.NON_EVENT)return false;  //どのシーンにも遷移しない時
		else if(num==Paramaters.STRATEGY_SCENE){ //戦闘準備画面に遷移する
			uponStrategyScene();
		}
		else if(num==Paramaters.BATTLE_SCENE){  //戦闘MAPに遷移する
			GlobalSceneList.add(0,EventData.createBattleField());
		}
		else if(num==Paramaters.SELECT_STAGE){ //戦闘MAPの選択画面に遷移する
			GlobalScene stage=new StageSelect();
			GlobalSceneList.add(0,stage);
		}
		else if(num==Paramaters.DELETE_NOW_SCENE){ //現在のシーンを終了して、GlobalSceneListの２番目に格納されているシーンに移る。
			GlobalSceneList.remove(0);
		}
		else if(num==Paramaters.GAMEOVER_SCENE){  //ゲームオーバー
			GlobalScene gameover=new GameOver(2000);
			GlobalSceneList.add(0,gameover);
		}
		else if(num==Paramaters.START_SCENE){  //最初の画面に遷移する
			createStartScene();
		}
		else if(num==Paramaters.PARAMATER_SCENE){  //パラメータ-の閲覧画面に遷移する
			GlobalScene pramS = new ParamaterScene(Paramaters.AllyList,"味方キャラの能力確認");
			GlobalSceneList.add(0,pramS);
		}
		else if(num == Paramaters.JOBCHANGE_SCENE1){  //職業の変更画面(職業を変えるキャラを選択する画面)に遷移する
			GlobalScene charalist = new CharaListForJob();
			GlobalSceneList.add(0,charalist);
		}
		else if(num == Paramaters.JOBCHANGE_SCENE2){  //１キャラの職業の変更画面に遷移する
			CharaData Chara=null;
			GlobalScene scene=GlobalSceneList.get(0);
			if(scene instanceof CharaListForJob){
				CharaListForJob cl=(CharaListForJob)scene;
				Chara=cl.getChara();
			}
			if(Chara==null)return false;
			GlobalScene changejob = new DecideJob(Chara);
			GlobalSceneList.add(0,changejob);
		}
		else if(num == Paramaters.FIRST_REGISTER_SCENE_ALL){ //タイトル画面で「はじめから」を選択した直後のシーン(キャラ登録シーン)に遷移する
			GlobalScene first=new FirstScene();
			GlobalSceneList.add(0,first);
		}
		else if(num == Paramaters.FIRST_REGISTER_SCENE_ONE){ //１体のキャラの作成画面に遷移する
			CharaData Chara=null;
			GlobalScene scene=GlobalSceneList.get(0);
			//初期画面から呼び出された場合
			if(scene instanceof FirstScene){
				FirstScene first = (FirstScene)scene;
				Chara=first.getCharaData();
			}
			GlobalScene create = new CharaCreate(Chara);
			GlobalSceneList.add(0,create);
		}
		else if(num == Paramaters.FIRST_REGISTER_FINISH){ //キャラ登録シーンを終了して、戦闘準備シーンに遷移する
			GlobalScene scene=GlobalSceneList.get(0);
			FirstScene first = (FirstScene)scene;
			Paramaters.addAlly(first.getNewCharaList());
			GlobalSceneList.clear();
			GlobalScene stra=new Strategy();
			GlobalSceneList.add(0,stra);
		}
		else if(num == Paramaters.DECIDE_CHARACTER){  //１体のキャラの作成画面からキャラ登録シーンに戻る
			whenCreateChara();
		}
		else if(num == Paramaters.MATING_SCENE){ //継承システムの画面に移る
			GlobalScene match = new MatchScene();
			GlobalSceneList.add(0,match);
		}
		else if(num == Paramaters.CHILD_REGISTER_SCENE){  //子供のキャラクリエイト画面に移る
			GlobalScene scene=GlobalSceneList.get(0);
			MatchScene match =(MatchScene)scene;
			CharaData parents[]=match.getParents();  //両親のデータの取得
			GlobalScene child = new ChildCreate(parents);
			GlobalSceneList.add(0,child);
		}
		else if(num == Paramaters.DECIDE_CHILD){  //子供のキャラクリエイトを終了して戦闘準備画面に戻る
			GlobalScene scene=GlobalSceneList.get(0);
			ChildCreate child=(ChildCreate)scene;
			Paramaters.addAlly(child.getChild());
			uponStrategyScene();
		}
		else if(num == Paramaters.SAVE_EVENT){ //セーブシーンに移る
			GlobalScene save = new SaveScene();
			GlobalSceneList.add(0,save);
		}
		else if(num == Paramaters.LOAD_SCENE){  //ロードシーンに移る
			GlobalScene load = new LoadScene();
			GlobalSceneList.add(0,load);
		}
		else if(num==Paramaters.FRIEND_SCENE){  //新規加入キャラのお知らせのシーンに遷移する
			EventData.formatNowEvent();
			GlobalScene pramF = new ParamaterScene(EventData.Friends,"新規加入キャラ");
			for(CharaData C : EventData.Friends) C.typeToFriend();  //AllyListに追加する前に、キャラタイプを味方キャラに変更
			Paramaters.addAlly(EventData.Friends);
			GlobalSceneList.add(0,pramF);
		}
		else if(num == Paramaters.SENTENCE_SCENE){  //シナリオシーンに移る
		    GlobalScene sentence = new SentenceScene(EventData.getSentenceNum(), Paramaters.STRATEGY_SCENE, null);
		    EventData.formatNowEvent();
		    GlobalSceneList.add(0,sentence);
		}
		else if(num == Paramaters.FRIEND_SENTENCE_SCENE){  //新規加入キャラのお知らせとシナリオシーンに移る
			GlobalScene pramF = new ParamaterScene(EventData.Friends,"新規加入キャラ",Paramaters.SENTENCE_SCENE);
			for(CharaData C : EventData.Friends) C.typeToFriend();  //AllyListに追加する前に、キャラタイプを味方キャラに変更
			Paramaters.addAlly(EventData.Friends);
			GlobalSceneList.add(0,pramF);
		}
		else if(num == Paramaters.CHECK_SAVE){
		    GlobalScene checkSave = new CheckScene(Paramaters.EXECUTE_SAVE,"セーブデータを上書きしますか？");
	        GlobalSceneList.add(0,checkSave);
		}
	    else if(num == Paramaters.CHECK_BATTLE){
	        GlobalScene checkBattle = new CheckScene(Paramaters.BATTLE_SCENE,"戦闘MAPに移動します。よろしいですか？");
	        GlobalSceneList.add(0,checkBattle);
	    }
	    else if(num == Paramaters.CHECK_MOVE_TITLE){
	        GlobalScene checkTitle = new CheckScene(Paramaters.START_SCENE,"タイトルに戻ります。よろしいですか？");
	        GlobalSceneList.add(0,checkTitle);
	    }
	    else if(num == Paramaters.EXECUTE_SAVE){
	        SaveScene scene = getSaveScene();
	        if(scene == null) return true;
	        scene.save();
	        GlobalSceneList.remove(0);
	    }
	    else if(num == Paramaters.OPENNING_SCENE){
	        GlobalScene openning = new SentenceScene(Paramaters.SENTENCE_OPENNING, Paramaters.FIRST_REGISTER_SCENE_ALL, Paramaters.BGM_START);
	        GlobalSceneList.add(0,openning);
	    }
	    else if(num == Paramaters.SAVE_AND_START_SCENE){
	        GlobalSceneList.clear();
	        GlobalScene save = new SaveScene();
	        GlobalSceneList.add(save);
	        GlobalScene start=new Start(0.4,0.6,0.3,0.2,0);
	        GlobalSceneList.add(start);
	    } 
		return true;
	}
	
	private SaveScene getSaveScene(){
        while(GlobalSceneList.size() > 0){
            GlobalScene scene = GlobalSceneList.get(0);
            if(scene instanceof SaveScene) return (SaveScene)scene;
            else GlobalSceneList.remove(scene);
        }
        uponStrategyScene();
        return null;
	}
	
	private StageSelect getStageSelect(){
	    for(GlobalScene scene : GlobalSceneList){
	        if(scene instanceof StageSelect) return (StageSelect)scene;
	        else GlobalSceneList.remove(scene);
	    }
	    uponStrategyScene();
	    return null;
	}
	
	/** タイトル画面に移るメソッド */
	public void createStartScene(){
		GlobalSceneList.clear();
		Paramaters.whenStart();
		GlobalScene start=new Start(0.4,0.6,0.3,0.2,0);
		GlobalSceneList.add(0,start);
	}
	
	/** データのロード中の画面を表示する */
	public void setWaitScene(){
	    GlobalScene wait=new WaitScene();
	    GlobalSceneList.add(0,wait);
	}
	
	/** 戦闘準備画面に移るメソッド */
	public void uponStrategyScene(){
		int index;
		for(index=0;index<GlobalSceneList.size();index++){
			GlobalScene scene=GlobalSceneList.get(index);
			if(scene instanceof Strategy)break;
		}
		if(index==GlobalSceneList.size()){
			GlobalScene stra=new Strategy();
			GlobalSceneList.add(0,stra);
		}
		else{
			for(int i=0;i<index;i++)GlobalSceneList.remove(0);
		}
	}
	
	/** キャラクタを作成した時に呼び出されるメソッド。 */
	private void whenCreateChara(){
		GlobalScene scene1=GlobalSceneList.get(0);
		GlobalScene scene2=GlobalSceneList.get(1);
		CharaCreate create=(CharaCreate)scene1;
		FirstScene first=(FirstScene)scene2;
		first.setCharacter(create.getNowChara());
		GlobalSceneList.remove(0);
	}
	
	/** キャンセルボタンを押した時の挙動
	 * 連続押し防止のためにsynchronized修飾子をつけた。
	 * 返り値は、シーンが切り替わったかどうか
	 **/
	public synchronized boolean cancel(){
		if(GlobalSceneList.size()==0)return false;
		int num=GlobalSceneList.get(0).pushEscape();
		if(num==Paramaters.NON_EVENT)return false;
		else if(num==Paramaters.STRATEGY_SCENE){
			uponStrategyScene();
		}
		else if(num==Paramaters.DELETE_NOW_SCENE){
			GlobalSceneList.remove(0);
		}
		else if(num == Paramaters.DECIDE_CHARACTER){
			whenCreateChara();
		}
		else if(num == Paramaters.SENTENCE_SCENE){  //シナリオシーンに移る
		    GlobalScene sentence = new SentenceScene(EventData.getSentenceNum(), Paramaters.STRATEGY_SCENE, null);
		    EventData.formatNowEvent();
		    GlobalSceneList.add(0,sentence);
		}
		return true;
	}
	
	/** 方向キーを押した時の挙動 */
	public synchronized boolean direction(int k){
		if(GlobalSceneList.size()==0)return false;
		int num=Paramaters.NON_EVENT;
		GlobalScene scene=GlobalSceneList.get(0);
		switch(k){
			case 0: num=scene.pushUp(); //↑キーが押された時
					break;
			case 1: num=scene.pushDown(); //↓キーが押された時
					break;
			case 2: num=scene.pushLeft();  //←キーが押された時
					break;
			case 3: num=scene.pushRight();  //→キーが押された時
					break;
			default: break;
		}
		if(num==Paramaters.NON_EVENT)return false;
		else return true;
	}
	
	public synchronized void moveCursor(int x, int y){
	    if(GlobalSceneList.size()==0)return;
	    GlobalScene scene=GlobalSceneList.get(0);
	    scene.moveCursor(x, y);
	}
	
	public synchronized void mouseDragged(int x, int y){
	    if(GlobalSceneList.size()==0 || !(GlobalSceneList.get(0) instanceof GlobalSceneWithMouseDragged))return;
	    GlobalSceneWithMouseDragged scene=(GlobalSceneWithMouseDragged) GlobalSceneList.get(0);
	    scene.mouseDragged(x, y);		
	}
	
	public synchronized void mousePressed(int x, int y){
	    if(GlobalSceneList.size()==0 || !(GlobalSceneList.get(0) instanceof GlobalSceneWithMouseDragged))return;
	    GlobalSceneWithMouseDragged scene=(GlobalSceneWithMouseDragged) GlobalSceneList.get(0);
	    scene.mousePressed(x, y);		
	}
	
	public synchronized void mouseReleased(int x, int y){
	    if(GlobalSceneList.size()==0 || !(GlobalSceneList.get(0) instanceof GlobalSceneWithMouseDragged))return;
	    GlobalSceneWithMouseDragged scene=(GlobalSceneWithMouseDragged) GlobalSceneList.get(0);
	    scene.mouseReleased(x, y);		
	}
	
	/** ゲーム画面の描画 */
	public void draw(Graphics g,Container con){
		if(GlobalSceneList.size()==0)return;
		GlobalSceneList.get(0).drawing(g,con);
	}
	
	/** 現在、GlobalSceneListの一番先頭のGlobalSceneオブジェクト(現在のシーン)を外部に渡すメソッド */
	public GlobalScene getNowGlobalScene(){
		if(GlobalSceneList.size()==0)return null;
		return GlobalSceneList.get(0);
	}
	
	/** 別スレッドで行う処理 */
	public void running(){
		if(GlobalSceneList.size()==0)return;
		GlobalSceneList.get(0).running();
	}
}
