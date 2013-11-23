package sim;

import javax.swing.JFrame;

import sim.edit.EditorPanel;
import sim.game.Paramaters;

public class StageEditer extends JFrame {
	
	private EditorPanel panel; //パネル

	public static void main(String[] args) {
		new StageEditer();
	}
	
	public StageEditer(){
		super("崩壊世界ステージエディター");  //タイトル
		panel=new EditorPanel(this.getInsets().top, this.getInsets().left);
		add(panel); //パネルをこのフレームのコンポーネントに加える。
        /*以下でパネルとメニューバーのレイアウトを行う*/
        pack(); // コンポーネントに合わせてフレームサイズを自動設定
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); //ゲーム画面のサイズの固定
        setVisible(true);
        addKeyListener(panel);
        addMouseListener(panel);
        addMouseMotionListener(panel);
        Paramaters.frameTop = this.getInsets().top;
        Paramaters.frameLeft = this.getInsets().left;
	}

}
