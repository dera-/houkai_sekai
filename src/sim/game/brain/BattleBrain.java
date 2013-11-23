package sim.game.brain;

import java.awt.Point;
import java.util.ArrayList;

import sim.game.Paramaters;
import sim.game.data.CharaData;
import sim.game.data.Map;
import sim.game.data.MapChip;
import sim.game.data.Skill;

/** 戦闘MAPにおける敵AIに関するクラス */
public class BattleBrain {
    protected ArrayList<CharaData> Allys; //プレイヤー軍のキャラリスト
    protected ArrayList<CharaData> Enemies;  //AI軍のキャラリスト
    protected ArrayList<EstimateClass> EstimateList;  //推測リスト
    protected Map NowMap;  //今戦ってるマップ
    protected int NowCharaNumber=0;  //今行動を行っているキャラクターのインデックス
    protected CharaData NowChara;  //今行動を行っているキャラ
    protected Action BestAction=null;
    protected BrainParameter NowParameter=null;
    protected final int UnusualPoint = 30; //相手を状態異常にした時の基本評価値(本来は10)
    protected final int RecoverScore = 30;  //回復を行うことによる基本評価値(本来は15)
    protected EstimateClass NowEstimate=null;  //現在のバトルシーンで使用している推測オブジェクト
    
    public BattleBrain(ArrayList<CharaData> AL,ArrayList<CharaData> EL,Map m){
        Allys=AL;
        Enemies=EL;
        NowMap=m;
        EstimateList = new ArrayList<EstimateClass>();
    }
    
    /** ターンが始まった時に行う事 */
    public void startTurn(){
        BestAction=null;
        NowParameter=null;
        NowCharaNumber=0;
        NowChara=null;
        sortStronger();
        EstimateList.clear();
    }
    
    /** 強い順にEnemiesリストをソートする */
    //TODO 死亡に関する評価値の絶対値が低いやつから順にソートする必要もある
    protected void sortStronger(){
        for(int i=0;i<Enemies.size();i++){
            CharaData E=Enemies.remove(i);
            int score=E.getStrongScore();
            int dead = Math.abs(E.getBrainParameter().Dead);
            int j;
            for(j=0;j<i;j++){
                CharaData elem=Enemies.get(j);
                int elem_dead=Math.abs(elem.getBrainParameter().Dead);
                int score_elem=elem.getStrongScore();
                if(elem_dead>dead || (elem_dead==dead && score>score_elem)){
                    break;
                }
            }
            Enemies.add(j,E);
        }
    }
    
    /** 1体のキャラの行動に関するメソッド */
    public void decideAction(){
        if(NowCharaNumber>=Enemies.size()){
            BestAction=null;
            return;
        }
        NowChara = Enemies.get(NowCharaNumber);//動かす敵キャラの取得
        NowParameter = NowChara.getBrainParameter();
        BestAction=getAction(NowChara,NowChara.getPlace());
        if(NowChara.isMovable()){
            //移動範囲の取得
            ArrayList<Point> reaches = NowChara.getReachList();
            for(Point p : reaches){
                if(existOtherAi(p))continue;
                Action act=getAction(NowChara,p);
                if(act.Score>BestAction.Score)BestAction=act;
            }
        }
        /*
        ArrayList<EstimateClass> list;
        if(NowParameter.Injured) list = BestAction.EstimateList;
        else list = createEstimateClassList(NowChara,BestAction.MovePoint);
        */
        EstimateList.addAll(BestAction.EstimateList);
    }
    
    /** 指定した位置に他のAIキャラがいるかどうかの判定 */
    private boolean existOtherAi(Point reach){
        for(CharaData enemy : Enemies){
            if(enemy.isPlace(reach))return true;
        }
        return false;
    }
    
    /** 今操作しているキャラを返すメソッド */
    public CharaData getNowEnemy(){
        if(NowChara!=null)return NowChara;
        else return null;
    }
    
    /** 今操作しているキャラの移動する座標を返す */
    public Point getMovePoint(){
        return BestAction.MovePoint;
    }
    
    /** 今操作しているキャラが使う技を返す */
    public Skill getUsedSkill(){
        return BestAction.Attack;
    }
    
    /** 今操作しているキャラがターゲットにしているキャラを返す */
    public CharaData getTerget(){
        return BestAction.Terget;
    }
    
    /** 現在操作しているキャラの行動を終了して、操作キャラを変える */
    public void changeNext(){
        if(!NowChara.isDie()){
            NowChara.setPermition(false);
            NowCharaNumber++;
        }
        NowChara=null;
    }
    
    /** NowEstimateに値を指定するメソッド */
    public void setNowEstimate(CharaData Player,CharaData AI){
        for(EstimateClass est : EstimateList){
            if(Player==est.Player && AI==est.AI){
                NowEstimate=est;
                break;
            }
        }
    }
    
    /** NowEstimateのフィールドDamegeの値を返すメソッド */
    public Integer getDamege_Estimate(){
        if(NowEstimate!=null)return NowEstimate.Damege;
        return null;
    }
    
    /** NowEstimateのフィールドHitRateの値を返すメソッド */
    public Double getHitRate_Estimate(){
        if(NowEstimate!=null)return NowEstimate.HitRate;
        return null;
    }
    
    /** 指定したマスにおける最適な行動を返す */
    private Action getAction(CharaData C,Point pt){
        Action better=null;
        ArrayList<Skill> SkillList = C.getSkillList();
        
        ArrayList<EstimateClass> list = createEstimateClassList(C,pt);  //予想リストの作成
        double NearScore = 0;
        if(NowParameter.NearPlayer>0) NearScore = getNearSocre(C.getPlace(),pt);  //敵に近接することに関する評価値
        double Score_NearAIs = 0; 
        if(NowParameter.NearAI>0) Score_NearAIs = getNearScore_AI(C.getPlace(),pt);  //味方に近接することに関する評価値
        double NearRecover = 0;
        if(NowParameter.NearHeeler>0)NearRecover = getNearRecover(C,C.getPlace(),pt);  //近くに回復キャラがいることに関する評価値
        int ReachMapChip = 0;
        if(NowParameter.Mapchip_Satisfaction>0)ReachMapChip = getMapChipScore(C,pt);  //移動先のマップチップに関する評価値
        
        double SCORE=NearScore+Score_NearAIs+NearRecover+ReachMapChip;
        
        //技を使わない時の行動クラスのオブジェクトの生成
        better = new Action(C,SCORE,pt,null,null,false,list);
        if(!C.isMovable())return better;  //行動不可能なら何もしない行動を返り値として返す。
        
        for(Skill S : SkillList){
            if(!S.isRestSkill())continue;
            
            //相手に攻撃する技を使った場合
            boolean beat=false; //敵を倒せるかどうか
            double score=0;
            if(S.Type==0){
                ArrayList<CharaData> tergets= S.getTergetList_CharaData(Allys, NowMap, pt);
                for(CharaData T : tergets){
                    CounterBrain counter = new CounterBrain(T,C,S,NowMap,true,pt);
                    Skill StrongSkill = counter.decideEnemySkill();  //プレイヤーが選ぶであろう最適な技
                    score=calculatePoint(S,StrongSkill,C,T,pt);
                    /* 敵を気絶もしくは撃破した場合 */
                    beat=(score>=NowParameter.Special_Attack_Faint);
                    Action NowAct=new Action(C,SCORE+score,pt,S,T,beat,list);
                    if(NowAct.Score>better.Score)better=NowAct;
                }
            }
            
            //味方を回復する技を使った場合
            else if(S.Type==1){
                ArrayList<CharaData> tergets= S.getTergetList_CharaData(Enemies, NowMap, pt);
                for(CharaData T : tergets){
                    score=calculateRecoverScore(S,C,T);
                    Action NowAct=new Action(C,SCORE+score,pt,S,T,beat,list);
                    if(NowAct.Score>better.Score)better=NowAct;
                }
            }
        }
        return better;
    }
    
    /** 次のターンのプレイヤーの行動によって受けるダメージの予測リストを作成する */
    private ArrayList<EstimateClass> createEstimateClassList(CharaData C,Point pt){
        ArrayList<EstimateClass> list = new ArrayList<EstimateClass>();
        for(CharaData Elem : Allys){
            EstimateClass est = getEstimate(C,Elem,pt);
            if(est!=null) list.add(est);
        }
        return list;
    }
    
    /** 指定したプレイヤーキャラと戦った時の推測値を返す */
    //TODO 修正の必要あり
    private EstimateClass getEstimate(CharaData AI,CharaData P,Point pt){
        EstimateClass est = null;
        ArrayList<Skill> skills = P.getSkillList(); 
        for(Skill S : skills){
            if(S.isInRange(pt) == false)continue;
            int D = BattleCalculation.getDamege(S, null, P, AI, NowMap, P.getPlace(), pt);
            double H = BattleCalculation.getHitRate(S, null, P, AI, NowMap, P.getPlace(), pt);
            if(est==null || (H >= Paramaters.Hit_Probability_By_Player && D > est.Damege)){
                est=new EstimateClass(AI,P,D,H);
            }
        }
        return est;
    }
    
    /** 敵(プレイヤーキャラ)に接近することに関する評価値の計算 */
    private double getNearSocre(Point before,Point after){
        double score=0;
        for(CharaData C : Allys){
            double diffDistance = getDiffDistance(C.getPlace(),before,after,true);
            if( diffDistance > 0 )score += diffDistance;
        }
        double rate=1.0*score/(Allys.size()*NowChara.getMove());
        return rate*NowParameter.NearPlayer;
    }
    
    /** 行動済みの味方(AIキャラ)に近接することに関する評価値の計算 */
    private double getNearScore_AI(Point before,Point after){
        double score=0;
        for(CharaData C : Enemies){
            double diffDistance = getDiffDistance(C.getPlace(),before,after);
            if( diffDistance > 0 )score += diffDistance;
        }
        double rate=1.0*score/(Enemies.size()*NowChara.getMove());
        return rate*NowParameter.NearAI;
    }
//    private double getNearScore_AI(Point pt){
//        int near=0;
//        MovingBrain moving = new MovingBrain(Allys,Enemies,NowMap,NowChara,false,pt);
//        for(int i=0;i<NowCharaNumber;i++){
//            CharaData AI = Enemies.get(i);
//            Point AI_Place = AI.getPlace();
//            if(moving.isNear(AI_Place, 1))near++;
//        }
//        return (1.0*near/Enemies.size())*(NowParameter.NearAI); //今の所、乗算にしておく
//    }
    
    /** 近くにまだ行動してない回復キャラがいることに関する評価値 */
    private double getNearRecover(CharaData AI, Point before, Point after){
        double near=0;
        int RecoverPoint=NowParameter.NearHeeler;
        int[] array=getBadStates(AI);
        
        //正常ならば、0を返す
        boolean ok=true;
        for(int i=0;i<array.length;i++){
            if(array[i]!=0){
                ok=false;
                break;
            }
        }
        if(ok)return 0;
        //他のAIキャラについて
        for(CharaData Chara : Enemies){
            ArrayList<Skill> CharaSkills = Chara.getSkillList();
            int[] recovers = {0,0,0,0};
            for(int index=0;index<CharaSkills.size();index++){
                Skill S=CharaSkills.get(index);
                if(S.Type!=1 || !S.isRestSkill())continue;
                int[] S_array=S.getRecovers();
                for(int j=0;j<recovers.length;j++){
                    if(S_array[j]>recovers[j])recovers[j]=S_array[j];
                }
            }
            int total=0;  //最高回復値
            for(int index=0; index<array.length ;index++){
                if(index == 0){
                    int recover = BattleCalculation.getRecover(recovers[index],Chara,AI);
                    int afterRecover = getDecreasedHP(AI.getNowHP() + recover, AI.getHP()) ;
                    total += array[index] - afterRecover;
                }
                else if(recovers[index]<array[index]) continue;
                else total += array[index];
            }
            if(total==0)continue;
            double diffDistance = getDiffDistance(Chara.getPlace(),before,after);
            if( diffDistance > 0 )near += total*diffDistance;
        }
        return (1.0*near/Enemies.size())*RecoverPoint;
    }
    
       /** 第1引数に対する距離が、第2引数から第3引数への移動でどの程度縮まったかを整数値で返すメソッド */
    private double getDiffDistance(Point targetPlace, Point beforePlace, Point afterPlace){
        return getDiffDistance(targetPlace, beforePlace, afterPlace, false);
    }
    
    
    /** 第1引数に対する距離が、第2引数から第3引数への移動でどの程度縮まったかを整数値で返すメソッド */
    private double getDiffDistance(Point targetPlace, Point beforePlace, Point afterPlace, boolean limit){
        int before = Math.abs(targetPlace.x-beforePlace.x)+Math.abs(targetPlace.y-beforePlace.y);
        int after = Math.abs(targetPlace.x-afterPlace.x)+Math.abs(targetPlace.y-afterPlace.y);
        if(limit && before > NowParameter.NearStartDistance)return 0;
        return 1.0 * (before-after) / after;
    }
    
    /** 移動先のマップチップに関する評価値を返すメソッド */
    private int getMapChipScore(CharaData C,Point place){
        double UpRate=1.2;
        int Satisfaction = NowParameter.Mapchip_Satisfaction;
        MapChip chip=NowMap.getChip(place.x, place.y);
        if(MapChip.isAdvantageous(chip.getChar())) return (int)Math.round(UpRate*Satisfaction);
        else if(C.isSuccessChip(chip.getName()))return Satisfaction;
        else return 0;
    }
    
    /** 引数のキャラの負傷具合を示す配列を返すメソッド */
    private int[] getBadStates(CharaData C){
        int [] Bads=new int[4];
        Bads[0] = getDecreasedHP(C.getNowHP(), C.getHP());
        int Unusuals[]=C.getUnusuals();
        for(int i=0;i<Unusuals.length;i++){
            Bads[i+1]=Unusuals[i];
        }
        return Bads;
    }
    
    /** HPの減少度合を返すメソッド */
    private int getDecreasedHP(int hp,int maxHp){
           double AboutHp[]={0.5,0.3,0.1};
            double rate=1.0*hp/maxHp;
            int index;
            for(index=AboutHp.length-1; index>=0; index--){
                if(rate<AboutHp[index])break;
            }
            return index+1;
    }
    
    //TODO メソッドの分割
    /** 評価値の計算を行う.第1引数はAI軍の技。第2引数はプレイヤー軍の技。第3引数はAIキャラ。第4引数はプレイヤーキャラ。第5引数はAIキャラの位置。 */
    private double calculatePoint(Skill ES,Skill AS,CharaData AI,CharaData Player,Point Place_AI){
        int BravePoint=NowParameter.BravePoint;
        //int MaxHP_AI=AI.getHP();  //AI軍のキャラのHP
        //int MaxHP_Player=Player.getHP();  //プレイヤー軍のキャラのHP
        int DamegeToPlayer = BattleCalculation.getDamege(ES,AS,AI,Player,NowMap,Place_AI,Player.getPlace());
        int DamegeToAI = BattleCalculation.getDamege(AS,ES,Player,AI,NowMap,Player.getPlace(),Place_AI);
        double HitToPlayer = BattleCalculation.getHitRate(ES, AS, AI, Player, NowMap,Place_AI,Player.getPlace());
        double HitToAI = BattleCalculation.getHitRate(AS,ES,Player,AI,NowMap,Player.getPlace(),Place_AI);
        double UnusualToPlayer = BattleCalculation.getUnusual(AI, Player);
        double UnusualToAI = BattleCalculation.getUnusual(Player,AI);
        int[] UnusalListToPlayer = {0,0,0};
        if(ES!=null)UnusalListToPlayer = ES.getUnusuals();
        int[] UnusualListToAI={0,0,0};
        if(AS!=null)UnusualListToAI = AS.getUnusuals();
        //boolean risk=NowParameter.Injured;

        int PlusScore=0;
        int MinusScore=0;
        
        if(HitToPlayer>=Paramaters.Hit_Probability){
            int playerNowHP = Player.getNowHP();
            if(DamegeToPlayer > playerNowHP){
                return NowParameter.Defeat;
            }
            //新たに追加。どれだけプレイヤーキャラを撃破に近づけたかも評価に入れる
            int before = getDecreasedHP(playerNowHP, Player.getHP());
            int after = getDecreasedHP(playerNowHP - DamegeToPlayer, Player.getHP());
            PlusScore += UnusualPoint * ( after - before );
            if(UnusualToPlayer>=Paramaters.Hit_Probability){
                if(UnusalListToPlayer[1]>0){
                    return NowParameter.Special_Attack_Faint;
                }
                else if(UnusalListToPlayer[0]>0 || UnusalListToPlayer[2]>0){
                    int power;
                    if(UnusalListToPlayer[0]>UnusalListToPlayer[2])
                        power = UnusalListToPlayer[0];
                    else power = UnusalListToPlayer[2];
                    PlusScore+=UnusualPoint*power;
                }    
            }
        }
        if(HitToAI>=Paramaters.Hit_Probability){
            if(DamegeToAI>AI.getNowHP() && NowParameter.Dead<0){ //Deadの値が0以上のキャラはここの処理を無視
                return NowParameter.Dead;
            }
            if(UnusualToAI>=Paramaters.Hit_Probability){
                int power=0;
                for(int i=0;i<UnusualListToAI.length;i++){
                    if(UnusualListToAI[i]>power) power=UnusualListToAI[i];
                }
                MinusScore-=UnusualPoint*power;
            }
        }
        double ai = 1.0*DamegeToPlayer*HitToPlayer;
        double player = 1.0*DamegeToAI*HitToAI;
        double minus;
        if(NowParameter.Injured)minus=player;
        else{
            minus=0;
            MinusScore=0;
        }
        double score = 100.0*(ai-minus)/(ai+player)+PlusScore+MinusScore+BravePoint;
        return score;
    }
    
    /** 回復技を使用する時の評価値を返すメソッド */
    public int calculateRecoverScore(Skill S,CharaData Heeler,CharaData Terget){
        int score=0;
        int[] Recovers=S.getRecovers();
        int[] State=Terget.getUnusuals();
        score+=(int)Math.round( 100.0*BattleCalculation.getRecover(Recovers[0],Heeler,Terget)/Terget.getHP() );
        for(int i=0;i<State.length;i++){
            int R=0;
            if(Recovers[i+1]<State[i]) R=0;
            else R=State[i];
            score+=(RecoverScore*R);
        }
        return score;
    }
    
}
//AIキャラの行動に関するクラス
class Action{
    CharaData AI_Chara;  //行動を起こすAIキャラ
    double Score;  //評価値
    Point MovePoint;  //移動する場所
    Skill Attack;  //使用する技
    CharaData Terget; //戦う相手
    boolean Beat;  //撃破あるいは気絶させたかどうか
    ArrayList<EstimateClass> EstimateList;  //この行動をとった時の推測リスト
    
    public Action(CharaData AI,double a,Point p,Skill s,CharaData t,boolean b,ArrayList<EstimateClass> list){
        AI_Chara=AI;
        Score=a;
        MovePoint=p;
        Attack=s;
        Terget=t;
        Beat=b;
        EstimateList = list;
        caluculateScore(list);
    }
    
    /** 評価値を計算するメソッド */
    private void caluculateScore(ArrayList<EstimateClass> list){
        BrainParameter brain=AI_Chara.getBrainParameter();
        if(brain.Dead==0)return;
        int Damege_All=0;
        for(EstimateClass E : list){
            if(E.Player==Terget && Beat)continue;
            if(E.HitRate>Paramaters.Hit_Probability_By_Player)Damege_All+=E.Damege;
        }
        if(Damege_All>AI_Chara.getNowHP())Score+=brain.Dead;
        else if(brain.Injured)Score-=(int)Math.round( 100.0*Damege_All/AI_Chara.getHP() );
    }
    
}

//戦闘でプレイヤーキャラとの戦闘結果の予測を行うクラス
class EstimateClass{
    CharaData AI;
    CharaData Player;
    int Damege;
    double HitRate;
    
    public EstimateClass(CharaData A,CharaData P,int d,double h){
        AI=A;
        Player=P;
        Damege=d;
        HitRate=h;
    }
    
}

