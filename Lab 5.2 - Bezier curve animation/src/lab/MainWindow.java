/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab;

import com.jogamp.opengl.util.Animator;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainWindow extends javax.swing.JFrame implements GLEventListener, VertexActionListener {

    final GLProfile gp = GLProfile.get(GLProfile.GL2);
    GLCapabilities cap = new GLCapabilities((gp));
    final GLJPanel panel = new GLJPanel(cap);

    final Animator animator = new Animator(panel);

    JPopupMenu contextMenuPanel;
    ArrayList<GraphVertex> points;

    private int shaderProg;

    float t = 0f;
    GLU glu = new GLU();

    public Point getPointBezier(List<Point> P, int start, int end, double t) {
        if (start + 1 == end) {
            return P.get(start);
        } else {
            Point p0 = getPointBezier(P, start, end - 1, t);
            Point p1 = getPointBezier(P, start + 1, end, t);
            double x = (1 - t) * p0.getX() + t * p1.getX();
            double y = (1 - t) * p0.getY() + t * p1.getY();
            return new Point(x, y);
        }
    }

    @Override
    public void onVertexPositionChanged() {
        reevaluatePoints();
        panel.repaint();
    }

    @Override
    public void init(GLAutoDrawable glad) {
        final GL2 gl = glad.getGL().getGL2();

        this.shaderProg = gl.glCreateProgram();
        int vert = createShader(gl, "shaders/wave_2.vert", GL2.GL_VERTEX_SHADER);
        int frag = createShader(gl, "shaders/wave_2.frag", GL2.GL_FRAGMENT_SHADER);
        gl.glAttachShader(shaderProg, vert);
        gl.glAttachShader(shaderProg, frag);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLinkProgram(shaderProg);
        gl.glValidateProgram(shaderProg);

        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glEnable(GL2.GL_DEPTH_TEST);
    }

    int createShader(GL2 gl, String path, int type) {
        int shader = gl.glCreateShader(type);
        if (shader == 0) {
            System.out.println("Cant create shader");
            return 0;
        }
        String[] code = new String[1];
        try {
            code[0] = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        gl.glShaderSource(shader, 1, code, null);
        gl.glCompileShader(shader);

        // error checking
        IntBuffer ib = IntBuffer.allocate(10);
        gl.glGetShaderiv(shader, GL2.GL_COMPILE_STATUS, ib);
        if (ib.get() == GL2.GL_FALSE) {
            IntBuffer maxLength = IntBuffer.allocate(10);
            gl.glGetShaderiv(shader, GL2.GL_INFO_LOG_LENGTH, maxLength);

            int length = maxLength.get();
            ByteBuffer errLog = ByteBuffer.allocate(length);
            gl.glGetShaderInfoLog(shader, length, maxLength, errLog);

            System.out.println("err: " + new String(errLog.array(), StandardCharsets.UTF_8));
        }
        return shader;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        if (height <= 0) {
            height = 1;
        }

        final float h = (float) width / (float) height;

        gl.glClearColor(0, 0, 0, 0);
        gl.glClearDepth(1);
        gl.glViewport(0, 0, width, height);
        gl.glLoadIdentity();
        gl.glOrtho(0, panel.getSize().width, panel.getSize().height, 0, -1, 1);
    }

    ArrayList<Point> bezierPoints;

    private void reevaluatePoints() {
        bezierPoints = new ArrayList<>();
        List<Point> P = points.stream().map((e) -> {
            return new Point(e.getCenteredX(), e.getCenteredY());
        }).collect(Collectors.toList());

        for (double t = 0; t <= 1; t += 0.005) {
            Point result = getPointBezier(P, 0, P.size(), t);
            bezierPoints.add(result);
        }
    }

    @Override
    public void display(GLAutoDrawable glad) {
        t += 0.01f;

        final GL2 gl = glad.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        if (btnMode.getText().equals("Stop")) {
            gl.glUseProgram(this.shaderProg);

            int location = gl.glGetUniformLocation(this.shaderProg, "time");
            gl.glUniform1f(location, t);
        }
        gl.glColor3d(0.95, 0.66, 0.5);
        gl.glPointSize(4.0f);
        gl.glLineWidth(3.0f);
        gl.glBegin(GL2.GL_LINE_STRIP);
        bezierPoints.forEach((point) -> {
            gl.glVertex2d(point.getX(), point.getY());
        });
        gl.glEnd();

        gl.glUseProgram(0);
    }

    public MainWindow() {
        initComponents();
        initContextMenuPanel();
        leftPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    showPanelContextMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    showPanelContextMenu(e);
                }
            }

        });
        leftPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.setSize(leftPanel.getWidth(), leftPanel.getHeight());
                panel.repaint();
                super.componentResized(e);
            }

        });
        // add GL panel to left side and set event listener to this class
        panel.setSize(leftPanel.getWidth(), leftPanel.getHeight());
        panel.addGLEventListener(this);
        leftPanel.add(panel);

        points = new ArrayList<>();

        addPoint(100, 100);
        addPoint(200, 100);
        addPoint(200, 200);
        addPoint(400, 100);
        this.reevaluatePoints();

        animator.setRunAsFastAsPossible(true);

        btnMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnMode.getText().equals("Run")) {
                    // not running
                    btnMode.setText("Stop");
                    animator.start();
                } else {
                    btnMode.setText("Run");
                    animator.stop();
                    leftPanel.repaint();
                }
            }
        });

        leftPanel.repaint();

    }
    private int lastId = 0;

    private void initContextMenuPanel() {
        contextMenuPanel = new JPopupMenu();
        contextMenuPanel.setName("contextMenuPanel");
        JMenuItem addVertexMI = new JMenuItem("Add vertex");
        addVertexMI.setName("addVertexMI");
        addVertexMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.awt.Point pos = leftPanel.getMousePosition();
                addPoint(pos.x, pos.y);
            }
        });

        contextMenuPanel.add(addVertexMI);
        this.add(contextMenuPanel);
    }

    private void addPoint(int x, int y) {
        GraphVertex gv = new GraphVertex(leftPanel, x, y, lastId);
        points.add(gv);
        gv.addVertexChangedListener(this);
        leftPanel.add(gv, 0);
        lastId += 1;

        reevaluatePoints();
        leftPanel.repaint();
    }

    @Override
    public void onDelete(GraphVertex v) {
        points.remove(v);
        leftPanel.remove(v);
        leftPanel.repaint();
    }

    private void showPanelContextMenu(MouseEvent e) {
        if (e.isPopupTrigger()) {
            contextMenuPanel.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        leftPanel = new javax.swing.JPanel();
        btnMode = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Nguyen QH - Nguyen TTD - Lab 5 - Bezier Curve Animation");

        leftPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        leftPanel.setForeground(new java.awt.Color(204, 0, 51));

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 879, Short.MAX_VALUE)
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 520, Short.MAX_VALUE)
        );

        btnMode.setText("Run");

        jLabel1.setText("Нажмите правую кнопку мыши на поверхности, чтобы добавить точку");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnMode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMode)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void dispose(GLAutoDrawable glad) {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMode;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel leftPanel;
    // End of variables declaration//GEN-END:variables

}
