package com.github.DivionMagKekse.Events;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.DivionMagKekse.Main_LevelUp;
import com.github.DivionMagKekse.Helper.Progress_Bar;




public class PlayerLeave implements Listener{
	private final Main_LevelUp main;
	public PlayerLeave(Main_LevelUp main) {
		this.main = main;
	}
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		UUID playerID = event.getPlayer().getUniqueId();
		main.removeAbilityUser(playerID);
		main.stopOreSight(playerID);
		Progress_Bar.removePlayerBar(playerID);
	}
}
