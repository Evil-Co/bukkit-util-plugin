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

import com.evilco.bukkit.util.plugin.configuration.annotation.Configuration;
import com.evilco.bukkit.util.plugin.configuration.annotation.ConfigurationComment;
import com.evilco.bukkit.util.plugin.configuration.annotation.ConfigurationProperty;
import com.evilco.bukkit.util.plugin.configuration.annotation.ConfigurationPropertyWrapper;
import com.evilco.bukkit.util.plugin.configuration.exception.ConfigurationException;
import com.evilco.bukkit.util.plugin.configuration.exception.ConfigurationLoadException;
import com.evilco.bukkit.util.plugin.configuration.exception.ConfigurationProcessorException;
import com.evilco.bukkit.util.plugin.configuration.exception.ConfigurationSaveException;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sun.reflect.ReflectionFactory;

import javax.xml.crypto.dsig.TransformException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @package com.evilco.bukkit.util.plugin.configuration
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.org>
 */
public class ConfigurationProcessor <T> {

	/**
	 * Defines the configuration settings.
	 */
	protected final Configuration objectMetadata;

	/**
	 * Defines the configuration processor type.
	 */
	protected final Class<T> objectType;

	/**
	 * Constructs a new ConfigurationException for a object.
	 * @param objectType
	 */
	public ConfigurationProcessor (Class<T> objectType) throws ConfigurationProcessorException {
		// verify
		if (!objectType.isAnnotationPresent (Configuration.class)) throw new ConfigurationProcessorException ("Invalid configuration type.");

		// store
		this.objectType = objectType;

		// get metadata
		this.objectMetadata = objectType.getAnnotation (Configuration.class);
	}

	public T Load (InputStream stream) throws ConfigurationException {
		try {
			// create xml document
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();

			// set properties
			documentBuilderFactory.setNamespaceAware (true);

			// get document
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder ();

			// construct from file
			Document document = documentBuilder.parse (stream);

			// get correct constructor
			ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory ();

			// get constructor
			Constructor defaultConstructor = this.objectType.getDeclaredConstructor ();
			Constructor serializationConstructor = reflectionFactory.newConstructorForSerialization (this.objectType, defaultConstructor);

			// setup constructor
			serializationConstructor.setAccessible (true);

			// create a new instance
			T object = this.objectType.cast (serializationConstructor.newInstance ());

			// retrieve all fields
			this.SerializeFrom (object, document.getDocumentElement ());

			// return object
			return object;
		} catch (ParserConfigurationException ex) {
			throw new ConfigurationLoadException (ex);
		} catch (SAXException ex) {
			throw new ConfigurationLoadException (ex);
		} catch (IOException ex) {
			throw new ConfigurationLoadException (ex);
		} catch (NoSuchMethodException ex) {
			throw new ConfigurationLoadException ("The configuration class does not declare a default constructor.", ex);
		} catch (InstantiationException ex) {
			throw new ConfigurationLoadException (ex);
		} catch (IllegalAccessException ex) {
			throw new ConfigurationLoadException (ex);
		} catch (InvocationTargetException ex) {
			throw new ConfigurationLoadException (ex);
		}
	}

	/**
	 * Saves a configuration object into an XML file.
	 * @param object
	 * @param stream
	 * @throws ConfigurationException
	 */
	public void Save (T object, OutputStream stream) throws ConfigurationException {
		try {
			// construct maps
			Map<String, Element> parentMap = new HashMap<String, Element> ();

			// construct XML factory
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();

			// set properties
			documentBuilderFactory.setNamespaceAware (true);

			// build document
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder ();
			Document document = documentBuilder.newDocument ();

			// create root element
			Element rootElement = document.createElementNS (this.objectMetadata.namespace (), this.objectMetadata.value ());
			document.appendChild (rootElement);

			// add data
			this.SerializeInto (document, object, rootElement);

			// write into file
			Transformer transformer = TransformerFactory.newInstance ().newTransformer ();

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty ("{http://xml.apache.org/xslt}indent-amount", "8");

			Result output = new StreamResult (stream);
			Source source = new DOMSource (document);

			transformer.transform (source, output);
		} catch (ParserConfigurationException ex) {
			throw new ConfigurationSaveException (ex);
		} catch (TransformerException ex) {
			throw new ConfigurationSaveException (ex);
		}
	}

	/**
	 * Loads a serialized element into the desired object.
	 * @param object
	 * @param element
	 * @throws ConfigurationException
	 */
	protected void SerializeFrom (Object object, Element element) throws ConfigurationException {
		try {
			for (Field field : object.getClass ().getFields ()) {
				// check for annotation
				if (!field.isAnnotationPresent (ConfigurationProperty.class)) continue;

				// get metadata
				ConfigurationProperty propertyMetadata = field.getAnnotation (ConfigurationProperty.class);

				// define parent
				Element parent = element;

				// find parent
				if (field.isAnnotationPresent (ConfigurationPropertyWrapper.class)) {
					// get metadata
					ConfigurationPropertyWrapper parentMetadata = field.getAnnotation (ConfigurationPropertyWrapper.class);

					// find element
					NodeList parentElements = parent.getElementsByTagNameNS (this.objectMetadata.namespace (), parentMetadata.value ());

					// not found?
					if (parentElements == null || parentElements.getLength () == 0) continue;

					// get element
					parent = ((Element) parentElements.item (0));
				}

				// find item
				NodeList items = parent.getElementsByTagNameNS (this.objectMetadata.namespace (), (propertyMetadata.value ().isEmpty () ? field.getName () : propertyMetadata.value ()));

				// not found?
				if (items == null || items.getLength () == 0) continue;

				// get element
				Element item = ((Element) items.item (0));

				// get value
				String value = item.getNodeValue ();

				// find corresponding type
				ConfigurationPropertyType type = ConfigurationPropertyType.fromType (field.getType ());

				// make field accessible
				field.setAccessible (true);

				// decode
				switch (type) {
					case BOOLEAN:
						field.setBoolean (object, Boolean.getBoolean (value));
						break;
					case DOUBLE:
						field.setDouble (object, Double.valueOf (value));
						break;
					case FLOAT:
						field.setFloat (object, Float.valueOf (value));
						break;
					case INTEGER:
						field.setInt (object, Integer.valueOf (value));
						break;
					case STRING:
						field.set (object, value);
						break;
					case SERIALIZED:
						// get reflection factory
						ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory ();

						// get constructor
						Constructor defaultConstructor = field.getType ().getDeclaredConstructor ();
						Constructor serializationConstructor = reflectionFactory.newConstructorForSerialization (field.getType (), defaultConstructor);

						// create instance
						Object obj = serializationConstructor.newInstance ();

						// serialize
						this.SerializeFrom (obj, item);

						// store
						field.set (object, obj);
						break;
				}
			}
		} catch (IllegalAccessException ex) {
			throw new ConfigurationProcessorException (ex);
		} catch (NoSuchMethodException ex) {
			throw new ConfigurationProcessorException ("One or more configuration properties lack a default constructor.", ex);
		} catch (InstantiationException ex) {
			throw new ConfigurationProcessorException (ex);
		} catch (InvocationTargetException ex) {
			throw new ConfigurationProcessorException (ex);
		}
	}

	/**
	 * Serializes data into the XML document.
	 * @param document
	 * @param object
	 * @param parent
	 * @throws ConfigurationException
	 */
	protected void SerializeInto (Document document, Object object, Element parent) throws ConfigurationException {
		// initialize temporary maps
		HashMap<String, Element> parentMap = new HashMap<String, Element> ();

		// loop through fields
		for (Field field : object.getClass ().getFields ()) {
			// verify annotation
			if (!field.isAnnotationPresent (ConfigurationProperty.class)) continue;

			// get field metadata
			ConfigurationProperty property = field.getAnnotation (ConfigurationProperty.class);

			// create property element
			Element propertyElement = document.createElementNS (this.objectMetadata.namespace (), (property.value ().isEmpty () ? field.getName () : property.value ()));

			// construct parent
			Element propertyParent = parent;

			// apply parent
			if (field.isAnnotationPresent (ConfigurationPropertyWrapper.class)) {
				// get parent metadata
				ConfigurationPropertyWrapper parentMetadata = field.getAnnotation (ConfigurationPropertyWrapper.class);

				// get name
				if (parentMap.containsKey (parentMetadata.value ()))
					propertyParent = parentMap.get (parentMetadata.value ());
				else {
					// check for collisions
					if (parent.getElementsByTagNameNS (this.objectMetadata.namespace (), parentMetadata.value ()).getLength () > 0) throw new ConfigurationProcessorException ("Wrapper " + parentMetadata.value () + " of property " + propertyElement.getTagName () + " collides with already registered property.");

					// create new parent element
					propertyParent = document.createElementNS (this.objectMetadata.namespace (), parentMetadata.value ());

					// append
					parent.appendChild (parent);
				}
			}

			// make field accessible
			field.setAccessible (true);

			// find type
			ConfigurationPropertyType type = ConfigurationPropertyType.fromType (field.getType ());

			// add data
			try {
				switch (type) {
					case BOOLEAN:
						propertyElement.setTextContent (((Boolean) field.getBoolean (object)).toString ());
						break;
					case DOUBLE:
						propertyElement.setTextContent (((Double) field.getDouble (object)).toString ());
						break;
					case FLOAT:
						propertyElement.setTextContent (((Float) field.getFloat (object)).toString ());
						break;
					case INTEGER:
						propertyElement.setTextContent (((Integer) field.getInt (object)).toString ());
						break;
					case STRING:
						propertyElement.setTextContent (String.valueOf (field.get (object)));
						break;
					case SERIALIZED:
						this.SerializeInto (document, field.get (object), propertyElement);
						break;
					default:
						throw new ConfigurationSaveException ("Cannot serialize type " + object.getClass ().getName () + " into configuration files.");
				}
			} catch (IllegalAccessException ex) {
				throw new ConfigurationProcessorException (ex);
			}

			// append comment
			if (field.isAnnotationPresent (ConfigurationComment.class)) {
				// get comment
				ConfigurationComment commentMetadata = field.getAnnotation (ConfigurationComment.class);

				// create XML comment
				Comment comment = document.createComment (commentMetadata.value ());

				// append to DOM
				parent.appendChild (comment);
			}

			// append to root
			parent.appendChild (propertyElement);
		}
	}
}
