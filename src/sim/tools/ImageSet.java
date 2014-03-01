package sim.tools;

import java.io.BufferedReader;
import java.io.File;
import java.awt.Container;
import java.awt.color.ColorSpace;
import java.awt.Image;
import java.awt.image.*;
import java.awt.Toolkit;
import java.io.InputStreamReader;
import java.awt.MediaTracker;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.*;

import sim.game.Paramaters;
import sim.tools.BufferedImagesFoldr.ReadBufferedImage;
import sim.tools.ImagesFoldr.ReadImage;

public class ImageSet {
	private HashMap<String,Image> ImageHash;
	private HashMap<String,BufferedImage> BufferedImageHash;
	
	//コンストラクタ
	public ImageSet(){
		ImageHash=new HashMap<String,Image>();
		BufferedImageHash=new HashMap<String,BufferedImage>();
	}
	
	//Imageリストへファイルから読み込まれた要素の追加
	public void addImage(String file,Container con){
		try {
			ReadImage reading=new ReadImage();
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(file)));  // ファイルを開く
            br.readLine(); //ヘッダの読み飛ばし
            String line;
            while((line=br.readLine())!=null){
            	String strs[]=line.split(",");
            	Image img=reading.getImage(strs[0]);
            	loadWait(img,con);
            	ImageHash.put(strs[1],img);
            }
            br.close();
		}catch(Exception ex){}
	}
	
	//BufferedImageリストへファイルから読み込まれた要素の追加
	public void addBafferedImage(String file,Container con){
		try {
			ReadBufferedImage reading=new ReadBufferedImage();
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(file)));  // ファイルを開く
            br.readLine(); //ヘッダの読み飛ばし
            String line;
            while((line=br.readLine())!=null){
            	String strs[]=line.split(",");
            	BufferedImage img = reading.getBufferedImage(strs[0]);
            	if(img==null)continue;
            	loadWait(img,con);
            	int num=Integer.parseInt(strs[1]);
            	switch(num){
            	//ウィンドウの分割
            	case 0: SplitWindow(img);
            			break;
            	//キャラの顔グラの分割
            	case 1: SplitFG(img,strs[2]);
            			break;
            	//キャラのドット絵の分割
            	case 2: SplitDot(img,strs[2]);
            			break;
            	default: break;
            	}
            }
            //System.out.println("Hash Size:"+BufferedImageHash.size());
		}catch(Exception ex){
			System.out.println("画像読み込みエラー");
		}
	}
	
	
	//画像のロード
	public void loadWait(Image img,Container con){
		Image[] array=new Image[1];
		array[0]=img;
		loadWait(array,con);
	}
	
	//画像配列のロード
	public void loadWait(Image[] img,Container con){
	   	MediaTracker track=new MediaTracker(con);
		for(int i=0;i<img.length;i++){
			track.addImage(img[i],1);
		}
		try{
			track.waitForID(1);
		}catch(Exception ex){}
	}
	
	/** ウィンドウ枠画像を分割してBufferedImageHashに格納する */
	private void SplitWindow(BufferedImage img){
		int FrameSize=6;
		int CornerSize=14;
		int WholeSize=64;
		int BorderLength=8;
		
		BufferedImage SPLITS[] = new BufferedImage[8];
		SPLITS[0]=img.getSubimage( WholeSize/2-BorderLength/2 , 0 , BorderLength , FrameSize );
		SPLITS[1]=img.getSubimage( 0 , WholeSize/2-BorderLength/2 , FrameSize , BorderLength );
		SPLITS[2]=img.getSubimage( WholeSize/2-BorderLength/2 , WholeSize-FrameSize , BorderLength , FrameSize );
		SPLITS[3]=img.getSubimage( WholeSize-FrameSize , WholeSize/2-BorderLength/2 , FrameSize , BorderLength);
		SPLITS[4]=img.getSubimage(0, 0, CornerSize, CornerSize);
		SPLITS[5]=img.getSubimage(0, WholeSize-CornerSize, CornerSize, CornerSize);
		SPLITS[6]=img.getSubimage(WholeSize-CornerSize, WholeSize-CornerSize, CornerSize, CornerSize);
		SPLITS[7]=img.getSubimage(WholeSize-CornerSize, 0, CornerSize, CornerSize);
		for(int i=0;i<SPLITS.length;i++){
			BufferedImageHash.put("WindowParts"+i,SPLITS[i]);
		}
	}
	
	/** キャラクターの顔画像を分割してBufferedImageHashに格納する。第2引数はキャラクターの名前 */
	private void SplitFG(BufferedImage img,String name){
		int Width_One=96;
		int Height_One=96;
		int yoko=4;
		int tate=2;
		for(int x=0;x<yoko;x++){
			for(int y=0;y<tate;y++){
				BufferedImage elem=img.getSubimage(Width_One*x, Height_One*y, Width_One, Height_One);
				BufferedImageHash.put(name+Paramaters.FacePicture+(tate*x+y), elem);
			}
		}	
	}
	
	/** キャラクターのドット絵を分割してBufferedImageHashに格納する。 */
	private void SplitDot(BufferedImage img,String name){
		int Width_One=32;
		int Height_One=32;
		int yoko=3;
		int tate=4;
		for(int y=0;y<tate;y++){
			for(int x=0;x<yoko+1;x++){
				BufferedImage elem;
				if(x==yoko){
					BufferedImage B = img.getSubimage(Width_One, Height_One*y, Width_One, Height_One);
					ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
					elem = colorConvert.filter(B, null);
				}
				else elem=img.getSubimage(Width_One*x, Height_One*y, Width_One, Height_One);
				BufferedImageHash.put(name+Paramaters.DotPicture+((yoko+1)*y+x), elem);
			}
		}
	}
	
	/** BufferedImageリストから画像データを取得する */
	public BufferedImage getBufferdImage(String str){
		return BufferedImageHash.get(str);
	}
	
	/** Imageリストから画像データを取得する */
	public Image getImage(String str){
		return ImageHash.get(str);
	}
	
	/** 顔グラの名前リストの取得 */
	public ArrayList<String> getFaceNameList(boolean meal){
		ArrayList<String> list = new ArrayList<String>();
		String Sex;
		if(meal)Sex="男";
		else Sex="女";
		for(String str : BufferedImageHash.keySet()){
			int index;
			if((index=str.lastIndexOf(Paramaters.FacePicture+0))!=-1){
				if(str.lastIndexOf(Sex)!=-1) list.add(str.substring(0, index));
			}
		}
		return list;
	}
	
	/** 顔グラの名前をランダムで取得するメソッド */
	public String getRandomFaceName(boolean meal){
		double random = Paramaters.getRandom();
		ArrayList<String> list = getFaceNameList(meal);
		for(int i=0;i<list.size();i++){
			if(random<1.0*(i+1)/list.size()){
				return list.get(i);		
			}
		}
		return null;
	}
	
	
}
