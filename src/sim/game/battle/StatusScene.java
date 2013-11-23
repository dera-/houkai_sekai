package sim.game.battle;

import java.awt.Container;
import java.awt.Graphics;

import sim.game.Paramaters;
import sim.game.data.CharaData;
import sim.game.data.StatusData;

public class StatusScene extends StatusData implements Scene {
	
	public StatusScene(CharaData C){
		super(C);
	}

	@Override
	public void draw(Graphics g, Container con) {
		drawScene(g,con);
	}

	@Override
	public boolean keyEventDown() {
		ViewSkill(true);
		return false;
	}

	@Override
	public boolean keyEventEnter() {
		return false;
	}

	@Override
	public boolean keyEventEscape() {
		return true; //Escキーが押されたらこの画面を終了する
	}

	@Override
	public boolean keyEventLeft() {
		changeScene(false);
		return false;
	}

	@Override
	public boolean keyEventRight() {
		changeScene(true);
		return false;
	}

	@Override
	public boolean keyEventUp() {
		ViewSkill(false);
		return false;
	}

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void moveCursor(int x, int y) {
		moveCursorInSkillMenu(x,y);
	}
}
