import java.awt.image.BufferedImage;
/**
 * This class encapsulates a network players
 * @author Joseph Anthony C. Hermocilla
 *
 */

public class Troop {
	/**
	 * Archer (a), Barbarian (b), or Horseman (h)
	 */
	private String type;	
	
	/**
	 * The owner
	 */
	private int owner;
	
	
	/**
	 * The position of troop
	 */
	private int x,y,hp;

	/**
	 * String representation. used for transfer over the network
	 */
	private BufferedImage[] walkingLeft = {Sprite.getSprite(0, 0, 1), Sprite.getSprite(0, 2, 1)}; // Gets the upper left images of my sprite sheet
	private BufferedImage[] walkingRight = {Sprite.getSprite(0, 0, 2), Sprite.getSprite(0, 2, 2)};// Gets the upper left images of my sprite sheet
	private BufferedImage[] walkingUp = {Sprite.getSprite(0, 0, 3), Sprite.getSprite(0, 2, 3)}; //Gets the upper left images of my sprite sheet
	private BufferedImage[] walkingDown = {Sprite.getSprite(0, 0, 0), Sprite.getSprite(0, 2, 0)};	
	private BufferedImage[] standing = {Sprite.getSprite(0, 1, 0)};

	// These are animation states
	private Animation walkLeft = new Animation(walkingLeft, 5);
	private Animation walkRight = new Animation(walkingRight, 5);
	private Animation walkUp = new Animation(walkingUp, 5);
	private Animation walkDown = new Animation(walkingDown, 5);
	private Animation stand = new Animation(standing, 5);

	// This is the actual animation
	public Animation animation = stand;

	public Troop(String data){
		String tokens[] = data.split(" ");
		this.owner = Integer.parseInt(tokens[1]);
		this.type = tokens[2];
		this.x = Integer.parseInt(tokens[3]);
		this.y = Integer.parseInt(tokens[4]);
		this.hp = 60;
		if(type.equals("a")){
			walkingLeft[0] = Sprite.getSprite(0, 0, 1);
			walkingLeft[1] = Sprite.getSprite(0, 2, 1);
			walkingRight[0] = Sprite.getSprite(0, 0, 2);
			walkingRight[1] = Sprite.getSprite(0, 2, 2);
			standing[0] = Sprite.getSprite(0, 1, 0);
		}
		else if(type.equals("b")){
			walkingLeft[0] = Sprite.getSprite(1, 0, 1);
			walkingLeft[1] = Sprite.getSprite(1, 2, 1);
			walkingRight[0] = Sprite.getSprite(1, 0, 2);
			walkingRight[1] = Sprite.getSprite(1, 2, 2);
			standing[0] = Sprite.getSprite(1, 1, 0);
		}
		else if(type.equals("h")){
			walkingLeft[0] = Sprite.getSprite(2, 0, 1);
			walkingLeft[1] = Sprite.getSprite(2, 2, 1);
			walkingRight[0] = Sprite.getSprite(2, 0, 2);
			walkingRight[1] = Sprite.getSprite(2, 2, 2);
			standing[0] = Sprite.getSprite(2, 1, 0);

		}
		walkLeft = new Animation(walkingLeft, 5);
		walkRight = new Animation(walkingRight, 5);
		stand = new Animation(standing, 5);
		animation = walkRight;
		animation.start();
		
	}
	
	public void decide(){
		if(x > 650){
			animation.stop();
		}
		else{
			this.x = this.x + 20;
			animation.update();
		}
	}

	public String toString(){
		return owner + " " + type + " " + x + " " + y;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getOwner(){
		return owner;
	}
}
