# GLAC

Language: English | [中文](./README.md)
- [GLAC](#glac)
  - [Introduction](#introduction)
  - [Quick start](#quick-start)
    - [First: Source code](#first-source-code)
    - [Second: Import](#second-import)
    - [Third: Run](#third-run)
  - [Feature](#feature)
    - [Online Tracking](#online-tracking)
    - [Offline Tracking](#offline-tracking)
    - [Simulation](#simulation)
    - [Batch Processing](#batch-processing)
    - [Config Setting](#config-setting)
  - [Core Code](#core-code)
  - [Config Description](#config-description)
  - [Data Description](#data-description)
    - [TagData](#tagdata)
    - [GroundTruth](#groundtruth)
  - [File Description](#file-description)

## Introduction
GLAC is a realtime tracking system based on COTS RFID devices, which is able to accurately track an RFID tag with high-speed motions. 
Its main idea is to transform the RFID tracking problem into an HMM and construct an network including all candidate trajectories. 
Each trajectory is represented by an EKF. With more and more observations, the probability of candidate trajectories will change. 
Finally there will be only one trajectory close to the groundtruth.

## Quick start

### First: Source code
Clone or download this repository.
### Second: Import
This project is built with Apache Ant. 
It's recommended to import the entire warehouse as a project with NetBeans 8.2 or choose another appropriate ways.
### Third: Run
Click the "Run Project" button of NetBeans or choose another ways.

## Feature
### Online Tracking
![pic](https://github.com/543202718/GLAC/raw/master/pic/online.jpg)

Connect the ThingMagic M6e RFID Reader with the computer.
Then, correctly fill in the port, EPC of target and other parameters in the interface.
After that, click the "Create & Connect" button to connect the reader and click the "Start Reading" button to start tracking.
The phase reading of each antenna will be shown in the table and the trajectory obtained will be shown in the right figure.

### Offline Tracking
![pic](https://github.com/543202718/GLAC/raw/master/pic/offline.jpg)

Click the "Select" button to select the file of tag readings.
Then, click the "Track" button to start tracking.
The trajectory obtained will be shown in the figure.

### Simulation
![pic](https://github.com/543202718/GLAC/raw/master/pic/simulation.jpg)

First, choose the shape and parameters of the simulated trajectory.
Then, click the "Simulate" button to simulate.
The error will be shown in the figure below.
Click the "Switch" button to switch the CDF of position error and velocity error.

### Batch Processing
![pic](https://github.com/543202718/GLAC/raw/master/pic/batch.jpg)

This feature calculates the tracking error of all data in a directory. 
Click the "Select" button to select the directories of tag data and groundtruth.
Then, click the "Process" button to calculate.
The error will be shown in the figure below. 
Click the "Switch" button to switch the CDF of position error and velocity error.

### Config Setting
![pic](https://github.com/543202718/GLAC/raw/master/pic/config.jpg)

The configs described in the json format are displayed in the text box and can be modified directly. 
After the modification, click the "Save" button and the modification will take effect immediately.
If you do not want to save the modification, click the "Reset" button to reset the configs in the text box.

## Core Code
The core code of GLAC is packaged as a class library file "lib_GLAC.jar", exposing three classes, Config, HMM and TagData.
Among them, TagData is the encapsulation of tag data, Config is the parameter configuration, and HMM is the core class.

The following methods are exposed by HMM:
```java
public void add(TagData td);
public void clear();
public ArrayList<Pair<Double, Double>> getTrajectory();
public ArrayList<Pair<Double, Double>> getVelocity();
```

While tracking, we clear the information of HMM by clear();
We provide new observations to HMM and update the trajectory by add();
We get current result by getTrajectory() and getVelocity().

track() method of "Simulation.java" shows how to use this class library.
For more details, please refer to its Javadoc (lib\lib_GLAC_javadoc.zip).

## Config Description
"Config.json" is a configuration file that stores all the parameters of the GLAC system. 
The current config settings are the default settings. 
Modifying these parameters may affect performance and even lead to running failure. 
In fact, the file is derived after the Config class is converted to json, and the parameter type, default value and specific meaning are as follows (the unit of position is cm and the unit of velocity is cm/s):

```
int k = 4;//The number of antennas
double[] x = {0, 30, 80, 80};//X coordinate of the antenna
double[] y = {0, 0, 0, 80};//Y coordinate of the antenna
double semiLambda = 16.3;//Half the wavelength
double stopThreshold = 1e-30;//The threshold of quick stop
double maxPerimeter = 15.0;//The threshold of triangulation
int maxS = 15;//Maximum number of extended semiLambda
double sigmaP = 0.5;//The standard deviation of position estimation
double sigmaV = 3.5;//The standard deviation of velocity estimation
```

## Data Description
Part of the experimental data is stored under the directory "data", including data on linear motion at 10 cm/s and 40 cm/s. 
The data is divided into tag data and groundtruth, and files with the same name correspond to each other.

### TagData
It's saved in csv format.
Each line represents a reading, including three numbers, which are the antenna index(0-3), the time in milliseconds, and the phase in radians.

### GroundTruth
It's saved in csv format.
Each line represents a state, including four numbers, which are X coordinate, Y coordinate, X velocity and Y velocity.
The unit of position is cm and the unit of velocity is cm/s.


## File Description
```
|- build  
|- data //Including experimental data
|- lib //The libraries which GLAC relies on
    |- commons-lang3-3.9.jar //The jar of Apache Common
    |- gson-2.8.5.jar //The jar of Gson
    |- Jama-1.0.3.jar //The jar of Jama
    |- jcommon-1.0.23.jar //The jar JFreeChart relies on
    |- jfreechart-1.0.19.jar //The jar of JFreeChart
    |- lib_GLAC.jar //The jar of GLAC's core
    |- lib_GLAC_javadoc.zip //The Javadoc of GLAC's core
    |- ltkjava-1.0.0.6.jar //The jar ThingMagic API relies on
|- nbproject 
|- pic //Including the pictures of README
|- src
    |- main 
        |- Main.java //The main class
        |- Offline.java //The class of offline tracking
    |- simulation //Including the classes about simulation
        |- Simulation.java //The class of simulation
        |- Shape.java //The class of trajectory's shape
        |- Circle.java //The class of round trajectory
        |- Line.java //The class of linear trajectory
    |- ui //Including GUI
        |- MainFrame.java //The main frame
        |- OnlinePanel.java //The panel of online tracking
        |- OfflinePanel.java //The panel of offline tracking
        |- SimulationPanel.java //The panel of simulation
        |- BatchPanel.java //The panel of batch processing
        |- SettingPanel.java //The panel of config setting
        |- TrajectoryPanel.java //The panel of trajectory
    |- utils  
        |- MyChart.java //The encapsulation of JFreeChart
        |- MyUtils.java //Some useful methods
        |- MyRandom.java //The extension of java.util.Random
	|- com
		|- thingmagic //Including the API of ThingMagic 
|- build.xml 
|- Config.json //The config of GLAC
|- manifest.mf 
|- NormalDistribution.txt //Standard normal distribution probability function table
|- README.md 
```
