package sim.edit;

import java.awt.Graphics;

import sim.BasePanel;
import sim.game.Game;
import sim.game.Paramaters;

public class EditorPanel extends BasePanel{
	
	public EditorPanel(int frame_top, int frame_left) {
		super(frame_top, frame_left);
	}

	@Override
	protected void initializeFields() {
		application = new EditorScreen();
	}

	@Override
	protected void beforeThreadLoop() {
		Paramaters.format(this);  //ゲームの初期化。様々なデータをテキストから読み込んだりする。
	    if(application != null && application instanceof EditorScreen){
	    	EditorScreen screen = (EditorScreen) application;
	    	screen.setSelectMapScene();
	    }
	}

	@Override
	protected void loopAction() {
		application.running();
	}

}
