/* 
 * Copyright 2015 DECOIT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.decoit.simu.cbor.xml.dictionary;

import co.nstant.in.cbor.model.DataItem;
import de.decoit.simu.cbor.xml.dictionary.exception.DictionaryPathException;
import de.decoit.simu.cbor.xml.dictionary.parser.DictionaryParser;
import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.collections4.bidimap.UnmodifiableBidiMap;



/**
 * Single instance of a CBOR-XML dictionary.
 * May be loaded with one or more dictionary description files. An object of this class
 * should be obtained using the {@link DictionaryProvider} class, which provides a singleton
 * Dictionary instance.<br>
 * This class provides methods to find a specific element or attribute entry inside this dictionary.
 * The target entry is defined by a dictionary path. The dictionary path describes a path from namespace
 * to the target element or attribute. It looks like these:<br>
 * - &lt;NAMESPACE&gt;ELEMENTNAME<br>
 * - &lt;NAMESPACE&gt;ELEMENTNAME@ATTRIBUTENAME<br>
 * - &lt;NAMESPACE&gt;ELEMENTNAME_1+ELEMENTNAME_2<br>
 * - &lt;NAMESPACE&gt;ELEMENTNAME_1+ELEMENTNAME_2@ATTRIBUTENAME<br>
 * A path defines a single NAMESPACE to start the search at. The namespace is followed by one or more ELEMENTNAMEs separated by plus signs.
 * Such a path will look for the dictionary entry of the last ELEMENTNAME. If an attribute of ELEMENTNAME is the desired target entry instead of the
 * element itself, the path may be suffixed by a single @ATTRIBUTENAME where ATTRIBUTENAME if the XML name of the target attribute.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@ToString
@Slf4j
public class Dictionary {
	private static final Pattern FULL_PATH_PATTERN = Pattern.compile("^\\<([^\\<\\>]+)\\>([a-zA-Z_][\\w-.]*(?:\\+[a-zA-Z_][\\w-.]*)*)(?:@([a-zA-Z_:][-a-zA-Z0-9_:.]*))?$");

	private final BidiMap<String, DictionaryNamespace> namespaces;


	/**
	 * Create a new empty dictionary.
	 * Constructor is package private because a dictionary should be provided by
	 * the {@link DictionaryProvider}.
	 */
	Dictionary() {
		this.namespaces = new DualHashBidiMap<>();

		if(log.isTraceEnabled()) {
			log.trace("Dictionary constructed:");
			log.trace(this.toString());
		}
	}


	/**
	 * Load a dictionary from an input file.
	 * This method extends the existing dictionary, existing mappings will not be removed.
	 * The only exception from that rule is if the input file overrides existing mappings.
	 *
	 * @param inFile File to read the dictionary from
	 * @throws IOException if the file cannot be read
	 */
	public void extendDictionary(Path inFile) throws IOException {
		DictionaryParser dp = new DictionaryParser(this);
		dp.parseDictionary(inFile);
	}


	/**
	 * Load a dictionary from an input file.
	 * This method removes all existing mapping before loading the new ones from the input file.
	 *
	 * @param inFile File to read the dictionary from
	 * @throws IOException if the file cannot be read
	 */
	public void replaceDictionary(Path inFile) throws IOException {
		this.clear();
		this.extendDictionary(inFile);
	}


	/**
	 * Lookup the CBOR data item representation for the provided attribute XML namespace URI.
	 * The actual CBOR data item may be retrieved by calling getCborName() on the returned
	 * object.
	 *
	 * @param xmlName XML name string
	 * @return Dictionary entry for the specified attribute
	 */
	public DictionaryNamespace lookupNamespace(String xmlName) {
		return this.namespaces.get(xmlName);
	}


	/**
	 * Lookup the XML namespace URI for the specified CBOR data item representation.
	 * The actual XML namespace URI may be retrieved by calling getXmlName() on the returned
	 * object.
	 *
	 * @param cborName CBOR data item
	 * @return Dictionary entry for the specified namespace
	 */
	public DictionaryNamespace reverseLookupNamespace(DataItem cborName) {
		DictionaryNamespace tmpDsa = new DictionaryNamespace(cborName);
		String key = this.namespaces.getKey(tmpDsa);

		return this.namespaces.get(key);
	}


	/**
	 * Add a new namespace to this dictionary.
	 *
	 * @param dns Namespace object
	 */
	public void addNamespace(DictionaryNamespace dns) {
		if(dns == null) {
			throw new IllegalArgumentException("Dictionary namespace must not be null");
		}

		String prevKey = this.namespaces.getKey(dns);
		DictionaryNamespace prev = this.namespaces.put(dns.getXmlName(), dns);

		if(prevKey != null) {
			log.warn("Two namespaces with same CBOR mapping: old:" + prevKey + ", new:" + dns.getXmlName());
		}
		if(prev != null) {
			log.warn("Previous namespace mapping overridden: " + dns.getXmlName() + ", Namespace:" + prev.toString());
		}
	}


	/**
	 * Remove the namespace identified by the specified XML name from this dictionary.
	 *
	 * @param xmlName XML name string
	 */
	public void removeNamespace(String xmlName) {
		this.namespaces.remove(xmlName);
	}


	/**
	 * Remove all entries from this dictionary.
	 */
	public void clear() {
		this.namespaces.clear();
	}


	/**
	 * Evaluate a dictionary path and return the dictionary entry for the target element.
	 * If the target element or any element on the path (including namespace) does not exist in the dictionary,
	 * this method will return null.
	 *
	 * @param path Dictionary path to evaluate
	 * @return Dictionary element of target element or null
	 * @throws DictionaryPathException if the provided path cannot be evaluated
	 */
	public DictionarySimpleElement findElementByPath(final String path) throws DictionaryPathException {
		if(path == null) {
			throw new DictionaryPathException("Null reference for dictionary path");
		}

		Matcher m = Dictionary.FULL_PATH_PATTERN.matcher(path);

		// Test if provided path is valid
		if(m.matches()) {
			String namespace = m.group(1);
			String[] elements = m.group(2).split("\\+");

			// Get namespace entry from dictionary
			DictionaryNamespace nsEntry = this.lookupNamespace(namespace);

			// Only continue if namespace entry exists, otherwise return null
			if(nsEntry != null) {
				// Read the first element
				DictionarySimpleElement eEntry = nsEntry.lookupElement(elements[0]);

				// Iterate over the remaining elements
				for(int i=1; i<elements.length; i++) {
					// If previous entry was a complex element, read the next and continue. Otherwise return null.
					if(eEntry instanceof DictionaryComplexElement) {
						DictionaryComplexElement complexEntry = (DictionaryComplexElement) eEntry;

						eEntry = complexEntry.lookupNestedElement(elements[i]);
					}
					else {
						return null;
					}
				}

				// Return the last element entry
				// Will be the target element if it exists in the dictionary. Otherwise it is null.
				return eEntry;
			}
			else {
				return null;
			}
		}
		else {
			throw new DictionaryPathException("Cannot evaluate dictionary path: " + path);
		}
	}


	/**
	 * Evaluate a dictionary path and return the dictionary entry for the target attribute.
	 * If the target attribute or any element on the path (including namespace) does not exist in the dictionary,
	 * this method will return null.
	 *
	 * @param path Dictionary path to evaluate
	 * @return Dictionary element of target attribute or null
	 * @throws DictionaryPathException if the provided path cannot be evaluated
	 */
	public DictionarySimpleAttribute findAttributeByPath(final String path) throws DictionaryPathException {
		DictionarySimpleElement targetElement = this.findElementByPath(path);

		if(targetElement != null) {
			Matcher m = Dictionary.FULL_PATH_PATTERN.matcher(path);

			// Test if provided path is valid (it should be at this point) and fill groups
			if(m.matches()) {
				String attribute = m.group(3);

				if(attribute != null) {
					return targetElement.lookupAttribute(attribute);
				}
				else {
					throw new DictionaryPathException("Path specifies no target attribute: " + path);
				}
			}
		}

		return null;
	}


	/**
	 * Return an immutable view of the namespaces map for testing purposes.
	 *
	 * @return Immutable map view
	 */
	BidiMap<String, DictionaryNamespace> getUnmodifiableNamespaces() {
		return UnmodifiableBidiMap.unmodifiableBidiMap(this.namespaces);
	}
}
