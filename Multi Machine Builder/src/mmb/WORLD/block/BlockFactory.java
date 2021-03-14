/**
 * 
 */
package mmb.WORLD.block;

import mmb.WORLD.worlds.world.World.BlockMap;

/**
 * @author oskar
 *
 */
@FunctionalInterface
public interface BlockFactory {
	/**
	 * This function is run when a block entity is created
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param map block map
	 * @return a new {@link BlockEntry}
	 */
	public BlockEntity create(int x, int y, BlockMap map);
}
