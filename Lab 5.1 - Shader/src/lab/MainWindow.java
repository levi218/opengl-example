/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab;

import com.jogamp.opengl.util.Animator;
import com.sun.prism.impl.BufferUtil;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainWindow extends javax.swing.JFrame implements GLEventListener {

    final GLProfile gp = GLProfile.get(GLProfile.GL2);
    GLCapabilities cap = new GLCapabilities((gp));
    final GLJPanel panel = new GLJPanel(cap);

    private int shaderProg;

    float t = 0f;
    GLU glu = new GLU();

    @Override
    public void init(GLAutoDrawable glad) {
        final GL2 gl = glad.getGL().getGL2();

        this.shaderProg = gl.glCreateProgram();
        int vert = createShader(gl, "shaders/wave_1.vert", GL2.GL_VERTEX_SHADER);
        int frag = createShader(gl, "shaders/wave_1.frag", GL2.GL_FRAGMENT_SHADER);
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
        if(ib.get()==GL2.GL_FALSE){
            IntBuffer maxLength = IntBuffer.allocate(10);
            gl.glGetShaderiv(shader, GL2.GL_INFO_LOG_LENGTH, maxLength);
            
            int length = maxLength.get();
            ByteBuffer errLog = ByteBuffer.allocate(length);
            gl.glGetShaderInfoLog(shader, length, maxLength, errLog);
            
            System.out.println("err: "+new String(errLog.array(), StandardCharsets.UTF_8));
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
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 1.0, 100.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluLookAt(-1, 10, -10, 0, 0, 0, 0, 1, 0);
    }

    @Override
    public void display(GLAutoDrawable glad) {
        // draw base on settings
        t += 0.01f;
        final GL2 gl = glad.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glUseProgram(this.shaderProg);

        int location = gl.glGetUniformLocation(this.shaderProg, "time");
        gl.glUniform1f(location, t);

        gl.glColor3d(0.96, 0.66, 0.5);
        gl.glPointSize(4.0f);
        gl.glLineWidth(3.0f);
        gl.glBegin(GL2.GL_TRIANGLE_STRIP);

        for (float x = -5f; x <= 5f; x += 0.1f) {
            gl.glVertex3f(x, 0, -5);
            gl.glVertex3f(x, 0, 5);
        }

        gl.glEnd();

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

        final Animator animator = new Animator(panel);
        animator.setRunAsFastAsPossible(true);
        animator.start();
//        panel.repaint();
        leftPanel.repaint();

    }
    private int lastId = 0;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        leftPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Nguyen QH - Nguyen TTD - Lab 5 - Shader Animation");

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
            .addGap(0, 517, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
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
    private javax.swing.JPanel leftPanel;
    // End of variables declaration//GEN-END:variables

}
