package sim.tools;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class MusicSet {
    private MusicSTART start;
    
    //staticじゃないけど、実質staticなものとして扱う。(SE用メソッド)
    public void playSe(String file){
    	AudioClip se=Applet.newAudioClip(getClass().getResource(file));
    	se.play();
	}
    
    public void playMusic(String file){
    	if(start!=null)stopMusic();
        start=new MusicSTART(file);
    }
    
    public void stopMusic(){
    	if(start!=null){
    		try{
    			start.stopMusic();
    		}catch(Exception ex){}
		}
    	start=null;
    }
    
    public String getNowBgmName(){
        return start == null ? null : start.getNowBgmName();
    }
}
class MusicSTART extends Thread{
    private Player player;
    private BufferedInputStream stream;
    private static final String FOLDR_BGM = "bgm";
    private String Song;
    private boolean LOOP=true;
    
    public MusicSTART(String s){
        Song=s;
        start();
    }
    
    public void playMusic() throws JavaLayerException, FileNotFoundException {
        stream = new BufferedInputStream((new FileInputStream(FOLDR_BGM + File.separatorChar + Song)));
        player = new Player(stream);
        player.play();
    }
    
    public void stopMusic() throws IOException {
        Song=null;
        if (player != null) {
            player.close();
            player=null;
        }
        if (stream != null) {
            stream.close();
            stream=null;
        }
        LOOP=false;
    }
    
    public void run(){
        while(LOOP){
            if(Song!=null) {
                try{
                    playMusic();
                }catch(Exception ex){
                    try{
                        stopMusic();
                    }catch(Exception e){
                        Song=null;
                        LOOP=false;
                    }
                    //ex.printStackTrace();
                }
            }
        }
    }
    
    //BGMの曲名を返す
    public String getNowBgmName(){
        return Song;
    }
}
