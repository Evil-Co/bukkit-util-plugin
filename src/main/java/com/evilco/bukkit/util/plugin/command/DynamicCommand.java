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

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @package com.evilco.bukkit.util.plugin.command
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2013 Evil-Co <http://www.evil-co.com>
 */
public class DynamicCommand extends org.bukkit.command.Command {

	/**
	 * Stores all supplied command flags.
	 */
	protected String flags;

	/**
	 * Stores the handler object.
	 */
	protected Object handlerObject;

	/**
	 * Stores the handler method.
	 */
	protected Method handlerMethod;

	/**
	 * Stores the parent manager.
	 */
	protected CommandManager parentManager;

	/**
	 * Stores the permission list for this command.
	 */
	protected String[] permissions = new String[0];

	/**
	 * @param aliases
	 * @param flags
	 * @param description
	 * @param usage
	 * @param parentManager
	 * @param handlerObject
	 * @param handlerMethod
	 */
	public DynamicCommand (String[] aliases, String flags, String description, String usage, CommandManager parentManager, Object handlerObject, Method handlerMethod) {
		super (aliases[0], description, usage, Arrays.asList (aliases));

		// store arguments
		this.parentManager = parentManager;
		this.handlerObject = handlerObject;
		this.handlerMethod = handlerMethod;
		this.flags = flags;
	}

	/**
	 * Executes the command.
	 * @param commandSender
	 * @param s
	 * @param strings
	 * @return
	 */
	@Override
	public boolean execute (CommandSender commandSender, String s, String[] strings) {
		// parse arguments
		CommandContext context = new CommandContext (flags);
		context.parse (strings);

		try {
			return ((Boolean) this.handlerMethod.invoke (this.handlerObject, commandSender, context));
		} catch (IllegalAccessException ex) {
			this.parentManager.logger.severe ("Cannot access " + this.handlerObject.getClass ().getName () + " -> " + this.handlerMethod.getName () + ": " + ex.getMessage ());
			this.parentManager.logger.severe ("This is an error in the implementation of the command " + this.getName () + ". Please contact the plugin author and report this issue.");
		} catch (InvocationTargetException ex) {
			this.parentManager.logger.severe ("Cannot execute " + this.handlerMethod.getName () + " on object of type " + this.handlerObject.getClass ().getName () + ": " + ex.getMessage ());
			this.parentManager.logger.severe ("This might be a bug in the plugin utilities. Please report this error to the author of this library.");
		}

		// execution failed
		return false;
	}

	/**
	 * Sets a list of permissions.
	 * @param permissions
	 */
	public void setPermissions (String[] permissions) {
		this.permissions = permissions;

		// send information to parent
		if (permissions == null) return;

		// create buffer
		StringBuilder buffer = new StringBuilder (permissions[0]);

		for (int i = 1; i < permissions.length; i++) {
			// append permission (and delimiter)
			buffer
				.append (";")
				.append (permissions[i]);
		}

		// send permission
		super.setPermission (buffer.toString ());
	}
}
