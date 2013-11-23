package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;

import sim.game.data.CharaData;
import sim.game.data.Job;
import sim.tools.Select;

/** １キャラの職業の変更画面に関するクラス */
public class DecideJob extends Select implements GlobalScene {
	private CharaData NowChara;
	
	public DecideJob(CharaData C){
		super(0.05,0.2,0.6,0.2,11,11);
		NowChara=C;
		createMenu();
	}
	
	/** 各項目の作成 */
	private void createMenu(){
		for(int i=0;i<Names.length-1;i++){
			Job j = Job.getJob(i);
			Names[i]=j.getJobName();
			Permits[i]=NowChara.isBecome(i);
		}
		Names[Names.length-1]="戻る";
		Permits[Names.length-1]=true;
	}
	
	@Override
	public void drawing(Graphics g, Container con) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
		Draw(g,con);
		NowChara.drawFace(g,con,(int)Math.round(0.4*Paramaters.Width),(int)Math.round(0.05*Paramaters.Height));
		g.setColor(Color.white);
		g.setFont(new Font("明朝体",Font.BOLD,(int)Math.round(0.05*Paramaters.Height)));
		g.drawString("名前："+NowChara.getCharaName(),(int)Math.round(0.6*Paramaters.Width),(int)Math.round(0.1*Paramaters.Height));
		if(kettei==-1 || kettei==Names.length-1)return;
		int[] params = NowChara.getStatuses(Job.getJob(kettei)); //今指している職業になった時のパラメーター
		int[] NowParams = {NowChara.getHP(),NowChara.getAttack(),NowChara.getDiffence(),NowChara.getHit(),
				NowChara.getAvoid(),NowChara.getIntel(),NowChara.getRuck(),NowChara.getMove()};
		String[] Params_String = {"HP:","攻撃:","防御:","命中:","回避:","知力:","運:","移動:"};
		for(int i=0;i<NowParams.length;i++){
			Color col;
			if(params[i]>NowParams[i])col=Color.blue;
			else if(params[i]==NowParams[i])col=Color.green;
			else col=Color.red;
			g.setColor(col);
			g.drawString(Params_String[i]+params[i]+"("+NowParams[i]+")",
					(int)Math.round(0.4*Paramaters.Width), (int)Math.round((0.34+0.08*i)*Paramaters.Height));
		}
	}

	@Override
	public int pushDown() {
		Moving(true);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushEnter() {
		if(kettei==-1 || !Permits[kettei]) return Paramaters.NON_EVENT;
		else if(kettei==Names.length-1)return Paramaters.DELETE_NOW_SCENE;
		else{
			NowChara.changeJob(Job.getJob(kettei));
			return Paramaters.DELETE_NOW_SCENE;
		}
	}

	@Override
	public int pushEscape() {
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
	
	/** ketteiの値に従ったコメント番号を返すメソッド */
	public int getComment(){
		switch(kettei){
			case -1: return 20;
			case 0: return 21;
			case 1: return 22;
			case 2: return 23;
			case 3: return 24;
			case 4: return 25;
			case 5: return 26;
			case 6: return 27;
			case 7: return 28;
			case 8: return 29;
			case 9: return 30;
			case 10: return 31;
			default: return 1000;
		}
	}

	@Override
	public void running(){}

    @Override
    public void moveCursor(int x, int y) {
        decideNowSelectedItem(x,y);
    }

}
