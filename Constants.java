/**
 * Important game constants
 * @author Joseph Anthony C. Hermocill
 *
 */
public interface Constants {
	public static final String APP_NAME="Bad Blood BETA 0.2";
	public final int NUMBER_OF_PLAYERS=2;
	/**
	 * Game states.
	 */
	public static final int GAME_START=0;
	public static final int IN_PROGRESS=1;
	public final int GAME_END=2;
	
	public final int WAITING_FOR_PLAYERS=3;
	public final int WAITING_FOR_TROOPS=4;
	public final int GAME=5;
	
	
	public final int MAX_TROOP=15;
	
	/**
	 * Game port
	 */
	public static final int PORT=9137;
	public static final int MIDDLEPORT=9139;
}
