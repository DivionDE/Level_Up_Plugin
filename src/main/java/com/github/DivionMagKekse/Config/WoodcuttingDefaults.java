package com.github.DivionMagKekse.Config;

import java.util.List;
import java.util.Map;

public class WoodcuttingDefaults {
    public static List<Map<String, Object>> DEFAULT_WOODCUTTING_XP(){
        return List.of(
            Map.of(
                "xp", 0.5,
                "blocks", List.of(
                    "OAK_LEAVES", "SPRUCE_LEAVES", "BIRCH_LEAVES", 
                    "JUNGLE_LEAVES", "ACACIA_LEAVES", "DARK_OAK_LEAVES", 
                    "MANGROVE_LEAVES", "CHERRY_LEAVES", "NETHER_WART_BLOCK",
                    "WARPED_WART_BLOCK", "PALE_OAK_LEAVES", "BAMBOO"
                )
            ),
            Map.of(
                "xp", 1.0,
                "blocks", List.of(
                    "OAK_LOG", "SPRUCE_LOG", "BIRCH_LOG", "JUNGLE_LOG", 
                    "ACACIA_LOG", "DARK_OAK_LOG", "MANGROVE_LOG", 
                    "CHERRY_LOG", "PALE_OAK_LOG", "CRIMSON_STEM",
                    "WARPED_STEM"
                )
            )
        );
    }
}
