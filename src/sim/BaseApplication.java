package sim;

import java.awt.Container;
import java.awt.Graphics;

public interface BaseApplication {
	public static final int PUSH_UP = 0;
	public static final int PUSH_DOWN = 1;
	public static final int PUSH_LEFT = 2;
	public static final int PUSH_RIGHT = 3;
	
	/** 決定ボタンを押した時の挙動 
	 * 連続押し防止のためにsynchronized修飾子をつけた。
	 * 返り値は、シーンが切り替わったかどうか
	 **/
	public boolean decide();
	
	/** キャンセルボタンを押した時の挙動
	 * 連続押し防止のためにsynchronized修飾子をつけた。
	 * 返り値は、シーンが切り替わったかどうか
	 **/
	public boolean cancel();
	
	/** 方向キーを押した時の挙動 */
	public boolean direction(int k);
	
	public void moveCursor(int x, int y);
	
	public void mouseDragged(int x, int y);
	
	public void mousePressed(int x, int y);
	
	public void mouseReleased(int x, int y);
	
	/** ゲーム画面の描画 */
	public void draw(Graphics g,Container con);
	
	/** 別スレッドで行う処理 */
	public void running();
}
