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
package com.evilco.bukkit.util.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipEntry;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.evilco.bukkit.util.plugin.exception.MissingDependencyException;
import com.evilco.bukkit.util.plugin.io.StreamUtil;

/**
 * A simple java plugin extension.
 * @author			Johannes Donath <johannesd@evil-co.com>
 * @copyright		(C) 2013 Evil-Co <http://www.evil-co.com>
 * @license			GNU Lesser General Public License <http://www.gnu.org/licenses/lgpl.txt>
 * @package			com.evilco.bukkit.util.plugin.exception
 */
public class SimpleJavaPlugin extends JavaPlugin {

	/**
	 * Extracts a file from the plugin archive.
	 * @param inputPath
	 * @param outputPath
	 */
	public void extractFile(String inputPath, File outputPath) {
		// check output file
		if (outputPath.exists()) return;
		
		// log
		this.getLogger().info("Extracting " + inputPath + " from plugin archive ...");
		
		// create streams
		InputStream in = null;
		OutputStream out = null;
		JarFile jar = null;
		
		// extract
		try {
			// get jar file
			jar = new JarFile(this.getFile());
			
			// get zip entry
			ZipEntry inputFile = jar.getEntry(inputPath);
			
			// verify jar entry
			if (inputFile == null) throw new FileNotFoundException();
			
			// get input stream
			in = jar.getInputStream(inputFile);
			
			// get output stream
			out = new FileOutputStream(outputPath);
			
			// copy file contents
			if (in != null) StreamUtil.copy(in, out);
		} catch (IOException ex) {
			// log error
			// XXX: This may cause some serious problems in the plugin execution!
			this.getLogger().log(Level.SEVERE, "Cannot extract file " + inputPath + " from plugin archive!", ex);
		} finally {
			// close input stream
			try {
				in.close();
			} catch (Exception ex) { } // ignore
			
			// close output stream
			try {
				out.close();
			} catch (Exception ex) { } // ignore
			
			// close jar file
			try {
				jar.close();
			} catch (Exception ex) { } // ignore
		}
	}
	
	/**
	 * Returns a plugin dependency.
	 * @param type
	 * @param name
	 * @return
	 * @throws MissingDependencyException 
	 */
	protected <T extends Plugin> T getDependency(Class<T> type, String name) throws MissingDependencyException {
		// get plugin
		T plugin = this.getSoftDependency(type, name);
		
		// check existance
		if (plugin == null) throw new MissingDependencyException(name);
		
		// return
		return plugin;
	}
	
	/**
	 * Returns a plugin soft dependency.
	 * @param name
	 * @return
	 */
	protected <T extends Plugin> T getSoftDependency(Class<T> type, String name) {
		// get plugin
		T plugin = type.cast(this.getServer().getPluginManager().getPlugin(name));
		
		// return
		return plugin;
	}
}