package team.brick.shootem.game.worlds;

import java.awt.Graphics;
import resources.ResourceLoader;
import team.brick.shootem.game.Handler;
import team.brick.shootem.game.entities.EntityManager;
import team.brick.shootem.game.entities.creatures.Player;
import team.brick.shootem.game.tiles.Tile;
import team.brick.shootem.game.utils.Utils;

/**
 *	A world is made of Tiles and Entities. A world is what the player
 *	sees and interacts with to play the game.
 * 	
 *	@author 
 *	@version 1.0
 *	@since version 1.0
 */
public class World {

	private Handler handler;
	private int width, height, bossType;
	private int spawnX, spawnY;
	private int[][] tiles;
	//Entities
	private EntityManager entityManager;
	
	public World(Handler handler, String path){
		this.handler = handler;
		entityManager = new EntityManager(handler, handler.getPlayer());
		loadWorld(path);
		
		//set player spawn point
		entityManager.getPlayer().setX(spawnX);
		entityManager.getPlayer().setY(spawnY);
	}
	
	/**
	 *  Calls the tick() of the entityManager, 
	 *  resulting in the tick() of all Entities.
	 */
	public void tick(){
		entityManager.tick();
	}
	
	/**
	 * Renders all entities and all visible portions of the world.
	 * 
	 * @param g
	 */
	public void render(Graphics g){
		// Since we only want to render what is currently visible on the screen, 
		// we set start and end points for the x and y coordinates which will determine 
		// which tiles to render. This is based off the GameCameras position, as found 
		// by its xOffset and yOffset.
		int xStart = (int) Math.max(0, handler.getGameCamera().getxOffset() / Tile.TILEWIDTH);
		int xEnd = (int) Math.min(width, (handler.getGameCamera().getxOffset() + handler.getWidth()) / Tile.TILEWIDTH + 1);
		int yStart = (int) Math.max(0, handler.getGameCamera().getyOffset() / Tile.TILEHEIGHT);
		int yEnd = (int) Math.min(height, (handler.getGameCamera().getyOffset() + handler.getHeight()) / Tile.TILEHEIGHT + 1);
		
		// Render each tile which is currently visible
		for(int y = yStart;y < yEnd;y++){
			for(int x = xStart;x < xEnd;x++){
				if(getTile(x,y).isGoal() && handler.getPlayer().isBossDead()){
					getTile(x,y).showGoal();
				}else{
					getTile(x,y).hideGoal();
				}
				getTile(x, y).render(g, (int) (x * Tile.TILEWIDTH - handler.getGameCamera().getxOffset()),
						(int) (y * Tile.TILEHEIGHT - handler.getGameCamera().getyOffset()));
			}
		}
		// Render all Entities
		entityManager.render(g);
	}
	
	/**
	 * Gets a Tile at the specified position (x,y).
	 * 
	 * @param x the x position of the Tile
	 * @param y the y position of the Tile
	 * @return t the Tile at position (x,y)
	 * @return Tile.grassTile if (x,y) is out of bounds
	 */
	public Tile getTile(int x, int y){
		if(x < 0 || y < 0 || x >= width || y >= height)
			return Tile.grassTile;
		
		Tile t = Tile.tiles[tiles[x][y]];
		
		if(t == null)
			return Tile.dirtTile;
		
		return t;
	}
	
	/**
	 * Loads the world from a file at the specified location.
	 * 
	 * @param path the location of the world file.
	 */
	private void loadWorld(String path){
		String file = ResourceLoader.loadWorldFile(path);
		String[] tokens = file.split("\\s+");
		
		handler.getPlayer().setIsBossDead(false);
		
		// In the world text file, the first four tokens (integers divided by a space or end line)
		// determine the width, height, and (x,y) coordinates of the player spawn.
		width = Utils.parseInt(tokens[0]);
		height = Utils.parseInt(tokens[1]);
		spawnX = Utils.parseInt(tokens[2]);
		spawnY = Utils.parseInt(tokens[3]);
		bossType = Utils.parseInt(tokens[4]);
		
		tiles = new int[width][height];
		for(int y = 0;y < height;y++){
			for(int x = 0;x < width;x++){
					//gets the id of the tile corresponding to the world*.txt 
					//this will be used to render the appropriate tiles later.
				tiles[x][y] = Utils.parseInt(tokens[(x + y * width) + 5]);
				
					//Sets the player's spawn point if the tile at (x,y) is a player spawn tile
				if(getTile(x,y).isPSpawn()){
					spawnX = x * Tile.TILEWIDTH;
					spawnY = (y) * Tile.TILEHEIGHT;
				
					//Spawns a random enemy on an enemy spawn tile
				}else if(getTile(x,y).isESpawn()){
					int randomSpawn = Utils.randomNum(1,3);
					entityManager.spawnEnemy(handler, x * Tile.TILEWIDTH, (y) * Tile.TILEHEIGHT, randomSpawn);
					
					//Spawns the boss on a boss spawn tile
				}else if(getTile(x,y).isBossSpawn()){
					System.out.println(bossType + ", x: " + x * Tile.TILEWIDTH + ", y: "+ Tile.TILEHEIGHT);
					entityManager.spawnBoss(handler, x * Tile.TILEWIDTH, y * Tile.TILEHEIGHT, bossType);
					
				}
			}
		}
	}
	
	
	
	/**
	 * @return width the width of the world
	 */
	public int getWidth(){
		return width;
	}
	
	/**
	 * @return height the height of the world
	 */
	public int getHeight(){
		return height;
	}

	/**
	 * @return entityManager the EntityManager of the world
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
}








