/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.jmeter.threads;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jmeter.util.JMeterUtils;

/**
 * Class which defines JMeter variables. These are similar to properties, but
 * they are local to a single thread.
 */
public class JMeterVariables {

	private LinkedList<JMeterVariablesContext> contextStack = new LinkedList<>();

	private JMeterVariablesContext currentContext;

	private int iteration = 0;

	// Property names to preload into JMeter variables:
	private static final String[] PRE_LOAD = { "START.MS", // $NON-NLS-1$
			"START.YMD", // $NON-NLS-1$
			"START.HMS", //$NON-NLS-1$
			"TESTSTART.MS", // $NON-NLS-1$
	};

	public JMeterVariablesContext pushContext() {
		this.currentContext = new JMeterVariablesContext();
		currentContext.put("__jm__VariableScope", Integer.toString(contextStack.size()));
		contextStack.push(this.currentContext);
		return this.currentContext;
	}

	public JMeterVariablesContext popContext() {
		JMeterVariablesContext ctx = contextStack.pop();
		this.currentContext = contextStack.peek();
		return ctx;
	}

	public JMeterVariablesContext getContext() {
		return contextStack.peek();
	}
	
	public int getDepth() {
	    return contextStack.size();
	}

	/**
	 * Constructor, that preloads the variables from the JMeter properties
	 */
	public JMeterVariables() {
		preloadVariables(pushContext().getVariables());
	}

	private void preloadVariables(Map<String, Object> variables) {
		for (String property : PRE_LOAD) {
			String value = JMeterUtils.getProperty(property);
			if (value != null) {
				variables.put(property, value);
			}
		}
	}

	/**
	 * @return the name of the currently running thread
	 */
	public String getThreadName() {
		return Thread.currentThread().getName();
	}

	/**
	 * @return the current number of iterations
	 */
	public int getIteration() {
		return iteration;
	}

	/**
	 * Increase the current number of iterations
	 */
	public void incIteration() {
		iteration++;
	}

	/**
	 * Remove a variable.
	 * 
	 * @param key the variable name to remove
	 * 
	 * @return the variable value, or {@code null} if there was no such variable
	 */
	public Object remove(String key) {
		return currentContext.remove(key);
	}

	/**
	 * Creates or updates a variable with a String value.
	 * 
	 * @param key   the variable name
	 * @param value the variable value
	 */
	public void put(String key, String value) {
		currentContext.put(key, value);
	}

	/**
	 * Creates or updates a variable with a value that does not have to be a String.
	 * 
	 * @param key   the variable name
	 * @param value the variable value
	 */
	public void putObject(String key, Object value) {
		currentContext.putObject(key, value);
	}

	/**
	 * Updates the variables with all entries found in the {@link Map} {@code vars}
	 * 
	 * @param vars map with the entries to be updated
	 */
	public void putAll(Map<String, ?> vars) {
		currentContext.putAll(vars);
	}

	/**
	 * Updates the variables with all entries found in the variables in {@code vars}
	 * 
	 * @param vars {@link JMeterVariables} with the entries to be updated
	 */
	public void putAll(JMeterVariables vars) {
		currentContext.putAll(vars);
	}

	/**
	 * Gets the value of a variable, converted to a String.
	 * 
	 * @param key the name of the variable
	 * @return the value of the variable or a toString called on it if it's non
	 *         String, or {@code null} if it does not exist
	 */
	public String get(String key) {
		Object out = getObject(key);
		return out == null ? null : out.toString();
	}

	/**
	 * Gets the value of a variable (not converted to String).
	 * 
	 * @param key the name of the variable
	 * @return the value of the variable, or {@code null} if it does not exist
	 */
	public Object getObject(String key) {
		if (contextStack.size() == 1) {
			return currentContext.getObject(key);
		} else {
			Iterator<JMeterVariablesContext> contexts = contextStack.iterator();
			while (contexts.hasNext()) {
				JMeterVariablesContext varContext = contexts.next();
				Object out = varContext.getObject(key);
                if(out != null) {
                    // key has a value at this level: hit map only once and return that value
                    return out;
                } else {
                    // if value is null we have 2 cases:
                    //   1: the key exists and its value is actually null
                    //   2: the key does not exist at this level and wee need to check at the next level
                    if(varContext.containsKey(key)) {
                        // key exists and value is null
                        return null;
                    }
                }
			}
			// here the key has not been found at any level: the key does not exist.
			return null;
		}
	}

	/**
	 * Gets a read-only Iterator over the variables.
	 * 
	 * @return the iterator
	 */
	public Iterator<Entry<String, Object>> getIterator() {
	    JMeterVariables vars = flatten();
	    return Collections.unmodifiableMap(vars.currentContext.getVariables()).entrySet().iterator() ;
	}
	
    /**
     * <p>
     * Return JMeterVariables as a single instance that represents the stacked view
     * of variables scopes at the time of the call<br>
     * This actually provided a flattened view of the current stacked variables
     * scopes.
     * </p>
     * 
     * @return A single JMeterVariables instance containing all stacked variables
     *         scopes
     */
	public JMeterVariables flatten() {
	    JMeterVariables out = new JMeterVariables();
	    if(contextStack.size() == 1) {
	        out.putAll(currentContext.getVariables());
	    } else {
	        Iterator<JMeterVariablesContext> contexts = contextStack.descendingIterator();
	        while(contexts.hasNext()) {
	            JMeterVariablesContext context = contexts.next();
	            out.putAll(context.getVariables());
	        }
	    }
	    return out;
	}
	
	JMeterVariablesContext getJMeterVariablesContext(int depth) {
	    return contextStack.get(depth);
	}

    /**
     * Merges the provided context with the current one by overwriting all current
     * variables with those of the specified context
     * 
     * @param varContext The variable context to be merged with the current one.
     */
	public void mergeWith(JMeterVariablesContext varContext) {
	    currentContext.putAll(varContext.getVariables());
	}
	
	// Used by DebugSampler
	/**
	 * @return an unmodifiable view of the entries contained in
	 *         {@link JMeterVariables}
	 */
	public Set<Entry<String, Object>> entrySet() {
		return flatten().getContext().entrySet();
	}
}
