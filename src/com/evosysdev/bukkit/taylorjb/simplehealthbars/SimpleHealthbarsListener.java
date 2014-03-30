package com.evosysdev.bukkit.taylorjb.simplehealthbars;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class SimpleHealthbarsListener implements Listener
{
    private final int BAR_LENGTH = 20;
    private final char BAR_CHAR = 0x25ae;
    
    private Plugin plugin; // plugin instance
    private BukkitScheduler scheduler; // scheduler
    private Map<Integer, HealthBar> tasks; // tasks scheduled(by int) to remove
                                           // bar
    
    public SimpleHealthbarsListener(Plugin plugin)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        scheduler = Bukkit.getScheduler();
        tasks = new HashMap<Integer, HealthBar>();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(EntityDamageEvent event)
    {
        healthBar(event.getEntity(), event.getDamage());
    }
    
    private void healthBar(final Entity entity, double eventDamage)
    {
        if (!(entity instanceof LivingEntity)) return;
        final LivingEntity living = (LivingEntity) entity;
        
        String originalName = findOriginalName(living);
        
        // create health bar
        final String healthBar = ' ' + formatHealthBar(living, eventDamage);
        
        // check if we should have a custom name when we remove the bar
        HealthBar bar = tasks.get(entity.getEntityId());
        final boolean hasCustomName = bar != null ? bar.entityHasCustomName()
                : living.getCustomName() != null;
        
        // task to remove bar
        int taskID = scheduler.scheduleSyncDelayedTask(plugin, new Runnable()
        {
            public void run()
            {
                tasks.remove(entity.getEntityId());
                
                // restore original custom name if there was one
                if (hasCustomName)
                {
                    living.setCustomName(living.getCustomName().replace(
                            healthBar, ""));
                }
                // remove custom name if there wasn't one
                else
                {
                    living.setCustomNameVisible(false);
                    living.setCustomName(null);
                }
            }
        }, 100);
        
        // update existing health bar if it exists
        if (bar != null)
        {
            scheduler.cancelTask(bar.getTaskID());
            originalName = originalName.replace(bar.getBar(), "");
            bar.setTaskID(taskID);
            bar.setBar(healthBar);
        }
        // add bar to map
        else
        {
            tasks.put(entity.getEntityId(), new HealthBar(taskID, healthBar,
                    living.getCustomName() != null));
        }
        
        // actually set health bar on entity
        living.setCustomName(originalName + healthBar);
        living.setCustomNameVisible(true);
    }
    
    /**
     * Find original name of living entity
     * 
     * @param living
     *            entity to find name for
     * @return original entity name
     */
    private String findOriginalName(LivingEntity living)
    {
        if (living instanceof Player)
            return living.getCustomName() == null ?
                    ((Player) living).getDisplayName() : living.getCustomName();
        else
        {
            // format name
            String entityName = living.getType().name().toLowerCase();
            entityName = entityName.substring(0, 1).toUpperCase()
                    + entityName.substring(1);
            
            return living.getCustomName() == null ?
                    entityName : living.getCustomName();
        }
    }
    
    /**
     * Create health bar string
     * 
     * @param proportion
     *            proportion of health entity still has
     * @return health bar string
     */
    private String formatHealthBar(LivingEntity entity, double eventDamage)
    {
        float proportion = (float) ((entity.getHealth() - eventDamage) / entity.getMaxHealth());
        int hasHealth = Math.round(BAR_LENGTH * proportion);
        StringBuilder health =
                new StringBuilder(ChatColor.WHITE.toString())
                        .append('[')
                        .append(ChatColor.DARK_GREEN.toString());
        
        // construct has health part
        for (int i = 0; i < hasHealth; i++)
            health.append(BAR_CHAR);
        
        // empty health
        health.append(ChatColor.DARK_RED.toString());
        for (int i = hasHealth; i < BAR_LENGTH; i++)
            health.append(BAR_CHAR);
        
        return health.append(ChatColor.WHITE.toString()).append(']').toString();
    }
}
