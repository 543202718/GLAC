/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import org.apache.commons.lang3.tuple.Pair;

public class TrajectoryPanel extends javax.swing.JPanel {

    ArrayList<Pair<Double, Double>> tr;

    /**
     * Creates new form TestFrame
     *
     * @param tr
     */
    public TrajectoryPanel(ArrayList<Pair<Double, Double>> tr) {
        this.tr = tr;
        this.setPreferredSize(new java.awt.Dimension(500, 500));
        this.setVisible(true);
    }

    /**
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        int x0 = 30, xn = 480, y0 = 480, yn = 30;
        g.setColor(Color.BLACK);
        for (double t = 0; t <= 1; t += 0.1) {
            g.drawLine((int) (x0 + (xn - x0) * t), yn, (int) (x0 + (xn - x0) * t), y0);//绘制纵向网格线
            g.drawLine(x0, (int) (yn + (y0 - yn) * t), xn, (int) (yn + (y0 - yn) * t));//绘制横向网格线
        }
        double xsize = (xn - x0) * 0.01, ysize = (yn - y0) * 0.01;
        char chars[];
        for (int i = 0; i <= 100; i += 10) {
            chars = Integer.toString(i).toCharArray();
            g.drawChars(chars, 0, chars.length, (int) (x0 + xsize * i - 5), (int) (y0 + 15));
            g.drawChars(chars, 0, chars.length, (int) (x0 - 25), (int) (y0 + ysize * i + 5));
        }
        g.setColor(Color.red);
        if (tr != null) {
            for (int i = 1; i < tr.size(); i++) {
                Pair<Double, Double> p1, p2;
                p1 = tr.get(i);
                p2 = tr.get(i - 1);
                int x1 = (int) (p1.getLeft() * xsize + x0);
                int y1 = (int) (p1.getRight() * ysize + y0);
                int x2 = (int) (p2.getLeft() * xsize + x0);
                int y2 = (int) (p2.getRight() * ysize + y0);
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }
}
