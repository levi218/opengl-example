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

public class MainWindow extends javax.swing.JFrame implements GLEventListener {

    final GLProfile gp = GLProfile.get(GLProfile.GL2);
    GLCapabilities cap = new GLCapabilities((gp));
    final GLJPanel panel = new GLJPanel(cap);

    final Animator animator = new Animator(panel);

    JPopupMenu contextMenuPanel;

    private int shaderProg;

    float t = 0f;
    GLU glu = new GLU();

    @Override
    public void init(GLAutoDrawable glad) {
        final GL2 gl = glad.getGL().getGL2();

        this.shaderProg = gl.glCreateProgram();
        int vert = createShader(gl, "shaders/shader.vert", GL2.GL_VERTEX_SHADER);
        int frag = createShader(gl, "shaders/shader.frag", GL2.GL_FRAGMENT_SHADER);
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
    }

    void drawCylinder(final GL2 gl, double height, double radius, int verticalSegs, int roundSegs) {

        double seg_angle = 2 * Math.PI / roundSegs;
        double seg_height = height / verticalSegs;

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glNormal3d(0, 1, 0);
        for (int j = 0; j < roundSegs; j++) {
            gl.glVertex3d(radius * Math.cos(seg_angle * j), height, radius * Math.sin(seg_angle * j));
        }
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glNormal3d(0, -1, 0);
        for (int j = 0; j < roundSegs; j++) {
            gl.glVertex3d(radius * Math.cos(seg_angle * j), 0, radius * Math.sin(seg_angle * j));
        }
        gl.glEnd();

        for (int i = 0; i < verticalSegs; i++) {
            for (int j = 0; j < roundSegs; j++) {
                gl.glBegin(GL2.GL_LINE_LOOP);

                gl.glNormal3d(radius * Math.cos(seg_angle * j), 0, radius * Math.sin(seg_angle * j));
                gl.glVertex3d(radius * Math.cos(seg_angle * j), seg_height * (i + 1), radius * Math.sin(seg_angle * j));
                gl.glVertex3d(radius * Math.cos(seg_angle * (j + 1)), seg_height * (i + 1), radius * Math.sin(seg_angle * (j + 1)));
                gl.glVertex3d(radius * Math.cos(seg_angle * (j + 1)), seg_height * i, radius * Math.sin(seg_angle * (j + 1)));
                gl.glVertex3d(radius * Math.cos(seg_angle * j), seg_height * i, radius * Math.sin(seg_angle * j));

                gl.glEnd();
            }
        }
    }

    void drawEllipsoid(final GL2 gl, double a, double b, int roundSegs) {

        double seg_angle = 2 * Math.PI / roundSegs;
        for (int i = 0; i < roundSegs; i++) {
            gl.glBegin(GL2.GL_LINE_STRIP);
            for (double j = 0; j <= Math.PI; j += Math.PI / 20) {
                gl.glNormal3d(
                        a * Math.sin(j) * Math.cos(seg_angle * i),
                        b * Math.cos(j),
                        a * Math.sin(j) * Math.sin(seg_angle * i)
                );
                gl.glVertex3d(
                        a * Math.sin(j) * Math.cos(seg_angle * i),
                        b * Math.cos(j),
                        a * Math.sin(j) * Math.sin(seg_angle * i)
                );
            }
            gl.glEnd();
        }
    }

    @Override
    public void display(GLAutoDrawable glad) {
        final GL2 gl = glad.getGL().getGL2();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, panel.getSize().width / panel.getSize().height, 1.0, 200.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluLookAt(sliderCamX.getValue(), sliderCamY.getValue(), sliderCamZ.getValue(),
                0, 0, 0,
                0, 1, 0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        if (cbFaceCulling.isSelected()) {
            gl.glUseProgram(this.shaderProg);
        }
        int location = gl.glGetUniformLocation(this.shaderProg, "cameraPos");
        gl.glUniform3i(location, sliderCamX.getValue(), sliderCamY.getValue(), sliderCamZ.getValue());
        gl.glColor3d(0.95, 0.66, 0.5);
        gl.glPointSize(4.0f);
        gl.glLineWidth(3.0f);

//        gl.glRotated(sliderRotX.getValue() / 100.0 * 360, 1, 0, 0);
//        gl.glRotated(sliderRotY.getValue() / 100.0 * 360, 0, 1, 0);
//        gl.glRotated(sliderRotZ.getValue() / 100.0 * 360, 0, 0, 1);
        gl.glPushMatrix();

        gl.glPushMatrix();
        gl.glTranslated(sliderXCyl.getValue()/20.0, sliderYCyl.getValue()/20.0, sliderZCyl.getValue()/20.0);
        gl.glRotated(sliderRotXCyl.getValue() / 100.0 * 360, 1, 0, 0);
        gl.glRotated(sliderRotYCyl.getValue() / 100.0 * 360, 0, 1, 0);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
        drawCylinder(gl, sliderHeightCylinder.getValue(), sliderRadiusCylinder.getValue() / 3.0, sliderVerticalSegsCylinder.getValue(), sliderRoundSegsCylinder.getValue());
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslated(sliderXEli.getValue()/20.0, sliderYEli.getValue()/20.0, sliderZEli.getValue()/20.0);
        gl.glRotated(sliderRotXEli.getValue() / 100.0 * 360, 1, 0, 0);
        gl.glRotated(sliderRotYEli.getValue() / 100.0 * 360, 0, 1, 0);
        drawEllipsoid(gl, sliderRadiusEllipsoid.getValue(), sliderHeightEllipsoid.getValue(), sliderRoundSegsEllipsoid.getValue());
        gl.glPopMatrix();

        gl.glPopMatrix();
        gl.glUseProgram(0);

        gl.glBegin(GL2.GL_LINES);
        gl.glColor3d(255, 0, 0); // x - red
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(20, 0, 0);

        gl.glColor3d(0, 255, 0); // y - green
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(0, 20, 0);

        gl.glColor3d(0, 0, 255); // z - blue
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(0, 0, 20);
        gl.glEnd();
    }

    public MainWindow() {
        initComponents();

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

        animator.setRunAsFastAsPossible(true);

        animator.start();

        leftPanel.repaint();

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
        jPanel1 = new javax.swing.JPanel();
        cbFaceCulling = new javax.swing.JCheckBox();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel1 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel5 = new javax.swing.JLabel();
        sliderCamX = new javax.swing.JSlider();
        jLabel6 = new javax.swing.JLabel();
        sliderCamY = new javax.swing.JSlider();
        jLabel7 = new javax.swing.JLabel();
        sliderCamZ = new javax.swing.JSlider();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel4 = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel18 = new javax.swing.JLabel();
        sliderXCyl = new javax.swing.JSlider();
        jLabel19 = new javax.swing.JLabel();
        sliderYCyl = new javax.swing.JSlider();
        jLabel26 = new javax.swing.JLabel();
        sliderZCyl = new javax.swing.JSlider();
        jLabel22 = new javax.swing.JLabel();
        sliderRotXCyl = new javax.swing.JSlider();
        jLabel23 = new javax.swing.JLabel();
        sliderRotYCyl = new javax.swing.JSlider();
        jLabel11 = new javax.swing.JLabel();
        sliderHeightCylinder = new javax.swing.JSlider();
        jLabel17 = new javax.swing.JLabel();
        sliderRadiusCylinder = new javax.swing.JSlider();
        jLabel12 = new javax.swing.JLabel();
        sliderRoundSegsCylinder = new javax.swing.JSlider();
        jLabel13 = new javax.swing.JLabel();
        sliderVerticalSegsCylinder = new javax.swing.JSlider();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel3 = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel20 = new javax.swing.JLabel();
        sliderXEli = new javax.swing.JSlider();
        jLabel21 = new javax.swing.JLabel();
        sliderYEli = new javax.swing.JSlider();
        jLabel27 = new javax.swing.JLabel();
        sliderZEli = new javax.swing.JSlider();
        jLabel24 = new javax.swing.JLabel();
        sliderRotXEli = new javax.swing.JSlider();
        jLabel25 = new javax.swing.JLabel();
        sliderRotYEli = new javax.swing.JSlider();
        jLabel15 = new javax.swing.JLabel();
        sliderRadiusEllipsoid = new javax.swing.JSlider();
        jLabel16 = new javax.swing.JLabel();
        sliderHeightEllipsoid = new javax.swing.JSlider();
        jLabel14 = new javax.swing.JLabel();
        sliderRoundSegsEllipsoid = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Nguyen QH - Nguyen TTD - Lab 5 - Bezier Curve Animation");

        leftPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        leftPanel.setForeground(new java.awt.Color(204, 0, 51));

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 774, Short.MAX_VALUE)
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel1.setLayout(new java.awt.GridLayout(30, 2));

        cbFaceCulling.setText("Face Culling");
        jPanel1.add(cbFaceCulling);
        jPanel1.add(filler11);

        jLabel1.setText("Camera");
        jPanel1.add(jLabel1);
        jPanel1.add(filler1);

        jLabel5.setText("x");
        jPanel1.add(jLabel5);

        sliderCamX.setMaximum(15);
        sliderCamX.setMinimum(-10);
        sliderCamX.setToolTipText("");
        sliderCamX.setValue(10);
        jPanel1.add(sliderCamX);

        jLabel6.setText("y");
        jPanel1.add(jLabel6);

        sliderCamY.setMaximum(15);
        sliderCamY.setMinimum(-10);
        sliderCamY.setToolTipText("");
        sliderCamY.setValue(10);
        jPanel1.add(sliderCamY);

        jLabel7.setText("z");
        jPanel1.add(jLabel7);

        sliderCamZ.setMaximum(15);
        sliderCamZ.setMinimum(-10);
        sliderCamZ.setToolTipText("");
        sliderCamZ.setValue(10);
        jPanel1.add(sliderCamZ);
        jPanel1.add(filler5);
        jPanel1.add(filler6);

        jLabel4.setText("Cylinder");
        jPanel1.add(jLabel4);
        jPanel1.add(filler3);

        jLabel18.setText("x");
        jPanel1.add(jLabel18);

        sliderXCyl.setMinimum(-100);
        sliderXCyl.setToolTipText("");
        sliderXCyl.setValue(0);
        jPanel1.add(sliderXCyl);

        jLabel19.setText("y");
        jPanel1.add(jLabel19);

        sliderYCyl.setMinimum(-100);
        sliderYCyl.setToolTipText("");
        sliderYCyl.setValue(0);
        jPanel1.add(sliderYCyl);

        jLabel26.setText("z");
        jPanel1.add(jLabel26);

        sliderZCyl.setMinimum(-100);
        sliderZCyl.setToolTipText("");
        sliderZCyl.setValue(0);
        jPanel1.add(sliderZCyl);

        jLabel22.setText("rotX");
        jPanel1.add(jLabel22);

        sliderRotXCyl.setToolTipText("");
        sliderRotXCyl.setValue(0);
        jPanel1.add(sliderRotXCyl);

        jLabel23.setText("rotY");
        jPanel1.add(jLabel23);

        sliderRotYCyl.setToolTipText("");
        sliderRotYCyl.setValue(0);
        jPanel1.add(sliderRotYCyl);

        jLabel11.setText("height");
        jPanel1.add(jLabel11);

        sliderHeightCylinder.setMaximum(5);
        sliderHeightCylinder.setMinimum(2);
        sliderHeightCylinder.setToolTipText("");
        sliderHeightCylinder.setValue(3);
        jPanel1.add(sliderHeightCylinder);

        jLabel17.setText("radius");
        jPanel1.add(jLabel17);

        sliderRadiusCylinder.setMaximum(10);
        sliderRadiusCylinder.setMinimum(2);
        sliderRadiusCylinder.setToolTipText("");
        sliderRadiusCylinder.setValue(6);
        jPanel1.add(sliderRadiusCylinder);

        jLabel12.setText("circular segments");
        jPanel1.add(jLabel12);

        sliderRoundSegsCylinder.setMaximum(50);
        sliderRoundSegsCylinder.setMinimum(10);
        sliderRoundSegsCylinder.setToolTipText("");
        sliderRoundSegsCylinder.setValue(25);
        jPanel1.add(sliderRoundSegsCylinder);

        jLabel13.setText("vertical segments");
        jPanel1.add(jLabel13);

        sliderVerticalSegsCylinder.setMaximum(15);
        sliderVerticalSegsCylinder.setToolTipText("");
        sliderVerticalSegsCylinder.setValue(7);
        jPanel1.add(sliderVerticalSegsCylinder);
        jPanel1.add(filler9);
        jPanel1.add(filler10);

        jLabel3.setText("Ellipsoid");
        jPanel1.add(jLabel3);
        jPanel1.add(filler4);

        jLabel20.setText("x");
        jPanel1.add(jLabel20);

        sliderXEli.setMinimum(-100);
        sliderXEli.setToolTipText("");
        sliderXEli.setValue(80);
        jPanel1.add(sliderXEli);

        jLabel21.setText("y");
        jPanel1.add(jLabel21);

        sliderYEli.setMinimum(-100);
        sliderYEli.setToolTipText("");
        sliderYEli.setValue(0);
        jPanel1.add(sliderYEli);

        jLabel27.setText("z");
        jPanel1.add(jLabel27);

        sliderZEli.setMinimum(-100);
        sliderZEli.setToolTipText("");
        sliderZEli.setValue(0);
        jPanel1.add(sliderZEli);

        jLabel24.setText("rotX");
        jPanel1.add(jLabel24);

        sliderRotXEli.setToolTipText("");
        sliderRotXEli.setValue(0);
        jPanel1.add(sliderRotXEli);

        jLabel25.setText("rotY");
        jPanel1.add(jLabel25);

        sliderRotYEli.setToolTipText("");
        sliderRotYEli.setValue(0);
        jPanel1.add(sliderRotYEli);

        jLabel15.setText("radius");
        jPanel1.add(jLabel15);

        sliderRadiusEllipsoid.setMaximum(5);
        sliderRadiusEllipsoid.setMinimum(2);
        sliderRadiusEllipsoid.setToolTipText("");
        sliderRadiusEllipsoid.setValue(2);
        jPanel1.add(sliderRadiusEllipsoid);

        jLabel16.setText("height");
        jPanel1.add(jLabel16);

        sliderHeightEllipsoid.setMaximum(3);
        sliderHeightEllipsoid.setMinimum(1);
        sliderHeightEllipsoid.setToolTipText("");
        sliderHeightEllipsoid.setValue(1);
        jPanel1.add(sliderHeightEllipsoid);

        jLabel14.setText("circular segments");
        jPanel1.add(jLabel14);

        sliderRoundSegsEllipsoid.setMaximum(50);
        sliderRoundSegsEllipsoid.setMinimum(10);
        sliderRoundSegsEllipsoid.setToolTipText("");
        sliderRoundSegsEllipsoid.setValue(20);
        jPanel1.add(sliderRoundSegsEllipsoid);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    private javax.swing.JCheckBox cbFaceCulling;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JSlider sliderCamX;
    private javax.swing.JSlider sliderCamY;
    private javax.swing.JSlider sliderCamZ;
    private javax.swing.JSlider sliderHeightCylinder;
    private javax.swing.JSlider sliderHeightEllipsoid;
    private javax.swing.JSlider sliderRadiusCylinder;
    private javax.swing.JSlider sliderRadiusEllipsoid;
    private javax.swing.JSlider sliderRotXCyl;
    private javax.swing.JSlider sliderRotXEli;
    private javax.swing.JSlider sliderRotYCyl;
    private javax.swing.JSlider sliderRotYEli;
    private javax.swing.JSlider sliderRoundSegsCylinder;
    private javax.swing.JSlider sliderRoundSegsEllipsoid;
    private javax.swing.JSlider sliderVerticalSegsCylinder;
    private javax.swing.JSlider sliderXCyl;
    private javax.swing.JSlider sliderXEli;
    private javax.swing.JSlider sliderYCyl;
    private javax.swing.JSlider sliderYEli;
    private javax.swing.JSlider sliderZCyl;
    private javax.swing.JSlider sliderZEli;
    // End of variables declaration//GEN-END:variables

}
