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

import de.decoit.simu.cbor.xml.dictionary.DictionaryEnumValueAttribute;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;
import org.apache.commons.collections4.BidiMap;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class DictionaryEnumValueAttributeTest {
	private final String xmlName = "xml-attribute-name";
	private final DataItem cborName = new UnsignedInteger(0);
	private final String enumXmlName1 = "enum-value-1";
	private final DataItem enumCborName1 = new UnsignedInteger(0);


	@Test
	public void testConstructor() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(xmlName, cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_null_DataItem() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(null, cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_EmptyString_DataItem() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute("", cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_Whitespaces_DataItem() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute("   ", cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_String_null() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(xmlName, null);
	}


	@Test
	public void testConstructor_DataItem() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_null() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(null);
	}


	@Test
	public void testIsEnumValueAttribute() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(xmlName, cborName);
		assertTrue(instance.isEnumValueAttribute());
	}


	@Test
	public void testLookupEnumValue() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(xmlName, cborName);
		instance.addEnumValue(enumXmlName1, enumCborName1);

		String search = "enum-value-1";
		DataItem expResult = new UnsignedInteger(0);
		assertEquals(expResult, instance.lookupEnumValue(search));
	}


	@Test
	public void testLookupEnumValue_null() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(xmlName, cborName);
		DataItem result = instance.lookupEnumValue(null);

		assertNull(result);
	}


	@Test
	public void testLookupEnumValue_UnknownMapping() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(xmlName, cborName);
		DataItem result = instance.lookupEnumValue("unknown");

		assertNull(result);
	}


	@Test
	public void testReverseLookupEnumValue() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(xmlName, cborName);
		instance.addEnumValue(enumXmlName1, enumCborName1);

		DataItem search = new UnsignedInteger(0);
		String expResult = "enum-value-1";
		assertEquals(expResult, instance.reverseLookupEnumValue(search));
	}


	@Test
	public void testReverseLookupEnumValue_null() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(xmlName, cborName);
		String result = instance.reverseLookupEnumValue(null);

		assertNull(result);
	}


	@Test
	public void testReverseLookupEnumValue_UnknownMapping() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(xmlName, cborName);
		String result = instance.reverseLookupEnumValue(new UnicodeString("unknown"));

		assertNull(result);
	}


	@Test
	public void testAddEnumValue() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(xmlName, cborName);
		instance.addEnumValue(enumXmlName1, enumCborName1);

		BidiMap<String, DataItem> result = instance.getUnmodifiableEnumValues();
		assertTrue(result.containsKey(enumXmlName1));
		assertTrue(result.containsValue(enumCborName1));
		assertEquals(enumCborName1, result.get(enumXmlName1));
	}


	@Test(expected = IllegalArgumentException.class)
	public void testAddEnumValue_null_DataItem() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(xmlName, cborName);
		instance.addEnumValue(null, enumCborName1);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testAddEnumValue_EmptyString_DataItem() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(xmlName, cborName);
		instance.addEnumValue("", enumCborName1);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testAddEnumValue_Whitespaces_DataItem() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(xmlName, cborName);
		instance.addEnumValue("  ", enumCborName1);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testAddEnumValue_String_null() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(xmlName, cborName);
		instance.addEnumValue(enumXmlName1, null);
	}


	@Test
	public void testRemoveEnumValue_String() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(xmlName, cborName);
		instance.addEnumValue(enumXmlName1, enumCborName1);

		BidiMap<String, DataItem> result = instance.getUnmodifiableEnumValues();
		instance.removeEnumValue(enumXmlName1);

		assertFalse(result.containsKey(enumXmlName1));
		assertFalse(result.containsValue(enumCborName1));
	}


	@Test
	public void testRemoveEnumValue_DataItem() {
		DictionaryEnumValueAttribute instance = new DictionaryEnumValueAttribute(xmlName, cborName);
		instance.addEnumValue(enumXmlName1, enumCborName1);

		BidiMap<String, DataItem> result = instance.getUnmodifiableEnumValues();
		instance.removeEnumValue(enumCborName1);

		assertFalse(result.containsKey(enumXmlName1));
		assertFalse(result.containsValue(enumCborName1));
	}
}
