package sim.game.battle;

import java.awt.Container;
import java.awt.Graphics;

public interface Scene {
	/** 上ボタンを押した時の処理 */
	public boolean keyEventUp();
	
	/** 下ボタンを押した時の処理 */
	public boolean keyEventDown();
	
	/** 右ボタンを押した時の処理 */
	public boolean keyEventRight();
	
	/** 左ボタンを押した時の処理 */
	public boolean keyEventLeft();
	
	/** エンターボタンを押した時の処理 */
	public boolean keyEventEnter();
	
	/** エスケープボタンを押した時の処理 */
	public boolean keyEventEscape();
	
	/** 描画処理 */
	public void draw(Graphics g,Container con);
	
	/** 並行処理 */
	public void run();
	
	/** マウスを動かした時の処理 */
	public void moveCursor(int x, int y);
}
