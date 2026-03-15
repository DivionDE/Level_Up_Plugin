package com.github.DivionMagKekse.Helper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.github.DivionMagKekse.Main_LevelUp;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.bossbar.*;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Progress_Bar {

    private static final Map<UUID, List<BossBar>> activeBars = new HashMap<>();
    private static final Map<UUID, BukkitTask> removalTasks = new HashMap<>();

    public static void showXPBar(Main_LevelUp main, UUID playerID, double currentXP, double neededXP, String skillName) {
        Player player = Bukkit.getPlayer(playerID);
        if (player == null) return;

        Color barColor = main.getMyConfig().getSkillColor(skillName);

        skillName = skillName.substring(0, 1).toUpperCase() + skillName.substring(1).toLowerCase();

        int level = main.getMyData().getPlayerLevel(playerID, skillName);
        float progress = (float) Math.min(currentXP / neededXP, 1.0);

        // Font-Keys
        Key customFont = Key.key("levelup", "custom_font"); // nutzt dein levelup:custom_font.json

        // Icon-Glyph
        String iconChar = null;
        final String text = skillName + " Level: " + level + " " + String.format("%.1f / %.1f XP", currentXP, neededXP);

        switch (skillName.toLowerCase()) {
            case "combat" : 
                iconChar = "\uE005";
                break;
            case "mining" : 
                iconChar = "\uE006";
                break;
            case "farming" : 
                iconChar = "\uE007";
                break;
            case "woodcutting" : 
                iconChar = "\uE008";
                break;
            case "excavation" : 
                iconChar = "\uE009";
                break;
        }
        
        Component icon = iconChar != null 
            ? Component.text(iconChar).font(customFont) 
            : Component.empty();

        Component skillText = Component.text(text)
            .font(Key.key("minecraft:default"));

        Component centeredText = Component.text()
            .append(icon)
            .append(skillText)
            .build();

        Component centeredBar = Component.text()
            .append(createProgressBar(progress, barColor, customFont))
            .build();

        BossBar textBar;
        BossBar progressBar;
        if (activeBars.containsKey(playerID)) {
            textBar = activeBars.get(playerID).get(0);
            progressBar = activeBars.get(playerID).get(1);
            textBar.name(centeredText);
            progressBar.name(centeredBar);

        } else {
            textBar = BossBar.bossBar(
                centeredText,
                0,
                BossBar.Color.WHITE,
                BossBar.Overlay.PROGRESS
            );
            progressBar = BossBar.bossBar(
                centeredBar,
                0,
                BossBar.Color.WHITE,
                BossBar.Overlay.PROGRESS
            );
            textBar.addViewer(player);
            progressBar.addViewer(player);
            activeBars.put(playerID, List.of(textBar, progressBar));
        }

        // alten Removal-Task abbrechen
        if (removalTasks.containsKey(playerID)) {
            removalTasks.get(playerID).cancel();
        }

        BukkitTask task = Bukkit.getScheduler().runTaskLater(main, () -> {
            List<BossBar> bossBars = activeBars.remove(playerID);
            if (bossBars != null) {
                for(BossBar bar : bossBars){
                    Player p = Bukkit.getPlayer(playerID);
                    if (p != null) bar.removeViewer(p);
                }
                
            }
            removalTasks.remove(playerID);
        }, 60L);

        removalTasks.put(playerID, task);
    }

    private static Component createProgressBar(float progress, Color barColor, Key customFont) {
        int segments = 20;
        int filled = Math.round(progress * segments);

        Component bar = Component.empty();

        for (int i = 0; i < segments; i++) {
            String segment = "\uE002";
            TextColor color = TextColor.color(barColor.getRGB());

            if (i == 0) {
                segment = "\uE001";
            } else if (i == segments - 1) {
                segment = "\uE004";
            }

            if (i >= filled) {
                color = TextColor.color(Color.WHITE.getRGB());
            }

            bar = bar.append(Component.text(segment).font(customFont).color(color));
        }

        return bar;
    }

    public static void removePlayerBar(UUID playerId) {
        BukkitTask task = removalTasks.remove(playerId);
        if (task != null) {
            task.cancel();
        }

        List <BossBar> bars = activeBars.remove(playerId);
        if (bars != null) {
            for(BossBar bar : bars){
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    bar.removeViewer(player);
                }
            }
        }
    }
}
