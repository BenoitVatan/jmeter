package org.apache.jmeter.threads;

/**
 * Works together with ScopeController to control variable scope policy
 * at the end of the scope 
 */
public enum ScopedVariablePolicy {

    /**
     * Discard variables when the scope within which they have been altered is ended
     */
    CLEAR_AFTER_SCOPE,
    /**
     * Merge variables with existing ones when the scope within which they have
     * been altered is ended
     */
    MERGE_AFTER_SCOPE
    
}
