package com.github.DivionMagKekse.Commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.DivionMagKekse.Main_LevelUp;
import com.github.DivionMagKekse.Config.Config;
import com.github.DivionMagKekse.Data.Data;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;

public class Skills implements CommandExecutor, TabCompleter{
	private Main_LevelUp main;
	private Config config;
	private Data data;

	public Skills(Main_LevelUp main) {
		this.main = main;
		this.config = main.getMyConfig();
		this.data = main.getMyData();
	}
	
	private final List<String> list= List.of(
			"ls",
			"list");
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
			@NotNull String @NotNull [] args) {
		String arg1 = null, arg2 = null, arg3 = null;
			if(args.length >= 1) {
				arg1 = args[0].toLowerCase();
				if(args.length>=2)arg2 = args[1].toLowerCase();
				if(args.length>=3)arg3 = args[2].toLowerCase();
				if(sender instanceof Player) {
					if(playerCommand(sender, arg1, arg2, arg3)) return true;
					sender.sendMessage(ChatColor.RED + "Wrong input use:'/skill <ls/list/skill> oder /skill player <ls/list/skill>'");
					return true;
				}else {
					if(consoleCommand(sender, arg1, arg2, arg3)) return true;
					sender.sendMessage(ChatColor.RED + "Wrong input use:'/skill <ls/list/skill> oder /skill player <ls/list/skill>'");
					return true;
				}
			
			}else {
			    sender.sendMessage(ChatColor.RED + "Wrong input use:'/skill <ls/list/skill> oder /skill player <ls/list/skill>'");
			    return true;
			}
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
			@NotNull String label, @NotNull String @NotNull [] args) {
		    List<String> completions = new java.util.ArrayList<>();

		    // 1️⃣ First argument
		    if (args.length == 1) {

		        String current = args[0].toLowerCase();

		        // Console: only player names
		        if (!(sender instanceof Player)) {
		            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
		                if (p.getName().toLowerCase().startsWith(current)) {
		                    completions.add(p.getName());
		                }
		            }
		            return completions;
		        }

		        // Player: suggest self-commands + player names

		        // ls / list
		        for (String s : list) {
		            if (s.startsWith(current)) {
		                completions.add(s);
		            }
		        }

		        // Skills
		        for (String skill : config.getAllSkills()) {
		            if (skill.toLowerCase().startsWith(current)) {
		                completions.add(skill);
		            }
		        }

		        // Online players
		        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
		            if (p.getName().toLowerCase().startsWith(current)) {
		                completions.add(p.getName());
		            }
		        }

		        return completions;
		    }

		    // 2️⃣ Second argument
		    if (args.length == 2) {

		        String firstArg = args[0];
		        String current = args[1].toLowerCase();

		        OfflinePlayer target = Bukkit.getOfflinePlayer(firstArg);

		        // If first arg is a player → suggest skills + list
		        if (target != null || !(sender instanceof Player)) {

		            for (String s : list) {
		                if (s.startsWith(current)) {
		                    completions.add(s);
		                }
		            }

		            for (String skill : config.getAllSkills()) {
		                if (skill.toLowerCase().startsWith(current)) {
		                    completions.add(skill);
		                }
		            }

		            return completions;
		        }

		        // If self skill → suggest xp
		        if (config.getAllSkills().contains(firstArg)) {
		            if ("xp".startsWith(current)) {
		                completions.add("xp");
		            }
		            return completions;
		        }
		    }

		    // 3️⃣ Third argument (xp only)
		    if (args.length == 3) {

		        String current = args[2].toLowerCase();

		        if ("xp".startsWith(current)) {
		            completions.add("xp");
		        }

		        return completions;
		    }

		    return completions;
	}
	
	private boolean showSkillList(String arg, Audience playerAu, UUID playerID) {
		if(arg == null){
			return false;
		}
		if(list.contains(arg)) {
			MiniMessage mm = MiniMessage.miniMessage();
		    OfflinePlayer player = Bukkit.getOfflinePlayer(playerID);
		    if(player == null) return false;
		    String message = "<green><b>Skill level of " + player.getName() + ":</b></green><newline>";
		    for(String skill : config.getAllSkills()) {
		    	skill = skill.substring(0, 1).toUpperCase() + skill.substring(1).toLowerCase();
		        int plevel = data.getPlayerLevel(playerID, skill);
		        message += "<aqua>"
		                + "<hover:show_text:\"<green>Show XP</green>\">"
		                + "<click:run_command:'/skills " + skill + " xp'>" + skill + "</click>"
		                + "</hover></aqua>"
		        		+ " = <red>" + plevel + "</red><newline>";
		    }
		    playerAu.sendMessage(mm.deserialize(message));
		    return true;
		}
	    return false;
	}
	
	private boolean showSkill(String arg1, String arg2, Audience playerAu, UUID playerID) {
		if(arg1 == null){
			return false;
		}
		if(config.getAllSkills().contains(arg1.toLowerCase())) {
			MiniMessage mm = MiniMessage.miniMessage();
			OfflinePlayer player = Bukkit.getOfflinePlayer(playerID);
			if(player == null)return false;
			int level = data.getPlayerLevel(playerID, arg1);
			arg1 = arg1.substring(0, 1).toUpperCase() + arg1.substring(1).toLowerCase();
			String message = "<aqua><b>" + player.getName() + " " + arg1 + "</b></aqua> level <red>" + level + "</red>";
			if(arg2 != null && arg2.equalsIgnoreCase("xp")) {
				double neededXP = main.getNeededXP(level);
				double currentXP = data.getCurrentXP(playerID, arg1);
				message += "<newline>Current xp <green>" + String.format("%.2f", currentXP) + "</green>" + "<newline>Needed xp <green>" + String.format("%.2f", neededXP) + "</green>";
			}
			playerAu.sendMessage(mm.deserialize(message));
			return true;
		}
		return false;
	}
	
	private boolean playerCommand(CommandSender sender, String arg1, String arg2, String arg3) {

	    Audience audience = sender;
	    UUID selfID = ((Player) sender).getUniqueId();

		if(!list.contains(arg1)  && !config.getAllSkills().contains(arg1)){
			OfflinePlayer target = Bukkit.getOfflinePlayer(arg1);
	    	if (target != null) {

	        	UUID targetID = target.getUniqueId();

	        	if (showSkillList(arg2, audience, targetID)) return true;
	        	if (showSkill(arg2, arg3, audience, targetID)) return true;
	        	return false;
	    	}
		}
		else{
			if (showSkillList(arg1, audience, selfID)) return true;
			if (showSkill(arg1, arg2, audience, selfID)) return true;
		}
	    return false;
	}
	
	private boolean consoleCommand(CommandSender sender, String arg1, String arg2, String arg3) {

	    if (arg1 == null) {
	        sender.sendMessage("Usage: /skill <player> <ls/list/skill>");
	        return true;
	    }

	    Player target = Bukkit.getPlayer(arg1);

	    if (target == null) {
	        sender.sendMessage("Player is not online or does not exist.");
	        return true;
	    }

	    UUID playerID = target.getUniqueId();
	    String playerName = target.getName();

	    if (arg2 != null && list.contains(arg2.toLowerCase())) {
	        showSkillListConsole(sender, playerID, playerName);
	        return true;
	    }

	    if (arg2 != null && config.getAllSkills().contains(arg2)) {
	        showSkillConsole(sender, playerID, playerName, arg2, arg3);
	        return true;
	    }

	    sender.sendMessage("Usage: /skill <player> <ls/list/skill>");
	    return true;
	}
	
	
	private void showSkillListConsole(CommandSender sender, UUID playerID, String playerName) {

	    sender.sendMessage("===== Skill levels of " + playerName + " =====");

	    for (String skill : config.getAllSkills()) {
	        int level = data.getPlayerLevel(playerID, skill);
	        skill = skill.substring(0, 1).toUpperCase() + skill.substring(1).toLowerCase();
	        sender.sendMessage(skill + " = " + level);
	    }
	}
	
	
	private void showSkillConsole(CommandSender sender, UUID playerID, String playerName, String skill, String arg3) {

	    int level = data.getPlayerLevel(playerID, skill);

	    sender.sendMessage("===== " + playerName + " " + skill.substring(0, 1).toUpperCase() + skill.substring(1).toLowerCase() + " =====");
	    sender.sendMessage("Level: " + level);

	    if (arg3 != null && arg3.equalsIgnoreCase("xp")) {
	        double neededXP = main.getNeededXP(level);
	        double currentXP = data.getCurrentXP(playerID, skill);

	        sender.sendMessage("Current XP: " + currentXP);
	        sender.sendMessage("Needed XP: " + neededXP);
	    }
	}
}
