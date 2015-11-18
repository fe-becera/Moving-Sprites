//http://gamedev.stackexchange.com/questions/53705/how-can-i-make-a-sprite-sheet-based-animation-system
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import javax.swing.Timer;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.LinkedList;
import java.awt.Insets;
/**
 * The game client itself!
 * @author Joseph Anthony C. Hermocilla
 *
 */


public class BadBlood extends JPanel implements Runnable, Constants{
	/**
	 * Main window
	 */
	JFrame frame= new JFrame();
	
	/**
	 * Player position, speed etc.
	 */
	int x=200,y=200,xspeed=2,yspeed=2,prevX,prevY;
	
	/**
	 * Game timer, handler receives data from server to update game state
	 */
	Thread t=new Thread(this);
	ImageIcon imageIcon;
	/**
	 * Nice name!
	 */
	String name="";
	JTextField nameField = new JTextField(10);
	LinkedList<Troop> deployedTroops = new LinkedList<Troop>();
	
	/**
	 * Player name of others
	 */
	String pname;
	String selectedTroop = "a";
	int userID;
	/**
	 * Server to connect to
	 */
	String server="localhost";

	/**
	 * Flag to indicate whether this player has connected or not
	 */
	boolean connected=false, connecting=false;
	JPanel left_panel;
	
	/**
	 * Number of troops
	 */
	int hp=600;
	int archerCount = 0, barbarianCount = 0, horsemanCount = 0;
	JLabel archerCountLabel = new JLabel(Integer.toString(archerCount));
	JLabel barbarianCountLabel = new JLabel(Integer.toString(barbarianCount));
	JLabel horsemanCountLabel = new JLabel(Integer.toString(horsemanCount));
	JButton doneButton = new JButton("DONE");
	JTextField chatField = new JTextField(100);
	DefaultTableModel chatTableModel = new DefaultTableModel(new Object[][]{}, new Object[]{"Chat"});
	JTable chatTable = new JTable(chatTableModel);
	JTextArea chat = new JTextArea();
	JTextPane chatText = new JTextPane();
	JScrollPane chatPane = new JScrollPane(chat);
	Timer trepaint = new Timer(500, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    frame.getContentPane().repaint();
			frame.repaint();
			frame.revalidate();	
		}
	});
	BufferedImage myPicture, castle;
	/*SPRITE*/
	// Images for each animation
	/*private BufferedImage[] walkingLeft = {Sprite.getSprite(0, 1), Sprite.getSprite(2, 1)}; // Gets the upper left images of my sprite sheet
	private BufferedImage[] walkingRight = {Sprite.getSprite(0, 2), Sprite.getSprite(2, 2)};
	private BufferedImage[] standing = {Sprite.getSprite(1, 0)};

	// These are animation states
	private Animation walkLeft = new Animation(walkingLeft, 5);
	private Animation walkRight = new Animation(walkingRight, 5);
	private Animation stand = new Animation(standing, 5);

	// This is the actual animation
	private Animation animation = walkLeft;
	*/
	/**
	 * get a datagram socket
	 */
    DatagramSocket socket = new DatagramSocket();

	
    /**
     * Placeholder for data received from server
     */
	String serverData;
	
	/**
	 * Offscreen image for double buffering, for some
	 * real smooth animation :)
	 */
	//BufferedImage offscreen;

	
	/**
	 * Basic constructor
	 * @param server
	 * @param name
	 * @throws Exception
	 */
	public BadBlood(String server) throws Exception{
		this.server=server;
		this.name=name;
		
		frame.setTitle(APP_NAME+":"+name);
		//set some timeout for the socket
		socket.setSoTimeout(100);
		
		//Some gui stuff i hate.
		//frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1150, 720);
		frame.setVisible(true);
        	frame.setResizable(false);
		
		//create the buffer
		//offscreen=(BufferedImage)this.createImage(720, 480);
		
		//Some gui stuff again...
		//frame.addKeyListener(new KeyHandler());		
		//frame.addMouseMotionListener(new MouseMotionHandler());

		//tiime to play
		t.start();		
	}
	
	/**
	 * Helper method for sending data to server
	 * @param msg
	 */
	public void send(String msg){
		try{
			byte[] buf = msg.getBytes();
			InetAddress address = InetAddress.getByName(server);
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, MIDDLEPORT);
			socket.send(packet);
		}catch(Exception e){}
		
	}
	
	public boolean isTroopComplete(){
		if(archerCount+barbarianCount+horsemanCount == MAX_TROOP)
			return true;
		return false;
	}
	
	public void updateCountLabels(){
		archerCountLabel.setText(Integer.toString(archerCount));
		barbarianCountLabel.setText(Integer.toString(barbarianCount));
		horsemanCountLabel.setText(Integer.toString(horsemanCount));
		frame.getContentPane().repaint();
		frame.repaint();
		frame.revalidate();
		frame.repaint();
	}
	
	/**
	 * The juicy part!
	 */
	public void run(){
		while(true){
			try{
				Thread.sleep(1);
			}catch(Exception ioe){}
						
			//Get the data from players
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try{
			socket.receive(packet);
			}catch(Exception ioe){}
			
			serverData=new String(buf);
			serverData=serverData.trim();
			
			//if (!serverData.equals("")){
			//	System.out.println("Server Data:" +serverData);
			//}

			//Study the following kids.
			if (!connected && serverData.startsWith("CONNECTED")){
				connected=true;
				userID = Integer.parseInt(serverData.split(" ")[1].trim());
				System.out.println("Connected. userID = " + userID);
				
			}else if (!connected && !connecting){
				
				JButton connectButton = new JButton();
				connectButton.setPreferredSize(new Dimension(75,75));
				connectButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						if(!nameField.getText().isEmpty()){
							((JButton) e.getSource()).setEnabled(false);
							connecting = true;
						}
					}
				});

				ImageIcon connectIcon = new ImageIcon("icon.png");
				Image connectImg = connectIcon.getImage() ;  
				Image connectNewimg = connectImg.getScaledInstance(80, 80,  java.awt.Image.SCALE_SMOOTH );  
				connectIcon = new ImageIcon(connectNewimg);
				connectButton.setIcon(connectIcon);
						
				frame.setLayout(new GridBagLayout());
				GridBagConstraints cons1 = new GridBagConstraints();
				frame.getContentPane().setBackground(Color.BLACK);
				JPanel center_panel = new JPanel();
				center_panel.setLayout(new GridBagLayout());
				center_panel.setBackground(Color.BLACK);
				GridBagConstraints cons = new GridBagConstraints();
				GridBagConstraints cons2 = new GridBagConstraints();

				JLabel titleLabel = new JLabel();
				titleLabel.setIcon(new ImageIcon("final_title.PNG"));
				cons.gridx = 2;
				cons.gridy = 0;
				center_panel.add(titleLabel,cons);

				cons2.fill = GridBagConstraints.BOTH;
				cons2.gridx = 2;
				cons2.gridy = 2;
				cons2.insets = new Insets(70,10,20,0);
				nameField.setPreferredSize(new Dimension(70, 50));
				center_panel.add(nameField,cons2);

				cons.gridx = 2;
				cons.gridy = 3;
				center_panel.add(connectButton,cons);

				cons1.gridx = 2;
				cons1.gridy = 0;
				frame.getContentPane().add(center_panel,cons1);
				frame.getContentPane().repaint();
				frame.repaint();
				frame.revalidate();


				while(connectButton.isEnabled()){	
					try{
						Thread.sleep(1);
					}catch(Exception ioe){}
				}
				nameField.setEditable(false);
				name = nameField.getText();
			}else if (!connected && connecting){
				System.out.println("Connecting..");
				System.out.println(name);
				send("TOSERVER CONNECT "+ name);
			}
			else if (connected){
				//offscreen.getGraphics().clearRect(0, 0, 640, 480);
				/*if (serverData.startsWith("PLAYER")){
					String[] playersInfo = serverData.split(":");
					for (int i=0;i<playersInfo.length;i++){
						String[] playerInfo = playersInfo[i].split(" ");
						String pname =playerInfo[1];
						int x = Integer.parseInt(playerInfo[2]);
						int y = Integer.parseInt(playerInfo[3]);
						//draw on the offscreen image
						//offscreen.getGraphics().fillOval(x, y, 20, 20);
						//offscreen.getGraphics().drawString(pname,x-10,y+30);					
					}
					//show the changes
					frame.repaint();
				}*/
				if (serverData.startsWith("SELECT")){
					frame.getContentPane().removeAll();
					frame.getContentPane().repaint();
					frame.repaint();
					frame.revalidate();
					
					
					frame.setLayout(new BorderLayout());
					frame.getContentPane().setBackground(Color.BLACK);
					doneButton.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e)
						{
							if(isTroopComplete())
								((JButton) e.getSource()).setEnabled(false);
						}
					});
				
					JButton archerButtonAdd = new JButton("+");
					JButton barbarianButtonAdd = new JButton("+");
					JButton horsemanButtonAdd = new JButton("+");
					JButton archerButtonMinus = new JButton("-");
					JButton barbarianButtonMinus = new JButton("-");
					JButton horsemanButtonMinus = new JButton("-");
					
					archerButtonAdd.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(!isTroopComplete()){
								archerCount++;
								updateCountLabels();
							}
					}});
					barbarianButtonAdd.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(!isTroopComplete()){
								barbarianCount++;
								updateCountLabels();
							}
					}});
					horsemanButtonAdd.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(!isTroopComplete()){
								horsemanCount++;
								updateCountLabels();
							}
					}});
					archerButtonMinus.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(archerCount > 0){
								archerCount--;
								updateCountLabels();
							}
					}});
					barbarianButtonMinus.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(barbarianCount > 0){
								barbarianCount--;
								updateCountLabels();
							}
					}});
					horsemanButtonMinus.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(horsemanCount > 0){
								horsemanCount--;
								updateCountLabels();
							}
					}});
					JPanel centerPanel = new JPanel();
					JPanel archerPanel = new JPanel();
					JPanel barbarianPanel = new JPanel();
					JPanel horsemanPanel = new JPanel();
					JPanel archerBottomPanel = new JPanel();
					JPanel barbarianBottomPanel = new JPanel();
					JPanel horsemanBottomPanel = new JPanel();
					
					JLabel archerMug = new JLabel();
					JLabel barbarianMug = new JLabel();
					JLabel horsemanMug = new JLabel();
					
					archerMug.setIcon(new ImageIcon("archer_mug.png"));
					barbarianMug.setIcon(new ImageIcon("barbarian_mug.png"));
					horsemanMug.setIcon(new ImageIcon("horseman_mug.png"));
					
					archerBottomPanel.setLayout(new GridLayout(1,3));
					barbarianBottomPanel.setLayout(new GridLayout(1,3));
					horsemanBottomPanel.setLayout(new GridLayout(1,3));
					
					archerPanel.setLayout(new BorderLayout());
					archerPanel.add(new JLabel("ARCHERS"), BorderLayout.NORTH);
					archerPanel.add(archerMug, BorderLayout.CENTER);
					archerBottomPanel.add(archerButtonAdd);
					archerBottomPanel.add(archerCountLabel);
					archerBottomPanel.add(archerButtonMinus);
					archerPanel.add(archerBottomPanel, BorderLayout.SOUTH);
					
					barbarianPanel.setLayout(new BorderLayout());
					barbarianPanel.add(new JLabel("BARBARIANS"), BorderLayout.NORTH);
					barbarianPanel.add(barbarianMug, BorderLayout.CENTER);
					barbarianBottomPanel.add(barbarianButtonAdd);
					barbarianBottomPanel.add(barbarianCountLabel);
					barbarianBottomPanel.add(barbarianButtonMinus);
					barbarianPanel.add(barbarianBottomPanel, BorderLayout.SOUTH);
					
					horsemanPanel.setLayout(new BorderLayout());
					horsemanPanel.add(new JLabel("HORSEMEN"), BorderLayout.NORTH);
					horsemanPanel.add(horsemanMug, BorderLayout.CENTER);
					horsemanBottomPanel.add(horsemanButtonAdd);
					horsemanBottomPanel.add(horsemanCountLabel);
					horsemanBottomPanel.add(horsemanButtonMinus);
					horsemanPanel.add(horsemanBottomPanel, BorderLayout.SOUTH);
					
					centerPanel.setLayout(new GridLayout(1,3));
					centerPanel.add(archerPanel);
					centerPanel.add(barbarianPanel);
					centerPanel.add(horsemanPanel);
					
					frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
					frame.getContentPane().add(doneButton, BorderLayout.SOUTH);
					frame.getContentPane().repaint();
					frame.repaint();
					frame.revalidate();
					//frame.getContentPane(BorderLayout.CENTER);
					while(doneButton.isEnabled() || !isTroopComplete()){	
						try{
							Thread.sleep(100);
						}catch(Exception ioe){}
					}
					send("TOSERVER TROOPS "+ name);
				}
				if (serverData.startsWith("START")){
					frame.getContentPane().removeAll();
					frame.getContentPane().repaint();
					frame.repaint();
					frame.revalidate();
					
					frame.setLayout(new GridBagLayout());
					GridBagConstraints c = new GridBagConstraints();
					
					myPicture = null;
					castle = null;
					try {
						myPicture = ImageIO.read(new File("images_rpg/final/base_final.png"));
						castle = ImageIO.read(new File("images_rpg/castle.png"));
					} catch (IOException e) {
					}


					left_panel = new JPanel(){
					    @Override
					    protected void paintComponent(Graphics g) {
							super.paintComponent(g);
							g.drawImage(myPicture, 0, 0, null);
							for(int i=0; i<deployedTroops.size();i++){
								g.drawImage(deployedTroops.get(i).animation.getSprite(), deployedTroops.get(i).getX(), deployedTroops.get(i).getY(), null);
								if(deployedTroops.get(i).getX() >= 640 && hp != 0 && deployedTroops.get(i).getOwner() != userID)
									hp -= 15;
								System.out.println("Paint "+i);
								deployedTroops.get(i).animation.update();
							}
							if(hp != 0)
								g.drawImage(castle, 700, 100 +200*userID, null);
        					g.drawString("HP: "+hp, 700, 75+200*userID);
				    	}
					};


					left_panel.setLayout(new GridBagLayout());

					//JPanel transparent_panel =new JPanel();
					//transparent_panel.setBackground(Color.RED);

					imageIcon = new ImageIcon(myPicture); // load the image to a imageIcon
					Image image = imageIcon.getImage(); // transform it 
					Image newimg = image.getScaledInstance(1000, 689,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
					imageIcon = new ImageIcon(newimg); // transform it back

					JLabel picLabel = new JLabel(imageIcon);
					left_panel.setBackground(Color.BLACK);
					//left_panel.add(picLabel);
					left_panel.setPreferredSize(new Dimension(1000, 689));
					
					c.fill = GridBagConstraints.BOTH;
					c.gridx = 0;
					c.weightx=0.90;
					c.weighty=1.0;
					c.gridy = 0;
					left_panel.addMouseListener(new CustomMouseListener());

					frame.add(left_panel, c);
					//frame.add(transparent_panel,c);


					JPanel right_panel = new JPanel();
					right_panel.setLayout(new GridLayout(4,1));
					right_panel.setBackground(Color.BLACK);
					
					
					//Archer
					JButton label5 = new JButton();
					label5.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(archerCount != 0){
								selectedTroop = "a";
								System.out.println("Will deploy an archer.");
							}
					}});
					ImageIcon icon5 = new ImageIcon("archer_mug.png");
					Image img5 = icon5.getImage() ;  
					Image newimg5 = img5.getScaledInstance(100, 100,  java.awt.Image.SCALE_SMOOTH );  
					icon5 = new ImageIcon(newimg5);
					label5.setIcon(icon5);

					right_panel.add(label5);

					//Horseman
					JButton label6 = new JButton();
					label6.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(horsemanCount != 0){
								selectedTroop = "h";
								System.out.println("Will deploy a horseman.");
							}
					}});
					ImageIcon icon6 = new ImageIcon("horseman_mug.png");
					Image img6 = icon6.getImage() ;  
					Image newimg6 = img6.getScaledInstance(100, 100,  java.awt.Image.SCALE_SMOOTH );  
					icon6 = new ImageIcon(newimg6);
					label6.setIcon(icon6);

					right_panel.add(label6);
					
					//Barbarian
					JButton label7 = new JButton();
					label7.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(barbarianCount != 0){
								selectedTroop = "b";
								System.out.println("Will deploy a barbarian.");
							}
					}});
					ImageIcon icon7 = new ImageIcon("barbarian_mug.png");
					Image img7 = icon7.getImage() ;  
					Image newimg7 = img7.getScaledInstance(100, 100,  java.awt.Image.SCALE_SMOOTH );  
					icon7 = new ImageIcon(newimg7);
					label7.setIcon(icon7);

					right_panel.add(label7);

					
					c.fill = GridBagConstraints.BOTH;
					c.gridx = 1;
					c.gridy = 0;
					c.weightx=0.10;
					c.weighty=1.0;
					
					JPanel chatPanel = new JPanel();
					Action chatEnter = new AbstractAction(){
						@Override
						public void actionPerformed(ActionEvent e){
							send("TOSERVER MESSAGE "+ name + " : " + chatField.getText());
							chatField.setText("");
						}
					};
					chatPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
					chatPanel.setLayout(new BorderLayout());
					chatPanel.add(chatPane, BorderLayout.CENTER);
					chatPanel.add(chatField, BorderLayout.SOUTH);
					chatField.addActionListener(chatEnter);
					chat.setText("CHATBOX\n");
					chatText.setEditable(false);
					chat.setLineWrap(true);
					chat.setEditable(false);
					right_panel.add(chatPanel);
					
					frame.add(right_panel, c);

					if(archerCount != 0){
						selectedTroop = "a";
					}
					else if(barbarianCount != 0){
						selectedTroop = "b";
					}
					else{
						selectedTroop = "c";
					}

					frame.revalidate();
					/*frame.setLayout(new GridLayout(2,4));
					JPanel chatPanel = new JPanel();
					Action chatEnter = new AbstractAction(){
						@Override
						public void actionPerformed(ActionEvent e){
							send("MESSAGE "+ name + " : " + chatField.getText());
							chatField.setText("");
						}
					};
					chatPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
					chatPanel.setLayout(new BorderLayout());
					chatPanel.add(chatPane, BorderLayout.CENTER);
					chatPanel.add(chatField, BorderLayout.SOUTH);
					chatField.addActionListener(chatEnter);
					chat.setText("_________CHATBOX_________\n");
					chatText.setEditable(false);
					chat.setLineWrap(true);
					chat.setEditable(false);
					frame.getContentPane().add(new JPanel());
					frame.getContentPane().add(new JPanel());
					frame.getContentPane().add(new JPanel());
					frame.getContentPane().add(new JPanel());
					frame.getContentPane().add(new JPanel());
					frame.getContentPane().add(chatPanel);
					frame.getContentPane().add(new JPanel());
					frame.getContentPane().add(new JPanel());

					animation.start();
					JPanel gpanel = new JPanel() {
					    @Override
					    protected void paintComponent(Graphics g) {
							super.paintComponent(g);
							g.drawImage(animation.getSprite(), 0, 0, null);
							animation.update();
					    }
					};
					gpanel.setBackground(Color.RED);
					gpanel.addMouseListener(new CustomMouseListener());
							

					frame.getContentPane().add(gpanel);
					frame.getContentPane().repaint();
					frame.repaint();
					frame.revalidate();
					trepaint.start();*/
					
				}
				if (serverData.startsWith("MESSAGE")){
					/*JTextArea chat = new JTextArea();
					chat.setText(serverData.substring(7));
					chat.setEditable(false);
					chat.setLineWrap(true);
					chatPane.getViewport().add(chat);*/
					/*chatTableModel.addRow(new Object[]{
						serverData.substring(7)
					});*/
					//chatTable.setRowHeight(chatTable.getRowCount()-1, ((int)serverData.substring(7).length()/22 + 1)*14);
					chat.append("\n"+serverData.substring(7)+"\n");
					chat.setCaretPosition(chat.getText().length());
				}
				else if(serverData.startsWith("TROOP")){
					//System.out.println(serverData);
					serverData = serverData.substring(6);
					String tokens[] = serverData.split(":");
					deployedTroops = new LinkedList<Troop>();
					for(int i=0; i<tokens.length; i++){
						deployedTroops.add(new Troop("TROOP "+ tokens[i]));
					}
					left_panel.repaint();
				}
			}			
		}
	}
	
	/**
	 * Repainting method
	 */
	/*public void paintComponent(Graphics g){
		g.drawImage(offscreen, 0, 0, null);
	}*/
	
	
	
	class MouseMotionHandler extends MouseMotionAdapter{
		public void mouseMoved(MouseEvent me){
			x=me.getX();y=me.getY();
			if (prevX != x || prevY != y){
				send("TOSERVER PLAYER "+name+" "+x+" "+y);
			}				
		}
	}

	class CustomMouseListener implements MouseListener{

		public void mouseClicked(MouseEvent e) {
			System.out.println("Mouse Clicked: ("+e.getX()+", "+e.getY() +")");

			if(selectedTroop=="a" && archerCount>0){
				send("TOSERVER DEPLOY "+userID+" "+selectedTroop+" "+e.getX()+" "+e.getY());
				archerCount--;
				if(archerCount==0){
					if(horsemanCount > 0) selectedTroop = "h";
					else if(barbarianCount > 0) selectedTroop = "b";
					else selectedTroop = "x"; //NO MORE
				}
			}
			else if(selectedTroop=="h" && horsemanCount>0){
				send("TOSERVER DEPLOY "+userID+" "+selectedTroop+" "+e.getX()+" "+e.getY());
				horsemanCount--;
				if(horsemanCount==0){
					if(archerCount > 0) selectedTroop = "a";
					else if(barbarianCount > 0) selectedTroop = "b";
					else selectedTroop = "x"; //NO MORE
				}
			}
			else if(selectedTroop=="b" && barbarianCount>0) {
				send("TOSERVER DEPLOY "+userID+" "+selectedTroop+" "+e.getX()+" "+e.getY());
				barbarianCount--;
				if(barbarianCount==0){
					if(archerCount > 0) selectedTroop = "a";
					else if(horsemanCount > 0) selectedTroop = "h";
					else selectedTroop = "x"; //NO MORE
				}
			}
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}
	
	class KeyHandler extends KeyAdapter{
		public void keyPressed(KeyEvent ke){
			/*prevX=x;prevY=y;
			switch (ke.getKeyCode()){
			case KeyEvent.VK_DOWN:y+=yspeed;break;
			case KeyEvent.VK_UP:y-=yspeed;break;
			case KeyEvent.VK_LEFT:x-=xspeed;break;
			case KeyEvent.VK_RIGHT:x+=xspeed;break;
			}
			if (prevX != x || prevY != y){
				send("PLAYER "+name+" "+x+" "+y);
			}*/
				
		}
	}
	
	
	public static void main(String args[]) throws Exception{
		//if (args.length != 2){
			//System.out.println("Usage: java -jar badblood <server> <player name>");
		//	System.exit(1);
		//}

		if (args.length == 0)
			new BadBlood("localhost");
		else
			new BadBlood(args[0]);
	}
}
