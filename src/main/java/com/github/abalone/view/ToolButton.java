package com.github.abalone.view;

import com.github.abalone.controller.GameController;
import com.github.abalone.controller.Move;
import com.kitfox.svg.app.beans.SVGIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author melkir
 */
class ToolButton extends JButton implements ActionListener {
    private final String type;
    private JFrame frame;
    private JComponent component;

    public ToolButton(String type, JComponent component) {
        this(type);
        this.component = component;
    }

    public ToolButton(String type) {
        super();
        this.type = type;
        Dimension d = new Dimension(30, 30);

        SVGIcon icon = new SVGIcon();
        icon.setScaleToFit(true);
        icon.setAntiAlias(true);
        icon.setPreferredSize(d);
        try {
            icon.setSvgURI(getClass().getResource("icons/" + this.type + ".svg").toURI());
        } catch (URISyntaxException ex) {
            Logger.getLogger(ToolButton.class.getName()).log(Level.SEVERE, null, ex);
        }
        setIcon(icon);
        addActionListener(this);
        if (this.type.equals("preferences")) this.frame = new com.github.abalone.view.config.Window();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        GameController gc = GameController.getInstance();
        if (type.equals("new-game")) {
            gc.launch();
            ((Toolbar) component).gameLaunched();
        } else if (type.equals("best-move")) {
            Move move = gc.getCurrentBestMove();
            ((Board) component).setMove(move);
        } else if (type.equals("load-game") && gc.load()) ((Toolbar) component).gameLaunched();
        else if (type.equals("save-game")) gc.save();
        else if (type.equals("undo")) gc.goBack();
        else if (type.equals("preferences")) frame.setVisible(true);
        else if (type.equals("quit")) gc.quit();
    }
}
