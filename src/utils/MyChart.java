package utils;

import java.util.ArrayList;
import java.util.Collections;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Wang
 */
public class MyChart {

    /**
     * 使用指定的数据创建XYSeries
     *
     * @param lineName 数据标签
     * @param x X轴数据
     * @param y Y轴数据
     * @return 创建好的XYSeries
     */
    private static XYSeries createXYSeries(String lineName, double x[], double y[]) {
        XYSeries serie = new XYSeries(lineName);
        int n = x.length;
        for (int i = 0; i < n; i++) {
            serie.add(x[i], y[i]);
        }
        return serie;
    }

    public static ChartPanel cdfPlot(String title, String axisLabel, String[] legends, ArrayList<Double>[] data) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        int m = data.length;
        double max = data[0].get(0);
        for (int i = 0; i < m; i++) {
            Collections.sort(data[i]);//排序原始数据
            int n = data[i].size();
            double x[] = new double[n + 1], y[] = new double[n + 1];
            x[0] = data[i].get(0);
            y[0] = 0;
            for (int j = 1; j <= n; j++) {
                y[j] = j / (n * 1.0);
                x[j] = data[i].get(j - 1);
            }
            max = Math.max(max, data[i].get(n - 1));
            //组织数据
            if (i < legends.length) {
                dataset.addSeries(createXYSeries(legends[i], x, y));
            } else {
                dataset.addSeries(createXYSeries("Line" + i, x, y));
            }
        }
        JFreeChart chart = ChartFactory.createXYLineChart(title, axisLabel, "CDF", dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chart.getXYPlot().getDomainAxis().setUpperBound(max);//设置X轴范围
        chart.getXYPlot().getRangeAxis().setUpperBound(1.0);//设置Y轴范围
        return chartPanel;
    }

}
