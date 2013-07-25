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
package com.evilco.bukkit.util.plugin.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A simple stream utility.
 * @author			Johannes Donath <johannesd@evil-co.com>
 * @copyright		(C) 2013 Evil-Co <http://www.evil-co.com>
 * @license			GNU Lesser General Public License <http://www.gnu.org/licenses/lgpl.txt>
 * @package			com.evilco.bukkit.util.plugin.exception
 */
public class StreamUtil {

	/**
	 * Copies the contents of an input stream into an output stream.
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copy(InputStream in, OutputStream out) throws IOException {
		// create buffer
		byte[] buffer = new byte[8192]; // This should work for bigger files without problems, too ...
		int length = 0;
		
		// copy stream
		while((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}
	}
}
