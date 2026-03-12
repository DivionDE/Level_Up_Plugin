package com.github.DivionMagKekse.Events;

import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class CreeperExplode implements Listener{
	@EventHandler
	public void onCreeperExplode(EntityExplodeEvent event) {
	    // Prüfen, ob die Entität ein Creeper ist
	    if (event.getEntity() instanceof Creeper) {
	        event.blockList().clear();
	    }
	}

}
