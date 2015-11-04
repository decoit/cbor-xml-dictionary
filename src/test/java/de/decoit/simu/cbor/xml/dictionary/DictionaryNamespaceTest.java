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

import de.decoit.simu.cbor.xml.dictionary.DictionaryNamespace;
import de.decoit.simu.cbor.xml.dictionary.DictionaryComplexElement;
import de.decoit.simu.cbor.xml.dictionary.DictionaryEnumValueElement;
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
public class DictionaryNamespaceTest {
	private final String xmlName = "xml-namespace-name";
	private final DataItem cborName = new UnsignedInteger(0);
	private final String elementXmlName = "xml-element-name";
	private final DataItem elementCborName = new UnsignedInteger(0);


	@Test
	public void testConstructor() {
		DictionaryNamespace instance = new DictionaryNamespace(xmlName, cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_null_DataItem() {
		DictionaryNamespace instance = new DictionaryNamespace(null, cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_EmptyString_DataItem() {
		DictionaryNamespace instance = new DictionaryNamespace("", cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_Whitespaces_DataItem() {
		DictionaryNamespace instance = new DictionaryNamespace("   ", cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_String_null() {
		DictionaryNamespace instance = new DictionaryNamespace(xmlName, null);
	}


	@Test
	public void testConstructor_DataItem() {
		DictionaryNamespace instance = new DictionaryNamespace(cborName);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_null() {
		DictionaryNamespace instance = new DictionaryNamespace(null);
	}


	@Test
	public void testLookupElement() {
		DictionarySimpleElement dse = new DictionarySimpleElement(elementXmlName, elementCborName);

		DictionaryNamespace instance = new DictionaryNamespace(xmlName, cborName);
		instance.addElement(dse);

		DictionarySimpleElement expResult = new DictionarySimpleElement(elementXmlName, elementCborName);
		DictionarySimpleElement result = instance.lookupElement(elementXmlName);

		assertEquals(expResult, result);
	}


	@Test
	public void testLookupElement_null() {
		DictionaryNamespace instance = new DictionaryNamespace(xmlName, cborName);
		DictionarySimpleElement result = instance.lookupElement(null);

		assertNull(result);
	}


	@Test
	public void testLookupElement_UnknownMapping() {
		DictionaryNamespace instance = new DictionaryNamespace(xmlName, cborName);
		DictionarySimpleElement result = instance.lookupElement("unknown");

		assertNull(result);
	}


	@Test
	public void testReverseLookupElement() {
		DictionarySimpleElement dse = new DictionarySimpleElement(elementXmlName, elementCborName);

		DictionaryNamespace instance = new DictionaryNamespace(xmlName, cborName);
		instance.addElement(dse);

		DictionarySimpleElement expResult = new DictionarySimpleElement(elementXmlName, elementCborName);
		DictionarySimpleElement result = instance.reverseLookupElement(elementCborName);

		assertEquals(expResult, result);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testReverseLookupElement_null() {
		DictionaryNamespace instance = new DictionaryNamespace(xmlName, cborName);
		DictionarySimpleElement result = instance.reverseLookupElement(null);

		assertNull(result);
	}


	@Test
	public void testReverseLookupElement_UnknownMapping() {
		DictionaryNamespace instance = new DictionaryNamespace(xmlName, cborName);
		DictionarySimpleElement result = instance.reverseLookupElement(new UnicodeString("unknown"));

		assertNull(result);
	}


	@Test
	public void testAddElement_DictionarySimpleElement() {
		DictionarySimpleElement dse = new DictionarySimpleElement(elementXmlName, elementCborName);

		DictionaryNamespace instance = new DictionaryNamespace(xmlName, cborName);
		instance.addElement(dse);

		BidiMap<String, DictionarySimpleElement> result = instance.getUnmodifiableElements();
		assertTrue(result.containsKey(elementXmlName));
		assertTrue(result.containsValue(dse));
		assertEquals(dse, result.get(elementXmlName));
	}


	@Test
	public void testAddElement_DictionaryEnumValueElement() {
		DictionaryEnumValueElement deve = new DictionaryEnumValueElement(elementXmlName, elementCborName);

		DictionaryNamespace instance = new DictionaryNamespace(xmlName, cborName);
		instance.addElement(deve);

		BidiMap<String, DictionarySimpleElement> result = instance.getUnmodifiableElements();
		assertTrue(result.containsKey(elementXmlName));
		assertTrue(result.containsValue(deve));
		assertEquals(deve, result.get(elementXmlName));
	}


	@Test
	public void testAddElement_DictionaryComplexElement() {
		DictionaryComplexElement dce = new DictionaryComplexElement(elementXmlName, elementCborName);

		DictionaryNamespace instance = new DictionaryNamespace(xmlName, cborName);
		instance.addElement(dce);

		BidiMap<String, DictionarySimpleElement> result = instance.getUnmodifiableElements();
		assertTrue(result.containsKey(elementXmlName));
		assertTrue(result.containsValue(dce));
		assertEquals(dce, result.get(elementXmlName));
	}


	@Test(expected = IllegalArgumentException.class)
	public void testAddElement_null() {
		DictionaryNamespace instance = new DictionaryNamespace(xmlName, cborName);
		instance.addElement(null);
	}


	@Test
	public void testRemoveElement() {
		DictionarySimpleElement dse = new DictionarySimpleElement(elementXmlName, elementCborName);

		DictionaryNamespace instance = new DictionaryNamespace(xmlName, cborName);
		instance.addElement(dse);

		BidiMap<String, DictionarySimpleElement> result = instance.getUnmodifiableElements();
		instance.removeElement(elementXmlName);

		assertFalse(result.containsKey(elementXmlName));
		assertFalse(result.containsValue(dse));
	}


	@Test
	public void testEquals_true() {
		String localXmlName = "xml-namespace-name";
		DataItem localCborName = new UnsignedInteger(0);

		DictionaryNamespace instance1 = new DictionaryNamespace(xmlName, cborName);
		DictionaryNamespace instance2 = new DictionaryNamespace(localXmlName, localCborName);

		assertTrue(instance1.equals(instance2));
		assertTrue(instance2.equals(instance1));
	}


	@Test
	public void testEquals_ReverseLookup_true() {
		DataItem localCborName = new UnsignedInteger(0);

		DictionaryNamespace instance1 = new DictionaryNamespace(xmlName, cborName);
		DictionaryNamespace instance2 = new DictionaryNamespace(localCborName);

		assertTrue(instance1.equals(instance2));
		assertTrue(instance2.equals(instance1));
	}


	@Test
	public void testEquals_false() {
		String localXmlName = "xml-namespace-name";
		DataItem localCborName = new UnsignedInteger(1);

		DictionaryNamespace instance1 = new DictionaryNamespace(xmlName, cborName);
		DictionaryNamespace instance2 = new DictionaryNamespace(localXmlName, localCborName);

		assertFalse(instance1.equals(instance2));
	}


	@Test
	public void testGetCborName() {
		DictionaryNamespace instance = new DictionaryNamespace(xmlName, cborName);

		DataItem expResult = new UnsignedInteger(0);
		assertEquals(expResult, instance.getCborName());
	}


	@Test
	public void testGetXmlName() {
		DictionaryNamespace instance = new DictionaryNamespace(xmlName, cborName);

		String expResult = "xml-namespace-name";
		assertEquals(expResult, instance.getXmlName());
	}
}
