/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextAttribute;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.event.MouseInputAdapter;

/**
 *
 * @author theph
 */
public class GraphVertex extends JComponent {

    public static final int radius = 20;
    //variables for drag and drop vertex
    private boolean dragging = false;

    private int mouseX = 0;
    private int mouseY = 0;
    private int dragStartX = 0;
    private int dragStartY = 0;

    JPopupMenu contextMenu;
    LinkedList<VertexActionListener> listeners;

    private int id;
    public GraphVertex(JPanel parent, int x, int y, int id) {
        this.id = id;
        this.setLocation(x, y);
        this.setSize(radius * 2, radius * 2);
        listeners = new LinkedList<>();
        this.initContextMenu();
        this.addMouseListener(new MouseInputAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    mouseX = e.getXOnScreen();
                    mouseY = e.getYOnScreen();

                    dragStartX = getX();
                    dragStartY = getY();

                    dragging = true;
                }
            }

//            @Override
//            public void mouseReleased(MouseEvent e) {
//                if (e.getButton() == MouseEvent.BUTTON1) {
//                    dragging = false;
//                    for (VertexActionListener listener : listeners) {
//                        listener.onVertexSelected(GraphVertex.this, e);
//                    }
//                }
//            }

        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    int deltaX = e.getXOnScreen() - mouseX;
                    int deltaY = e.getYOnScreen() - mouseY;

                    int newX = dragStartX + deltaX;
                    int newY = dragStartY + deltaY;
                    setLocation(newX, newY);

                    for (VertexActionListener listener : listeners) {
                        listener.onVertexPositionChanged();
                    }
                }

            }
        });
    }
    public int getCenteredX(){
        return getX()+radius;
    }
    public int getCenteredY(){
        return getY()+radius;        
    }
    private void initContextMenu() {
        contextMenu = new JPopupMenu();

//        JMenuItem addEdgeMI = new JMenuItem("Add edge");
//        addEdgeMI.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                for (VertexActionListener listener : listeners) {
//                    listener.onVertexSelected(GraphVertex.this, e);
//                }
//            }
//        });

        JMenuItem deleteVertexMI = new JMenuItem("Remove vertex");
        deleteVertexMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (VertexActionListener listener : listeners) {
                    listener.onDelete(GraphVertex.this);
                }
            }
        });

//        JSeparator sep1 = new JSeparator();
//        JMenuItem setSourceMI = new JMenuItem("Set source");
//        setSourceMI.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                for (VertexActionListener listener : listeners) {
//                    listener.onSourceChanged(GraphVertex.this);
//                }
//            }
//        });

//        JMenuItem setSinkMI = new JMenuItem("Set sink");
//        setSinkMI.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                for (VertexActionListener listener : listeners) {
//                    listener.onSinkChanged(GraphVertex.this);
//                }
//            }
//        });

//        contextMenu.add(addEdgeMI);
//        contextMenu.add(new JSeparator());
        contextMenu.add(deleteVertexMI);
//        contextMenu.add(sep1);
//        contextMenu.add(setSourceMI);
//        contextMenu.add(setSinkMI);

        this.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                showContextMenu(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                showContextMenu(e);
            }
        });
        //this.setComponentPopupMenu(contextMenu);

    }

    private void showContextMenu(MouseEvent e) {
        if (e.isPopupTrigger()) {
//            if (Setting.getInstance().getRunningMode() == Setting.MODE_GRAPH_DESIGN) {
                contextMenu.show(e.getComponent(), e.getX(), e.getY());
//            }

        }
    }

    public void addVertexChangedListener(VertexActionListener listener) {
        listeners.add(listener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        if (!this.selected) {
//            if (this.data.isSource()) {
//                g.setColor(Color.ORANGE);
//            } else if (this.data.isSink()) {
//                g.setColor(Color.CYAN);
//            } else {
                g.setColor(Color.GREEN);
//            }
//        } else {
//            g.setColor(Color.red);
//        }
        g.fillOval(0, 0, radius * 2, radius * 2);

        g.setColor(Color.darkGray);
        drawCenteredString(g, toString(), new Rectangle(0, 0, 2 * radius, 2 * radius), new Font("TimesRoman", Font.BOLD, 16));

    }

    public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    @Override
    public String toString() {
        return id + "";
//        if (data != null) {
//            return data.getName() + "";
//        } else {
//            return id + "";
//        }
    }

//    public Vertex getData() {
//        return data;
//    }
//
//    public void setData(Vertex data) {
//        this.data = data;
//    }

}
