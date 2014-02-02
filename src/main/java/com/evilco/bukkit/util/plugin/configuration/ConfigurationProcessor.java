package com.evilco.bukkit.util.plugin.configuration;

import com.evilco.bukkit.util.plugin.configuration.annotation.Configuration;
import com.evilco.bukkit.util.plugin.configuration.annotation.ConfigurationProperty;
import com.evilco.bukkit.util.plugin.configuration.annotation.ConfigurationPropertyWrapper;
import com.evilco.bukkit.util.plugin.configuration.exception.ConfigurationException;
import com.evilco.bukkit.util.plugin.configuration.exception.ConfigurationLoadException;
import com.evilco.bukkit.util.plugin.configuration.exception.ConfigurationProcessorException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sun.reflect.ReflectionFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * @auhtor Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.org>
 */
public class ConfigurationProcessor<T> {

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

	/**
	 * De-Serializes the data.
	 * @param parent
	 * @param type
	 * @param childName
	 * @param <S>
	 * @return
	 * @throws ConfigurationException
	 */
	protected <S> S GetDataFrom (Element parent, Class<S> type, String childName) throws ConfigurationException {
		// guess property value
		ConfigurationPropertyType dataType = ConfigurationPropertyType.fromType (type);

		switch (dataType) {
			case BOOLEAN:
				// get proper element
				if (childName != null) {
					NodeList elementList = parent.getElementsByTagNameNS (this.objectMetadata.namespace (), childName);

					// verify list
					if (elementList.getLength () == 0) throw new ConfigurationLoadException ("Could not find element " + childName + ".");

					// get correct element
					parent = ((Element) elementList.item (0));
				}

				// get data
				return type.cast (Boolean.getBoolean (parent.getTextContent ()));
			case ENUM:
				// get proper element
				if (childName != null) {
					NodeList elementList = parent.getElementsByTagNameNS (this.objectMetadata.namespace (), childName);

					// verify list
					if (elementList.getLength () == 0) throw new ConfigurationLoadException ("Could not find element " + childName + ".");

					// get correct element
					parent = ((Element) elementList.item (0));
				}

				Class<? extends Enum> enumType = type.asSubclass (Enum.class);

				// get data
				return type.cast (Enum.valueOf (enumType, parent.getTextContent ()));
			case FLOAT:
				// get proper element
				if (childName != null) {
					NodeList elementList = parent.getElementsByTagNameNS (this.objectMetadata.namespace (), childName);

					// verify list
					if (elementList.getLength () == 0) throw new ConfigurationLoadException ("Could not find element " + childName + ".");

					// get correct element
					parent = ((Element) elementList.item (0));
				}

				// get data
				return type.cast (Float.valueOf (parent.getTextContent ()));
			case DOUBLE:
				// get proper element
				if (childName != null) {
					NodeList elementList = parent.getElementsByTagNameNS (this.objectMetadata.namespace (), childName);

					// verify list
					if (elementList.getLength () == 0) throw new ConfigurationLoadException ("Could not find element " + childName + ".");

					// get correct element
					parent = ((Element) elementList.item (0));
				}

				// get data
				return type.cast (Double.valueOf (parent.getTextContent ()));
			case INTEGER:
				// get proper element
				if (childName != null) {
					NodeList elementList = parent.getElementsByTagNameNS (this.objectMetadata.namespace (), childName);

					// verify list
					if (elementList.getLength () == 0) throw new ConfigurationLoadException ("Could not find element " + childName + ".");

					// get correct element
					parent = ((Element) elementList.item (0));
				}

				// get data
				return type.cast (Integer.getInteger (parent.getTextContent ()));
			case LIST:
				// get type arguments
				Class<?> listType = ((Class<?>) ((ParameterizedType) type.getGenericSuperclass ()).getActualTypeArguments ()[0]);

				// construct a new list object
				List list = new ArrayList ();

				// find data elements
				NodeList listElements = parent.getElementsByTagNameNS (this.objectMetadata.namespace (), childName);

				// deserialize data
				for (int i = 0; i < listElements.getLength (); i++) {
					Element element = ((Element) listElements.item (i));

					// create element
					list.add (this.GetDataFrom (element, listType, null));
				}

				return type.cast (list);
			case MAP:
				// get type arguments
				Class<?> mapType = ((Class<?>) ((ParameterizedType) type.getGenericSuperclass ()).getActualTypeArguments ()[0]);

				// construct a new map object
				Map map = new HashMap ();

				/// find data elements
				NodeList mapElements = parent.getElementsByTagNameNS (this.objectMetadata.namespace (), childName);

				// deserialize data
				for (int i = 0; i < mapElements.getLength (); i++) {
					Element element = ((Element) mapElements.item (i));

					// verify object
					if (!element.hasAttribute ("key")) continue;

					// create element
					map.put (element.getAttribute ("key"), this.GetDataFrom (element, mapType, null));
				}

				return type.cast (map);
			case OBJECT:
				// get proper element
				if (childName != null) {
					NodeList elementList = parent.getElementsByTagNameNS (this.objectMetadata.namespace (), childName);

					// verify list
					if (elementList.getLength () == 0) throw new ConfigurationLoadException ("Could not find element " + childName + ".");

					// get correct element
					parent = ((Element) elementList.item (0));
				}

				// get correct constructor
				ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory ();

				// get constructor
				Constructor defaultConstructor = null;

				try {
					defaultConstructor = type.getDeclaredConstructor ();
				} catch (NoSuchMethodException ex) {
					throw new ConfigurationProcessorException ("Could not find the default constructor.", ex);
				}

				Constructor serializationConstructor = reflectionFactory.newConstructorForSerialization (this.objectType, defaultConstructor);

				// setup constructor
				serializationConstructor.setAccessible (true);

				// construct object instance
				S object = null;

				try {
					serializationConstructor.newInstance ();
				} catch (Exception ex) {
					throw new ConfigurationProcessorException ("Could not initialize instance of type " + type.getCanonicalName () + ".", ex);
				}

				// iterate over all fields
				for (Field field : type.getFields ()) {
					// skip non-configuration fields
					if (!field.isAnnotationPresent (ConfigurationProperty.class)) continue;

					// initialize parent
					Element propertyParent = parent;

					// find parent based on annotations
					if (field.isAnnotationPresent (ConfigurationPropertyWrapper.class)) {
						// get parent element name
						String parentName = field.getAnnotation (ConfigurationPropertyWrapper.class).value ();

						// find element(s)
						NodeList parentElements = propertyParent.getElementsByTagNameNS (this.objectMetadata.namespace (), parentName);

						//verify
						if (parentElements.getLength () == 0) throw new ConfigurationLoadException ("Could not find parent element for field " + field.getName () + ".");

						// get element
						parent = ((Element) parentElements.item (0));
					}

					// make field accessible
					field.setAccessible (true);

					// set field value
					try {
						field.set (object, this.GetDataFrom (propertyParent, field.getType (), (field.getAnnotation (ConfigurationProperty.class).value ().isEmpty () ? field.getName () : field.getAnnotation (ConfigurationProperty.class).value ())));
					} catch (ConfigurationLoadException ignore) {
					} catch (IllegalAccessException ex) {
						throw new ConfigurationProcessorException ("Could not set field value of " + type.getCanonicalName () + " -> " + field.getName () + ".", ex);
					}
				}
				return object;
			case STRING:
				// get proper element
				if (childName != null) {
					NodeList elementList = parent.getElementsByTagNameNS (this.objectMetadata.namespace (), childName);

					// verify list
					if (elementList.getLength () == 0) throw new ConfigurationLoadException ("Could not find element " + childName + ".");

					// get correct element
					parent = ((Element) elementList.item (0));
				}

				// get data
				return type.cast (parent.getTextContent ());
		}

		// handle problems
		throw new ConfigurationLoadException ("Could not de-serialized the supplied argument.");
	}

	/**
	 * Loads a configuration file.
	 * @param stream
	 * @return
	 * @throws com.evilco.bukkit.util.plugin.configuration.exception.ConfigurationException
	 */
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

			// create a new instance
			T object = this.GetDataFrom (document.getDocumentElement (), this.objectType, null);

			// return object
			return object;
		} catch (ParserConfigurationException ex) {
			throw new ConfigurationLoadException (ex);
		} catch (SAXException ex) {
			throw new ConfigurationLoadException (ex);
		} catch (IOException ex) {
			throw new ConfigurationLoadException (ex);
		}
	}

	public void Save (T object, OutputStream stream) throws ConfigurationException {
		// create new document builder
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();

		// make namespace aware
		documentBuilderFactory.setNamespaceAware (true);

		// build new document
		DocumentBuilder builder = null;

		try {
			builder = documentBuilderFactory.newDocumentBuilder ();
		} catch (ParserConfigurationException ex) {
			throw new ConfigurationProcessorException (ex);
		}

		Document document = builder.newDocument ();

		// create root element
		Element root = document.createElementNS (this.objectMetadata.namespace (), this.objectMetadata.value ());
		document.appendChild (root);

		// serialize data
		this.SetData (document, root, object, null);
	}

	/**
	 * Serializes objects into XML.
	 * @param document
	 * @param parent
	 * @param object
	 * @param childName
	 * @throws ConfigurationException
	 */
	protected void SetData (Document document, Element parent, Object object, String childName) throws ConfigurationException {
		// guess property value
		ConfigurationPropertyType dataType = ConfigurationPropertyType.fromType (object.getClass ());

		// find correct serialization mode
		switch (dataType) {
			case BOOLEAN:
			case ENUM:
			case FLOAT:
			case DOUBLE:
			case INTEGER:
			case STRING:
				if (childName != null) {
					Element tmp = document.createElementNS (this.objectMetadata.namespace (), childName);
					parent.appendChild (tmp);

					parent = tmp;
				}

				// serialize data
				parent.setTextContent (object.toString ());
				break;
			case LIST:
				List elementList = ((List) object);

				// serialize all elements
				for (Object element : elementList) {
					// create element
					Element elementRepresentation = document.createElementNS (this.objectMetadata.namespace (), childName);

					// serialize data
					this.SetData (document, elementRepresentation, element, null);

					// append element
					parent.appendChild (elementRepresentation);
				}
				break;
			case MAP:
				Map elementMap = ((Map) object);

				// get iterator
				Iterator<Map.Entry> it = elementMap.entrySet ().iterator ();

				// iterate over all objects
				while (it.hasNext ()) {
					// get element
					Map.Entry element = it.next ();

					// create element
					Element elementRepresentation = document.createElementNS (this.objectMetadata.namespace (), childName);

					// set key attribute
					elementRepresentation.setAttributeNS (this.objectMetadata.namespace (), "key", element.getKey ().toString ());

					// set value
					this.SetData (document, elementRepresentation, element.getValue (), null);

					// append element
					parent.appendChild (elementRepresentation);
				}
				break;
			case OBJECT:
				if (childName != null) {
					Element tmp = document.createElementNS (this.objectMetadata.namespace (), childName);
					parent.appendChild (tmp);

					parent = tmp;
				}

				// iterate over fields
				for (Field field : object.getClass ().getFields ()) {
					// check annotations
					if (!field.isAnnotationPresent (ConfigurationProperty.class)) continue;

					// initialize names
					Element fieldParent = parent;

					// make field accessible
					field.setAccessible (true);

					// find parent
					try {
						if (field.isAnnotationPresent (ConfigurationPropertyWrapper.class)) {
							ConfigurationPropertyWrapper propertyWrapper = field.getAnnotation (ConfigurationPropertyWrapper.class);

							// find parent
							NodeList parentList = parent.getElementsByTagNameNS (this.objectMetadata.namespace (), propertyWrapper.value ());

							// create element if needed
							if (parentList.getLength () == 0) {
								fieldParent = document.createElementNS (this.objectMetadata.namespace (), propertyWrapper.value ());
								parent.appendChild (fieldParent);
							} else
								fieldParent = ((Element) parentList.item (0));

							// store data
							this.SetData (document, fieldParent, field.get (object), field.getAnnotation (ConfigurationProperty.class).value ());
						} else {
							// create element
							Element fieldElement = document.createElementNS (this.objectMetadata.namespace (), field.getAnnotation (ConfigurationProperty.class).value ());

							// store data
							this.SetData (document, fieldElement, field.get (object), null);

							// append child
							fieldParent.appendChild (fieldElement);
						}
					} catch (IllegalAccessException ex) {
						throw new ConfigurationProcessorException (ex);
					}
				}
				break;
		}
	}
}
