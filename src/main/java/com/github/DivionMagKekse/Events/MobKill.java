package com.github.DivionMagKekse.Events;

import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.github.DivionMagKekse.Main_LevelUp;
import com.github.DivionMagKekse.Config.Config;
import com.github.DivionMagKekse.Data.Data;
import com.github.DivionMagKekse.Combat;

public class MobKill implements Listener{
	Main_LevelUp main;
	Data data;
	public MobKill (Main_LevelUp main) {
		this.main = main;
		this.data = main.getMyData();
	}
	@EventHandler
	public void onKillEven(EntityDeathEvent event) {
		if(event.getEntity().getKiller() != null) {
			EntityType entity = event.getEntityType();
			Player player = event.getEntity().getKiller();
			UUID playerID = player.getUniqueId();
			String skill = "Combat";
			int level = data.getPlayerLevel(playerID, skill);
			List<ItemStack> drops = event.getDrops();
			Config config = main.getMyConfig();
			if(config.getEntitySkill(entity).equalsIgnoreCase("Combat")) {
				int dropped_xp = event.getDroppedExp();
				for(ItemStack drop : main.getExtraItemDrop(skill, playerID, drops, event.getEntity().getEyeLocation())){
					boolean hasDrop = false;
					for(int pos = 0; pos<drops.size(); pos++){
						if(drops.get(pos).isSimilar(drop)){
							ItemStack temp_drop = drops.get(pos);
							drops.add(temp_drop.add(drop.getAmount()));
							drops.remove(pos);
							hasDrop = true;
						}
					}
					if(hasDrop){
						continue;
					}
					drops.add(drop);
				}
				
				event.setDroppedExp(dropped_xp+main.getExtraXPDrop(skill, playerID, dropped_xp));
				main.xpProcess(skill, playerID, entity);
				
				Combat.randomEffect(main, skill, player);	
			}
			if (level >= 100) {
				int tier = config.getMobTier(entity);
				double baseChance;
				double perLevel;
				double maxChance;
				switch (tier) {
					case 1:
						baseChance = 0.0001; maxChance = 0.05; break; // tier1: slightly higher
					case 2:
						baseChance = 0.00005; maxChance = 0.025; break;
					case 3:
						baseChance = 0.00002; maxChance = 0.0125; break;
					case 4:
						baseChance = 0.00001; maxChance = 0.006125; break;
					default:
						baseChance = 0; maxChance = 0; break;
				}
				perLevel = (maxChance - baseChance) / 500.0;
				double chance = Math.min(maxChance, baseChance + level * perLevel);
				if (Math.random() < chance) {
					Material egg = Material.getMaterial(entity.name() + "_SPAWN_EGG");
					if (egg != null) {
						ItemStack spawn_egg = new ItemStack(egg);
						drops.add(spawn_egg);
					}
				}
			}
		}
	}
}
