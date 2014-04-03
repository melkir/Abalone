package com.github.abalone.view;

import com.github.abalone.controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author melkir
 */
public class Window extends JFrame implements ComponentListener {
    private final Board board;
    private final Boolean locked = Boolean.FALSE;

    public Window() throws Exception {
        super("Abalone");

        GameController.getInstance().setWindow(this);

        this.setSize(600, 600);

        String[] lookAndFeels = {"com.sun.java.swing.plaf.gtk.GTKLookAndFeel",
                "com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
                "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel",
                "com.sun.java.swing.plaf.motif.MotifLookAndFeel",
                "javax.swing.plaf.metal.MetalLookAndFeel"};

        integrate:
        {
            for (String name : lookAndFeels) {
                if (this.checkLookAndFeel(name))
                    break integrate;
            }
            throw new Exception("No LookAndFeel");
        }

        LayoutManager layout = new BorderLayout();
        this.setLayout(layout);

        this.board = new Board(this);
        Toolbar toolbar = new Toolbar(this.board);
        JLabel status = new JLabel("Abalone");

        this.add(toolbar, BorderLayout.PAGE_START);
        this.add(this.board);
        this.add(status, BorderLayout.PAGE_END);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.addComponentListener(this);
    }

    Boolean isLocked() {
        return this.locked;
    }

    private Boolean checkLookAndFeel(String name) {
        try {
            UIManager.setLookAndFeel(name);
            return true;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void updateBoard() {
        this.board.repaint();
    }

    @Override
    public void componentResized(ComponentEvent ce) {
        this.board.computeBoardScale();
    }

    @Override
    public void componentMoved(ComponentEvent ce) {
    }

    @Override
    public void componentShown(ComponentEvent ce) {
    }

    @Override
    public void componentHidden(ComponentEvent ce) {
    }

}
