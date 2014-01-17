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
package com.evilco.bukkit.util.plugin.command;

import com.evilco.bukkit.util.plugin.command.annotation.Command;
import com.evilco.bukkit.util.plugin.command.annotation.CommandHandler;
import com.evilco.bukkit.util.plugin.java.ReflectionUtility;
import com.google.common.reflect.ClassPath;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandManager {

	/**
	 * Stores the proper bukkit command map.
	 */
	protected CommandMap commandMap = null;

	/**
	 * Stores the fallback command map.
	 */
	protected CommandMap fallbackCommandMap = null;

	/**
	 * Stores a logging instance.
	 */
	public final Logger logger;

	/**
	 * Stores the parent plugin.
	 */
	protected Plugin plugin;

	/**
	 * Constructs a new command manager.
	 * @param plugin
	 */
	public CommandManager(Plugin plugin) {
		this.plugin = plugin;

		// get logger
		this.logger = Logger.getLogger(this.getClass().getSimpleName());
		this.logger.setParent(plugin.getLogger());

		// search for sub package
		Package p = plugin.getClass().getPackage();

		if (p != null) {
			try {
				ClassPath classPath = ClassPath.from(plugin.getClass().getClassLoader());

				for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(p.getName())) {
					if (classInfo.getClass().isAnnotationPresent(CommandHandler.class)) this.registerCommandHandler(classInfo.getClass());
				}

			} catch (Exception ex) { } // ignore
		}
	}

	/**
	 * Returns the command map.
	 * @return
	 */
	public CommandMap getCommandMap () {
		// return default map
		if (this.commandMap != null) return this.commandMap;

		// return fallback map (if any)
		if (this.fallbackCommandMap != null) return this.fallbackCommandMap;

		// read command map (reflection to the rescue!)
		CommandMap commandMap = ReflectionUtility.getField (this.plugin.getServer ().getPluginManager (), "commandMap");

		// cache command map
		if (commandMap != null) return (this.commandMap = commandMap);

		// create fallback
		this.fallbackCommandMap = new SimpleCommandMap (this.plugin.getServer ());

		// register fallback listener
		this.plugin.getServer ().getPluginManager ().registerEvents ((new FallbackCommandListener (this)), this.plugin);

		// return fallback
		return this.fallbackCommandMap;
	}

	/**
	 * Registers a new command handler and it's commands.
	 * @param obj
	 */
	public void registerCommandHandler(Class<?> obj) {
		// verify
		if (!obj.isAnnotationPresent(CommandHandler.class)) {
			this.logger.log(Level.SEVERE, "Cannot register command handler \"" + obj.getCanonicalName() + "\": CommandHandler annotation is not present.");
			this.logger.log(Level.SEVERE, "Please report this error to the plugin author(s) (" + this.plugin.getDescription().getAuthors().toString() + ")");
			return;
		}

		// create map
		Map<String, Method> objectMap = new HashMap<String, Method>();
		Object object;

		try {
			object = obj.newInstance();
		} catch (InstantiationException ex) {
			this.logger.log(Level.SEVERE, "Cannot instantiate command class " + obj.getCanonicalName() + " for plugin " + this.plugin.getName() + ": Unknown problem", ex);
			this.logger.log(Level.SEVERE, "Please report this error to the plugin author(s) (" + this.plugin.getDescription().getAuthors().toString() + ")");
			return;
		} catch (IllegalAccessException ex) {
			this.logger.log(Level.SEVERE, "Cannot instantiate command class " + obj.getCanonicalName() + " for plugin " + this.plugin.getName() + "!", ex);
			this.logger.log(Level.SEVERE, "Please report this error to the plugin author(s) (" + this.plugin.getDescription().getAuthors().toString() + ")");
			return;
		}

		// walk methods
		for(Method method : obj.getMethods()) {
			// skip classes without annotations
			if (!method.isAnnotationPresent(Command.class)) continue;

			// decode annotation
			Command cmd = method.getAnnotation(Command.class);

			// register command
			this.registerCommand (cmd, obj, method);
		}
	}

	/**
	 * Registers a new command.
	 * @param command
	 * @param handlerObject
	 * @param handlerMethod
	 */
	public void registerCommand (Command command, Object handlerObject, Method handlerMethod) {
		// get command map
		CommandMap map = this.getCommandMap ();

		// create new command
		DynamicCommand newCommand = new DynamicCommand (command.aliases (), command.description (), command.usage (), this, handlerObject, handlerMethod);

		// set permissions
		newCommand.setPermissions (command.permissions ());

		// register
		map.register (newCommand.getName (), newCommand);
	}
}
