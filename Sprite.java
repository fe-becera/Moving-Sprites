import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprite {

    private static BufferedImage archerSpriteSheet;
    private static BufferedImage barbarianSpriteSheet;
    private static BufferedImage horsemanSpriteSheet;
    private static final int TILE_SIZE = 32;

    public static BufferedImage loadSprite(String file) {

        BufferedImage sprite = null;

        try {
            sprite = ImageIO.read(new File("res/" + file + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sprite;
    }

    public static BufferedImage getSprite(int type, int xGrid, int yGrid) {
	switch(type){
		case 0:
			if (archerSpriteSheet == null) {
			    archerSpriteSheet = loadSprite("AnimationSpriteSheet");
			}
			return archerSpriteSheet.getSubimage(xGrid * TILE_SIZE, yGrid * TILE_SIZE, TILE_SIZE, TILE_SIZE);

		case 1:
			if (barbarianSpriteSheet == null) {
			    barbarianSpriteSheet = loadSprite("AnimationSpriteSheet2");
			}
			return barbarianSpriteSheet.getSubimage(xGrid * TILE_SIZE, yGrid * TILE_SIZE, TILE_SIZE, TILE_SIZE);

		case 2:
			if (horsemanSpriteSheet == null) {
			    horsemanSpriteSheet = loadSprite("AnimationSpriteSheet3");
			}
			return horsemanSpriteSheet.getSubimage(xGrid * TILE_SIZE, yGrid * TILE_SIZE, TILE_SIZE, TILE_SIZE);
		
	}
	return null;
    }

}
