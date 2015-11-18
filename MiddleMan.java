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

public class MiddleMan implements Runnable, Constants{
	
	/**
	 * The socket
	 */
	String receivedData;
	GameState game;
    DatagramSocket serverSocket = null;
	Thread t = new Thread(this);
	String server="localhost";
	int numPlayers;
	

	public MiddleMan(String server){
		this.server = server;
		this.numPlayers = 0;
		try {
            serverSocket = new DatagramSocket(MIDDLEPORT);
			serverSocket.setSoTimeout(100);
		} catch (IOException e) {
            System.err.println("Could not listen on port: "+MIDDLEPORT);
            System.exit(-1);
		}catch(Exception e){}
		//Create the game state
		game = new GameState();
		
		System.out.println("Middle man has started...");
		
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

	public void sendToServer(String msg){
		try{
			DatagramPacket packet;	
			byte buf[] = msg.getBytes();		
			packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(server), PORT);
			serverSocket.send(packet);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
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
			receivedData=new String(buf);
			
			//remove excess bytes
			receivedData = receivedData.trim();
			//if (!receivedData.equals("")){
			//	System.out.println("Player Data:"+receivedData);
			//}

			if(receivedData.startsWith("TOSERVER")){
				receivedData = receivedData.substring(8).trim();
				sendToServer(receivedData);

				if (receivedData.startsWith("CONNECT ")){
					String tokens[] = receivedData.split(" ");
					NetPlayer player=new NetPlayer(tokens[1].trim(),packet.getAddress(),packet.getPort());
					System.out.println("Player connected: "+tokens[1]);
					game.update(tokens[1].trim(),player);
					numPlayers++;
				}
				System.out.println("TOSERVER================================================");
				System.out.println("From: "+ packet.getAddress() +":"+ packet.getPort());
				System.out.println(receivedData);
				System.out.println("========================================================\n");
			}
			else if(receivedData.startsWith("TOCLIENTS")){
				receivedData = receivedData.substring(9).trim();
				broadcast(receivedData);
				System.out.println("TOCLIENTS================================================");
				System.out.println("From: "+ packet.getAddress() +":"+ packet.getPort());
				System.out.println(receivedData);
				System.out.println("========================================================\n");

			}
			else if(receivedData.startsWith("TOCLIENT")){
				receivedData = receivedData.substring(8).trim();
				System.out.println("TOCLIENT================================================");
				System.out.println("From: "+ packet.getAddress() +":"+ packet.getPort());
				System.out.println(receivedData);
				System.out.println("========================================================\n");
				send(game.getPlayers().get(Integer.parseInt(receivedData.substring(0,1).trim())), receivedData.substring(2).trim());
				
			}

		}
	}	
	
	
	public static void main(String args[]){
		/*if (args.length != 1){
			System.out.println("Usage: java -jar circlewars-server <number of players>");
			System.exit(1);
		}*/
		if (args.length == 0)
			new MiddleMan("localhost");
		else
			new MiddleMan(args[0]);
	}
}

