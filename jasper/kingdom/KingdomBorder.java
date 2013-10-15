package jasper.kingdom;


import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import net.minecraft.src.ModLoader;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

import java.awt.Color;
import java.awt.FontMetrics;

import java.util.logging.Level;


import org.lwjgl.input.Keyboard;


@Mod(modid="KingdomBorder", name="Kingdom Borders", version="0.0.2")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class KingdomBorder {

		//Initialize calculation variables once
		private double px = 0,py = 0,pz = 0; 											//Calculized coordinates
		private double dist=0, angle=0, dt,df,pi2 = Math.PI * 2;	//More calc variables
		private int prevKingdom = -2;															//Index of which kingdom we've last calculated (-1 is neutral territory, -2 is undefined)
		private int flag=0, section; 															//More calc variables
		private int count = 0;																		//Tick counter
		private boolean found = false;														//More calc variables
		
		//Set up a key 
		private KingdomBorderKey kbk;
		private boolean kbk_loaded = false;
		public KeyBinding[] key = {new KeyBinding("Kingdom Check", Keyboard.KEY_SEMICOLON)};
		public boolean[] repeat = {false};
	

      // The instance of your mod that Forge uses.
      @Instance("KingdomBorder")
      public static KingdomBorder instance;
      
      // Says where the client and server 'proxy' code is loaded.
      @SidedProxy(clientSide="jasper.kingdom.client.ClientProxy", serverSide="jasper.kingdom.CommonProxy")
      public static CommonProxy proxy;

      
      @EventHandler
      public void preInit(FMLPreInitializationEvent event) {
      	
      }
      
      @EventHandler
      public void load(FMLInitializationEvent event) {
      	 //Initialize key
      	 this.kbk = new KingdomBorderKey(key, repeat);
       	 proxy.registerRenderers();
              
      }
      
      @EventHandler
      public void postInit(FMLPostInitializationEvent event) {
      	//Add this module to the list of event things to check
       	MinecraftForge.EVENT_BUS.register(new KingdomBorder());   
        KeyBindingRegistry.registerKeyBinding(this.kbk);
      }
      
      
      
      @ForgeSubscribe
      public void onLivingUpdateEvent(LivingUpdateEvent event)
      {
      	if (event.entity instanceof EntityPlayer) 
      	{
      		EntityPlayer player = (EntityPlayer) event.entity;
      		if ( player.dimension == 0 ) {
      		
      			
      			//Button check - force re-check
      			if ( KingdomBorderKey.kbdr ) {
      				prevKingdom = -2;
      				this.count = 30;
      				KingdomBorderKey.kbdr = false;	        				
      			}
      			
        		//Every 50 ticks..
        		if (this.count > 50 ) {
        			count = 0;

    	        	
    	        	
  	        	//Figure out location of player, and distance/angle from origin
  	        	px = (double)player.getPlayerCoordinates().posX;
  	        	py = (double)player.getPlayerCoordinates().posZ;
  	        	dist = Math.sqrt(px*px+py*py);
  	        	angle = Math.atan2(py,px);
  	        	
  	        	//normalize angle between 0 and 2pi
  	        	if ( angle < 0 ) { angle += pi2; }
    	    		angle = angle % pi2; 
    	        	
    	    		//First, determine whether we're within the inner/outer ring, distance-wise
    	    		flag = 0; //no ring detected
    	    		if (dist > 100 && dist < 500) { flag = 1; } //Inner ring
    	    		if (dist > 1500) { flag = 2; } 				//Outer ring
    	    		
    	    		    	    		
    	    		//Found we're in a ring - Do spoke check
    	    		if ( flag > 0 ) { 
    	    		
    	    		    //Second, check spokes    	    
    	    			section = 0; 	//Counterclockwise - check the 8 "slices"
    	    			found = true; 	//Start by assuming we're not in a spoke
    	    			
    	    			//Spoke check - disprove above assumption
    	    			for(double r = 0; r < pi2; r += pi2 / 8) {
    	    				dt = r - angle; 			//angle difference
    	    				df = dist * Math.sin(dt); 	//distance from spoke
    	    				df = Math.abs(df); 			//absolute distance from spoke center
    	    				
    	    				//... in a spoke - assumption disproved
    	    				if ( df < (flag == 1 ? 25 : 200) ) { //inside a spoke-width (inner and outer spoke widths considered)
    	    					found = false; 
    	    					break;
    	    				}
    	    			} 
    	    			
    	    			//If the above assumption wasn't disproved, continue, and see what sector we're in
    	    			if ( found ) {
	    	    			
    	    				//Scan through the 8 sector "slices"
	    	    			for(double r = 0; r < pi2; r += pi2 / 8) {
	    	    				dt = r - angle; //angle difference
	    	    			
    	    					//We're in this section
    	    					if ( angle > r && angle < r + pi2 / 8) {
    	    					
	    	    					if ( section != prevKingdom ) { //We entered somewhere new
	    	    						
	    	    						prevKingdom = section;
	    	    						
	    	    						if ( flag == 2) { //outer ring
	    	    							player.addChatMessage("You've entered Kingdom " + kingdom(section));
	    	    						} else { //inner ring
	    	    							player.addChatMessage("You've entered Origin Plot " + kingdom(section));	    	    						
	    	    						}
	    	    						
	    	    						
	    	    					}
    	    	    	    break;
    	    					
    	    					}
	    	    			
	    	    				section++;
	    	    			}
    	    			} else { //We were in a spoke - we're in neutral territory
	    	    			if ( prevKingdom != -1 ) { //If this has changed
	    	    				player.addChatMessage("You've entered neutral territory.");
	    	    				prevKingdom = -1;
	    	    			}
    	    			}
    	    			    	    			
    	    		
    	    		} else { //We were not in a ring - we're in neutral territory
    	    			if ( prevKingdom != -1 ) {
    	    				player.addChatMessage("You've entered neutral territory.");
    	    				prevKingdom = -1;
    	    			}	    	    			
    	    		}
    	    		
    	    		
      			
        		}
        		count++;
      		
      		}
      	}
      	
      }

      
      private void log(String s) {
      	ModLoader.getLogger().log(Level.INFO, s);
      }
      
      private String kingdom(int which) {
      	switch(which) {
        	case -1: return "Neutral Territory";
        	case 7: return "East North-East (Sector 8)";
        	case 6: return "North North-East (Sector 7)";
        	case 5: return "North North-West (Sector 6)";
        	case 4: return "West North-West (Sector 5)";
        	case 3: return "West South-West (Sector 4)";
        	case 2: return "South South-West (Sector 3)";
        	case 1: return "South South-East (Sector 2)";
        	case 0: return "East South-East (Sector 1)";
       	}
      	return "----";
      }
  
}
