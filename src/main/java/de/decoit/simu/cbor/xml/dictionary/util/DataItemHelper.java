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
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;

/**
 * A utility class to help with operations on CBOR data items.
 * 
 * @author Thomas Rix (rix@decoit.de)
 */
public class DataItemHelper {
	/**
	 * Clone a CBOR data item.
	 * This creates a shallow copy meaning that only the data item object is cloned, values still use the same reference
	 * as in the old object.
	 * 
	 * Allowed input types are {@link UnsignedInteger}, {@link NegativeInteger}, {@link DoublePrecisionFloat}, 
	 * {@link ByteString}, {@link UnicodeString} and {@link SimpleValue}.
	 * 
	 * @param input Data item to clone
	 * @return Shallow copy of the input data item
	 */
	public static DataItem cloneDataItem(DataItem input) {
		if(input == null) {
			throw new IllegalArgumentException("Null pointer for input data item");
		}
		
		DataItem rv;
		
		if(input instanceof UnsignedInteger) {
			UnsignedInteger ui = (UnsignedInteger) input;
			rv = new UnsignedInteger(ui.getValue());
		}
		else if(input instanceof NegativeInteger) {
			NegativeInteger ni = (NegativeInteger) input;
			rv = new NegativeInteger(ni.getValue());
		}
		else if(input instanceof DoublePrecisionFloat) {
			DoublePrecisionFloat dpf = (DoublePrecisionFloat) input;
			rv = new DoublePrecisionFloat(dpf.getValue());
		}
		else if(input instanceof ByteString) {
			ByteString bs = (ByteString) input;
			rv = new  ByteString(bs.getBytes());
		}
		else if(input instanceof UnicodeString) {
			UnicodeString us = (UnicodeString) input;
			rv = new UnicodeString(us.getString());
		}
		else if(input instanceof SimpleValue) {
			SimpleValue sv = (SimpleValue) input;
			rv = new SimpleValue(sv.getSimpleValueType());
		}
		else {
			throw new IllegalArgumentException("Unable to clone input data item of type " + input.getMajorType());
		}
		
		if(input.hasTag()) {
			rv.setTag(input.getTag());
		}
		
		return rv;
	}
	
	
	protected DataItemHelper() {}
}
