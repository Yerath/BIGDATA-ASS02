#!/bin/bash
echo "Cleaning previous build"
make clean

echo "Creating Directories" 
hadoop fs -mkdir /user/${USER}
hadoop fs -chown yerath /user/${USER}
hadoop fs -mkdir /user/${USER}/commonfriends /user/${USER}/commonfriends/input 

echo "Creating input files" 
hadoop fs -rm -f -r /user/${USER}/commonfriends/input
hadoop fs -put friends /user/${USER}/commonfriends/input

echo "Removing old results"
hadoop fs -rm -r /user/${USER}/commonfriends/output 

echo "Running CommonFriends"
make

echo "Results"
hadoop fs -cat /user/${USER}/commonfriends/output/*
