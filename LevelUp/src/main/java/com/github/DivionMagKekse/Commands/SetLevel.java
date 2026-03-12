package com.github.DivionMagKekse.Commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.github.DivionMagKekse.Main_LevelUp;
import com.github.DivionMagKekse.Config.Config;

import net.md_5.bungee.api.ChatColor;

public class SetLevel implements CommandExecutor, TabCompleter{

	Main_LevelUp main;
	Config config;
	public SetLevel(Main_LevelUp main_LevelUp) {
		this.main = main_LevelUp;
		config = main.getMyConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length<3) {
			sender.sendMessage("§cBenutzung: /setlevel <Spieler> <Skill> <Level>");
			return true;
		}
		Player player = Bukkit.getPlayer(args[0]);
		String skill = args[1].toLowerCase();
		int level = Integer.parseInt(args[2]);
		if(level<1) {
			sender.sendMessage(ChatColor.RED + "Level muss >=1 sein");
			return true;
		}
		main.getMyData().saveLevel(player.getUniqueId(), skill, level);
		player.sendMessage(skill + " auf Level:" + level + " gesetzt");
		return true;

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
	    if (args.length == 1) {
	        // Suggest online player names
	        return Bukkit.getOnlinePlayers().stream()
	                .map(Player::getName)
	                .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
	                .toList();
	    }
	    if (args.length == 2) {
	        // Suggest skills
	        List<String> skills = config.getAllSkills();
	        return skills.stream().filter(skill -> skill.startsWith(args[1].toLowerCase())).toList();
	    }
	    return List.of(); // nothing for further args
	}

}
