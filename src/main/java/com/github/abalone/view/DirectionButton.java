package com.github.abalone.view;

import com.github.abalone.config.Config;
import com.github.abalone.util.Direction;
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
class DirectionButton extends JButton implements ActionListener {
    private final Direction direction;
    private final Board board;

    DirectionButton(Direction direction, Board board) {
        super();
        this.direction = direction;
        this.board = board;
        Dimension d = new Dimension(30, 30);
        SVGIcon icon = new SVGIcon();
        icon.setScaleToFit(true);
        icon.setAntiAlias(true);
        icon.setPreferredSize(d);
        try {
            icon.setSvgURI(getClass().getResource("game/" + Config.get("theme") + "/" + this.direction.name() + ".svg").toURI());
        } catch (URISyntaxException ex) {
            Logger.getLogger(ToolButton.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.setIcon(icon);
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        this.board.move(this.direction);
    }

}
