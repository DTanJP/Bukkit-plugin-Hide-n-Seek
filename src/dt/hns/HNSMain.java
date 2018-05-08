package dt.hns;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HNSMain extends JavaPlugin {

	public static int GAME_VERSION = 1;//Default is version 1
	
	@Override
	public void onEnable(){
		new HNSEvents(this);
		getLogger().info("Hide N Seek - Enabled");
	}
	
	@Override
	public void onDisable(){
		getLogger().info("Hide N Seek - Disabled");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		/*
		 * ::hns lobby
		 * ::hns ready
		 * ::hns start
		 * ::hns quit
		 * ::hns info
		 * ::hns reset
		 */
		
		Player player = (Player) sender;
		World world = player.getWorld();
		House[0] = new Location(world,394,63,347);//Wooden House
		House[1] = new Location(world,380,63,347);//Abandon Construction Site
		House[2] = new Location(world,405,63,368);//Mixtape Studio
		House[3] = new Location(world,379,66,369);//Water Pool
		House[4] = new Location(world,395,63,386);//Simon's Brick House
		House[5] = new Location(world,381,63,394);//Brick Apartment
		
		if(cmd.getName().equalsIgnoreCase("hns2") && sender instanceof Player){
			if(args.length > 0){
				if(args[0].equalsIgnoreCase("lobby")) {
					if(!inLobby(player)) {
					player.teleport(new Location(world,390,95,348));
					player.sendMessage(ChatColor.GOLD+"[HideNSeek2]: "+ChatColor.WHITE + "Welcome to Hide N Seek 2 Lobby");
					}
				} else if(args[0].equalsIgnoreCase("ready")){
					if(playerReady.size() == 0 && HOST == null){
						HOST = player;
						GAME_VERSION = 2;
						this.getServer().broadcastMessage(ChatColor.GOLD+"[HideNSeek2]: "+ChatColor.DARK_AQUA + player.getName()
											+ ChatColor.GOLD + " is now the Hide N Seek 2 Host.");
					}
					if(playerReady.size() < MAX_PLAYERS){//Prevent too many players from joining
						playerReady.add(player);     //Add to Ready list
						this.getServer().broadcastMessage(ChatColor.GOLD+"[HideNSeek2]: "+ChatColor.DARK_AQUA + player.getName()+ChatColor.GOLD+" has joined Hide N Seek.");
						player.sendMessage(ChatColor.GOLD + "[HIDE N SEEK]: Ready! Waiting for Host to start game.");
					} else
						player.sendMessage(ChatColor.RED + "Error: Maximum Capacity of "+MAX_PLAYERS+" players Reached");
					
				} else if(args[0].equalsIgnoreCase("start") && !playerReady.isEmpty()){
					if(player == getHOST() && getHOST() != null){
					if(playerReady.size() >= 2){//At least 2 players
						gameStart = true;
						GAME_VERSION = 2;
						InitGame();
						this.getServer().broadcastMessage(ChatColor.GOLD+"[HideNSeek2]: "+ChatColor.WHITE + "Hide N Seek Match started.");
					}
					} else {//If host dc
						setHOST(player);//The next player that uses this command becomes the host
						if(playerReady.size() >= 2){
							gameStart = true;
							InitGame();
							this.getServer().broadcastMessage(ChatColor.GOLD+"[HideNSeek2]: "+ChatColor.WHITE + "Hide N Seek Match started.");
						}
					}
				} else if(args[0].equalsIgnoreCase("quit")){
				if(player == getHOST())
					setHOST(null);
				if(playerReady.contains(player))
					playerReady.remove(player);
				if(gamePlayer.contains(player)){
					gamePlayer.remove(player);
					if(Hunter.contains(player))
						Hunter.remove(player);
					for (PotionEffect effect : player.getActivePotionEffects())
						player.removePotionEffect(effect.getType());
						player.getInventory().clear();
						player.teleport(new Location(player.getWorld(),200,201,306));
						player.setGameMode(GameMode.SURVIVAL);
				}
				if(HNSMain.Hunter.size() == 0){
					this.getServer().broadcastMessage(ChatColor.GOLD+"[HIDENSEEK2]: "+
							ChatColor.WHITE+" All hunters has been eliminated.");
					for(int i=0;i<HNSMain.gamePlayer.size();i++){
						for (PotionEffect effect : HNSMain.gamePlayer.get(i).getActivePotionEffects())
							HNSMain.gamePlayer.get(i).removePotionEffect(effect.getType());
							HNSMain.gamePlayer.get(i).getInventory().clear();
							HNSMain.gamePlayer.get(i).teleport(new Location(world,200,201,306));
							HNSMain.gamePlayer.get(i).setGameMode(GameMode.SURVIVAL);
					  }
					this.getServer().getScheduler().cancelTasks(this);
					HNSMain.ResetGame();
				}
				if(HNSMain.gamePlayer.size() - HNSMain.Hunter.size() == 0){//If there are no more hiders
					this.getServer().broadcastMessage(ChatColor.GOLD+"[HIDENSEEK2]: "+
							ChatColor.WHITE+" All hiders has been eliminated.");
					for(int i=0;i<HNSMain.gamePlayer.size();i++){
						for (PotionEffect effect : HNSMain.gamePlayer.get(i).getActivePotionEffects())
							HNSMain.gamePlayer.get(i).removePotionEffect(effect.getType());
							HNSMain.gamePlayer.get(i).getInventory().clear();
							HNSMain.gamePlayer.get(i).teleport(new Location(world,200,201,306));
							HNSMain.gamePlayer.get(i).setGameMode(GameMode.SURVIVAL);
					  }
					this.getServer().getScheduler().cancelTasks(this);
					HNSMain.ResetGame();
				}
				this.getServer().broadcastMessage(ChatColor.GOLD+"[HideNSeek2]: "+ChatColor.AQUA+player.getName()+" has left the match.");
				player.sendMessage("You have quit the match.");
				} else if(args[0].equalsIgnoreCase("reset")){
					if(!playerReady.isEmpty()) playerReady.clear();
					if(!gamePlayer.isEmpty()) gamePlayer.clear();
					if(getHOST() != null) setHOST(null);
					if(!Hunter.isEmpty()) Hunter.clear();
					if(!Hider.isEmpty()) Hider.clear();
					if(gameStart) gameStart = false;
					ResetGame();
					this.getServer().broadcastMessage(ChatColor.GOLD+"[HideNSeek2]: "+ChatColor.WHITE+" Match has been reset.");
					this.getServer().getScheduler().cancelTasks(this);
				} else if(args[0].equalsIgnoreCase("info")) {
				if(!playerReady.isEmpty()
				 ||!gamePlayer.isEmpty()
				 ||!Hunter.isEmpty()
				 ||!Hider.isEmpty()){
					
					if(!playerReady.isEmpty()){
					player.sendMessage(ChatColor.GOLD + "Ready: ");
					for(int i=0;i<playerReady.size();i++)
					player.sendMessage(i+": "+playerReady.get(i).getName());
					player.sendMessage(ChatColor.GOLD + "=====================");
					}
					
					if(!gamePlayer.isEmpty()) {
					player.sendMessage(ChatColor.GOLD + "In Game: ");
					for(int i=0;i<gamePlayer.size();i++)
					player.sendMessage(i+": "+gamePlayer.get(i).getName());
					}
					
					if(!Hunter.isEmpty()) {
					player.sendMessage(ChatColor.GOLD + "Hunters: ");
					for(int i=0;i<Hunter.size();i++)
					player.sendMessage(i+": "+Hunter.get(i).getName());
					}
					
					if(!Hider.isEmpty()) {
						player.sendMessage(ChatColor.GOLD + "Hiders: ");
						for(int i=0;i<Hider.size();i++)
						player.sendMessage(i+": "+Hider.get(i).getName());
					}
				} else {
					player.sendMessage(ChatColor.GOLD+"[HideNSeek2]: "+ChatColor.BLUE + "No one is playing Hide N Seek atm.");
				}
				} else {
					player.sendMessage(ChatColor.GOLD + "/hns2 <lobby:ready:start:quit:reset:info>");
				}
			} else {
				player.sendMessage(ChatColor.GOLD + "/hns2 <lobby:ready:start:quit:reset:info>");
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("getplayers") && sender instanceof Player){
			player.sendMessage(ChatColor.GOLD + "Ready Count: "+playerReady.size());
			player.sendMessage(ChatColor.GOLD + "Ingame Count: "+gamePlayer.size());
			player.sendMessage(ChatColor.GOLD + "Hunter Count: "+Hunter.size());
			player.sendMessage(ChatColor.GOLD + "Hider Count: "+Hider.size());
			for(int i=0;i<Hider.size();i++)
					player.sendMessage(ChatColor.GREEN+"Hider: "+ChatColor.WHITE+Hider.get(i).getName());
			for(int i=0;i<Hunter.size();i++)
					player.sendMessage(ChatColor.RED+"Hunter: "+ChatColor.WHITE+Hunter.get(i).getName());
		}
		
		if(cmd.getName().equalsIgnoreCase("hns") && sender instanceof Player){
			if(args.length > 0){
				if(args[0].equalsIgnoreCase("lobby")) {
					if(!inLobby(player)) {
					player.teleport(new Location(world,390,95,348));
					player.sendMessage(ChatColor.GOLD+"[HideNSeek]: "+ChatColor.WHITE + "Welcome to Hide N Seek Lobby");
					}
				} else if(args[0].equalsIgnoreCase("ready")){
					if(playerReady.size() == 0 && HOST == null){
						HOST = player;
						this.getServer().broadcastMessage(ChatColor.GOLD+"[HideNSeek]: "+ChatColor.DARK_AQUA + player.getName()
											+ ChatColor.GOLD + " is now the Hide N Seek Host.");
					}
					if(playerReady.size() < MAX_PLAYERS){//Prevent too many players from joining
						playerReady.add(player);     //Add to Ready list
						this.getServer().broadcastMessage(ChatColor.GOLD+"[HideNSeek]: "+ChatColor.DARK_AQUA + player.getName()+ChatColor.GOLD+" has joined Hide N Seek.");
						player.sendMessage(ChatColor.GOLD + "[HIDE N SEEK]: Ready! Waiting for Host to start game.");
					} else
						player.sendMessage(ChatColor.RED + "Error: Maximum Capacity of "+MAX_PLAYERS+" players Reached");
					
				} else if(args[0].equalsIgnoreCase("start") && !playerReady.isEmpty()){
					if(player == getHOST() && getHOST() != null){
					if(playerReady.size() >= 2){//At least 2 players
						gameStart = true;
						InitGame();
						this.getServer().broadcastMessage(ChatColor.GOLD+"[HideNSeek]: "+ChatColor.WHITE + "Hide N Seek Match started.");
					}
					} else {//If host dc
						setHOST(player);//The next player that uses this command becomes the host
						if(playerReady.size() >= 2){
							gameStart = true;
							InitGame();
							this.getServer().broadcastMessage(ChatColor.GOLD+"[HideNSeek]: "+ChatColor.WHITE + "Hide N Seek Match started.");
						}
					}
				} else if(args[0].equalsIgnoreCase("quit")){
				if(player == getHOST())
					setHOST(null);
				if(playerReady.contains(player))
					playerReady.remove(player);
				if(gamePlayer.contains(player)){
					gamePlayer.remove(player);
					if(Hunter.contains(player))
						Hunter.remove(player);
					for (PotionEffect effect : player.getActivePotionEffects())
						player.removePotionEffect(effect.getType());
						player.getInventory().clear();
						player.teleport(new Location(player.getWorld(),200,201,306));
						player.setGameMode(GameMode.SURVIVAL);
				}
				if(HNSMain.Hunter.size() == 0){
					this.getServer().broadcastMessage(ChatColor.GOLD+"[HIDENSEEK]: "+
							ChatColor.WHITE+" All hunters has been eliminated.");
					for(int i=0;i<HNSMain.gamePlayer.size();i++){
						for (PotionEffect effect : HNSMain.gamePlayer.get(i).getActivePotionEffects())
							HNSMain.gamePlayer.get(i).removePotionEffect(effect.getType());
							HNSMain.gamePlayer.get(i).getInventory().clear();
							HNSMain.gamePlayer.get(i).teleport(new Location(world,200,201,306));
							HNSMain.gamePlayer.get(i).setGameMode(GameMode.SURVIVAL);
					  }
					this.getServer().getScheduler().cancelTasks(this);
					HNSMain.ResetGame();
				}
				if(HNSMain.gamePlayer.size() - HNSMain.Hunter.size() == 0){//If there are no more hiders
					this.getServer().broadcastMessage(ChatColor.GOLD+"[HIDENSEEK]: "+
							ChatColor.WHITE+" All hiders has been eliminated.");
					for(int i=0;i<HNSMain.gamePlayer.size();i++){
						for (PotionEffect effect : HNSMain.gamePlayer.get(i).getActivePotionEffects())
							HNSMain.gamePlayer.get(i).removePotionEffect(effect.getType());
							HNSMain.gamePlayer.get(i).getInventory().clear();
							HNSMain.gamePlayer.get(i).teleport(new Location(world,200,201,306));
							HNSMain.gamePlayer.get(i).setGameMode(GameMode.SURVIVAL);
					  }
					this.getServer().getScheduler().cancelTasks(this);
					HNSMain.ResetGame();
				}
				this.getServer().broadcastMessage(ChatColor.GOLD+"[HideNSeek]: "+ChatColor.AQUA+player.getName()+" has left the match.");
				player.sendMessage("You have quit the match.");
				} else if(args[0].equalsIgnoreCase("reset")){
					if(!playerReady.isEmpty()) playerReady.clear();
					if(!gamePlayer.isEmpty()) gamePlayer.clear();
					if(getHOST() != null) setHOST(null);
					if(!Hunter.isEmpty()) Hunter.clear();
					if(!Hider.isEmpty()) Hider.clear();
					if(gameStart) gameStart = false;
					ResetGame();
					this.getServer().broadcastMessage(ChatColor.GOLD+"[HideNSeek]: "+ChatColor.WHITE+" Match has been reset.");
					this.getServer().getScheduler().cancelTasks(this);
				} else if(args[0].equalsIgnoreCase("info")) {
						player.sendMessage("gameStart status: "+gameStart);
				if(!playerReady.isEmpty()
				 ||!gamePlayer.isEmpty()
				 ||!Hunter.isEmpty()
				 ||!Hider.isEmpty()){
					
					if(!playerReady.isEmpty()){
					player.sendMessage(ChatColor.GOLD + "Ready: ");
					for(int i=0;i<playerReady.size();i++)
					player.sendMessage(i+": "+playerReady.get(i).getName());
					player.sendMessage(ChatColor.GOLD + "=====================");
					}
					
					if(!gamePlayer.isEmpty()) {
					player.sendMessage(ChatColor.GOLD + "In Game: ");
					for(int i=0;i<gamePlayer.size();i++)
					player.sendMessage(i+": "+gamePlayer.get(i).getName());
					}
					
					if(!Hunter.isEmpty()) {
					player.sendMessage(ChatColor.GOLD + "Hunters: ");
					for(int i=0;i<Hunter.size();i++)
					player.sendMessage(i+": "+Hunter.get(i).getName());
					}
					
					if(!Hider.isEmpty()) {
						player.sendMessage(ChatColor.GOLD + "Hiders: ");
						for(int i=0;i<Hider.size();i++)
						player.sendMessage(i+": "+Hider.get(i).getName());
					}
				} else {
					player.sendMessage(ChatColor.GOLD+"[HideNSeek]: "+ChatColor.BLUE + "No one is playing Hide N Seek atm.");
				}
				} else {
					player.sendMessage(ChatColor.GOLD + "/hns <lobby:ready:start:quit:reset:info>");
					player.sendMessage(ChatColor.GOLD + "/hns2 <lobby:ready:start:quit:reset:info>");
				}
			}
		}
		return false;
	}
	
	public void InitGame(){

		/*
		 * Preload the items
		 */
		ItemMeta meta = GodBow.getItemMeta();
		meta.setDisplayName("Hunter Bow");
		GodBow.setItemMeta(meta);
		
		ItemMeta metaPointer = Pointer.getItemMeta();
		metaPointer.setDisplayName("Pointer");
		Pointer.setItemMeta(metaPointer);
		
//		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
//		objective.setDisplayName(ChatColor.GOLD+"[HideNSeek]");
//		tHunt.setAllowFriendlyFire(false);
//		tHide.setAllowFriendlyFire(false);
//		tHide.setDisplayName("");
		
		GodBow.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1000);
		GodBow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1000);
		GodBow.addUnsafeEnchantment(Enchantment.DURABILITY, 1000);
		GodBow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		StickRun.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1000);
		
		gamePlayer.addAll(playerReady);
		playerReady.clear();
		Hunter.clear();
		Hider.clear();
		/*
		 * Start of the game
		 * Remove everyone's potion effects
		 * CLear their inventory
		 * Reset their health
		 * Make them invisible for 30 seconds
		 * Teleport them into a random house
		 * Make them into adventure mode
		 */
		for(int i=0;i<gamePlayer.size();i++){
		for (PotionEffect effect : gamePlayer.get(i).getActivePotionEffects())
				gamePlayer.get(i).removePotionEffect(effect.getType());
			gamePlayer.get(i).getInventory().clear();
			gamePlayer.get(i).setCanPickupItems(false);
			gamePlayer.get(i).setHealth(20);
			gamePlayer.get(i).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 2));
			gamePlayer.get(i).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 600, 2));
			gamePlayer.get(i).teleport(House[(int)(5*Math.random()+0)]);
			gamePlayer.get(i).setGameMode(GameMode.ADVENTURE);
			gamePlayer.get(i).sendMessage(ChatColor.GOLD+"[HideNSeek]: "+ChatColor.GREEN + "Round Start!");
		}
		/*
		 * After 30 seconds
		 * Add players to the hunter list
		 */
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			 
			public void run() {  
		int hunterID = (int)((gamePlayer.size()-1)*Math.random()+0);
		Hunter.add(gamePlayer.get(hunterID));
		//tHunt.addEntry(Hunter.get(0).getName());
		//Give the hunters their equips
		for(int i=0;i<Hunter.size();i++){
			Hunter.get(i).addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 18000, 2));
			Hunter.get(i).getOpenInventory().setItem(38, Pointer);
			Hunter.get(i).getOpenInventory().setItem(9, new ItemStack(Material.ARROW));
			Hunter.get(i).getOpenInventory().setItem(37, Food);//2nd slot
			Hunter.get(i).getOpenInventory().setItem(36, GodBow);//1st slot
			Hunter.get(i).sendMessage(ChatColor.GOLD+"[HideNSeek]: "+ChatColor.LIGHT_PURPLE + "You are the hunter! Hunt down the runners/hiders before 15 minutes!");
		}
		
		//Give the hiders their equips
		for(int i=0;i<gamePlayer.size();i++){
				if(gamePlayer.get(i) != gamePlayer.get(hunterID)){
					//tHide.addEntry(gamePlayer.get(i).getName());
					Hider.add(gamePlayer.get(i));
					gamePlayer.get(i).addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 18000, 1));
					gamePlayer.get(i).addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 18000, 2));
					gamePlayer.get(i).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 18000, 1));
					gamePlayer.get(i).getOpenInventory().setItem(37, Food);
					gamePlayer.get(i).getOpenInventory().setItem(36, StickRun);
					gamePlayer.get(i).sendMessage(ChatColor.GOLD+"[HideNSeek]: "+ChatColor.LIGHT_PURPLE + "Survive from the hunter for 15 minutes.");
					//gamePlayer.get(i).setSneaking(true);
				}
			}
			
		//Heal the players
			for(int i=0;i<gamePlayer.size();i++){
					gamePlayer.get(i).setFoodLevel(20);
					gamePlayer.get(i).setHealth(20);
			}
	}//End of run()
	}, 600);
		
		//After 15 minutes declare winner
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			  public void run() {
				  
				  for(int i=0;i<gamePlayer.size();i++){
					  gamePlayer.get(i).sendMessage(ChatColor.GOLD+"[HideNSeek]: "+ChatColor.WHITE+"Match has ended!");
					  
					  if(!Hider.isEmpty())//If hiders survive
					  gamePlayer.get(i).sendMessage(ChatColor.GOLD+"[HideNSeek]: "+ChatColor.GREEN + "Hider's Win!");
					  
					  for (PotionEffect effect : gamePlayer.get(i).getActivePotionEffects())
							gamePlayer.get(i).removePotionEffect(effect.getType());
					  
					gamePlayer.get(i).getInventory().clear();
					gamePlayer.get(i).teleport(new Location(gamePlayer.get(i).getWorld(),200,201,306));
					gamePlayer.get(i).setGameMode(GameMode.SURVIVAL);
					//gamePlayer.get(i).setSneaking(false);
				  }
				  
				  //Hard reset of the match
				  ResetGame();
			  }//End of void run()
			}, 18000);//18600 = 15 minutes
	}
	
	public void InitGame2(){
		/*
		 * Preload the items
		 */
		ItemMeta meta = Scythe.getItemMeta();
		meta.setDisplayName("Scythe");
		Scythe.setItemMeta(meta);
		
		ItemMeta metaPointer = Pointer.getItemMeta();
		metaPointer.setDisplayName("Pointer");
		Pointer.setItemMeta(metaPointer);
		
		ItemStack Nausea_Potion = new ItemStack(Material.POTION,1);
		((PotionMeta) Nausea_Potion.getItemMeta()).addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION,1200,1), true);
		
		ItemStack Blindness_Potion = new ItemStack(Material.POTION,1);
		((PotionMeta) Blindness_Potion.getItemMeta()).addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS,1200,1), true);
		
		ItemStack Slowness_Potion = new ItemStack(Material.POTION,1);
		((PotionMeta) Slowness_Potion.getItemMeta()).addCustomEffect(new PotionEffect(PotionEffectType.SLOW,1200,1), true);
		
		Scythe.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1000);
		
		gamePlayer.addAll(playerReady);
		playerReady.clear();
		Hunter.clear();
		Hider.clear();
		/*
		 * Start of the game
		 * Remove everyone's potion effects
		 * CLear their inventory
		 * Reset their health
		 * Make them invisible for 30 seconds
		 * Teleport them into a random house
		 * Make them into adventure mode
		 */
		for(int i=0;i<gamePlayer.size();i++){
		for (PotionEffect effect : gamePlayer.get(i).getActivePotionEffects())
				gamePlayer.get(i).removePotionEffect(effect.getType());
			gamePlayer.get(i).getInventory().clear();
			gamePlayer.get(i).setInvulnerable(true);
			gamePlayer.get(i).setCanPickupItems(false);
			gamePlayer.get(i).setHealth(20);
			gamePlayer.get(i).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 2));
			gamePlayer.get(i).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 600, 2));
			gamePlayer.get(i).teleport(House[(int)(5*Math.random()+0)]);
			gamePlayer.get(i).setGameMode(GameMode.ADVENTURE);
			gamePlayer.get(i).sendMessage(ChatColor.GOLD+"[HideNSeek2]: "+ChatColor.GREEN + "Round Start!");
		}
		/*
		 * After 30 seconds
		 * Add players to the hunter list
		 */
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			 
			public void run() {  
		int hunterID = (int)((gamePlayer.size()-1)*Math.random()+0);
		Hunter.add(gamePlayer.get(hunterID));
		//tHunt.addEntry(Hunter.get(0).getName());
		//Give the hunters their equips
		for(int i=0;i<Hunter.size();i++){
			Hunter.get(i).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 18000, 1));
			Hunter.get(i).addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 18000, 2));
			Hunter.get(i).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 18000, 2));
			Hunter.get(i).getOpenInventory().setItem(39, new ItemStack(Material.ENDER_PEARL,64));
			Hunter.get(i).getOpenInventory().setItem(38, Pointer);
			Hunter.get(i).getOpenInventory().setItem(37, Food);//2nd slot
			Hunter.get(i).getOpenInventory().setItem(36, Scythe);//1st slot
			Hunter.get(i).sendMessage(ChatColor.GOLD+"[HideNSeek2]: "+ChatColor.LIGHT_PURPLE + "You are the Reaper! Hunt down the sinners before 15 minutes!");
		}
		
		//Give the hiders their equips
		for(int i=0;i<gamePlayer.size();i++){
				if(gamePlayer.get(i) != gamePlayer.get(hunterID)){
					//tHide.addEntry(gamePlayer.get(i).getName());
					Hider.add(gamePlayer.get(i));
					gamePlayer.get(i).addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 18000, 2));
					gamePlayer.get(i).getOpenInventory().setItem(40, Slowness_Potion);
					gamePlayer.get(i).getOpenInventory().setItem(39, Blindness_Potion);
					gamePlayer.get(i).getOpenInventory().setItem(38, Nausea_Potion);
					gamePlayer.get(i).getOpenInventory().setItem(37, Food);
					gamePlayer.get(i).getOpenInventory().setItem(36, StickRun);
					gamePlayer.get(i).sendMessage(ChatColor.GOLD+"[HideNSeek2]: "+ChatColor.LIGHT_PURPLE + "Survive from the Reaper for 15 minutes.");
				}
			}
			
		//Heal the players
			for(int i=0;i<gamePlayer.size();i++){
					gamePlayer.get(i).setFoodLevel(20);
					gamePlayer.get(i).setHealth(20);
					gamePlayer.get(i).setInvulnerable(false);
			}
	}//End of run()
	}, 600);
		
		//After 15 minutes declare winner
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			  public void run() {
				  
				  for(int i=0;i<gamePlayer.size();i++){
					  gamePlayer.get(i).sendMessage(ChatColor.GOLD+"[HideNSeek2]: "+ChatColor.WHITE+"Match has ended!");
					  
					  if(!Hider.isEmpty())//If hiders survive
					  gamePlayer.get(i).sendMessage(ChatColor.GOLD+"[HideNSeek2]: "+ChatColor.GREEN + "Sinners Win!");
					  
					  for (PotionEffect effect : gamePlayer.get(i).getActivePotionEffects())
							gamePlayer.get(i).removePotionEffect(effect.getType());
					  
					gamePlayer.get(i).getInventory().clear();
					gamePlayer.get(i).teleport(new Location(gamePlayer.get(i).getWorld(),200,201,306));
					gamePlayer.get(i).setGameMode(GameMode.SURVIVAL);
				  }
				  
				  //Hard reset of the match
				  ResetGame();
			  }//End of void run()
			}, 18000);//18600 = 15 minutes
	}
	
	public static void ResetGame(){
		if(!gamePlayer.isEmpty())
		for(int i=0;i<gamePlayer.size();i++) {
			for (PotionEffect effect : gamePlayer.get(i).getActivePotionEffects())
			gamePlayer.get(i).removePotionEffect(effect.getType());
		gamePlayer.get(i).getInventory().clear();
		gamePlayer.get(i).setGameMode(GameMode.SURVIVAL);
		gamePlayer.get(i).setCanPickupItems(true);
		}
		GAME_VERSION = 1;
		playerReady.clear();
		Hunter.clear();
		Hider.clear();
		gamePlayer.clear();
		setHOST(null);
		gameStart = false;
		for(int i=0;i<gamePlayer.size();i++){
			if(!Hunter.contains((gamePlayer.get(i))))
				gamePlayer.get(i).setSneaking(false);
		}
	}
//	static ScoreboardManager teams = Bukkit.getScoreboardManager();
//    static Scoreboard board = teams.getNewScoreboard();
//    Objective objective = board.registerNewObjective("HideNSeek", "dummy");
//    static Team tHunt = board.registerNewTeam("Hunters");
//    static Team tHide = board.registerNewTeam("Hiders");
    
	public static ArrayList<Player> playerReady = new ArrayList<>();
	public static ArrayList<Player> gamePlayer = new ArrayList<>();
	static ArrayList<Player> Hunter = new ArrayList<>();
	static ArrayList<Player> Hider = new ArrayList<>();
	
	static ItemStack GodBow = new ItemStack(Material.BOW);
	static ItemStack Scythe = new ItemStack(Material.IRON_HOE);
	ItemStack StickRun = new ItemStack(Material.STICK);
	static ItemStack Pointer = new ItemStack(Material.ARROW);
	static ItemStack Food = new ItemStack(Material.COOKED_BEEF,64);
	
	Location[] House = new Location[6];
	private static Player HOST;
	
	static boolean gameStart = false;
	private final int MAX_PLAYERS = 10;//Only 10 player game
	
	private boolean inLobby(Player p){
		int x = p.getLocation().getBlockX();
		int y = p.getLocation().getBlockY();
		int z = p.getLocation().getBlockZ();
		if((y >= 95 && y < 99) && (x <= 392 && x >= 388) && (z <= 350 && z >= 346))
			return true;
		return false;
	}
	
	public static Player getHOST() {
		return HOST;
	}

	public static void setHOST(Player host) {
		HOST = host;
	}
}
