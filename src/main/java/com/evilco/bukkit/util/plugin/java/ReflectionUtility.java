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
package com.evilco.bukkit.util.plugin.java;

import java.lang.reflect.Field;

/**
 * Helps out with minor reflection tasks.
 * @author			Johannes Donath
 * @copyright			Copyright (C) 2013 Evil-Co <http://www.evil-co.org>
 */
public class ReflectionUtility {

	/**
	 * Returns a field value from any class.
	 * Note: This ignores security levels defined by the parent class.
	 * @param object
	 * @param fieldName
	 * @param <T>
	 * @return
	 */
	public static <T> T getField (Object object, String fieldName) {
		// get class
		Class<?> clazz = object.getClass ();

		// check the whole class tree
		do {
			try {
				// get the field of this value
				Field field = clazz.getDeclaredField (fieldName);

				// make field accessible
				if (!field.isAccessible ()) field.setAccessible (true);

				// return field value
				return ((T) field.get (object));

			// ignore all errors
			} catch (NoSuchFieldException ex) {
			} catch (IllegalAccessException ex) { }
		} while ((clazz = clazz.getSuperclass ()) != null);

		// in case of failure (field not found, illegal access?!)
		return null;
	}
}
