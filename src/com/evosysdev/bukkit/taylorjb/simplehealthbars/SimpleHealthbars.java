package com.evosysdev.bukkit.taylorjb.simplehealthbars;

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
        new SimpleHealthbarsListener(this);
        
        getLogger().info(getDescription().getName() + " version " + getDescription().getVersion() + " enabled!");
    }
}
