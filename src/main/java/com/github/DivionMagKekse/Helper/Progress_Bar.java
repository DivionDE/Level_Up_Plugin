package com.github.DivionMagKekse.Helper;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.github.DivionMagKekse.Main_LevelUp;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class Progress_Bar {

    private static final int segments = 20;
    private static final int segmentWidth = 6;
    private static final int barWidth = segmentWidth*segments;
    private static final int iconHeight = 12;
    private static final int trueIconHeight = 24;

    private static final Map<UUID, BossBar> activeBars = new HashMap<>(); 
    private static final Map<UUID, BukkitTask> removalTasks = new HashMap<>();

    private static final Map<Integer, List<Character>> charWidths = Map.of( 
        1, List.of('i', '!', '|', ';', ':', ',', '.', '\''), 
        2, List.of('l', '[', ']'), 
        3, List.of('t', 'I', '(', ')', '{', '}', '<', '>'), 
        4, List.of('f', 'k', '"', '*', ' '), 
        5, List.of( 'a', 'b', 'c', 'd', 'e', 'g', 'h', 'j', 'm', 'n', 'o', 'p', 'q', 'r', 's', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '#', '$', '%', '&', '?', '@', '+', '=', '/', '\\', '_', '^', '~', '-' ) );

    private static final Map<String, Integer> trueIconWidth = Map.of("combat", 30, "excavation", 26, "farming", 28, "mining", 26, "woodchopping", 24);

    private static final Key key = Key.key("levelup", "custom_font");

    private static final List<String> barComponent = List.of(
        "\uE001", "\uE002", "\uE003", "\uE004" 
    );

    public static void showXpBar(Main_LevelUp main, String skill, UUID playerID){
        skill = skill.toLowerCase();
        int level = main.getMyData().getPlayerLevel(playerID, skill);
        double currentXP = main.getMyData().getCurrentXP(skill, playerID);
        double neededXP = main.getMyData().getNeededXP(level);
        float progress = (float) (currentXP/neededXP);
        Color barColor = main.getMyConfig().getSkillColor(skill);

        String barText = (skill.substring(0, 0).toUpperCase() + skill.substring(1) + " " + level);

        String iconChar = getSkillIcon(skill);
        Component icon = Component.text("");
        if(iconChar != null){
            icon.append(Component.text(iconChar).font(key));
        }

        Component text = Component.text(barText);

        int xpBarSpace = -barWidth/2;
        int textSpace = -(getIconWidth(skill)+getTextWidth(barText))/2;

        Component xpBar = Component.text("\uF000").font(key)
            .append(getPixelSpace(xpBarSpace))
            .append(createBarComponent(progress, barColor));

        Component titleText = Component.text("\uF000").font(key)
            .append(getPixelSpace(textSpace))
            .append(icon)
            .append(text);

        Component progessBar = Component.text("").append(xpBar).append(titleText);

        BossBar bossBar;
        if (activeBars.containsKey(playerID)) { 
            bossBar = activeBars.get(playerID); 
            bossBar.name(progessBar); 
        } else { 
            bossBar = BossBar.bossBar( progessBar, 0, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS ); 
            bossBar.addViewer(Bukkit.getPlayer(playerID)); 
            activeBars.put(playerID, bossBar); 
        }
        if (removalTasks.containsKey(playerID)) { 
            removalTasks.get(playerID).cancel(); 
        } 
        BukkitTask task = Bukkit.getScheduler().runTaskLater(main, () -> { 
            BossBar barToRemove = activeBars.remove(playerID); 
            if (barToRemove != null) { 
                Player p = Bukkit.getPlayer(playerID); 
                if (p != null) 
                    barToRemove.removeViewer(p); 
                } 
                removalTasks.remove(playerID); 
            }, 60L); 
        removalTasks.put(playerID, task); 
    }

    private static Component createBarComponent(float progress, Color color){
        int filled = Math.round(segments*progress);
        Component bar = Component.empty();
        bar.color(TextColor.color(color.getRGB()));
        for(int i = 0; i<segments; i++){
            if(i >= filled){
                color = Color.WHITE;
            }
            String append = barComponent.get(1);

            if(i==0){
                append = barComponent.get(0);
            }
            else if(i==segments-1){
                append = barComponent.get(3);
            }
            bar = bar.append(Component.text(append).color(TextColor.color(color.getRGB())));
        }
        return bar;
    }

    private static String getSkillIcon(String skill){
        switch (skill.toLowerCase()) {
            case "combat":
                return "\uE005";

            case "mining":
                return "\uE006";

            case "farming":
                return "\uE007";

            case "woodcutting":
                return "\uE008";

            case "excavation":
                return "\uE009";
        }
        return "";
    }

    private static int getIconWidth(String skill){
        return Math.round(trueIconWidth.get(skill)/(trueIconHeight/iconHeight));
    }

    private static int getTextWidth(String text){
        int pixeltext = 0;
        outer:
        for(char current_char : text.toCharArray()){ 
            for(int i = 5; i>=1; i--){ 
                if(charWidths.get(i).contains(current_char)){ 
                    pixeltext += i; continue outer; } } }
        return pixeltext;
    }

    private static Component getPixelSpace(int pixels) {

        Component space = Component.empty();

        Map<Integer, String> positive = Map.of(
                128, "\uF128",
                64, "\uF064",
                32, "\uF032",
                16, "\uF016",
                8, "\uF008",
                4, "\uF004",
                2, "\uF002",
                1, "\uF001"
        );

        Map<Integer, String> negative = Map.of(
                128, "\uF872",
                64, "\uF936",
                32, "\uF968",
                16, "\uF984",
                8, "\uF992",
                4, "\uF996",
                2, "\uF998",
                1, "\uF999"
        );

        Map<Integer, String> map;

        if(pixels >=0){
            map = positive;
        }else{
            map = negative;
        }

        int remaining = Math.abs(pixels);

        for (int value : map.keySet()) {

            while (remaining >= value) {

                space = space.append(Component.text(map.get(value)));
                remaining -= value;
            }
        }
        space = space.font(key);
        return space;
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