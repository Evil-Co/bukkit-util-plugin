/**
 * This file is part of plugin.
 *
 * Copyright (C) 2013 Evil-Co <http://www.evil-co.com>
 * plugin is licensed under the GNU Lesser General Public License.
 *
 * plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * This file is part of the plugin.
 * The plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * The plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with the plugin. If not, see <http://www.gnu.org/licenses/>.
 */
package com.evilco.bukkit.util.plugin.command;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class FallbackCommandListener implements Listener {

	/**
	 * Stores the parent command manager.
	 */
	protected CommandManager manager;

	/**
	 * @param manager
	 */
	public FallbackCommandListener (CommandManager manager) {
		this.manager = manager;
	}

	/**
	 * Handles player commands.
	 * @param event
	 */
	@EventHandler (ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		// dispatch command
		if (!this.manager.getCommandMap ().dispatch (event.getPlayer (), event.getMessage ())) return;

		// cancel event (command has been processed)
		event.setCancelled (true);
	}
}
