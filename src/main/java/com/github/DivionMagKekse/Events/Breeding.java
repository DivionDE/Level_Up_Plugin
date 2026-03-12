package com.github.DivionMagKekse.Events;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

import com.github.DivionMagKekse.Main_LevelUp;
import com.github.DivionMagKekse.Data.Data;

public class Breeding implements Listener {
	private Data data;
	public Breeding(Main_LevelUp main) {
		this.data = main.getMyData();
	}
	
	@EventHandler
	public void onEntityBreed(EntityBreedEvent event) {
		if (event.getBreeder() != null && event.getBreeder().getType().equals(EntityType.PLAYER)) {
			Player player = (Player) event.getBreeder();
			Entity animal = event.getEntity();
			World world = player.getWorld();
			Location loc = animal.getLocation();

			
			int level = data.getPlayerLevel(player.getUniqueId(), "Farming");
			double chance = (double)level%1000/1000;
			int count = level/1000;
			if (chance >= Math.random()) {
				count ++;
			}
			if(count >= 1) {
				EntityType type = animal.getType(); // z. B. COW, SHEEP, etc.
				Class<? extends Entity> entityClass = type.getEntityClass();
				world.spawnParticle(Particle.ELECTRIC_SPARK, loc, 10, 0.5, 0.5, 0.5, 0.25);
				if (entityClass != null) {
					while(count >=1) {						
						    world.spawn(loc, entityClass, e -> {
						        if (e instanceof org.bukkit.entity.Ageable ageable) {
						            ageable.setBaby();
						        }
						    });
						

						
						int xp = event.getExperience();
						ExperienceOrb orb = (ExperienceOrb) world.spawnEntity(loc, EntityType.EXPERIENCE_ORB);
						orb.setExperience(xp);
						count--;
					}
				}
			}
		}
		
	}
}
