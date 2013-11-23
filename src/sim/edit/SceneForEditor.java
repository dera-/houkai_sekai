package sim.edit;

import java.awt.Container;
import java.awt.Graphics;

public interface SceneForEditor {	
	/** エンターボタンを押した時の処理 */
	public int pushEnter();
	
	/** エスケープボタンを押した時の処理 */
	public int pushEscape();
	
	/** 描画処理 */
	public void drawing(Graphics g,Container con);
	
	/** マウスを動かしたときの処理 */
	public void moveCursor(int x, int y);
}
