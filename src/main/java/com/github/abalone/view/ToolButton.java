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
 * @author sardemff7
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
        this.setIcon(icon);

        this.addActionListener(this);

        if (this.type.equals("preferences")) {
            this.frame = new com.github.abalone.view.config.Window();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (this.type.equals("new-game")) {
            GameController.getInstance().launch();
            ((Toolbar) this.component).gameLaunched();
        } else if (this.type.equals("save-game")) {
            GameController.getInstance().save();
        } else if (this.type.equals("load-game")) {
            if (GameController.getInstance().load())
                ((Toolbar) this.component).gameLaunched();
        } else if (this.type.equals("best-move")) {
            Move move = GameController.getInstance().getCurrentBestMove();
            ((Board) this.component).setMove(move);
        } else if (this.type.equals("undo")) {
            GameController.getInstance().goBack();
        } else if (this.type.equals("preferences")) {
            this.frame.setVisible(true);
        } else if (this.type.equals("quit")) {
            GameController.getInstance().quit();
        }
    }
}
