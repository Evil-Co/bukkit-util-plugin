package com.evilco.bukkit.util.plugin.command;

/**
 * @auhtor Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.org>
 */
public class CommandRegistrationException extends Exception {

	public CommandRegistrationException () {
		super ();
	}

	public CommandRegistrationException (String message) {
		super (message);
	}

	public CommandRegistrationException (String message, Throwable cause) {
		super (message, cause);
	}

	public CommandRegistrationException (Throwable cause) {
		super (cause);
	}
}
