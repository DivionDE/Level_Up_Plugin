package com.github.DivionMagKekse.Config;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import com.github.DivionMagKekse.Main_LevelUp;

import java.awt.Color;



public class Config {
    File cfgFile;
    FileConfiguration cfg;

    Main_LevelUp main;

    private final List<String> skills = List.of("combat", "mining", "farming", "woodcutting", "excavation");
    private final Map<String, Color> skill_color = Map.of("combat", Color.decode("#750b0b"), "mining", Color.decode("#35d1ec"), "farming", Color.decode("#2ab425"), 
                                                          "woodcutting", Color.decode("#423207"), "excavation", Color.decode("#585151"));
    private final Map<String, Map<String, Integer>> abilities = Map.of("mining", Map.of("oresight", 200, "veinminer", 100),
                                                                "excavation", Map.of("sedimentminer", 100, "treasure_finder", 100, "archaeologist", 200),
                                                                "farming", Map.of("elitefarmer", 0),
                                                                "woodcutting", Map.of("woodchopper", 100));

    public Config(Main_LevelUp main) {
        this.main = main;
        cfgFile = new File(main.getDataFolder(), "config.yml");
        cfg = YamlConfiguration.loadConfiguration(cfgFile);
    }

    public FileConfiguration getConfig() {
        return cfg;
    }

    public void saveConfig() {
        try {
            cfg.save(cfgFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Map<Material, Double> getBlockAndXP(String skill) {
        Map<Material, Double> blockXP = new HashMap<>();
        List<?> xpList = cfg.getMapList(skill.toLowerCase() + "-xp");
        double xp = 0;
        if(xpList != null){
            for(Object obj : xpList) {
                if(obj instanceof Map<?, ?> xpEntry) {
                    Object xpOb = xpEntry.get("xp");
                    if(xpOb instanceof Number number){
                        xp = number.doubleValue();
                    }
                    if(xpEntry.get("blocks") instanceof List<?> blocks) {
                        for(Object block : blocks) {
                            Material mat = null;
                            if(block instanceof Material material) {
                                mat = material;
                            }
                            else if(block instanceof String materialStr){
                                mat = Material.matchMaterial(materialStr);
                                
                            }
                            if(mat != null) {
                                blockXP.put(mat, xp);
                            }
                        }
                    } 
                }
            }
        }
        return blockXP;
    }

    public double getBlockXP(String skill, Material blockType) {
        return getBlockAndXP(skill).getOrDefault(blockType, 0.0)*getXP_Multiplier(skill);
    }

    public Map<EntityType, Double> getEntityAndXP(String skill){
        Map<EntityType, Double> entityXP = new HashMap<>();
        List<?> xpList = cfg.getMapList(skill.toLowerCase() + "-xp");
        double xp = 0;
        if(xpList != null){
            for(Object obj : xpList) {
                if(obj instanceof Map<?, ?> xpEntry) {
                    Object xpOb = xpEntry.get("xp");
                    if(xpOb instanceof Number number){
                        xp = number.doubleValue();
                    }
                    if(xpEntry.get("entities") instanceof List<?> entities) {
                        for(Object entity : entities) {
                            EntityType ent = null;
                            if(entity instanceof EntityType entType) {
                                ent = entType;
                            }
                            else if(entity instanceof String entityStr){
                                ent = EntityType.fromName(entityStr);
                                
                            }
                            if(ent != null) {
                                entityXP.put(ent, xp);
                            }
                        }
                    } 
                }
            }
        }
        return entityXP;
    }

    public double getEntityXP(String skill, EntityType entityType){
        return getEntityAndXP(skill).getOrDefault(entityType, 0.0)*getXP_Multiplier(skill);
    }

    public int getMobTier(EntityType entityType){
        if(cfg.getList("combat.tiers.tier1").contains(entityType.toString()))return 1;
        if(cfg.getList("combat.tiers.tier2").contains(entityType.toString()))return 2;
        if(cfg.getList("combat.tiers.tier3").contains(entityType.toString()))return 3;
        if(cfg.getList("combat.tiers.tier4").contains(entityType.toString()))return 4;
        return 0;
    }


    public String getBlockSkill(Material blockType){
        for(String skill : getAllSkills()){
            if(getBlockXP(skill, blockType) > 0){
                return skill;
            }
        }
        return null;
    }

    public String getEntitySkill(EntityType entityType){
        for(String skill : getAllSkills()){
            if(getEntityXP(skill, entityType) > 0){
                return skill;
            }
        }
        return null;
    }

    public boolean isSkillBlock(String skill, Material blockType){
        return getBlocks(skill).contains(blockType);
    }

    public List<Material> getBlocks(String skill){
        return getBlockAndXP(skill).keySet().stream().toList();
    }

    public double getXP_Multiplier(String skill) {
        return cfg.getDouble("xp_multipliers." + skill.toLowerCase());
    }

    public List<String> getAllSkills(){
        return cfg.getConfigurationSection("skills").getKeys(false).stream().toList();
    }

    public Map<String, Integer> getAllAbilities(){
        Map<String, Integer> abilities = new HashMap<>();
        for(String skill : getAllSkills()) {
            ConfigurationSection abilitiesSection = cfg.getConfigurationSection("abilities.skills." + skill.toLowerCase());
            if(abilitiesSection != null) {
                for(String ability : abilitiesSection.getKeys(false)) {
                    if(getAbilityLevel(skill, ability) < 0){
                        continue;
                    }
                    abilities.put(ability, cfg.getInt("abilities.skills." + skill.toLowerCase() + "." + ability));
                }   
            }
        }
        return abilities;
    }

    public int getAbilityLevel(String skill, String ability){
        return cfg.getInt("abilities.skills." + skill.toLowerCase() + "." + ability.toLowerCase(), -1);
    }

    public String getAbilitySkill(String ability){
        for(String skill : getAllSkills()) {
            ConfigurationSection abilitiesSection = cfg.getConfigurationSection("abilities.skills." + skill.toLowerCase());
            if(abilitiesSection != null) {
                for(String current_ability : abilitiesSection.getKeys(false)) {
                    if(current_ability.equalsIgnoreCase(ability)){
                        return skill;
                    }
                }   
            }
        }
        return null;
    }

    public boolean addSkill(String skill) {
        if(!skills.contains(skill.toLowerCase())) {
            skills.add(skill.toLowerCase());
            return true;
        }
        return false;
    }

    public Color getSkillColor(String skill){
        String hexCode = cfg.getString("skills." + skill);
        return Color.decode(hexCode);
    }

    public void configFileSetup() {
    	for(String skill : skills) {
        	if(!cfg.contains("skills." + skill)) {
                cfg.setComments("skills", List.of("Sets the color of the progress bar. Accepts HEX color values"));
                int colorInt = skill_color.get(skill).hashCode();
                String hex = String.format("#%06X", (0xFFFFFF & colorInt));
                cfg.set("skills." + skill, hex);
        	}
            if(!cfg.contains("xp_multipliers." + skill)) {
                cfg.setComments("xp_multipliers", List.of("Generall XP Multipliers for each Skill, can be used to balance the XP gain of each skill"));
                cfg.set("xp_multipliers." + skill, 1.0);
            }
        }

        if(!cfg.contains("abilities")){
            cfg.setComments("abilities", List.of("List of Abilities for each Skill, can be used to unlock Abilities at specific Levels. Put -1 to deactivate the ability"));
            for(String skill: abilities.keySet()){
                if(!cfg.contains("abilities.skills." + skill)){ 
                    cfg.set("abilities.skills." + skill , abilities.get(skill));
                }
            }
        }

        if(!cfg.contains("combat.tiers")){
            cfg.setComments("combat.tiers", List.of("Mob Tiers to determine the Chance for a Spawn Egg to Drop"));
        	cfg.set("combat.tiers.tier1", CombatTierDefaults.TIER_1());
            cfg.setComments("combat.tiers.tier1", List.of("Chance of tier 1 mob egg to drop: 0.5%"));
        	cfg.set("combat.tiers.tier2", CombatTierDefaults.TIER_2());
            cfg.setComments("combat.tiers.tier2", List.of("Chance of tier 2 mob egg to drop: 0.25%"));
        	cfg.set("combat.tiers.tier3", CombatTierDefaults.TIER_3());
            cfg.setComments("combat.tiers.tier3", List.of("Chance of tier 3 mob egg to drop: 0.125%"));
        	cfg.set("combat.tiers.tier4", CombatTierDefaults.TIER_4());
            cfg.setComments("combat.tiers.tier4", List.of("Chance of tier 4 mob egg to drop: 0.06125%"));
        }

        if(!cfg.contains("mining-xp")){
            cfg.setComments("mining-xp", List.of("Mining XP for each Block, XP fully customizable, can be used to balance the XP gain of each block"));
            cfg.set("mining-xp", MiningDefaults.DEFAULT_MINING_XP());
        }
        if
        (!cfg.contains("woodcutting-xp")){
            cfg.setComments("woodcutting-xp", List.of("Woodcutting XP for each Block, XP fully customizable, can be used to balance the XP gain of each block"));
            cfg.set("woodcutting-xp", WoodcuttingDefaults.DEFAULT_WOODCUTTING_XP());
        }

        if(!cfg.contains("farming-xp")){
            cfg.setComments("farming-xp", List.of("Farming XP for each Block, XP fully customizable, can be used to balance the XP gain of each block"));
            cfg.set("farming-xp", FarmingDefaults.DEFAULT_FARMING_XP());
        }

        if(!cfg.contains("excavation-xp")){
            cfg.setComments("excavation-xp", List.of("Excavation XP for each Block, XP fully customizable, can be used to balance the XP gain of each block"));
            cfg.set("excavation-xp", ExcavationDefaults.DEFAULT_EXCAVATION_XP());
        }

        if(!cfg.contains("combat-xp")){
            cfg.setComments("combat-xp", List.of("Combat XP for each Entitiy, XP fully customizable, can be used to balance the XP gain of each Entity"));
            cfg.set("combat-xp", CombatDefaults.DEFAULT_COMBAT_XP());
        }

        if(!cfg.contains("excavation.treasure_finder")){
            cfg.set("excavation.treasure_finder", TreasureDefaults.TREASURE_CHANCES());
            cfg.set("excavation.treasure_finder.drops", TreasureDefaults.TREASURE_DEFAULTS());
            cfg.setComments("excavation.treasure_finder", List.of("Chance for which rarity to drop in %, can be used to balance the Treasure Finder ability"));
            cfg.setComments("excavation.treasure_finder.drops", List.of("Customizeable list of droppable items"));
        }

        if(!cfg.contains("excavation.archaeologist")){
            cfg.set("excavation.archaeologist", ArchaeologistDefaults.ARCHAEOLOGIST_CHANCES());
            cfg.set("excavation.archaeologist.drops", ArchaeologistDefaults.ARCHAEOLOGIST_DEFAULTS());
            cfg.setComments("excavation.archaeologist", List.of("Chance for which rarity to drop in %, can be used to balance the Archaeologist ability"));
            cfg.setComments("excavation.archaeologist.drops", List.of("Customizeable list of droppable items"));
        }
        
        saveConfig();
    }

}