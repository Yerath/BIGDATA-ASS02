run: jar
	hadoop fs -rm -f -r  /user/${USER}/commonfriends/output
	hadoop jar commonfriends.jar nl.hu.hadoop.CommonFriends /user/${USER}/commonfriends/input /user/${USER}/commonfriends/output

run_caseSensitive: jar
	hadoop fs -rm -f -r  /user/yerath/commonfriends/output
	hadoop jar commonfriends.jar nl.hu.hadoop.CommonFriends -Dwordcount.case.sensitive=true /user/yerath/commonfriends/input /user/yerath/commonfriends/output 

run_stopwords: jar stopwords
	hadoop fs -rm -f -r  /user/yerath/commonfriends/output
	hadoop jar commonfriends.jar nl.hu.hadoop.CommonFriends /user/yerath/commonfriends/input /user/yerath/commonfriends/output -skip /user/yerath/commonfriends/stop_words.text

compile: build/org/myorg/CommonFriends.class

jar: commonfriends.jar

commonfriends.jar: build/org/myorg/CommonFriends.class
	jar -cvf commonfriends.jar -C build/ .

build/org/myorg/CommonFriends.class: CommonFriends.java
	mkdir -p build
	javac -cp libs/hadoop-mapred-0.22.0.jar:libs/hadoop-common-2.7.3.jar:libs/hadoop-mapreduce-client-core-2.7.3.jar:libs/log4j-1.2.17.jar:libs/asciitable-0.2.5.jar *.java -d build -Xlint

clean:
	rm -rf build commonfriends.jar

data:
	hadoop fs -rm -f -r /user/yerath/commonfriends/input
	hadoop fs -mkdir /user/yerath/commonfriends
	hadoop fs -mkdir /user/yerath/commonfriends/input
	echo "Hadoop is an elephant" > file0
	echo "Hadoop is as yellow as can be" > file1
	echo "Oh what a yellow fellow is Hadoop" > file2
	hadoop fs -put file* /user/yerath/commonfriends/input
	rm file*

poetry:
	hadoop fs -rm -f -r /user/yerath/commonfriends/input
	hadoop fs -mkdir /user/yerath/commonfriends/input
	echo -e "Hadoop is the Elephant King! \\nA yellow and elegant thing.\\nHe never forgets\\nUseful data, or lets\\nAn extraneous element cling! "> HadoopPoem0.txt
	echo -e "A wonderful king is Hadoop.\\nThe elephant plays well with Sqoop.\\nBut what helps him to thrive\\nAre Impala, and Hive,\\nAnd HDFS in the group." > HadoopPoem1.txt
	echo -e "Hadoop is an elegant fellow.\\nAn elephant gentle and mellow.\\nHe never gets mad,\\nOr does anything bad,\\nBecause, at his core, he is yellow." > HadoopPoem2.txt
	hadoop fs -put HadoopP* /user/yerath/commonfriends/input
	rm HadoopPoem*

showResult:
	hadoop fs -cat /user/yerath/commonfriends/output/*
	
stopwords:
	hadoop fs -rm -f /user/yerath/commonfriends/stop_words.text
	echo -e "a\\nan\\nand\\nbut\\nis\\nor\\nthe\\nto\\n.\\n," >stop_words.text
	hadoop fs -put stop_words.text /user/yerath/commonfriends/

