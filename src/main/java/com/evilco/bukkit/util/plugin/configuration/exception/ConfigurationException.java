/*
 * This file is part of the WorldHub.
 *
 * The WorldHub is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * The WorldHub is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with the WorldHub. If not, see <http://www.gnu.org/licenses/>.
 */
package com.evilco.bukkit.util.plugin.configuration.exception;

/**
 * @package com.evilco.bukkit.util.plugin.configuration.exception
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.org>
 */
public abstract class ConfigurationException extends Exception {

	public ConfigurationException () {
		super ();
	}

	public ConfigurationException (String message) {
		super (message);
	}

	public ConfigurationException (String message, Throwable cause) {
		super (message, cause);
	}

	public ConfigurationException (Throwable cause) {
		super (cause);
	}
}
