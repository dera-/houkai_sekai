package sim.tools.BufferedImagesFoldr;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class ReadBufferedImage {
	/** 引数文字列のパスの画像を取得するメソッド */
	public BufferedImage getBufferedImage(String file){
		try{
			BufferedImage img=ImageIO.read(getClass().getResourceAsStream(file));
			return img;
		}catch(Exception ex){
			return null;
		}
	}

}
