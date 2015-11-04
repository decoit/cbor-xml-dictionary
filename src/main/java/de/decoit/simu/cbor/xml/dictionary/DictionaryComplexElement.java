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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.collections4.bidimap.UnmodifiableBidiMap;



/**
 * Dictionary entry for a complex XML element that can have nested elements besides its attributes.
 * Attributes may be added by using the addAttribute() method. Possible nested elements may be
 * added by using the addNestedElement() method.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Slf4j
public class DictionaryComplexElement extends DictionarySimpleElement {
	private final BidiMap<String, DictionarySimpleElement> nestedElements;


	/**
	 * Create a new complex element object that maps the specified XML name to the specified CBOR name.
	 * Both name parameters MUST NOT be null. Addtionally the XML name MUST NOT be
	 * empty or whitespace only. The attribute and nested element maps are empty after construction.
	 *
	 * @param xmlName Name as used in XML representation
	 * @param cborName Name as used in CBOR representation
	 */
	public DictionaryComplexElement(String xmlName, DataItem cborName) {
		super(xmlName, cborName);

		this.nestedElements = new DualHashBidiMap<>();

		if(log.isTraceEnabled()) {
			log.trace("DictionaryComplexElement constructed:");
			log.trace(this.toString());
		}
	}


	/**
	 * Create a new complex element object for reverse lookup purposes.
	 * The object created by this constructor may only be used to perform a reverse lookup from
	 * CBOR data item to XML string. The CBOR data item specified MUST exactly match the data item that is
	 * mapped to the XML string. The name parameter MUST NOT be null.
	 *
	 * @param cborName Name as used in CBOR representation
	 */
	DictionaryComplexElement(DataItem cborName) {
		super(cborName);

		this.nestedElements = null;

		if(log.isTraceEnabled()) {
			log.trace("DictionaryComplexElement for reverse lookup constructed:");
			log.trace(this.toString());
		}
	}


	/**
	 * Lookup the CBOR data item representation for the provided nested element XML name.
	 * The actual CBOR data item may be retrieved by calling getCborName() on the returned
	 * object.
	 *
	 * @param xmlName XML name string
	 * @return Dictionary entry for the specified nested element
	 */
	public DictionarySimpleElement lookupNestedElement(String xmlName) {
		DictionarySimpleElement rv = this.nestedElements.get(xmlName);

		if(log.isTraceEnabled()) {
			log.trace("Nested element forward lookup performed:");
			log.trace("[S] XML name: " + xmlName);
			log.trace("[R] Dictionary entry: " + rv);
		}

		return rv;
	}


	/**
	 * Lookup the XML nested element name for the specified CBOR data item representation.
	 * The actual XML name string may be retrieved by calling getXmlName() on the returned
	 * object.
	 *
	 * @param cborName CBOR data item
	 * @return Dictionary entry for the specified nested element
	 */
	public DictionarySimpleElement reverseLookupNestedElement(DataItem cborName) {
		DictionarySimpleElement tmpDse = new DictionarySimpleElement(cborName);
		String key = this.nestedElements.getKey(tmpDse);

		DictionarySimpleElement rv = this.nestedElements.get(key);

		if(log.isTraceEnabled()) {
			log.trace("Nested element reverse lookup performed:");
			log.trace("[S] CBOR name: " + cborName);
			log.trace("[R] Dictionary entry: " + rv);
		}

		return rv;
	}


	/**
	 * Add a new nested element to this dictionary element.
	 *
	 * @param dse Nested element object
	 */
	public void addNestedElement(DictionarySimpleElement dse) {
		if(dse == null) {
			throw new IllegalArgumentException("Dictionary element must not be null");
		}

		String prevKey = this.nestedElements.getKey(dse);
		DictionarySimpleElement prev = this.nestedElements.put(dse.getXmlName(), dse);

		if(prevKey != null) {
			log.warn("Two nested elements with same CBOR mapping: old:" + prevKey + ", new:" + dse.getXmlName());
		}
		if(prev != null) {
			log.warn("Previous nested element mapping overridden: " + dse.getXmlName() + ", NestedElement:" + prev.toString());
		}
	}


	/**
	 * Remove the nested element identified by the specified XML name from this dictionary element.
	 *
	 * @param xmlName XML name string
	 */
	public void removeNestedElement(String xmlName) {
		this.nestedElements.remove(xmlName);
	}


	/**
	 * Return an immutable view of the nested elements map for testing purposes.
	 *
	 * @return Immutable map view
	 */
	BidiMap<String, DictionarySimpleElement> getUnmodifiableNestedElements() {
		return UnmodifiableBidiMap.unmodifiableBidiMap(this.nestedElements);
	}
}
