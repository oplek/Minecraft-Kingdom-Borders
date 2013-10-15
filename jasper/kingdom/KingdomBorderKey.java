package jasper.kingdom;

import java.util.EnumSet;
import java.util.logging.Level;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.src.ModLoader;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import jasper.kingdom.KingdomBorder;

public class KingdomBorderKey extends KeyHandler {
	static public boolean kbdr = false;
	static private EnumSet tickTypes = EnumSet.of(TickType.CLIENT);
	
	public KingdomBorderKey(KeyBinding[] keyBindings, boolean[] repeatings) {
		super(keyBindings, repeatings);
		// TODO Auto-generated constructor stub

	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "Kingdom Border Checker";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,
			boolean tickEnd, boolean isRepeat) {
		// TODO Auto-generated method stub
		kbdr = true;
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		// TODO Auto-generated method stub

	}

	@Override
	public EnumSet<TickType> ticks() {
		// TODO Auto-generated method stub
		return tickTypes;
	}
	
	private void log(String s) {
    	ModLoader.getLogger().log(Level.INFO, s);
    }
	
	

}
