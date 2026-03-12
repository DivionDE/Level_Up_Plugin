package com.github.DivionMagKekse.Helper;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitTask;

import com.github.DivionMagKekse.Main_LevelUp;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Progress_Bar {
    private static final Map<UUID, BossBar> activeBars = new HashMap<>();
    private static final Map<UUID, BukkitTask> removalTasks = new HashMap<>();
    

    public static void showXPBar(Main_LevelUp main, UUID player, double currentXP, double neededXP, String skillName) {
        Color barColor = main.getMyConfig().getSkillColor(skillName);
        Color textColor = main.getComplementaryColor(barColor);
        Component customBarIcon = Component.text("\uE001")
        .font(Key.key("minecraft:default"))
        .color(TextColor.color(
                barColor.getRed(),
                barColor.getGreen(),
                barColor.getBlue()
        ));
    	skillName = skillName.substring(0, 1).toUpperCase() + skillName.substring(1).toLowerCase();
        double progress = Math.min(currentXP / neededXP, 1.0);
        int level = main.getMyData().getPlayerLevel(player, skillName);

        // Cancel old removal task if exists
        if (removalTasks.containsKey(player)) {
            removalTasks.get(player).cancel();
            removalTasks.remove(player);
        }

        BossBar bossBar;
        String barText = "§a" + skillName + " Level: " + level + " XP:" + String.format("%.1f", currentXP) + " / " + String.format("%.1f", neededXP);

        // Reuse existing BossBar if exists
        if (activeBars.containsKey(player)) {
            bossBar = activeBars.get(player);
            bossBar.setTitle(barText);
            bossBar.setProgress(progress);
        } else {
            bossBar = Bukkit.createBossBar(
                    barText,
                    BarColor.WHITE,
                    BarStyle.SEGMENTED_10
            );
            bossBar.setProgress(progress);
            bossBar.addPlayer(Bukkit.getPlayer(player));
            bossBar.setVisible(true);
            activeBars.put(player, bossBar);
        }

        // Schedule bar to be removed after 3 seconds
        BukkitTask task = Bukkit.getScheduler().runTaskLater(main, () -> {
            BossBar bar = activeBars.remove(player);
            if (bar != null) {
                bar.setVisible(false);
                bar.removeAll();
            }
            removalTasks.remove(player);
        }, 60L); // 60 ticks = 3 seconds

        removalTasks.put(player, task);
    }
    
    public static void removePlayerBar(UUID player) {
        // Remove the scheduled removal task
        BukkitTask task = removalTasks.remove(player);
        if (task != null) {
            task.cancel();
        }
        
        // Remove the boss bar
        BossBar bar = activeBars.remove(player);
        if (bar != null) {
            bar.setVisible(false);
            bar.removeAll();
        }
    }
}
