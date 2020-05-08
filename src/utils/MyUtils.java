package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wang
 */
public class MyUtils {

    private static double ndis[];

    private static void prepareNormal() {
        try {
            ndis = new double[7000];
            Scanner sc = new Scanner(new File("NormalDistribution.txt"));
            for (int i = 0; i < 7000; i++) {
                ndis[i] = sc.nextDouble();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MyUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 返回均值为0，方差为1的标准正态密度函数在p处的值
     *
     * @param p 函数自变量取值
     * @return 标准正态密度函数值
     */
    public static double getStandardNormal(double p) {
        if (ndis == null) {
            prepareNormal();
        }
        double f = 0;
        p = Math.abs(p);
        if (p <= 6.8) {
            int i = (int) Math.floor(p * 1000.0);
            f = p - i / 1000.0;
            f = f * ndis[i + 1] + (1 - f) * ndis[i];//线性插值
        }
        return f;
    }

    /**
     * 返回均值为mu，标准差为sigma的正态分布密度函数在p处的值
     *
     * @param mu 均值
     * @param sigma 标准差
     * @param p 函数自变量取值
     * @return 正态密度函数值
     */
    public static double getNormalDistribution(double mu, double sigma, double p) {
        return getStandardNormal((p - mu) / sigma);
    }

    /**
     * 计算两个点的欧氏距离
     *
     * @param x1 第一个点的X坐标
     * @param y1 第一个点的Y坐标
     * @param x2 第二个点的X坐标
     * @param y2 第二个点的Y坐标
     * @return 欧氏距离
     */
    public static double dist(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

}
