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
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.collections4.bidimap.UnmodifiableBidiMap;
import org.apache.commons.lang3.StringUtils;



/**
 * Dictionary entry for an attribute that can take any value of a defined enum.
 * Possible enum values may be added by using the addEnumValue() method.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@ToString(callSuper = true)
@Slf4j
public class DictionaryEnumValueAttribute extends DictionarySimpleAttribute {
	private final BidiMap<String, DataItem> enumValues;


	/**
	 * Create a new enum value attribute object that maps the specified XML name to the specified CBOR name.
	 * Both name parameters MUST NOT be null. Addtionally the XML name MUST NOT be
	 * empty or whitespace only. The enum value map is empty after construction.
	 *
	 * @param xmlName Name as used in XML representation
	 * @param cborName Name as used in CBOR representation
	 */
	public DictionaryEnumValueAttribute(String xmlName, DataItem cborName) {
		super(xmlName, cborName);

		this.enumValues = new DualHashBidiMap<>();

		if(log.isTraceEnabled()) {
			log.trace("DictionaryEnumValueAttribute constructed:");
			log.trace(this.toString());
		}
	}


	/**
	 * Create a new enum value attribute object for reverse lookup purposes.
	 * The object created by this constructor may only be used to perform a reverse lookup from
	 * CBOR data item to XML string. The CBOR data item specified MUST exactly match the data item that is
	 * mapped to the XML string. The name parameter MUST NOT be null.
	 *
	 * @param cborName Name as used in CBOR representation
	 */
	DictionaryEnumValueAttribute(DataItem cborName) {
		super(cborName);

		this.enumValues = null;

		if(log.isTraceEnabled()) {
			log.trace("DictionaryEnumValueAttribute for reverse lookup constructed:");
			log.trace(this.toString());
		}
	}


	/**
	 * Lookup the CBOR data item representation for the specified XML enum value.
	 *
	 * @param xmlValue XML value string
	 * @return Associated CBOR data item
	 */
	public DataItem lookupEnumValue(String xmlValue) {
		DataItem rv = this.enumValues.get(xmlValue);

		if(log.isTraceEnabled()) {
			log.trace("Enum value forward lookup performed:");
			log.trace("[S] XML value: " + xmlValue);
			log.trace("[R] CBOR value: " + rv);
		}

		if(rv != null) {
			return DataItemHelper.cloneDataItem(rv);
		}
		else {
			return null;
		}
	}


	/**
	 * Lookup the XML enum value for the specified CBOR data item representation.
	 *
	 * @param cborValue CBOR data item
	 * @return Associated XML value string
	 */
	public String reverseLookupEnumValue(DataItem cborValue) {
		String rv = this.enumValues.getKey(cborValue);

		if(log.isTraceEnabled()) {
			log.trace("Enum value reverse lookup performed:");
			log.trace("[S] CBOR value: " + cborValue);
			log.trace("[R] XML value: " + rv);
		}

		return rv;
	}


	/**
	 * Add a new enum value mapping to this attribute.
	 * Both value parameters MUST NOT be null. Addtionally the XML value MUST NOT be
	 * empty or whitespace only.
	 *
	 * @param xmlValue Value as used in XML representation
	 * @param cborValue Value as used in CBOR representation
	 */
	public void addEnumValue(String xmlValue, DataItem cborValue) {
		if(StringUtils.isBlank(xmlValue)) {
			throw new IllegalArgumentException("XML value must not be blank");
		}

		if(cborValue == null) {
			throw new IllegalArgumentException("CBOR value must not be null");
		}

		String prevKey = this.enumValues.getKey(cborValue);
		DataItem prev = this.enumValues.put(xmlValue, cborValue);

		if(log.isTraceEnabled()) {
			log.trace("Enum value mapped:");
			log.trace("XML value: " + xmlValue);
			log.trace("CBOR value: " + cborValue.toString());
		}

		if(prevKey != null) {
			log.warn("Two enum values with same CBOR mapping: old:" + prevKey + ", new:" + xmlValue);
		}
		if(prev != null) {
			log.warn("Previous enum mapping overridden: XML:" + xmlValue + ", CBOR:" + prev.toString());
		}
	}


	/**
	 * Remove the enum value mapping containing the specified XML value string.
	 *
	 * @param xmlValue XML value string
	 */
	public void removeEnumValue(String xmlValue) {
		this.enumValues.remove(xmlValue);
	}


	/**
	 * Remove the enum value mapping containing the specified CBOR data item.
	 *
	 * @param cborValue CBOR data item
	 */
	public void removeEnumValue(DataItem cborValue) {
		this.enumValues.removeValue(cborValue);
	}


	/**
	 * Return an immutable view of the enum values map for testing purposes.
	 *
	 * @return Immutable map view
	 */
	BidiMap<String, DataItem> getUnmodifiableEnumValues() {
		return UnmodifiableBidiMap.unmodifiableBidiMap(this.enumValues);
	}
}
