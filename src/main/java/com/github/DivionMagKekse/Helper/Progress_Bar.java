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

    private static final Map<UUID, BossBar> activeBars = new HashMap<>();
    private static final Map<UUID, BukkitTask> removalTasks = new HashMap<>();
    private static final Map<Integer, List<Character>> charWidths = Map.of(
    1, List.of('i', '!', '|', ';', ':', ',', '.', '\''),
    2, List.of('l', '[', ']'),
    3, List.of('t', 'I', '(', ')', '{', '}', '<', '>'),
    4, List.of('f', 'k', '"', '*', ' '),
    5, List.of(
        'a', 'b', 'c', 'd', 'e', 'g', 'h', 'j', 'm', 'n', 'o', 'p', 'q', 'r', 's', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '#', '$', '%', '&', '?', '@', '+', '=', '/', '\\', '_', '^', '~', '-'
    )
    );
 

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
        int pixeltext = 0;
        String negativeSpace = "";

        switch (skillName.toLowerCase()) {
            case "combat" : 
                iconChar = "\uE005";
                pixeltext += 23;
                break;
            case "mining" : 
                iconChar = "\uE006";
                pixeltext += 16;
                break;
            case "farming" : 
                iconChar = "\uE007";
                pixeltext += 19;
                break;
            case "woodcutting" : 
                iconChar = "\uE008";
                pixeltext += 19;
                break;
            case "excavation" : 
                iconChar = "\uE009";
                pixeltext += 25;
                break;
        }
        outer:
        for(char current_char : text.toCharArray()){
            for(int i = 5; i>=1; i--){
                if(charWidths.get(i).contains(current_char)){
                    pixeltext += i;
                    continue outer;
                }
            }
        }
        negativeSpace = getNegativeSpace(pixeltext);

        
        Component icon = iconChar != null 
            ? Component.text(iconChar).font(customFont) 
            : Component.empty();

        Component skillText = Component.text(text)
            .font(Key.key("minecraft:default"));

        Component progressBar = createProgressBar(progress, barColor, customFont);

        Component barTitle = Component.text()
            .append(icon)
            .append(skillText)
            .append(Component.text(negativeSpace).font(customFont))
            .append(progressBar) 
            .build();
                                                                        


        BossBar bossBar;
        if (activeBars.containsKey(playerID)) {
            bossBar = activeBars.get(playerID);
            bossBar.name(barTitle);
        } else {
            bossBar = BossBar.bossBar(
                barTitle,
                0,
                BossBar.Color.WHITE,
                BossBar.Overlay.PROGRESS
            );
            bossBar.addViewer(player);
            activeBars.put(playerID, bossBar);
        }

        // alten Removal-Task abbrechen
        if (removalTasks.containsKey(playerID)) {
            removalTasks.get(playerID).cancel();
        }

        BukkitTask task = Bukkit.getScheduler().runTaskLater(main, () -> {
            BossBar barToRemove = activeBars.remove(playerID);
            if (barToRemove != null) {
                Player p = Bukkit.getPlayer(playerID);
                if (p != null) barToRemove.removeViewer(p);
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

    private static String getNegativeSpace(int amount) {
    if (amount <= 0) return "";
    StringBuilder sb = new StringBuilder();
    int temp = amount;
    
    // Große Sprünge (falls vorhanden, z.B. -128)
    while (temp >= 128) { sb.append("\uF872"); temp -= 128; }
    
    // Deine Switch-Logik für den Rest (angepasst auf einfache if-Kette für Performance)
    if (temp >= 64) { sb.append("\uF936"); temp -= 64; }
    if (temp >= 32) { sb.append("\uF968"); temp -= 32; }
    if (temp >= 16) { sb.append("\uF984"); temp -= 16; }
    if (temp >= 8)  { sb.append("\uF992"); temp -= 8; }
    if (temp >= 4)  { sb.append("\uF996"); temp -= 4; }
    if (temp >= 2)  { sb.append("\uF998"); temp -= 2; }
    if (temp >= 1)  { sb.append("\uF999"); temp -= 1; }
    
    return sb.toString();
}


    public static void removePlayerBar(UUID playerId) {
        BukkitTask task = removalTasks.remove(playerId);
        if (task != null) {
            task.cancel();
        }

        BossBar bar = activeBars.remove(playerId);
        if (bar != null) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                bar.removeViewer(player);
            }
        }
    }
}
