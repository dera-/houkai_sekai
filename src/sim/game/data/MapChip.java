package sim.game.data;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import sim.game.Paramaters;
import sim.tools.GraphicTool;

/** 基本マップチップクラス */
public class MapChip{
	private static ArrayList<MapChip> MC_LIST=new ArrayList<MapChip>();
	public static String[] NormalChipArrays={"森林","山岳","砂漠","雪原","水"};
	private static char[] AdvantageousChips = {'g','h','i'};  //有利なマップチップ群
	protected char Chip;
	protected String Name;
	protected double[] Revisions=new double[7];  //順に、HP,攻、防、命、避、知、移。
	protected boolean movable;
	public static final int Width_Chip=32;
	public static final int Height_Chip=32;
	protected Image chipImg;
	protected boolean ColorFlags[]={false,false,false,false}; //0が移動可能マス(青)、1が攻撃可能マス(赤),2が何も関係ないマス(透明な黒),3は配置可能なマスを表している
	protected static GraphicTool tool_g = new GraphicTool();
	
	public MapChip(char c,String n,double[] r,boolean m){
		Chip=c;
		Name=n;
		for(int i=0;i<Revisions.length;i++){
			Revisions[i]=r[i];
		}
		movable=m;
		chipImg=Paramaters.Image_BOX.getImage(Name);
	}
	
	public MapChip(MapChip mc){
		Chip=mc.Chip;
		Name=mc.Name;
		for(int i=0;i<Revisions.length;i++){
			Revisions[i]=mc.Revisions[i];
		}
		movable=mc.movable;
		chipImg=Paramaters.Image_BOX.getImage(Name);	
	}
	
	/** 外部にMC_LISTを返すメソッド */
	public static ArrayList<MapChip> getMC_LIST(){
		return MC_LIST;
	}
	
	/** 文字からマップチップの検索 */
	public static MapChip searchChip(char c){
		for(MapChip mc : MC_LIST){
			if(mc.Chip==c)return new MapChip(mc);
		}
		return null;
	}
	
	/** 引数のマップチップが有利なマップチップかどうか */
	public static boolean isAdvantageous(char c){
		for(int i=0; i<AdvantageousChips.length ;i++){
			if(c==AdvantageousChips[i])return true;
		}
		return false;
	}
	
	/** 文字列からマップチップの検索 */
	public static MapChip searchChip(String str){
		for(MapChip mc : MC_LIST){
			if(str.equals(mc.Name))return new MapChip(mc);
		}
		return null;
	}
	
	/** マップチップを表す文字の取得 */
	public char getChar(){
		return Chip;
	}
	
	public boolean isMovable(){
		return movable;
	}
	
	/** 指定された補正を返す */
	public double getRevisionsElement(int i){
		return Revisions[i];
	}
	
	public void draw(Graphics g,Container con,int x,int y){
		g.drawImage( chipImg , x*Width_Chip , y*Height_Chip , con );
		if(ColorFlags[0] || ColorFlags[1] || ColorFlags[2] || ColorFlags[3]){
			Color col=null;
			if(ColorFlags[0])col=Color.blue;
			else if(ColorFlags[1])col=Color.red;
			else if(ColorFlags[2])col=Color.black;
			else if(ColorFlags[3])col=Color.green;
			tool_g.setTransmission(g, x*Width_Chip, y*Height_Chip, 
				Width_Chip, Height_Chip, col, 0.7f);
		}
	}
	
	public void drawInfo(Graphics g, Container con){
	       int font=20;
	        String name="MAP名:" + getName();
	        String[] params = getRevisions_String();
	        int width=font*name.length();
	        for(int i=0;i<params.length;i++){
	            int l=params[i].length();
	            int size=(int)Math.round(0.8*font*l);
	            if(size>width)width=size;
	        }
	        int height=font*(params.length+2)+(font/2)*(params.length+1);
	        int X=(int)Math.round(0.1*Paramaters.Width);
	        int Y=(int)Math.round(0.4*Paramaters.Height);
	        g.setFont(new Font("明朝体",Font.BOLD,font));
	        g.setColor(new Color(0,0,0,150));
	        g.fillRect(X,Y,width,height);
	        tool_g.drawFrame(g, con, X, Y, width, height);
	        g.setColor(Color.white);
	        g.drawString(name, (int)Math.round(X+0.05*width), Y+font+(font/4));
	        for(int i=0;i<params.length;i++){
	            g.drawString(params[i],(int)Math.round(X+0.05*width),Y+font*(i+2)+(font/2)*(i+1)+(font/4));
	        }
	}
	
	/** マスに指定されたフラグの付与あるいは消去を行う */
	public void setColorFlag(int i,boolean b){
		ColorFlags[i]=b;
	}
	
	/** マスに指定されたフラグをすべて削除するメソッド */
	public void deleteColorFlags(){
	    for(int i=0; i < ColorFlags.length; i++){
	        ColorFlags[i] = false;
	    }
	}
	
	/** 配置可能なマスかどうか判断するメソッド */
	public boolean isUsableChip(){
		return ColorFlags[3];
	}
	
	/** マップチップの名前を返す */
	public String getName(){
		return Name;
	}
	
	/** このマップチップの回復量を返すメソッド */
	public int getRecover(){
		return (int)Revisions[0];
	}
	
	/** マップ効果を文字列にして返すメソッド */
	public String[] getRevisions_String(){
		String[] R_String={"HP","攻撃","防御","命中","回避","知力","移動"};
		ArrayList<String> strList = new ArrayList<String>();
		String str="";
		int num=0;
		int interval=(int)Math.ceil(1.0*Revisions.length/2);
		for(int i=0;i<Revisions.length;i++){
			if(Revisions[i]==0)continue;
			if(!str.equals(""))str+=",";
			if(i==0)str+=(R_String[i]+":"+Revisions[i]+"回復");
			else if(i==Revisions.length-1)str+=(R_String[i]+":"+Revisions[i]+"DOWN");
			else{
				String last="";
				if(Revisions[i]>0) last="UP";
				else last="DOWN";
				str+=(R_String[i]+":"+((int)(100*Revisions[i]))+"%"+last);
			}
			num++;
			if(num>=interval){
				strList.add(str);
				num=0;
				str="";
			}
		}
		if(!str.equals(""))strList.add(str);
		String[] strs=new String[strList.size()];
		for(int i=0;i<strs.length;i++){
			strs[i]=strList.get(i);
		}
		return strs;
	}
	
}
