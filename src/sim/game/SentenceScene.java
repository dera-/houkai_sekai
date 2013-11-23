package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;

import sim.game.data.Sentence;

/** 文章垂れ流しクラス */
public class SentenceScene implements GlobalScene {
    private Sentence sentenceData;
    private final int nextEvent;
    private String bgmFileName;
    private int nowIndex = 0;
    private String nowSentence;
    private final char nextChar= '>';
    private final int maxLength = 3;
    private final long intervalTime = 500;
    private String arrowString = "";
    private long beforeTime = System.currentTimeMillis();
    
    
    public SentenceScene(int num, int next, String bgm){
        sentenceData = Sentence.getSentence(num);
        nextEvent = next;
        bgmFileName = bgm;
        nowSentence = sentenceData.getOneSentence(nowIndex);
    }

    @Override
    public int pushUp() {
        return Paramaters.NON_EVENT;
    }

    @Override
    public int pushDown() {
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
        nowIndex++;
        if(sentenceData.isFinished(nowIndex)) return nextEvent;
        nowSentence = sentenceData.getOneSentence(nowIndex);
        return Paramaters.NON_EVENT;
    }

    @Override
    public int pushEscape() {
        return Paramaters.NON_EVENT;
    }

    @Override
    public void drawing(Graphics g, Container con) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Paramaters.Width, Paramaters.Height);
        
        //右下の矢印
        g.setColor(Color.white);
        g.setFont(new Font("明朝体",Font.BOLD,20));
        g.drawString(arrowString, (int)Math.round(0.85*Paramaters.Width), (int)Math.round(0.9*Paramaters.Height));
        
        //文章の描画
        if(nowSentence == null)return;
        int fontDetails = 25;
        g.setFont(new Font("",Font.PLAIN,fontDetails));
        g.setColor(Color.yellow);
        char[] charArray = nowSentence.toCharArray();
        int charNumsOneLine = (int) Math.round(0.8 * Paramaters.Width / fontDetails);
        int lineNums = charArray.length / charNumsOneLine +1;
        int startY = (int)Math.round(0.35*Paramaters.Height);
        for(int line=0; line<lineNums; line++){
            String subString;
            if(line == lineNums-1) subString = nowSentence.substring(line*charNumsOneLine);
            else subString = nowSentence.substring(line*charNumsOneLine, (line+1)*charNumsOneLine);
            g.drawString(subString, (int) Math.round(0.1 * Paramaters.Width), startY+line*fontDetails);
        }
    }

    @Override
    public void running() {
        if( bgmFileName != null && !bgmFileName.equals(Paramaters.Music_Box.getNowBgmName()) )
            Paramaters.Music_Box.playMusic(bgmFileName);
        
        long now = System.currentTimeMillis();
        if(now-beforeTime < intervalTime)return;
        createArrowString();
        beforeTime = System.currentTimeMillis();
    }
    
    private void createArrowString(){
        int length = arrowString.length();
        if(length >= maxLength) arrowString = "";
        else arrowString += nextChar;
    }

    @Override
    public void moveCursor(int x, int y) {}

}
