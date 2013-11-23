package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;

public class WaitScene implements GlobalScene {

    @Override
    public int pushUp() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int pushDown() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int pushRight() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int pushLeft() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int pushEnter() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int pushEscape() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void drawing(Graphics g, Container con) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
        g.setColor(Color.white);
        g.setFont(new Font("明朝体",Font.BOLD,40));
        g.drawString("NowLoading...",(int)Math.round(0.6*Paramaters.Width),(int)Math.round(0.95*Paramaters.Height));
        
    }

    @Override
    public void running() {
        // TODO Auto-generated method stub
    }

    @Override
    public void moveCursor(int x, int y) {}

}
