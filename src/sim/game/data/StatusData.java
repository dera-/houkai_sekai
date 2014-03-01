package sim.game.data;

import java.awt.Container;
import java.awt.Graphics;

import sim.game.Paramaters;
import sim.game.battle.SkillMenu;

public class StatusData {
	protected int Number; //現在どのステータス画面を見ているのか表している番号
	protected CharaData Chara; //現在選択しているキャラクター
	protected SkillMenu Menu_Skill;
	
	public StatusData(CharaData C){
		Number=Paramaters.FirstNumber;
		Chara=C;
		Menu_Skill=new SkillMenu(C);
	}
	
	/** 技画面を見ているときに使用するメソッド。カーソルを動かす。 */
	public void ViewSkill(boolean ahead){
		if(Number == Paramaters.SkillStatus){
			Menu_Skill.Moving(ahead);
		}
	}
	
	   /** 技画面を見ているときに使用するメソッド。カーソルを動かす。 */
    public void moveCursorInSkillMenu(int x, int y){
        if(Number == Paramaters.SkillStatus){
            Menu_Skill.moveCursor(x, y);
        }
    }
	
	/** 画面を切り替えるメソッド */
	public void changeScene(boolean ahead){
		if(ahead){
			if(Number>=Paramaters.LastNumber)Number=Paramaters.FirstNumber;
			else Number++;
		}
		else{
			if(Number<=Paramaters.FirstNumber)Number=Paramaters.LastNumber;
			else Number--;
		}
	}
	
	/** 画面描画メソッド */
	public void drawScene(Graphics g, Container con){
		switch(Number){
		case Paramaters.BasicStatus:
			Chara.drawStatus(g, con, 0, 0, Paramaters.Width, Paramaters.Height);
			break;
		case Paramaters.JobStatus:
			Chara.drawAboutJob(g, con, 0, 0, Paramaters.Width, Paramaters.Height);
			break;
		case Paramaters.SkillStatus:
			Menu_Skill.draw(g, con);
			break;
		}
	}
	
	/** メイドさんの解説コメントの番号を取得するメソッド */
	public int getComment(){
		switch(Number){
		case Paramaters.BasicStatus:
			return 71;
		case Paramaters.JobStatus:
			return 73;
		case Paramaters.SkillStatus:
			return 74;
		default: return 1000;
		}
	}
	
}
