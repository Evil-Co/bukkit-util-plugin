/*******************************************************************************
 * This file is part of WorldHub.
 *
 * WorldHub is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WorldHub is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with WorldHub.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.evilco.bukkit.util.plugin.configuration.exception;

/**
 * @auhtor Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.org>
 */
public class ConfigurationLoadException extends ConfigurationException {

	public ConfigurationLoadException () {
		super ();
	}

	public ConfigurationLoadException (String message) {
		super (message);
	}

	public ConfigurationLoadException (String message, Throwable cause) {
		super (message, cause);
	}

	public ConfigurationLoadException (Throwable cause) {
		super (cause);
	}
}
