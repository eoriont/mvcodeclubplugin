package net.oriont.codeclub;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin implements Listener {

    String[] ranks = {"developer","coder","hacker","neophyte","instructor"};
    String[] locations = {"MV", "GB", "SF", "RC", "AL"};

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(label.equalsIgnoreCase("addrank")) {
            testPlayerRank(args, (Player)sender);
        }
        if(label.equalsIgnoreCase("removerank")) {
            testPlayerRemoveRank(args, (Player)sender);
        }

        if(label.equalsIgnoreCase("spawn")) {
            Player p = (Player) sender;
            reloadConfig();
            Location loc = new Location(getServer().getWorld((String)getConfig().get("spawn.world")), (double)getConfig().get("spawn.x"), (double)getConfig().get("spawn.y"), (double)getConfig().get("spawn.z"));
            p.teleport(loc);
        }

        if (label.equalsIgnoreCase("setspawn")) {
            Player p = (Player) sender;

            getConfig().set("spawn.world", p.getLocation().getWorld().getName());
            getConfig().set("spawn.x", p.getLocation().getX());
            getConfig().set("spawn.y", p.getLocation().getY());
            getConfig().set("spawn.z", p.getLocation().getZ());
            saveConfig();
            success(p, "Set the spawn to your location!");
        }

        if (label.equalsIgnoreCase("letbuild")) {

        }

        return true;
    }

    public boolean listContains(String thing, String[] list) {
        for (int i = 0; i < list.length; i++) {
            if (thing.equalsIgnoreCase(list[i])) {
                return true;
            }
        }
        return false;
    }

    public void letBuild(String[] args, Player p) {

    }

    public void addRank(Player p, String rank, String location) {
        getServer().dispatchCommand(getServer().getConsoleSender(), "pex user "+p.getName()+" group add "+location.toUpperCase());
        getServer().dispatchCommand(getServer().getConsoleSender(), "pex user "+p.getName()+" group add "+rank.toLowerCase());
        if(rank.equalsIgnoreCase("developer") || rank.equalsIgnoreCase("coder")) {
            getServer().dispatchCommand(getServer().getConsoleSender(), "rg addmember "+location.toLowerCase()+" "+p.getName()+" -w world");
        }
    }

    public void removeRank(Player p) {
        for(int i = 0; i < ranks.length; i++) {
            getServer().dispatchCommand(getServer().getConsoleSender(), "pex user "+p.getName()+" group remove "+ranks[i].toUpperCase());
        }
        for(int i = 0; i < locations.length; i++) {
            getServer().dispatchCommand(getServer().getConsoleSender(), "pex user "+p.getName()+" group remove "+locations[i].toUpperCase());
            getServer().dispatchCommand(getServer().getConsoleSender(), "rg removemember "+locations[i].toLowerCase()+" "+p.getName()+" -w world");
        }

    }

    public void testPlayerRank(String[] args, Player sender) {
        if(args.length == 0) {
            error(sender, "/addrank <player> <rank> <location>");
        } else if(args.length == 3) {
            Player p = Bukkit.getPlayer(args[0]);
            if(p != null) {
                String rank = args[1];
                if(listContains(rank, ranks)) {
                    String location = args[2];
                    if(listContains(location, locations)) {
                        removeRank(p);
                        addRank(p, rank, location);
                        success(sender, "Added "+p.getName()+" to rank "+rank.toLowerCase()+" and location "+location.toUpperCase());
                    } else {
                        error(sender, "Cannot find location '"+location.toUpperCase()+"'!");
                    }
                } else {
                    error(sender, "Cannot find rank '"+rank.toLowerCase()+"'!");
                }
            } else {
                error(sender, "Cannot find player '"+args[0]+"'!");
            }
        } else {
            error(sender, "Not the correct amount of arguments!");
        }
    }

    public void testPlayerRemoveRank(String[] args, Player sender) {
        if(args.length == 0) {
            error(sender, "/removerank <player>");
        } else if(args.length == 1) {
            Player p = Bukkit.getPlayer(args[0]);
            if(p != null) {
                removeRank(p);
                success(sender, "Removed all ranks from "+p.getName());
            } else {
                error(sender, "Could not find the player '"+args[0]+"'!");
            }
        } else {
            error(sender, "Not the correct amount of arguments!");
        }
    }

    public void error(Player p, String s) {
        p.sendMessage(ChatColor.RED+"ERROR: "+s);
    }
    public void success(Player p, String s) {
        p.sendMessage((ChatColor.GREEN+"SUCCESS: "+s));
    }
}
