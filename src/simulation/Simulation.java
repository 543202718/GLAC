/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;


import Jama.Matrix;
import glac.Config;
import glac.TagData;
import glac.HMM;
import utils.MyRandom;
import java.util.ArrayList;
import org.apache.commons.lang3.tuple.Pair;
import utils.MyUtils;

/**
 * 仿真测试类，用于评估系统的各项性能。
 *
 * @author Wang
 */
public class Simulation {

    static double sigma = 0.1;//仿真生成的相位的标准差
    static MyRandom random = new MyRandom();//随机数生成器

    public static ArrayList<Double>[][] track(Shape shape) {
        ArrayList<Double> lists[][] = new ArrayList[2][3];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                lists[i][j] = new ArrayList<>();
            }
        }
        HMM hmm = new HMM();
        for (int t = 0; t < 10; t++) {
            hmm.clear();
            int i = 0;
            ArrayList<StateStamp> g = shape.generate();
            for (StateStamp s : g) {
                double ph = genPhase(s.getStateVector().get(0, 0), s.getStateVector().get(1, 0), i);
                TagData td = new TagData(i, s.getTime(), ph);
                hmm.add(td);
                i = (i + 1) % Config.getK();
            }
            ArrayList<Pair<Double, Double>> tr = hmm.getTrajectory();
            ArrayList<Pair<Double, Double>> v = hmm.getVelocity();
            if (tr == null) {
                t--;
                continue;
            }
            for (int k = 0; k < g.size(); k++) {
                Matrix e = getError(tr.get(k), v.get(k), g.get(k).getStateVector());
                for (i = 0; i < 2; i++) {
                    for (int j = 0; j < 3; j++) {
                        lists[i][j].add(e.get(i, j));
                    }
                }
            }
        }
        return lists;
    }

    private static Matrix getError(Pair<Double, Double> p, Pair<Double, Double> v, Matrix g) {
        double mat[][] = new double[2][3];
        mat[0][0] = Math.abs(g.get(0, 0) - p.getLeft());
        mat[0][1] = Math.abs(g.get(1, 0) - p.getRight());
        mat[0][2] = MyUtils.dist(g.get(0, 0), g.get(1, 0), p.getLeft(), p.getRight());
        mat[1][0] = Math.abs(g.get(2, 0) - v.getLeft());
        mat[1][1] = Math.abs(g.get(3, 0) - v.getRight());
        mat[1][2] = MyUtils.dist(g.get(2, 0), g.get(3, 0), v.getLeft(), v.getRight());
        return new Matrix(mat);
    }

    /**
     * 指定标签的位置，生成指定天线的相位(模pi)
     *
     * @param x 标签X坐标
     * @param y 标签Y坐标
     * @param ano 天线标号
     * @return 相位
     */
    private static double genPhase(double x, double y, int ano) {
        double d = -MyUtils.dist(x, y, Config.getX(ano), Config.getY(ano)) * 2;
        double phase;
        phase = random.nextGaussian(d * Math.PI / Config.getSemiLambda(), sigma);
        phase = phase - Math.floor(phase / Math.PI) * Math.PI;
        return phase;
    }

}
