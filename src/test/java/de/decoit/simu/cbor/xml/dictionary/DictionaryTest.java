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
import de.decoit.simu.cbor.xml.dictionary.Dictionary;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;
import de.decoit.simu.cbor.xml.dictionary.exception.DictionaryPathException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.collections4.BidiMap;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class DictionaryTest {
	private final String xmlName = "http://www.trustedcomputinggroup.org/2010/IFMAP/2";
	private final DataItem cborName = new UnsignedInteger(0);


	@Test
	public void testExtendDictionary() throws Exception {
		DictionaryNamespace dns = new DictionaryNamespace("dummy-namespace-uri", new UnsignedInteger(20));
		Path input = Paths.get(ClassLoader.getSystemResource("namespace-with-simple-elements.dict").toURI());

		Dictionary instance = new Dictionary();

		assertTrue(instance.getUnmodifiableNamespaces().isEmpty());

		instance.addNamespace(dns);

		instance.extendDictionary(input);

		assertEquals(dns, instance.lookupNamespace("dummy-namespace-uri"));
		assertNotNull(instance.lookupNamespace("http://www.trustedcomputinggroup.org/2010/IFMAP/2"));
		assertNotNull(instance.lookupNamespace("http://www.trustedcomputinggroup.org/2010/IFMAP/2").lookupElement("access-request"));
	}


	@Test
	public void testReplaceDictionary() throws Exception {
		DictionaryNamespace dns = new DictionaryNamespace("dummy-namespace-uri", new UnsignedInteger(20));
		Path input = Paths.get(ClassLoader.getSystemResource("namespace-with-simple-elements.dict").toURI());

		Dictionary instance = new Dictionary();

		assertTrue(instance.getUnmodifiableNamespaces().isEmpty());

		instance.addNamespace(dns);

		instance.replaceDictionary(input);

		assertNull(instance.lookupNamespace("dummy-namespace-uri"));
		assertNotNull(instance.lookupNamespace("http://www.trustedcomputinggroup.org/2010/IFMAP/2"));
		assertNotNull(instance.lookupNamespace("http://www.trustedcomputinggroup.org/2010/IFMAP/2").lookupElement("access-request"));
	}


	@Test
	public void testLookupNamespace() {
		DictionaryNamespace dns = new DictionaryNamespace(xmlName, cborName);

		Dictionary instance = new Dictionary();
		instance.addNamespace(dns);

		DictionaryNamespace expResult = new DictionaryNamespace(xmlName, cborName);
		DictionaryNamespace result= instance.lookupNamespace(xmlName);

		assertEquals(expResult, result);
	}


	@Test
	public void testLookupNamespace_null() {
		Dictionary instance = new Dictionary();
		DictionaryNamespace result = instance.lookupNamespace(null);

		assertNull(result);
	}


	@Test
	public void testLookupNamespace_UnknownMapping() {
		Dictionary instance = new Dictionary();
		DictionaryNamespace result = instance.lookupNamespace("unknown");

		assertNull(result);
	}


	@Test
	public void testReverseLookupNamespace() {
		DictionaryNamespace dns = new DictionaryNamespace(xmlName, cborName);

		Dictionary instance = new Dictionary();
		instance.addNamespace(dns);

		DictionaryNamespace expResult = new DictionaryNamespace(xmlName, cborName);
		DictionaryNamespace result= instance.reverseLookupNamespace(cborName);

		assertEquals(expResult, result);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testReverseLookupNamespace_null() {
		Dictionary instance = new Dictionary();
		DictionaryNamespace result = instance.reverseLookupNamespace(null);

		assertNull(result);
	}


	@Test
	public void testReverseLookupNamespace_UnknownMapping() {
		Dictionary instance = new Dictionary();
		DictionaryNamespace result = instance.reverseLookupNamespace(new UnicodeString("unknown"));

		assertNull(result);
	}


	@Test
	public void testAddNamespace() {
		DictionaryNamespace dns = new DictionaryNamespace(xmlName, cborName);

		Dictionary instance = new Dictionary();
		instance.addNamespace(dns);

		BidiMap<String, DictionaryNamespace> result = instance.getUnmodifiableNamespaces();
		assertTrue(result.containsKey(xmlName));
		assertTrue(result.containsValue(dns));
		assertEquals(dns, result.get(xmlName));
	}


	@Test(expected = IllegalArgumentException.class)
	public void testAddNamespace_null() {
		Dictionary instance = new Dictionary();
		instance.addNamespace(null);
	}


	@Test
	public void testRemoveNamespace() {
		DictionaryNamespace dns = new DictionaryNamespace(xmlName, cborName);

		Dictionary instance = new Dictionary();
		instance.addNamespace(dns);

		BidiMap<String, DictionaryNamespace> result = instance.getUnmodifiableNamespaces();
		instance.removeNamespace(xmlName);

		assertFalse(result.containsKey(xmlName));
		assertFalse(result.containsValue(dns));
	}


	@Test
	public void testClear() {
		DictionaryNamespace dns = new DictionaryNamespace(xmlName, cborName);

		Dictionary instance = new Dictionary();
		instance.addNamespace(dns);

		BidiMap<String, DictionaryNamespace> result = instance.getUnmodifiableNamespaces();
		instance.clear();

		assertTrue(result.isEmpty());
	}


	@Test
	public void testFindElementByPath() throws Exception {
		Path input = Paths.get(ClassLoader.getSystemResource("namespace-with-complex-elements.dict").toURI());

		Dictionary instance = new Dictionary();
		instance.extendDictionary(input);

		String dictPath = "<http://www.trustedcomputinggroup.org/2010/IFMAP/2>access-request+nested-element";

		DictionarySimpleElement result = instance.findElementByPath(dictPath);

		assertNotNull(result);
	}


	@Test
	public void testFindElementByPath_NotExistentNamespace() throws Exception {
		Path input = Paths.get(ClassLoader.getSystemResource("namespace-with-complex-elements.dict").toURI());

		Dictionary instance = new Dictionary();
		instance.extendDictionary(input);

		String dictPath = "<this-is-my-namespace>access-request+nested-element";

		DictionarySimpleElement result = instance.findElementByPath(dictPath);

		assertNull(result);
	}


	@Test
	public void testFindElementByPath_NotExistentElement() throws Exception {
		Path input = Paths.get(ClassLoader.getSystemResource("namespace-with-complex-elements.dict").toURI());

		Dictionary instance = new Dictionary();
		instance.extendDictionary(input);

		String dictPath = "<http://www.trustedcomputinggroup.org/2010/IFMAP/2>access-request+nested-element-dummy";

		DictionarySimpleElement result = instance.findElementByPath(dictPath);

		assertNull(result);
	}


	@Test(expected = DictionaryPathException.class)
	public void testFindElementByPath_null() throws Exception {
		Dictionary instance = new Dictionary();

		DictionarySimpleElement result = instance.findElementByPath(null);
	}


	@Test(expected = DictionaryPathException.class)
	public void testFindElementByPath_EmptyString() throws Exception {
		Dictionary instance = new Dictionary();

		DictionarySimpleElement result = instance.findElementByPath("");
	}


	@Test(expected = DictionaryPathException.class)
	public void testFindElementByPath_Whitespaces() throws Exception {
		Dictionary instance = new Dictionary();

		DictionarySimpleElement result = instance.findElementByPath("   ");
	}


	@Test(expected = DictionaryPathException.class)
	public void testFindElementByPath_MalformedPath() throws Exception {
		Dictionary instance = new Dictionary();

		String dictPath = "<http://www.trustedcomputinggroup.org/2010/IFMAP/2>@myattr";

		DictionarySimpleElement result = instance.findElementByPath(dictPath);
	}


	@Test
	public void testFindAttributeByPath() throws Exception {
		Path input = Paths.get(ClassLoader.getSystemResource("namespace-with-complex-elements.dict").toURI());

		Dictionary instance = new Dictionary();
		instance.extendDictionary(input);

		String dictPath = "<http://www.trustedcomputinggroup.org/2010/IFMAP/2>access-request@name";

		DictionarySimpleAttribute result = instance.findAttributeByPath(dictPath);

		assertNotNull(result);
	}


	@Test
	public void testFindAttributeByPath_NotExistentNamespace() throws Exception {
		Path input = Paths.get(ClassLoader.getSystemResource("namespace-with-complex-elements.dict").toURI());

		Dictionary instance = new Dictionary();
		instance.extendDictionary(input);

		String dictPath = "<this-is-my-namespace>access-request@name";

		DictionarySimpleAttribute result = instance.findAttributeByPath(dictPath);

		assertNull(result);
	}


	@Test
	public void testFindAttributeByPath_NotExistentElement() throws Exception {
		Path input = Paths.get(ClassLoader.getSystemResource("namespace-with-complex-elements.dict").toURI());

		Dictionary instance = new Dictionary();
		instance.extendDictionary(input);

		String dictPath = "<http://www.trustedcomputinggroup.org/2010/IFMAP/2>access@name";

		DictionarySimpleAttribute result = instance.findAttributeByPath(dictPath);

		assertNull(result);
	}


	@Test
	public void testFindAttributeByPath_NotExistentAttribute() throws Exception {
		Path input = Paths.get(ClassLoader.getSystemResource("namespace-with-complex-elements.dict").toURI());

		Dictionary instance = new Dictionary();
		instance.extendDictionary(input);

		String dictPath = "<http://www.trustedcomputinggroup.org/2010/IFMAP/2>access@myattr";

		DictionarySimpleAttribute result = instance.findAttributeByPath(dictPath);

		assertNull(result);
	}


	@Test(expected = DictionaryPathException.class)
	public void testFindAttributeByPath_null() throws Exception {
		Dictionary instance = new Dictionary();

		DictionarySimpleAttribute result = instance.findAttributeByPath(null);
	}


	@Test(expected = DictionaryPathException.class)
	public void testFindAttributeByPath_EmptyString() throws Exception {
		Dictionary instance = new Dictionary();

		DictionarySimpleAttribute result = instance.findAttributeByPath("");
	}


	@Test(expected = DictionaryPathException.class)
	public void testFindAttributeByPath_Whitespaces() throws Exception {
		Dictionary instance = new Dictionary();

		DictionarySimpleAttribute result = instance.findAttributeByPath("   ");
	}


	@Test(expected = DictionaryPathException.class)
	public void testFindAttributeByPath_MalformedPath() throws Exception {
		Dictionary instance = new Dictionary();

		String dictPath = "<http://www.trustedcomputinggroup.org/2010/IFMAP/2>@myattr";

		DictionarySimpleAttribute result = instance.findAttributeByPath(dictPath);
	}


	@Test(expected = DictionaryPathException.class)
	public void testFindAttributeByPath_MissingAttribute() throws Exception {
		Path input = Paths.get(ClassLoader.getSystemResource("namespace-with-complex-elements.dict").toURI());

		Dictionary instance = new Dictionary();
		instance.extendDictionary(input);

		String dictPath = "<http://www.trustedcomputinggroup.org/2010/IFMAP/2>access-request+nested-element";

		DictionarySimpleAttribute result = instance.findAttributeByPath(dictPath);
	}
}
