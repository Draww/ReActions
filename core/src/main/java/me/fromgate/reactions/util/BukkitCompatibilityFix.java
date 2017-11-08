/*  
 *  ReActions, Minecraft bukkit plugin
 *  (c)2012-2017, fromgate, fromgate@gmail.com
 *  http://dev.bukkit.org/server-mods/reactions/
 *    
 *  This file is part of ReActions.
 *  
 *  ReActions is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ReActions is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with ReActions.  If not, see <http://www.gnorg/licenses/>.
 * 
 */

package me.fromgate.reactions.util;

import me.fromgate.reactions.util.message.M;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class BukkitCompatibilityFix {
    /*
     * In latest version of Bukkit API getShooter() method returns Projectile source
     * that not available in previous version of Bukkit API.
     * This method is returns LivingEntity instead of ProjectileSource
     */
    public static LivingEntity getShooter(Projectile projectile) {
        if (projectile == null) return null;
        try {
            Method getShooter = projectile.getClass().getDeclaredMethod("getShooter");
            Object shooterObject = getShooter.invoke(projectile);
            if (shooterObject != null && shooterObject instanceof LivingEntity) return (LivingEntity) shooterObject;
        } catch (Exception e) {
            M.logOnce("getShooter", "Method \"getShooter\" is not declared in Projectile class");
        }
        return null;
    }


    public static void setItemInHand(Player player, ItemStack item) {
        if (player == null) return;
        PlayerInventory inv = player.getInventory();
        Method setItem = null;
        try {
            setItem = PlayerInventory.class.getDeclaredMethod("setItemInMainHand", ItemStack.class);
        } catch (NoSuchMethodException e) {
            // nop
        }
        if (setItem == null) {
            try {
                setItem = PlayerInventory.class.getDeclaredMethod("setItemInHand", ItemStack.class);
            } catch (Exception e) {
                //nop
            }
        }
        try {
            setItem.invoke(inv, item);
        } catch (Exception e) {
            M.logOnce("setItemInHand", "Methods \"setItemInHand\" and \"setItemInMainHand\" are not declared in PlayerInventory class");
        }
    }

    @SuppressWarnings("deprecation")
    public static ItemStack getItemInHand(Player player) {
        if (player == null) return null;
        PlayerInventory inv = player.getInventory();
        Method getItem = null;
        try {
            getItem = PlayerInventory.class.getDeclaredMethod("getItemInMainHand");
        } catch (NoSuchMethodException e) {
            // nop
        }
        if (getItem == null) {
            try {
                getItem = PlayerInventory.class.getDeclaredMethod("getItemInHand", ItemStack.class);
            } catch (Exception e) {
                //nop
            }
        }
        try {
            Object itemObj = getItem.invoke(inv);
            if (itemObj != null && itemObj instanceof ItemStack) {
                return (ItemStack) itemObj;
            }
        } catch (Exception e) {
            M.logOnce("getItemInHand", "Methods \"getItemInHand\" and \"getItemInMainHand\" are not declared in PlayerInventory class (Old server?!)");
            return player.getItemInHand();
        }
        return null;
    }


    public static void setItemInHand(LivingEntity entity, ItemStack item) {
        if (entity == null) return;
        EntityEquipment inv = entity.getEquipment();
        Method setItem = null;
        try {
            setItem = EntityEquipment.class.getDeclaredMethod("setItemInMainHand", ItemStack.class);
        } catch (NoSuchMethodException e) {
            // nop
        }
        if (setItem == null) {
            try {
                setItem = EntityEquipment.class.getDeclaredMethod("setItemInHand", ItemStack.class);
            } catch (Exception e) {
                //nop
            }
        }
        try {
            setItem.invoke(inv, item);
        } catch (Exception e) {
            M.logOnce("EEsetItemInHand", "Methods \"setItemInHand\" and \"setItemInMainHand\" are not declared in EntityEquipment class");
        }
    }

    public static ItemStack getItemInHand(LivingEntity entity) {
        if (entity == null) return null;
        EntityEquipment inv = entity.getEquipment();
        Method getItem = null;
        try {
            getItem = EntityEquipment.class.getDeclaredMethod("getItemInMainHand");
        } catch (NoSuchMethodException e) {
            // nop
        }
        if (getItem == null) {
            try {
                getItem = EntityEquipment.class.getDeclaredMethod("getItemInHand", ItemStack.class);
            } catch (Exception e) {
                //nop
            }
        }
        try {
            Object itemObj = getItem.invoke(inv);
            if (itemObj != null && itemObj instanceof ItemStack) return (ItemStack) itemObj;
        } catch (Exception e) {
            M.logOnce("EEgetItemInHand", "Methods \"getItemInHand\" and \"getItemInMainHand\" are not declared in EntityEquipment class");
        }
        return null;
    }

    public static void setItemInOffHand(Player player, ItemStack item) {
        if (player == null) return;
        PlayerInventory inv = player.getInventory();
        Method setItem;
        try {
            setItem = PlayerInventory.class.getDeclaredMethod("setItemInOffHand", ItemStack.class);
            setItem.invoke(inv, item);
        } catch (Exception e) {
            M.logOnce("setItemInOffHand", "Method \"setItemInOffHand\" is not declared in PlayerInventory class");
        }
    }

    public static void setItemInOffHand(LivingEntity entity, ItemStack item) {
        if (entity == null) return;
        EntityEquipment inv = entity.getEquipment();
        Method setItem;
        try {
            setItem = EntityEquipment.class.getDeclaredMethod("setItemInOffHand", ItemStack.class);
            setItem.invoke(inv, item);
        } catch (Exception e) {
            M.logOnce("EEsetItemInOffHand", "Method \"setItemInOffHand\" is not declared in EntityEquipment class");
        }
    }

    public static ItemStack getItemInOffHand(LivingEntity entity) {
        if (entity == null) return null;
        EntityEquipment inv = entity.getEquipment();
        Method getItem;
        try {
            getItem = EntityEquipment.class.getDeclaredMethod("getItemInOffHand");
            Object itemObj = getItem.invoke(inv);
            if (itemObj != null && itemObj instanceof ItemStack) return (ItemStack) itemObj;
        } catch (Exception e) {
            M.logOnce("EEgetItemInOffHand", "Method \"getItemInOffHand\" is are not declared in EntityEquipment class");
        }
        return null;
    }

    public static ItemStack getItemInOffHand(Player player) {
        if (player == null) return null;
        PlayerInventory inv = player.getInventory();
        Method getItem;
        try {
            getItem = PlayerInventory.class.getDeclaredMethod("getItemInOffHand");
            Object itemObj = getItem.invoke(inv);
            if (itemObj != null && itemObj instanceof ItemStack) return (ItemStack) itemObj;
        } catch (Exception e) {
            M.logOnce("getItemInOffHand", "Method \"getItemInOffHand\" is are not declared in PlayerInventory class");
        }
        return null;
    }


    /*
     *  Fix health (double/int) compatibility issue
     *  Works nice in 1.5.2
     */
    public static double getEntityHealth(LivingEntity entity) {
        return getDoubleMethod("getHealth", entity, Damageable.class);
    }

    /*
     *  Fix health (double/int) compatibility issue
     *  Works nice in 1.5.2
     */
    public static double getEntityMaxHealth(LivingEntity entity) {
        return getDoubleMethod("getMaxHealth", entity, Damageable.class);
    }


    /*
     *  Fix health (double/int) compatibility issue
     *  Works nice in 1.5.2
     */
    public static void setEntityHealth(LivingEntity entity, double health) {
        executeMethodObjectDouble("setHealth", entity, Damageable.class, health);
    }

    /*
     *  Fix health (double/int) compatibility issue
     *  Works nice in 1.5.2
     */
    public static void setEntityMaxHealth(LivingEntity entity, double health) {
        executeMethodObjectDouble("setMaxHealth", entity, Damageable.class, health);
    }

    /*
     *  Fix damage (double/int) compatibility issue
     *  Works nice in 1.5.2
     */
    public static void damageEntity(LivingEntity entity, double damage) {
        executeMethodObjectDouble("damage", entity, Damageable.class, damage);
    }

    /*
     *  Fix damage (double/int) compatibility issue
     *  Works nice in 1.5.2
     */
    public static void setEventDamage(EntityDamageEvent event, double damage) {
        executeMethodObjectDouble("setDamage", event, EntityDamageEvent.class, damage);
    }

    /*
     *  Fix damage (double/int) compatibility issue
     *  Works nice in 1.5.2
     */
    public static double getEventDamage(EntityDamageEvent event) {
        return getDoubleMethod("getDamage", event, EntityDamageEvent.class);
    }

    /*
     *  Fix damage (double/int) compatibility issue
     *  Works nice in 1.5.2
     */
    public static void setItemPickupDelay(Item item, double pickupDelay) {
        executeMethodObjectDouble("setPickupDelay", item, Item.class, pickupDelay);
    }

    /*
     *  Fix damage (double/int) compatibility issue
     *  Works nice in 1.5.2
     */
    public static double getItemPickupDelay(Item item) {
        return getDoubleMethod("getPickupDelay", item, Item.class);
    }


    /*
     * Internal method, using reflections to access double or integer values
     */
    private static double getDoubleMethod(String methodName, Object object, Class<?> clazz) {
        if (object == null) return 0;
        try {
            Method method = clazz.getDeclaredMethod(methodName);
            Object value = method.invoke(object);
            double returnDouble = 0;
            if (method.getReturnType().equals(double.class)) returnDouble = (Double) value;
            else if (method.getReturnType().equals(int.class)) returnDouble = (double) (Integer) value;
            return returnDouble;
        } catch (Exception e) {
            M.logOnce("BCF" + methodName, "Looks like this version of BukkitAPI totally incompatible with API 1.7.x. Method \"" + methodName + "\" is not declared in " + object.getClass().getCanonicalName());
            e.printStackTrace();
        }
        return 0;
    }

    /*
     * Internal method, using reflections to access double or integer values
     */
    private static void executeMethodObjectDouble(String methodName, Object object, Class<?> clazz, double param) {
        if (object == null) return;
        boolean useInt = false;
        Method method;
        try {
            method = clazz.getDeclaredMethod(methodName, double.class);
        } catch (Exception e) {
            try {
                method = clazz.getDeclaredMethod(methodName, int.class);
                useInt = true;
            } catch (Exception e2) {
                return;
            }
        }
        if (method != null) {
            try {
                if (useInt) method.invoke(object, (int) param);
                else method.invoke(object, (double) param);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            M.logOnce("BCFix" + methodName, "Looks like this version of BukkitAPI totally incompatible with API 1.7.x. Method \"" + methodName + "\" is not declared in " + object.getClass().getCanonicalName() + " or not working properly");
    }


    /*
     * Create EntityDamageByEntityEvent according to version of Bukkit
     * Overrides damage (double/int) compatibility issue
     * Works nice with 1.5.2
     */
    public static EntityEvent createEntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause damageCause, double damage) {
        Class<?> EntityDamageByEntityEvent;
        try {
            EntityDamageByEntityEvent = Class.forName("org.bukkit.event.entity.EntityDamageByEntityEvent");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Constructor<?> constructor;
        boolean useInt = false;
        try {
            constructor = EntityDamageByEntityEvent.getConstructor(Entity.class, Entity.class, DamageCause.class, double.class);
        } catch (Exception e) {
            try {
                constructor = EntityDamageByEntityEvent.getConstructor(Entity.class, Entity.class, DamageCause.class, int.class);
                useInt = true;
            } catch (Exception e1) {
                M.logOnce("BCFixEntityDamageEvent", "Failed to determine constructor for EntityDamageByEntityEvent");
                return null;
            }
        }
        Object event;
        try {
            if (useInt) event = constructor.newInstance(damager, entity, DamageCause.ENTITY_ATTACK, (int) damage);
            else event = constructor.newInstance(damager, entity, DamageCause.ENTITY_ATTACK, damage);
        } catch (Exception e) {
            M.logOnce("BCFixEntityDamageEvent2", "Failed to initiate EntityDamageByEntityEvent");
            return null;
        }
        return (EntityEvent) event;
    }

    public static boolean isHandSlot(PlayerInteractEntityEvent event) {
        try {
            return event.getHand() == EquipmentSlot.HAND;
        } catch (NoSuchMethodError ignored) {
        }
        return true;
    }


    public static boolean isHandSlot(PlayerInteractEvent event) {
        try {
            return event.getHand() == EquipmentSlot.HAND;
        } catch (NoSuchMethodError ignored) {
        }
        return true;
    }

    public static Entity getHitEntity(ProjectileHitEvent event) {
        try {
            return event.getHitEntity();
        } catch (NoSuchMethodError e) {
            M.logOnce("event.getHitEntity()", "Failed to execute method getHitEntity in ProjectileHitEvent.class. Older server?");
        }
        return null;
    }

    public static boolean isDropItems(BlockBreakEvent event) {
        try {
            return event.isDropItems();
        } catch (NoSuchMethodError e) {
            M.logOnce("event.isDropItems()", "Failed to execute method isDropItems in BlockBreakEvent.class. Older server?");
        }
        return false;
    }

    public static void setDropItems(BlockBreakEvent event, boolean isDropItems) {
        try {
            event.setDropItems(isDropItems);
        } catch (NoSuchMethodError e) {
            M.logOnce("event.setDropItems()", "Failed to execute method setDropItems in BlockBreakEvent.class. Older server?");
        }
    }
}