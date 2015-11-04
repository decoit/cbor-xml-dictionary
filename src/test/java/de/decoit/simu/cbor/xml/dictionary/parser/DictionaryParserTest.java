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
package de.decoit.simu.cbor.xml.dictionary.parser;

import de.decoit.simu.cbor.xml.dictionary.parser.DictionaryParser;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DoublePrecisionFloat;
import co.nstant.in.cbor.model.NegativeInteger;
import co.nstant.in.cbor.model.SimpleValue;
import co.nstant.in.cbor.model.SimpleValueType;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;
import de.decoit.simu.cbor.xml.dictionary.Dictionary;
import de.decoit.simu.cbor.xml.dictionary.DictionaryComplexElement;
import de.decoit.simu.cbor.xml.dictionary.DictionaryEnumValueAttribute;
import de.decoit.simu.cbor.xml.dictionary.DictionaryEnumValueElement;
import de.decoit.simu.cbor.xml.dictionary.DictionaryNamespace;
import de.decoit.simu.cbor.xml.dictionary.DictionaryProvider;
import de.decoit.simu.cbor.xml.dictionary.DictionarySimpleAttribute;
import de.decoit.simu.cbor.xml.dictionary.DictionarySimpleElement;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class DictionaryParserTest {
	@After
	public void tearDown() {
		// Clear global dictionary instance after every test
		DictionaryProvider.getInstance().clear();
	}


	@Test(expected = IllegalArgumentException.class)
	public void testParseDictionary_null() throws Exception {
		DictionaryParser instance = new DictionaryParser();
		Dictionary result = instance.parseDictionary(null);
	}


	@Test
	public void testParseDictionary_CborTypeUint() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("cbor-type-uint.dict").toURI());
		Dictionary result = instance.parseDictionary(input);

		DictionaryNamespace expResult = new DictionaryNamespace("uint-namespace", new UnsignedInteger(1));
		assertEquals(expResult, result.lookupNamespace("uint-namespace"));
		assertEquals(expResult, result.reverseLookupNamespace(new UnsignedInteger(1)));
	}


	@Test
	public void testParseDictionary_CborTypeNegint() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("cbor-type-negint.dict").toURI());
		Dictionary result = instance.parseDictionary(input);

		DictionaryNamespace expResult = new DictionaryNamespace("negint-namespace", new NegativeInteger(-1));
		assertEquals(expResult, result.lookupNamespace("negint-namespace"));
		assertEquals(expResult, result.reverseLookupNamespace(new NegativeInteger(-1)));
	}


	@Test
	public void testParseDictionary_CborTypeDouble() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("cbor-type-double.dict").toURI());
		Dictionary result = instance.parseDictionary(input);

		DictionaryNamespace expResult = new DictionaryNamespace("double-namespace", new DoublePrecisionFloat(1.11));
		assertEquals(expResult, result.lookupNamespace("double-namespace"));
		assertEquals(expResult, result.reverseLookupNamespace(new DoublePrecisionFloat(1.11)));
	}


	@Test
	public void testParseDictionary_CborTypeBytestr() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("cbor-type-bytestr.dict").toURI());
		Dictionary result = instance.parseDictionary(input);

		DictionaryNamespace expResult = new DictionaryNamespace("bytestr-namespace", new ByteString(new byte[] {(byte)0xAD, (byte)0xFC, (byte)0xB3}));
		assertEquals(expResult, result.lookupNamespace("bytestr-namespace"));
		assertEquals(expResult, result.reverseLookupNamespace(new ByteString(new byte[] {(byte)0xAD, (byte)0xFC, (byte)0xB3})));
	}


	@Test
	public void testParseDictionary_CborTypeUnistr() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("cbor-type-unistr.dict").toURI());
		Dictionary result = instance.parseDictionary(input);

		DictionaryNamespace expResult = new DictionaryNamespace("unistr-namespace", new UnicodeString("my-namespace"));
		assertEquals(expResult, result.lookupNamespace("unistr-namespace"));
		assertEquals(expResult, result.reverseLookupNamespace(new UnicodeString("my-namespace")));
	}


	@Test
	public void testParseDictionary_CborTypeBool() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("cbor-type-bool.dict").toURI());
		Dictionary result = instance.parseDictionary(input);

		DictionaryNamespace expResult = new DictionaryNamespace("bool-namespace", new SimpleValue(SimpleValueType.FALSE));
		assertEquals(expResult, result.lookupNamespace("bool-namespace"));
		assertEquals(expResult, result.reverseLookupNamespace(new SimpleValue(SimpleValueType.FALSE)));
	}


	@Test
	public void testParseDictionary_EmptyNamespaces() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("empty-namespaces.dict").toURI());
		Dictionary result = instance.parseDictionary(input);

		DictionaryNamespace expResult = new DictionaryNamespace("http://www.trustedcomputinggroup.org/2010/IFMAP/2", new UnsignedInteger(1));
		assertEquals(expResult, result.lookupNamespace("http://www.trustedcomputinggroup.org/2010/IFMAP/2"));
		assertEquals(expResult, result.reverseLookupNamespace(new UnsignedInteger(1)));

		expResult = new DictionaryNamespace("http://www.trustedcomputinggroup.org/2010/IFMAPMETADATA/2", new NegativeInteger(-1));
		assertEquals(expResult, result.lookupNamespace("http://www.trustedcomputinggroup.org/2010/IFMAPMETADATA/2"));
		assertEquals(expResult, result.reverseLookupNamespace(new NegativeInteger(-1)));

		expResult = new DictionaryNamespace("http://www.trustedcomputinggroup.org/2012/IFMAPOPERATIONAL-METADATA/1", new UnicodeString("opmeta"));
		assertEquals(expResult, result.lookupNamespace("http://www.trustedcomputinggroup.org/2012/IFMAPOPERATIONAL-METADATA/1"));
		assertEquals(expResult, result.reverseLookupNamespace(new UnicodeString("opmeta")));
	}


	@Test
	public void testParseDictionary_NamespaceWithSimpleElements() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("namespace-with-simple-elements.dict").toURI());
		Dictionary result = instance.parseDictionary(input);

		// Test for correct namespace mapping
		DictionaryNamespace expResultDns = new DictionaryNamespace("http://www.trustedcomputinggroup.org/2010/IFMAP/2", new UnsignedInteger(1));
		assertEquals(expResultDns, result.lookupNamespace("http://www.trustedcomputinggroup.org/2010/IFMAP/2"));
		assertEquals(expResultDns, result.reverseLookupNamespace(new UnsignedInteger(1)));


		// Test for correct "access-request" element mapping
		DictionaryNamespace dns = result.lookupNamespace("http://www.trustedcomputinggroup.org/2010/IFMAP/2");
		DictionarySimpleElement expResultDse = new DictionarySimpleElement("access-request", new UnsignedInteger(0));

		assertEquals(expResultDse, dns.lookupElement("access-request"));
		assertEquals(expResultDse, dns.reverseLookupElement(new UnsignedInteger(0)));


		// Test for correct "access-request"."name" attribute mapping
		DictionarySimpleElement dse = dns.lookupElement("access-request");
		DictionarySimpleAttribute expResultDsa = new DictionarySimpleAttribute("name", new UnsignedInteger(0));

		assertEquals(expResultDsa, dse.lookupAttribute("name"));
		assertEquals(expResultDsa, dse.reverseLookupAttribute(new UnsignedInteger(0)));


		// Test for correct "access-request"."name" attribute enum value mapping
		DictionaryEnumValueAttribute deva = (DictionaryEnumValueAttribute) dse.lookupAttribute("name");

		assertEquals(new UnsignedInteger(0), deva.lookupEnumValue("enumVal1"));
		assertEquals(new UnsignedInteger(1), deva.lookupEnumValue("enumVal2"));
		assertEquals("enumVal1", deva.reverseLookupEnumValue(new UnsignedInteger(0)));
		assertEquals("enumVal2", deva.reverseLookupEnumValue(new UnsignedInteger(1)));


		// Test for correct "access-request"."administrative-domain" attribute mapping
		expResultDsa = new DictionarySimpleAttribute("administrative-domain", new UnsignedInteger(1));

		assertEquals(expResultDsa, dse.lookupAttribute("administrative-domain"));
		assertEquals(expResultDsa, dse.reverseLookupAttribute(new UnsignedInteger(1)));


		// Test for correct "publish" element mapping
		expResultDse = new DictionarySimpleElement("publish", new UnsignedInteger(1));

		assertEquals(expResultDse, dns.lookupElement("publish"));
		assertEquals(expResultDse, dns.reverseLookupElement(new UnsignedInteger(1)));
	}


	@Test
	public void testParseDictionary_NamespaceWithEnumElements() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("namespace-with-enum-elements.dict").toURI());
		Dictionary result = instance.parseDictionary(input);

		// Test for correct namespace mapping
		DictionaryNamespace expResultDns = new DictionaryNamespace("http://www.trustedcomputinggroup.org/2010/IFMAP/2", new UnsignedInteger(1));
		assertEquals(expResultDns, result.lookupNamespace("http://www.trustedcomputinggroup.org/2010/IFMAP/2"));
		assertEquals(expResultDns, result.reverseLookupNamespace(new UnsignedInteger(1)));


		// Test for correct "access-request" element mapping
		DictionaryNamespace dns = result.lookupNamespace("http://www.trustedcomputinggroup.org/2010/IFMAP/2");
		DictionarySimpleElement expResultDse = new DictionarySimpleElement("access-request", new UnsignedInteger(0));

		assertEquals(expResultDse, dns.lookupElement("access-request"));
		assertEquals(expResultDse, dns.reverseLookupElement(new UnsignedInteger(0)));


		// Test for correct "access-request" element enum value mapping
		DictionaryEnumValueElement deve = (DictionaryEnumValueElement) dns.lookupElement("access-request");

		assertEquals(new UnsignedInteger(0), deve.lookupEnumValue("enumVal1"));
		assertEquals(new UnsignedInteger(1), deve.lookupEnumValue("enumVal2"));
		assertEquals("enumVal1", deve.reverseLookupEnumValue(new UnsignedInteger(0)));
		assertEquals("enumVal2", deve.reverseLookupEnumValue(new UnsignedInteger(1)));


		// Test for correct "access-request"."name" attribute mapping
		DictionarySimpleAttribute expResultDsa = new DictionarySimpleAttribute("name", new UnsignedInteger(0));

		assertEquals(expResultDsa, deve.lookupAttribute("name"));
		assertEquals(expResultDsa, deve.reverseLookupAttribute(new UnsignedInteger(0)));


		// Test for correct "access-request"."name" attribute enum value mapping
		DictionaryEnumValueAttribute deva = (DictionaryEnumValueAttribute) deve.lookupAttribute("name");

		assertEquals(new UnsignedInteger(0), deva.lookupEnumValue("enumVal1"));
		assertEquals(new UnsignedInteger(1), deva.lookupEnumValue("enumVal2"));
		assertEquals("enumVal1", deva.reverseLookupEnumValue(new UnsignedInteger(0)));
		assertEquals("enumVal2", deva.reverseLookupEnumValue(new UnsignedInteger(1)));
	}


	@Test
	public void testParseDictionary_NamespaceWithComplexElements() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("namespace-with-complex-elements.dict").toURI());
		Dictionary result = instance.parseDictionary(input);

		// Test for correct namespace mapping
		DictionaryNamespace expResultDns = new DictionaryNamespace("http://www.trustedcomputinggroup.org/2010/IFMAP/2", new UnsignedInteger(1));
		assertEquals(expResultDns, result.lookupNamespace("http://www.trustedcomputinggroup.org/2010/IFMAP/2"));
		assertEquals(expResultDns, result.reverseLookupNamespace(new UnsignedInteger(1)));


		// Test for correct "access-request" element mapping
		DictionaryNamespace dns = result.lookupNamespace("http://www.trustedcomputinggroup.org/2010/IFMAP/2");
		DictionarySimpleElement expResultDse = new DictionarySimpleElement("access-request", new UnsignedInteger(0));

		assertEquals(expResultDse, dns.lookupElement("access-request"));
		assertEquals(expResultDse, dns.reverseLookupElement(new UnsignedInteger(0)));


		// Test for correct "access-request"."name" attribute mapping
		DictionaryComplexElement dce = (DictionaryComplexElement) dns.lookupElement("access-request");
		DictionarySimpleAttribute expResultDsa = new DictionarySimpleAttribute("name", new UnsignedInteger(0));

		assertEquals(expResultDsa, dce.lookupAttribute("name"));
		assertEquals(expResultDsa, dce.reverseLookupAttribute(new UnsignedInteger(0)));


		// Test for correct "access-request"."name" attribute enum value mapping
		DictionaryEnumValueAttribute deva = (DictionaryEnumValueAttribute) dce.lookupAttribute("name");

		assertEquals(new UnsignedInteger(0), deva.lookupEnumValue("enumVal1"));
		assertEquals(new UnsignedInteger(1), deva.lookupEnumValue("enumVal2"));
		assertEquals("enumVal1", deva.reverseLookupEnumValue(new UnsignedInteger(0)));
		assertEquals("enumVal2", deva.reverseLookupEnumValue(new UnsignedInteger(1)));


		// Test for correct "access-request"."nested-element" element mapping
		DictionarySimpleElement expResultNestedDse = new DictionarySimpleElement("nested-element", new UnsignedInteger(0));

		assertEquals(expResultNestedDse, dce.lookupNestedElement("nested-element"));
		assertEquals(expResultNestedDse, dce.reverseLookupNestedElement(new UnsignedInteger(0)));


		// Test for correct "access-request"."nested-element" element enum value mapping
		DictionaryEnumValueElement nestedDeve = (DictionaryEnumValueElement) dce.lookupNestedElement("nested-element");

		assertEquals(new UnsignedInteger(0), nestedDeve.lookupEnumValue("nested-element-enum-val1"));
		assertEquals(new UnsignedInteger(1), nestedDeve.lookupEnumValue("nested-element-enum-val2"));
		assertEquals("nested-element-enum-val1", nestedDeve.reverseLookupEnumValue(new UnsignedInteger(0)));
		assertEquals("nested-element-enum-val2", nestedDeve.reverseLookupEnumValue(new UnsignedInteger(1)));


		// Test for correct "access-request"."nested-element"."nested-element-attribute" attribute mapping
		DictionarySimpleAttribute expResultNestedDsa = new DictionarySimpleAttribute("nested-element-attribute", new UnsignedInteger(0));

		assertEquals(expResultNestedDsa, nestedDeve.lookupAttribute("nested-element-attribute"));
		assertEquals(expResultNestedDsa, nestedDeve.reverseLookupAttribute(new UnsignedInteger(0)));
	}


	@Test(expected = IOException.class)
	public void testParseDictionary_IllegalTopLevelA() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("illegal-toplevel-a.dict").toURI());
		Dictionary result = instance.parseDictionary(input);
	}


	@Test(expected = IOException.class)
	public void testParseDictionary_IllegalTopLevelE() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("illegal-toplevel-e.dict").toURI());
		Dictionary result = instance.parseDictionary(input);
	}


	@Test(expected = IOException.class)
	public void testParseDictionary_IllegalTopLevelT() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("illegal-toplevel-t.dict").toURI());
		Dictionary result = instance.parseDictionary(input);
	}


	@Test(expected = IOException.class)
	public void testParseDictionary_IllegalSecondLevelA() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("illegal-namespace-a.dict").toURI());
		Dictionary result = instance.parseDictionary(input);
	}


	@Test(expected = IOException.class)
	public void testParseDictionary_IllegalSecondLevelE() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("illegal-namespace-e.dict").toURI());
		Dictionary result = instance.parseDictionary(input);
	}


	@Test(expected = IOException.class)
	public void testParseDictionary_IllegalSecondLevelN() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("illegal-namespace-n.dict").toURI());
		Dictionary result = instance.parseDictionary(input);
	}


	@Test(expected = IOException.class)
	public void testParseDictionary_IllegalAttributeA() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("illegal-attribute-a.dict").toURI());
		Dictionary result = instance.parseDictionary(input);
	}


	@Test(expected = IOException.class)
	public void testParseDictionary_IllegalAttributeN() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("illegal-attribute-n.dict").toURI());
		Dictionary result = instance.parseDictionary(input);
	}


	@Test(expected = IOException.class)
	public void testParseDictionary_IllegalAttributeT() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("illegal-attribute-t.dict").toURI());
		Dictionary result = instance.parseDictionary(input);
	}


	@Test(expected = IOException.class)
	public void testParseDictionary_IllegalElementN() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("illegal-element-n.dict").toURI());
		Dictionary result = instance.parseDictionary(input);
	}


	@Test(expected = IOException.class)
	public void testParseDictionary_IllegalEof1() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("illegal-eof-1.dict").toURI());
		Dictionary result = instance.parseDictionary(input);
	}


	@Test(expected = IOException.class)
	public void testParseDictionary_IllegalEof2() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("illegal-eof-2.dict").toURI());
		Dictionary result = instance.parseDictionary(input);
	}


	@Test(expected = IOException.class)
	public void testParseDictionary_IllegalCborType() throws Exception {
		DictionaryParser instance = new DictionaryParser();

		Path input = Paths.get(ClassLoader.getSystemResource("illegal-cbor-type.dict").toURI());
		Dictionary result = instance.parseDictionary(input);
	}
}
