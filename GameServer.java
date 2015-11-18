import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.awt.event.ActionEvent;

/**
 * The main game server. It just accepts the messages sent by one player to
 * another player
 * @author Joseph Anthony C. Hermocilla
 *
 */

public class GameServer implements Runnable, Constants{
	/**
	 * Placeholder for the data received from the player
	 */	 
	String playerData;
	
	/**
	 * The number of currently connected player
	 */
	int playerCount=0;
	
	/**
	 * The socket
	 */
    DatagramSocket serverSocket = null;
    
    /**
     * The current game state
     */
	GameState game;

	/**
	 * The current game stage
	 */
	int gameStage=WAITING_FOR_PLAYERS;
	
	/**
	 * Number of players
	 */
	int numPlayers;
	int troopSelectionDone = 0;
	/**
	 * The main game thread
	 */
	String middleman="localhost";
	Thread t = new Thread(this);

	Timer sendTroopData = new Timer(500, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(game.deployedTroops.size() > 0){
				String gameUpdate = "TROOP ";
				for(int i=0; i<game.deployedTroops.size(); i++){
					game.deployedTroops.get(i).decide();
					gameUpdate = gameUpdate + game.deployedTroops.get(i).toString() + ":";
				}
				sendToMiddleMan("TOCLIENTS "+gameUpdate);
				//broadcast(gameUpdate);
			}
		}
	});
	
	/**
	 * Simple constructor
	 */
	public GameServer(String middlemanServer){
		this.middleman = middlemanServer;
		this.numPlayers = 0;
		try {
            serverSocket = new DatagramSocket(PORT);
			serverSocket.setSoTimeout(100);
		} catch (IOException e) {
            System.err.println("Could not listen on port: "+PORT);
            System.exit(-1);
		}catch(Exception e){}
		//Create the game state
		game = new GameState();
		
		System.out.println("Game created...");
		
		//Start the game thread
		t.start();
	}
	
	/**
	 * Helper method for broadcasting data to all players
	 * @param msg
	 */
	public void broadcast(String msg){
		for(int i=0; i<NUMBER_OF_PLAYERS; i++)
			send(game.getPlayers().get(i),msg);
	}


	/**
	 * Send a message to a player
	 * @param player
	 * @param msg
	 */
	public void send(NetPlayer player, String msg){
		DatagramPacket packet;	
		byte buf[] = msg.getBytes();		
		packet = new DatagramPacket(buf, buf.length, player.getAddress(),player.getPort());
		try{
			serverSocket.send(packet);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}

	public void sendToMiddleMan(String msg){
		try{
			byte[] buf = msg.getBytes();
			InetAddress address = InetAddress.getByName(middleman);
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, MIDDLEPORT);
			serverSocket.send(packet);
		}catch(Exception e){}
	}
	
	/**
	 * The juicy part
	 */
	public void run(){
		while(true){
						
			// Get the data from players
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try{
     			serverSocket.receive(packet);
			}catch(Exception ioe){}
			
			/**
			 * Convert the array of bytes to string
			 */
			playerData=new String(buf);
			
			//remove excess bytes
			playerData = playerData.trim();
			//if (!playerData.equals("")){
			//	System.out.println("Player Data:"+playerData);
			//}
		
			// process
			if (playerData.startsWith("MESSAGE")){
				//broadcast(playerData);
				sendToMiddleMan("TOCLIENTS "+playerData);
			}
			else{
				switch(gameStage){
					  case WAITING_FOR_PLAYERS:
							//System.out.println("Game State: Waiting for players...");
							if (playerData.startsWith("CONNECT")){
								String tokens[] = playerData.split(" ");
								NetPlayer player=new NetPlayer(tokens[1].trim(),packet.getAddress(),packet.getPort());
								System.out.println("Player connected: "+tokens[1]);
								game.update(tokens[1].trim(),player);
								//send(player, "CONNECTED "+numPlayers);
								sendToMiddleMan("TOCLIENT "+numPlayers+" CONNECTED "+numPlayers);
								numPlayers++;
								if (numPlayers==NUMBER_OF_PLAYERS){
									System.out.println("Select troops.");
									//broadcast("SELECT TROOPS");
									sendToMiddleMan("TOCLIENTS SELECT TROOPS");
									gameStage=WAITING_FOR_TROOPS;
								}
							}
						  break;
					  case WAITING_FOR_TROOPS:
					  		if (playerData.startsWith("TROOPS")){
					  			//String tokens[] = playerData.split(" ");
					  			troopSelectionDone++;
					  		}
					  		if (troopSelectionDone==NUMBER_OF_PLAYERS){
								gameStage=GAME_START;
							}
					  	break;	
					  case GAME_START:
						System.out.println("Game State: START");
						//broadcast("START");
						sendToMiddleMan("TOCLIENTS START");
						gameStage=IN_PROGRESS;
						sendTroopData.start();
						break;
					  case IN_PROGRESS:
						  //System.out.println("Game State: IN_PROGRESS");
						  
						  //Player data was received!
						  /*if (playerData.startsWith("PLAYER")){
							  //Tokenize:
							  //The format: PLAYER <player name> <x> <y>
							  String[] playerInfo = playerData.split(" ");					  
							  String pname =playerInfo[1];
							  //int x = Integer.parseInt(playerInfo[2].trim());
							  //int y = Integer.parseInt(playerInfo[3].trim());
							  //Get the player from the game state
							  NetPlayer player=(NetPlayer)game.getPlayers().get(pname);					  
							  player.setX(x);
							  player.setY(y);
							  //Update the game state
							  game.update(pname, player);
							  //Send to all the updated game state
							  broadcast(game.toString());
						  }*/
						if (playerData.startsWith("DEPLOY")){
							System.out.println(playerData);
							game.deployedTroops.add(new Troop(playerData));
							int playerID = Integer.parseInt(playerData.split(" ")[1]);
							//send(game.getPlayers().get(playerID), playerData);
							sendToMiddleMan("TOCLIENT "+playerID+" "+playerData);

						}
						break;
				}
			}
		}
	}	
	
	
	public static void main(String args[]){
		/*if (args.length != 1){
			System.out.println("Usage: java -jar circlewars-server <number of players>");
			System.exit(1);
		}*/
		if (args.length == 0)
			new GameServer("localhost");
		else
			new GameServer(args[0]);
	}
}

