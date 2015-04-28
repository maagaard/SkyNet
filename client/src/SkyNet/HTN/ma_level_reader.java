package client;

import java.io.BufferedReader;
import java.util.HashMap;

public class Level {

	public static int MAX_ROW = 80;
	public static int MAX_COLUMN = 80;
		
	public HashMap<Integer, Agent> agent_map;
	public HashMap<String, Box> box_map;
	public HashMap<String, Goal> goal_map;
	
	public HashMap<String, Color> box_color;
	
	boolean walls[][];
	
	public Level(){
		this.agent_map = new HashMap<Integer, Agent>();
		this.box_color = new HashMap<String, Color>();
		this.goal_map = new HashMap<String, Goal>();

		this.box_map = new HashMap<String, Box>();
		
		this.walls = new boolean[MAX_ROW][MAX_COLUMN];
	}
	
	public void read_level(BufferedReader serverMessages) throws Exception {
		
		String line, color;
		while ( ( line = serverMessages.readLine() ).matches( "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$" ) ) {
			line = line.replaceAll( "\\s", "" );
			String[] colonSplit = line.split( ":" );
			
			color = colonSplit[0].trim();
			Color c = Color.blue;
			switch (color.toLowerCase()) {
			case "blue":
				c = Color.blue;
				break;
			case "red":
				c = Color.red;
				break;
			case "green":
				c = Color.green;
				break;
			case "cyan":
				c = Color.cyan;
				break;
			case "magenta":
				c = Color.magenta;
				break;
			case "orange":
				c = Color.orange;
				break;
			case "pink":
				c = Color.pink;
				break;
			case "yellow":
				c = Color.yellow;
				break;
				
			default:
				Sys.error( "Line with no color ?!?" );
				break;
			}
			
			for ( String id : colonSplit[1].split( "," ) ) {
				
				if (id.matches("[0-9]")) {
			        Sys.print("hello agent " + id);
			        
			        Agent a = new Agent(Integer.parseInt(id), c);
			        this.agent_map.put(Integer.parseInt(id), a);
				}
				
				if (id.matches("[A-Z]")) {
					Sys.print("Boxes " + id + " are " + color);
				
					this.box_color.put(id, c);
				}
			}
		} // Done reading colors
		
		int max_col = 0;
		int row = 0;
		int box_count = 0;
		int goal_count = 0;
		
		while ( !line.equals( "" ) ) {
			
			if (line.length() > max_col) {
				max_col = line.length();
			}
			
			for ( int col = 0; col < line.length(); col++ ) {
				char chr = line.charAt( col );
				
				if ( '+' == chr ) {
					this.walls[row][col] = true;
				} 
				else if ( '0' <= chr && chr <= '9' ) {
					Integer id = Character.getNumericValue(chr);
					
					Sys.print("Hello agent: " + id.toString());
					
					Agent a = null;					
					a = this.agent_map.get( id );

					// If we find an agent with no color defined, make it and set color to blue.
					if (a == null) {
						a = new Agent(id, Color.blue);
						this.agent_map.put(id, a);
					}
					
					a.set_pos(row, col);
				} 
				else if ( 'A' <= chr && chr <= 'Z' ) {
					String id = "" + chr;

					Sys.print("Hello box: " + id);
					
					Color c = null;
					c = this.box_color.get(id);
					
					if (null == c) {
						c = Color.blue;
					}
					
					Box b = new Box(id, c, row, col);
					
					this.box_map.put(id + box_count, b);
					
					box_count++;
				} 
				else if ( 'a' <= chr && chr <= 'z' ) {
					String id = "" + chr;
					
					Sys.print("Hello goal: " + id);
					
					Goal g = new Goal(id + goal_count, row, col);
					
					goal_map.put(id, g);
					
					goal_count++;
				}
			}
			line = serverMessages.readLine();
			row++;
		}
	}
	
	
}
