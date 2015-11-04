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
import org.apache.commons.lang3.StringUtils;



/**
 * Dictionary entry for a simple attribute that can take any value.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@EqualsAndHashCode(of = {"cborName"})
@ToString
@Slf4j
public class DictionarySimpleAttribute {
	private final DataItem cborName;
	@Getter
	private final String xmlName;


	/**
	 * Create a new simple attribute object that maps the specified XML name to the specified CBOR name.
	 * Both name parameters MUST NOT be null. Addtionally the XML name MUST NOT be
	 * empty or whitespace only.
	 *
	 * @param xmlName Name as used in XML representation
	 * @param cborName Name as used in CBOR representation
	 */
	public DictionarySimpleAttribute(String xmlName, DataItem cborName) {
		if(StringUtils.isBlank(xmlName)) {
			throw new IllegalArgumentException("XML name must not be blank");
		}

		if(cborName == null) {
			throw new IllegalArgumentException("CBOR name must not be null");
		}

		this.cborName = cborName;
		this.xmlName = xmlName;

		if(log.isTraceEnabled()) {
			log.trace("DictionarySimpleAttribute constructed:");
			log.trace(this.toString());
		}
	}


	/**
	 * Create a new simple attribute object for reverse lookup purposes.
	 * The object created by this constructor may only be used to perform a reverse lookup from
	 * CBOR data item to XML string. The CBOR data item specified MUST exactly match the data item that is
	 * mapped to the XML string. The name parameter MUST NOT be null.
	 *
	 * @param cborName Name as used in CBOR representation
	 */
	DictionarySimpleAttribute(DataItem cborName) {
		if(cborName == null) {
			throw new IllegalArgumentException("CBOR name must not be null");
		}

		this.cborName = cborName;
		this.xmlName = null;

		if(log.isTraceEnabled()) {
			log.trace("DictionarySimpleAttribute for reverse lookup constructed:");
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
	 * Test if this object is an instance of {@link DictionaryEnumValueAttribute}.
	 *
	 * @return Result of (this instanceof DictionaryEnumValueAttribute)
	 */
	public boolean isEnumValueAttribute() {
		return (this instanceof DictionaryEnumValueAttribute);
	}
}
