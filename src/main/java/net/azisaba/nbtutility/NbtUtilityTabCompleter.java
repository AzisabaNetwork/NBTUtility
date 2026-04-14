package net.azisaba.nbtutility;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class NbtUtilityTabCompleter implements TabCompleter {

    private static final String[] SUB_COMMANDS = {"compare", "view", "sync", "add", "remove"};

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return Collections.emptyList();
        Player player = (Player) sender;

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], Arrays.asList(SUB_COMMANDS), completions);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                net.minecraft.server.v1_15_R1.ItemStack nmsItem =
                        CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand());
                if (nmsItem.hasTag()) {
                    StringUtil.copyPartialMatches(args[1], Objects.requireNonNull(nmsItem.getTag()).getKeys(), completions);
                }
            }
        }
        Collections.sort(completions);
        return completions;
    }
}