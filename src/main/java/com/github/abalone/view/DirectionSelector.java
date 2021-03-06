package com.github.abalone.view;

import com.github.abalone.util.Direction;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Set;

/**
 * @author melkir
 */
class DirectionSelector extends JDialog {
    private final EnumMap<Direction, DirectionButton> buttons;

    DirectionSelector(Window window, Board board) {
        super(window, "Direction");
        this.setSize(70, 100);
        this.setLocationRelativeTo(board);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        this.buttons = new EnumMap(Direction.class);

        for (Direction d : Direction.values()) {
            DirectionButton b = new DirectionButton(d, board);
            b.setVisible(false);
            this.buttons.put(d, b);
            panel.add(b);
        }
        this.add(panel);
    }

    void updateButtons(Set<Direction> valids) {
        for (DirectionButton b : this.buttons.values()) b.setVisible(false);
        if ((valids != null) && (!valids.isEmpty())) {
            for (Direction d : valids) this.buttons.get(d).setVisible(true);
            this.setVisible(true);
        } else this.setVisible(false);
    }
}
