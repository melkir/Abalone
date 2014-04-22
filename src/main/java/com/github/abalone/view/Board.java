package com.github.abalone.view;

import com.github.abalone.config.Config;
import com.github.abalone.controller.GameController;
import com.github.abalone.controller.Move;
import com.github.abalone.model.Ball;
import com.github.abalone.util.Color;
import com.github.abalone.util.Coords;
import com.github.abalone.util.Direction;
import com.github.abalone.util.GameState;
import com.github.abalone.util.listeners.ValueListener;
import com.kitfox.svg.app.beans.SVGIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author melkir
 */
class Board extends JPanel implements MouseListener, ValueListener {
    private final Window window;
    private final HashSet<Coords> selectedBalls;
    Boolean reversed = true;
    private SVGIcon board;
    private Double boardScale = -1.0;
    private SVGIcon whiteBall;
    private SVGIcon blackBall;
    private Integer origX = 0;
    private Integer origY = 0;
    private SVGIcon selection;
    private DirectionSelector selector;

    Board(Window window) {
        this.window = window;
        this.selectedBalls = new HashSet();
        Config.addValueListener(this);
        this.themeChange((String) Config.get("theme"));
        this.addMouseListener(this);
    }

    void computeBoardScale() {
        Dimension target = new Dimension(1500, 1500);
        Dimension container = this.getSize();
        Double s = 1.0;

        if ((target.width > container.width) && (target.height <= container.height))
            // It does not fit horizontally
            s = (double) container.width / (double) target.width;
        else if ((target.width <= container.width) && (target.height > container.height))
            // It does not fit vertically
            s = (double) container.height / (double) target.height;
        else if (target.width == target.height)
            s = ((container.width <= container.height) ? ((double) container.width / (double) target.width) : ((double) container.height / (double) target.height));

        Dimension scaled = new Dimension((int) (target.width * s), (int) (target.height * s));
        this.board.setPreferredSize(scaled);
        this.boardScale = s;
        this.origX = (container.width - scaled.width) / 2;
        this.origY = (container.height - scaled.height) / 2;
        Dimension ballSize = new Dimension((int) (100.0 * s), (int) (100.0 * s));
        this.whiteBall.setPreferredSize(ballSize);
        this.blackBall.setPreferredSize(ballSize);
        this.selection.setPreferredSize(ballSize);
    }

    @Override
    public void paintComponent(Graphics g) {
        this.board.paintIcon(this, g, this.origX, this.origY);
        this.paintBalls(g);
    }

    private void paintBalls(Graphics g) {
        for (Ball b : com.github.abalone.model.Board.getInstance()
                .getBalls()) {
            Coords ballCoords = b.getCoords();
            Coords coords;
            if (this.reversed) coords = new Coords(-ballCoords.getRow(), ballCoords.getCol());
            else coords = new Coords(ballCoords);
            Point point = this.getPoint(coords);
            if (b.getColor() == Color.WHITE) whiteBall.paintIcon(this, g, point.x, point.y);
            else if (b.getColor() == Color.BLACK) blackBall.paintIcon(this, g, point.x, point.y);
            if (this.selectedBalls.contains(ballCoords)) this.selection.paintIcon(this, g, point.x, point.y);
        }
    }

    private Point getPoint(Coords coords) {
        Integer r = coords.getRow();
        Integer c = coords.getCol();
        Double bX = 180.0 + Math.abs(r) * 65.0 + c * 130.0;
        Double bY = (700.0 + 110.0 * r);
        Integer x = this.origX + (int) (bX * this.boardScale);
        Integer y = this.origY + (int) (bY * this.boardScale);
        return new Point(x, y);
    }

    private Coords getCoords(Point point) {
        point.translate(-this.origX, -this.origY);
        Double bX = (double) point.x / this.boardScale - 185.0;
        Double bY = (double) point.y / this.boardScale - 750.0;

        Integer r, c;
        if ((bY > -50) && (bY < 50)) r = 0;
        else if ((bY > -160) && (bY < -60)) r = -1;
        else if ((bY > -270) && (bY < -170)) r = -2;
        else if ((bY > -380) && (bY < -280)) r = -3;
        else if ((bY > -490) && (bY < -390)) r = -4;
        else if ((bY > 60) && (bY < 160)) r = 1;
        else if ((bY > 170) && (bY < 270)) r = 2;
        else if ((bY > 280) && (bY < 380)) r = 3;
        else if ((bY > 390) && (bY < 490)) r = 4;
        else return null;

        bX -= (65.0 * Math.abs(r));
        if ((bX > 0) && (bX < 100)) c = 0;
        else if ((bX > 130) && (bX < 230)) c = 1;
        else if ((bX > 260) && (bX < 360)) c = 2;
        else if ((bX > 390) && (bX < 490)) c = 3;
        else if ((bX > 520) && (bX < 620)) c = 4;
        else if ((Math.abs(r) < 4) && (bX > 650) && (bX < 750)) c = 5;
        else if ((Math.abs(r) < 3) && (bX > 780) && (bX < 880)) c = 6;
        else if ((Math.abs(r) < 2) && (bX > 910) && (bX < 1010)) c = 7;
        else if ((Math.abs(r) < 1) && (bX > 1040) && (bX < 1140)) c = 8;
        else return null;

        return new Coords(reversed ? -r : r, c);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (this.window.isLocked()) return;
        Coords coords = this.getCoords(me.getPoint());
        if (coords == null) return;
        Color color = com.github.abalone.model.Board.getInstance().elementAt(coords);
        if (!color.isPlayer()) return;
        else if (this.selectedBalls.contains(coords)) this.selectedBalls.remove(coords);
        else if (this.selectedBalls.size() < 3) this.selectedBalls.add(coords);
        else return;
        this.repaint();
        Set<Direction> directions;
        Set<Direction> validDirections = GameController.getInstance().validDirections(this.selectedBalls);
        if (!this.reversed) directions = new HashSet<Direction>(validDirections);
        else {
            directions = new HashSet<Direction>();
            for (Direction d : validDirections) directions.add(d.reversed());
        }
        this.selector.updateButtons(directions);
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    void setMove(Move move) {
        HashSet<Direction> d = new HashSet<Direction>();
        d.add(this.reversed ? move.getDirection().reversed() : move.getDirection());
        this.selectedBalls.clear();
        for (Ball b : move.getInitialBalls()) {
            Coords c = b.getCoords();
            selectedBalls.add(c);
        }
        this.selector.updateButtons(d);
        this.repaint();
    }

    void move(Direction direction) {
        if (reversed) direction = direction.reversed();
        if (GameController.getInstance().move(this.selectedBalls, direction).equals(GameState.RUNNING)) {
            this.selectedBalls.clear();
            this.selector.updateButtons(null);
        }
    }

    /**
     * Change the theme of the game
     */
    private void themeChange(String theme) {
        this.board = new SVGIcon();
        this.board.setScaleToFit(true);
        this.board.setAntiAlias(true);
        this.whiteBall = new SVGIcon();
        this.whiteBall.setScaleToFit(true);
        this.whiteBall.setAntiAlias(true);
        this.blackBall = new SVGIcon();
        this.blackBall.setScaleToFit(true);
        this.blackBall.setAntiAlias(true);
        this.selection = new SVGIcon();
        this.selection.setScaleToFit(true);
        this.selection.setAntiAlias(true);
        try {
            if (theme == null) theme = "glossy";
            this.board.setSvgURI(getClass().getResource("game/" + theme + "/board.svg").toURI());
            this.whiteBall.setSvgURI(getClass().getResource("game/" + theme + "/white-ball.svg").toURI());
            this.blackBall.setSvgURI(getClass().getResource("game/" + theme + "/black-ball.svg").toURI());
            this.selection.setSvgURI(getClass().getResource("game/" + theme + "/selection.svg").toURI());
        } catch (URISyntaxException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }
        selector = new DirectionSelector(this.window, this);
    }

    @Override
    public void valueUpdated(Object value) {
        this.themeChange((String) value);
        this.computeBoardScale();
        this.repaint();
    }

}
