package com.evilco.bukkit.util.plugin.command.exception;

import org.bukkit.command.CommandException;

/**
 * @auhtor Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.org>
 */
public class CommandPermissionException extends CommandException {

	public CommandPermissionException () {
		super ();
	}

	public CommandPermissionException (String msg) {
		super (msg);
	}

	public CommandPermissionException (String msg, Throwable cause) {
		super (msg, cause);
	}
}
