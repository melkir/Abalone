package com.github.abalone.view.config;

import com.github.abalone.config.ConstraintValue;
import com.github.abalone.config.Value;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author melkir
 */
class ComboBox extends JComboBox implements ActionListener {
    private final Value value;

    ComboBox(Value value) {
        this.value = value;
        for (Object option : ((ConstraintValue) value).getList())
            this.addItem(option);
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        this.value.set(this.getSelectedItem());
    }
}
