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
package com.evilco.bukkit.util.plugin.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

	/**
	 * Stores a list of command aliases.
	 * @return
	 */
	String[] aliases();

	/**
	 * Stores the maximum amount of arguments.
	 * @return
	 */
	int argumentsMax() default 0;

	/**
	 * Stores the minimum amount of arguments.
	 * @return
	 */
	int argumentsMin() default 0;

	/**
	 * Stores a description.
	 * @return
	 */
	String description() default "";

	/**
	 * Stores flags.
	 * @return
	 */
	String flags() default "";

	/**
	 * Stores a help string.
	 * @return
	 */
	String help() default "";

	/**
	 * Stores a list of permissions.
	 * @return
	 */
	String[] permissions() default { };

	/**
	 * Stores the command usage.
	 * @return
	 */
	String usage() default "";
}
