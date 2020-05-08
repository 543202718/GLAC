# GLAC
## 简介
GLAC是一个基于商用UHF RFID设备的实时追踪系统，可以对高速运动的RFID标签进行精准追踪。它的总体思想是，将追踪问题构建为HMM，构建一个包含所有歧义性的候选轨迹网络，每一条候选轨迹都用一个EKF表示。随着观测的增加，候选轨迹的概率会发生变化，最后只会存在一个接近于真实情况的候选轨迹。

## 快速开始
### 第一步：获取源码
克隆或下载该仓库。
### 第二步：导入工程
该项目使用Apache Ant构建，建议采用NetBeans 8.2将整个仓库作为工程导入，或自行选择合适的方式导入。
### 第三步：运行
点击NetBeans的“运行项目”按钮运行本程序，或者采用其它合适的方式。

## 功能
### 在线追踪(Online Tracking)
![pic](https://github.com/543202718/GLAC/raw/master/pic/online.jpg)

在线追踪需要使用ThingMagic M6e RFID阅读器，将其与计算机连接，并在设置中正确填写端口、目标标签EPC、天线位置等参数。之后，点击“Create & Connect”按钮与阅读器连接，点击“Start Reading”按钮开始追踪，表格中会实时显示各个天线的相位读数（弧度），右边的图中会实时显示追踪得到的轨迹。
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

## 参数说明
Config.json文件是配置文件，存放着GLAC系统的所有参数。当前的参数设置是默认参数，修改这些参数可能会影响性能，甚至导致程序运行失败。事实上，该文件是Config类转化为json后导出得到的，参数类型、默认值和具体意义如下：
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
String port = "";//计算机与阅读器通信的端口
String epc = "";//被追踪的目标标签的EPC
```

## 数据说明
data目录下存放着部分实验数据，包括以10cm/s和40cm/s直线运动的数据。数据分为标签数据和基准数据，同名的文件相互对应。

### 标签数据(TagData)
以csv格式保存，每行表示一次读取。一行包括三个数，分别是天线编号(0-3)、毫秒表示的时间和弧度表示的相位。

### 基准数据(GroundTruth)
以csv格式保存，每行表示一个位置。一行包括四个数，分别是X方向位置、Y方向位置、X方向速度和Y方向速度，位置以cm表示，速度以cm/s表示。

## 源文件说明
```
|- glac 包含GLAC系统的核心类与主类
    |- Main.java 主类
    |- HMM.java 隐马尔可夫模型
    |- EKF.java 扩展卡尔曼滤波
    |- Trilateration.java 三角定位法
    |- StateStamp.java 卡尔曼滤波的状态帧
|- common 包含GLAC系统的辅助类
    |- Config.java 配置与参数
    |- TagData.java 封装标签读取的数据
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
```
