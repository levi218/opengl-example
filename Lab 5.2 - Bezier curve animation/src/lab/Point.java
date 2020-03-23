/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab;

/**
 *
 * @author theph
 */
public class Point {
    
    private double x, y;
    private double r,g,b,a;

    public Point normal;
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        this.r = Math.random();
        this.g = Math.random();
        this.b = Math.random();
        this.a = Math.random();
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
