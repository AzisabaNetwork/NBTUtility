package net.azisaba.nbtutility;

import org.bukkit.plugin.java.JavaPlugin;

public final class NBTUtility extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getLogger().info("NBTUtility has been enabled.");
        this.getCommand("nbt").setExecutor(new NbtUtilityCommand());
        this.getCommand("nbt").setTabCompleter(new NbtUtilityTabCompleter());
    }

    @Override
    public void onDisable() {
        this.getLogger().info("NBTUtility has been disabled.");
    }
}