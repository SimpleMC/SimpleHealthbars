package org.simplemc.simplehealthbars;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * SimpleHealthbars Bukkit plugin
 * 
 * @author Taylor
 * 
 */
public class SimpleHealthbars extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        // save default config file if there is none
        this.saveDefaultConfig();
        
        FileConfiguration config = getConfig();
        
        // create listener
        new SimpleHealthbarsListener(this,
                config.getInt("bar-length", 20),
                (char) config.getInt("bar-char", 0x25ae),
                config.getBoolean("show-mob-names", true));
        
        // log enable
        getLogger().info(
                getDescription().getName() + " version "
                        + getDescription().getVersion() + " enabled!");
    }
    
    @Override
    public void onDisable()
    {
        getLogger().info(getDescription().getName() + " disabled.");
    }
}
