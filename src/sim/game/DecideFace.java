package sim.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import sim.game.Paramaters;
import sim.game.data.CharaData;

/** キャラクリエイト時の顔の選択時の画面に関するクラス */
public class DecideFace implements GlobalScene {
	private CharaData NowChara;  //現在対象にしているキャラ
	private ArrayList<String> FaceList;  //キャラの顔画像名のリスト
	private int Xpoint=0;
	private int Ypoint=0;
	private final int Xsize=8;  //横に並べる顔画像の数
	private final int Ysize=6;  //縦に並べる顔画像の数
	private final int faceGraphicWidth = 98;
	private final int faceGraphicHeight = 98;
	
	public DecideFace(CharaData C){
		NowChara = C;
		FaceList = Paramaters.Image_BOX.getFaceNameList(C.isMeal());
	}

	@Override
	//描画メソッド
	public void drawing(Graphics g, Container con) {
		int dx=0;
		int dy=0;
		for(int y=0;y<Ysize;y++){
			int h=0;
			dx=0;
			dy+=2;
			for(int x=0;x<Xsize;x++){
				dx+=2;
				int num=y*Xsize+x;
				if(num>=FaceList.size())continue;
				BufferedImage img=Paramaters.Image_BOX.getBufferdImage(FaceList.get(num)+Paramaters.FacePicture+0);
				g.drawImage(img,dx,dy,con);
				if(x==Xpoint && y==Ypoint){
					g.setColor(new Color(0,150,0,150));
					g.fillRect(dx, dy, img.getWidth(), img.getHeight());
				}
				if(x==0)h=img.getHeight();
				dx+=img.getWidth();
			}
			dy+=h;
		}
	}

	@Override
	public int pushDown() {
		Moving(0,1);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushEnter() {
		String face = FaceList.get(Xsize*Ypoint+Xpoint);
		if(face!=null)NowChara.changeFace(face);
		return Paramaters.DELETE_NOW_SCENE;
	}

	@Override
	public int pushEscape() {
		return Paramaters.DELETE_NOW_SCENE;
	}

	@Override
	public int pushLeft() {
		Moving(-1,0);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushRight() {
		Moving(1,0);
		return Paramaters.NON_EVENT;
	}

	@Override
	public int pushUp() {
		Moving(0,-1);
		return Paramaters.NON_EVENT;
	}

	@Override
	public void running() {}
	
	/** カーソルを動かすメソッド */
	private void Moving(int x,int y){
		Xpoint+=x;
		Ypoint+=y;
		if(Xpoint<0)Xpoint=Xsize-1;
		if(Xpoint>=Xsize)Xpoint=0;
		if(Ypoint<0)Ypoint=Ysize-1;
		if(Ypoint>=Ysize)Ypoint=0;
		if(Xsize*Ypoint+Xpoint>=FaceList.size())Moving(x,y);
	}

    @Override
    public void moveCursor(int x, int y) {
        int num_x = x / faceGraphicWidth;
        int num_y = y / faceGraphicHeight;
        if( Xsize*num_y+num_x>=FaceList.size() ) return;
        Xpoint = num_x;
        Ypoint = num_y;
    }
}
