# DECOIT CBOR-XML-Dictionary
Dictionary library to translate between XML and CBOR data items.

## Features
This Java library allows you to define a dictionary that translates between XML and CBOR data items. Its main purpose is to reduce the size of XML structures when trasferring them over a network or storing them on disk. To achieve this goal it allows to map XML names, such as namespaces or element names, to simple CBOR data items. Depending on the target CBOR data type this may result in huge reductions in size. The mapping of which XML element translates into which CBOR data item is fully customizable using a simple dictionary description language. Short overview over the features:

* Forward lookup for translations from XML to CBOR
* Reverse lookup for translations from CBOR to XML
* Support for XML namespaces, elements, attributes and enums
* Support for hierarchical structures
* Support for multiple dictionaries in a single application
* Simple description language for dictionary definition
* Searching the whole dictionary by providing a path to an element

The library features full support for forward and reverse lookup of translations. Forward does mean that it translates XML elements into CBOR data items, reverse means the other way round. All major structural XML elements are supported, meaning namespaces, element names, attribute names and enum values can be mapped to CBOR data items. To fully exploit the CBOR feature of representing many values with a single byte the dictionary allows hierarchical definition of XML structures. This allows you to reuse the same CBOR data item as a translation for multiple different XML elements, as long as they reside on different levels inside the hierarchy. To make finding elements in this hierarchy easier it is possible to specify a path to the desired element and the dictionary will return it. See the section *Searching by path* below.

Examples of how to use this dictionary to create CBOR data structures that represent XML documents may be found in our CBOR-IF-MAP projects:

* CBOR-IF-MAP TNC Base
* CBOR-IF-MAP SIMU Extensions

### Description language
Dictionaries for usage with this library are defined by using a simple plain text description language. It allows both flat and hierarchical dictionaries at the same time, meaning you are free to build your dictionary structure the way your application can use it best. However some conventions must be followed to successfully import the dictionary into this library. Most of them are common sense since XML does not allow most those cases either.

* Namespaces MUST be the top level elements and MUST NOT be nested
* Tags MUST be members of namespaces and MAY be nested into each other
* Tags MUST NOT be nested into attributes
* Attributes MUST be members of tags and MUST NOT be nested into each other
* Enum values MUST be members of tags or attributes and MUST NOT be nested into each other

#### CBOR Types
The dictionary supports the six most important data types available in the CBOR language: Positive (unsigned) and negative integers, doubles, byte strings, Unicode strings and booleans. Those are represented by the following abbreviations:

* Positive integer: `uint`
* Negative integer: `negint`
* Double: `double`
* Byte string: `bytestr`
* Unicide String: `unistr`
* Boolean: `bool`

Those abbreviations are used to describe the type mapping in the description language. To define the target value it is places in parenthesis after the type. The value must of course match the type, otherwise the mapping will not be valid. Both positive and negative integers may represent the 0 and thus mapping two different names to it is possible but not recommended. Some examples:

```
uint(42)
negint(-5)
double(22.5)
bytestr(FFDD13658900FF)
unistr(Value String)
bool(true)
```

Now we'll have a look at how to describe the XML names to translate to those CBOR definitions.

#### XML Types
Describing the XML types uses a similar simple approach. The four supported types, namespace, tag, attribute and enum value, are represented by the following one-letter abbreviations:

* Namespace: `n`
* Tag: `t`
* Attribute: `a`
* Enum value: `e`

The name of the XML element is written in single quotes right after the type letter. Some examples:

```
n'http://www.example.org/XMLSCHEMA/1'
t'child-element'
a'type'
e'shallow'
```

#### Fitting both together
To describe a mapping from XML to CBOR the two components described before just need to be concatenated. First the XML description, then the CBOR component. To make parsing easier the CBOR component is enclosed in brackets.

```
n'http://www.example.org/XMLSCHEMA/1'[uint(42)] {}
```

This line describes a mapping of the namespace `http://www.example.org/XMLSCHEMA/1` to a CBOR unsigned integer data item with the value 42. The braces at the end of the line are optional if no nested elements follow. However they are required if nested elements follow, see the next section for information about how to fit all these things together.

#### Fitting it all together
Now let's see how we fit all these things together to build a final dictionary description that may be imported inte the library. This section will use the following example XML document to show how this works:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<ex:root xmlns:ex="http://www.example.org/XMLSCHEMA/1" xmlns:mex="http://www.myexample.org/XMLSCHEMA/2">
	<ex:child-element>
		<mex:some-element type="shallow">Some text here</mex:some-element>
		<mex:some-element type="hierarchical">
			<mex:some-other-element name="my-element-name" name-length="15" />
			<mex:some-other-element name="some other-element-name" name-length="23" />
		</mex:some-element>
	</ex:child-element>
</ex:root>
```

There are several ways to create a dictionary for this document but we will describe only two of them here: Flat and hierarchical. But first it is required to make a list of things we need to map after all:

* 2 namespaces: `http://www.example.org/XMLSCHEMA/1` and `http://www.myexample.org/XMLSCHEMA/2`
* 2 tags per namespace: `root` and `child-element` for the first and `some-element` and `some-other-element` for the second
* 1 attribute for the `some-element` tag and 2 attributes for `some-other-element`
* 2 enum values for the `type` attribute of `some-element`

If an attrbute or tag has an enum value and which actual values are allowed there can usually be found in the document description file (i.e. XSD or DOCTYPE). We'll assume here that the attribute 'type' is of type enum and has the possible values 'shallow' and 'hierarchical'.

The first step for creating a dictionary description is always deciding what CBOR types should be used for mapping. We will use only unsigned integers in this example. There is no restriction for mixing types but it is recommended to only use a single type per hierarchical level since it makes debugging much easier in case something went wrong. Now let's begin with the namespace mappings:

```
n'http://www.example.org/XMLSCHEMA/1'[uint(0)] {}
n'http://www.myexample.org/XMLSCHEMA/2'[uint(1)] {}
```

Now this was easy, let's add the tags to the mix. There are two approaches here, we'll use the flat dictionary layout first. An example of the hierarchical layout will be at the end of this section.

```
n'http://www.example.org/XMLSCHEMA/1'[uint(0)] {
	t'root'[uint(0)]
	t'child-element'[uint(1)]
}
n'http://www.myexample.org/XMLSCHEMA/2'[uint(1)] {
	t'some-element'[uint(0)]
	t'some-other-element'[uint(1)]
}
```

The namespace `http://www.example.org/XMLSCHEMA/1` is completed now but we need to add the attributes and enum values to the second namespace's tags:

```
n'http://www.example.org/XMLSCHEMA/1'[uint(0)] {
	t'root'[uint(0)]
	t'child-element'[uint(1)]
}
n'http://www.myexample.org/XMLSCHEMA/2'[uint(1)] {
	t'some-element'[uint(0)] {
		a'type'[uint(0)] {
			e'shallow'[uint(0)]
			e'hierarchical'[uint(1)]
		}
	}
	t'some-other-element'[uint(1)] {
		a'name'[uint(0)]
		a'name-length'[uint(1)]
	}
}
```

And we are done, the dictionary description is complete and ready to be imported into the library. Now let's take a look at the hierarchical dictionary layout. It requires even less different CBOR values because of the possibility to reuse mappings on lower levels.

```
n'http://www.example.org/XMLSCHEMA/1'[uint(0)] {
	t'root'[uint(0)] {
		t'child-element'[uint(0)]
	}
}
n'http://www.myexample.org/XMLSCHEMA/2'[uint(1)] {
	t'some-element'[uint(0)] {
		a'type'[uint(0)] {
			e'shallow'[uint(0)]
			e'hierarchical'[uint(1)]
		}
		t'some-other-element'[uint(0)] {
			a'name'[uint(0)]
			a'name-length'[uint(1)]
		}
	}
}
```

Both ways are perfectly fine and may even be mixed. Which one you choose depends on the things you want to do with the dictionary. If you are walking through an XML structure it may be better to use a hierarchical layout because you can use the last used dictionary element as a starting point for the next hierarchy level.

### Searching by path
To make finding a specific element in hierarchical dictionaries easier it may be specified by a path description which is then passed to the library to retrieve the element's description object. The path description must be provided as absolute path. It requires a namespace as starting point and must include the full path to the element. Target elements of a path search may be namespaces, tags or attributes. Enum values may not be searched directly, they can be retrieved from the parent tag or attribute description object.

Definition of a search path:

```
<NAMESPACE>ELEMENT_A+ELEMENT_B@ATTRIBUTE
```

For example, to retrieve the `name` attribute of the `some-other-element` tag in the above example the path would look as follows:

```
<http://www.myexample.org/XMLSCHEMA/2>some-element+some-other-element@name
```

To get the tag itself and not the attribute we would use this path:

```
<http://www.myexample.org/XMLSCHEMA/2>some-element+some-other-element
```

## Preparation
The following requirements must be met to compile and use this library:

* Java 8 or higher
* Maven 3

To compile this project the Oracle JDK is preferred but it may work as well on other JDK implementations. Any Java 8 compatible JRE (Oracle, OpenJDK, Apple) should be able to run the application.

## Installation
Follow these steps to compile the project and install the JAR to your local Maven repository:

* Open a command prompt and change directory to the root of this project
* Execute `mvn install`

## Usage
To use this library in your application simply add the dependency to your pom.xml file:

```xml
<dependency>
    <groupId>de.decoit.simu</groupId>
    <artifactId>cbor-xml-dictionary</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### Creating and using a dictionary
If you have a dictionary description ready using the description language discussed before, simply use the `DictionaryProvider` class to retrieve a `Dictionary` instance and import the description into it.

```java
// Retrieve a default singleton instance of Dictionary
// The instance will be the same for every call of this method
Dictionary defaultDict = DictionaryProvider.getInstance();

// If your need multiple dictionaries in your application you may
// use named instances
Dictionary namedDictA = DictionaryProvider.getNamedInstance("A");
Dictionary namedDictB = DictionaryProvider.getNamedInstance("B");

// A named instance may be removed completely by calling this method.
// The dictionary is cleared completely and is any existing reference to it
// is useless after this operation
DictionaryProvider.removeNamedInstance("A");

// Import a dictionary description file into your dictionary instance.
// This call will preserve any existing mappings inside the dictionary.
Path description = Path input = Paths.get(ClassLoader.getSystemResource("my-dict.dict").toURI());
defaultDict.extendDictionary(description);

// To make a fresh start and remove anything inside a dictionary before
// importing use this call
namedDictA.replaceDictionary(description);

// Now let's lookup a namespace description object
DictionaryNamespace ns = defaultDict.lookupNamespace("http://www.myexample.org/XMLSCHEMA/2");

// And now get a tag from the namespace
DictionarySimpleElement dse = ns.lookupElement("root");

// Of course we can do a reverse lookup as well, the result will be the same
// as the 'dse' object above.
DataItem di = new UnsignedInteger(0);
DictionarySimpleElement dseReverse = ns.reverseLookupElement(di);

// Now let's so some path searching, first retrieve a tag
DictionarySimpleElement dse = defaultDict.findElementByPath(
				"<http://www.myexample.org/XMLSCHEMA/2>some-element+some-other-element");

// And now let's find an attrbute
DictionarySimpleAttribute dse = defaultDict.findAttributeByPath(
				"<http://www.myexample.org/XMLSCHEMA/2>some-element+some-other-element@name");
```

The `DictionarySimpleElement` and `DictionarySimpleAttribute` are subclassed by additional classes to add features like nested tags or enum values. The lookup methods only return the simple versions of these classes but you can find out which actual type the result has by using the following code:

```java
DictionarySimpleElement dse = ns.lookupElement("foo");
if(dse.isEnumValueElement()) {
	DictionaryEnumValueElement deve = (DictionaryEnumValueElement) dse;
}

if(dse.isComplexElement()) {
	DictionaryComplexElement deve = (DictionaryComplexElement) dse;
}

DictionarySimpleAttribute dsa = dse.lookupAttribute("bar");
if(dsa.isEnumValueAttribute()) {
	DictionaryEnumValueAttribute deva = (DictionaryEnumValueAttribute) dsa;
}
```

## License
The source code and all other contents of this repository are copyright by DECOIT GmbH and licensed under the terms of the [Apache License Version 2.0](http://www.apache.org/licenses/). A copy of the license may be found inside the LICENSE file.