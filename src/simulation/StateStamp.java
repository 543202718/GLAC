/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import Jama.Matrix;

/**
 * 状态戳。记录时间、状态向量、协方差矩阵等信息。<br>
 * 状态向量是一个4*1矩阵，元素分别代表x方向位置、y方向位置、x方向速度、y方向速度。
 *
 * @author Wang
 */
public class StateStamp {

    private final long time;//以毫秒计的时间
    private final Matrix stateVector;//状态向量
    private final Matrix covMatrix;//协方差矩阵

    /**
     * 生成函数
     * @param time 时间戳
     * @param stateVector 状态向量
     * @param covMatrix 协方差矩阵
     */
    public StateStamp(long time, Matrix stateVector, Matrix covMatrix) {
        this.time = time;
        this.stateVector = stateVector;
        this.covMatrix = covMatrix;
    }

    /**
     * 获取时间戳
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * 获取状态向量
     * @return the stateVector
     */
    public Matrix getStateVector() {
        return stateVector;
    }

    /**
     * 获取协方差矩阵
     * @return the covMatrix
     */
    public Matrix getCovMatrix() {
        return covMatrix;
    }

}
