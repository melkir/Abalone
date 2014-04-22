package com.github.abalone.view.config;

import com.github.abalone.config.Value;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author melkir
 */
class TextField extends JTextField implements DocumentListener {
    private final Value value;

    public TextField(Value value) {
        super((String) value.get());
        this.value = value;
        this.getDocument().addDocumentListener(this);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        this.value.set(this.getText());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }
}
