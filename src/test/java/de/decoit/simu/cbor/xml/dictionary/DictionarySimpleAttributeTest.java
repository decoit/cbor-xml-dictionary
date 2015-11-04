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
import de.decoit.simu.cbor.xml.dictionary.DictionarySimpleAttribute;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.UnsignedInteger;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class DictionarySimpleAttributeTest {
	private final String xmlName = "xml-attribute-name";
	private final DataItem cborName = new UnsignedInteger(0);


	@Test
	public void testConstructor() {
		DictionarySimpleAttribute instance = new DictionarySimpleAttribute(xmlName, cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_null_DataItem() {
		DictionarySimpleAttribute instance = new DictionarySimpleAttribute(null, cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_EmptyString_DataItem() {
		DictionarySimpleAttribute instance = new DictionarySimpleAttribute("", cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_Whitespaces_DataItem() {
		DictionarySimpleAttribute instance = new DictionarySimpleAttribute("   ", cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_String_null() {
		DictionarySimpleAttribute instance = new DictionarySimpleAttribute(xmlName, null);
	}


	@Test
	public void testConstructor_DataItem() {
		DictionarySimpleAttribute instance = new DictionarySimpleAttribute(cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_null() {
		DictionarySimpleAttribute instance = new DictionarySimpleAttribute(null);
	}


	@Test
	public void testIsEnumValueAttribute() {
		DictionarySimpleAttribute instance = new DictionarySimpleAttribute(xmlName, cborName);
		assertFalse(instance.isEnumValueAttribute());
	}


	@Test
	public void testGetCborName() {
		DictionarySimpleAttribute instance = new DictionarySimpleAttribute(xmlName, cborName);

		DataItem expCborName = new UnsignedInteger(0);
		assertEquals(expCborName, instance.getCborName());
	}


	@Test
	public void testGetXmlName() {
		DictionarySimpleAttribute instance = new DictionarySimpleAttribute(xmlName, cborName);

		String expXmlName = "xml-attribute-name";
		assertEquals(expXmlName, instance.getXmlName());
	}


	@Test
	public void testEquals_true() {
		String localXmlName = "xml-attribute-name";
		DataItem localCborName = new UnsignedInteger(0);

		DictionarySimpleAttribute instance1 = new DictionarySimpleAttribute(xmlName, cborName);
		DictionarySimpleAttribute instance2 = new DictionarySimpleAttribute(localXmlName, localCborName);

		assertTrue(instance1.equals(instance2));
		assertTrue(instance2.equals(instance1));
	}


	@Test
	public void testEquals_EnumValueAttribute_true() {
		String localXmlName = "xml-attribute-name";
		DataItem localCborName = new UnsignedInteger(0);

		DictionarySimpleAttribute instance1 = new DictionarySimpleAttribute(xmlName, cborName);
		DictionaryEnumValueAttribute instance2 = new DictionaryEnumValueAttribute(localXmlName, localCborName);

		assertTrue(instance1.equals(instance2));
		assertTrue(instance2.equals(instance1));
	}


	@Test
	public void testEquals_ReverseLookup_true() {
		DataItem localCborName = new UnsignedInteger(0);

		DictionarySimpleAttribute instance1 = new DictionarySimpleAttribute(xmlName, cborName);
		DictionarySimpleAttribute instance2 = new DictionarySimpleAttribute(localCborName);

		assertTrue(instance1.equals(instance2));
		assertTrue(instance2.equals(instance1));
	}


	@Test
	public void testEquals_false() {
		String localXmlName = "xml-attribute-name";
		DataItem localCborName = new UnsignedInteger(1);

		DictionarySimpleAttribute instance1 = new DictionarySimpleAttribute(xmlName, cborName);
		DictionarySimpleAttribute instance2 = new DictionarySimpleAttribute(localXmlName, localCborName);

		assertFalse(instance1.equals(instance2));
		assertFalse(instance2.equals(instance1));
	}
}
