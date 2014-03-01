package sim.edit;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import sim.BaseApplication;
import sim.game.Paramaters;
import sim.game.data.CharaData;
import sim.game.data.Map;
import sim.tools.MapHandling;

public class EditorScreen implements BaseApplication{
	private ArrayList<SceneForEditor> scenelist = new ArrayList<SceneForEditor>();  //各画面(シーン)を格納するためのリスト。１番目のシーンが現在のシーンとなる。スタックのように利用する。
	private MapHandling mapHandling = null;
	private ArrayList<CharaData> allys = new ArrayList<CharaData>();
	private ArrayList<CharaData> setEnemies = new ArrayList<CharaData>(); //マップに配置された敵のリスト
	private ArrayList<CharaData> enemies = new ArrayList<CharaData>(); //敵候補リスト
	private CharaData nowSelectedChara = null; //現在選択されているキャラ
	private Point nowSelectedPlace = null;     //現在選択されている座標
	
	public void setSelectMapScene(){
		scenelist.add(0, new SelectMap(Map.getMapList()));
	}
	
	@Override
	public synchronized boolean decide() {
		if (scenelist.size() == 0) {
			CharaData chara = null;
			if ( (chara=searchCharaFromList(allys)) != null) {
				nowSelectedChara = chara;
				scenelist.add(0, new AllyPlaceMenu());
			} else if ((chara=searchCharaFromList(setEnemies)) != null) {
				nowSelectedChara = chara;
				scenelist.add(0, new EnemyMenu());
			} else{
				scenelist.add(0, new MainMenu());
			}
		} else {
			SceneForEditor scene = scenelist.get(0);
			int eventNum = scene.pushEnter();
			switch(eventNum){
			case Paramaters.EDITOR_DECIDE_MAP:
				SelectMap selectMap = (SelectMap)scene;
				int mapNum = selectMap.getSelectedMapNumber();
				mapHandling = new MapHandling(mapNum);
				scenelist.clear();
				break;
			case Paramaters.EDITOR_NOT_DECIDE_MAP:
				if(mapHandling != null){
					scenelist.clear();
				}
				break;
			case Paramaters.EDITOR_SELECT_ENEMY:
				scenelist.add(0, new EnemySelectScene(enemies));
				break;
			case Paramaters.EDITOR_MOVE_ALLY_PLACE:
				scenelist.add(0, new DecideSetPlace(mapHandling, Paramaters.EDITOR_SET_ALLY_PLACE));
				break;
			case Paramaters.EDITOR_SET_ALLY_PLACE:
				setAllyPlaceEvent();
				break;
			case Paramaters.EDITOR_SET_ENEMY_PLACE:
				setEnemyEvent();
				break;
			case Paramaters.EDITOR_MOVE_ENEMY_PLACE:
				scenelist.add(0, new DecideSetPlace(mapHandling, Paramaters.EDITOR_SET_ENEMY_PLACE));
				break;
			case Paramaters.EDITOR_SELECT_CHARA_LEVEL:
				scenelist.add(0, new DecideLevelScene());
				break;
			case Paramaters.EDITOR_SET_LEVEL:
				setLevelEvent();
				break;
			case Paramaters.EDITOR_DELETE_ENEMY_PLACE:
				setEnemies.remove(nowSelectedChara);
				enemies.add(nowSelectedChara);
				initializeField();
				break;
			case Paramaters.EDITOR_DELETE_ALLY_PLACE:
				allys.remove(nowSelectedChara);
				initializeField();
				break;
			case Paramaters.EDITOR_CREATE_ENEMY_LIST:
				scenelist.add(0, new CreateEnemyListScene(CharaData.getCharaList()));
				break;
			case Paramaters.EDITOR_CHARA_DECIDE_MENU:
				setNowSelectedChara();
				scenelist.add(0, new DecidedCharaMenu(nowSelectedChara));
				break;
			case Paramaters.EDITOR_SELECT_CHARA_NUMS:
				startCharaNumSelectEvent();
				break;
			case Paramaters.EDITOR_SET_CHARA_NUMS:
				setCharaNums();
				break;
			case Paramaters.EDITOR_CREATED_ENEMY:
				setCharaToEnemies();
				break;
			case Paramaters.DELETE_NOW_SCENE:
				scenelist.remove(0);
				break;
			case Paramaters.EDITOR_STAGE_SAVE:
				writeFile();
				break;
			case Paramaters.EDITOR_STAGE_LOAD:
				readFile();
				break;
			default: break;
			}
		}
		return false;
	}
	
	/**
	 * 味方の配置可能スペースを設置するイベント
	 */
	private void setAllyPlaceEvent(){
		CharaData chara;
		if(nowSelectedChara == null){
			chara = new CharaData();
			allys.add(chara);
		} else {
			chara = nowSelectedChara;
		}
		if(nowSelectedPlace == null){
			nowSelectedPlace = mapHandling.getCursor();
		}
		chara.setPlace(nowSelectedPlace.x, nowSelectedPlace.y);
		initializeField();
	}
	
	/**
	 * 敵キャラを設置するイベント
	 */
	private void setEnemyEvent(){
		if(nowSelectedChara == null){
			setNowSelectedChara();
		}
		if(!setEnemies.contains(nowSelectedChara)){
			enemies.remove(nowSelectedChara);
			setEnemies.add(nowSelectedChara);
		}
		if(nowSelectedPlace == null){
			nowSelectedPlace = mapHandling.getCursor();
		}
		nowSelectedChara.setPlace(nowSelectedPlace.x, nowSelectedPlace.y);
		initializeField();
	}
	
	/**
	 * nowSelectedCharaにレベルをセットするイベント
	 */
	private void setLevelEvent(){
		if(!(scenelist.get(0) instanceof DecideLevelScene)) return;
		DecideLevelScene lvScene = (DecideLevelScene) scenelist.get(0);
		nowSelectedChara.setLV(lvScene.getLevel(), false); 
		scenelist.remove(0);
	}
	
	/**
	 * nowSelectedCharaに値を与えるメソッド
	 */
	private void setNowSelectedChara(){
		if(scenelist.size() == 0) return;
		if(scenelist.get(0) instanceof HavingCharaData){
			HavingCharaData scene = (HavingCharaData)scenelist.get(0);
			nowSelectedChara = scene.getCharaData();
		}
	}
	
	private void startCharaNumSelectEvent(){
		int num;
		if(nowSelectedChara.isOriginal()){
			num = 1;
		} else {
			num = 30;
		}
		scenelist.add(0, new DecideCharaNumsScene(num));
	}
	
	private void setCharaNums(){
		if(scenelist.size() < 2) return;
		if((scenelist.get(0) instanceof DecideCharaNumsScene) && 
				(scenelist.get(1) instanceof DecidedCharaMenu)){
			DecideCharaNumsScene numsScene = (DecideCharaNumsScene) scenelist.get(0);
			DecidedCharaMenu charaScene = (DecidedCharaMenu)scenelist.get(1);
			charaScene.setCharaNums(numsScene.getCharaNum());
			scenelist.remove(0);
		}
	}
	
	private void setCharaToEnemies(){
		if(scenelist.size() == 0 || !(scenelist.get(0) instanceof DecidedCharaMenu)){
			return;
		}
		DecidedCharaMenu charaScene = (DecidedCharaMenu)scenelist.get(0);
		int nums = charaScene.getCharaNums();
		for(int i=0; i<nums ; i++){
			enemies.add(charaScene.getCharaData());
		}
		initializeField();
	}
	
	/**
	 * ファイルに書き込むメソッド
	 */
	private void writeFile(){
		StringBuilder builder = new StringBuilder(1000);
		int mapNum = mapHandling.getMap().Number;
		builder.append(mapNum);
		builder.append("\n");
		int allyNum = allys.size();
		builder.append(allyNum);
		for(CharaData chara : allys){
			Point place = chara.getPlace();
			builder.append(",");
			builder.append(place.x);
			builder.append(",");
			builder.append(place.y);
		}
		builder.append("\n");
		for(CharaData chara : setEnemies){
			String name = chara.getCharaName();
			int lv = chara.getLV();
			Point place = chara.getPlace();
			builder.append(name);
			builder.append(",");
			builder.append(lv);
			builder.append(",");
			builder.append(place.x);
			builder.append(",");
			builder.append(place.y);
			builder.append("\n");
		}		
		builder.append("END");
		FileWriter fileWriter;
		try{
			fileWriter = new FileWriter("map"+mapNum+"_"+System.currentTimeMillis()+".txt");
			fileWriter.write(builder.toString());
			fileWriter.close();
		}catch(Exception ex){
			
		}
		
	}
	
	/**
	 * ファイルから読み込むメソッド
	 */
	private void readFile(){
		String filePath = "load\\data.txt";
		BufferedReader reader;
		File file = new File(filePath);
		if(!file.exists())return;
		try{
			reader = new BufferedReader(new FileReader(filePath));
			mapHandling = new MapHandling(Integer.parseInt(reader.readLine()));
			String[] strs = reader.readLine().split(",");
			allys.clear();
			for(int i=1;i<strs.length;i+=2){
				CharaData chara = new CharaData();
				chara.setPlace(Integer.parseInt(strs[i]), Integer.parseInt(strs[i+1]));
				allys.add(chara);
			}
			setEnemies.clear();
			String data;
			while((data=reader.readLine())!=null){
				String[] elems = data.split(",");
				CharaData enemy = CharaData.searchChara(elems[0]);
				enemy.setLV(Integer.parseInt(elems[1]));
				enemy.setPlace(Integer.parseInt(elems[2]), Integer.parseInt(elems[3]));
				setEnemies.add(enemy);
			}
			reader.close();
		}catch(Exception e){}
		
	}
	
	
	
	/**
	 * 一時保存用に用いられているグローバル変数の初期化
	 */
	private void initializeField(){
		nowSelectedPlace = null;
		nowSelectedChara = null;
		scenelist.clear();
	}
	

	@Override
	public synchronized boolean cancel() {
		if(scenelist.size()==0){
			scenelist.add(0, new SubMenu());	
		} else {
			int num=scenelist.get(0).pushEscape();
			if(num == Paramaters.NON_EVENT)return false;
			else if(num == Paramaters.DELETE_NOW_SCENE){
				scenelist.remove(0);
				if(scenelist.size()==0){
					initializeField();
				}
			}
		}
		return false;
	}

	@Override
	public synchronized boolean direction(int k) {
		return false;
	}

	@Override
	public synchronized void moveCursor(int x, int y) {
		if(scenelist.size()>0){
			scenelist.get(0).moveCursor(x, y);
		}
		else if(mapHandling != null){
			mapHandling.moveCursorFromPixelValue(x, y);
		}
	}

	@Override
	public synchronized void mouseDragged(int x, int y) {
	    if(scenelist.size()>0 && scenelist.get(0) instanceof EditorSceneForDragged){
	    	EditorSceneForDragged scene = (EditorSceneForDragged) scenelist.get(0);
	    	scene.mouseDragged(x, y);
	    } else if(mapHandling != null){
	    	mapHandling.moveCenterFromPixelValue(x, y);
	    }
	}

	@Override
	public synchronized void mousePressed(int x, int y) {
	    if(scenelist.size()>0 && scenelist.get(0) instanceof EditorSceneForDragged){
	    	EditorSceneForDragged scene = (EditorSceneForDragged) scenelist.get(0);
	    	scene.mousePressed(x, y);
	    } else if(mapHandling != null){
	    	mapHandling.setMousePressedPlace(new Point(x,y));
	    }
	}

	@Override
	public synchronized void mouseReleased(int x, int y) {
	    if(scenelist.size()>0 && scenelist.get(0) instanceof EditorSceneForDragged){
	    	EditorSceneForDragged scene = (EditorSceneForDragged) scenelist.get(0);
	    	scene.mouseReleased(x, y);
	    } else if(mapHandling != null){
	    	mapHandling.setMousePressedPlace(null);
	    }
	}

	@Override
	public synchronized void draw(Graphics g, Container con) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
		if(mapHandling != null) mapHandling.drawMap(g, con, allys, setEnemies);
		CharaData chara=searchCharaFromList(setEnemies);
		if(chara!=null)chara.drawEasyParamater(g, con, 0, (int)Math.round(0.78*Paramaters.Height), Paramaters.Width, (int)Math.round(0.2*Paramaters.Height));
		if(scenelist.size()==0)return;
		scenelist.get(0).drawing(g,con);
	}

	@Override
	public void running() {}
	
	/**
	 * 引数のキャラデータリストの中から現在のカーソルの座標と一致するキャラデータを返すメソッド
	 * @param list キャラデータリスト
	 * @return
	 */
	private CharaData searchCharaFromList(ArrayList<CharaData> list){
		if(mapHandling == null) return null;
		Point cursor = mapHandling.getCursor();
		for(CharaData chara : list){
			Point place = chara.getPlace();
			if(cursor.x == place.x && cursor.y == place.y){
				return chara;
			}
		}
		return null;
	}
	
}
