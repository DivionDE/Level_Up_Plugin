package com.github.DivionMagKekse.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.github.DivionMagKekse.Main_LevelUp;
import com.github.DivionMagKekse.Helper.Progress_Bar;

public class Data {
    private File dataFile;
    private FileConfiguration data;

    private File placedDataFile;
    private FileConfiguration placedData;

    private Map<BukkitTask, Block> scheduledTasks = new HashMap<>();

    Main_LevelUp main;

    ArrayList<Double> neededXP = new ArrayList<>();
    final private double baseXP = 100.0;
    private double growthFactor = 1.0075;

    public Data(Main_LevelUp main) {
        this.main = main;
        
    }

    public File getDataFile() {
        return dataFile;
    }

    public FileConfiguration getData() {
        return data;
    }

    public File getPlacedDataFile() {
        return placedDataFile;
    }

    public FileConfiguration getPlacedData() {
        return placedData;
    }

    public void saveData() {
        try {
            data.save(dataFile);
            data.save(placedDataFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataSetup( ){
        dataFile = new File(main.getDataFolder(), "data.yml");
		placedDataFile = new File(main.getDataFolder(), "playerplaced.yml");
        if (!dataFile.exists()) {
            main.saveResource("data.yml", false);
        }
        if (!placedDataFile.exists()) {
            main.saveResource("playerplaced.yml", false);
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
        placedData = YamlConfiguration.loadConfiguration(placedDataFile);

        if(placedData.get("playerPlaced") == null){
            placedData.createSection("placedData");
        }

        // Checks if old syntax exists and converts it into new one
        if(placedData.get("Levels.playerPlaced") != null){ 
            placedData.set("playerPlaced", placedData.getList("Levels.playerPlaced"));
        }

        xpCalculation();
        saveData();
    }

    public double getNeededXP(int level) {
		if(level-1>2675) {
			return 200004.5;
		}
	    return neededXP.get(level-1); 
	}

    private void xpCalculation() {
		Bukkit.getScheduler().runTaskAsynchronously(main, ()->{
		double xp = baseXP;
		for (int level = 1; xp < 200000; level++) {
			if((level-500)%100 == 0 && level > 500){
				growthFactor -= 0.001;
				growthFactor = Math.max(growthFactor, 1.0005);
			}
			if (level > 1) {
				xp = xp * growthFactor;
			}
			neededXP.add(xp);
		}
		});
    }
    

    public int getPlayerLevel(UUID player, String skill) {
    	return data.getInt("Levels." + player + "." + skill.toLowerCase() + ".level");
    }

    public double getPrestigeLevel(UUID player, String skill) {
    	return data.getDouble("Levels." + player + "." + skill.toLowerCase() + ".prestige_level");
    }

    public double getCurrentXP(String skill, UUID player) {
    	return data.getDouble("Levels." + player + "." + skill.toLowerCase() + ".xp");
    }

    public void saveLevel(UUID player, String skill, int level) {
		data.set("Levels." + player + "." + skill.toLowerCase() + ".level", level);
	}

    public void saveXP(String skill, double currentXP, double neededXP, UUID playerID) {
		skill = skill.toLowerCase(); //Absolutely needed. Do not remove!!! removal causes saving problems
		int level = getPlayerLevel(playerID, skill);
		int i = 0;
		while(currentXP >= neededXP) {
			currentXP -= neededXP;
			level++;
			saveLevel(playerID, skill, level);
			Player player = Bukkit.getPlayer(playerID);
			if(i==0) {
				player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
			}
			data.set("Levels." + playerID + "." + skill + ".xp", currentXP);
			neededXP = getNeededXP(getPlayerLevel(playerID, skill));
			i++;
		}
		Progress_Bar.showXpBar(main, skill, playerID);
		data.set("Levels." + playerID + "." + skill + ".xp", currentXP);
	}

    public List<Map.Entry<UUID, Integer>> getAllPlayerData(String skill) {
		ConfigurationSection section = data.getConfigurationSection("Levels");
		if(section != null) {
			HashMap<UUID, Integer> playerdata = new HashMap<>();
			for(String uuidstr : section.getKeys(false)) {
				UUID uuid = UUID.fromString(uuidstr);
				int level = getPlayerLevel(uuid, skill.toLowerCase());	
				playerdata.put(uuid, level);
			}
			List<Map.Entry<UUID, Integer>> sortedList = new ArrayList<>(playerdata.entrySet());
			sortedList.sort(Map.Entry.<UUID, Integer>comparingByValue().reversed());
			return sortedList;
		}
		return null;
	}

    public void addPlayerPlaced(Main_LevelUp main, Block block) {
        String locKey = locToString(block.getLocation());
        placedData.set("playerPlaced." + locKey, true);
        if(main.getMyConfig().isSkillBlock("farming", block.getType())) {
            final BukkitTask[] taskHolder = new BukkitTask[1];
            taskHolder[0] = Bukkit.getScheduler().runTaskLater(main, () ->{
                removePlayerPlaced(block);
                scheduledTasks.remove(taskHolder[0]);
            }, 10000);
            scheduledTasks.put(taskHolder[0], block);
        }
    }
	
	public void removeTask(BukkitTask task) {
		scheduledTasks.remove(task);
	}
	
	public void removePlayerPlaced(Block block) {
		String locKey = locToString(block.getLocation());
		placedData.set("playerPlaced." + locKey, null);
	}

	public boolean isPlayerPlaced(Block block) {
	    String locKey = locToString(block.getLocation());
	    return placedData.getBoolean("playerPlaced." + locKey, false);
	}

    public Map<BukkitTask, Block> getScheduledTasks() {
        return scheduledTasks;
    }

    private String locToString(Location loc) {
	    return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
	}

    public void addPlayer(UUID playerID){
        for(String skill: main.getMyConfig().getAllSkills()) {
			if(data.get("Levels." + playerID + "." + skill) == null){
				data.set("Levels." + playerID + "." + skill + ".prestige_level", 1);
				data.set("Levels." + playerID + "." + skill + ".level", 1);
				data.set("Levels." + playerID + "." + skill + ".xp", 0);
			}
		}
    }

}
