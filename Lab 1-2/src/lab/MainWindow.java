/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainWindow extends javax.swing.JFrame implements GLEventListener {

    /**
     * Creates new form MainWindow
     */
    ArrayList<Point> points;

    private void initPointArray() {
        points = new ArrayList<>();
        points.add(new Point(-0.7, 0.5));
        points.add(new Point(-0.3, 0.7));

        points.add(new Point(0.2, 0.8));
        points.add(new Point(0.7, 0.5));

        points.add(new Point(0.9, -0.2));
        points.add(new Point(0.8, -0.7));

        points.add(new Point(0, -0.8));
        points.add(new Point(-0.7, -0.5));
        points.add(new Point(-0.9, 0));
    }

    int getShapeId() {
        switch (cbShapes.getSelectedIndex()) {
            default:
            case 0:
                return GL2.GL_POINTS;
            case 1:
                return GL2.GL_LINES;
            case 2:
                return GL2.GL_LINE_STRIP;
            case 3:
                return GL2.GL_LINE_LOOP;
            case 4:
                return GL2.GL_TRIANGLES;
            case 5:
                return GL2.GL_TRIANGLE_STRIP;
            case 6:
                return GL2.GL_TRIANGLE_FAN;
            case 7:
                return GL2.GL_QUADS;
            case 8:
                return GL2.GL_QUAD_STRIP;
            case 9:
                return GL2.GL_POLYGON;
        }
    }

    int getFunc() {
        switch (cbFunc.getSelectedIndex()) {
            default:
            case 0:
                return GL2.GL_NEVER;
            case 1:
                return GL2.GL_LESS;
            case 2:
                return GL2.GL_EQUAL;
            case 3:
                return GL2.GL_LEQUAL;
            case 4:
                return GL2.GL_GREATER;
            case 5:
                return GL2.GL_NOTEQUAL;
            case 6:
                return GL2.GL_GEQUAL;
            case 7:
                return GL2.GL_ALWAYS;
        }
    }

    int getSFactor() {
        switch (cbSFactor.getSelectedIndex()) {
            default:
            case 0:
                return GL2.GL_ZERO;
            case 1:
                return GL2.GL_ONE;
            case 2:
                return GL2.GL_DST_COLOR;
            case 3:
                return GL2.GL_ONE_MINUS_DST_COLOR;
            case 4:
                return GL2.GL_SRC_ALPHA;
            case 5:
                return GL2.GL_ONE_MINUS_SRC_ALPHA;
            case 6:
                return GL2.GL_DST_ALPHA;
            case 7:
                return GL2.GL_ONE_MINUS_DST_ALPHA;
            case 8:
                return GL2.GL_SRC_ALPHA_SATURATE;
        }
    }

    int getDFactor() {
        switch (cbDFactor.getSelectedIndex()) {
            default:
            case 0:
                return GL2.GL_ZERO;
            case 1:
                return GL2.GL_ONE;
            case 2:
                return GL2.GL_SRC_COLOR;
            case 3:
                return GL2.GL_ONE_MINUS_SRC_COLOR;
            case 4:
                return GL2.GL_SRC_ALPHA;
            case 5:
                return GL2.GL_ONE_MINUS_SRC_ALPHA;
            case 6:
                return GL2.GL_DST_ALPHA;
            case 7:
                return GL2.GL_ONE_MINUS_DST_ALPHA;
        }
    }

    final GLProfile gp = GLProfile.get(GLProfile.GL2);
    GLCapabilities cap = new GLCapabilities((gp));
    final GLJPanel panel = new GLJPanel(cap);

    public MainWindow() {
        initComponents();

        initPointArray(); // initiate points 
        
        // set maximum and default values for UI elements (sliders and comboboxes)
        panel.setSize(leftPanel.getWidth(), leftPanel.getHeight());
        sldX.setMaximum(leftPanel.getWidth());
        sldY.setMaximum(leftPanel.getHeight());
        sldWidth.setMaximum(leftPanel.getWidth());
        sldHeight.setMaximum(leftPanel.getHeight());
        sldRef.setMaximum(100);

        sldX.setValue(0);
        sldY.setValue(0);
        sldWidth.setValue(leftPanel.getWidth());
        sldHeight.setValue(leftPanel.getHeight());
        sldRef.setValue(0);
        cbFunc.setSelectedIndex(7);
        cbDFactor.setSelectedIndex(1);
        cbSFactor.setSelectedIndex(1);
        
        // add GL panel to left side and set event listener to this class
        panel.addGLEventListener(this);
        leftPanel.add(panel);
        
        ChangeListener clRedraw = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                redrawGLPanel();
            }
        };
        ActionListener alRedraw = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redrawGLPanel();
            }
        };
        
        cbShapes.addActionListener(alRedraw);
        cbFunc.addActionListener(alRedraw);
        cbSFactor.addActionListener(alRedraw);
        cbDFactor.addActionListener(alRedraw);

        sldX.addChangeListener(clRedraw);
        sldY.addChangeListener(clRedraw);
        sldWidth.addChangeListener(clRedraw);
        sldHeight.addChangeListener(clRedraw);
        sldRef.addChangeListener(clRedraw);
    }

    public void redrawGLPanel() {
        panel.repaint();
    }

    @Override
    public void init(GLAutoDrawable glad) {
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
    }

    @Override
    public void display(GLAutoDrawable glad) {
        // draw base on settings
        final GL2 gl = glad.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glEnable(GL2.GL_SCISSOR_TEST);
        gl.glScissor(sldX.getValue(), sldY.getValue(), sldWidth.getValue(), sldHeight.getValue());
        
        gl.glEnable(GL2.GL_ALPHA_TEST);
        gl.glAlphaFunc(getFunc(), sldRef.getValue()*0.01f);
        
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(getSFactor(), getDFactor());

        gl.glPointSize(4.0f);
        gl.glLineWidth(3.0f);

        gl.glBegin(getShapeId());
        points.forEach((point) -> {
            gl.glColor4d(point.getR(),point.getG(),point.getB(), point.getA());
            gl.glVertex2d(point.getX(), point.getY());
        });
        gl.glEnd();

        gl.glDisable(GL2.GL_SCISSOR_TEST);
        gl.glDisable(GL2.GL_ALPHA_TEST);
        gl.glDisable(GL2.GL_BLEND);

    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbShapes = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        cbFunc = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        sldRef = new javax.swing.JSlider();
        cbDFactor = new javax.swing.JComboBox<>();
        cbSFactor = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        sldX = new javax.swing.JSlider();
        sldY = new javax.swing.JSlider();
        sldWidth = new javax.swing.JSlider();
        sldHeight = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Nguyen Quang Huy - Lab 1-2");
        setResizable(false);

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 560, Short.MAX_VALUE)
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setText("Lab 1");

        cbShapes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GL_POINTS", "GL_LINES", "GL_LINE_STRIP", "GL_LINE_LOOP", "GL_TRIANGLES", "GL_TRIANGLE_STRIP", "GL_TRIANGLE_FAN", "GL_QUADS", "GL_QUAD_STRIP", "GL_POLYGON" }));

        jLabel2.setText("Lab 2");

        cbFunc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GL_NEVER", "GL_LESS", "GL_EQUAL", "GL_LEQUAL", "GL_GREATER", "GL_NOTEQUAL", "GL_GEQUAL", "GL_ALWAYS" }));

        jLabel3.setText("Тест отсечения");

        jLabel4.setText("Тест прозрачности");

        jLabel5.setText("Тест смешения цветов");

        cbDFactor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GL_ZERO", "GL_ONE", "GL_DST_COLOR", "GL_ONE_MINUS_DST_COLOR", "GL_SRC_ALPHA", "GL_ONE_MINUS_SRC_ALPHA", "GL_DST_ALPHA", "GL_ONE_MINUS_DST_ALPHA", "GL_SRC_ALPHA_SATURATE" }));

        cbSFactor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GL_ZERO", "GL_ONE", "GL_SRC_COLOR", "GL_ONE_MINUS_SRC_COLOR", "GL_SRC_ALPHA", "GL_ONE_MINUS_SRC_ALPHA", "GL_DST_ALPHA", "GL_ONE_MINUS_DST_ALPHA" }));

        jLabel6.setText("sfactor");

        jLabel7.setText("dfactor");

        jLabel8.setText("func");

        jLabel9.setText("ref");

        jLabel10.setText("x");

        jLabel11.setText("y");

        jLabel12.setText("width");

        jLabel13.setText("height");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbShapes, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbSFactor, 0, 1, Short.MAX_VALUE)
                            .addComponent(cbDFactor, 0, 1, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sldHeight, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(sldWidth, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(sldY, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(sldX, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sldRef, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(cbFunc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(cbShapes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel2)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel10))
                                            .addComponent(sldX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel11))
                                    .addComponent(sldY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12))
                            .addComponent(sldWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13))
                    .addComponent(sldHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbFunc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sldRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbSFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cbDFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(84, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(leftPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JComboBox<String> cbDFactor;
    private javax.swing.JComboBox<String> cbFunc;
    private javax.swing.JComboBox<String> cbSFactor;
    private javax.swing.JComboBox<String> cbShapes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JSlider sldHeight;
    private javax.swing.JSlider sldRef;
    private javax.swing.JSlider sldWidth;
    private javax.swing.JSlider sldX;
    private javax.swing.JSlider sldY;
    // End of variables declaration//GEN-END:variables

}
