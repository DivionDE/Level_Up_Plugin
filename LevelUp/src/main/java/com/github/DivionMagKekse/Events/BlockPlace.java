package com.github.DivionMagKekse.Events;

import org.bukkit.block.Block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.DivionMagKekse.Main_LevelUp;
import com.github.DivionMagKekse.WoodCutting;
import com.github.DivionMagKekse.Data.Data;


public class BlockPlace implements Listener{

	Data data;
	Main_LevelUp main;

	public BlockPlace(Main_LevelUp main) {
		this.main = main;
		data = main.getMyData();
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		if(!WoodCutting.isSource(block)) {
			data.addPlayerPlaced(main, block);
		}
	}
}
