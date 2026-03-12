package com.github.DivionMagKekse.Events;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.github.DivionMagKekse.Excavation;
import com.github.DivionMagKekse.Main_LevelUp;
import com.github.DivionMagKekse.Mining;
import com.github.DivionMagKekse.WoodCutting;
import com.github.DivionMagKekse.Config.Config;
import com.github.DivionMagKekse.Data.Data;

public class BlockBreak implements Listener{
	Main_LevelUp main;
	Data data;
	Config config;
	public BlockBreak(Main_LevelUp main) {
		this.main = main;
		this.data = main.getMyData();
		this.config = main.getMyConfig();
		
	}
	int blocks = 1;
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		
		Block block = event.getBlock();
		Material blockType = block.getType();	
		String skill = "";
		
		if(data.isPlayerPlaced(block)) {
			data.removePlayerPlaced(block);
			return;
		}else if(config.isSkillBlock("mining", blockType) && !data.isPlayerPlaced(block)) {
			skill = "Mining";	
		}else if(config.isSkillBlock("woodcutting", blockType) && !data.isPlayerPlaced(block)) {
			skill = "Woodcutting";	
		}else if(config.isSkillBlock("farming", blockType) && !data.isPlayerPlaced(block)) {
			skill = "Farming";		
		}else if(config.isSkillBlock("excavation", blockType) && !data.isPlayerPlaced(block)) {
			skill = "Excavation";
		}

		Player player = event.getPlayer();
		UUID playerID = player.getUniqueId();	
		ItemStack item = player.getInventory().getItemInMainHand();
		int xp = event.getExpToDrop();
		Collection <ItemStack> drops = block.getDrops(item, player);

		if(skill.equalsIgnoreCase("Farming") || skill.equalsIgnoreCase("Mining") || skill.equalsIgnoreCase("Woodcutting") || skill.equalsIgnoreCase("Excavation")) {
			if(Mining.isOre(blockType) && main.canUseAbility(skill, "veinminer", playerID) && player.isSneaking() 
					&& main.getAbilityStatus(playerID, "veinminer")) {		
				event.setCancelled(true);
				Mining.veinMiner(main, player, block);
			}else if(skill.equals("Woodcutting") && player.isSneaking() 
					&& main.canUseAbility(skill, "woodchopper", playerID) && main.getAbilityStatus(playerID, "woodchopper")) {
				event.setCancelled(true);
				WoodCutting.woodChopper(main, player, block);
			}else if(skill.equals("Excavation") && player.isSneaking() && Excavation.isFallable(block) 
					&& main.canUseAbility(skill, "sedimentMiner", playerID) && main.getAbilityStatus(playerID, "sedimentminer")) {
				event.setCancelled(true);
				Excavation.sedimentMiner(main, player, block);
			}else{
				switch(blockType){
					case SUGAR_CANE, BAMBOO, KELP, CACTUS, TWISTING_VINES:
						drops.addAll(main.getStackingPlantDrops(skill, block, playerID, drops));
						main.xpProcess(skill, playerID, blockType);
						break;
					case WEEPING_VINES:
						drops.addAll(main.getHangingPlantDrops(skill, block, playerID, drops));
						main.xpProcess(skill, playerID, blockType);
						break;
					case CHORUS_FLOWER, CHORUS_FRUIT, CHORUS_PLANT:
						drops.addAll(main.getChorusPlantDrops(skill, block, playerID, drops));
						main.xpProcess(skill, playerID, blockType);
						break;
					default:
						drops.addAll(main.getExtraItemDrop(skill, playerID, drops, block.getLocation()));
						event.setExpToDrop(xp+main.getExtraXPDrop(skill, playerID, xp));
						main.xpProcess(skill, playerID, blockType);
				}
				for(ItemStack drop: drops){
					block.getWorld().dropItemNaturally(block.getLocation(), drop);
				}		
			}
			
		}else if(block.getType() == Material.SPAWNER && player.getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
			event.setCancelled(true);
			ItemStack spawner = new ItemStack(Material.SPAWNER);
			block.getWorld().dropItemNaturally(block.getLocation(), spawner);
			block.setType(Material.AIR);
			
		}
	}
	
	
	
}
