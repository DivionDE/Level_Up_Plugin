package com.github.DivionMagKekse.Commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;

import com.github.DivionMagKekse.Main_LevelUp;
import com.github.DivionMagKekse.Config.Config;


public class Abilities implements CommandExecutor, TabCompleter{
	Main_LevelUp main;
	Config config;
	public Abilities(Main_LevelUp main) {
		this.main = main;
		this.config = main.getMyConfig();
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,@NotNull String [] args) {

		if(args.length >= 1) {
			
			if(args[0].equals("list") || args[0].equals("ls")) {
				Audience player = null;
				if(sender instanceof Player) {
					player = (Audience) sender;
				}
				if(player != null) {
					showAbilityList(player, sender);
					return true;
				}
				else {
					sender.sendMessage(ChatColor.GREEN + "Available Skills:");
					for(String ability : config.getAllAbilities().keySet()) {
						int level = config.getAllAbilities().get(ability);
						sender.sendMessage(ChatColor.AQUA + ability + " = " + ChatColor.RESET + level);	
					}
					return true;
				}
			}else {
				Player player = null;
				if(sender instanceof Player) {
					player = (Player) sender;
				}
				if(player != null && config.getAllAbilities().containsKey(args[0].toLowerCase())) {
					String ability = args[0];
					if(args.length == 2) {
						if(args[1].equals("en") || args[1].equals("enable") || args[1].equals("activate")) {
							String skill = config.getAbilitySkill(ability);
							if(main.canUseAbility(skill, ability, player.getUniqueId())) {
								main.activateAbility(player.getUniqueId(), ability);
								return true;
							}else {
								sender.sendMessage(ChatColor.RED + "You dont have the requiered Level of:" + config.getAbilityLevel(skill, ability));
								return true;
							}
						}else if(args[1].equals("dis") || args[1].equals("disable") || args[1].equals("deactivate")) {
							main.deactivateAbility(player.getUniqueId(), ability);
							return true;
						}else {
							sender.sendMessage(ChatColor.RED + "Usage: /abilities <list|ability> [enable|disable]");
							return true;
						}
					}else {
						main.toggleAbilityStatus(player.getUniqueId(), ability);
						return true;
					}
				} else {
					sender.sendMessage("No valid ability found");
					return true;
				}
			}
		}else {
			sender.sendMessage(ChatColor.RED + "Usage: /abilities <list|ability> [enable|disable]");
			return true;
		}
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
	        @NotNull String label, @NotNull String[] args) {

	    if (args.length == 1) {
	        List<String> completions = new java.util.ArrayList<>();
	        String current = args[0].toLowerCase();

	        if ("list".startsWith(current)) completions.add("list");

	        for (String ability : config.getAllAbilities().keySet()) {
	            if (ability.toLowerCase().startsWith(current)) {
	                completions.add(ability);
	            }
	        }
	        return completions;
	    }

	    if (args.length == 2 && config.getAllAbilities().containsKey(args[0].toLowerCase())) {
	        List<String> completions = new java.util.ArrayList<>();
	        String current = args[1].toLowerCase();
	        if ("enable".startsWith(current) || "en".startsWith(current)) completions.add("enable");
	        if ("disable".startsWith(current) || "dis".startsWith(current)) completions.add("disable");
	        return completions;
	    }
       	        
	    return java.util.Collections.emptyList();
	}
	
	private void showAbilityList(Audience player, CommandSender sender) {
	    MiniMessage mm = MiniMessage.miniMessage();
	    String message = "<green><b>Available Skills:</b></green><newline>";
	    UUID playerID = ((Player) sender).getUniqueId();
	    
	    for(String ability : config.getAllAbilities().keySet()) {
	        int alevel = config.getAllAbilities().get(ability);
	        int plevel = main.getMyData().getPlayerLevel(playerID, config.getAbilitySkill(ability));
	        String color = main.getAbilityStatus(playerID, ability) ? "green" : "red";
	        ability = ability.substring(0, 1).toUpperCase() + ability.substring(1).toLowerCase();
	        message += "<aqua>"
	                + "<hover:show_text:'<" + color + ">'Activate/Deactivate ability'</" + color + ">'>"
	                + "<click:run_command:'/abilities " + ability + "'>" + ability + "</click>"
	                + "</hover></aqua>";
	        if(alevel > 0 && alevel>plevel) message += ": Unlocked at " + alevel;
	        message += "<newline>";
	    }
	    player.sendMessage(mm.deserialize(message));
	}


}
