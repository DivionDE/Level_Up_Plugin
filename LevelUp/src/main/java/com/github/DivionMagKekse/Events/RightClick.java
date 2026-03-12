package com.github.DivionMagKekse.Events;

import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.DivionMagKekse.Farming;
import com.github.DivionMagKekse.Main_LevelUp;
import com.github.DivionMagKekse.Config.Config;

public class RightClick implements Listener{
	Main_LevelUp main;
	Config config;
	public RightClick(Main_LevelUp main) {
		this.main = main;
		this.config = main.getMyConfig();
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		final Block block = event.getClickedBlock();
		if(block == null){
			return;
		}

		if(!config.isSkillBlock("farming", block.getType())){
			return;
		}
		
		if(block.getBlockData() instanceof Ageable ageable){
			if(!(ageable instanceof CaveVinesPlant) && ageable.getAge() != ageable.getMaximumAge()){
				return;
			}
		}else{
			return;
		}

		event.setUseInteractedBlock(Event.Result.DENY);

		Farming.elite_farmer(main, block, event.getPlayer());
	}
}
