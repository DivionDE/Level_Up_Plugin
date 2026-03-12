package com.github.DivionMagKekse.Config;

import java.util.List;
import java.util.Map;

public class FarmingDefaults {
    public static List<Map<String, Object>> DEFAULT_FARMING_XP(){
        return List.of(
            Map.of(
                "xp", 1,
                "blocks", List.of(
                    "WHEAT", "CARROTS", "POTATOES", "BEETROOTS"
                )
            ),
            Map.of(
                "xp", 2,
                "blocks", List.of(
                    "SUGAR_CANE", "CACTUS", "SWEET_BERRY_BUSH",
                    "CAVE_VINES", "CAVE_VINES_PLANT", "KELP", "KELP_PLANT", "SEAGRASS",
                    "TALL_SEAGRASS"
                )
            ),
            Map.of(
                "xp", 3,
                "blocks", List.of(
                    "MELON", "PUMPKIN", "CARVED_PUMPKIN", "COCOA",
                    "RED_MUSHROOM", "BROWN_MUSHROOM", "MUSHROOM_STEM",
                    "SMALL_DRIPLEAF", "BIG_DRIPLEAF", "RED_MUSHROOM_BLOCK",
                    "BROWN_MUSHROOM_BLOCK"
                )
            )
        );
    }
}
