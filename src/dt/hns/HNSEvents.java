package dt.hns;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

public class HNSEvents implements Listener{

	public HNSEvents(HNSMain plugin){
		bukkitplugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		server = plugin.getServer();
	}
	
//	@EventHandler
//	public void onMove(PlayerMoveEvent e){
//		Player player = e.getPlayer();
//		if(HNSMain.gamePlayer.contains(player)){
//			if(!HNSMain.Hunter.contains(player))
//				player.setSneaking(true);
//		} else
//			player.setSneaking(false);
//	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		Player player = e.getEntity();
		World world = player.getWorld();
		if(HNSMain.gamePlayer.contains(player)){//If the player is playing HideNSeek
			if(HNSMain.Hunter.contains(player)){//If the player that died is a HNS hunter
				//Reset the player
				player.getInventory().clear();
				for (PotionEffect effect : player.getActivePotionEffects())
					player.removePotionEffect(effect.getType());
				player.teleport(new Location(world,200,201,306));
				player.setGameMode(GameMode.SURVIVAL);
				
				//Remove the player from the match
				HNSMain.gamePlayer.remove(player);
				HNSMain.Hunter.remove(player);
				server.broadcastMessage(ChatColor.GOLD+"[HIDENSEEK]: Hunter "
				+ChatColor.AQUA+player.getName()
				+ChatColor.WHITE+" has been permanently removed from the match.");
				/*
				 * If there are no more hunters remaining
				 */
				if(HNSMain.Hunter.isEmpty()){
					server.broadcastMessage(ChatColor.GOLD+"[HIDENSEEK]: "+
							ChatColor.WHITE+" All hunters has been eliminated.");
					for(int i=0;i<HNSMain.gamePlayer.size();i++){
						for (PotionEffect effect : HNSMain.gamePlayer.get(i).getActivePotionEffects())
							HNSMain.gamePlayer.get(i).removePotionEffect(effect.getType());
							HNSMain.gamePlayer.get(i).getInventory().clear();
							HNSMain.gamePlayer.get(i).teleport(new Location(world,200,201,306));
							HNSMain.gamePlayer.get(i).setGameMode(GameMode.SURVIVAL);
					  }
					server.getScheduler().cancelTasks(bukkitplugin);
					HNSMain.ResetGame();
				}
			} else {//Else if the player is a hider
				player.setHealth(20);
				player.setFoodLevel(20);
				player.teleport(player);
				//player.setSneaking(false);
				HNSMain.Hunter.add(player);
				HNSMain.Hider.remove(player);
				if(HNSMain.Hider.isEmpty()){//If there are no more hiders
					server.broadcastMessage(ChatColor.GOLD+"[HIDENSEEK]: "+
							ChatColor.WHITE+" All hiders has been eliminated.");
					for(int i=0;i<HNSMain.gamePlayer.size();i++){
						for (PotionEffect effect : HNSMain.gamePlayer.get(i).getActivePotionEffects())
							HNSMain.gamePlayer.get(i).removePotionEffect(effect.getType());
							HNSMain.gamePlayer.get(i).getInventory().clear();
							HNSMain.gamePlayer.get(i).teleport(new Location(world,200,201,306));
							HNSMain.gamePlayer.get(i).setGameMode(GameMode.SURVIVAL);
					  }
					server.getScheduler().cancelTasks(bukkitplugin);
					HNSMain.ResetGame();
				} else {//Else if there are still hiders
				player.getInventory().clear();
				//Clear their potion effects(running/jumping/night vision)
				for (PotionEffect effect : player.getActivePotionEffects())
					player.removePotionEffect(effect.getType());
				//Give them the hunter potion effect
				addpotioneffect:
					for(int i=0;i<HNSMain.Hunter.size();i++){
				player.addPotionEffects(HNSMain.Hunter.get(i).getActivePotionEffects());
				
				if(player.getActivePotionEffects() != null)
					break addpotioneffect;
				}
				//Give the new hunters their equipment
				player.getOpenInventory().setItem(38, HNSMain.Pointer);
				player.getOpenInventory().setItem(9, new ItemStack(Material.ARROW));
				player.getOpenInventory().setItem(37, HNSMain.Food);//2nd slot
				player.getOpenInventory().setItem(36, HNSMain.GodBow);//1st slot
				player.setGameMode(GameMode.ADVENTURE);
				//HNSMain.tHunt.addEntry(player.getName());
				server.broadcastMessage(ChatColor.GOLD+"[HIDENSEEK]: Hider "
				+ChatColor.AQUA+player.getName()
				+ChatColor.WHITE+" is now a hunter.");
				player.sendMessage("If you die as a hunter, you are removed from this match.");
			    }
			}
		}
	}
	
	@EventHandler
	public void onLogout(PlayerQuitEvent e){
		Player player = e.getPlayer();
		World world = player.getWorld();
		if(HNSMain.gamePlayer.contains(player)){
			HNSMain.gamePlayer.remove(player);
			if(HNSMain.Hunter.contains(player)) 
				HNSMain.Hunter.remove(player);
			if(HNSMain.Hunter.isEmpty()){//If there are no more hunters
				server.broadcastMessage(ChatColor.GOLD+"[HIDENSEEK]: "+
						ChatColor.WHITE+" All hunters has been eliminated.");
				for(int i=0;i<HNSMain.gamePlayer.size();i++){
					for (PotionEffect effect : HNSMain.gamePlayer.get(i).getActivePotionEffects())
						HNSMain.gamePlayer.get(i).removePotionEffect(effect.getType());
						HNSMain.gamePlayer.get(i).getInventory().clear();
						HNSMain.gamePlayer.get(i).teleport(new Location(world,200,201,306));
						HNSMain.gamePlayer.get(i).setGameMode(GameMode.SURVIVAL);
				  }
				server.getScheduler().cancelTasks(bukkitplugin);
				HNSMain.ResetGame();
			}
			if(HNSMain.Hider.isEmpty()){//If there are no more hiders
				server.broadcastMessage(ChatColor.GOLD+"[HIDENSEEK]: "+
						ChatColor.WHITE+" All hiders has been eliminated.");
				for(int i=0;i<HNSMain.gamePlayer.size();i++){
					for (PotionEffect effect : HNSMain.gamePlayer.get(i).getActivePotionEffects())
						HNSMain.gamePlayer.get(i).removePotionEffect(effect.getType());
						HNSMain.gamePlayer.get(i).getInventory().clear();
						HNSMain.gamePlayer.get(i).teleport(new Location(world,200,201,306));
						HNSMain.gamePlayer.get(i).setGameMode(GameMode.SURVIVAL);
				  }
				server.getScheduler().cancelTasks(bukkitplugin);
				HNSMain.ResetGame();
			}
		}
		if(HNSMain.getHOST() != null && HNSMain.getHOST() == player)
			HNSMain.setHOST(null);
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent e){
		Entity user = e.getPlayer();
		int revealID = -1;
		if(user instanceof Player) {
		Player player = (Player) user;
		//If the player is ingame and is a hunter
		if(HNSMain.gamePlayer.contains(player) && HNSMain.Hunter.contains(player)){
			//If the players right clicks the pointer
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK ||e.getAction() == Action.RIGHT_CLICK_AIR) {
//				if(e.getItem().getItemMeta().getDisplayName().equals(HNSMain.Pointer.getItemMeta().getDisplayName())){
				//if(player.getItemInHand().equals(HNSMain.Pointer)){
				if(player.getEquipment().getItemInMainHand().equals(HNSMain.Pointer)){
				if(!HNSMain.Hider.isEmpty()) {//If there are more than 1 hiders
					revealID = (int)((HNSMain.Hider.size()-1)*Math.random()+0);
					player.playSound(HNSMain.Hider.get(revealID).getLocation(), Sound.ENTITY_WOLF_HOWL, 0.75f, 1);
					HNSMain.Hider.get(revealID).playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 0.75f, 1);
					revealID = -1;
				}
				}
			}
		}
	}
	}
	
//	  @EventHandler
//	    public void onTestEntityDamage(EntityDamageEvent e) {
//	        //TODO Disable PVP
//		  Entity damager = e.getEntity();
//		  if(damager instanceof Player) {
//		  Player player = (Player) damager;
//		  if(HNSMain.gamePlayer.contains(player) && player.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY)){
//			  e.setCancelled(true);
//		  }
//		  }
//	    }
	  
	private Server server = null;
	private Plugin bukkitplugin = null;
}
