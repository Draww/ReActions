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
import me.fromgate.reactions.event.RegionLeaveEvent;
import me.fromgate.reactions.externals.RaWorldGuard;
import me.fromgate.reactions.module.wgbridge.WGBridge;
import me.fromgate.reactions.util.Util;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;

import java.util.List;

public class RegionLeaveActivator extends Activator {

    private String region;

    RegionLeaveActivator(String name, String group, YamlConfiguration cfg) {
        super(name, group, cfg);
    }

    public RegionLeaveActivator(String name, String region) {
        super(name, "activators");
        this.region = region;
    }

    @Override
    public boolean activate(Event event) {
        if (!(event instanceof RegionLeaveEvent)) return false;
        RegionLeaveEvent be = (RegionLeaveEvent) event;
        if (!be.getRegion().equalsIgnoreCase(WGBridge.getFullRegionName(this.region))) return false;
        return Actions.executeActivator(be.getPlayer(), this);
    }

    @Override
    public boolean isLocatedAt(Location loc) {
        if (!RaWorldGuard.isConnected()) return false;
        List<String> rgs = RaWorldGuard.getRegions(loc);
        if (rgs.isEmpty()) return false;
        return rgs.contains(this.region);
    }

    @Override
    public void save(String root, YamlConfiguration cfg) {
        cfg.set(root + ".region", this.region);
    }

    @Override
    public void load(String root, YamlConfiguration cfg) {
        this.region = cfg.getString(root + ".region");
    }

    @Override
    public ActivatorType getType() {
        return ActivatorType.REGION_LEAVE;
    }

    @Override
    public boolean isValid() {
        return !Util.emptySting(region);
    }

    public String getRegion() {
        return this.region;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name).append(" [").append(getType()).append("]");
        if (!getFlags().isEmpty()) sb.append(" F:").append(getFlags().size());
        if (!getActions().isEmpty()) sb.append(" A:").append(getActions().size());
        if (!getReactions().isEmpty()) sb.append(" R:").append(getReactions().size());
        sb.append(" (");
        sb.append("region:").append(this.region);
        sb.append(")");
        return sb.toString();
    }

}
