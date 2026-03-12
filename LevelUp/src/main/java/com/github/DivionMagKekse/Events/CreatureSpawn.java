package com.github.DivionMagKekse.Events;

import java.util.Collection;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.github.DivionMagKekse.Main_LevelUp;
import com.github.DivionMagKekse.Data.Data;

public class CreatureSpawn implements Listener{
	private Data data;
	public CreatureSpawn(Main_LevelUp main) {
		this.data = main.getMyData();
	}	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		SpawnReason reason= event.getSpawnReason();
		Location loc = event.getLocation();
		World world = loc.getWorld();
		Chunk chunk = loc.getChunk();
		Entity entity = event.getEntity();
		Collection<Player> players = chunk.getPlayersSeeingChunk();
		if(reason == SpawnReason.EGG) {
			for(Player player : players) {
				int level = data.getPlayerLevel(player.getUniqueId(), "Farming");
				double chance = (double)level%1000/1000;
				int count = level/1000;
				if (chance >= Math.random()) {
					count ++;
				}
				if(count >= 1) {
					Class<? extends Entity> entityClass = entity.getType().getEntityClass();
					world.spawnParticle(Particle.ELECTRIC_SPARK, loc, 10, 0.5, 0.5, 0.5, 0.25);
					if (entityClass != null) {
						while(count >=1) {							
							    world.spawn(loc, entityClass, e -> {
							        if (e instanceof org.bukkit.entity.Ageable ageable) {
							            ageable.setBaby(); // sicherstellen, dass es ein Baby wird
							        }
							    });
							count--;
						}
					}
				}
			}
		}
	}
}
