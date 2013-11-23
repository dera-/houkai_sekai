package sim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import sim.game.Paramaters;

/** ゲームの基盤(フレーム)となるクラス */
public class MainFrame extends JFrame implements ActionListener{
	/**
	 * @param args
	 */
	private MainPanel panel; //パネル
	private static MainFrame thisFrame; //現在のフレーム
	private static final String MENU_ITEM_TITLE = "タイトル";
	
	public static void main(String[] args) {
		new MainFrame();
	}
	
	public MainFrame(){
		super("崩壊世界");  //タイトル
		thisFrame=this;
		panel=new MainPanel(this.getInsets().top, this.getInsets().left);
		add(panel); //パネルをこのフレームのコンポーネントに加える。
		JMenuBar menuBar = createMenuBar(); //メニューバーの作成
        /*以下でパネルとメニューバーのレイアウトを行う*/
        pack(); // コンポーネントに合わせてフレームサイズを自動設定
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false); //ゲーム画面のサイズの固定
        setVisible(true);
        addKeyListener(panel);
        addMouseListener(panel);
        addMouseMotionListener(panel);
        Paramaters.frameTop = this.getInsets().top + menuBar.getHeight();
        Paramaters.frameLeft = this.getInsets().left;
        
        //ウィンドウ右上の×ボタンを押した時の動作の登録
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	confirmExiting();
            }
        });
	}
	
	/**
	 * メニューバーを作成するメソッド
	 */
	private JMenuBar createMenuBar(){
        JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);

        JMenu game = new JMenu("ゲーム");
        menubar.add(game);

        JMenuItem title = new JMenuItem(MENU_ITEM_TITLE);

        //メニューアイテムの追加
        game.add(title);
        
        //イベントリスクの設定
        title.addActionListener(this);
        
        return menubar;
	}
	
	/** ウィンドウを閉じるメソッド */
    public static void confirmExiting() {
        int retValue = JOptionPane.showConfirmDialog(thisFrame, "終了しますか？",
                "ウィンドウの終了", JOptionPane.OK_CANCEL_OPTION);
        if (retValue == JOptionPane.YES_OPTION) {
        	thisFrame.panel.closeMaidSystem();
        	//スレッドストップ処理の追加が必要？
        	thisFrame.panel.stopThread();
        	thisFrame=null;
            System.exit(0);
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getActionCommand() == MENU_ITEM_TITLE){
            panel.jumpStartScene();
        }
    }

}
