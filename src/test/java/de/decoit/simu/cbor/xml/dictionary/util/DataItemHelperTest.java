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
package de.decoit.simu.cbor.xml.dictionary.util;

import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.DoublePrecisionFloat;
import co.nstant.in.cbor.model.NegativeInteger;
import co.nstant.in.cbor.model.SimpleValue;
import co.nstant.in.cbor.model.SimpleValueType;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class DataItemHelperTest {
	@Test
	public void testCloneDataItem_UnsignedInteger() {
		UnsignedInteger input = new UnsignedInteger(42L);
		
		DataItem result = DataItemHelper.cloneDataItem(input);
		
		assertNotSame(input, result);
		assertEquals(input, result);
	}
	
	
	@Test
	public void testCloneDataItem_UnsignedInteger_tagged() {
		UnsignedInteger input = new UnsignedInteger(42L);
		input.setTag(21L);
		
		DataItem result = DataItemHelper.cloneDataItem(input);
		
		assertNotSame(input, result);
		assertEquals(input, result);
	}
	
	
	@Test
	public void testCloneDataItem_NegativeInteger() {
		NegativeInteger input = new NegativeInteger(-42L);
		
		DataItem result = DataItemHelper.cloneDataItem(input);
		
		assertNotSame(input, result);
		assertEquals(input, result);
	}
	
	
	@Test
	public void testCloneDataItem_NegativeInteger_tagged() {
		NegativeInteger input = new NegativeInteger(-42L);
		input.setTag(21L);
		
		DataItem result = DataItemHelper.cloneDataItem(input);
		
		assertNotSame(input, result);
		assertEquals(input, result);
	}
	
	
	@Test
	public void testCloneDataItem_DoublePrecisionFloat() {
		DoublePrecisionFloat input = new DoublePrecisionFloat(42.0);
		
		DataItem result = DataItemHelper.cloneDataItem(input);
		
		assertNotSame(input, result);
		assertEquals(input, result);
	}
	
	
	@Test
	public void testCloneDataItem_DoublePrecisionFloat_tagged() {
		DoublePrecisionFloat input = new DoublePrecisionFloat(42.0);
		input.setTag(21L);
		
		DataItem result = DataItemHelper.cloneDataItem(input);
		
		assertNotSame(input, result);
		assertEquals(input, result);
	}
	
	
	@Test
	public void testCloneDataItem_ByteString() {
		ByteString input = new ByteString(new byte[] {(byte) 0x42, (byte) 0x21});
		
		DataItem result = DataItemHelper.cloneDataItem(input);
		
		assertNotSame(input, result);
		assertEquals(input, result);
	}
	
	
	@Test
	public void testCloneDataItem_ByteString_tagged() {
		ByteString input = new ByteString(new byte[] {(byte) 0x42, (byte) 0x21});
		input.setTag(21L);
		
		DataItem result = DataItemHelper.cloneDataItem(input);
		
		assertNotSame(input, result);
		assertEquals(input, result);
	}
	
	
	@Test
	public void testCloneDataItem_UnicodeString() {
		UnicodeString input = new UnicodeString("42");
		
		DataItem result = DataItemHelper.cloneDataItem(input);
		
		assertNotSame(input, result);
		assertEquals(input, result);
	}
	
	
	@Test
	public void testCloneDataItem_UnicodeString_tagged() {
		UnicodeString input = new UnicodeString("42");
		input.setTag(21L);
		
		DataItem result = DataItemHelper.cloneDataItem(input);
		
		assertNotSame(input, result);
		assertEquals(input, result);
	}
	
	
	@Test
	public void testCloneDataItem_Boolean() {
		SimpleValue input = new SimpleValue(SimpleValueType.TRUE);
		
		DataItem result = DataItemHelper.cloneDataItem(input);
		
		assertNotSame(input, result);
		assertEquals(input, result);
	}
	
	
	@Test
	public void testCloneDataItem_Boolean_tagged() {
		SimpleValue input = new SimpleValue(SimpleValueType.TRUE);
		input.setTag(21L);
		
		DataItem result = DataItemHelper.cloneDataItem(input);
		
		assertNotSame(input, result);
		assertEquals(input, result);
	}
}
