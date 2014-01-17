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
package com.evilco.bukkit.util.plugin.configuration;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

/**
 * @package com.evilco.bukkit.util.plugin.configuration
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.org>
 */
public enum ConfigurationPropertyType {
	BOOLEAN (Boolean.class),
	FLOAT (Float.class),
	DOUBLE (Double.class),
	INTEGER (Integer.class),
	STRING (String.class),
	LIST (List.class),
	SERIALIZED (null);

	/**
	 * Stores the primitive mapping.
	 */
	protected static final Map<Class<?>, ConfigurationPropertyType> PRIMITIVE_MAP = (new ImmutableMap.Builder<Class<?>, ConfigurationPropertyType> ()).
		put (boolean.class, BOOLEAN).
		put (float.class, FLOAT).
		put (double.class, DOUBLE).
		put (int.class, INTEGER).
		build ();

	/**
	 * Defines the type associated with this property type.
	 */
	private final Class<?> type;

	/**
	 * Constructs a new ConfigurationpropertyType value.
	 * @param type
	 */
	private ConfigurationPropertyType (Class<?> type) {
		this.type = type;
	}

	/**
	 * Converts a type into a property type.
	 * @param type
	 * @return
	 */
	public static ConfigurationPropertyType fromType (Class<?> type) {
		// convert primitive
		if (type.isPrimitive () && PRIMITIVE_MAP.containsKey (type)) return PRIMITIVE_MAP.get (type);

		// get representation
		for (ConfigurationPropertyType propertyType : ConfigurationPropertyType.values ()) {
			if (propertyType.getType () != null && propertyType.getType ().isAssignableFrom (type)) return propertyType;
		}

		// fallback
		return ConfigurationPropertyType.SERIALIZED;
	}

	/**
	 * Returns the associated type.
	 */
	public Class<?> getType () {

		return this.type;
	}
}
