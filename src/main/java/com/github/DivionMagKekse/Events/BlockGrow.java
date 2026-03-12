package com.github.DivionMagKekse.Events;

import java.util.Collection;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

import com.github.DivionMagKekse.Main_LevelUp;
import com.github.DivionMagKekse.Data.Data;

public class BlockGrow implements Listener{
	private Data data;
	
	public BlockGrow (Main_LevelUp main) {
		this.data = main.getMyData();
	}
	
	@EventHandler
	public void onGrowEvent(BlockGrowEvent event) {
		Block block = event.getBlock();
		Chunk chunk = block.getChunk();
		if (block.getBlockData() instanceof Ageable) {
			Collection <Player> players = chunk.getPlayersSeeingChunk();
			Ageable ageable = (Ageable) block.getBlockData();
			int age = ageable.getAge()+1;
			int maxage = ageable.getMaximumAge();
			for(Player player : players) {
				int level = data.getPlayerLevel(player.getUniqueId(), "farming");
				age += level/1000;
				double chance = level%1000/1000;
				if(age>=maxage) {
					age = maxage;
				}else if(age<maxage && chance <= Math.random()) {
					age++;
				}
				ageable.setAge(age);
				block.setBlockData(ageable);
			}
		}
	}
}
