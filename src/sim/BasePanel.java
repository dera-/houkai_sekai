package sim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import sim.game.Paramaters;

public abstract class BasePanel extends JPanel implements Runnable,KeyListener,MouseListener,MouseMotionListener{
	protected BaseApplication application;
	private Thread thr; //別スレッド
	private Image offscreenImg; //裏イメージ
	private ArrayList<Integer> InputList = new ArrayList<Integer>(); //入力したキーの番号を格納するリスト。FIFO方式。
	
	public BasePanel(int frame_top, int frame_left){
		setPreferredSize(new Dimension(Paramaters.Width, Paramaters.Height));  //ゲーム画面のサイズの設定
		setBackground(Color.white);
		initializeFields();
		startThread();
	}
	
	protected abstract void initializeFields();
	
	private void startThread(){
		if(thr != null) return;
		thr = new Thread(this); //別スレッドの作成
		thr.start(); //別スレッドを起動する。
	}
	
	//スレッドを止める
	public void stopThread(){
		thr=null;
	}
	
	//repaintメソッドが呼び出すメソッド
	public final void update(Graphics g){
		paint(g);
	}
	
	//描画を行うメソッド
	public final void paint(Graphics g){
		//裏イメージを作成する
		if(offscreenImg==null){
			offscreenImg=createImage(Paramaters.Width,Paramaters.Height);
			Paramaters.Image_BOX.loadWait(offscreenImg, this);
		}
		//裏イメージを背景色で塗りつぶす
		Graphics offscreenG;
		offscreenG = offscreenImg.getGraphics();
		offscreenG.setColor(getBackground());
        offscreenG.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
        //裏イメージにゲーム画面の描画を行う。
        offscreenG.setColor(Color.black);
        draw(offscreenG);
        //裏イメージを表イメージに貼り付ける
        g.drawImage(offscreenImg,0,0,this);
	}
	
	//実際の描画を行う
	protected void draw(Graphics g){
		if(application == null) return;
		application.draw(g, this);
	}
	
	@Override
	//別スレッドが行う処理
	public final void run() {
	    beforeThreadLoop();
		while(thr!=null){
			keyPushEvent();
			loopAction();
			try{
				Thread.sleep(33); //30フレーム/秒で動かす。
			}catch(Exception ex){}
			repaint();
		}
	}
	
	protected abstract void beforeThreadLoop();
	
	protected abstract void loopAction();
	
	private final void keyPushEvent(){
		if(application == null || InputList.size() == 0) return;
		int num=InputList.remove(0);
		boolean change=false; //シーンが切り替わるかどうかを表す変数
		switch(num){
			//エンターキーが押された時の処理
			case KeyEvent.VK_ENTER:
				change = application.decide();
				break;
			//エスケープキーが押された時の処理
			case KeyEvent.VK_ESCAPE:
				change = application.cancel();
				break;
			//↑キーが押された時の処理
			case KeyEvent.VK_UP:
				change = application.direction(BaseApplication.PUSH_UP);
				break;
			//↓キーが押された時の処理
			case KeyEvent.VK_DOWN:
				change = application.direction(BaseApplication.PUSH_DOWN);
				break;
			//←キーが押された時の処理
			case KeyEvent.VK_LEFT:
				change = application.direction(BaseApplication.PUSH_LEFT);
			   	break;
			//→キーが押された時の処理
			case KeyEvent.VK_RIGHT:
				change = application.direction(BaseApplication.PUSH_RIGHT);
				break;
			default:
				break;
		}
		if(change)InputList.clear();  //シーンが切り替わった場合InputListの中身を全て削除する。
	}
		
	//キーリスナーメソッド。キーが押された時に呼び出されるメソッド。
	public void keyPressed(KeyEvent k){
		int key = k.getKeyCode();	//キーコードというものを取得する
		switch(key){
		//↑キー
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W:
			InputList.add(KeyEvent.VK_UP);
			break;
		//↓キー
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S:
			InputList.add(KeyEvent.VK_DOWN);
			break;
		//→キー
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:
			InputList.add(KeyEvent.VK_RIGHT);
			break;
		//←キー
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A:
			InputList.add(KeyEvent.VK_LEFT);
			break;
		default: break;
		}
	}
	
	//キーリスナーメソッド。キーが離された時に呼び出されるメソッド。
	public void keyReleased(KeyEvent k){
		int key = k.getKeyCode();	//キーコードというものを取得する
		switch(key){
			//エンターキー
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_Z:
				InputList.add(KeyEvent.VK_ENTER);
			    break;
			//エスケープキー
			case KeyEvent.VK_ESCAPE:
			case KeyEvent.VK_X:
				InputList.add(KeyEvent.VK_ESCAPE);
				break;
			default: break;
		}
	}
	
	//キーリスナーメソッド。
	public void keyTyped(KeyEvent k){}

    @Override
    public void mouseDragged(MouseEvent arg0) {
    	if(application == null) return;
    	Point place = getCursorPoint(arg0);
    	application.mouseDragged(place.x, place.y);
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
    	if(application == null) return;
    	Point place = getCursorPoint(arg0);
    	application.moveCursor(place.x, place.y);
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        int buttonNum = arg0.getButton();
        switch(buttonNum){
            case MouseEvent.BUTTON1:
            	InputList.add(KeyEvent.VK_ENTER);
                break;
            case MouseEvent.BUTTON3:
            	InputList.add(KeyEvent.VK_ESCAPE);
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {}

    @Override
    public void mouseExited(MouseEvent arg0) {}

    @Override
    public void mousePressed(MouseEvent arg0) {
    	if(application == null) return;
    	Point place = getCursorPoint(arg0);
    	application.mousePressed(place.x, place.y);
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
    	if(application == null) return;
    	Point place = getCursorPoint(arg0);
    	application.mouseReleased(place.x, place.y);
    }
    
    protected Point getCursorPoint(MouseEvent arg0){
    	int x = arg0.getX()-Paramaters.frameLeft;
    	int y = arg0.getY()-Paramaters.frameTop;
    	return new Point(x, y);
    }

}
