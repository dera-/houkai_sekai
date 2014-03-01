package sim;

import java.awt.Graphics;

import sim.game.Game;
import sim.game.Paramaters;

//ゲーム画面の描画と別スレッド(アニメーション等)の管理を行うクラス
public class MainPanel extends BasePanel{
	private MaidSystem maid;  //メイドシステム

	public MainPanel(int frame_top, int frame_left) {
		super(frame_top, frame_left);
	}
	
	@Override
	protected void initializeFields() {
	    application = new Game();
	}
	
	//タイトル画面に飛ぶメソッド
	public void jumpStartScene(){
	    if(application != null && application instanceof Game){
	    	Game game = (Game) application;
	    	game.createStartScene();
	    }
	}
	
	//メイドさんシステムを閉じるメソッド
	public void closeMaidSystem(){
		maid.setVisible(false);
	}

	@Override
	protected void beforeThreadLoop() {
	    Paramaters.format(this);  //ゲームの初期化。様々なデータをテキストから読み込んだりする。
	    jumpStartScene();
	    maid = new MaidSystem();
	}

	@Override
	protected void loopAction() {
		application.running();
		if(application != null && application instanceof Game){
			Game game=(Game)application;
			maid.setNowSerif(game.getNowGlobalScene()); //メイドさんのセリフ(説明)の取得
		}
		maid.repaint();  //メイドさんシステムにおける再描画
	}

}
