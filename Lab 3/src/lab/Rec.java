/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab;

import javax.media.opengl.GL2;

/**
 *
 * @author theph
 */
public class Rec {

    private double x, y, w, h, alpha;
    private double r, g, b, a;

    public Rec(double x, double y, double w, double h, double alpha) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.alpha = alpha;
        this.r = Math.random();
        this.g = Math.random();
        this.b = Math.random();
        this.a = 0.5+Math.random()/2;
    }

    public Rec cpyAndTransform(double offsetX, double offsetY, double kx, double ky, double da) {
        double x_dest = Math.cos(da) * this.x + Math.sin(da) * this.y + offsetX;
        double y_dest = -Math.sin(da) * this.x + Math.cos(da) * this.y + offsetY;
        return new Rec(x_dest, y_dest, w * kx, h * ky, alpha + da);
    }

    public void draw(GL2 gl) {
        double[] x_arr = new double[]{
            -w / 2,
            -w / 2,
            +w / 2,
            +w / 2};
        double[] y_arr = new double[]{
            -h / 2,
            +h / 2,
            +h / 2,
            -h / 2
        };
//        double[] x_dest = new double[4];
//        double[] y_dest = new double[4];
        gl.glColor4d(this.r, this.g, this.b, this.a);
        gl.glBegin(GL2.GL_QUADS);
        for (int i = 0; i < 4; i++) {
            double x_dest = Math.cos(this.alpha) * x_arr[i] + Math.sin(this.alpha) * y_arr[i] + this.x;
            double y_dest = -Math.sin(this.alpha) * x_arr[i] + Math.cos(this.alpha) * y_arr[i] + this.y;
            gl.glVertex2d(x_dest, y_dest);
        }
        gl.glEnd();
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getR() {
        return r;
    }

    public double getG() {
        return g;
    }

    public double getB() {
        return b;
    }

    public double getA() {
        return a;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

}
