package org.myorg;

import java.io.IOException;
import java.util.regex.Pattern;
import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import org.apache.log4j.Logger;

public class WordCount extends Configured implements Tool {

  //Logger for returning messages from the mapper and reducer
	private static final Logger LOG = Logger.getLogger(WordCount.class);

  //Main function for starting the application
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new WordCount(), args);
		System.exit(res);
	}

	public int run(String[] args) throws Exception {
		PrettyTable prettyTable = new PrettyTable();
		Job job = Job.getInstance(getConf(), "wordcount");
		
		job.setJarByClass(this.getClass());

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setCombinerClass(Reduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.waitForCompletion(true);

		prettyTable.parseMROutput(args[1]);
		prettyTable.printText();
		prettyTable.printHTML("output.html");
		
		System.out.println("done");

		return 1;
	}

	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private static final File ignoreList = new File("ignore-list");

		private Text word = new Text();
		private long numRecords = 0;    
		private static final Pattern WORD_BOUNDARY = Pattern.compile("\\s*\\b\\s*");

		public void map(LongWritable offset, Text lineText, Context context)
		throws IOException, InterruptedException {

			String line = lineText.toString();
			Text currentWord = new Text();

			for (String word : WORD_BOUNDARY.split(line)) {
				if (word.isEmpty() || !isAlpha(word) || checkWord(word)) {
					continue;
				}

      			char[] charArray = word.toLowerCase().toCharArray();
				char prevLetter = ' ';

				for( char letter : charArray){
					if(prevLetter != ' '){
						String combi = "" + prevLetter + letter;
						context.write(new Text(combi),one);
					}
					prevLetter = letter;
				}
			}
		}

		public boolean isAlpha(String name) {
			return name.matches("[a-zA-Z]+");
		}

		public boolean checkWord(String theWord) {
			try {
				Scanner scanner = new Scanner(ignoreList);
				while (scanner.hasNext()) {
					if(scanner.next().toLowerCase().contains(theWord.toLowerCase())){
						return true;
					}
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} 
			return false;
		}
	}



	public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
		@Override
		public void reduce(Text word, Iterable<IntWritable> counts, Context context)
		throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable count : counts) {
				sum += count.get();
			}

			context.write(word, new IntWritable(sum));

		}
	}
}



