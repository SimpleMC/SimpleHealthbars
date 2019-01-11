package org.simplemc.simplehealthbars;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Health bar listener
 * 
 * Listens to events and will create and manage mob health bars
 * 
 * @author Taylor
 * 
 */
public class SimpleHealthbarsListener implements Listener
{
    private Plugin plugin; // plugin instance
    private BukkitScheduler scheduler; // scheduler
    private Map<Integer, HealthBar> tasks; // entity health bars
    private int barLength; // length(in barChars) of health bar
    private char barChar; // character to construct bar out of
    private boolean showMobNames; // should we have mob names above heads?
    
    /**
     * Create health bar listener
     * 
     * @param plugin
     *            plugin instance
     * @param barLength
     *            length(in barChars) of health bar
     * @param barChar
     *            character to construct bar out of
     * @param showMobNames
     *            should we have mob names above heads?
     */
    SimpleHealthbarsListener(Plugin plugin, int barLength, char barChar,
                             boolean showMobNames)
    {
        this.plugin = plugin;
        this.barLength = barLength;
        this.barChar = barChar;
        this.showMobNames = showMobNames;
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        scheduler = Bukkit.getScheduler();
        tasks = new HashMap<>();
    }
    
    /**
     * Listen for entity damage events
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(EntityDamageEvent event)
    {
        if (!isBoss(event.getEntityType()))
            healthBar(event.getEntity(), event.getDamage());
    }
    
    /**
     * Create/update health bar
     * 
     * @param entity
     *            entity to put bar over
     * @param eventDamage
     *            damage from the event to apply to health bar
     */
    private void healthBar(final Entity entity, double eventDamage)
    {
        // ignore nonliving entities and for now, players
        if (!(entity instanceof LivingEntity) || entity instanceof Player)
            return;
        
        final LivingEntity living = (LivingEntity) entity;
        
        String originalName = findOriginalName(living);
        
        // create health bar
        final String healthBar = ' ' + formatHealthBar(living, eventDamage);
        
        // check if we should have a custom name when we remove the bar
        HealthBar bar = tasks.get(entity.getEntityId());
        final boolean hasCustomName = bar != null ? bar.entityHasCustomName()
                : living.getCustomName() != null;
        
        // task to remove bar
        int taskID = scheduler.scheduleSyncDelayedTask(plugin, () -> {
            tasks.remove(entity.getEntityId());

            // restore original custom name if there was one
            if (hasCustomName)
            {
                living.setCustomName(living.getCustomName().replace(healthBar, ""));
            }
            // remove custom name if there wasn't one
            else
            {
                living.setCustomNameVisible(false);
                living.setCustomName(null);
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
        // format name
        String entityName = living.getType().name().toLowerCase();
        entityName = entityName.substring(0, 1).toUpperCase()
                + entityName.substring(1);
        
        return living.getCustomName() == null ?
                (showMobNames ? entityName : "") : living.getCustomName();
    }
    
    /**
     * Create health bar string
     * 
     * @param entity
     *            Entity being damaged
     * @param eventDamage
     *            damage given
     *
     * @return health bar string
     */
    private String formatHealthBar(LivingEntity entity, double eventDamage)
    {
        double proportion =
                (entity.getHealth() - eventDamage) / entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        long hasHealth = Math.round(barLength * proportion);
        StringBuilder health = new StringBuilder();
        
        // show brackets if we are displaying the mob's name
        if (showMobNames) health.append(ChatColor.WHITE.toString()).append('[');
        
        health.append(ChatColor.DARK_GREEN.toString());
        
        // construct has health part
        for (int i = 0; i < hasHealth; i++)
            health.append(barChar);
        
        // empty health
        health.append(ChatColor.DARK_RED.toString());
        for (long i = hasHealth; i < barLength; i++)
            health.append(barChar);

        // show brackets if we are displaying the mob's name
        if (showMobNames) health.append(ChatColor.WHITE.toString()).append(']');
        
        return health.toString();
    }
    
    /**
     * Check if the entity type is a boss mob
     * 
     * Current boss mobs:
     * -ender dragon
     * -wither
     * 
     * @param entityType
     *            type of entity
     * @return if entity is a boss
     */
    private boolean isBoss(EntityType entityType)
    {
        switch (entityType)
        {
            case ENDER_DRAGON:
            case ELDER_GUARDIAN:
            case WITHER:
                return true;
            default:
                return false;
        }
    }
    
}
