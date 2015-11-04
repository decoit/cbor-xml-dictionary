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

import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class DictionaryProviderTest {
	@Test
	public void testGetInstance() {
		Dictionary result = DictionaryProvider.getInstance();
		assertNotNull(result);
	}
	
	
	@Test
	public void testGetNamedInstance() {
		Dictionary result = DictionaryProvider.getNamedInstance("get-named");
		assertNotNull(result);
	}
	
	
	@Test
	public void testGetNamedInstance_Equal() {
		Dictionary result = DictionaryProvider.getNamedInstance("get-named-equal");
		Dictionary reusedResult = DictionaryProvider.getNamedInstance("get-named-equal");
		
		assertNotNull(result);
		assertNotNull(reusedResult);
		assertSame(result, reusedResult);
	}
	
	
	@Test
	public void testGetNamedInstance_Different() {
		Dictionary result = DictionaryProvider.getNamedInstance("get-named-different-1");
		Dictionary reusedResult = DictionaryProvider.getNamedInstance("get-named-different-2");
		
		assertNotNull(result);
		assertNotNull(reusedResult);
		assertNotSame(result, reusedResult);
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetNamedInstance_null() {
		Dictionary result = DictionaryProvider.getNamedInstance(null);
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetNamedInstance_EmptyString() {
		Dictionary result = DictionaryProvider.getNamedInstance("");
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetNamedInstance_Whitespaces() {
		Dictionary result = DictionaryProvider.getNamedInstance("   ");
	}
	
	
	@Test
	public void testRemoveNamedInstance() {
		Dictionary result = DictionaryProvider.getNamedInstance("remove-named");
		assertTrue(DictionaryProvider.hasNamedInstance("remove-named"));
		
		DictionaryProvider.removeNamedInstance("remove-named");
		assertFalse(DictionaryProvider.hasNamedInstance("remove-named"));
	}
}
