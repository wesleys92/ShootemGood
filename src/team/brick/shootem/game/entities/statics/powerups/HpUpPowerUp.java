package team.brick.shootem.game.entities.statics.powerups;

import java.awt.Color;
import java.awt.Graphics;

import team.brick.shootem.game.Handler;

public class HpUpPowerUp extends PowerUp{

	public HpUpPowerUp(Handler handler, float x, float y) {
		super(handler, x, y);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void render(Graphics g) {
		posX = (int)(x - handler.getGameCamera().getxOffset());
		posY = (int) (y - handler.getGameCamera().getyOffset());
		g.setColor(Color.red);
		g.drawRect(posX, posY, width, height);
		
	}
	
	public void die(){
		active = false;
		handler.getPlayer().setHealth(handler.getPlayerHealth() + 6);
	}
}