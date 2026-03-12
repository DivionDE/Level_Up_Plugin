package com.github.DivionMagKekse;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.DivionMagKekse.Config.Config;

public class WoodCutting {
	public final static List<Material> woods = List.of(
			Material.OAK_LOG,
		    Material.SPRUCE_LOG,
		    Material.BIRCH_LOG,
		    Material.JUNGLE_LOG,
		    Material.ACACIA_LOG,
		    Material.DARK_OAK_LOG,
		    Material.MANGROVE_LOG,
		    Material.CHERRY_LOG,
		    Material.CRIMSON_STEM,
		    Material.WARPED_STEM,
		    Material.PALE_OAK_LOG,
		    Material.RED_MUSHROOM_BLOCK,
		    Material.BROWN_MUSHROOM_BLOCK
			);
	private static final Set<Material> sources = EnumSet.of(
	        Material.OAK_SAPLING,
	        Material.SPRUCE_SAPLING,
	        Material.BIRCH_SAPLING,
	        Material.JUNGLE_SAPLING,
	        Material.ACACIA_SAPLING,
	        Material.DARK_OAK_SAPLING,
	        Material.MANGROVE_PROPAGULE,
	        Material.CHERRY_SAPLING,
	        Material.PALE_OAK_SAPLING,
	        Material.WARPED_FUNGUS,
	        Material.CRIMSON_FUNGUS,
	        Material.BROWN_MUSHROOM,
	        Material.RED_MUSHROOM
	);

	public static boolean isWood(Block block) {
		return woods.contains(block.getType());
	}
	
	public static boolean isSource(Block block) {
		return sources.contains(block.getType());
	}
	public static void woodChopper(Main_LevelUp main, Player player, Block block) {
		UUID playerID = player.getUniqueId();
		int maxLogs = main.getMyData().getPlayerLevel(playerID, "woodcutting");
		Config config = main.getMyConfig();
		World world = block.getWorld();
		Set<Block> visited = new HashSet<>();
		Queue<Block> queue = new LinkedList<>();
		ItemStack oldtool = player.getInventory().getItemInMainHand();
		visited.add(block);
		queue.add(block);
		String origin = block.getType().toString();
		int origin_pos = origin.indexOf("_");
		
		
		
		final int MAX_PER_TICK = 10;
		final int[] taskId = new int[1];
		final int[] totalProcessed = {0};
		taskId[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
			
			int processed = 0;
			
			if (!player.isOnline() || player.getInventory().getItemInMainHand().getType() != oldtool.getType()) {
	            Bukkit.getScheduler().cancelTask(taskId[0]);
	            return;
	        }
			
			while(!queue.isEmpty() && processed < MAX_PER_TICK) {
				ItemStack tool = player.getInventory().getItemInMainHand();
				if (tool.getType() != oldtool.getType()) {
					break;
				}
				Block current = queue.poll();
				String currentType = current.getType().toString();
				int current_pos = currentType.indexOf("_");
				if(origin.substring(0, origin_pos).equals(origin.substring(0, current_pos))) {
					if(!main.getMyData().isPlayerPlaced(current)) {
						Collection<ItemStack> drops = current.getDrops(oldtool, player);
						drops.addAll(main.getExtraItemDrop("Woodcutting", playerID, drops, current.getLocation()));
						main.xpProcess("Woodcutting", playerID, current.getType());
						main.removeDurability(player);
						player.incrementStatistic(Statistic.MINE_BLOCK, current.getType());

						for(ItemStack drop : drops){
							world.dropItemNaturally(current.getLocation(), drop);
						}

						if(isWood(current)) {
							current.breakNaturally(tool, true, true);
							totalProcessed[0]++;
						}else if(config.isSkillBlock("Woodcutting", current.getType())) {
							current.breakNaturally(tool, true, true);
						}
						processed++;
					}
					
					int x=current.getX(),y=current.getY(),z=current.getZ();
					for(int dx = -1; dx<=1; dx++) {
						for(int dy=-1; dy<=1; dy++) {
							for(int dz = -1; dz<=1; dz++) {
								Block temp_block = world.getBlockAt(x+dx, y+dy, z+dz);
								if(!visited.contains(temp_block) && config.isSkillBlock("Woodcutting", temp_block.getType())) {
									visited.add(temp_block);
									queue.add(temp_block);
								}
							}
						}
					}
					if(totalProcessed[0] >= maxLogs) {
						Bukkit.getScheduler().cancelTask(taskId[0]);
						break;
					}
				}	
			}
		}, 0L, 2L);
		

	}
}
