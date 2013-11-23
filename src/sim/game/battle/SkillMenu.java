package sim.game.battle;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import sim.game.Paramaters;
import sim.game.data.CharaData;
import sim.game.data.Map;
import sim.game.data.Skill;
import sim.tools.GraphicTool;
import sim.tools.Select;

public class SkillMenu extends Select implements Scene {
	protected ArrayList<Skill> SkillList;
	protected Map map;
	protected ArrayList<CharaData> Tergets_Enemy;
	protected ArrayList<CharaData> Tergets_Ally;
	protected Point CharaPlace;
	
	public SkillMenu(CharaData c,Map m,ArrayList<CharaData> es,ArrayList<CharaData> as){
		super(0.4,0.05,0.5,0.2,10,c.getSkillList().size());
		SkillList=c.getSkillList();
		map=m;
		Tergets_Enemy = es;
		Tergets_Ally = as;
		CharaPlace=c.getPlace();
		formatArrays();
	}
	
	public SkillMenu(CharaData c){
		super(0.4,0.15,0.5,0.2,10,0);
		formatArrays(c);
	}
	
	/** このコンストラクタで技表の座標と大きさの指定を行う */
	public SkillMenu(double x,double y,double w,double h){
		super(x,y,w,h,10,0);
	}
	
	/** 配列の初期化 */
	protected void formatArrays(){
		for(int i=0;i<Permits.length;i++){
			Skill S=SkillList.get(i);
			Names[i]=S.getName();
			ArrayList<CharaData> Tergets=getTergets(S.Type);
			Permits[i]=S.isUsable_CharaData(Tergets,map,CharaPlace);
		}
	}
	
	/** 配列の初期化。技の参照の場合。*/
	public void formatArrays(CharaData C){
		SkillList=C.getSkillList();
		Names = new String[SkillList.size()];
		Permits = new boolean[SkillList.size()];
		for(int i=0;i<SkillList.size();i++){
			Skill S=SkillList.get(i);
			Names[i]=S.getName();
			Permits[i]=false;
		}
	}
	
	@Override
	public void draw(Graphics g,Container con) {
		// TODO Auto-generated method stub
		Tools.setTransmission(g, 0, 0, Paramaters.Width, Paramaters.Height, Color.black, 0.7f);
		Draw(g,con);
		explainEffect(g, con, Pt.x-Swide, (int)Math.round(Pt.y+1.1*Shigh),
				(int)Math.round(3*Swide), (int)Math.round(0.3*Paramaters.Height));
	}
	
	/** 効果説明部の描画 */
	private void explainEffect(Graphics g,Container con,int x,int y,int width,int height){
		g.setColor(new Color(0,0,0,150));
		g.fillRect(x, y, width, height);
		Tools.drawFrame(g, con, x, y, width, height);
		Skill S=getSelectedSkill();
		if(S==null)return;
		g.setFont(new Font("明朝体",Font.BOLD,(int)Math.round(0.08*height)));
		g.setColor(Color.white);
		g.drawString(S.getRange_String()+"　　　"+S.getRest_String(),(int)Math.round(x+0.03*width),(int)Math.round(y+0.20*height));
		g.drawString(S.getRevisions_String(),(int)Math.round(x+0.03*width),(int)Math.round(y+0.38*height));
		g.drawString(S.getUnusuals_String(),(int)Math.round(x+0.03*width),(int)Math.round(y+0.56*height));
		g.drawString(S.getRecovers_String(),(int)Math.round(x+0.03*width),(int)Math.round(y+0.74*height));
		g.drawString(S.getLimit_String(),(int)Math.round(x+0.03*width),(int)Math.round(y+0.92*height));
	}

	@Override
	public boolean keyEventDown() {
		// TODO Auto-generated method stub
		Moving(true);
		return false;
	}

	@Override
	public boolean keyEventEnter() {
		// TODO Auto-generated method stub
		if(kettei==-1 || !Permits[kettei])return false;
		else return true;
	}

	@Override
	public boolean keyEventEscape() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	/** 一応、ここでは使用しない */
	public boolean keyEventLeft() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	/** 一応、ここでは使用しない */
	public boolean keyEventRight() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyEventUp() {
		// TODO Auto-generated method stub
		Moving(false);
		return false;
	}
	
	/** 選ばれた技を返す */
	public Skill getSelectedSkill(){
		if(kettei==-1)return null;
		return SkillList.get(kettei);
	}
	
	/** 選ばれた技の射程範囲にいる対象を全て返す */
	public ArrayList<CharaData> getTergets(){
		Skill S=SkillList.get(kettei);
		ArrayList<CharaData> Tergets=getTergets(S.Type);
		return S.getTergetList_CharaData(Tergets,map,CharaPlace);
	}
	
	/** 引数の技タイプに対するターゲットを返すメソッド */
	protected ArrayList<CharaData> getTergets(int type){
		switch(type){
			case 0: return Tergets_Enemy;
			case 1: return Tergets_Ally;
			default: return null;
		}
	}
	
	/** コメントの番号を返すメソッド */
	public int getComment(){
		Skill now=getSelectedSkill();
		if(now==null)return 50;
		else{
			if(now.Type==0 && Permits[kettei])return 51;
			if(now.Type==0 && !Permits[kettei])return 52;
			if(now.Type==1 && Permits[kettei])return 53;
			if(now.Type==1 && !Permits[kettei])return 54;
		}
		return 1000;
	}

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void moveCursor(int x, int y) {
		decideNowSelectedItem(x,y);
	}
	
}
