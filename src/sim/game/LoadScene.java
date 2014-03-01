package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.util.ArrayList;

import sim.game.data.CharaData;
import sim.game.data.EventData;
import sim.tools.Security;
import sim.tools.Select;

public class LoadScene extends Select implements GlobalScene {
	
	public LoadScene(){
		super(0.3,0.15,0.7,0.4,5,Paramaters.FileNumbers);
		createItems();		
	}
	
	/** 各項目の作成 */
	private void createItems(){
		for(int i=0;i<Permits.length;i++){
			String name=Paramaters.FileFoldr+File.separatorChar+Paramaters.FileName_Head+i+Paramaters.FileName_Tail;
			File file = new File(name);
			if(file.exists()){
				Names[i]="ロードデータ"+(i+1);
				Permits[i]=true;
			}
			else{
				Names[i]="データなし";
				Permits[i]=false;
			}
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
		else if(kettei==-1 || !Permits[kettei])return Paramaters.NON_EVENT;
		else{
			Paramaters.whenStart();
			String name=Paramaters.FileFoldr+File.separatorChar+Paramaters.FileName_Head+kettei+Paramaters.FileName_Tail;
			ArrayList<String> list = readFile(name);
			loadData(decryptFromList(list));
			return Paramaters.STRATEGY_SCENE;
		}
	}
	
	/** 引数のファイル名のファイルから文字列リストを取得するメソッド */
	private ArrayList<String> readFile(String fileName){
		try{
			ArrayList<String> list = new ArrayList<String>();
			File file = new File(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));  // ファイルを開く
			String line;
			while((line=br.readLine())!=null){
				list.add(line);
			}
			br.close();
			return list;
		}catch(Exception ex){
			System.out.println("読み込みエラー");
			return null;
		}
	}
	
	/** 引数の文字列リストを復号化するメソッド */
	private ArrayList<String> decryptFromList(ArrayList<String> list){
	    ArrayList<String> decryptedList = new ArrayList<String>();
	    for(String line : list){
	        String str = Security.decrypt(Security.KEY_SAVE_DATA, line);
	        if(str == null)continue;
	        String[] datas = str.split("\n");
	        int charaNum = Integer.parseInt(datas[0]);
	        datas = str.split("\n",charaNum*4+2);
	        for(String data : datas) decryptedList.add(data);
	    }
	    return decryptedList;
	}
	
	/**　引数の文字列リストをロードするメソッド */
	private void loadData(ArrayList<String> list){
		Paramaters.setAllyNumbers(Integer.parseInt(list.get(0)));
		for(int i=1;i<list.size()-1;i+=4){
			String[] DataArray={list.get(i),list.get(i+1),list.get(i+2),list.get(i+3)};
			CharaData chara = new CharaData(DataArray);
			Paramaters.AllyList.add(chara);
		}
		EventData.setFinishedEvents(list.get(list.size()-1));
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
		String Message="データのロード";
		int font=(int)Math.round(0.075*Paramaters.Height);
		g.setFont( new Font("明朝体",Font.BOLD,font) );
		int startX=(Paramaters.Width-(font+1)*Message.length())/2;
		g.drawString(Message,startX,(int)Math.round(Pt.y-0.05*Paramaters.Height));
		Draw(g,con);
	}

	@Override
	public void running(){}

    @Override
    public void moveCursor(int x, int y) {
    	cursorX = x;
    	cursorY = y;
        decideNowSelectedItem(x,y);
    }
}
