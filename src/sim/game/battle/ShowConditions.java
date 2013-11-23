package sim.game.battle;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;

import sim.game.Paramaters;
import sim.game.data.EventData;

public class ShowConditions implements Scene {

    @Override
    public boolean keyEventUp() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyEventDown() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyEventRight() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyEventLeft() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyEventEnter() {
        return true;
    }

    @Override
    public boolean keyEventEscape() {
        return true;
    }

    @Override
    public void draw(Graphics g, Container con) {
        g.setColor(new Color(0,0,0,150));
        g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
        g.setColor(Color.white);
        String[] strs = EventData.getConditions();
        String[] defs = {"勝利条件","敗北条件"};
        double dy=0.1;
        for(int i=0;i<defs.length;i++){
            g.setFont(new Font("明朝体",Font.BOLD,(int)Math.round(0.05*Paramaters.Height)));
            g.drawString( defs[i], (int)Math.round(0.05*Paramaters.Width), (int)Math.round(dy*Paramaters.Height) );
            dy+=0.1;
            g.setFont(new Font("明朝体",Font.BOLD,(int)Math.round(0.03*Paramaters.Height)));
            g.drawString( strs[i], (int)Math.round(0.05*Paramaters.Width), (int)Math.round(dy*Paramaters.Height) );
            dy+=0.2;
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void moveCursor(int x, int y) {}

}
