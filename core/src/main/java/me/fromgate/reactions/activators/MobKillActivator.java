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

package me.fromgate.reactions.activators;

import me.fromgate.reactions.actions.Actions;
import me.fromgate.reactions.event.MobKillEvent;
import me.fromgate.reactions.util.Locator;
import me.fromgate.reactions.util.Param;
import me.fromgate.reactions.util.Util;
import me.fromgate.reactions.util.Variables;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class MobKillActivator extends Activator {
    private String mobName;
    private String mobType;

    public MobKillActivator(String name, String group, YamlConfiguration cfg) {
        super(name, group, cfg);
    }

    public MobKillActivator(String name, String param) {
        super(name, "activators");
        this.mobType = param;
        this.mobName = "";
        Param params = new Param(param);
        if (params.isParamsExists("type")) {
            this.mobType = params.getParam("type");
            this.mobName = params.getParam("name");
        } else if (param.contains("$")) {
            this.mobName = this.mobType.substring(0, this.mobType.indexOf("$"));
            this.mobType = this.mobType.substring(this.mobName.length() + 1);
        }
    }


    @Override
    public boolean activate(Event event) {
        if (!(event instanceof MobKillEvent)) return false;
        MobKillEvent me = (MobKillEvent) event;
        if (mobType.isEmpty()) return false;
        if (me.getMob() == null) return false;
        if (!isActivatorMob(me.getMob())) return false;
        Variables.setTempVar("moblocation", Locator.locationToString(me.getMob().getLocation()));
        Variables.setTempVar("mobkiller", me.getPlayer() == null ? "" : me.getPlayer().getName());
        Variables.setTempVar("mobtype", me.getMob().getType().name());
        LivingEntity mob = me.getMob();
        Player player = mob instanceof Player ? (Player) mob : null;
        String mobName = (player == null) ? me.getMob().getCustomName() : player.getName();
        Variables.setTempVar("mobname", mobName != null && !mobName.isEmpty() ? mobName : me.getMob().getType().name());
        return Actions.executeActivator(me.getPlayer(), this);
    }


    private boolean isActivatorMob(LivingEntity mob) {
        if (!mobName.isEmpty()) {
            if (!ChatColor.translateAlternateColorCodes('&', mobName.replace("_", " ")).equals(getMobName(mob)))
                return false;
        } else if (!getMobName(mob).isEmpty()) return false;
        return mob.getType().name().equalsIgnoreCase(this.mobType);
    }


    private String getMobName(LivingEntity mob) {
        if (mob.getCustomName() == null) return "";
        return mob.getCustomName();
    }

    @Override
    public boolean isLocatedAt(Location l) {
        return false;
    }


    @Override
    public void save(String root, YamlConfiguration cfg) {
        cfg.set(root + ".mob-type", this.mobType);
        cfg.set(root + ".mob-name", this.mobName);
    }

    @Override
    public void load(String root, YamlConfiguration cfg) {
        this.mobType = cfg.getString(root + ".mob-type", "");
        this.mobName = cfg.getString(root + ".mob-name", "");
    }

    @Override
    public ActivatorType getType() {
        return ActivatorType.MOB_KILL;
    }

    @Override
    public boolean isValid() {
        return !Util.emptySting(mobType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name).append(" [").append(getType()).append("]");
        if (!getFlags().isEmpty()) sb.append(" F:").append(getFlags().size());
        if (!getActions().isEmpty()) sb.append(" A:").append(getActions().size());
        if (!getReactions().isEmpty()) sb.append(" R:").append(getReactions().size());
        sb.append(" (");
        sb.append("type:").append(mobType.isEmpty() ? "-" : mobType.toUpperCase());
        sb.append(" name:").append(mobName.isEmpty() ? "-" : mobName.isEmpty());
        sb.append(")");
        return sb.toString();
    }
}
