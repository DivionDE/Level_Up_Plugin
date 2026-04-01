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

    private static final int segments = 36+2;       //36 Segmente und ein Anfang- + ein Endsegment
    private static final int segmentWidth = 5;
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
        double currentXP = Math.round(main.getMyData().getCurrentXP(skill, playerID));
        double neededXP = Math.round(main.getMyData().getNeededXP(level));
        float progress = (float) (currentXP/neededXP);
        Color barColor = main.getMyConfig().getSkillColor(skill);
        int text_width = 41;
        switch(skill){
            case "combat" -> text_width += 28;
            case "excavation" -> text_width += 44;
            case "farming" -> text_width += 30;
            case "mining" -> text_width += 22;
            case "woodchopping" -> text_width += 56;
            default -> text_width += getTextWidth(skill);
        }
        text_width += getNumberWidth(level);

        text_width += getNumberWidth(currentXP);
        text_width += getNumberWidth(neededXP);

        String barText = (" " + skill.substring(0, 1).toUpperCase() + skill.substring(1) + 
                            " level: " + level + " " + currentXP + 
                            " / " + neededXP);

        String iconChar = getSkillIcon(skill);
        Component icon = Component.text("");
        if(iconChar != null){
            icon = icon.append(Component.text(iconChar).font(key).color(TextColor.color(barColor.getRGB())));
        }

        Component text = Component.text(barText).font(Key.key("minecraft", "default")).color(TextColor.color(barColor.getRGB()));

        int xpBarSpace = -barWidth/2;
        int textSpace = -(getIconWidth(skill)+text_width)/2;

        Component xpBar = Component.text("\uF000").font(key)
            .append(getPixelSpace(xpBarSpace))
            .append(createBarComponent(progress, barColor))
            .append(getPixelSpace(xpBarSpace))
            ;

        Component titleText = Component.text("\uF000").font(key)
            .append(getPixelSpace(textSpace))
            .append(icon)
            .append(text)
            .append(getPixelSpace(textSpace));

        Component progressBar = Component.text("").append(xpBar).append(titleText);

        BossBar bossBar;
        if (activeBars.containsKey(playerID)) { 
            bossBar = activeBars.get(playerID); 
            bossBar.name(progressBar); 
        } else { 
            bossBar = BossBar.bossBar( progressBar, 0, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS ); 
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
        for(int i = 0; i<segments; i++){
            /*if(i >= filled){
                color = Color.WHITE;
            }*/
            String append = barComponent.get(1);

            if(i==0){
                append = barComponent.get(0);
            }
            else if(i==segments-1){
                append = barComponent.get(2);
            }
            append += "\uF999";
            bar = bar.append(Component.text(append).color(TextColor.color(color.getRGB())));
        }
        return bar;
    }

    private static String getSkillIcon(String skill){
        switch (skill.toLowerCase()) {
            case "combat":
                return "\uE007";

            case "mining":
                return "\uE008";

            case "farming":
                return "\uE009";

            case "woodcutting":
                return "\uE010";

            case "excavation":
                return "\uE011";
        }
        return "";
    }

    private static int getIconWidth(String skill){
        if(trueIconWidth.containsKey(skill)){
            return Math.round(trueIconWidth.get(skill)/(trueIconHeight/iconHeight));
        }
        return 0;
    }

    private static int getTextWidth(String text){
        int pixeltext = 0;
        outer:
        for(char current_char : text.toCharArray()){ 
            for(int i = 5; i>=1; i--){ 
                if(charWidths.get(i).contains(current_char)){ 
                    pixeltext += i + 1; 
                    continue outer; 
                } 
            } 
        }
        pixeltext--;
        return pixeltext;
    }

    private static int getNumberWidth(double num){
        num = Math.round(num);
        int count = 0;
        while(true){
            if(num >= 1){
                count++;
                num /= 10;
            }else{
                break;
            }
        }
        return count;
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

        for(int value : List.of(128,64,32,16,8,4,2,1)){

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