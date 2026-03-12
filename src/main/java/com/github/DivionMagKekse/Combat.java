package com.github.DivionMagKekse;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Combat {
	public static final List<PotionEffectType> potions = List.of(
		    PotionEffectType.ABSORPTION,
		    PotionEffectType.SPEED,
		    PotionEffectType.HASTE,
		    PotionEffectType.STRENGTH,
		    PotionEffectType.INSTANT_HEALTH,
		    PotionEffectType.JUMP_BOOST,
		    PotionEffectType.REGENERATION,
		    PotionEffectType.RESISTANCE,
		    PotionEffectType.FIRE_RESISTANCE,
		    PotionEffectType.WATER_BREATHING,
		    PotionEffectType.HEALTH_BOOST,
		    PotionEffectType.SATURATION
		);
	public static double criticalStrike(Main_LevelUp main, String skill, Player damager, Entity damaged, double baseDamage) {
		int level = main.getMyData().getPlayerLevel(damager.getUniqueId(), skill);
		int count = level/1000;
		double chance = (double)level%1000/1000;
		if(chance>=Math.random()) {
			count++;
		}
		double finalDamage = 0;
		while (count >=1){
			finalDamage = finalDamage + baseDamage * 1.25;
			Location loc = damaged.getLocation();
			damaged.getWorld().spawnParticle(Particle.ELECTRIC_SPARK,loc.getX(), loc.getY()+1, loc.getZ(), 10, 0.5, 0.5, 0.5, 0.25);
			ItemStack item = damager.getInventory().getItemInMainHand();
			if (item.getItemMeta() != null) {
				damager.setCooldown(item, (getCooldownReduction(item)));
			}	
			count--;
		}
		if (finalDamage == 0) {
			finalDamage = baseDamage;
		}
		return finalDamage;
	}
	
	public static int getCooldownReduction(ItemStack tool) {
	    return (int) (tool.getItemMeta().getUseCooldown().getCooldownSeconds() * 20 * 0.75);
	}
	
	public static void randomEffect(Main_LevelUp main, String skill, Player player) {
		int level = main.getMyData().getPlayerLevel(player.getUniqueId(), skill);
		int count = level/1000;
		double chance = (double)level%1000/1000;
		if (chance>=Math.random()) {
			count++;
		}
		
		Random rand = new Random();
		while (count>=1) {
			PotionEffectType pot = potions.get(rand.nextInt(0, potions.size()));
			PotionEffect current_pot = player.getPotionEffect(pot);
			int duration = rand.nextInt(10*20, 120*20);
			int amplifier = rand.nextInt(0,3);
			if(current_pot != null && current_pot.getType().equals(pot)){
				if(current_pot.getDuration() == -1){
					duration = -1;
				}else{
					duration += current_pot.getDuration();
				}
				amplifier += current_pot.getAmplifier();
				if(amplifier > 3 && current_pot.getAmplifier()<3){
					amplifier = 3;
				}
				player.removePotionEffect(current_pot.getType());
			}
			PotionEffect added_pot = pot.createEffect(duration, amplifier);
			player.addPotionEffect(added_pot);
			count--;
		}
	}
}
