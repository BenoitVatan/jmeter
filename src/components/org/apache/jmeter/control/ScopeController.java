package org.apache.jmeter.control;

import java.io.Serializable;

import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.threads.JMeterVariablesContext;
import org.apache.jmeter.threads.ScopedVariablePolicy;

/**
 * <p>
 * Controller working together with JMeterVariables to make it possible to scope
 * variables created within this controller scope
 * </p>
 * <p>
 * Variables created or modified within this controller scope will be discarded
 * or merged (depending of the scope variable policy) when the controller has
 * completed its execution (i.e. when next sampler is null)
 * </p>
 */
public class ScopeController extends GenericController implements Serializable {

    public static final String SCOPED_VARIABLE_POLICY = "VariablesScopeController.SCOPED_VARIABLE_POLICY";
    
    public ScopeController() {
        setScopedVariablePolicy(ScopedVariablePolicy.MERGE_AFTER_SCOPE);
    }
    
    public void setScopedVariablePolicy(ScopedVariablePolicy policy) {
        setProperty(SCOPED_VARIABLE_POLICY, policy.toString());
    }
    
    public ScopedVariablePolicy getScopedVariablePolicy() {
        return ScopedVariablePolicy.valueOf(getPropertyAsString(SCOPED_VARIABLE_POLICY));
    }
    
    @Override
    protected void fireIterationStart() {
        super.fireIterationStart();
        // push a new variable context upon iteration start
        JMeterContextService.getContext().getVariables().pushContext();
    }

    @Override
    protected Sampler nextIsNull() throws NextIsNullException {
        // upon iteration completion : pop current variable context
        JMeterVariables vars = JMeterContextService.getContext().getVariables();
        JMeterVariablesContext varContext = vars.popContext();
        // depending of the policy, we need to discard context variables
        // or merge them with the next context
        switch(getScopedVariablePolicy()) {
            case MERGE_AFTER_SCOPE:
                vars.mergeWith(varContext);
                break;
            case CLEAR_AFTER_SCOPE:
                // do nothing here as context has just been popped, it is no more effective
                break;
        }
        return super.nextIsNull();
    }
    
}
