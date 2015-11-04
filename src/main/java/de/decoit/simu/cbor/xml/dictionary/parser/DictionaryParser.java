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

import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;



/**
 * This class is able to parse a dictionary description file into an object of {@link Dictionary}.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Slf4j
public class DictionaryParser {
	/**
	 * RegEx pattern to match on dictionary description lines.
	 * Provides the following groups when matched:<br>
	 * Group 1: Line type (a, e, n, t)<br>
	 * Group 2: xmlName<br>
	 * Group 3: CBOR type<br>
	 * Group 4: CBOR value<br>
	 * Group 5: Braces, if present
	 */
	private final Pattern p = Pattern.compile("^([aent])'(.+)'\\[(uint|negint|double|bytestr|unistr|bool)\\((.*)\\)\\]\\s*(\\{|\\{\\s*\\})?$");
	private final Dictionary dictInstance;
	private BufferedReader br = null;


	/**
	 * Create a parser instance that parses into the {@link Dictionary} object that is provided by {@link DictionaryProvider}.getInstance().
	 * Once created this parser instance is bound to the Dictionary object obtained from DictionaryProvider.
	 */
	public DictionaryParser() {
		this.dictInstance = DictionaryProvider.getInstance();
	}


	/**
	 * Create a parser instance that parses into the specified {@link Dictionary} object.
	 * Once created this parser instance is bound to the specified Dictionary object.
	 *
	 * @param dict Dictionary object to parse into.
	 */
	public DictionaryParser(Dictionary dict) {
		this.dictInstance = dict;
	}


	/**
	 * Parse the dictionary description located in the specified input file.
	 *
	 * @param inFile Input file containing the description
	 * @return The filled dictionary
	 * @throws IOException if the input file cannot be read or contains illegal or corrupt lines
	 */
	public Dictionary parseDictionary(Path inFile) throws IOException {
		if(inFile == null) {
			throw new IllegalArgumentException("Input file must not be null");
		}

		this.br = Files.newBufferedReader(inFile);

		// Read next line from buffer
		String srcLine = readLineFromBuffer();
		while(srcLine != null) {
			// Create a RegEx matcher for this line
			Matcher m = p.matcher(srcLine);

			DictionaryNamespace dns = parseDictionaryNamespace(m);
			this.dictInstance.addNamespace(dns);

			// Read next line from buffer
			srcLine = readLineFromBuffer();
		}

		this.br.close();
		this.br = null;

		return dictInstance;
	}


	/**
	 * Parse a line containing a namespace dictionary definition.
	 * Such a line must begin with <pre>n'</pre> to be parsed. The returned namespace entry
	 * will contain any nested element that is defined by the description.
	 *
	 * @param m RegEx Matcher filled with the source line to parse
	 * @return Namespace dictionary entry
	 * @throws IOException If the line cannot be parsed as a namespace entry
	 */
	private DictionaryNamespace parseDictionaryNamespace(Matcher m) throws IOException {
		// If RegEx does not match, the line is illegal. Cancel operation!
		if(!m.matches()) {
			throw new IOException("Source file contains illegal line");
		}

		// If first group does not equal "n" this line cannot be parsed as namespace
		if(!m.group(1).equals("n")) {
			throw new IOException("Illegal line type for namespace definition: " + m.group(1));
		}

		// Create a CBOR data item and create the new namespace
		DataItem cborName = createCborDataItem(m.group(3), m.group(4));
		DictionaryNamespace rv = new DictionaryNamespace(m.group(2), cborName);

		// If single opening brace at the end, then begin parsing the inner contents
		if(m.group(5) != null && m.group(5).equals("{")) {
			// Read next line from buffer
			String srcLine = readLineFromBuffer();
			// Check if line is null, this would be unexpected and an error
			checkUnexpectedEndOfFile(srcLine);

			while(!srcLine.equals("}")) {
				Matcher localMatcher = p.matcher(srcLine);

				DictionarySimpleElement dse = parseDictionaryElement(localMatcher);
				rv.addElement(dse);

				// Read next line from buffer
				srcLine = readLineFromBuffer();
				// Check if line is null, this would be unexpected and an error
				checkUnexpectedEndOfFile(srcLine);
			}
		}

		return rv;
	}


	/**
	 * Parse a line containing an element dictionary definition.
	 * Such a line must begin with <pre>t'</pre> to be parsed. The returned element entry
	 * will contain any nested element that is defined by the description. It may be of type
	 * {@link DictionarySimpleElement}, {@link DictionaryEnumValueElement} or
	 * {@link DictionaryComplexElement} depending on what additional content is defined in
	 * the dictionary description file.
	 *
	 * @param m RegEx Matcher filled with the source line to parse
	 * @return Element dictionary entry
	 * @throws IOException If the line cannot be parsed as an element entry
	 */
	private DictionarySimpleElement parseDictionaryElement(Matcher m) throws IOException {
		// If RegEx does not match, the line is illegal. Cancel operation!
		if(!m.matches()) {
			throw new IOException("Source file contains illegal line");
		}

		// If first group does not equal "t" this line cannot be parsed as element (tag)
		if(!m.group(1).equals("t")) {
			throw new IOException("Illegal line type for element (tag) definition: " + m.group(1));
		}

		// Create a CBOR data item and XML name
		String xmlName = m.group(2);
		DataItem cborName = createCborDataItem(m.group(3), m.group(4));

		DictionarySimpleElement rv;

		if(m.group(5) != null && m.group(5).equals("{")) {
			List<DictionarySimpleAttribute> nestedAttributes = new ArrayList<>();
			List<DictionarySimpleElement> nestedTags = new ArrayList<>();
			Map<String, DataItem> nestedEnumValues = new HashMap<>();

			// Read next line from buffer
			String srcLine = readLineFromBuffer();
			checkUnexpectedEndOfFile(srcLine);
			while(!srcLine.equals("}")) {
				Matcher localMatcher = p.matcher(srcLine);

				if(!localMatcher.matches()) {
					throw new IOException("Source file contains illegal line");
				}

				switch(localMatcher.group(1)) {
					case "a":
						// Found nested attribute definition, call parsing method
						DictionarySimpleAttribute nestedAttribute = parseDictionaryAttribute(localMatcher);
						nestedAttributes.add(nestedAttribute);
						break;
					case "e":
						// Found enum value definition, read XML and CBOR names
						String enumXmlName = localMatcher.group(2);
						DataItem enumCborName = createCborDataItem(localMatcher.group(3), localMatcher.group(4));
						nestedEnumValues.put(enumXmlName, enumCborName);
						break;
					case "t":
						// Found nested tag definition, call parsing method
						DictionarySimpleElement nestedTag = parseDictionaryElement(localMatcher);
						nestedTags.add(nestedTag);
						break;
					default:
						throw new IOException("Illegal nested line in element (tag) definition: " + m.group(0));
				}

				// Read next line from buffer
				srcLine = readLineFromBuffer();
				checkUnexpectedEndOfFile(srcLine);
			}

			// Having nested tags and nested enum values is an error
			if(nestedTags.size() > 0 && nestedEnumValues.size() > 0) {
				throw new IOException("Nested tags and nested enum values detected for element (tag) definition");
			}

			// If we have nested tags, we have to create a DictionaryComplexElement
			if(nestedTags.size() > 0) {
				DictionaryComplexElement dce = new DictionaryComplexElement(xmlName, cborName);

				// Add found nested tags to the created element
				nestedTags.stream().forEach((locDse) -> {
					dce.addNestedElement(locDse);
				});

				rv = dce;
			}
			// If we have nested enum values, we have to create a DictionaryEnumValueElement
			else if(nestedEnumValues.size() > 0) {
				DictionaryEnumValueElement deve = new DictionaryEnumValueElement(xmlName, cborName);

				// Add found nested enum values to the created element
				nestedEnumValues.entrySet().stream().forEach((evEntry) -> {
					deve.addEnumValue(evEntry.getKey(), evEntry.getValue());
				});

				rv = deve;
			}
			// If none of those are present, we create a DictionarySimpleElement
			else {
				rv = new DictionarySimpleElement(xmlName, cborName);
			}

			// Add all nested attributes to the element created before
			nestedAttributes.stream().forEach((locDsa) -> {
				rv.addAttribute(locDsa);
			});
		}
		else {
			rv = new DictionarySimpleElement(xmlName, cborName);
		}

		return rv;
	}


	/**
	 * Parse a line containing an attribute dictionary definition.
	 * Such a line must begin with <pre>a'</pre> to be parsed. The returned attribute entry
	 * will contain any nested enum value that is defined by the description. It may be of type
	 * {@link DictionarySimpleAttribute} or {@link DictionaryEnumValueAttribute} depending on
	 * what additional content is defined in the dictionary description file.
	 *
	 * @param m RegEx Matcher filled with the source line to parse
	 * @return Attribute dictionary entry
	 * @throws IOException If the line cannot be parsed as an attribute entry
	 */
	private DictionarySimpleAttribute parseDictionaryAttribute(Matcher m) throws IOException {
		// If RegEx does not match, the line is illegal. Cancel operation!
		if(!m.matches()) {
			throw new IOException("Source file contains illegal line");
		}

		// If first group does not equal "a" this line cannot be parsed as attribute
		if(!m.group(1).equals("a")) {
			throw new IOException("Illegal line type for attribute definition: " + m.group(1));
		}

		// Create a CBOR data item and XML name
		String xmlName = m.group(2);
		DataItem cborName = createCborDataItem(m.group(3), m.group(4));

		DictionarySimpleAttribute rv;

		if(m.group(5) != null && m.group(5).equals("{")) {
			Map<String, DataItem> nestedEnumValues = new HashMap<>();

			// Read next line from buffer
			String srcLine = readLineFromBuffer();
			checkUnexpectedEndOfFile(srcLine);
			while(!srcLine.equals("}")) {
				Matcher localMatcher = p.matcher(srcLine);

				if(!localMatcher.matches()) {
					throw new IOException("Source file contains illegal line");
				}

				switch(localMatcher.group(1)) {
					case "e":
						// Found enum value definition, read XML and CBOR names
						String enumXmlName = localMatcher.group(2);
						DataItem enumCborName = createCborDataItem(localMatcher.group(3), localMatcher.group(4));
						nestedEnumValues.put(enumXmlName, enumCborName);
						break;
					default:
						throw new IOException("Illegal nested line in attribute definition: " + m.group(0));
				}

				// Read next line from buffer
				srcLine = readLineFromBuffer();
				checkUnexpectedEndOfFile(srcLine);
			}

			if(nestedEnumValues.size() > 0) {
				DictionaryEnumValueAttribute deva = new DictionaryEnumValueAttribute(xmlName, cborName);

				// Add found nested enum values to the created attribute object
				nestedEnumValues.entrySet().stream().forEach((evEntry) -> {
					deva.addEnumValue(evEntry.getKey(), evEntry.getValue());
				});

				rv = deva;
			}
			else {
				rv = new DictionarySimpleAttribute(xmlName, cborName);
			}
		}
		else {
			rv = new DictionarySimpleAttribute(xmlName, cborName);
		}

		return rv;
	}


	/**
	 * Create a CBOR data item of the correct type filled with the specified value.
	 * Possible values for the type parameter are:<br>
	 * - uint (Unsigned Integer)<br>
	 * - negint (Negative Integer)<br>
	 * - double (Double)<br>
	 * - bytestr (Byte String)<br>
	 * - unistr (Unicode String)<br>
	 * - bool (Boolean)<br>
	 * The provided CBOR value must be a valid value for that type, otherwise an exception
	 * will be raised by the specific parser.
	 *
	 * @param type CBOR type to use
	 * @param value Value of the data item
	 * @return CBOR data item
	 * @throws IOException if an invalid CBOR type was provided
	 */
	private DataItem createCborDataItem(String type, String value) throws IOException {
		if(StringUtils.isBlank(type)) {
			throw new IllegalArgumentException("CBOR type must not be blank");
		}

		if(StringUtils.isBlank(value)) {
			throw new IllegalArgumentException("CBOR value must not be blank");
		}

		switch(type) {
			case "uint":
				return new UnsignedInteger(Long.valueOf(value));
			case "negint":
				return new NegativeInteger(Long.valueOf(value));
			case "double":
				return new DoublePrecisionFloat(Double.valueOf(value));
			case "bytestr":
				return new ByteString(DatatypeConverter.parseHexBinary(value));
			case "unistr":
				return new UnicodeString(value);
			case "bool":
				if(Boolean.parseBoolean(value)) {
					return new SimpleValue(SimpleValueType.TRUE);
				}
				else {
					return new SimpleValue(SimpleValueType.FALSE);
				}
			default:
				throw new IOException("Unknown CBOR type found: " + type);
		}
	}


	/**
	 * Read the next line from the {@link BufferedReader}.
	 * This method skips empty lines and returns either lines filled with content
	 * or null (end of file).
	 *
	 * @return Source file or null (end of file)
	 * @throws IOException if reading from the source file fails
	 */
	private String readLineFromBuffer() throws IOException {
		String srcLine;
		do {
			// Read lines until the line is null or not empty
			srcLine = this.br.readLine();
		} while(StringUtils.isWhitespace(srcLine));

		// If line is not null trim leading and trailing whitespaces
		if(srcLine != null) {
			srcLine = srcLine.trim();
		}

		if(log.isDebugEnabled()) {
			log.debug("Source line: " + srcLine);

			if(srcLine == null) {
				log.debug("Reached EOF!");
			}
		}

		return srcLine;
	}


	/**
	 * Test if line is unexpectedly null.
	 * If it is null, an {@link IOException} is raised to show this was unexpected.
	 *
	 * @param line Line to check
	 * @throws IOException if line is null
	 */
	private void checkUnexpectedEndOfFile(String line) throws IOException {
		if(line == null) {
			throw new IOException("Unexpected end of file");
		}
	}
}
