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
import de.decoit.simu.cbor.xml.dictionary.DictionaryComplexElement;
import de.decoit.simu.cbor.xml.dictionary.DictionaryEnumValueElement;
import de.decoit.simu.cbor.xml.dictionary.DictionarySimpleAttribute;
import de.decoit.simu.cbor.xml.dictionary.DictionarySimpleElement;
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
public class DictionarySimpleElementTest {
	private final String xmlName = "xml-element-name";
	private final DataItem cborName = new UnsignedInteger(0);
	private final String attrXmlName = "xml-attribute-name";
	private final DataItem attrCborName = new UnsignedInteger(0);


	@Test
	public void testConstructor_String_DataItem() {
		DictionarySimpleElement instance = new DictionarySimpleElement(xmlName, cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_null_DataItem() {
		DictionarySimpleElement instance = new DictionarySimpleElement(null, cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_EmptyString_DataItem() {
		DictionarySimpleElement instance = new DictionarySimpleElement("", cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_Whitespaces_DataItem() {
		DictionarySimpleElement instance = new DictionarySimpleElement("   ", cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_String_null() {
		DictionarySimpleElement instance = new DictionarySimpleElement(xmlName, null);
	}


	@Test
	public void testConstructor_DataItem() {
		DictionarySimpleElement instance = new DictionarySimpleElement(cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_null() {
		DictionarySimpleElement instance = new DictionarySimpleElement(null);
	}


	@Test
	public void testIsEnumValueElement() {
		DictionarySimpleElement instance = new DictionarySimpleElement(xmlName, cborName);
		assertFalse(instance.isEnumValueElement());
	}


	@Test
	public void testIsComplexElement() {
		DictionarySimpleElement instance = new DictionarySimpleElement(xmlName, cborName);
		assertFalse(instance.isComplexElement());
	}


	@Test
	public void testLookupAttribute() {
		DictionarySimpleAttribute dsa = new DictionarySimpleAttribute(attrXmlName, attrCborName);

		DictionarySimpleElement instance = new DictionarySimpleElement(xmlName, cborName);
		instance.addAttribute(dsa);

		DictionarySimpleAttribute expResult = new DictionarySimpleAttribute(attrXmlName, attrCborName);
		DictionarySimpleAttribute result = instance.lookupAttribute(attrXmlName);

		assertEquals(expResult, result);
	}


	@Test
	public void testLookupAttribute_null() {
		DictionarySimpleElement instance = new DictionarySimpleElement(xmlName, cborName);
		DictionarySimpleAttribute result = instance.lookupAttribute(null);

		assertNull(result);
	}


	@Test
	public void testLookupAttribute_UnknownMapping() {
		DictionarySimpleElement instance = new DictionarySimpleElement(xmlName, cborName);
		DictionarySimpleAttribute result = instance.lookupAttribute("unknown");

		assertNull(result);
	}


	@Test
	public void testReverseLookupAttribute() {
		DictionarySimpleAttribute dsa = new DictionarySimpleAttribute(attrXmlName, attrCborName);

		DictionarySimpleElement instance = new DictionarySimpleElement(xmlName, cborName);
		instance.addAttribute(dsa);

		DictionarySimpleAttribute expResult = new DictionarySimpleAttribute(attrXmlName, attrCborName);
		DictionarySimpleAttribute result = instance.reverseLookupAttribute(attrCborName);

		assertEquals(expResult, result);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testReverseLookupAttribute_null() {
		DictionarySimpleElement instance = new DictionarySimpleElement(xmlName, cborName);
		DictionarySimpleAttribute result = instance.reverseLookupAttribute(null);

		assertNull(result);
	}


	@Test
	public void testReverseLookupAttribute_UnknownMapping() {
		DictionarySimpleElement instance = new DictionarySimpleElement(xmlName, cborName);
		DictionarySimpleAttribute result = instance.reverseLookupAttribute(new UnicodeString("unknown"));

		assertNull(result);
	}


	@Test
	public void testAddAttribute_DictionarySimpleAttribute() {
		DictionarySimpleAttribute dsa = new DictionarySimpleAttribute(attrXmlName, attrCborName);

		DictionarySimpleElement instance = new DictionarySimpleElement(xmlName, cborName);
		instance.addAttribute(dsa);

		BidiMap<String, DictionarySimpleAttribute> result = instance.getUnmodifiableAttributes();
		assertTrue(result.containsKey(attrXmlName));
		assertTrue(result.containsValue(dsa));
		assertEquals(dsa, result.get(attrXmlName));
	}


	@Test
	public void testAddAttribute_DictionaryEnumValueAttribute() {
		DictionaryEnumValueAttribute deva = new DictionaryEnumValueAttribute(attrXmlName, attrCborName);

		DictionarySimpleElement instance = new DictionarySimpleElement(xmlName, cborName);
		instance.addAttribute(deva);

		BidiMap<String, DictionarySimpleAttribute> result = instance.getUnmodifiableAttributes();
		assertTrue(result.containsKey(attrXmlName));
		assertTrue(result.containsValue(deva));
		assertEquals(deva, result.get(attrXmlName));
	}


	@Test(expected = IllegalArgumentException.class)
	public void testAddAttribute_null() {
		DictionarySimpleElement instance = new DictionarySimpleElement(xmlName, cborName);
		instance.addAttribute(null);
	}


	@Test
	public void testRemoveAttribute() {
		DictionaryEnumValueAttribute deva = new DictionaryEnumValueAttribute(attrXmlName, attrCborName);

		DictionarySimpleElement instance = new DictionarySimpleElement(xmlName, cborName);
		instance.addAttribute(deva);

		BidiMap<String, DictionarySimpleAttribute> result = instance.getUnmodifiableAttributes();
		instance.removeAttribute(attrXmlName);

		assertFalse(result.containsKey(attrXmlName));
		assertFalse(result.containsValue(deva));
	}


	@Test
	public void testEquals_true() {
		String localXmlName = "xml-element-name";
		DataItem localCborName = new UnsignedInteger(0);

		DictionarySimpleElement instance1 = new DictionarySimpleElement(xmlName, cborName);
		DictionarySimpleElement instance2 = new DictionarySimpleElement(localXmlName, localCborName);

		assertTrue(instance1.equals(instance2));
		assertTrue(instance2.equals(instance1));
	}


	@Test
	public void testEquals_EnumValueElement_true() {
		String localXmlName = "xml-element-name";
		DataItem localCborName = new UnsignedInteger(0);

		DictionarySimpleElement instance1 = new DictionarySimpleElement(xmlName, cborName);
		DictionaryEnumValueElement instance2 = new DictionaryEnumValueElement(localXmlName, localCborName);

		assertTrue(instance1.equals(instance2));
		assertTrue(instance2.equals(instance1));
	}


	@Test
	public void testEquals_ComplexElement_true() {
		String localXmlName = "xml-element-name";
		DataItem localCborName = new UnsignedInteger(0);

		DictionarySimpleElement instance1 = new DictionarySimpleElement(xmlName, cborName);
		DictionaryComplexElement instance2 = new DictionaryComplexElement(localXmlName, localCborName);

		assertTrue(instance1.equals(instance2));
		assertTrue(instance2.equals(instance1));
	}


	@Test
	public void testEquals_ReverseLookup_true() {
		DataItem localCborName = new UnsignedInteger(0);

		DictionarySimpleElement instance1 = new DictionarySimpleElement(xmlName, cborName);
		DictionarySimpleElement instance2 = new DictionarySimpleElement(localCborName);

		assertTrue(instance1.equals(instance2));
		assertTrue(instance2.equals(instance1));
	}


	@Test
	public void testEquals_false() {
		String localXmlName = "xml-element-name";
		DataItem localCborName = new UnsignedInteger(1);

		DictionarySimpleElement instance1 = new DictionarySimpleElement(xmlName, cborName);
		DictionarySimpleElement instance2 = new DictionarySimpleElement(localXmlName, localCborName);

		assertFalse(instance1.equals(instance2));
	}
	
	
	@Test
	public void testHashCode_true() {
		String localXmlName = "xml-element-name";
		DataItem localCborName = new UnsignedInteger(0);

		DictionarySimpleElement instance1 = new DictionarySimpleElement(xmlName, cborName);
		DictionarySimpleElement instance2 = new DictionarySimpleElement(localXmlName, localCborName);

		assertEquals(instance1.hashCode(), instance2.hashCode());
	}


	@Test
	public void testHashCode_EnumValueElement_true() {
		String localXmlName = "xml-element-name";
		DataItem localCborName = new UnsignedInteger(0);

		DictionarySimpleElement instance1 = new DictionarySimpleElement(xmlName, cborName);
		DictionaryEnumValueElement instance2 = new DictionaryEnumValueElement(localXmlName, localCborName);

		assertEquals(instance1.hashCode(), instance2.hashCode());
	}


	@Test
	public void testHashCode_ComplexElement_true() {
		String localXmlName = "xml-element-name";
		DataItem localCborName = new UnsignedInteger(0);

		DictionarySimpleElement instance1 = new DictionarySimpleElement(xmlName, cborName);
		DictionaryComplexElement instance2 = new DictionaryComplexElement(localXmlName, localCborName);

		assertEquals(instance1.hashCode(), instance2.hashCode());
	}


	@Test
	public void testHashCode_ReverseLookup_true() {
		DataItem localCborName = new UnsignedInteger(0);

		DictionarySimpleElement instance1 = new DictionarySimpleElement(xmlName, cborName);
		DictionarySimpleElement instance2 = new DictionarySimpleElement(localCborName);

		assertEquals(instance1.hashCode(), instance2.hashCode());
	}


	@Test
	public void testHashCode_false() {
		String localXmlName = "xml-element-name";
		DataItem localCborName = new UnsignedInteger(1);

		DictionarySimpleElement instance1 = new DictionarySimpleElement(xmlName, cborName);
		DictionarySimpleElement instance2 = new DictionarySimpleElement(localXmlName, localCborName);

		assertFalse(instance1.hashCode() == instance2.hashCode());
	}



	@Test
	public void testGetCborName() {
		DictionarySimpleElement instance = new DictionarySimpleElement(xmlName, cborName);

		DataItem expResult = new UnsignedInteger(0);
		assertEquals(expResult, instance.getCborName());
	}


	@Test
	public void testGetXmlName() {
		DictionarySimpleElement instance = new DictionarySimpleElement(xmlName, cborName);

		String expResult = "xml-element-name";
		assertEquals(expResult, instance.getXmlName());
	}
}
