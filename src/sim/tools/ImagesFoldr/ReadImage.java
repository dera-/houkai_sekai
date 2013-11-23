package sim.tools.ImagesFoldr;

import java.awt.Image;
import java.awt.Toolkit;

public class ReadImage {
	/** 引数文字列のパスの画像を取得するメソッド */
	public Image getImage(String file){
		Image img=Toolkit.getDefaultToolkit().getImage(getClass().getResource(file));
		return img;
	}
}
