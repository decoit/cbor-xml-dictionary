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

import de.decoit.simu.cbor.xml.dictionary.DictionaryEnumValueElement;
import de.decoit.simu.cbor.xml.dictionary.DictionaryComplexElement;
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
public class DictionaryComplexElementTest {
	private final String xmlName = "xml-element-name";
	private final DataItem cborName = new UnsignedInteger(0);
	private final String nestedXmlName = "nested-xml-element-name-1";
	private final DataItem nestedCborName = new UnsignedInteger(0);


	@Test
	public void testConstructor() {
		DictionaryComplexElement instance = new DictionaryComplexElement(xmlName, cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_null_DataItem() {
		DictionaryComplexElement instance = new DictionaryComplexElement(null, cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_EmptyString_DataItem() {
		DictionaryComplexElement instance = new DictionaryComplexElement("", cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_Whitespaces_DataItem() {
		DictionaryComplexElement instance = new DictionaryComplexElement("   ", cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_String_null() {
		DictionaryComplexElement instance = new DictionaryComplexElement(xmlName, null);
	}


	@Test
	public void testConstructor_DataItem() {
		DictionaryComplexElement instance = new DictionaryComplexElement(cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_null() {
		DictionaryComplexElement instance = new DictionaryComplexElement(null);
	}


	@Test
	public void testIsEnumValueElement() {
		DictionaryComplexElement instance = new DictionaryComplexElement(xmlName, cborName);
		assertFalse(instance.isEnumValueElement());
	}


	@Test
	public void testIsComplexElement() {
		DictionaryComplexElement instance = new DictionaryComplexElement(xmlName, cborName);
		assertTrue(instance.isComplexElement());
	}


	@Test
	public void testLookupNestedElement() {
		DictionarySimpleElement dse = new DictionarySimpleElement(nestedXmlName, nestedCborName);

		DictionaryComplexElement instance = new DictionaryComplexElement(xmlName, cborName);
		instance.addNestedElement(dse);

		DictionarySimpleElement expResult = new DictionarySimpleElement(nestedXmlName, nestedCborName);
		DictionarySimpleElement result = instance.lookupNestedElement(nestedXmlName);

		assertEquals(expResult, result);
	}


	@Test
	public void testLookupNestedElement_null() {
		DictionaryComplexElement instance = new DictionaryComplexElement(xmlName, cborName);
		DictionarySimpleElement result = instance.lookupNestedElement(null);

		assertNull(result);
	}


	@Test
	public void testLookupNestedElement_UnknownMapping() {
		DictionaryComplexElement instance = new DictionaryComplexElement(xmlName, cborName);
		DictionarySimpleElement result = instance.lookupNestedElement("unknown");

		assertNull(result);
	}


	@Test
	public void testReverseLookupNestedElement() {
		DictionarySimpleElement dse = new DictionarySimpleElement(nestedXmlName, nestedCborName);

		DictionaryComplexElement instance = new DictionaryComplexElement(xmlName, cborName);
		instance.addNestedElement(dse);

		DictionarySimpleElement expResult = new DictionarySimpleElement(nestedXmlName, nestedCborName);
		DictionarySimpleElement result = instance.reverseLookupNestedElement(nestedCborName);

		assertEquals(expResult, result);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testReverseLookupNestedElement_null() {
		DictionaryComplexElement instance = new DictionaryComplexElement(xmlName, cborName);
		DictionarySimpleElement result = instance.reverseLookupNestedElement(null);

		assertNull(result);
	}


	@Test
	public void testReverseLookupNestedElement_UnknownMapping() {
		DictionaryComplexElement instance = new DictionaryComplexElement(xmlName, cborName);
		DictionarySimpleElement result = instance.reverseLookupNestedElement(new UnicodeString("unknown"));

		assertNull(result);
	}


	@Test
	public void testAddNestedElement_DictionarySimpleElement() {
		DictionarySimpleElement dse = new DictionarySimpleElement(nestedXmlName, nestedCborName);

		DictionaryComplexElement instance = new DictionaryComplexElement(xmlName, cborName);
		instance.addNestedElement(dse);

		BidiMap<String, DictionarySimpleElement> result = instance.getUnmodifiableNestedElements();
		assertTrue(result.containsKey(nestedXmlName));
		assertTrue(result.containsValue(dse));
		assertEquals(dse, result.get(nestedXmlName));
	}


	@Test
	public void testAddNestedElement_DictionaryEnumValueElement() {
		DictionaryEnumValueElement deve = new DictionaryEnumValueElement(nestedXmlName, nestedCborName);

		DictionaryComplexElement instance = new DictionaryComplexElement(xmlName, cborName);
		instance.addNestedElement(deve);

		BidiMap<String, DictionarySimpleElement> result = instance.getUnmodifiableNestedElements();
		assertTrue(result.containsKey(nestedXmlName));
		assertTrue(result.containsValue(deve));
		assertEquals(deve, result.get(nestedXmlName));
	}


	@Test
	public void testAddNestedElement_DictionaryComplexElement() {
		DictionaryComplexElement dce = new DictionaryComplexElement(nestedXmlName, nestedCborName);

		DictionaryComplexElement instance = new DictionaryComplexElement(xmlName, cborName);
		instance.addNestedElement(dce);

		BidiMap<String, DictionarySimpleElement> result = instance.getUnmodifiableNestedElements();
		assertTrue(result.containsKey(nestedXmlName));
		assertTrue(result.containsValue(dce));
		assertEquals(dce, result.get(nestedXmlName));
	}


	@Test(expected = IllegalArgumentException.class)
	public void testAddNestedElement_null() {
		DictionaryComplexElement instance = new DictionaryComplexElement(xmlName, cborName);
		instance.addNestedElement(null);
	}


	@Test
	public void testRemoveNestedElement() {
		DictionarySimpleElement dse = new DictionarySimpleElement(nestedXmlName, nestedCborName);

		DictionaryComplexElement instance = new DictionaryComplexElement(xmlName, cborName);
		instance.addNestedElement(dse);

		BidiMap<String, DictionarySimpleElement> result = instance.getUnmodifiableNestedElements();
		instance.removeNestedElement(nestedXmlName);

		assertFalse(result.containsKey(nestedXmlName));
		assertFalse(result.containsValue(dse));
	}
}
