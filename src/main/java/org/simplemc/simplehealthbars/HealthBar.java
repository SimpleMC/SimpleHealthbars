package org.simplemc.simplehealthbars;

/**
 * Holds health bar data
 * 
 * @author Taylor
 * 
 */
class HealthBar
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
    HealthBar(int taskID, String bar, boolean entityHasCustomName)
    {
        this.taskID = taskID;
        this.bar = bar;
        this.entityHasCustomName = entityHasCustomName;
    }
    
    int getTaskID()
    {
        return taskID;
    }
    
    void setTaskID(int taskID)
    {
        this.taskID = taskID;
    }
    
    String getBar()
    {
        return bar;
    }
    
    void setBar(String bar)
    {
        this.bar = bar;
    }

    boolean entityHasCustomName()
    {
        return entityHasCustomName;
    }
}
