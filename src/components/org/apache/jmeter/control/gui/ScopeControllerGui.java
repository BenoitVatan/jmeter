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

package org.apache.jmeter.control.gui;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.apache.jmeter.control.ScopeController;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.threads.ScopedVariablePolicy;
import org.apache.jorphan.gui.layout.VerticalLayout;

public class ScopeControllerGui extends AbstractControllerGui {
    private static final long serialVersionUID = 240L;

    private JComboBox<ScopedVariablePolicy> scopeVariablePolicyComboBox;

    private JPanel policyPanel;
    
    public ScopeControllerGui() {
        init();
    }

    private void init() {
        setLayout(new VerticalLayout(5, VerticalLayout.BOTH, VerticalLayout.TOP));
        setBorder(makeBorder());
        add(makeTitlePanel());
        add(getPolicyPanel());
    }
    
    private JComboBox<ScopedVariablePolicy> getScopedVariablePolicyComboBox() {
        if(scopeVariablePolicyComboBox == null) {
            scopeVariablePolicyComboBox = new JComboBox<>();
            for(ScopedVariablePolicy policy : ScopedVariablePolicy.values()) {
                scopeVariablePolicyComboBox.addItem(policy);
            }
        }
        return scopeVariablePolicyComboBox;
    }
    
    private JPanel getPolicyPanel() {
        if(policyPanel == null) {
            policyPanel = new VerticalPanel();
            policyPanel.add(getScopedVariablePolicyComboBox());
        }
        return policyPanel;
    }
    
    @Override
    public TestElement createTestElement() {
        ScopeController out = new ScopeController();
        modifyTestElement(out);
        return out;
    }

    /**
     * Modifies a given TestElement to mirror the data in the gui components.
     *
     * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
     */
    @Override
    public void modifyTestElement(TestElement oc) {
        configureTestElement(oc);
        ScopeController varController = (ScopeController)oc;
        varController.setScopedVariablePolicy((ScopedVariablePolicy)getScopedVariablePolicyComboBox().getSelectedItem());
    }

    @Override
    public String getLabelResource() {
        return "scope_controller_title"; // $NON-NLS-1$
    }
    
    @Override
    public void configure(TestElement element) {
        super.configure(element);
        ScopeController varController = (ScopeController)element;
        ScopedVariablePolicy policy = varController.getScopedVariablePolicy();
        getScopedVariablePolicyComboBox().setSelectedItem(policy);
    }
    
}
