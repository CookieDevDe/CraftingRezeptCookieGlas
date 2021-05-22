package de.obvcookie.craftingrezepte;

import de.obvcookie.craftingrezepte.Commands.Content;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Skull;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class Craftingrezepte extends JavaPlugin {

    @Override
    public void onEnable() {
        Content content1 = new Content("a476695dfcc170bd74ead4cbaf175e13787cbdf7f32f08ffcaaff63858e3c120");
        content1.setName("Cookie Glas");
        content1.setLore(1, "Ein Cookie Glas");
        content1.setLore(5, "Made by ObvCookie and phoeniix <3");
        NamespacedKey key = new NamespacedKey(this, "cookie_glas");
        ShapedRecipe recipe1 = new ShapedRecipe(key, content1);
        recipe1.shape(" I ",
                      "GCG",
                      "GGG");
        recipe1.setIngredient('I', Material.IRON_INGOT);
        recipe1.setIngredient('C', Material.COOKIE);
        recipe1.setIngredient('G', Material.GLASS);
        Bukkit.addRecipe(recipe1);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
