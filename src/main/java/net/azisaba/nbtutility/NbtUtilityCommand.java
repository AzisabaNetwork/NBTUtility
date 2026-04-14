package net.azisaba.nbtutility;

import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.ItemStack;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.NBTTagString;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class NbtUtilityCommand implements CommandExecutor {

    private final String prefix = "§7[§dNBTUtility§7]§r ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (args.length == 0) {
            handleView(player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "sync":
                handleSync(player);
                break;
            case "add":
                handleAdd(player, args);
                break;
            case "remove":
                handleRemove(player, args);
                break;
            case "compare":
                handleCompare(player);
                break;
            case "view":
                handleView(player);
                break;
            default:
                player.sendMessage("§c未知のサブコマンドです");
                break;
        }
        return true;
    }

    private void handleSync(Player player) {
        org.bukkit.inventory.ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        org.bukkit.inventory.ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (mainHandItem.getType() == Material.AIR || offHandItem.getType() == Material.AIR) {
            sendMessage(player, "&c両手にアイテムを持ってください");
            return;
        }
        ItemStack offNms = CraftItemStack.asNMSCopy(offHandItem);
        NBTTagCompound offTag = offNms.getTag();
        ItemStack mainNms = CraftItemStack.asNMSCopy(mainHandItem);
        mainNms.setTag(offTag != null ? offTag.clone() : null);
        player.getInventory().setItemInMainHand(CraftItemStack.asBukkitCopy(mainNms));
        sendMessage(player, "メインハンドのNBTをオフハンドのNBTと同期しました");
        if (offTag == null) {
            sendMessage(player, "&eオフハンドにNBTが存在しなかったため、メインハンドのNBTを削除しました");
        }
    }

    private void handleAdd(Player player, String[] args) {
        if (args.length < 3) {
            sendMessage(player, "&cUsage: /nbtutil add <key> <value>");
            return;
        }
        String key = args[1];
        String value = args[2];
        org.bukkit.inventory.ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) return;
        ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        tag.set(key, NBTTagString.a(value));
        nmsItem.setTag(tag);
        player.getInventory().setItemInMainHand(CraftItemStack.asBukkitCopy(nmsItem));
        sendMessage(player, "&aメインハンドのNBTに&e" + key + ":" + value + "&aを追加しました");
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            sendMessage(player, "&cUsage: /nbtutil remove <key>");
            return;
        }
        String key = args[1];
        org.bukkit.inventory.ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) return;
        ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (!nmsItem.hasTag()) return;
        NBTTagCompound tag = nmsItem.getTag();
        if (tag.hasKey(key)) {
            tag.remove(key);
            if (tag.isEmpty()) nmsItem.setTag(null);
            else nmsItem.setTag(tag);
            player.getInventory().setItemInMainHand(CraftItemStack.asBukkitCopy(nmsItem));
            sendMessage(player, "&cキー: " + key + "を削除しました");
        } else {
            sendMessage(player, "&cそのキーは存在しません");
        }
    }

    private void handleCompare(Player player) {
        ItemStack mainNms = CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand());
        ItemStack offNms = CraftItemStack.asNMSCopy(player.getInventory().getItemInOffHand());
        NBTTagCompound mainTag = mainNms.getTag();
        NBTTagCompound offTag = offNms.getTag();
        if (mainTag == null && offTag == null) {
            sendMessage(player, "&a両方のアイテムにNBTが存在しません");
            return;
        }
        if (mainTag == null || offTag == null) {
            sendMessage(player, "&c片方のアイテムにのみNBTが存在します");
            return;
        }
        sendMessage(player, "&aNBT比較結果");
        Set<String> allKeys = new HashSet<>(mainTag.getKeys());
        allKeys.addAll(offTag.getKeys());
        boolean matched = true;
        for (String key : allKeys) {
            if (!mainTag.hasKey(key)) {
                sendMessage(player, "&6- " + key + ": &eオフハンドのみ存在");
                sendMessage(player, "&b値: &f" + offTag.get(key).toString());
                matched = false;
            } else if (!offTag.hasKey(key)) {
                sendMessage(player, "&6- " + key + ": &eメインハンドのみ存在");
                sendMessage(player, "&b値: &f" + mainTag.get(key).toString());
                matched = false;
            } else if (!mainTag.get(key).equals(offTag.get(key))) {
                sendMessage(player, "&c- " + key + ": &e不一致");
                sendMessage(player, "&aメインハンド: &f" + mainTag.get(key).toString());
                sendMessage(player, "&bオフハンド: &f" + offTag.get(key).toString());
                matched = false;
            }
        }
        if (matched) {
            sendMessage(player, "&aすべてのNBTが一致しました");
        }
    }

    private void handleView(Player player) {
        ItemStack mainNms = CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand());
        NBTTagCompound mainTag = mainNms.getTag();
        if (mainTag == null) {
            sendMessage(player, "&eNBTが存在しません");
        } else {
            sendMessage(player, "&a" + mainTag);
        }
    }

    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }
}