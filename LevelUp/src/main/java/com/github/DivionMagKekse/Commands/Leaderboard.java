package com.github.DivionMagKekse.Commands;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.github.DivionMagKekse.Main_LevelUp;
import com.github.DivionMagKekse.Config.Config;
import com.github.DivionMagKekse.Data.Data;

public class Leaderboard implements CommandExecutor, TabCompleter{
	private Config config;
	private Data data;
	public Leaderboard(Main_LevelUp main) {
		this.config = main.getMyConfig();
		this.data = main.getMyData();
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			sender.sendMessage("Bitte geben sie einen Skill an");
			return true;
		}
		if(!config.getAllSkills().contains(args[0])) {
			sender.sendMessage("Geben sie einen gültigen Skill an");
			return true;
		}
		int i=0;
		Bukkit.getLogger().info(args[0]);
		for (Map.Entry<UUID, Integer> entry : data.getAllPlayerData(args[0])) {
			Bukkit.getLogger().info(entry + "");
			i++;
			if(i>10) {
				return true;
			}
			if(data.getPlayerLevel(entry.getKey(), args[0]) == 0) {
				return false;
			}
			sender.sendMessage(i + ". " + Bukkit.getOfflinePlayer(entry.getKey()).getName() + " lvl=" + entry.getValue());
		}
		return true;
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(args.length==1) {
			List <String> skills = config.getAllSkills();
			String current = args[0].toLowerCase();
			return skills.stream().filter(skill -> skill.toLowerCase().startsWith(current)).toList();
		}
		return List.of();
		
	}

}
