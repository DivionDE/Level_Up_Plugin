package com.github.DivionMagKekse.Helper;

import java.awt.Color;
import java.util.HashMap;
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

    private static final Map<UUID, BossBar> activeBars = new HashMap<>();
    private static final Map<UUID, BukkitTask> removalTasks = new HashMap<>();

    private static final int SEGMENTS = 20;
    private static final int SEGMENT_WIDTH = 6;

    public static void showXPBar(Main_LevelUp main, UUID playerID, double currentXP, double neededXP, String skillName) {

        Player player = Bukkit.getPlayer(playerID);
        if (player == null) return;

        Color barColor = main.getMyConfig().getSkillColor(skillName);

        skillName = skillName.substring(0, 1).toUpperCase() + skillName.substring(1).toLowerCase();

        int level = main.getMyData().getPlayerLevel(playerID, skillName);
        float progress = (float) Math.min(currentXP / neededXP, 1.0);

        Key customFont = Key.key("levelup", "custom_font");

        String iconChar = getSkillIcon(skillName);

        String text = skillName + " Level: " + level + " " +
                String.format("%.1f / %.1f XP", currentXP, neededXP);

        Component bossbarComponent = buildBossbar(iconChar, text, progress, barColor, customFont);

        BossBar bar;

        if (activeBars.containsKey(playerID)) {

            bar = activeBars.get(playerID);
            bar.name(bossbarComponent);

        } else {

            bar = BossBar.bossBar(
                    bossbarComponent,
                    0,
                    BossBar.Color.WHITE,
                    BossBar.Overlay.PROGRESS
            );

            bar.addViewer(player);
            activeBars.put(playerID, bar);
        }

        if (removalTasks.containsKey(playerID)) {
            removalTasks.get(playerID).cancel();
        }

        BukkitTask task = Bukkit.getScheduler().runTaskLater(main, () -> removePlayerBar(playerID), 60L);

        removalTasks.put(playerID, task);
    }

    private static Component buildBossbar(String iconChar, String text, float progress, Color barColor, Key customFont) {

        Component icon = Component.text(iconChar).font(customFont);

        Component textComponent = Component.text(text)
                .font(Key.key("minecraft:default"));

        Component bar = createProgressBar(progress, barColor, customFont);

        int barWidth = SEGMENTS * SEGMENT_WIDTH;

        int textWidth = text.length() * 6 + 10;

        int offset = (barWidth - textWidth) / 2;

        return Component.text()
                .append(pixelSpace(offset))
                .append(icon)
                .append(Component.space())
                .append(textComponent)
                .append(pixelSpace(-offset))
                .append(bar)
                .build();
    }

    private static Component createProgressBar(float progress, Color barColor, Key customFont) {

        int filled = Math.round(progress * SEGMENTS);

        Component bar = Component.empty();

        for (int i = 0; i < SEGMENTS; i++) {

            String segment = "\uE002";

            if (i == 0) segment = "\uE001";
            else if (i == SEGMENTS - 1) segment = "\uE004";

            TextColor color = (i < filled)
                    ? TextColor.color(barColor.getRGB())
                    : TextColor.color(Color.WHITE.getRGB());

            bar = bar.append(Component.text(segment).font(customFont).color(color));
        }

        return bar;
    }

    private static Component pixelSpace(int pixels) {

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

        Map<Integer, String> map = pixels >= 0 ? positive : negative;

        int remaining = Math.abs(pixels);

        for (int value : map.keySet()) {

            while (remaining >= value) {

                space = space.append(Component.text(map.get(value)));
                remaining -= value;
            }
        }

        return space;
    }

    private static String getSkillIcon(String skill) {

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

    public static void removePlayerBar(UUID playerId) {

        BukkitTask task = removalTasks.remove(playerId);
        if (task != null) task.cancel();

        BossBar bar = activeBars.remove(playerId);

        if (bar != null) {

            Player player = Bukkit.getPlayer(playerId);

            if (player != null) bar.removeViewer(player);
        }
    }
}