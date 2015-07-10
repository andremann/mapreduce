#!/usr/bin/env bash

hadoop fs -rmr output
hadoop fs -rm params.txt
hadoop fs -rm x.txt
hadoop fs -rm dimensions.txt
hadoop fs -copyFromLocal ParametriGMM_INRIA_HOLIDAYS/kMeans-k64_INRIA-Holidays.txt params.txt

#For 1K point.
hadoop fs -copyFromLocal ParametriGMM_INRIA_HOLIDAYS/INRIA_dataset_SIFTs1000LF.txt x.txt

#For 10K point. Results provided in folder “Results 10K”.
#hadoop fs -copyFromLocal ParametriGMM_INRIA_HOLIDAYS/INRIA_dataset_SIFTs10000LF.txt x.txt

hadoop fs -copyFromLocal ParametriGMM_INRIA_HOLIDAYS/dimensions.txt dimensions.txt
nohup hadoop jar map-reduce-GMM-0.0.1-SNAPSHOT.jar gmm.Main &


