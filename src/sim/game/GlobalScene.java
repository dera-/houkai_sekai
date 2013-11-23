package sim.game;

import java.awt.Container;
import java.awt.Graphics;

public interface GlobalScene {
	/** 上ボタンを押した時の処理 */
	public int pushUp();
	
	/** 下ボタンを押した時の処理 */
	public int pushDown();
	
	/** 右ボタンを押した時の処理 */
	public int pushRight();
	
	/** 左ボタンを押した時の処理 */
	public int pushLeft();
	
	/** エンターボタンを押した時の処理 */
	public int pushEnter();
	
	/** エスケープボタンを押した時の処理 */
	public int pushEscape();
	
	/** 描画処理 */
	public void drawing(Graphics g,Container con);
	
	/**並行処理 */
	public void running();
	
	/** マウスを動かしたときの処理 */
	public void moveCursor(int x, int y);
}
