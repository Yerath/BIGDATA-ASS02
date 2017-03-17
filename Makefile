run: jar
	#Remove old build
	rm -rf build commonfriends.jar

	hadoop fs -rm -f -r /user/${USER}

	#Create folders
	hadoop fs -mkdir /user/${USER}
	hadoop fs -chown yerath /user/${USER}
	hadoop fs -mkdir /user/${USER}/commonfriends /user/${USER}/commonfriends/input 

	#Move the input files
	hadoop fs -rm -f -r /user/${USER}/commonfriends/input
	hadoop fs -put friends /user/${USER}/commonfriends/input

	#Remove old results
	hadoop fs -rm -f -r  /user/${USER}/commonfriends/output
	hadoop jar commonfriends.jar org.myorg.commonfriends /user/${USER}/commonfriends/input /user/${USER}/commonfriends/output

run_caseSensitive: jar
	hadoop fs -rm -f -r  /user/yerath/commonfriends/output
	hadoop jar commonfriends.jar org.myorg.commonfriends -Dwordcount.case.sensitive=true /user/yerath/commonfriends/input /user/yerath/commonfriends/output 

run_stopwords: jar stopwords
	hadoop fs -rm -f -r  /user/yerath/commonfriends/output
	hadoop jar commonfriends.jar org.myorg.commonfriends /user/yerath/commonfriends/input /user/yerath/commonfriends/output -skip /user/yerath/commonfriends/stop_words.text

compile: build/org/myorg/commonfriends.class

jar: commonfriends.jar

commonfriends.jar: build/org/myorg/commonfriends.class
	jar -cvf commonfriends.jar -C build/ .

build/org/myorg/commonfriends.class: commonfriends.java
	mkdir -p build
	javac -cp libs/hadoop-mapred-0.22.0.jar:libs/hadoop-common-2.7.3.jar:libs/hadoop-mapreduce-client-core-2.7.3.jar:libs/log4j-1.2.17.jar:asciitable-0.2.5.jar *.java -d build -Xlint

clean:
	rm -rf build commonfriends.jar

showResult:
	hadoop fs -cat /user/yerath/commonfriends/output/*

