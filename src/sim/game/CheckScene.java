package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;

import sim.tools.Select;

public class CheckScene extends Select implements GlobalScene {
    private final int eventNumber;
    private String message;
    
    public CheckScene(int eventNum,String mes){
        super(0.45,0.5,0.1,0.1,2,2);
        Names[0]="はい";
        Names[1]="いいえ";
        Permits[0]=true;
        Permits[1]=true;
        message=mes;
        eventNumber = eventNum;
    }

    @Override
    public int pushUp() {
        Moving(false);
        return Paramaters.NON_EVENT;
    }

    @Override
    public int pushDown() {
        Moving(true);
        return Paramaters.NON_EVENT;
    }

    @Override
    public int pushRight() {
        return Paramaters.NON_EVENT;
    }

    @Override
    public int pushLeft() {
        return Paramaters.NON_EVENT;
    }

    @Override
    public int pushEnter() {
        switch(kettei){
        case -1:
            return Paramaters.NON_EVENT;
        case 0:
            return eventNumber;
        default:
            return Paramaters.DELETE_NOW_SCENE;
        }
    }

    @Override
    public int pushEscape() {
        return Paramaters.DELETE_NOW_SCENE;
    }

    @Override
    public void drawing(Graphics g, Container con) {
        Tools.setTransmission(g,0,0,Paramaters.Width,Paramaters.Height,Color.black,(float)0.5);
        Draw(g,con);
        g.setColor(Color.white);
        int font=30;
        g.setFont(new Font("明朝体",Font.BOLD,font));
        int startX=(Paramaters.Width-(font+1)*message.length())/2;
        g.drawString(message,startX,(int)Math.round(Pt.y-0.05*Paramaters.Height));
    }

    @Override
    public void running() {
        // TODO Auto-generated method stub

    }

    @Override
    public void moveCursor(int x, int y) {
        decideNowSelectedItem(x,y);
    }

}
