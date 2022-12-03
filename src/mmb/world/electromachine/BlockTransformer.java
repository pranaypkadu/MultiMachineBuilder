/**
 * 
 */
package mmb.world.electromachine;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;

import javax.annotation.Nonnull;

import mmb.graphics.awt.ColorMapper;
import mmb.texture.Textures;
import mmb.world.block.BlockEntityType;
import mmb.world.block.BlockEntry;
import mmb.world.block.BlockType;
import mmb.world.blocks.SkeletalBlockEntityRotary;
import mmb.world.electric.Electricity;
import mmb.world.electric.VoltageTier;
import mmb.world.electric.Electricity.SettablePressure;
import mmb.world.item.Items;
import mmb.world.rotate.RotatedImageGroup;
import mmb.world.rotate.Side;
import mmb.world.worlds.MapProxy;

/**
 * @author oskar
 * A transformer is a block which allows to change voltage
 */
public class BlockTransformer extends SkeletalBlockEntityRotary {

	@Nonnull public final TransformerData type;
	public BlockTransformer(TransformerData type) {
		this.type = type;
	}

	@Override
	public BlockType type() {
		return type.type;
	}

	@Override
	public BlockEntry blockCopy() {
		return new BlockTransformer(type);
	}

	@Override
	public RotatedImageGroup getImage() {
		return type.image;
	}
	
	@Nonnull private static final BufferedImage src0 = Textures.get("machine/transformer.png");
	@Nonnull private static final ColorMapper mapper = ColorMapper.ofType(src0.getType(), Color.RED, Color.red);
	@Nonnull private static final LookupOp op = new LookupOp(mapper, null);
	@Nonnull private static final ColorMapper mapper2 = ColorMapper.ofType(src0.getType(), Color.MAGENTA, Color.red);
	@Nonnull private static final LookupOp op2 = new LookupOp(mapper2, null);
	/**
	 * @author oskar
	 * This class contains information about transformer tiers
	 */
	public enum TransformerData{
		VLV(VoltageTier.V2, VoltageTier.V1),
		 LV(VoltageTier.V3, VoltageTier.V2),
		 MV(VoltageTier.V4, VoltageTier.V3),
		 HV(VoltageTier.V5, VoltageTier.V4),
		 EV(VoltageTier.V6, VoltageTier.V5),
		 IV(VoltageTier.V7, VoltageTier.V6),
		LuV(VoltageTier.V8, VoltageTier.V7),
		MAX(VoltageTier.V9, VoltageTier.V8);
		
		@Nonnull public final VoltageTier high;
		@Nonnull public final VoltageTier low;
		@Nonnull public final BlockEntityType type;
		@Nonnull public final RotatedImageGroup image;
		/**
		 * Initializes the transformers
		 */
		public static void init() { /* used just for initialization */ }
		TransformerData(VoltageTier high, VoltageTier low) {
			this.high = high;
			this.low = low;
			mapper.setTo(high.c);
			mapper2.setTo(low.c);
			BufferedImage img = op.filter(src0, null);
			op2.filter(img, img);
			this.image = RotatedImageGroup.create(img);
			this.type = new BlockEntityType()
					.title("Transformer "+high.name+"/"+low.name)
					.factory(() -> new BlockTransformer(this))
					.texture(image.U)
					.finish("industry.transformer"+low.ordinal());
			Items.tagsItem(type, "voltage-"+high.name, "voltage-"+low.name, "machine-transformer");
		}
	}
	
	//Electrical data
	public double ppres;
	/**1-up, 2-down, 3-right*/
	@Nonnull private ElecLow elow = new ElecLow();
	@Nonnull private ElecHi ehigh = new ElecHi();
	
	private class ElecLow implements SettablePressure{
		@Override
		public double insert(double amt, VoltageTier volt) {
			if(volt.compareTo(type.low) > 0) {
				blow();
				return 0;
			}
			//Insert electricity to the backing unit
			Electricity elec = getAtSide(getRotation().L()).getElectricalConnection(getRotation().R());
			if(elec == null) return 0;
			double charge = elec.insert(amt/4, type.high)*4;
			ppres += amt-charge;
			return charge;
		}

		@Override
		public double extract(double amt, VoltageTier volt, Runnable blow) {
			if(volt.compareTo(type.low) < 0) {
				blow.run();
				return 0;
			}
			//Extract electricity from the backing unit
			Electricity elec = getAtSide(getRotation().L()).getElectricalConnection(getRotation().R());
			if(elec == null) return 0;
			double charge = elec.extract(amt/4, type.high, BlockTransformer.this::blow)*4;
			ppres -= amt-charge;
			return charge;
		}

		@Override
		public VoltageTier voltage() {
			return type.low;
		}

		@Override
		public double pressure() {
			return ppres;
		}

		@Override
		public double pressureWeight() {
			return 1;
		}

		@Override
		public void setPressure(double pressure) {
			ppres = pressure;
		}
	}
	private class ElecHi implements Electricity{
		@Override
		public double insert(double amt, VoltageTier volt) {
			if(volt.compareTo(type.high) > 0) {
				blow();
				return 0;
			}
			double tfd = 0;
			Electricity elec = getAtSide(getRotation().R()).getElectricalConnection(getRotation().L());
			if(elec != null) tfd = elec.insert(amt*4, type.low)/4;
			ppres += amt-tfd;
			return tfd;
		}

		@Override
		public double extract(double amt, VoltageTier volt, Runnable blow) {
			if(volt.compareTo(type.high) < 0) {
				blow.run();
				return 0;
			}
			double tfd = 0;
			Electricity elec = getAtSide(getRotation().R()).getElectricalConnection(getRotation().L());
			if(elec != null) tfd = elec.insert(amt*4, type.low)/4;
			ppres -= amt-tfd;
			return tfd;
		}

		@Override
		public VoltageTier voltage() {
			return type.high;
		}

		@Override
		public double pressure() {
			return ppres;
		}

		@Override
		public double pressureWeight() {
			return 1;
		}
	}
	

	@Override
	public void onTick(MapProxy map) {
		Electricity.equatePPs(this, map, elow, 0.99);
	}

	@Override
	public Electricity getElectricalConnection(Side s) {
		switch(getRotation().bwd().apply(s)) {
		case L:
			return ehigh;
		case R:
			return elow;
		default:
			return null;
		}
	}

}
