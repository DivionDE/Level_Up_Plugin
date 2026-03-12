package com.github.DivionMagKekse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Farming {
    public static void elite_farmer(Main_LevelUp main, Block block, Player player){
        int distance = 0;
        UUID playerID = player.getUniqueId();
        if(main.getAbilityStatus(playerID, "elitefarmer") && 
        main.getMyConfig().getAbilityLevel("farming", "elitefarmer") >= 0){
            distance  = (int)main.getMyData().getPlayerLevel(playerID, "farming")/100;
        }
        ItemStack tool = player.getInventory().getItemInMainHand();
        List <ItemStack> drops = new ArrayList<>();
        for(int x = -distance; x <= distance; x++){
            for(int z = -distance; z <= distance; z++){
                Block currentBlock = block.getRelative(x, 0, z);
                if(currentBlock.getBlockData() instanceof Ageable ageable){
                    if((ageable.getAge() == ageable.getMaximumAge()) || (ageable instanceof CaveVinesPlant plant && plant.hasBerries())){
                        Collection<ItemStack> current_drop = currentBlock.getDrops(tool, player);
                        drops.addAll(main.getExtraItemDrop("farming", playerID, current_drop, currentBlock.getLocation()));
                        drops.addAll(current_drop);          
                        if(ageable instanceof CaveVinesPlant plant){
                            plant.setBerries(false);
                            currentBlock.setBlockData(plant);
                        }else{
                            ageable.setAge(0);
                            currentBlock.setBlockData(ageable);
                            Collection<ItemStack> defaultDrops = currentBlock.getDrops();
                            for(ItemStack seed : defaultDrops){
                                boolean seedFound = false;
                                for(ItemStack drop : drops){
                                    if(drop.isSimilar(seed)){
                                        drop.add(-1);
                                        seedFound = true;
                                        break;
                                    }
                                }
                                if(seedFound){
                                    break;
                                }
                            }
                        }
                        for(ItemStack drop : drops){
                            currentBlock.getWorld().dropItemNaturally(currentBlock.getLocation(), drop);
                        } 

                        main.xpProcess("farming", playerID, currentBlock.getType());
                        player.incrementStatistic(Statistic.MINE_BLOCK, currentBlock.getType()); 
                    }
                }
            }
        }
    }
}
