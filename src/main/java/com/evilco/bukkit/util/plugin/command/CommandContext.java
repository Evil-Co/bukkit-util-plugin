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

import org.bukkit.command.CommandException;

import java.util.*;

public class CommandContext {

	/**
	 * Stores a list of arguments.
	 */
	protected List<String> arguments;

	/**
	 * Stores all argument values.
	 */
	protected Map<Character, String> argumentValues;

	/**
	 * Stores all flags.
	 */
	protected String flags = null;

	/**
	 * Stores a list of flags which can have values.
	 */
	protected String validValueFlags;

	/**
	 * Stores a list of flags.
	 */
	protected String validFlags;

	/**
	 * Stores a list of value flags.
	 */
	protected String valueFlags = null;

	/**
	 * @param flags
	 */
	public CommandContext (String flags) {
		// split up flags
		String[] flagTypes = flags.split (":");

		// store types
		this.validValueFlags = (flagTypes.length > 1 ? flagTypes[0] : "");
		this.validFlags = (flagTypes.length > 1 ? flagTypes[1] : flagTypes[0]);
		this.arguments = new ArrayList<String> ();
		this.argumentValues = new HashMap<Character, String> ();
	}

	/**
	 * Parses the supplied argument list.
	 * @param arguments
	 */
	public void parse (String[] arguments) {
		// initialize
		this.flags = new String ();
		this.valueFlags = new String ();

		// convert argument listing
		List<String> argumentList = new ArrayList<String> (Arrays.asList (arguments));

		// find flags
		if (argumentList.get (0).startsWith ("-")) {
			// get flag list
			String flags = argumentList.remove (0).substring (1);

			// iterate over flags
			for (int i = 0; i < flags.length (); i++) {
				if (this.validFlags.contains ("" + flags.charAt (i))) this.flags += flags.charAt (i);
				if (this.valueFlags.contains ("" + flags.charAt (i))) this.valueFlags += flags.charAt (i);
			}
		}

		// create argument buffer
		StringBuffer buffer = null;
		int currentFlag = 0;

		// iterate over arguments
		for (String argument : arguments) {
			// process flags
			if (currentFlag < this.valueFlags.length ()) {
				// create new buffer (or handle one word arguments)
				if (buffer == null) {
					// check for a new string literal
					if (argument.startsWith ("\"") || argument.startsWith ("'")) {
						buffer = new StringBuffer ();
						buffer.append (argument.substring (1));
					} else {
						this.argumentValues.put (this.valueFlags.charAt (currentFlag), argument);
						currentFlag++;
					}
				} else {
					buffer.append (" " + argument);

					// end literal
					if (argument.endsWith ("\"") || argument.endsWith ("'")) {
						// delete literal
						buffer.deleteCharAt ((buffer.length () - 1));

						// add value
						this.argumentValues.put (this.valueFlags.charAt (currentFlag), buffer.toString ());
						currentFlag++;
					}
				}
			// process arguments
			} else
				this.arguments.add (argument);
		}
	}

	/**
	 * Returns an argument.
	 * @param position
	 * @return
	 */
	public String getArgument (int position) {
		return this.arguments.get (position);
	}

	/**
	 * Returns the argument list.
	 * @return
	 */
	public List<String> getArgumentList () {
		return this.arguments;
	}

	/**
	 * Returns a flag value.
	 * @param flag
	 * @return
	 */
	public String getFlagValue (char flag) {
		return this.argumentValues.get (flag);
	}

	/**
	 * Checks whether a specific flag is set.
	 * @param flag
	 * @return
	 */
	public boolean hasFlag (char flag) {
		return (this.flags.contains ("" + flag) || this.valueFlags.contains ("" + flag));
	}
}
