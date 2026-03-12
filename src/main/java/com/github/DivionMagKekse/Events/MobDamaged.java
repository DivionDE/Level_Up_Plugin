package com.github.DivionMagKekse.Events;


import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.github.DivionMagKekse.Combat;
import com.github.DivionMagKekse.Main_LevelUp;

public class MobDamaged implements Listener{
	
	Main_LevelUp main;
	private static String skill = "Combat";
	private double baseDamage;
	
	public MobDamaged(Main_LevelUp main) {
		this.main = main;
	}

	@EventHandler
	public void onMobDamaged(EntityDamageEvent event) {
		if(event.getDamageSource().getCausingEntity()!= null && event.getDamageSource().getCausingEntity().getType() == EntityType.PLAYER) {
			Player damager = (Player) event.getDamageSource().getCausingEntity();
			Entity damaged = event.getEntity();
			baseDamage = event.getDamage();
			if (damager != null && damaged.getType() != EntityType.PLAYER) {
				event.setDamage(Combat.criticalStrike(main, skill, damager, damaged, baseDamage));
			}
		}
		
	}
	
	
}
