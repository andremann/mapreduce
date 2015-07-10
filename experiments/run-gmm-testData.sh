#!/usr/bin/env bash

hadoop fs -rmr output
hadoop fs -rm params.txt
hadoop fs -rm x.txt
hadoop fs -rm dimensions.txt
hadoop fs -copyFromLocal testData/params.txt params.txt
hadoop fs -copyFromLocal testData/x.txt x.txt
hadoop fs -copyFromLocal testData/dimensions.txt dimensions.txt
nohup hadoop jar map-reduce-GMM-0.0.1-SNAPSHOT.jar gmm.Main &


