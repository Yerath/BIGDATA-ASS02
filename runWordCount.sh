#!/bin/bash
echo "Cleaning previous build"
make clean

echo "Creating Directories" 
hadoop fs -mkdir /user/${USER}
hadoop fs -chown yerath /user/${USER}
hadoop fs -mkdir /user/${USER}/wordcount /user/${USER}/wordcount/input 

echo "Creating input files" 
hadoop fs -rm -f -r /user/${USER}/wordcount/input
hadoop fs -put Alice /user/${USER}/wordcount/input

echo "Removing old results"
hadoop fs -rm -r /user/${USER}/wordcount/output 

echo "Running WordCount"
make

echo "Results"
hadoop fs -cat /user/${USER}/wordcount/output/*
