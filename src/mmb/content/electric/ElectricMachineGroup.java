/**
 * 
 */
package mmb.content.electric;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Function;
import mmb.NN;
import mmb.engine.block.BlockEntity;
import mmb.engine.block.BlockEntityType;
import mmb.engine.item.Items;
import mmb.engine.java2d.TexGen;
import mmb.engine.rotate.RotatedImageGroup;
import mmb.engine.settings.GlobalSettings;

/**
 * Provides 9 voltages of the same electrical item
 * @author oskar
 */
public class ElectricMachineGroup {
	//Group contents
	/** Machine block types, sorted by voltage */
	@NN public final List<@NN ElectroMachineType> blocks;
	/** Machine images, sorted by voltage */
	@NN public final List<@NN BufferedImage> images;
	/** Machine textures, sorted by voltage */
	@NN public final List<@NN RotatedImageGroup> textures;
	private static final VoltageTier[] volts = VoltageTier.values();
	
	//Constructors
	/**
	 * Creates an electric machine group
	 * @param image machine's texture
	 * @param ctor creates machine block entity from a block type
	 * @param id machine group ID
	 */
	public ElectricMachineGroup(BufferedImage image, Function<@NN ElectroMachineType, @NN BlockEntity> ctor, String id) {
		this(image, ctor, id, 10);
	}
	/**
	 * Creates an electric machine group
	 * @param image machine's texture
	 * @param ctor creates machine block entity from a block type
	 * @param id machine group ID
	 * @param powermul power in coulombs per tick
	 */
	@SuppressWarnings("null")
	public ElectricMachineGroup(BufferedImage image, Function<@NN ElectroMachineType, @NN BlockEntity> ctor, String id, double powermul) {
		images = TexGen.generateMachineTextures(image);
		@NN RotatedImageGroup[] textures0 = new RotatedImageGroup[9];
		@NN ElectroMachineType[] blocks0 = new ElectroMachineType[9];
		for(int i = 0; i < 9; i++) {
			//Create a texture
			BufferedImage img = images.get(i);
			RotatedImageGroup rig = RotatedImageGroup.create(img);
			textures0[i] = rig;
			
			//Create a block type
			VoltageTier volt = volts[i];
			ElectroMachineType type = new ElectroMachineType(volt, rig, powermul);
			type.title(GlobalSettings.$res1("machine-"+id)+' '+volt.name);
			type.factory(() -> ctor.apply(type));
			type.finish("industry."+id+i);
			blocks0[i] = type;
		}
		
		textures = List.of(textures0);
		blocks = List.of(blocks0);
		
		Items.tagItems("machine-"+id, blocks);
		for(int i = 0; i < 9; i++) {
			ElectroMachineType block = blocks.get(i);
			Items.tagItem("voltage-"+block.volt.name, block);
		}
	}
	
	/**
	 * A specialized block type for autogenerated electrical machines
	 * @author oskar
	 */
	public class ElectroMachineType extends BlockEntityType{
		/** Voltage tier */
		@NN public final VoltageTier volt;
		/** Machine texture */
		@NN public final RotatedImageGroup rig;
		/** Machine power in coulombs per tick*/
		public final double powermul;
		/**
		 * Creates an electric machine type. The texture is set automatically
		 * @param volt voltage tier
		 * @param rig texture
		 * @param powermul power mutiplier above the base power
		 */
		public ElectroMachineType(VoltageTier volt, RotatedImageGroup rig, double powermul) {
			super();
			this.volt = volt;
			this.rig = rig;
			this.powermul = powermul;
			texture(rig.U);
		}
	}
}
