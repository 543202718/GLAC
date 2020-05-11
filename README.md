# GLAC
- [GLAC](#glac)
  - [简介](#%e7%ae%80%e4%bb%8b)
  - [快速开始](#%e5%bf%ab%e9%80%9f%e5%bc%80%e5%a7%8b)
    - [第一步：获取源码](#%e7%ac%ac%e4%b8%80%e6%ad%a5%e8%8e%b7%e5%8f%96%e6%ba%90%e7%a0%81)
    - [第二步：导入工程](#%e7%ac%ac%e4%ba%8c%e6%ad%a5%e5%af%bc%e5%85%a5%e5%b7%a5%e7%a8%8b)
    - [第三步：运行](#%e7%ac%ac%e4%b8%89%e6%ad%a5%e8%bf%90%e8%a1%8c)
  - [功能](#%e5%8a%9f%e8%83%bd)
    - [在线追踪(Online Tracking)](#%e5%9c%a8%e7%ba%bf%e8%bf%bd%e8%b8%aaonline-tracking)
    - [离线追踪(Offline Tracking)](#%e7%a6%bb%e7%ba%bf%e8%bf%bd%e8%b8%aaoffline-tracking)
    - [仿真测试(Simulation)](#%e4%bb%bf%e7%9c%9f%e6%b5%8b%e8%af%95simulation)
    - [批量处理(Batch Processing)](#%e6%89%b9%e9%87%8f%e5%a4%84%e7%90%86batch-processing)
    - [参数设置(Config Setting)](#%e5%8f%82%e6%95%b0%e8%ae%be%e7%bd%aeconfig-setting)
  - [核心代码说明](#%e6%a0%b8%e5%bf%83%e4%bb%a3%e7%a0%81%e8%af%b4%e6%98%8e)
  - [参数说明](#%e5%8f%82%e6%95%b0%e8%af%b4%e6%98%8e)
  - [数据说明](#%e6%95%b0%e6%8d%ae%e8%af%b4%e6%98%8e)
    - [标签数据(TagData)](#%e6%a0%87%e7%ad%be%e6%95%b0%e6%8d%aetagdata)
    - [基准数据(GroundTruth)](#%e5%9f%ba%e5%87%86%e6%95%b0%e6%8d%aegroundtruth)
  - [文件说明](#%e6%96%87%e4%bb%b6%e8%af%b4%e6%98%8e)

## 简介
GLAC是一个基于商用UHF RFID设备的实时追踪系统，可以对高速运动的RFID标签进行精准追踪。它的总体思想是，将追踪问题构建为HMM，构建一个包含所有歧义性的候选轨迹网络，每一条候选轨迹都用一个EKF表示。随着观测的增加，候选轨迹的概率会发生变化，最后只会存在一个接近于真实情况的候选轨迹。

## 快速开始

### 第一步：获取源码
克隆或下载本仓库。
### 第二步：导入工程
本项目使用Apache Ant构建，建议采用NetBeans 8.2将整个仓库作为工程导入，或自行选择合适的方式导入。
### 第三步：运行
点击NetBeans的“运行项目”按钮运行本程序，或者采用其它合适的方式。

## 功能
### 在线追踪(Online Tracking)
![pic](https://github.com/543202718/GLAC/raw/master/pic/online.jpg)

在线追踪需要使用ThingMagic M6e RFID阅读器，将其与计算机连接，并在界面中正确填写端口、目标标签EPC等参数。之后，点击“Create & Connect”按钮与阅读器连接，点击“Start Reading”按钮开始追踪，表格中会实时显示各个天线的相位读数（弧度），右边的图中会实时显示追踪得到的轨迹。
### 离线追踪(Offline Tracking)
![pic](https://github.com/543202718/GLAC/raw/master/pic/offline.jpg)

离线追踪需要点击“Select”按钮选择标签数据文件，再点击“Track”按钮进行追踪，追踪得到的轨迹会显示在下方。
### 仿真测试(Simulation)
![pic](https://github.com/543202718/GLAC/raw/master/pic/simulation.jpg)

仿真测试需要先选择轨迹形状与参数，再点击“Simulate”按钮进行仿真，在下方显示误差。点击“Switch”按钮可以切换位置误差与速度误差的CDF图。

### 批量处理(Batch Processing)
![pic](https://github.com/543202718/GLAC/raw/master/pic/batch.jpg)

批量处理功能可以计算一个文件夹下所有标签数据的追踪误差。点击“Select”按钮选择保存标签数据和基准数据的文件夹，再点击“Process”按钮进行处理，在下方显示误差。点击“Switch”按钮可以切换位置误差与速度误差的CDF图。
### 参数设置(Config Setting)
![pic](https://github.com/543202718/GLAC/raw/master/pic/config.jpg)

文本框中显示的是以json格式描述的参数，可以直接进行修改。在修改完成后，点击“Save”按钮保存即可生效；如果不希望保存修改，可以点击“Reset”按钮将文本框中的参数恢复。

## 核心代码说明
GLAC系统的核心代码被打包为类库lib_GLAC.jar文件，对外暴露Config、HMM和TagData三个类。其中，TagData是对标签数据的封装，Config是参数配置，而HMM是最核心的类。

HMM暴露以下方法：

```java
public void add(TagData td);
public void clear();
public ArrayList<Pair<Double, Double>> getTrajectory();
public ArrayList<Pair<Double, Double>> getVelocity();
```

在追踪中，我们通过clear方法清空HMM保存的信息，为一次崭新的追踪做准备；通过add方法不断向HMM提供新的观测，更新轨迹状态；通过getTrajectory和getVelocity获取当前的追踪结果。

类库的使用可以参考Simulation.java文件中的track方法，更加详细的说明请参阅其Javadoc(lib文件夹下的lib_GLAC_javadoc.zip文件)。

## 参数说明
Config.json文件是配置文件，存放着GLAC系统的所有参数。当前的参数设置是默认参数，修改这些参数可能会影响性能，甚至导致程序运行失败。事实上，该文件是Config类转化为json后导出得到的，参数类型、默认值和具体意义如下（位置的单位是cm，速度的单位是cm/s）：
```
int k = 4;//天线数目
double[] x = {0, 30, 80, 80};//天线的X坐标
double[] y = {0, 0, 0, 80};//天线的Y坐标
double semiLambda = 16.3;//半波长
double stopThreshold = 1e-30;//提前停止阈值
double maxPerimeter = 15.0;//三角定位时的周长阈值
int maxS = 15;//最大扩展的半波长数目
double sigmaP = 0.5;//位置估计的标准差
double sigmaV = 3.5;//速度估计的标准差
```

## 数据说明
data目录下存放着部分实验数据，包括以10cm/s和40cm/s直线运动的数据。数据分为标签数据和基准数据，同名的文件相互对应。

### 标签数据(TagData)
以csv格式保存，每行表示一次读取。一行包括三个数，分别是天线编号(0-3)、毫秒表示的时间和弧度表示的相位。

### 基准数据(GroundTruth)
以csv格式保存，每行表示一个位置。一行包括四个数，分别是X方向位置、Y方向位置、X方向速度和Y方向速度，位置以cm表示，速度以cm/s表示。


## 文件说明
```
|- build 包含自动生成的class文件等
|- data 包含实验数据
|- lib 包含程序依赖的外部库
    |- commons-lang3-3.9.jar Apache Common的jar包
    |- gson-2.8.5.jar Gson的jar包
    |- Jama-1.0.3.jar Jama的jar包
    |- jcommon-1.0.23.jar JFreeChart依赖的jar包
    |- jfreechart-1.0.19.jar JFreeChart的jar包
    |- lib_GLAC.jar GLAC的核心代码的jar包
    |- lib_GLAC_javadoc.zip GLAC的核心代码的Javadoc
    |- ltkjava-1.0.0.6.jar ThingMagic API依赖的jar包
|- nbproject 包含NetBeans的项目配置文件
|- pic 包含README需要的图片
|- src
    |- main 
        |- Main.java 主类
        |- Offline.java 封装离线追踪的方法
    |- simulation 包含仿真测试需要的类
        |- Simulation.java 仿真测试类
        |- Shape.java 轨迹形状虚拟类
        |- Circle.java 圆形轨迹类
        |- Line.java 直线轨迹类
    |- ui 包含图形界面
        |- MainFrame.java 主界面
        |- OnlinePanel.java 在线追踪界面
        |- OfflinePanel.java 离线追踪界面
        |- SimulationPanel.java 仿真测试界面
        |- BatchPanel.java 批量处理界面
        |- SettingPanel.java 参数设置界面
        |- TrajectoryPanel.java 轨迹显示面板
    |- utils 包含工具类 
        |- MyChart.java 封装JFreeChart
        |- MyUtils.java 一些需要用到的工具方法
        |- MyRandom.java 对java.util.Random做的扩展
	|- com
		|- thingmagic 包含ThingMagic的API
|- build.xml Apache Ant的配置文件
|- Config.json GLAC的配置文件
|- manifest.mf 
|- NormalDistribution.txt 标准正态分布的概率函数表，用于计算正态分布的概率函数值
|- README.md 说明文档
```
