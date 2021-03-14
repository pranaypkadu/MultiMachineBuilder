/**
 * 
 */
package mmb.WORLD.gui;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import mmb.WORLD.block.BlockEntityType;
import mmb.WORLD.block.BlockType;
import mmb.WORLD.worlds.world.BlockArrayProvider;
import mmb.WORLD.worlds.world.World;
import mmb.WORLD.worlds.world.World.BlockMap;

/**
 * @author oskar
 *
 */
public class BlockPlacer implements Placer {
	public final BlockType block;
	/**
	 * Create a {@link BlockPlacer} for a given {@link BlockEntityType}
	 * @param t the {@link BlockEntityType} for the {@link BlockPlacer}
	 */
	public BlockPlacer(BlockType t) {
		this.block = t;
	}
	@Override
	public BufferedImage getIcon() {
		return block.getTexture().img;
	}
	@Override
	public String title() {
		return block.title();
	}
	@Override
	public void place(int x, int y, World map) {
		block.place(x, y, map);
	}
	@Override
	public void openGUI() {
		//unused
	}
	@Override
	public void closeGUI() {
		//unused
	}
	@Override
	public void preview(Graphics g, Point renderStartPos, BlockMap map, Point targetLocation) {
		g.drawImage(getIcon(), renderStartPos.x, renderStartPos.y, null);
	}

}
