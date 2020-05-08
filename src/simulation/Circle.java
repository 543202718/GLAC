/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import Jama.Matrix;

import java.util.ArrayList;
import static simulation.Simulation.random;

/**
 *
 * @author Wang
 */
public class Circle extends Shape {

    double r;
    double w;
    long time;

    public Circle(double r, double w, long time) {
        this.r = r;
        this.w = w;
        this.time = time;
    }

    /**
     * 生成匀速圆周运动的状态序列
     *
     * @return
     * 描述运动状态的序列，序列的每个元素是一个5*1的列向量，五个元素分别代表时间、X方向位置、Y方向位置、X方向速度、Y方向速度，单位为ms、cm和cm/s
     */
    @Override
    public ArrayList<StateStamp> generate() {
        ArrayList<StateStamp> statelist = new ArrayList<>();
        double x0 = random.nextDouble(30, 80);
        double y0 = random.nextDouble(30, 80);
        double x = x0 + r, y = y0, th = 0;
        double ww = w / 180.0 * Math.PI;//角度制转为弧度制
        for (long t = 0; t <= time; t += 25) {
            double mat[][] = {{x}, {y}, {-ww * r * Math.sin(th)}, {ww * r * Math.cos(th)}};
            statelist.add(new StateStamp(t, new Matrix(mat), null));
            th = th + ww / 40.0;
            x = x0 + r * Math.cos(th);
            y = y0 + r * Math.sin(th);
        }
        return statelist;
    }
}
