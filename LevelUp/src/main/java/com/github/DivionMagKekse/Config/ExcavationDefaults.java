package com.github.DivionMagKekse.Config;

import java.util.List;
import java.util.Map;

public class ExcavationDefaults {
    public static List<Map<String, Object>> DEFAULT_EXCAVATION_XP() {
        return List.of(
            Map.of(
                    "xp", 1,
                    "blocks", List.of(
                            "DIRT", "GRASS_BLOCK", "FARMLAND", "COARSE_DIRT",
                            "ROOTED_DIRT", "SNOW_BLOCK", "SNOW")),
            Map.of(
                    "xp", 2,
                    "blocks", List.of(
                            "GRAVEL", "SAND", "RED_SAND")),
            Map.of(
                    "xp", 3,
                    "blocks", List.of(
                            "CLAY", "SOUL_SAND", "SOUL_SOIL", "PODZOL", "MYCELIUM")
            )
        );
    }
}
