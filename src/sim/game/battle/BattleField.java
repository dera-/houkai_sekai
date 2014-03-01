package sim.game.battle;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import sim.game.GlobalSceneWithMouseDragged;
import sim.game.Paramaters;
import sim.game.brain.BattleBrain;
import sim.game.data.CharaData;
import sim.game.data.EventData;
import sim.game.data.Map;
import sim.game.data.MapChip;
import sim.game.data.Skill;
import sim.tools.MapHandling;

public class BattleField implements GlobalSceneWithMouseDragged{
	private ArrayList<CharaData> Attackers=new ArrayList<CharaData>();
	private ArrayList<CharaData> Diffenders=new ArrayList<CharaData>();
	private Point[] UsablePlaces;  //キャラを配置できる場所
	private final int AttackerNumber;
	private final int DiffenderNumber;
	private String bgmFileName = null;
	private boolean Coin=true; //攻撃側のターンか防衛側のターンか
	private ArrayList<Scene> scene=new ArrayList<Scene>();
	private CharaData NowCharacter;  //プレイヤーが今操作しているキャラクター
	private BattleBrain EnemyBrain;  //CPUのAIオブジェクト
	private ArrayList<CharaData> CloneList = new ArrayList<CharaData>();  //全味方キャラの控えリスト
	private int BeforeNumbers;
	private MapHandling mapHandling;
	//private boolean EnemyAction; //敵の行動中かどうか
	
	public BattleField(int m,int a,int d,ArrayList<CharaData> attackers,ArrayList<CharaData> diffenders,Point[] pts,String bgm){
		Point firstAllyPlace = null;
		if (pts.length > 0) firstAllyPlace = pts[0];
		mapHandling = new MapHandling(m, firstAllyPlace);
		bgmFileName = bgm;
		AttackerNumber=a;
		DiffenderNumber=d;
		EnemyBrain = new BattleBrain(getList_Chara(true),getList_Chara(false),mapHandling.getMap());
		Attackers.clear();
		Diffenders.clear();
		Attackers.addAll(attackers);
		Diffenders.addAll(diffenders);
		UsablePlaces=pts;
		/** 配置可能なマップチップにフラグを立てる */
		setFlagToChip(true);
		/** クローンリストの作成 */
		createCloneList();
		/** キャラ配置フェイズを始める */
		whenStart();
	}
	
	/** 配置可能なマップチップにフラグを立てる(或いはフラグを折る)メソッド */
	private void setFlagToChip(boolean t){
		Map map = mapHandling.getMap();
		for(int i=0;i<UsablePlaces.length;i++){
			Point place=UsablePlaces[i];
			MapChip mc=map.getChip(place.x, place.y);
			mc.setColorFlag(Paramaters.USABLE_CHIP, t);
		}
	}
	
	/** クローンリストを作成するメソッド */
	private void createCloneList(){
		CloneList.clear();
		for(CharaData elem : Paramaters.AllyList){
			CloneList.add(CharaData.getClone(elem));
		}
	}
	
	/** キャラ配置フェイズ(或いはいきなり戦闘)を始めるメソッド */
	private void whenStart(){
		if(UsablePlaces.length>0)scene.add(0,new CharacterSelect(getList_Chara(true)));
		else scene.add(0,new MessageScene(Paramaters.TURN_MASSAGE,"Ally Turn",1000));
		
		BeforeNumbers=Paramaters.getAllyNumbers(); //AllyNumbersのバックアップ
		Paramaters.addAlly(getList_Chara(true));  //ゲストキャラをAllyListに入れておく
	}
	
	/** 座標Centerを動かす */
	public void moveCenter(int x,int y){
		mapHandling.moveCenter(x, y);
	}
	
	/** 引数のPointオブジェクトを(第2引数、第3引数)の位置に移動させるメソッド */
//    public void moveToAssinedPlace(Point pt, int x,int y){
//    	mapHandling.moveToAssinedPlace(pt, x, y);
//    }
    
    /** cursorを引数の数値の分だけ動かすメソッド */
	public void moveBattleCursor(int x,int y){
		mapHandling.moveCursor(x, y);
	}
	
	/** allyがtrueの時は味方キャラ、allyがfalseの時は敵キャラを返す */
	private CharaData getCharaData_P(boolean ally){
		if(AttackerNumber==Paramaters.PlayerNumber)
			return getCharaData(ally);
		else if(DiffenderNumber==Paramaters.PlayerNumber)
			return getCharaData(!ally);
		else return null;
	}
	
	/** 座標(cursor.x,cursor.y)にいるキャラクターを返す */
	private CharaData getCharaData(boolean attacker){
		ArrayList<CharaData> list;
		if(attacker)list=Attackers;
		else list=Diffenders;
		for(CharaData chara : list){
			if(chara.isPlace(mapHandling.getCursor())) return chara;
		}
		return null;
	}
	
	/** 味方か敵のキャラクターリストを返す */
	public ArrayList<CharaData> getList_Chara(boolean ally){
		if(AttackerNumber==Paramaters.PlayerNumber){
			if(ally) return Attackers;
			else return Diffenders;
		}
		else if(DiffenderNumber==Paramaters.PlayerNumber){
			if(ally) return Diffenders;
			else return Attackers;
		}
		else return null;
	}
	
	/** 引数座標に存在するキャラクターのデータを返す */
	private CharaData getChara(Point pt,boolean ally){
		ArrayList<CharaData> list=getList_Chara(ally);
		for(CharaData C : list){
			if(C.isPlace(pt))return C;
		}
		return null;
	}
	
	/** エンターキーを押した時の処理を記述 */
	public int pushEnter(){
		if(scene.size()==0){
			if(!isPlayerTurn())return Paramaters.NON_EVENT;
			NowCharacter=getCharaData_P(true);
			if(NowCharacter!=null && NowCharacter.isMovable()){
				ArrayList<CharaData> AL=getList_Chara(true);
				ArrayList<CharaData> EL=getList_Chara(false);
				if( AL==null || EL==null )return Paramaters.NON_EVENT;
				mapHandling.moveCenterToCursorPlace();
				scene.add( 0, new MovingScene(AL,EL,NowCharacter,mapHandling) );
			}
			/** ただのパラメーター表示 */
			else{
			    showParameters();
			}
		}
		else if(scene.get(0).keyEventEnter()){
			Scene NS=scene.get(0);
			/** 配置するキャラクターの選択 */
			if(NS instanceof CharacterSelect){
				CharacterSelect Select_Chara=(CharacterSelect)NS;
				CharaData Chara=Select_Chara.getSelectedCharacter();
				if(Chara==null){
					scene.add(0,new CheckScene(Paramaters.TWO_BATTLE,"戦闘を開始してよろしいですか？"));
				}
				else if(Select_Chara.isPermit()){
					scene.add(0,new SelectPlace(getList_Chara(true),getList_Chara(false),mapHandling));
				}
				else{
					ArrayList<CharaData> CharaList=getList_Chara(true);
					CharaList.remove(Chara);
					Select_Chara.setPermit(true);
				}
			}
			/** キャラクターの配置 */
			else if(NS instanceof SelectPlace){
				Point cursor = mapHandling.getCursor();
				CharacterSelect Select_Chara=(CharacterSelect)scene.get(1);
				CharaData Chara=Select_Chara.getSelectedCharacter();
				CharaData Other_Ally=getChara(cursor,true);
				CharaData Other_Enemy=getChara(cursor,false);
				MapChip mc = mapHandling.getMapChipOfCursor();
				if(Other_Ally==null && mc!=null && mc.isUsableChip()){
					Chara.setPlace(cursor.x, cursor.y);
					getList_Chara(true).add(Chara);
					Select_Chara.setPermit(false);
					scene.remove(0);
				}
				else showParameters();
			}
			/** ２択を表示する */
			else if(NS instanceof CheckScene){
				CheckScene check=(CheckScene)NS;
				switch(check.getNumber()){
				case Paramaters.TWO_BATTLE:
					if(check.isGoAhead()){
						setFlagToChip(false);
						scene.clear();
						scene.add(0,new MessageScene(Paramaters.TURN_MASSAGE,"Ally Turn",100));
						updateMoveRangeForEnemies();
					}
					else{
						scene.remove(0);
					}
					break;
				case Paramaters.TWO_RETRY:
					Paramaters.AllyList.clear();
					Paramaters.AllyList.addAll(CloneList);
					Paramaters.setAllyNumbers(BeforeNumbers);
					//リトライする場合
					if(check.isGoAhead()){
					    Attackers.clear();
					    Diffenders.clear();
					    Attackers.addAll(EventData.getCharaList(true));
					    Diffenders.addAll(EventData.getCharaList(false));
						Coin=true;
						scene.clear();
						setFlagToChip(true);
						createCloneList();
						whenStart();
					}
					else{
						return Paramaters.GAMEOVER_SCENE;
					}
					break;
				case Paramaters.IS_RETURN:
				    if(check.isGoAhead()){
	                    Paramaters.AllyList.clear();
	                    Paramaters.AllyList.addAll(CloneList);
	                    Paramaters.setAllyNumbers(BeforeNumbers);
				        return Paramaters.STRATEGY_SCENE;
				    }
				    else scene.remove(0);
				    break;
				case Paramaters.IS_FINISH_TURN:
	                scene.remove(0);
	                if(check.isGoAhead()) changeTurn();
	                break;
				default: break;
				}
			}
			/** メッセージの表示 */
			else if(NS instanceof MessageScene){
				MessageScene ms=(MessageScene)NS;
				switch(ms.getNumber()){
				case Paramaters.TURN_MASSAGE:
					scene.clear();
					break;
				case Paramaters.VICTORY_MASSAGE:
					//ストラテジー画面への遷移
					return EventData.getNextScene();
				default: break;
				}
				
			}
			/** 移動後のコマンドの表示 */
//			else if(NS instanceof MovingScene){
//				scene.add( 0,new MenuAfterMoving(map,Center) );
//			}
			else if(NS instanceof MenuAfterMoving){
				MenuAfterMoving menu=(MenuAfterMoving)NS;
				int num=menu.getDicideNum();
				/** スキル選択画面を呼び出す */
				if(num==0){
					scene.add( 0,new SkillMenu( NowCharacter , mapHandling.getMap() , getList_Chara(false),getList_Chara(true) ) );
				}
				else if(num==1){
					scene.add( 0, new StatusScene(NowCharacter));
				}
				else if(num==2)finishNowAction();
			}
			/** ターゲットの決定 */
			else if(NS instanceof SkillMenu){
				SkillMenu menu=(SkillMenu)NS;
				if(isPlayerTurn()){
					ArrayList<Point> tergets=changeList(menu.getTergets());
					scene.add( 0,new SearchTerget(tergets,mapHandling) );
				}
				else{
					Skill skill=menu.getSelectedSkill();
					scene.remove(0);
					BattleScene bs=(BattleScene)scene.get(0);
					bs.setAllySkill(skill);
				}
			}
			else if(NS instanceof SearchTerget){
				SkillMenu menu2=(SkillMenu)scene.get(1);
				Skill S=menu2.getSelectedSkill();
				switch(S.Type){
					case 0: CharaData C0=getCharaData_P(false);
							BattleScene bs = new BattleScene(NowCharacter,S,C0,mapHandling,true);
							EnemyBrain.setNowEstimate(NowCharacter, C0);  //推測パラメータの取得
							Integer Damege_E=EnemyBrain.getDamege_Estimate();
							if(Damege_E!=null)bs.setEstimateDamege(Damege_E.intValue());
							Double HitRate_E=EnemyBrain.getHitRate_Estimate();
							if(HitRate_E!=null)bs.setEstimateHitRate(HitRate_E.doubleValue());
							//味方キャラと敵キャラの情報と技をそれぞれ渡してバトルシーンに移る。
							scene.add( 0,bs );
							break;
					case 1: CharaData C1=getCharaData_P(true);
							scene.add( 0,new RecoverScene(NowCharacter,S,C1,mapHandling,true) );
							break;
					default : break;
				}	
			}
			//戦闘を行う場合
			else if(NS instanceof BattleScene){
				BattleScene bs=(BattleScene)NS;
				//技の選び直し
				if(bs.getNumber()==1){
					if(bs.First)deleteUntilSkillMenu();
					else {
						ArrayList<CharaData> enemy = new ArrayList<CharaData>();
						enemy.add(EnemyBrain.getNowEnemy());
						scene.add(0,new SkillMenu( EnemyBrain.getTerget() , mapHandling.getMap() , enemy, new ArrayList<CharaData>() ));
					}
				}
				//戦闘結果に関して
				else{
					//戦闘でなく、回復技をかけただけの場合
					if(NS instanceof RecoverScene){
						if(isPlayerTurn()){
							RecoverScene recover=(RecoverScene)NS;
							CharaData chara=recover.getNowEnemy();
							chara.updateLikability(Paramaters.RecoverPoint, NowCharacter.getAllyNumber(), NowCharacter.isMeal());
						}
						finishNowAction();
					}
					else{
						//敵キャラを倒した場合
						if(bs.getNowEnemy().isDie()){
							updateLikability(bs.getNowAlly(),Paramaters.BeatPoint,3);  //近くにいるキャラの好感度を上げる
							ArrayList<CharaData> list = getList_Chara(false);
							list.remove(bs.getNowEnemy());
						}
						//味方キャラが倒された場合
						if(bs.getNowAlly().isDie()){
							//updateLikability(bs.getNowAlly(),Paramaters.LosePoint,100);  //生存しているキャラの好感度を下げておく
							ArrayList<CharaData> list = getList_Chara(true);
							list.remove(bs.getNowAlly());
						}
						finishNowAction();
					}
				}
			}
			else if(NS instanceof EscapeMenu){
			    EscapeMenu menu = (EscapeMenu)NS;
			    switch(menu.getNowSelect()){
			    case 0:
			        scene.add(0,new ShowConditions());
			        break;
			    case 1:
			        scene.add(0,new CheckScene(Paramaters.IS_FINISH_TURN,"ターンを終了してもよろしいですか？"));
			        break;
			    case 2:
                    scene.add(0,new CheckScene(Paramaters.IS_RETURN,"メニュー選択画面に戻ってもよろしいですか？"));
                    break;
			    }
			}
			else if(NS instanceof ShowConditions){
			    scene.remove(0);
			}
			else if(NS instanceof MapChipScene){
				scene.remove(0);
			}
		}
		return Paramaters.NON_EVENT;
	}
	
	/** パラメーター等の表示 */
	private void showParameters(){
        CharaData ally=getCharaData_P(true);
        CharaData enemy=getCharaData_P(false);
        if(ally!=null) scene.add( 0, new StatusScene(ally));
        else if(enemy!=null) scene.add( 0, new StatusScene(enemy));
        else scene.add( 0, new MapChipScene(mapHandling));
	}
	
	/** コメントの番号を返すメソッド */
	public int getComment(){
		if(scene.size()==0){
			if(isPlayerTurn()){
				CharaData ally=getCharaData_P(true);
				CharaData enemy=getCharaData_P(false);
				if(ally!=null){
					if(ally.isMovable())return 97;
					else return 94;
				}
				if(enemy!=null)return 95;
				return 44;
			}
			else return 77;
		}
		else{
			Scene top=scene.get(0);
			if(top instanceof CharacterSelect){
				CharacterSelect Select_Chara=(CharacterSelect)top;
				return Select_Chara.getComment();
			}
			else if(top instanceof SelectPlace) return 43;
			else if(top instanceof MovingScene) return 45;
			else if(top instanceof MenuAfterMoving){
				MenuAfterMoving AfterMove = (MenuAfterMoving)top;
				return AfterMove.getComment();
			}
			else if(top instanceof SkillMenu){
				SkillMenu Menu_Skill = (SkillMenu)top;
				return Menu_Skill.getComment();
			}
			else if(top instanceof SearchTerget){
				SearchTerget search = (SearchTerget)top;
				SkillMenu menu2=(SkillMenu)scene.get(1);
				Skill S=menu2.getSelectedSkill();
				switch(S.Type){
				case 0: return 55;
				case 1: return 63;
				default: return 1000;
				}
			}
			else if(top instanceof BattleScene){
				if(top instanceof RecoverScene){
					RecoverScene recover=(RecoverScene)top;
					return recover.getComment();
				}
				else{
					BattleScene battle=(BattleScene)top;
					return battle.getComment();
				}
			}
			else if(top instanceof EscapeMenu){
				EscapeMenu menu = (EscapeMenu)top;
				return menu.getComment();
			}
			else if(top instanceof StatusScene){
				StatusScene status = (StatusScene)top;
				return status.getComment();
			}
			else if(top instanceof MessageScene){
				MessageScene ms=(MessageScene)top;
				switch(ms.getNumber()){
				case Paramaters.TURN_MASSAGE:
					if(isPlayerTurn()) return 75;
					else return 76;
				case Paramaters.VICTORY_MASSAGE:
					return 82;
				default: return 1000;
				}
			}
			else if(top instanceof CheckScene){
				CheckScene check=(CheckScene)top;
				if(check.getNumber()==Paramaters.TWO_RETRY)return 84;
			}
		}
		return 1000;
	}
	
	/** エスケープキーを押した時の処理 */
	public int pushEscape(){
		if(scene.size()>0){
			if(scene.size()==1 && !isPlayerTurn())
				return Paramaters.NON_EVENT;
			if(scene.get(0).keyEventEscape()){
				Scene NowS=scene.remove(0);
				/** 移動後のコマンドをキャンセルする場合 */
				if(NowS instanceof MenuAfterMoving){
					MovingScene MS=(MovingScene)scene.get(0);
					MS.setBeforePlace();	
				}
				else if(NowS instanceof CharacterSelect){
				    scene.add(0,NowS);
				    scene.add(0,new CheckScene(Paramaters.IS_RETURN,"メニュー選択画面に戻ってもよろしいですか？"));
				}
			}
		}
		else if(isPlayerTurn()){
			scene.add( 0 , new EscapeMenu());
		}
		return Paramaters.NON_EVENT;
	}
	
	/** バトルシーンの画面になるまでsceneリストの要素を削除する */
	private void deleteUntilSkillMenu(){
		while(true){
			Scene TOP=scene.get(0);
			if(TOP instanceof SkillMenu)break;
			else scene.remove(0);
		}
	}
	
	public int pushUp(){
		if(scene.size()>0){
			if(scene.get(0).keyEventUp())scene.remove(0);
		}
		else if(!isPlayerTurn()) return Paramaters.NON_EVENT;
		else{
		    moveBattleCursor(0,-1);
		    showEnemyRange();
		}
		return Paramaters.NON_EVENT;
	}
	
	public int pushDown(){
		if(scene.size()>0){
			if(scene.get(0).keyEventDown())scene.remove(0);
		}
		else if(!isPlayerTurn()) return Paramaters.NON_EVENT;
		else{
			moveBattleCursor(0,1);
		    showEnemyRange();
		}
		return Paramaters.NON_EVENT;
	}
	
	public int pushLeft(){
		if(scene.size()>0){
			if(scene.get(0).keyEventLeft())scene.remove(0);
		}
		else if(!isPlayerTurn()) return Paramaters.NON_EVENT;
		else{
			moveBattleCursor(-1,0);
		    showEnemyRange();
		}
		return Paramaters.NON_EVENT;
	}
	
	public int pushRight(){
		if(scene.size()>0){
			if(scene.get(0).keyEventRight())scene.remove(0);
		}
		else if(!isPlayerTurn()) return Paramaters.NON_EVENT;
		else{
			moveBattleCursor(1,0);
		    showEnemyRange();
		}
		return Paramaters.NON_EVENT;
	}
	
	/** 敵キャラの移動範囲を表示するメソッド */
	public void showEnemyRange(){
		Map map = mapHandling.getMap();
	    CharaData nowEnemy = getCharaData_P(false);
        if(nowEnemy == null) map.deleteFlagsForMap();
        else{
            nowEnemy.setMoveRange(getList_Chara(true), getList_Chara(false), map, false); //一応ここで更新しておく. 処理が重かったら変更
            ArrayList<Point> reaches = nowEnemy.getReachList();
            if(reaches != null) map.setFlagsForEnemyReachs(reaches);
        }
	}
	
	/** キャラクターを選ぶシーンかどうか */
	private boolean isCharaSelectScene(){
		for(Scene s : scene){
			if(s instanceof CharacterSelect)return true;
		}
		return false;
	}
	
	/** 別スレッドから呼び出すメソッド */
	public void running(){
	    String nowPlayingBgm = Paramaters.Music_Box.getNowBgmName();
	    if( bgmFileName!=null && !Paramaters.BGM_WIN.equals(nowPlayingBgm) && !bgmFileName.equals(nowPlayingBgm) )
	        Paramaters.Music_Box.playMusic(bgmFileName);
	    if(scene.size()>0) scene.get(0).run();
		if(isCharaSelectScene())return;
		//プレイヤーターン
		if(isPlayerTurn()){
			changeAllyFaceIndex();
			if(scene.size()>0){
				Scene NowS=scene.get(0);
	            if(NowS instanceof MovingScene){
	                MovingScene MS=(MovingScene)NowS;
	                if(MS.isFinishMoving()) scene.add( 0,new MenuAfterMoving(mapHandling.getMap()) );
	                else if(!MS.isMoving()) MS.setMovingFlag(true);
	            }
	            else if(NowS instanceof SearchTerget){
					SearchTerget ST=(SearchTerget)NowS;
					ST.setTergetFlag(true);
				}
			}
		}
		//相手ターン
		else{
			if(scene.size()>0){
	             Scene NowS=scene.get(0);
	             if(NowS instanceof MovingScene){
	                 MovingScene MS = (MovingScene)NowS;
	                 if(MS.isFinishMoving()){
	                     scene.clear();
	                     Skill EnemySkill =  EnemyBrain.getUsedSkill();
	                     if(EnemySkill!=null){
	                         CharaData Terget = EnemyBrain.getTerget();
	                         if(EnemySkill.Type==0) scene.add( 0,new BattleScene(EnemyBrain.getNowEnemy(),EnemySkill,Terget,mapHandling,false) );  //敵の攻撃
	                         else if(EnemySkill.Type==1) scene.add( 0,new RecoverScene(EnemyBrain.getNowEnemy(),EnemySkill,Terget,mapHandling,false) );   //敵の回復行動
	                         return;
	                     }
	                     else EnemyBrain.changeNext();
	                 }
	             }
			}
			else{
			    EnemyBrain.decideAction();
			    CharaData NowEnemy = EnemyBrain.getNowEnemy();
			    if(NowEnemy==null){
				    changeTurn();
				    return;
			    }
			    Point nowEnemyPlace = NowEnemy.getPlace();
				mapHandling.moveCursorToAssinedPlace(nowEnemyPlace.x, nowEnemyPlace.y);
				mapHandling.moveCenterToAssinedPlace(nowEnemyPlace.x, nowEnemyPlace.y);
			    Point MovePoint = EnemyBrain.getMovePoint();
			    scene.add( 0,new MovingScene(NowEnemy, MovePoint, mapHandling) );  //敵の移動
			}
		}
		if(EventData.isWin(this)){
			if(isWinScene())return;
			scene.add(0,new MessageScene(Paramaters.VICTORY_MASSAGE,"VICTORY!!",2000));
		}
		else if(EventData.isLose(this)){
			if(isLoseScene())return;
			scene.add(0,new CheckScene(Paramaters.TWO_RETRY,"リトライしますか？"));
		}	
		else if(isChange()){
			changeTurn();
		}
	}
	
	/** sceneリストの先頭が勝利画面かどうか */
	private boolean isWinScene(){
		if(scene.size()==0)return false;
		Scene S=scene.get(0);
		if(S instanceof MessageScene){
			MessageScene messe=(MessageScene)S;
			return (messe.getNumber()==Paramaters.VICTORY_MASSAGE);
		}
		return false;
	}
	
	/** sceneリストの先頭が敗北画面かどうか */
	private boolean isLoseScene(){
		if(scene.size()==0)return false;
		Scene S=scene.get(0);
		if(S instanceof CheckScene){
			CheckScene check=(CheckScene)S;
			return (check.getNumber()==Paramaters.TWO_RETRY);
		}
		return false;
	}
	
	/** ターンが終了したかどうか判定するメソッド */
	protected boolean isChange(){
		ArrayList<CharaData> list;
		if(Coin) list=Attackers;
		else list=Diffenders;
		int index;
		for(index=0;index<list.size();index++){
			CharaData C=list.get(index);
			if(C.isMovable()) break;
		}
		return (index==list.size());
	}
	
	/** ターン終了時(交換時)に呼び出すメソッド */
	public void changeTurn(){
		Map map = mapHandling.getMap();
	    map.deleteFlagsForMap();
	    updateMoveRangeForEnemies(); //敵キャラの移動範囲の更新
		ArrayList<CharaData> Nexts;
		ArrayList<CharaData> Finishes;
		if(Coin){
		    Nexts = Diffenders;
		    Finishes = Attackers;
		}
		else{
		    Nexts = Attackers;
		    Finishes = Diffenders;
		}
		
		Coin=!Coin;
		for(CharaData C : Nexts){
			//まずは、マップチップによるキャラクターの回復
			Point place = C.getPlace();
			int recover = map.getChip(place.x, place.y).getRecover();
			C.downHP(-1*recover);
			C.runUnusual();//状態異常の発動
			C.startTurnToSkill();
			C.setPermition(true);
		}
		for(CharaData chara : Finishes){
		    chara.setPermition(true);
		}
		
		if(Nexts.size() > 0){
			Point pt = Nexts.get(0).getPlace();
			mapHandling.moveCursorToAssinedPlace(pt.x, pt.y);
			mapHandling.moveCenterToAssinedPlace(pt.x, pt.y);
		}
		String turn;
		//敵ターンが始まるならば、EnemyBrainのフィールド等の初期化を行う
		if(!isPlayerTurn()){
			EnemyBrain.startTurn();
			turn="Enemy Turn";
		}
		else{
			checkNears();
			turn="Ally Turn";
		}
		scene.add(0,new MessageScene(Paramaters.TURN_MASSAGE,turn,100));
	}
	
	/** 敵キャラのMoveRangeの更新 */
	private void updateMoveRangeForEnemies(){
		Map map = mapHandling.getMap();
        ArrayList<CharaData> allys = getList_Chara(true);
        ArrayList<CharaData> enemies = getList_Chara(false);
	    for(CharaData chara : enemies){
	        chara.setMoveRange(allys, enemies, map, false);
	    }
	}
	
	/** 隣合ってるキャラ同士の好感度を上げるメソッド */
	private void checkNears(){
		ArrayList<CharaData> allys=getList_Chara(true);
		for(CharaData C: allys){
			int num=C.getAllyNumber(); //このキャラクターのAllyNumberの取得
			ArrayList<CharaData> list=getNearCharaList(C.getPlace(),1);
			for(CharaData elem : list){
				elem.updateLikability(Paramaters.NearPoint, num, C.isMeal());
			}
		}
	}
	
	/** 引数キャラクターの近くにいる異性キャラの好感度を引数の値だけ上げるメソッド */
	private void updateLikability(CharaData chara,int score,int distance){
		int all=100;
		ArrayList<CharaData> list = getNearCharaList(chara.getPlace(),distance);
		if(distance>=all)list = getList_Chara(true);
		else list = getNearCharaList(chara.getPlace(),distance);
		for(CharaData elem : list){
			elem.updateLikability(score, chara.getAllyNumber(), chara.isMeal());
		}
	}
	
	/** 味方キャラ全員を調べて、条件があった場合そのキャラの顔差分を変える */
	public void changeAllyFaceIndex(){
		ArrayList<CharaData> allys=getList_Chara(true);
		for(CharaData C : allys){
			ArrayList<CharaData> list=getNearCharaList(C.getPlace(),1); //隣合っているキャラの取得
			int max=0;
			for(CharaData elem :list){
			    if(C.isMeal()==elem.isMeal())continue;
				int score=C.getLikability(elem.getAllyNumber());
				if( max==0 || score>max)max=score;
			}
			C.changeFaceIndex(max);
		}
	}
	
	
	/** 第1引数座標との距離が第2引数の値以内に位置するキャラクター全てを格納したリストを返す */
	private ArrayList<CharaData> getNearCharaList(Point place,int dis){
		ArrayList<CharaData> list = new ArrayList<CharaData>();
		for(CharaData C: getList_Chara(true)){
			Point elem=C.getPlace();
			int Distance=Math.abs(elem.x-place.x)+Math.abs(elem.y-place.y);
			if(Distance==0)continue;  //自分自身はlistに格納しない
			if(Distance<=dis)list.add(C);
		}
		return list;
	}
	
	/** 描画処理 */
	public void drawing(Graphics g,Container con){
		g.setColor(Color.black);
		g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
		mapDraw(g,con);
		if(scene.size()>0) scene.get(0).draw(g,con);
		else{
			CharaData ally=getCharaData_P(true);
			CharaData enemy=getCharaData_P(false);
			if(ally!=null)ally.drawEasyParamater(g, con, 0, (int)Math.round(0.78*Paramaters.Height), Paramaters.Width, (int)Math.round(0.2*Paramaters.Height));
			else if(enemy!=null){
			    enemy.drawEasyParamater(g, con, 0, (int)Math.round(0.78*Paramaters.Height), Paramaters.Width, (int)Math.round(0.2*Paramaters.Height));
			}
		}
	}
	
	/** マップに関する描画 */
	protected void mapDraw(Graphics g,Container con){
		mapHandling.drawMap(g, con, getList_Chara(true), getList_Chara(false));
	}
	
	/** 現在選んでいるキャラクターの行動を終了する */
	protected void finishNowAction(){
		if(NowCharacter!=null){
			Map map = mapHandling.getMap();
			NowCharacter.setSkillsRange(getList_Chara(true), getList_Chara(false), map, true); //移動後の位置からのスキルの範囲を取得する(新しく追加)
			NowCharacter.setPermition(false);
			NowCharacter=null;
		}
		if(!isPlayerTurn()){
			//EnemyAction=false;
			EnemyBrain.changeNext();   //敵の行動終了と敵の切り替え。
		}
		scene.clear();
	}
	
	/** CharaDataリストを座標リストに変換する */
	protected ArrayList<Point> changeList(ArrayList<CharaData> clist){
		ArrayList<Point> list = new ArrayList<Point>();
		for(CharaData c : clist){
			list.add(c.getPlace());
		}
		return list;
	}
	
	/** プレイヤーのターンかどうか判断するメソッド */
	protected boolean isPlayerTurn(){
		if(AttackerNumber==Paramaters.PlayerNumber)return Coin;
		else return !Coin;
	}
	
	/** フィールドMapを返すメソッド */
	public Map getNowMap(){
		return mapHandling.getMap();
	}

    @Override
    public void moveCursor(int x, int y) {
		if(scene.size()>0){
			scene.get(0).moveCursor(x, y);
		}
		else if(isPlayerTurn()){
			mapHandling.moveCursorFromPixelValue(x, y);
		}
    }
    
	@Override
	public void mouseDragged(int x, int y) {
		if(scene.size()>0 && scene.get(0) instanceof SceneWithMouseDragged){
			SceneWithMouseDragged mouseDrag = (SceneWithMouseDragged) scene.get(0);
			mouseDrag.mouseDragged(x, y);
		}
		else if(isPlayerTurn()){
			mapHandling.moveCenterFromPixelValue(x, y);
		}
	}

	@Override
	public void mousePressed(int x, int y) {
		mapHandling.setMousePressedPlace(new Point(x,y));
	}

	@Override
	public void mouseReleased(int x, int y) {
		mapHandling.setMousePressedPlace(null);
	}

}
