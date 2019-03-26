package org.apache.jmeter.threads;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * <p>
 * Represents a set of variables that are being accessed within some scope.<br>
 * This actually defines what _happened_ at some scope level but not what is _visible_
 * at that scope.
 * </p>
 * <p>
 * What is actually visible for a given scope is determined by stacking contexts
 * (top to bottom) and is performed by JMeterVariables. 
 * </p>
 */
public class JMeterVariablesContext {

	private Map<String, Object> variables  = new HashMap<>();
	
	
	Map<String, Object> getVariables() {
		return variables;
	}
	
	public boolean containsKey(String key) {
	    return variables.containsKey(key);
	}
	
	/**
     * Remove a variable.
     * 
     * @param key the variable name to remove
     * 
     * @return the variable value, or {@code null} if there was no such variable
     */
    public Object remove(String key) {
        return variables.remove(key);
    }

    /**
     * Creates or updates a variable with a String value.
     * 
     * @param key the variable name
     * @param value the variable value
     */
    public void put(String key, String value) {
        variables.put(key, value);
    }

    /**
     * Creates or updates a variable with a value that does not have to be a String.
     * 
     * @param key the variable name
     * @param value the variable value
     */
    public void putObject(String key, Object value) {
        variables.put(key, value);
    }

    /**
     * Updates the variables with all entries found in the {@link Map} {@code vars}
     * @param vars map with the entries to be updated
     */
    public void putAll(Map<String, ?> vars) {
        variables.putAll(vars);
    }

    /**
     * Updates the variables with all entries found in the variables in {@code vars}
     * @param vars {@link JMeterVariables} with the entries to be updated
     */
    public void putAll(JMeterVariables vars) {
        putAll(vars.getContext().variables);
    }

    /**
     * Gets the value of a variable, converted to a String.
     * 
     * @param key the name of the variable
     * @return the value of the variable or a toString called on it if it's non String, or {@code null} if it does not exist
     */
    public String get(String key) {
        Object o = variables.get(key);
        if(o instanceof String) {
            return (String) o;
        } else if (o != null) {
            return o.toString();
        } else {
            return null;
        }
    }

    /**
     * Gets the value of a variable (not converted to String).
     * 
     * @param key the name of the variable
     * @return the value of the variable, or {@code null} if it does not exist
     */
    public Object getObject(String key) {
        return variables.get(key);
    }
    
    /**
     * Gets a read-only Iterator over the variables.
     * 
     * @return the iterator
     */
    public Iterator<Entry<String, Object>> getIterator(){
        return Collections.unmodifiableMap(variables).entrySet().iterator() ;
    }

    // Used by DebugSampler
    /**
     * @return an unmodifiable view of the entries contained in {@link JMeterVariables}
     */
    public Set<Entry<String, Object>> entrySet(){
        return Collections.unmodifiableMap(variables).entrySet();
    }

}
