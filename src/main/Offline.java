/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import Jama.Matrix;
import glac.HMM;
import glac.TagData;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.commons.lang3.tuple.Pair;
import utils.MyUtils;

/**
 * 离线追踪
 *
 * @author Wang
 */
public class Offline {

    /**
     * 对指定的文件进行离线追踪
     *
     * @param f 保存有标签数据的文件
     * @return 追踪得到的轨迹
     * @throws java.lang.Exception
     */
    public static ArrayList<Pair<Double, Double>> offlineTrack(File f) throws Exception {
        return track(f).getTrajectory();
    }

    /**
     * 对指定文件夹下的标签数据文件进行离线追踪，并与基准数据进行对比，得到各个维度的误差
     * @param tagDir 保存有标签数据的文件夹
     * @param gDir 保存有基准数据的文件夹
     * @return 2*3的二维数组，表示各个维度的误差，分别是X方向的位置误差、Y方向的位置误差、总的位置误差、X方向的速度误差、Y方向的速度误差和总的速度误差。
     * @throws Exception 
     */
    public static ArrayList<Double>[][] batch(File tagDir, File gDir) throws Exception {
        ArrayList<Double> lists[][] = new ArrayList[2][3];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                lists[i][j] = new ArrayList<>();
            }
        }
        File tagFiles[] = tagDir.listFiles();
        File gFiles[] = gDir.listFiles();
        for (File f : tagFiles) {
            HMM hmm = track(f);
            ArrayList<Pair<Double, Double>> tr = hmm.getTrajectory();
            ArrayList<Pair<Double, Double>> v = hmm.getVelocity();
            File g = findGroundtruthFile(f.getName(), gFiles);
            ArrayList<Matrix> glist = getGroundtruth(g);
            for (int k = 0; k < glist.size(); k++) {
                Matrix e = getError(tr.get(k), v.get(k), glist.get(k));
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 3; j++) {
                        lists[i][j].add(e.get(i, j));
                    }
                }
            }
        }
        return lists;
    }

    private static HMM track(File f) throws FileNotFoundException {
        Scanner sc = new Scanner(f);
        sc.useDelimiter("\\s*(,|\\r|\\n)\\s*");//设置分隔符，以逗号或回车分隔，前后可以有若干个空白符
        HMM hmm = new HMM();
        while (sc.hasNext()) {
            int k = sc.nextInt();//天线编号
            long t = sc.nextLong();//时间戳
            double ph = sc.nextDouble();//相位（弧度）                
            hmm.add(new TagData(k, t, ph));//将数据提供给滤波器
        }
        sc.close();
        return hmm;
    }

    private static ArrayList<Matrix> getGroundtruth(File f) throws FileNotFoundException {
        ArrayList<Matrix> list = new ArrayList<>();
        Scanner sc = new Scanner(f);
        sc.useDelimiter("\\s*(,|\\r|\\n)\\s*");
        while (sc.hasNext()) {
            double x = sc.nextDouble(), y = sc.nextDouble(), vx = sc.nextDouble(), vy = sc.nextDouble();
            double mat[][] = {{x}, {y}, {vx}, {vy}};
            list.add(new Matrix(mat));
        }
        sc.close();
        return list;
    }

    private static File findGroundtruthFile(String name, File files[]) {
        for (File f : files) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
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
}
