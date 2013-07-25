/**
 * This file is part of Plugin Utility.
 *
 * Copyright (C) 2013 Evil-Co <http://www.evil-co.com>
 * Plugin Utility is licensed under the GNU Lesser General Public License.
 *
 * Plugin Utility is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.evilco.bukkit.util.plugin.exception;

/**
 * Occurs if a dependency is missing.
 * @author			Johannes Donath <johannesd@evil-co.com>
 * @copyright		(C) 2013 Evil-Co <http://www.evil-co.com>
 * @license			GNU Lesser General Public License <http://www.gnu.org/licenses/lgpl.txt>
 * @package			com.evilco.bukkit.util.plugin.exception
 */
public class MissingDependencyException extends Exception {
	private static final long serialVersionUID = 1182025140535532462L;

	/**
	 * Stores the missing dependency name.
	 */
	protected String dependency;
	
	/**
	 * Constructs a new missing dependency exception.
	 * @param dependency
	 */
	public MissingDependencyException(String dependency) {
		this.dependency = dependency;
	}
	
	/**
	 * Returns the missing dependency's name.
	 * @return
	 */
	public String getDependency() {
		return this.dependency;
	}
}
