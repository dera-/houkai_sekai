package sim.game.data;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import sim.game.Paramaters;

/** 戦闘mapに関するクラス */
public class Map {
	public static final int CHIP_NUMS_WIDTH = (int)Math.round(1.0*Paramaters.Width/MapChip.Width_Chip);
	public static final int CHIP_NUMS_HEIGHT = (int)Math.round(1.0*Paramaters.Height/MapChip.Height_Chip);
	public static final int HALF_CHIP_WIDTH_MINUS = (int)(-1*Math.floor(1.0*CHIP_NUMS_WIDTH/2));
	public static final int HALF_CHIP_HEIGHT_MINUS = (int)(-1*Math.floor(1.0*CHIP_NUMS_HEIGHT/2));
	public static final int HALF_CHIP_WIDTH_PLUS = (int)Math.round(1.0*CHIP_NUMS_WIDTH/2);
	public static final int HALF_CHIP_HEIGHT_PLUS = (int)Math.round(1.0*CHIP_NUMS_HEIGHT/2);
	
	private static ArrayList<Map> MapList = new ArrayList<Map>();
	public final int Number;  //マップナンバー
	private MapChip[][] Land;  //実際のマッピング
	
	public Map(int n,char[][] land){
		Number=n;
		Land=new MapChip[land.length][land[0].length];
		for(int i=0;i<land.length;i++){
			for(int j=0;j<land[i].length;j++){
				Land[i][j]=MapChip.searchChip(land[i][j]);
			}
		}
	}
	
	/** 外部にMapListを渡すメソッド */
	public static ArrayList<Map> getMapList(){
		return MapList;
	}
	
	/** マップと整数値の照らし合わせ */
	public static Map searchMap(int n){
		for(Map m :MapList){
			if(m.Number==n)return m;
		}
		return null;
	}
	
	/** 引数の座標に対応するマップチップを返すメソッド */
	public MapChip getChip(int x,int y){
		if(x<0 || Land[0].length<=x || y<0 || Land.length<=y) return null;
		else return Land[y][x];
	}
	
	/** マップの横のマスの数を返すメソッド */
	public int getWidth(){
		return Land[0].length;
	}
	
	/** マップの縦のマスの数を返すメソッド */
	public int getHeight(){
		return Land.length;
	}
	
	public Point getPoint(Point pt,int dx, int dy){
	    MapChip chip = getChip(pt.x + dx, pt.y + dy);
	    if(chip == null) return pt;
	    return new Point(pt.x + dx, pt.y + dy);
	}
	
	/** 全てのマスのフラグを解除するメソッド */
	public void deleteFlagsForMap(){
        for(int y=0;y<Land.length;y++){
            for(int x=0;x<Land[y].length;x++){
                Land[y][x].deleteColorFlags();
            }
        }
	}
	
	/** 敵キャラの各到着点にフラグ付けをする */
    public void setFlagsForEnemyReachs(ArrayList<Point> list){
        setFlagsForReachs(list, false, false, true);
    }
    
    /** キャラの各到着点にフラグ付けをする */
    public void setFlagsForReachs(ArrayList<Point> list, boolean black, boolean ally, boolean setFlag){
        for(int y=0;y<Land.length;y++){
            for(int x=0;x<Land[y].length;x++){
                boolean event=false;
                for(Point pt : list){
                    if(x==pt.x && y==pt.y){
                        event=true;
                        break;
                    }
                }
                int num = -1;
                if(event){
                    if(ally) num=Paramaters.MOVABLE_CHIP;
                    else num=Paramaters.ACTIVE_CHIP;
                }
                else if(black) num=Paramaters.NONEVENT_CHIP;
                if(num!=-1) Land[y][x].setColorFlag(num,setFlag);
                else Land[y][x].deleteColorFlags();
            }
        }
    }

}
