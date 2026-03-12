package com.github.DivionMagKekse.Events;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.view.AnvilView;

public class AnvilPrepare implements Listener {

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        AnvilInventory inv = event.getInventory();
        AnvilView view = event.getView();
        ItemStack first = inv.getItem(0);
        ItemStack second = inv.getItem(1);

        if (first == null || second == null) return;

        ItemStack result = event.getResult();

        
        if (first.getType() == Material.BOW) {
            boolean hasInf = hasEnchant(first, Enchantment.INFINITY);
            boolean hasMend = hasEnchant(first, Enchantment.MENDING);
            boolean secondInf = hasEnchant(second, Enchantment.INFINITY);
            boolean secondMend = hasEnchant(second, Enchantment.MENDING);

            if ((hasInf && secondMend) || (hasMend && secondInf)) {
                if (result == null) result = first.clone();
                result.addUnsafeEnchantment(Enchantment.INFINITY, 1);
                result.addUnsafeEnchantment(Enchantment.MENDING, 1);

                event.setResult(result);

                
                view.setRepairCost(30);
                return;
            }
        }

 
        if (first.getType() == Material.ENCHANTED_BOOK && second.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta firstMeta = (EnchantmentStorageMeta) first.getItemMeta();
            EnchantmentStorageMeta secondMeta = (EnchantmentStorageMeta) second.getItemMeta();

            if (firstMeta != null && secondMeta != null) {
                boolean hasInf = firstMeta.hasStoredEnchant(Enchantment.INFINITY) || secondMeta.hasStoredEnchant(Enchantment.INFINITY);
                boolean hasMend = firstMeta.hasStoredEnchant(Enchantment.MENDING) || secondMeta.hasStoredEnchant(Enchantment.MENDING);

                if (hasInf && hasMend) {
                    ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
                    meta.addStoredEnchant(Enchantment.INFINITY, 1, true);
                    meta.addStoredEnchant(Enchantment.MENDING, 1, true);
                    book.setItemMeta(meta);

                    event.setResult(book);
                    view.setRepairCost(30);
                    return;
                }
            }
        }
    }

    private boolean hasEnchant(ItemStack stack, Enchantment ench) {
        if (stack == null) return false;
        if (stack.getType() == Material.ENCHANTED_BOOK && stack.getItemMeta() instanceof EnchantmentStorageMeta meta) {
            return meta.hasStoredEnchant(ench);
        }
        return stack.containsEnchantment(ench);
    }
}
