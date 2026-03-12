package com.github.DivionMagKekse.Config;

import java.util.List;
import java.util.Map;

public class MiningDefaults {
    public static List<Map<String, Object>> DEFAULT_MINING_XP(){
        return List.of(
            Map.of(
                "xp", 0.5,
                "blocks", List.of("NETHERRACK")
            ),
            Map.of(
                "xp", 1.0,
                "blocks", List.of(
                    "STONE", "COBBLESTONE", "ANDESITE", "DIORITE", "GRANITE", 
                    "TUFF", "CALCITE", "BASALT", "END_STONE", "BLACKSTONE", 
                    "DEEPSLATE", "TERRACOTTA", "WHITE_TERRACOTTA", 
                    "ORANGE_TERRACOTTA", "MAGENTA_TERRACOTTA", 
                    "LIGHT_BLUE_TERRACOTTA", "YELLOW_TERRACOTTA", 
                    "LIME_TERRACOTTA", "PINK_TERRACOTTA", "GRAY_TERRACOTTA", 
                    "LIGHT_GRAY_TERRACOTTA", "CYAN_TERRACOTTA", 
                    "PURPLE_TERRACOTTA", "BLUE_TERRACOTTA", "BROWN_TERRACOTTA", 
                    "GREEN_TERRACOTTA", "RED_TERRACOTTA", "BLACK_TERRACOTTA"
                )  
            ),
            Map.of(
                "xp", 2.0,
                "blocks", List.of(
                    "COAL_ORE", "COPPER_ORE"
                )
            ),
            Map.of(
                "xp", 3.0,
                "blocks", List.of(
                    "DEEPSLATE_COAL_ORE", "DEEPSLATE_COPPER_ORE", "LAPIS_ORE",
                    "DEEPSLATE_LAPIS_ORE", "NETHER_GOLD_ORE", 
                    "NETHER_QUARTZ_ORE", "IRON_ORE", "DEEPSLATE_IRON_ORE"
                )
            ),
            Map.of(
                "xp", 4.0,
                "blocks", List.of(
                    "GOLD_ORE", "DEEPSLATE_GOLD_ORE", "REDSTONE_ORE",
                    "DEEPSLATE_REDSTONE_ORE", "ANETHYST_BLOCK"
                )
            ),
            Map.of(
                "xp", 7.0,
                "blocks", List.of(
                    "DIAMOND_ORE", "DEEPSLATE_DIAMOND_ORE", 
                    "BUDDING_AMETHYST_BLOCK"
                )
            ),
            Map.of(
                "xp", 10.0,
                "blocks", List.of(
                    "EMERALD_ORE"
                )
            ),
            Map.of(
                "xp", 15.0,
                "blocks", List.of(
                    "ANCIENT_DEBRIS", "DEEPSLATE_EMERALD_ORE"
                )
            ),
            Map.of(
                "xp", 27.0,
                "blocks", List.of(
                    "RAW_IRON_BLOCK", "RAW_COPPER_BLOCK"
                )
            ),
            Map.of(
                "xp", 36.0,
                "blocks", List.of(
                    "RAW_GOLD_BLOCK"
                )
            )
        );
    }
}
