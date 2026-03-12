package com.github.DivionMagKekse;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Statistic;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


public class Mining {

	public static final List<Material> stones = List.of(
        // Stone and Stone-like
        Material.STONE, Material.COBBLESTONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE,
        Material.TUFF, Material.CALCITE, Material.BASALT, Material.END_STONE, Material.BLACKSTONE,
        Material.DEEPSLATE, Material.NETHERRACK,

        // Terracotta & Concrete
        Material.TERRACOTTA, Material.WHITE_TERRACOTTA, Material.ORANGE_TERRACOTTA, Material.MAGENTA_TERRACOTTA,
        Material.LIGHT_BLUE_TERRACOTTA, Material.YELLOW_TERRACOTTA, Material.LIME_TERRACOTTA,
        Material.PINK_TERRACOTTA, Material.GRAY_TERRACOTTA, Material.LIGHT_GRAY_TERRACOTTA,
        Material.CYAN_TERRACOTTA, Material.PURPLE_TERRACOTTA, Material.BLUE_TERRACOTTA,
        Material.BROWN_TERRACOTTA, Material.GREEN_TERRACOTTA, Material.RED_TERRACOTTA,
        Material.BLACK_TERRACOTTA
        
	);

	public static final List<Material> ores = List.of(
        // Ores
        Material.COAL_ORE, Material.IRON_ORE, Material.COPPER_ORE, Material.GOLD_ORE,
        Material.LAPIS_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.REDSTONE_ORE,
        Material.NETHER_QUARTZ_ORE, Material.NETHER_GOLD_ORE,
        Material.DEEPSLATE_COAL_ORE, Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_COPPER_ORE,
        Material.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_DIAMOND_ORE,
        Material.DEEPSLATE_EMERALD_ORE, Material.DEEPSLATE_REDSTONE_ORE,
        Material.ANCIENT_DEBRIS,

        // Blocks of raw/valuable resources
        Material.RAW_IRON_BLOCK, Material.RAW_COPPER_BLOCK, Material.RAW_GOLD_BLOCK,

        // Misc (glass-like that require pickaxe)
        Material.AMETHYST_BLOCK, Material.BUDDING_AMETHYST
    );
	
	public static boolean isOre(Material block) {
		return ores.contains(block);
	}
	
	public static boolean isStone(Material block) {
		return stones.contains(block);
	}
	
	public static void veinMiner(Main_LevelUp main, Player player, Block origin) {
		
		Material target = origin.getType();
	    if (!ores.contains(target)) return;

	    ItemStack oldTool = player.getInventory().getItemInMainHand();
	    if (oldTool == null || oldTool.getType().isAir()) return;

	    UUID uuid = player.getUniqueId();
	    World world = origin.getWorld();

	    Set<Block> visited = new HashSet<>();
	    Queue<Block> queue = new LinkedList<>();

	    visited.add(origin);
	    queue.add(origin);

	    final int MAX_PER_TICK = 10; // change if you want faster/slower

	    final int[] taskId = new int[1];
	    taskId[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {

	        // cancel if player changed tool
	        if (!player.isOnline() || player.getInventory().getItemInMainHand().getType() != oldTool.getType()) {
	            Bukkit.getScheduler().cancelTask(taskId[0]);
				queue.clear();
	            return;
	        }

	        int processed = 0;

	        while (!queue.isEmpty() && processed < MAX_PER_TICK) {
	        	ItemStack tool = player.getInventory().getItemInMainHand();
				if (tool.getType() != oldTool.getType()) {
					break;
				}
	            Block current = queue.poll();
				Collection <ItemStack> drops = current.getDrops(tool, player);
	            drops.addAll(main.getExtraItemDrop("Mining", uuid, current.getDrops(), current.getLocation()));
				Location loc = current.getLocation();

				main.xpProcess("Mining", uuid, current.getType());
	            main.removeDurability(player);
				for(ItemStack drop : drops){
					world.dropItemNaturally(loc, drop);
				}
				player.incrementStatistic(Statistic.MINE_BLOCK, target);
	            current.breakNaturally(oldTool, true, true);
	            processed++;

	            int x = current.getX(), y = current.getY(), z = current.getZ();
	            for (int dx = -1; dx <= 1; dx++) {
	                for (int dy = -1; dy <= 1; dy++) {
	                    for (int dz = -1; dz <= 1; dz++) {

	                        if (dx == 0 && dy == 0 && dz == 0) continue;
	                        Block neighbor = world.getBlockAt(x + dx, y + dy, z + dz);

	                        if (!visited.contains(neighbor) && neighbor.getType() == target) {
	                            visited.add(neighbor);
	                            queue.add(neighbor);
	                        }
	                    }
	                }
	            }
	        }

	        // done?
	        if (queue.isEmpty()) {
	            Bukkit.getScheduler().cancelTask(taskId[0]);
	        }

	    }, 0L, 2L); // run every 2 ticks (0.1 sec)
	}
	
	public static void oreSight(Player player, int level, Main_LevelUp main) {
		for(PotionEffect effect : player.getActivePotionEffects()) {
			PotionEffectType effectType = effect.getType();
			if(effectType.equals(PotionEffectType.INVISIBILITY)) {
				return;
			}
		}
		int numP = 6;	//defines Number of Particles
		final Location loc = player.getEyeLocation();
		final double px = loc.getX();
		final double py = loc.getY();
		final double pz = loc.getZ();
		final World world = player.getWorld();
		final int range = Math.max(level/100, 3);
		Bukkit.getScheduler().runTaskAsynchronously(main, () ->	{
			for(int x = -range; x<range; x++) {
				for(int y = -range; y<range; y++) {
					for(int z = -range; z<range; z++) {
						
						double bx = (x + px);
						double by = (y + py);
						double bz = (z + pz);
						Material block = world.getType((int)bx, (int)by, (int)bz);
						if(isOre(block)) {
							Color color;
							switch (block) {
						    case COAL_ORE, DEEPSLATE_COAL_ORE ->
						        color = Color.fromRGB(30, 30, 30);

						    case COPPER_ORE, DEEPSLATE_COPPER_ORE ->
						        color = Color.fromRGB(198, 114, 62);

						    case IRON_ORE, DEEPSLATE_IRON_ORE ->
						        color = Color.fromRGB(184, 140, 103);

						    case GOLD_ORE, DEEPSLATE_GOLD_ORE ->
						        color = Color.fromRGB(222, 207, 70);

						    case LAPIS_ORE, DEEPSLATE_LAPIS_ORE ->
						        color = Color.fromRGB(40, 80, 170);

						    case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE ->
						        color = Color.fromRGB(180, 10, 10);

						    case EMERALD_ORE, DEEPSLATE_EMERALD_ORE ->
						        color = Color.fromRGB(0, 180, 80);

						    case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE ->
						        color = Color.fromRGB(45, 190, 180);

						    case NETHER_GOLD_ORE ->
						        color = Color.fromRGB(230, 190, 70);

						    case NETHER_QUARTZ_ORE ->
						        color = Color.fromRGB(230, 220, 210);

						    case ANCIENT_DEBRIS ->
						        color = Color.fromRGB(90, 50, 40);

						    default ->
						        color = Color.fromRGB(255, 255, 255);
							}
							DustOptions dust = new DustOptions(color, 0.5f);
							Particle particle = Particle.DUST;
							double vx=bx-px;
							double vy=by-py;
							double vz =bz-pz;
							double vl = Math.sqrt(vx*vx+vy*vy+vz*vz);
							new BukkitRunnable() {
								int ticks = 0;
								@Override
								public void run() {
									if(vl>=2) {
										for(double p = 1; p<((double) numP/2.5)+1; p+=0.4) {
											double lp = p/vl;
											double pax = px+vx*lp;
											double pay = py+vy*lp;
											double paz = pz+vz*lp;
											world.spawnParticle(particle, pax, pay, paz, 3, dust);
										}
									}else {
										for(double p = 0.5; p<((double) numP/2.5)+0.5; p+=0.4) {
											double lp = p/vl;
											double pax = px+vx*lp;
											double pay = py+vy*lp-1;
											double paz = pz+vz*lp;
											world.spawnParticle(particle, pax, pay, paz, 3, dust);
										}
									}
									ticks++;
									if(ticks >= 20)cancel();
								}
							}.runTaskTimerAsynchronously(main, 0, 5);
						}
					}
				}
			}
		});
	}

}