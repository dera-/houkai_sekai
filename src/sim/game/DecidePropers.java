package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import sim.game.data.CharaData;
import sim.game.data.Job;
import sim.game.data.MapChip;
import sim.tools.Select;

public class DecidePropers extends Select implements GlobalScene {
	private CharaData NowChara;
	private int[] BasicPropers = new int[10];
	private ArrayList<String> BasicLands = new ArrayList<String>();
	
	public DecidePropers(CharaData C){
		super(0.05,0.2,0.6,0.3,10,C.getPropersSize()+MapChip.NormalChipArrays.length+2);
		NowChara=C;
		for(int i=0;i<BasicPropers.length;i++)BasicPropers[i]=0;
		createItems();
	}
	
	public DecidePropers(CharaData C,int[] propers){
		super(0.05,0.2,0.6,0.3,10,C.getPropersSize()+MapChip.NormalChipArrays.length+2);
		NowChara=C;
		for(int i=0;i<BasicPropers.length;i++)BasicPropers[i]=propers[i];
		createItems();
	}
	
	/** 各項目の作成 */
	private void createItems(){
		int index;
		for(index=0;index<NowChara.getPropersSize();index++){
			String job=Job.getJob(index).getJobName();
			int threshold=NowChara.getProperThreshold(index);
			Names[index]=job+"の適正度UP(消費:"+threshold+")";
			Permits[index]=NowChara.canAddPropers(threshold);
		}
		int threshold=NowChara.NecessaryPointToChip();
		
		for(int i=0;i<MapChip.NormalChipArrays.length;i++,index++){
			Names[index]=MapChip.NormalChipArrays[i]+"を得意MAPにする(消費:"+threshold+")";
			Permits[index]=(!NowChara.isSuccessChip(MapChip.NormalChipArrays[i]) && NowChara.canAddPropers(threshold));
		}
		
		Names[Names.length-2]="クリア";
		Names[Names.length-1]="終了";
		Permits[Permits.length-2]=true;
		Permits[Permits.length-1]=true;	
	}
	
	@Override
	public void drawing(Graphics g, Container con) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
		Draw(g,con);
		NowChara.drawFace(g,con,(int)Math.round(0.4*Paramaters.Width),(int)Math.round(0.05*Paramaters.Height));
		g.setColor(Color.white);
		g.setFont(new Font("明朝体",Font.BOLD,(int)Math.round(0.05*Paramaters.Height)));
		g.drawString("名前:"+NowChara.getCharaName(),(int)Math.round(0.6*Paramaters.Width),(int)Math.round(0.1*Paramaters.Height));
		g.drawString("ポイント:"+NowChara.getProperPoint(), (int)Math.round(0.6*Paramaters.Width), (int)Math.round(0.2*Paramaters.Height));
		double startY=0.27;
		double dy=0.07;
		for(int i=0;i<NowChara.getPropersSize();i++){
			String J=Job.getJob(i).getJobName();
			g.drawString(J+":"+NowChara.getProper(i),(int)Math.round(0.4*Paramaters.Width),(int)Math.round((startY+i*dy)*Paramaters.Height));
		}
		g.drawString("得意MAP:"+NowChara.MapchipsString(),(int)Math.round(0.4*Paramaters.Width),(int)Math.round((startY+NowChara.getPropersSize()*dy)*Paramaters.Height));
	}

	@Override
	public int pushDown() {
		Moving(true);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushEnter() {
		if(pointToNextScreen(cursorX, cursorY)){
			jumpNextScreen();
			return Paramaters.NON_EVENT;
		}
		else if(kettei==-1 || !Permits[kettei])return Paramaters.NON_EVENT;
		else if(kettei==Names.length-2){
			formatPropers();
			return Paramaters.NON_EVENT;
		}
		else if(kettei==Names.length-1){
			NowChara.clearList();
			return Paramaters.DELETE_NOW_SCENE;
		}
		else if(0<=kettei && kettei<NowChara.getPropersSize()){
			NowChara.addPropers(kettei);
			return Paramaters.NON_EVENT;
		}
		else if(kettei-NowChara.getPropersSize()<MapChip.NormalChipArrays.length){
			NowChara.addSelfLand(MapChip.NormalChipArrays[kettei-NowChara.getPropersSize()]);
			return Paramaters.NON_EVENT;
		}
		else return Paramaters.NON_EVENT;
	}
	
	/** NowCharaの適性等をリセットするメソッド */
	private void formatPropers(){
		NowChara.resetProperPoint();
		NowChara.setPropers(BasicPropers);
		NowChara.setSelfLands(BasicLands);
		NowChara.recoverHP();
	}

	@Override
	public int pushEscape() {
		formatPropers();
		NowChara.clearList();
		return Paramaters.DELETE_NOW_SCENE;
	}

	@Override
	public int pushLeft() {
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushRight() {
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushUp() {
		Moving(false);
		return Paramaters.NON_EVENT;
	}

	@Override
	public void running() {
		createItems();
	}
	
	/** 各場面に対するコメント番号を返すメソッド */
	public int getComment(){
		if(kettei==-1)return 15;
		else if(0<=kettei && kettei<NowChara.getPropersSize())return 16;
		else if(kettei-NowChara.getPropersSize()<MapChip.NormalChipArrays.length)return 17;
		else if(kettei==Names.length-2)return 18;
		else if(kettei==Names.length-1)return 19;
		else return 1000;
	}

    @Override
    public void moveCursor(int x, int y) {
    	cursorX = x;
    	cursorY = y;
        decideNowSelectedItem(x,y);
    }

}
