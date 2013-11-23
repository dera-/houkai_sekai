package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import org.omg.Dynamic.Parameter;

import sim.game.data.CharaData;
import sim.game.data.Job;
import sim.tools.Select;

public class MatchScene extends Select implements GlobalScene {
	private ArrayList<CharaData> CharaList = new ArrayList<CharaData>();
	private EndCheckScene Check;
	
	public MatchScene(){
		super(0.4,0.2,0.6,0.2,10,Paramaters.AllyList.size());
		createMenu_Name();
		createMenu_Permit();
	}
	
	/** 各項目の作成 */
	private void createMenu_Name(){
		for(int i=0;i<Paramaters.AllyList.size();i++){
			Names[i]=Paramaters.AllyList.get(i).getCharaName();
		}
	}
	
	/** 各項目が選択可能かどうか調べる */
	private void createMenu_Permit(){
		CharaData Chara=null;
		if(CharaList.size()>0)Chara=CharaList.get(0);
		for(int i=0; i<Permits.length ;i++){
			CharaData C = Paramaters.AllyList.get(i);
			if(Chara==null)Permits[i]=true;
			else if(Chara.isMeal()!=C.isMeal()){
				boolean like1=(Chara.getLikability(C.getAllyNumber())>=Paramaters.THRESHOLD_LIKE);
				boolean like2=(C.getLikability(Chara.getAllyNumber())>=Paramaters.THRESHOLD_LIKE);
				Permits[i] = (like1 && like2);
			}
			else Permits[i]=false;
		}
	}
	
	@Override
	public void drawing(Graphics g, Container con) {
		//真ん中のキャラ表の作成
		g.setColor(Color.black);
		g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
		Draw(g,con);
		
		//キャラ表の上のメッセージの描画
		String Message="カップルを決定してください";
		//if(CharaList.size()>0)Message=CharaList.get(0).getCharaName()+"と"+Message;
		int font=(int)Math.round(0.04*Paramaters.Height);
		g.setColor(Color.white);
		g.setFont( new Font("明朝体",Font.BOLD,font) );
		int startX=(Paramaters.Width-(font+1)*Message.length())/2;
		g.drawString(Message,startX,(int)Math.round(Pt.y-0.05*Paramaters.Height));
		
		//パラメーターの描画
		g.setFont(new Font("明朝体",Font.BOLD,(int)Math.round(0.05*Paramaters.Height)));
		double dx=0.05;
		String[] Params_String = {"HP:","攻撃:","防御:","命中:","回避:","知力:","運:","移動:"};
		for(int i=0;i<=CharaList.size();i++){
			CharaData chara = null;
			if(i==CharaList.size()){
				if(kettei==-1 || i==2)break;
				chara=Paramaters.AllyList.get(kettei);
			}
			else chara=CharaList.get(i);
			chara.drawFace(g,con,(int)Math.round(dx*Paramaters.Width),(int)Math.round(0.2*Paramaters.Height));
			g.drawString(chara.getCharaName(),(int)Math.round((dx+0.15)*Paramaters.Width),(int)Math.round(0.25*Paramaters.Height));
			int[] NowParams = {chara.getHP(),chara.getAttack(),chara.getDiffence(),chara.getHit(),
					chara.getAvoid(),chara.getIntel(),chara.getRuck(),chara.getMove()};
			for(int j=0;j<NowParams.length;j++){
				g.drawString(Params_String[j]+NowParams[j],
						(int)Math.round(dx*Paramaters.Width), (int)Math.round((0.40+0.07*j)*Paramaters.Height));
			}
			dx+=0.6;
		}
		if(CharaList.size() >= 1){
		    CharaData chara1 = CharaList.get(0);
		    CharaData chara2;
		    if(CharaList.size() == 1)chara2 = Paramaters.AllyList.get(kettei);
		    else chara2 = CharaList.get(1);
		    if(chara1.isMeal() != chara2.isMeal()){
		        g.setColor(Color.yellow);
		        g.drawString("好感度:" + (int)Math.round( 1.0 * chara1.getLikability(chara2.getAllyNumber()) / Paramaters.THRESHOLD_LOVE * 100 ) + "%",
                    (int)Math.round(0.05*Paramaters.Width), (int)Math.round(0.95*Paramaters.Height));
		        g.drawString("好感度:" + (int)Math.round( 1.0 * chara2.getLikability(chara1.getAllyNumber()) / Paramaters.THRESHOLD_LOVE * 100 ) + "%",
                    (int)Math.round(0.65*Paramaters.Width), (int)Math.round(0.95*Paramaters.Height));
		    }
		}
		if(Check!=null) Check.drawing(g, con);
	}

	
	@Override
	public int pushDown() {
		if(Check==null)Moving(true);
		else Check.Moving(true);
		return Paramaters.NON_EVENT;
	}
	
	/** メイドさんの解説コメント番号の取得 */
	public int getComment(){
		switch(CharaList.size()){
		case 0: return 86;
		case 1: if(kettei==-1 || Permits[kettei])return 87;
				if(!Permits[kettei]){
					CharaData chara1 = CharaList.get(0);
					CharaData chara2 = Paramaters.AllyList.get(kettei);
					if(chara1.isMeal()==chara2.isMeal())return 89;
					else return 88;
				}
		case 2: return 90;
		}
		return 1000;
	}

	@Override
	public int pushEnter() {
		if(kettei==-1 || !Permits[kettei])return Paramaters.NON_EVENT;
		int size=CharaList.size();
		if(size<2 && pointToNextScreen(cursorX, cursorY)){
			jumpNextScreen();
			return Paramaters.NON_EVENT;
		}
		switch(size){
		case 0: CharaList.add(Paramaters.AllyList.get(kettei));
				break;
		case 1: CharaList.add(Paramaters.AllyList.get(kettei));
				Check = new EndCheckScene("決定してよろしいですか？");
				break;
		case 2: if(Check==null)return Paramaters.NON_EVENT;
				int num = Check.pushEnter();
				if(num == Paramaters.DECIDE_YES){
					for(CharaData chara : Paramaters.AllyList)chara.formatFaceIndex();
					return Paramaters.CHILD_REGISTER_SCENE;
				}
				else if(num == Paramaters.DECIDE_NO){
					Check=null;
					CharaList.remove(CharaList.size()-1);
				}
		}
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushEscape() {
		if(Check!=null)Check=null;
		if(CharaList.size()==0){
			for(CharaData chara : Paramaters.AllyList)chara.formatFaceIndex();
			return Paramaters.DELETE_NOW_SCENE;
		}
		else CharaList.remove(CharaList.size()-1);
		return Paramaters.NON_EVENT;
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
		if(Check==null) Moving(false);
		else Check.Moving(false);
		return Paramaters.NON_EVENT;
	}

	@Override
	public void running(){
	    String bgm = Paramaters.BGM_LOVERS;
	    if( !bgm.equals(Paramaters.Music_Box.getNowBgmName()) )
	        Paramaters.Music_Box.playMusic(bgm);
		createMenu_Permit();
		//各キャラの顔画像差分の変更
		if(CharaList.size()>0 && kettei!=-1){
			CharaData chara1=CharaList.get(0);
			CharaData chara2=Paramaters.AllyList.get(kettei);
			int num_chara1=chara1.getAllyNumber();
			int num_chara2=chara2.getAllyNumber();
			chara1.changeFaceIndex(num_chara2,chara2.isMeal());
			chara2.changeFaceIndex(num_chara1,chara1.isMeal());
		}
	}
	
	/** CharaListをCharaData配列に変換して返すメソッド */
	public CharaData[] getParents(){
		CharaData[] array=new CharaData[2];
		for(int i=0;i<array.length;i++){
			array[i] = CharaList.get(i);
		}
		return array;
	}

    @Override
    public void moveCursor(int x, int y) {
    	cursorX = x;
    	cursorY = y;
        if(Check==null) decideNowSelectedItem(x,y);
        else Check.moveCursor(x,y);
    }

}
