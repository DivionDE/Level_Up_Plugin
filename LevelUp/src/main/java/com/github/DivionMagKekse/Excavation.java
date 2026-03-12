package com.github.DivionMagKekse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Excavation {
	
	public static boolean isFallable(Block block) {
		return block.getType().hasGravity();
	}
	public static void sedimentMiner(Main_LevelUp main, Player player, Block block) {
		ItemStack tool = player.getInventory().getItemInMainHand();
		UUID playerID = player.getUniqueId();
		Block above = block.getRelative(0, 1, 0);
		ArrayList<Block> blocks = new ArrayList <>();
		while(block.getType().equals(above.getType())) {
			blocks.add(block);
			block = above;
			above = block.getRelative(0, 1, 0);
			main.removeDurability(player);
			if(!tool.equals(player.getInventory().getItemInMainHand())){
				break;
			}
		}
		blocks.add(block);
		while(!blocks.isEmpty()) {
			Block temp_block = blocks.removeLast();
			Collection<ItemStack> drops = temp_block.getDrops(tool, player);
			drops.addAll(main.getExtraItemDrop("excavation", playerID, drops, temp_block.getLocation()));
			main.xpProcess("excavation", playerID, temp_block.getType());
			player.incrementStatistic(Statistic.MINE_BLOCK, temp_block.getType());
			for(ItemStack drop : drops){
				temp_block.getWorld().dropItemNaturally(temp_block.getLocation(), drop);
			}
			temp_block.breakNaturally(tool, true, true);
		}
	}
}
