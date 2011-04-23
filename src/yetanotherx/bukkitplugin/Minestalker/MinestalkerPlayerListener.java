package yetanotherx.bukkitplugin.Minestalker;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

/*
 * Minestalker Version 1.1 - Check last time user was online
 * Copyright (C) 2011 Yetanotherx <yetanotherx -a--t- gmail -dot- com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @author yetanotherx
 * 
 * Player listener
 *
 */
public class MinestalkerPlayerListener extends PlayerListener{

    public MinestalkerPlayerListener() {
    }

    /**
     * 
     * Called when a player quits, updates the database file
     * 
     */
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {

	String player = event.getPlayer().getName();
	long timestamp = System.currentTimeMillis();

	Minestalker.seenconfig.setProperty( player, timestamp );
	Minestalker.seenconfig.save();

    }	


}
