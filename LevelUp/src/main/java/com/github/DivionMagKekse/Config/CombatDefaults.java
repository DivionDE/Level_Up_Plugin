package com.github.DivionMagKekse.Config;

import java.util.List;
import java.util.Map;

public class CombatDefaults {
    public static List<Map<String, Object>> DEFAULT_COMBAT_XP() {
        return List.of(
                Map.of(
                        "xp", 1,
                        "entities", List.of(
                                "ZOMBIE", "HUSK", "STRAY", "DROWNED", "WOLF", "BEE")),
                Map.of(
                        "xp", 2,
                        "entities", List.of(
                                "SKELETON", "CREEPER", "ZOMBIFIED_PIGLIN", "PIGLIN",
                                "ZOMBIE_VILLAGER", "POLAR_BEAR", "SNOW_GOLEM"

                        )),
                Map.of(
                        "xp", 3,
                        "entities", List.of(
                                "SPIDER", "CAVE_SPIDER", "SLIME", "SILVERFISH",
                                "PHANTOM", "ZOGLIN")),
                Map.of(
                        "xp", 4,
                        "entities", List.of(
                                "PILLAGER", "WITCH", "MAGMA_CUBE", "BLAZE")),
                Map.of(
                        "xp", 5,
                        "entities", List.of(
                                "ENDERMITE", "VINDICATOR", "GUARDIAN", "ENDERMAN", "HOGLIN")),
                Map.of(
                        "xp", 7,
                        "entities", List.of(
                                "EVOKER", "ILLUSIONER", "GHAST", "RAVAGER", "VEX",
                                "WITHER_SKELETON", "SHULKER")),
                Map.of(
                        "xp", 8,
                        "entities", List.of(
                                "PIGLIN_BRUTE")),
                Map.of(
                        "xp", 10,
                        "entities", List.of(
                                "IRON_GOLEM", "PLAYER")),
                Map.of(
                        "xp", 50,
                        "entities", List.of(
                                "ELDER_GUARDIAN")),
                Map.of(
                        "xp", 100,
                        "entities", List.of(
                                "WITHER")),
                Map.of(
                        "xp", 250,
                        "entities", List.of(
                                "WARDEN")),
                Map.of(
                        "xp", 500,
                        "entities", List.of(
                                "ENDER_DRAGON")));
    }
}
