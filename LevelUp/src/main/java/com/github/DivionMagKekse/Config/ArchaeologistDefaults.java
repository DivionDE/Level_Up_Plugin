package com.github.DivionMagKekse.Config;

import java.util.List;
import java.util.Map;

public class ArchaeologistDefaults {
    public static Map<String, List<String>> ARCHAEOLOGIST_DEFAULTS() {
        return Map.of(
                        "common", List.of(
                                "POTTERY_SHARD", "BRICK", "TERRACOTTA",
                                "QUARTZ", "AMETHYST_SHARD", "COPPER_INGOT",
                                "GOLD_INGOT", "LAPIS_LAZULI", "EMERALD",
                                "BONE_BLOCK", "PRISMARINE_SHARD",
                                "PRISMARINE_CRYSTALS", "ECHO_SHARD",
                                "SUSPICIOUS_GRAVEL", "SUSPICIOUS_SAND"
                                ),
                        "uncommon", List.of(
                                "ENCHANTED_BOOK", "EXPERIENCE_BOTTLE",
                                "OBSIDIAN", "CRYING_OBSIDIAN", "END_STONE",
                                "GILDED_BLACKSTONE", "ENDER_PEARL", "EYE_OF_ENDER",
                                "RARE_POTTERY_SHERD", "GOLDEN_APPLE",
                                "DISC_FRAGMENT_5", "TOTEM_OF_UNDYING",
                                "ARMOR_TRIM_SMITHING_TEMPLATE", "BRUSH"
                                ),
                        "rare", List.of(
                                "MACE_HEAD", "NETHERITE_SCRAP",
                                "ANCIENT_DEBRIS", "NETHERITE_UPGRADE_SMITHING_TEMPLATE",
                                "TOTEM_OF_UNDYING", "LODESTONE",
                                "ECHO_SHARD", "ENCHANTED_BOOK",
                                "TRIDENT", "ENDER_CHEST",
                                "SCULK_CATALYST", "WITHER_SKELETON_SKULL"
                                ),
                        "epic", List.of(
                                "ENCHANTED_GOLDEN_APPLE", "NETHER_STAR",
                                "DIAMOND_CHESTPLATE", "NETHERITE_AXE",
                                "NETHERITE_SHOVEL", "NETHERITE_PICKAXE",
                                "DRAGON_BREATH", "TOTEM_OF_UNDYING",
                                "ELYTRA"
                                ),
                        "legendary", List.of(
                                "MACE", "DRAGON_EGG", "NETHER_STAR",
                                "NETHERITE_SCRAP", "ECHO_SHARD",
                                "SCULK_CATALYST", "ENCHANTED_GOLDEN_APPLE",
                                "MACE_HEAD", "ENDER_CHEST", "ANCIENT_DEBRIS"
                                ));
    }

    public static Map<String, Double> ARCHAEOLOGIST_CHANCES() {
        return Map.of(
                "common", 55.0,
                "uncommon", 30.0,
                "rare", 10.0,
                "epic", 3.5,
                "legendary", 1.5
        );
    }
}
