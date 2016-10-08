/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2011-2012  Linagora
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version, provided you comply 
 * with the Additional Terms applicable for OBM connector by Linagora 
 * pursuant to Section 7 of the GNU Affero General Public License, 
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain 
 * the “Message sent thanks to OBM, Free Communication by Linagora” 
 * signature notice appended to any and all outbound messages 
 * (notably e-mail and meeting requests), (ii) retain all hypertext links between 
 * OBM and obm.org, as well as between Linagora and linagora.com, and (iii) refrain 
 * from infringing Linagora intellectual property rights over its trademarks 
 * and commercial brands. Other Additional Terms apply, 
 * see <http://www.linagora.com/licenses/> for more details. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License 
 * for more details. 
 *
 * You should have received a copy of the GNU Affero General Public License 
 * and its applicable Additional Terms for OBM along with this program. If not, 
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License version 3 
 * and <http://www.linagora.com/licenses/> for the Additional Terms applicable to 
 * OBM connectors. 
 * 
 * ***** END LICENSE BLOCK ***** */
package com.linagora.crsh.guice;

import java.util.Collection;
import java.util.Map.Entry;

import com.google.inject.Inject;
import org.crsh.plugin.PropertyDescriptor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public class CrashGuiceConfiguration {


	private final ImmutableMap<PropertyDescriptor<Object>, Object> configuration;


	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private Multimap<String, Object> values;

		private Builder() {
			values = ArrayListMultimap.create();
		}
		
		public Builder property(String propertyName, Object value) {
			values.put(propertyName, value);
			return this;
		}
		
		public CrashGuiceConfiguration build() {
			ImmutableMap.Builder<PropertyDescriptor<Object>, Object> configuration = ImmutableMap.builder();
			for (Entry<String, Collection<Object>> entry: values.asMap().entrySet()) {
				Collection<Object> values = entry.getValue();
				if (values.size() != 1) {
					throw new IllegalStateException("Duplicate entry for property : " + entry.getKey());
				}
				Object value = Iterables.getOnlyElement(values);
				configuration.put(new PropertyDescriptor(value.getClass(), entry.getKey(), value, "") {
					@Override
					protected Object doParse(String s) throws Exception {
						return s;
					}
				}, value);
				
			}
			return new CrashGuiceConfiguration(configuration.build());
		}
	}

    @Inject
	public CrashGuiceConfiguration(ImmutableMap<PropertyDescriptor<Object>, Object> configuration) {
		this.configuration = configuration;
	}

	
	public Iterable<Entry<PropertyDescriptor<Object>, Object>> toEntries() {
		return configuration.entrySet();
	}
	
}
