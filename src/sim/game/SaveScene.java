package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import sim.game.Paramaters;
import sim.game.data.CharaData;
import sim.game.data.EventData;
import sim.tools.Security;
import sim.tools.Select;

public class SaveScene extends Select implements GlobalScene {
	private String nonData = "データなし";
    
	public SaveScene(){
		super(0.3,0.15,0.7,0.4,5,Paramaters.FileNumbers);
		createItems();
	}
	
	/** 各項目の作成 */
	public void createItems(){
		for(int i=0;i<Permits.length;i++){
			Permits[i]=true;
			String name=Paramaters.FileFoldr+File.separatorChar+Paramaters.FileName_Head+i+Paramaters.FileName_Tail;
			File file = new File(name);
			if(file.exists())Names[i]="セーブデータ"+(i+1);
			else Names[i] = nonData;
		}
	}

	@Override
	public int pushUp() {
		Moving(false);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushDown() {
		Moving(true);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushRight() {
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushLeft() {
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushEnter() {
		if(pointToNextScreen(cursorX, cursorY)){
			jumpNextScreen();
			return Paramaters.NON_EVENT;
		}
		else if(kettei==-1)return Paramaters.NON_EVENT;
		else{
			if(!Names[kettei].equals(nonData)) return Paramaters.CHECK_SAVE;
			else return Paramaters.EXECUTE_SAVE;
		}
	}
	
	/** 現在のデータをセーブするメソッド */
	public void save(){
        String data = createSaveData();
        String encryptedData = Security.encrypt(Security.KEY_SAVE_DATA, data);
        if( encryptedData != null ) writeFile(encryptedData);
	}
	
	/** セーブするデータをStringにして、返り値として返す */
	private String createSaveData(){
	    StringBuilder data = new StringBuilder();
	    data.append(Integer.toString(Paramaters.getAllyNumbers()));
	    data.append("\n");
		//キャラクターのデータをまとめている部分
		for(CharaData C : Paramaters.AllyList){
			String[] charaDatas=C.getDataArray();
			for(int i=0;i<charaDatas.length;i++){
				data.append(charaDatas[i]);
				data.append("\n");
			}
		}
		//イベントのデータをまとめている部分
		data.append(EventData.getFinishedEvents_String());
		return data.toString();
	}
	
	/** 引数の文字列をファイルに保存するメソッド */
	private void writeFile(String data){
		try{
			String filename=Paramaters.FileFoldr+File.separatorChar+Paramaters.FileName_Head+kettei+Paramaters.FileName_Tail;
			FileWriter writer = new FileWriter(filename);
			PrintWriter pWriter = new PrintWriter(writer);
			pWriter.println(data);
			writer.close();
		}catch(Exception ex){}
	}
	

	@Override
	public int pushEscape() {
		return Paramaters.DELETE_NOW_SCENE;
	}

	@Override
	public void drawing(Graphics g, Container con) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
		g.setColor(Color.white);
		String Message="データのセーブ";
		int font=(int)Math.round(0.075*Paramaters.Height);
		g.setFont( new Font("明朝体",Font.BOLD,font) );
		int startX=(Paramaters.Width-(font+1)*Message.length())/2;
		g.drawString(Message,startX,(int)Math.round(Pt.y-0.05*Paramaters.Height));
		Draw(g,con);
	}

	@Override
	public void running() {}

    @Override
    public void moveCursor(int x, int y) {
    	cursorX = x;
    	cursorY = y;
        decideNowSelectedItem(x,y);
    }

}
