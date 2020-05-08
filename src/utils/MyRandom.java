package utils;

import java.util.Random;

/**
 *
 * @author Wang
 */
public class MyRandom extends Random {

    /**
     * 返回一个一维高斯分布随机变量
     *
     * @param mu 均值
     * @param sigma 标准差
     * @return 随机变量
     */
    public double nextGaussian(double mu, double sigma) {
        return mu + sigma * nextGaussian();
    }

    /**
     * 返回一个在x和y之间均匀分布的随机变量
     * @param x 边界
     * @param y 边界
     * @return 随机变量
     */
    public double nextDouble(double x, double y) {
        double min = Math.min(x, y);
        double max = Math.max(x, y);
        return (max - min) * nextDouble() + min;
    }

    /**
     * 返回在[lower,upper)内均匀分布的随机整数
     *
     * @param lower 下界
     * @param upper 上界
     * @return 随机整数
     */
    public int nextInt(int lower, int upper) {
        return lower + nextInt(upper - lower);
    }

}
