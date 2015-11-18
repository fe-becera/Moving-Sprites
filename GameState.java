import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedList;

/**
 * The class that contains the state of the game.
 * The game state refers the current position of the players etc.
 * @author Joseph Anthony C. Hermocilla
 *
 */


public class GameState{
	/**
	 * This is a map(key-value pair) of <player name,NetPlayer>
	 */
	//private Map players=new HashMap();
	private LinkedList<NetPlayer> players = new LinkedList<NetPlayer>();
	public LinkedList<Troop> deployedTroops = new LinkedList<Troop>();
	/**
	 * Simple constructor
	 *
	 */
	public GameState(){}
	
	/**
	 * Update the game state. Called when player moves
	 * @param name
	 * @param player
	 */
	public void update(String name, NetPlayer player){
		//players.put(name,player);
		players.add(player);
	}
	
	/**
	 * String representation of this object. Used for data transfer
	 * over the network
	 */
	public String toString(){
		String retval="";
		/*for(Iterator ite=players.keySet().iterator();ite.hasNext();){
			String name=(String)ite.next();
			NetPlayer player=(NetPlayer)players.get(name);
			retval+=player.toString()+":";
		}*/
		for(int i=0; i<Constants.NUMBER_OF_PLAYERS; i++){
			retval += players.get(i).toString()+":";
		}
		return retval;
	}
	
	/**
	 * Returns the map
	 * @return
	 */
	public LinkedList<NetPlayer> getPlayers(){
		return players;
	}
}
