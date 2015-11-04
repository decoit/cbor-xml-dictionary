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
 * Dictionary entry for a simple XML element that may have attributes but no enum value or nested elements.
 * Attributes may be added by using the addAttribute() method.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@EqualsAndHashCode(of = {"cborName"})
@ToString
@Slf4j
public class DictionarySimpleElement {
	private final DataItem cborName;
	@Getter
	private final String xmlName;
	private final BidiMap<String, DictionarySimpleAttribute> attributes;


	/**
	 * Create a new simple element object that maps the specified XML name to the specified CBOR name.
	 * Both name parameters MUST NOT be null. Addtionally the XML name MUST NOT be
	 * empty or whitespace only. The map of attributes is empty after construction.
	 *
	 * @param xmlName Name as used in XML representation
	 * @param cborName Name as used in CBOR representation
	 */
	public DictionarySimpleElement(String xmlName, DataItem cborName) {
		if(StringUtils.isBlank(xmlName)) {
			throw new IllegalArgumentException("XML name must not be blank");
		}

		if(cborName == null) {
			throw new IllegalArgumentException("CBOR name must not be null");
		}

		this.cborName = cborName;
		this.xmlName = xmlName;
		this.attributes = new DualHashBidiMap<>();

		if(log.isTraceEnabled()) {
			log.trace("DictionarySimpleElement constructed:");
			log.trace(this.toString());
		}
	}


	/**
	 * Create a new simple element object for reverse lookup purposes.
	 * The object created by this constructor may only be used to perform a reverse lookup from
	 * CBOR data item to XML string. The CBOR data item specified MUST exactly match the data item that is
	 * mapped to the XML string. The name parameter MUST NOT be null.
	 *
	 * @param cborName Name as used in CBOR representation
	 */
	DictionarySimpleElement(DataItem cborName) {
		if(cborName == null) {
			throw new IllegalArgumentException("CBOR name must not be null");
		}

		this.cborName = cborName;
		this.xmlName = null;
		this.attributes = null;

		if(log.isTraceEnabled()) {
			log.trace("DictionarySimpleElement for reverse lookup constructed:");
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
	 * Test if this object is an instance of {@link DictionaryEnumValueElement}.
	 *
	 * @return Result of (this instanceof DictionaryEnumValueElement)
	 */
	public boolean isEnumValueElement() {
		return (this instanceof DictionaryEnumValueElement);
	}


	/**
	 * Test if this object is an instance of {@link DictionaryComplexElement}.
	 *
	 * @return Result of (this instanceof DictionaryComplexElement)
	 */
	public boolean isComplexElement() {
		return (this instanceof DictionaryComplexElement);
	}


	/**
	 * Lookup the CBOR data item representation for the provided attribute XML name.
	 * The actual CBOR data item may be retrieved by calling getCborName() on the returned
	 * object.
	 *
	 * @param xmlName XML name string
	 * @return Dictionary entry for the specified attribute
	 */
	public DictionarySimpleAttribute lookupAttribute(String xmlName) {
		DictionarySimpleAttribute rv = this.attributes.get(xmlName);

		if(log.isTraceEnabled()) {
			log.trace("Attribute forward lookup performed:");
			log.trace("[S] XML name: " + xmlName);
			log.trace("[R] Dictionary entry: " + rv);
		}

		return rv;
	}


	/**
	 * Lookup the XML attribute name for the specified CBOR data item representation.
	 * The actual XML name string may be retrieved by calling getXmlName() on the returned
	 * object.
	 *
	 * @param cborName CBOR data item
	 * @return Dictionary entry for the specified attribute
	 */
	public DictionarySimpleAttribute reverseLookupAttribute(DataItem cborName) {
		DictionarySimpleAttribute tmpDsa = new DictionarySimpleAttribute(cborName);
		String key = this.attributes.getKey(tmpDsa);

		DictionarySimpleAttribute rv = this.attributes.get(key);

		if(log.isTraceEnabled()) {
			log.trace("Attribute reverse lookup performed:");
			log.trace("[S] CBOR name: " + cborName);
			log.trace("[R] Dictionary entry: " + rv);
		}

		return rv;
	}


	/**
	 * Add a new attribute to this dictionary element.
	 *
	 * @param dsa Attribute object
	 */
	public void addAttribute(DictionarySimpleAttribute dsa) {
		if(dsa == null) {
			throw new IllegalArgumentException("Dictionary attribute must not be null");
		}

		String prevKey = this.attributes.getKey(dsa);
		DictionarySimpleAttribute prev = this.attributes.put(dsa.getXmlName(), dsa);

		if(prevKey != null) {
			log.warn("Two attributes with same CBOR mapping: old:" + prevKey + ", new:" + dsa.getXmlName());
		}
		if(prev != null) {
			log.warn("Previous attribute mapping overridden: " + dsa.getXmlName() + ", Attribute:" + prev.toString());
		}
	}


	/**
	 * Remove the attribute identified by the specified XML name from this dictionary element.
	 *
	 * @param xmlName XML name string
	 */
	public void removeAttribute(String xmlName) {
		this.attributes.remove(xmlName);
	}


	/**
	 * Return an immutable view of the attributes map for testing purposes.
	 *
	 * @return Immutable map view
	 */
	BidiMap<String, DictionarySimpleAttribute> getUnmodifiableAttributes() {
		return UnmodifiableBidiMap.unmodifiableBidiMap(this.attributes);
	}
}
