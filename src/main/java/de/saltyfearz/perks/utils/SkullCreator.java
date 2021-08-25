package de.saltyfearz.perks.utils;

import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import com.mojang.authlib.GameProfile;

public class SkullCreator {
    public static ItemStack getCustomTextureHead(String texture, boolean base64) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) Objects.requireNonNull(head).getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        if (base64) {
            profile.getProperties().put("textures", new Property("textures", texture));
        } else {
            byte[] encoded = Base64.getEncoder().encode(String.format("{\"textures\": {\"SKIN\": {\"url\": \"%s\"}}}", texture).getBytes());
            profile.getProperties().put("textures", new Property("textures", new String(encoded)));
        }
        Field profileField;
        try {
            profileField = Objects.requireNonNull(meta).getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        head.setItemMeta(meta);
        return head;
    }
}
