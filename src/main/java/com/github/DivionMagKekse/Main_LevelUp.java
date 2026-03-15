package com.github.DivionMagKekse;

import java.awt.Color;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import com.github.DivionMagKekse.Commands.Leaderboard;
import com.github.DivionMagKekse.Commands.SetLevel;
import com.github.DivionMagKekse.Commands.Skills;
import com.github.DivionMagKekse.Config.Config;
import com.github.DivionMagKekse.Data.Data;
import com.github.DivionMagKekse.Commands.Abilities;
import com.github.DivionMagKekse.Events.AnvilPrepare;
import com.github.DivionMagKekse.Events.BlockBreak;
import com.github.DivionMagKekse.Events.BlockGrow;
import com.github.DivionMagKekse.Events.BlockPlace;
import com.github.DivionMagKekse.Events.Breeding;
import com.github.DivionMagKekse.Events.CreatureSpawn;
import com.github.DivionMagKekse.Events.CreeperExplode;
import com.github.DivionMagKekse.Events.EntityChangeBlock;
import com.github.DivionMagKekse.Events.MobDamaged;
import com.github.DivionMagKekse.Events.MobKill;
import com.github.DivionMagKekse.Events.PlayerJoin;
import com.github.DivionMagKekse.Events.PlayerLeave;
import com.github.DivionMagKekse.Events.RightClick;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;



public class Main_LevelUp extends JavaPlugin{
    
    public Map<UUID, BukkitTask> oreSightTasks = new HashMap<>();
    public Map<UUID, Map<String, Boolean>> abilities_user = new HashMap<>();
	private Map<UUID, Set<Block>> visited = new ConcurrentHashMap<>();		
    
    private BukkitTask saveTask;

	private Config config = new Config(this);

	private Data data = new Data(this);
	private static final ResourcePackInfo PACK_INFO = ResourcePackInfo.resourcePackInfo().uri(URI.create(
												"https://github.com/DivionDE/Level_Up_Plugin/raw/refs/heads/master/src/main/Level_Up_resourcepack/Level_Up_Texture_Pack.zip"))
												.hash("7ad29ee0a4c36b3f362863fe3dc322eb1a0aab18").build();
    
	public void onEnable(){
		
		saveDefaultConfig();
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
		Bukkit.getPluginManager().registerEvents(new BlockBreak(this), this);
		Bukkit.getPluginManager().registerEvents(new BlockPlace(this), this);
		Bukkit.getPluginManager().registerEvents(new MobKill(this), this);
		Bukkit.getPluginManager().registerEvents(new MobDamaged(this), this);
		Bukkit.getPluginManager().registerEvents(new AnvilPrepare(), this);
		Bukkit.getPluginManager().registerEvents(new BlockGrow(this), this);
		Bukkit.getPluginManager().registerEvents(new Breeding(this), this);
		Bukkit.getPluginManager().registerEvents(new CreatureSpawn(this), this);
		Bukkit.getPluginManager().registerEvents(new RightClick(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerLeave(this), this);
		Bukkit.getPluginManager().registerEvents(new CreeperExplode(), this);
		Bukkit.getPluginManager().registerEvents(new EntityChangeBlock(), this);
		
		getCommand("set_level").setExecutor(new SetLevel(this));
		getCommand("leaderboard").setExecutor(new Leaderboard(this));
		getCommand("abilities").setExecutor(new Abilities(this));
		getCommand("skills").setExecutor(new Skills(this));
		
		saveTask = new BukkitRunnable() {
			@Override
			public void run() {
				data.saveData();
			}
		}.runTaskTimer(this, 20*60*10, 20*60*10);	

		config.configFileSetup();
		data.dataSetup();
		addAbilityAllUser();
		
		for(Player player : Bukkit.getOnlinePlayers()){	
			sendOptionalResourcePack(player);
		}
	}
	
	public void onDisable() {
		// Clear abilities map
		for(UUID playerID : new ArrayList<>(abilities_user.keySet())) {
			abilities_user.remove(playerID);
		}
		
		Map<BukkitTask, Block> scheduledTasks = data.getScheduledTasks();
		if(!scheduledTasks.isEmpty()) {
			for(BukkitTask task : new ArrayList<>(scheduledTasks.keySet())) {
				task.cancel();
				data.removePlayerPlaced(scheduledTasks.get(task));
			}
			scheduledTasks.clear();
		}
		if(!oreSightTasks.isEmpty()) {
			for(UUID key : new ArrayList<>(oreSightTasks.keySet())) {
				BukkitTask task = oreSightTasks.get(key);
				if(task != null) task.cancel();
			}
			oreSightTasks.clear();
		}
		if(saveTask != null) saveTask.cancel();
		data.saveData();
		Bukkit.getScheduler().cancelTasks(this);
	}
		
	public Config getMyConfig(){
		return config;
	}

	public Data getMyData() {
		return data;
	}
    
    
	

	public Collection<ItemStack> getExtraItemDrop(@NotNull String skill, @NotNull UUID playerID, @NotNull Collection <ItemStack> current_drops, Location loc){
		skill = skill.toLowerCase();
		List<ItemStack> drops = new ArrayList<>();
		int level = data.getPlayerLevel(playerID, skill);
		double chance = (double)level%1000/1000;
		int amount = level/1000;
		boolean alreadyParticle = false;
		for(ItemStack current_drop : current_drops){
			boolean more = false; 
			boolean added = false;
			if(chance >= ThreadLocalRandom.current().nextFloat()){
				amount ++;
				more = true;
			}
			if(loc != null && amount >= 1 && !alreadyParticle){
				alreadyParticle = true;
				loc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK,loc.getX(), loc.getY()+1, loc.getZ(), 10, 0.5, 0.5, 0.5, 0.25);
		}

			for(int pos = 0; pos<drops.size(); pos++){
				if(drops.get(pos).isSimilar(current_drop)){
					ItemStack temp_drop = drops.get(pos).clone();
					temp_drop.add(current_drop.clone().getAmount()*(amount-1));
					drops.add(temp_drop);
					drops.remove(pos);
					if(more){
						amount --;
					}
					added = true;
					break;
				}
			}
			if(added){
				continue;
			}
			drops.add(current_drop.clone().add(current_drop.getAmount()*(amount-1)));	
			if(more){
				amount --;
			}		
		}

		return drops;
	}

	public int getExtraXPDrop(@NotNull String skill, @NotNull UUID playerID, @NotNull int currentXP) {
		skill = skill.toLowerCase();
		int xp = 0;
		int level = data.getPlayerLevel(playerID, skill);
		double chance = (double)level/1000;
		float randNum = ThreadLocalRandom.current().nextFloat();
		while (chance >= randNum) {
			xp += currentXP;
			randNum = ThreadLocalRandom.current().nextFloat();
			chance--;
		}
		return xp;
	}

	public Collection <ItemStack> getStackingPlantDrops(String skill, Block block, UUID playerID, Collection <ItemStack> current_drops){
		Collection <ItemStack> drops = new ArrayList<>();
		Block above = block.getRelative(0, 1, 0);
		if(above.getType().equals(block.getType())){
            drops.addAll(getStackingPlantDrops(skill, above, playerID, current_drops));
			xpProcess(skill, playerID,  above.getType());
			drops.addAll(getExtraItemDrop(skill, playerID, current_drops, block.getLocation()));
			return drops;
        }else{
			xpProcess(skill, playerID,  above.getType());
			drops.addAll(getExtraItemDrop(skill, playerID, current_drops, block.getLocation()));
			return drops;
		}	
	}

	public Collection <ItemStack> getHangingPlantDrops(String skill, Block block, UUID playerID, Collection <ItemStack> current_drops){
		Collection <ItemStack> drops = new ArrayList<>();
		Block above = block.getRelative(0, -1, 0);
		if(above.getType().equals(block.getType())){
            drops.addAll(getHangingPlantDrops(skill, above, playerID, current_drops));
			xpProcess(skill, playerID,  above.getType());
			drops.addAll(getExtraItemDrop(skill, playerID, current_drops, block.getLocation()));
			return drops;
        }else{
			xpProcess(skill, playerID,  above.getType());
			drops.addAll(getExtraItemDrop(skill, playerID, current_drops, block.getLocation()));
			return drops;
		}
	}

	public Collection <ItemStack> getChorusPlantDrops(String skill, Block block, UUID playerID, Collection <ItemStack> current_drops){
		Collection <ItemStack> drops = new ArrayList<>();
		visited.putIfAbsent(playerID, ConcurrentHashMap.newKeySet());
		Material blockType = block.getType();
		World world = block.getWorld();
		Location loc = block.getLocation();

        final int[] taskCount = {0};
        final boolean[] nothingNew = {true};

		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			for (int ax = -1; ax <= 1; ax++) {
				for (int ay = 0; ay <= 1; ay++) {
					for (int az = -1; az <= 1; az++) {
                       	if (ax == 0 && ay == 0 && az == 0) continue;  // Skip self
							taskCount[0]++;
							final int x = ax, y = ay, z = az;
							Bukkit.getScheduler().runTask(this, () -> {
							Block current = world.getBlockAt(loc.clone().add(x, y, z));
                           	if (current.getType().equals(blockType) && visited.get(playerID).add(current)) {  // add() returns true if added
								drops.addAll(getExtraItemDrop(skill, playerID, current_drops, current.getLocation()));
								xpProcess(skill, playerID,  current.getType());
								nothingNew[0] = false;
							}
							taskCount[0]--;
							if (taskCount[0] == 0 && nothingNew[0]) {
                                visited.remove(playerID);
							}
						});
					}
				}
			}
		});
        return drops;
	}
	
	public void xpProcess(String skill, UUID playerID, Object object) {
		
	    skill = skill.toLowerCase();
		double currentXP = data.getCurrentXP(skill, playerID);
	    int level = data.getPlayerLevel(playerID, skill);
	    double objectxp = 0;
		if(object instanceof Material blockType){
			objectxp = config.getBlockXP(skill, blockType);
		}else if(object instanceof EntityType entityType){
			objectxp = config.getEntityXP(skill, entityType);
		}else{
			return;
		}
	    currentXP += objectxp;
	    double neededXP = data.getNeededXP(level);
	    data.saveXP(skill, currentXP, neededXP, playerID);	  
	}

	public void removeDurability(Player player) {
	    ItemStack tool = player.getInventory().getItemInMainHand();
	    if (tool == null || tool.getType() == Material.AIR)return;


	    ItemMeta meta = tool.getItemMeta();
	    if (!(meta instanceof Damageable))return;

	    Damageable damageable = (Damageable)meta;
	    if (tool.getType().getMaxDurability() == 0)return;

	    int level = tool.getEnchantmentLevel(Enchantment.UNBREAKING);
	    double chance = (double)100.0 / (level + 1);
	    double randomNum = Math.random() * 100;

	    if (randomNum <= chance) {
	        int currentDamage = damageable.getDamage()+1;
	        int maxDamage = tool.getType().getMaxDurability();

	        if (currentDamage + 1 >= maxDamage) {
	            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
	            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
	        } else {
	            damageable.setDamage(currentDamage);
	            tool.setItemMeta(damageable);
	            player.getInventory().setItemInMainHand(tool);
	        }
	    }
	}
	
	public void startOreSight(Player player) {
		UUID playerID = player.getUniqueId();
		BukkitTask task = Bukkit.getScheduler().runTaskTimer(this, ()->{
			int alevel = config.getAbilityLevel("mining", "oresight");
			if(alevel == -1) {
				stopOreSight(playerID);
				return;
			}
			int plevel = data.getPlayerLevel(playerID, "mining");
			if(plevel >= alevel && player.getLocation().getY()<63) {
				Mining.oreSight(player, plevel, this);
			}
		}, 10, 200);
		oreSightTasks.put(playerID, task);
	}
	
	public void stopOreSight(UUID playerID) {
		BukkitTask task = oreSightTasks.remove(playerID);
		if(task != null) {
			task.cancel();
		}	
	}
	
	public void addAbilityAllUser() {
		for(Player player : Bukkit.getOnlinePlayers()){
			addAbilityUser(player.getUniqueId());
		}
	}
	
	public void addAbilityUser(UUID playerID) {
		Map<String, Boolean> abi= new HashMap<>();
		for(String ability : config.getAllAbilities().keySet()) {
			String skill = config.getAbilitySkill(ability);
			if(canUseAbility(skill, ability, playerID)){
				abi.put(ability, true);
				continue;
			}
			abi.put(ability, false);
		}
		abilities_user.put(playerID, abi);
	}
	
	public void removeAbilityAllUser() {
		for(Player player : Bukkit.getOnlinePlayers()){
			removeAbilityUser(player.getUniqueId());
		}
	}
	
	public void removeAbilityUser(UUID playerID) {
		abilities_user.remove(playerID);
	}
	
	public void activateAbility(UUID playerID, String ability) {
		ability = ability.substring(0, 1).toUpperCase() + ability.substring(1).toLowerCase();
		Bukkit.getPlayer(playerID).sendMessage(ChatColor.GREEN + ability + " activated!");
		abilities_user.get(playerID).replace(ability.toLowerCase(), true);
		if(ability.equalsIgnoreCase("oresight")) startOreSight(Bukkit.getPlayer(playerID));	
	}
	
	public void deactivateAbility(UUID playerID, String ability) {
		ability = ability.substring(0, 1).toUpperCase() + ability.substring(1).toLowerCase();
		Bukkit.getPlayer(playerID).sendMessage(ChatColor.RED + ability + " deactivated!");
		abilities_user.get(playerID).replace(ability.toLowerCase(), false);
		if(ability.equals("oresight")) stopOreSight(playerID);
	}
	
	public void toggleAbilityStatus(UUID playerID, String ability) {
		ability = ability.toLowerCase();
		Boolean current = abilities_user.get(playerID).get(ability);
		String skill = config.getAbilitySkill(ability);
		if(current) {
			deactivateAbility(playerID, ability);
		}else if(canUseAbility(skill, ability, playerID)){
			abilities_user.get(playerID).replace(ability, !current);
			activateAbility(playerID, ability);
		}else {
			Bukkit.getPlayer(playerID).sendMessage(ChatColor.RED + "You dont have the requiered Level of:" + config.getAbilityLevel(skill, ability));
		}
	}
	
	public Boolean getAbilityStatus(UUID playerID, String ability) {
		return abilities_user.get(playerID).get(ability.toLowerCase());
	}
	
	
	public Boolean canUseAbility(String skill, String ability, UUID playerID) {
		int pLevel = data.getPlayerLevel(playerID, skill);
		int aLevel = config.getAbilityLevel(skill, ability);
		Boolean state = pLevel >= aLevel;
		return  state;
	}

	public Color getComplementaryColor(Color color){
		float[] hsb = new float[3];
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);

		hsb[0] = (hsb[0] + 0.5f) % 1.0f;

		return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
	}

	public void sendOptionalResourcePack(final @NotNull Audience target) {
  		final ResourcePackRequest request = ResourcePackRequest.resourcePackRequest()
    		.packs(PACK_INFO)
    		.prompt(Component.text("Please download the resource pack for the Progress_Bar."))
    		.required(false)
    		.build();

  		target.sendResourcePacks(request);
	}	
}
/*

	Excavation / Shovel Skills 
 
Treasure Finder – Chance, beim Graben wertvolle Items wie alte Münzen, Knochen oder seltene Artefakte zu finden.

Effect Shoveler – Gibt Haste Effekt

Archaeologist – Chance, beim Graben alte Relikte oder seltene Items zu entdecken (z. B. antike Scherben, XP-Orbs, vergrabene Schätze).

Soil Sense – Markiert (z. B. durch Partikeleffekt) nahe Schätze oder versteckte Erze unter Erde/Sand.


	🎣 Fishing / Water Skills

Lucky Catch – Higher chance for rare fish/items.

Fast Reel – Reduces time to catch a fish.

Treasure Hunter – Chance to catch treasure items like enchanted books or bows.
*/
