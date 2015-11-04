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
import co.nstant.in.cbor.model.Tag;
import de.decoit.simu.cbor.xml.dictionary.util.DataItemHelper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.collections4.bidimap.UnmodifiableBidiMap;
import org.apache.commons.lang3.StringUtils;



/**
 * This class represents a collection if XML elements and can be mapped to specific XML namespace.
 * Entries may be added to this collection by using the addEntry() method.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@EqualsAndHashCode(of = {"cborName"})
@ToString
@Slf4j
public class DictionaryNamespace {
	private final DataItem cborName;
	@Getter
	private final String xmlName;
	private final BidiMap<String, DictionarySimpleElement> elements;


	/**
	 * Create a new namespace object that maps the specified XML name to the specified CBOR name.
	 * Both name parameters MUST NOT be null. Addtionally the XML name MUST NOT be
	 * empty or whitespace only. The map of elements is empty after construction.
	 *
	 * @param xmlName Name as used in XML representation
	 * @param cborName Name as used in CBOR representation
	 */
	public DictionaryNamespace(String xmlName, DataItem cborName) {
		if(StringUtils.isBlank(xmlName)) {
			throw new IllegalArgumentException("XML name must not be blank");
		}

		if(cborName == null) {
			throw new IllegalArgumentException("CBOR name must not be null");
		}

		this.cborName = cborName;
		this.xmlName = xmlName;
		this.elements = new DualHashBidiMap<>();

		if(log.isTraceEnabled()) {
			log.trace("DictionaryNamespace constructed:");
			log.trace(this.toString());
		}
	}


	/**
	 * Create a new namespace object for reverse lookup purposes.
	 * The object created by this constructor may only be used to perform a reverse lookup from
	 * CBOR data item to XML string. The CBOR data item specified MUST exactly match the data item that is
	 * mapped to the XML string. The name parameter MUST NOT be null.
	 *
	 * @param cborName Name as used in CBOR representation
	 */
	DictionaryNamespace(DataItem cborName) {
		if(cborName == null) {
			throw new IllegalArgumentException("CBOR name must not be null");
		}

		this.cborName = cborName;
		this.xmlName = null;
		this.elements = null;

		if(log.isTraceEnabled()) {
			log.trace("DictionaryNamespace for reverse lookup constructed:");
			log.trace(this.toString());
		}
	}
	
	
	/**
	 * Return a shallow copy of the CBOR name data item.
	 * 
	 * @return CBOR name data item.
	 */
	public DataItem getCborName() {
		return DataItemHelper.cloneDataItem(this.cborName);
	}


	/**
	 * Lookup the CBOR data item representation for the provided element XML name.
	 * The actual CBOR data item may be retrieved by calling getCborName() on the returned
	 * object.
	 *
	 * @param xmlName XML name string
	 * @return Dictionary entry for the specified element
	 */
	public DictionarySimpleElement lookupElement(String xmlName) {
		DictionarySimpleElement rv = this.elements.get(xmlName);

		if(log.isTraceEnabled()) {
			log.trace("Element forward lookup performed:");
			log.trace("[S] XML name: " + xmlName);
			log.trace("[R] Dictionary entry: " + rv);
		}

		return rv;
	}


	/**
	 * Lookup the XML element name for the specified CBOR data item representation.
	 * The actual XML name string may be retrieved by calling getXmlName() on the returned
	 * object.
	 *
	 * @param cborName CBOR data item
	 * @return Dictionary entry for the specified element
	 */
	public DictionarySimpleElement reverseLookupElement(DataItem cborName) {
		if(cborName == null) {
			throw new IllegalArgumentException("CBOR name must not be null");
		}
		
		// Tags make reverse lookup impossible, remove it temporarily
		Tag tmpTag = cborName.getTag();
		cborName.removeTag();
		
		DictionarySimpleElement tmpDsa = new DictionarySimpleElement(cborName);
		String key = this.elements.getKey(tmpDsa);
		
		// The cborName has done its magic, add the tag again
		if(tmpTag != null) {
			cborName.setTag(tmpTag);
		}

		DictionarySimpleElement rv = this.elements.get(key);

		if(log.isTraceEnabled()) {
			log.trace("Element reverse lookup performed:");
			log.trace("[S] CBOR name: " + cborName);
			log.trace("[R] Dictionary entry: " + rv);
		}

		return rv;
	}


	/**
	 * Add a new element to this dictionary namespace.
	 *
	 * @param dse Element object
	 */
	public void addElement(DictionarySimpleElement dse) {
		if(dse == null) {
			throw new IllegalArgumentException("Dictionary element must not be null");
		}

		String prevKey = this.elements.getKey(dse);
		DictionarySimpleElement prev = this.elements.put(dse.getXmlName(), dse);

		if(prevKey != null) {
			log.warn("Two elements with same CBOR mapping: old:" + prevKey + ", new:" + dse.getXmlName());
		}
		if(prev != null) {
			log.warn("Previous element mapping overridden: " + dse.getXmlName() + ", Element:" + prev.toString());
		}
	}


	/**
	 * Remove the element identified by the specified XML name from this dictionary namespace.
	 *
	 * @param xmlName XML name string
	 */
	public void removeElement(String xmlName) {
		this.elements.remove(xmlName);
	}


	/**
	 * Return an immutable view of the elements map for testing purposes.
	 *
	 * @return Immutable map view
	 */
	BidiMap<String, DictionarySimpleElement> getUnmodifiableElements() {
		return UnmodifiableBidiMap.unmodifiableBidiMap(this.elements);
	}
}
