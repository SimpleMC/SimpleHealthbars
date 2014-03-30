package com.evosysdev.bukkit.taylorjb.simplehealthbars;

/**
 * Holds health bar data
 * 
 * @author Taylor
 * 
 */
public class HealthBar
{
    private int taskID; // ID of task on scheduler
    private String bar; // actual health bar string
    private final boolean entityHasCustomName; // does the entity have a custom name
    
    /**
     * Init health bar
     * 
     * @param taskID
     *            ID of task on scheduler
     * @param bar
     *            actual health bar string
     */
    public HealthBar(int taskID, String bar, boolean entityHasCustomName)
    {
        this.taskID = taskID;
        this.bar = bar;
        this.entityHasCustomName = entityHasCustomName;
    }
    
    public int getTaskID()
    {
        return taskID;
    }
    
    public void setTaskID(int taskID)
    {
        this.taskID = taskID;
    }
    
    public String getBar()
    {
        return bar;
    }
    
    public void setBar(String bar)
    {
        this.bar = bar;
    }

    public boolean entityHasCustomName()
    {
        return entityHasCustomName;
    }
}
