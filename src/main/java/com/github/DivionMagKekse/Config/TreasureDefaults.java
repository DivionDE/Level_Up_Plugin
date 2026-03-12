package com.github.DivionMagKekse.Config;

import java.util.List;
import java.util.Map;

public class TreasureDefaults {
    public static Map<String, List<String>> TREASURE_DEFAULTS() {
        return Map.of(
                        "common", List.of(
                                "CLAY_BALL", "FLINT", "STICK",
                                "BONE", "ROTTEN_FLESH", "STRING",
                                "LEATHER", "COAL", "RAW_IRON",
                                "RAW_COPPER", "RAW_GOLD",
                                "IRON_NUGGET", "GOLD_NUGGET",
                                "PAPER"),
                        "uncommon", List.of(
                                "IRON_INGOT", "COPPER_INGOT",
                                "GOLD_INGOT", "LAPIS_LAZULI",
                                "EMERALD", "EXPERIENCE_BOTTLE",
                                "MAP", "BOOK", "COMPASS",
                                "CLOCK", "CHAINMAIL_HELMET",
                                "CHAINMAIL_BOOTS", "IRON_SHOVEL",
                                "IRON_PICKAXE", "SADDLE", "LEAD",
                                "GOLDEN_HORSE_ARMOR", "MUSIC_DISC_CAT",
                                "MUSIC_DISC_13", "BRICK"),
                        "rare", List.of(
                                "ENCHANTED_BOOK", "DIAMOND",
                                "GOLDEN_APPLE", "HEART_OF_THE_SEA",
                                "NAUTILUS_SHELL", "MUSIC_DISC_BLOCKS",
                                "MUSIC_DISC_STAL", "BELL",
                                "TOTEM_OF_UNDYING", "ANCIENT_DEBRIS"),
                        "epic", List.of(
                                "NETHERITE_SCRAP", "ENCHANTED_GOLDEN_APPLE",
                                "NETHERITE_UPGRADE_SMITHING_TEMPLATE",
                                "TRIDENT", "ARMOR_TRIM_SMITHING_TEMPLATE",
                                "DISC_FRAGMENT_5", "TOTEM_OF_UNDYING"),
                        "legendary", List.of(
                                "ELYTRA", "NETHER_STAR", "MACE", "DRAGON_EGG",
                                "WITHER_SKELETON_SKULL", "ENCHANTED_GOLDEN_APPLE",
                                "DIAMOND_CHESTPLATE", "NETHERITE_PICKAXE",
                                "NETHERITE_AXE", "LODESTONE"));
    }

    public static Map<String, Double> TREASURE_CHANCES() {
        return Map.of(
                "common", 85.0,
                "uncommon", 12.0,
                "rare", 2.5,
                "epic", 0.4,
                "legendary", 0.1
        );
    }
}
