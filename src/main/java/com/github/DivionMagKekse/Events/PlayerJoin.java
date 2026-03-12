package com.github.DivionMagKekse.Events;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.DivionMagKekse.Main_LevelUp;
import com.github.DivionMagKekse.Data.Data;


public class PlayerJoin implements Listener{
	
	private final Main_LevelUp main;
	private Data data;
	public PlayerJoin(Main_LevelUp main) {
		this.main = main;
		this.data = main.getMyData();
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		UUID playerID = event.getPlayer().getUniqueId();
		data.addPlayer(playerID);
		main.addAbilityUser(playerID);	
		main.sendOptionalResourcePack(event.getPlayer());	
	}
}
