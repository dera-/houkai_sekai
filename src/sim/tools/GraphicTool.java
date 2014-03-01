package sim.tools;

import java.awt.*;
import java.awt.image.*;

import sim.game.Paramaters;

/** このゲームにおいて頻繁に使用する描画パターンをまとめたクラス */
public class GraphicTool {
	private Color MenuItemColor=Color.yellow;  //選択肢の文字列の色
		
	/**指定された矩形を透過する　*/
	public void setTransmission(Graphics g,int x,int y,int wide,int high,Color col,float t){
		Graphics2D g2 = (Graphics2D) g;
		Composite comp_defo=g2.getComposite();	
		// アルファ値
		AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, t);
		// アルファ値をセット（以後の描画は半透明になる）
		g2.setComposite(composite);
		g2.setColor(col);
		g2.fillRect(x,y,wide,high);
		g2.setComposite(comp_defo);
	}
	
	/** 選択画面自体の描画 */
	public void drawSelect(Graphics g,Container con,int x,int y,int width,int height,
			String[] strs,boolean oks[],int now){
		Color[] colors = new Color[strs.length];
		for(int i=0;i<colors.length;i++)colors[i]=MenuItemColor;
		drawSelect(g,con,x,y,width,height,strs,oks,now,colors);
	}
	
	/** 選択画面自体の描画 */
	public void drawSelect(Graphics g,Container con,int x,int y,int width,int height,
			String[] strs,boolean oks[],int now,Color[] cols){
		g.setColor(Color.black);
		g.fillRect(x, y, width, height);
		drawChoices(g,x,y,width,height,strs,oks,cols);
		drawNowDecide(g,x,y,width,height,strs.length,now);
		drawFrame(g,con,x,y,width,height);
		drawBorder(g,con,x,y,width,height,strs.length);
	}
	
	/** 1列の表の描画 */
	public void drawTable(Graphics g,Container con,int x,int y,int width,int height,String[] strs,boolean border){
		g.setColor(Color.black);
		g.fillRect(x, y, width, height);
		boolean[] flags = new boolean[strs.length];
		for(int i=0;i<flags.length;i++){
			flags[i]=true;
		}
		drawChoices(g,x,y,width,height,strs,flags,Color.white);
		drawFrame(g,con,x,y,width,height);
		if(border)drawBorder(g,con,x,y,width,height,strs.length);
	}
	
	/** 1列の表の描画（区切り線あり） */
	public void drawTable(Graphics g,Container con,int x,int y,int width,int height,String[] strs){
		drawTable(g,con,x,y,width,height,strs,true);
	}
	
	/** 枠を描画する */
	public void drawFrame(Graphics g,Container con,int x,int y,int width,int height){
		BufferedImage corners[]=new BufferedImage[4];
		for(int i=0;i<corners.length;i++){
			corners[i]=Paramaters.Image_BOX.getBufferdImage("WindowParts"+(i+4));
		}
		int CornerSize=corners[0].getWidth();
		for(int a=0;a<4;a++){
			BufferedImage img=null;
			int length=0;
			int size=0;
			int StartX=0;
			int StartY=0;
			switch(a){
			   case 0:
				   length=width;
				   img=Paramaters.Image_BOX.getBufferdImage("WindowParts0");
				   size=img.getWidth();
				   break;
			   case 1:
				   length=height;
				   img=Paramaters.Image_BOX.getBufferdImage("WindowParts1");
				   size=img.getHeight();
				   break;
			   case 2:
				   length=width;
				   img=Paramaters.Image_BOX.getBufferdImage("WindowParts2");
				   size=img.getWidth();
				   StartY=height-img.getHeight();
				   break;
			   case 3:
				   length=height;
				   img=Paramaters.Image_BOX.getBufferdImage("WindowParts3");
				   size=img.getHeight();
				   StartX=width-img.getWidth();
				   break;
				}
			int finish = length-CornerSize;
			for(int i=CornerSize;i<=finish;i+=size){
				if(a%2==0)g.drawImage(img,x+i+StartX,y+StartY,con);
				else g.drawImage(img,x+StartX,y+i+StartY,con);
			}
		}
		//4隅の描画
		g.drawImage(corners[0],x,y,con);
		g.drawImage(corners[1],x,y+height-CornerSize,con);
		g.drawImage(corners[2],x+width-CornerSize,y+height-CornerSize,con);
		g.drawImage(corners[3],x+width-CornerSize,y,con);
	}
	
	/** 区切り線の描画 */
	private void drawBorder(Graphics g,Container con,int x,int y,int width,int height,int nums){
		BufferedImage img=Paramaters.Image_BOX.getBufferdImage("WindowParts0");
		int size=img.getHeight();
		int length=img.getWidth();
		int bun=(int)Math.round(1.0*(height-size)/nums);
		for(int i=1;i<nums;i++){
	        for(int j=0; j<=width-size; j+=length){
	            g.drawImage(img,x+j,y+bun*i,con);
	        }
		}
	}
	
	/** 横線の描画 */
	public void drawHorizonLine(Graphics g,Container con,int x,int y,int width){
	    BufferedImage img=Paramaters.Image_BOX.getBufferdImage("WindowParts0");
	    int length=img.getWidth();
        for(int j=0;j<=width;j+=length){
            g.drawImage(img,x+j,y,con);
        }
	}
	
	/** 選択肢の描画(色の種類は一つで統一) */
	private void drawChoices(Graphics g,int x,int y,int width,int height,String[] strs,boolean[] oks,Color col){
		Color[] colors = new Color[strs.length];
		for(int i=0;i<colors.length;i++)colors[i]=col;
		drawChoices(g,x,y,width,height,strs,oks,colors);
	}
	
	/**　選択肢の描画　*/
	private void drawChoices(Graphics g,int x,int y,int width,int height,String[] strs,boolean[] oks,Color[] colors){
		BufferedImage img=Paramaters.Image_BOX.getBufferdImage("WindowParts0");
		int size=img.getHeight();
		int bun=(int)Math.round(1.0*(height-size)/strs.length);
		for(int i=0;i<strs.length;i++){
			g.setFont(new Font("明朝体",Font.BOLD,
					getFontSize(bun-size , (int)Math.round( 1.0*(width-2*size)/strs[i].length() )) ));
			g.setColor(colors[i]);
			g.drawString(strs[i], x+size, (int)Math.round(y+bun*(i+1)-0.1*bun));
			if(!oks[i])setTransmission(g,x,y+bun*i,width,bun,Color.black,0.7f);
		}
	}	
	
	/** 選択バーの描画 */
	private void drawNowDecide(Graphics g,int x,int y,int width,int height,int nums,int now){
		if(now==-1)return;
		BufferedImage img=Paramaters.Image_BOX.getBufferdImage("WindowParts0");
		int size=img.getHeight();
		int bun=(int)Math.round(1.0*(height-size)/nums);
		setTransmission(g,x,y+bun*now,width,bun,Color.green,0.5f);		
	}
	
	/** 文字フォントのサイズを返す */
	private int getFontSize(int h,int w){
		if(h>w)return w-3;
		else return h-3;
	}
	
	/** 文字の色を引数の色に変更する */
	public void changeMenuItemColor(Color col){
		MenuItemColor=col;
	}
	
	/** 文字の色を返すメソッド */
	public Color getMenuItemColor(){
	    return MenuItemColor;
	}
	
}
