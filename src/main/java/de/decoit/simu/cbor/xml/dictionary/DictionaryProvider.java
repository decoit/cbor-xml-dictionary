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

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;



/**
 * This utility class provides a singleton instance of {@link Dictionary}.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public abstract class DictionaryProvider {
	private static Dictionary instance = null;
	private static final Map<String, Dictionary> namedInstances = new HashMap<>();


	/**
	 * Retrieve the singleton Dictionary instance.
	 *
	 * @return Dictionary instance
	 */
	public static Dictionary getInstance() {
		if(instance == null) {
			instance = new Dictionary();
		}

		return instance;
	}


	/**
	 * Retrieve a Dictionary instance that may be referenced by its name.
	 * If no instance is registered under this name, a new one is created.
	 *
	 * @param name Reference name for the requested instance
	 * @return The Dictionary instance referenced by the provided name
	 */
	public static Dictionary getNamedInstance(String name) {
		if(StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("Instance name must not be blank");
		}
		
		if(namedInstances.get(name) == null) {
			namedInstances.put(name, new Dictionary());
		}

		return namedInstances.get(name);
	}


	/**
	 * Remove the Dictionary instance referenced by the specified name.
	 * The instance will be removed from the storage of named instances and its clear() method is called to clear any content
	 * inside the Dictionary.
	 *
	 * @param name Reference name of the instance to be removed
	 */
	public static void removeNamedInstance(String name) {
		Dictionary rem = namedInstances.remove(name);

		if(rem != null) {
			rem.clear();
		}
	}
	
	
	static boolean hasNamedInstance(String name) {
		return namedInstances.containsKey(name);
	}
}
