package de.dosmike.sponge.vshop;

import java.util.Optional;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class InteractionHandler {
	public static enum Button { left, right }
	
	/** return true to cancel the event in the parent */
	static boolean clickEntity(Player source, Entity target, Button side) {
		//try to get shop:
		Location<World> tl = target.getLocation();
		Optional<NPCguard> npc = VillagerShops.getNPCfromLocation(tl);
		
		if (npc.isPresent()) {
//			VillagerShops.l("NPC: " + npc.get().getDisplayName().toPlain());
			if (side == Button.right) {
				if (npc.get().getPreparator().size()>0) {
					source.openInventory(npc.get().getInventory(), Cause.builder().named("PLUGIN (Shop Opened)", VillagerShops.getInstance()).build());
					VillagerShops.openShops.put(source.getUniqueId(), npc.get().getIdentifier());
				}
			}
			
			return true;
		}
		return false;
	}
	
	/** return true to cancel the event in the parent */
	static boolean clickInventory(Player source, int slot) {
		//assume width of 9
//		int column=slot%9, row=slot/9;
		
		if (!VillagerShops.openShops.containsKey(source.getUniqueId())) {
			VillagerShops.l("No openShop");
			return false;
		}
		Optional<NPCguard> shop = VillagerShops.getNPCfromShopUUID(VillagerShops.openShops.get(source.getUniqueId()));
		if (!shop.isPresent()) {
			VillagerShops.l("No NPCguard");
			return false;
		}
		InvPrep stock = shop.get().getPreparator();
		
		int type = stock.isSlotBuySell(slot);
		int index = stock.slotToIndex(slot);
		if (type < 2) {
			int itemcount = stock.itemClicked(source, index, type);
//			ItemStack item = stock.getItem(index).getItem();
//			source.sendMessage(Text.of("You ", type==0?"bought":"sold", " ", itemcount, " ", item.get(Keys.DISPLAY_NAME).orElse(Text.of(item.getItem().getTranslation()))));
		}
		
		return true;
	}
}
