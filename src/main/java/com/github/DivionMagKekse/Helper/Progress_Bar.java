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
        Color textColor = main.getComplementaryColor(barColor);

        skillName = skillName.substring(0, 1).toUpperCase() + skillName.substring(1).toLowerCase();

        int level = main.getMyData().getPlayerLevel(playerID, skillName);
        float progress = (float) Math.min(currentXP / neededXP, 1.0);

        String content = null;
        switch(skillName){
            case "Combat", "combat" -> content = "\uE005";
            case "Mining", "mining" -> content = "\uE006";
            case "Farming", "farming" -> content = "\uE007";
            case "Woodcutting", "woodcutting" -> content = "\uE008";
            case "Excavation", "excavation" -> content = "\uE009";
        };
        
        // Custom icon glyph
        Component icon = Component.text("");

        if(content != null){
            icon = Component.text(content)
                .font(Key.key("levelup:default"));
        }


        // Text information
        Component title_text = Component.text(
                skillName + " Level: " + level + " "
        ).append(Component.text(
                String.format("%.1f / %.1f XP", currentXP, neededXP)
        )).color(TextColor.color(
                textColor.getRed(),
                textColor.getGreen(),
                textColor.getBlue()
        ));


        // Fake progress bar
        Component barStatus = createBar(progress, barColor);


        Component title = Component.text().append(icon).append(Component.space()).append(title_text).build();
        
        Component bar = Component.text().append(barStatus).build();


        BossBar titleBar;
        BossBar progressBar;

        if (activeBars.containsKey(playerID)) {

            titleBar = activeBars.get(playerID).get(0);
            titleBar.name(title);
            progressBar = activeBars.get(playerID).get(1);
            progressBar.name(bar);

        } else {

            titleBar = BossBar.bossBar(
                    title,
                    0,
                    BossBar.Color.WHITE,
                    BossBar.Overlay.PROGRESS
            );

            progressBar = BossBar.bossBar(
                    bar,
                    0,
                    BossBar.Color.WHITE,
                    BossBar.Overlay.PROGRESS
            );

            titleBar.addViewer(player);
            progressBar.addViewer(player);

            activeBars.put(playerID, List.of(titleBar, progressBar));
        }


        // cancel previous removal task
        if (removalTasks.containsKey(playerID)) {
            removalTasks.get(playerID).cancel();
        }


        BukkitTask task = Bukkit.getScheduler().runTaskLater(main, () -> {

            List<BossBar> barToRemove = activeBars.get(playerID);

            for(BossBar currentbar : barToRemove) {
                currentbar.removeViewer(player);
            }
            activeBars.remove(playerID);
            removalTasks.remove(playerID);

        }, 60L);


        removalTasks.put(playerID, task);
    }


    private static Component createBar(float progress, Color color) {

        int segments = 20;
        int filled = Math.round(progress * segments);

        Component bar = Component.empty();

        for (int i = 0; i < segments; i++) {
            String segment_content = "\uE002";
            TextColor segment_color = TextColor.color(color.getRGB());
            if(i == 0){
                segment_content = "\uE001";
            }else if(i == segments){
                segment_content = "\uE004";    
            }if (i < filled) {
                segment_color = TextColor.color(Color.WHITE.getRGB());
            }
            bar = bar.append(Component.text(segment_content).font(Key.key("levelup:default")).color(segment_color));
            if(segment_content.equals("\uE001") || segment_content.equals("\uE002")){
                bar = bar.append(Component.text("\uE003").font(Key.key("levelup:default"))
                         .color(TextColor.color(color.getRGB())));
            }
        }

        return bar;
    }


    public static void removePlayerBar(UUID playerId) {

        BukkitTask task = removalTasks.remove(playerId);
        if (task != null) {
            task.cancel();
        }

        List<BossBar> bar = activeBars.remove(playerId);

        if (bar != null) {

            Player player = Bukkit.getPlayer(playerId);

            if (player != null) {
                for(BossBar currentbar : bar){
                    currentbar.removeViewer(player);
                }
            }
        }
    }
}