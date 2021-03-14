/**
 * 
 */
package mmb.WORLD.gui;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import mmb.BEANS.Titled;
import mmb.WORLD.worlds.world.BlockArrayProvider;
import mmb.WORLD.worlds.world.World;
import mmb.WORLD.worlds.world.World.BlockMap;

/**
 * @author oskar
 *
 */
public interface Placer extends Titled {
	public BufferedImage getIcon();
	public default void place(int x, int y, BlockArrayProvider that) {
		place(x, y, that.getOwner());
	}
	public void place(int x, int y, World that);
	public void openGUI();
	public void closeGUI();
	public void preview(Graphics g, Point renderStartPos, BlockMap map, Point targetLocation);
	
	@FunctionalInterface
	/**
	 * This is a functional interface for preview generation
	 * @author oskar
	 */
	public static interface Previewer{
		public void draw(Graphics g, Point renderStartPos, BlockMap map, Point targetLocation);
	}
}
