import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
 
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class CommonFriends{

	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{
        public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)
            throws IOException{
            //Get the string line
			String line = value.toString();

			//[0] Original user -> Rest == 
			List<String> users = Arrays.asList(line.split(" "));

			//Set important 
			String mainUser = users.get(0);
			users.remove(0);

			//Create tmp array
			String[] tempArray = new String[2];

			for (String friend : users) {

				tempArray[0] = friend;
				tempArray[1] = mainUser;

				//Sort the for the reducer
				Arrays.sort(tempArray);

				//Send 
				output.collect(new Text(tempArray[0] + " " + tempArray[1]), new Text(users.toString()));
			}
        }
    }

    public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text>{
        public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) 
        	throws IOException{

            Text[] texts = new Text[2];
            int index = 0;

            while(values.hasNext()){
                texts[index++] = new Text(values.next());
            }

            String[] list1 = texts[0].toString().split(" ");
            String[] list2 = texts[1].toString().split(" ");

            List<String> list = new LinkedList<String>();
            for(String friend1 : list1){
                for(String friend2 : list2){
                    if(friend1.equals(friend2)){
                        list.add(friend1);
                    }
                }
            }

            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < list.size(); i++){
                    sb.append(list.get(i));
                    if(i != list.size() - 1)
                            sb.append(" ");
            }
            
            output.collect(key, new Text(sb.toString()));
        }
    }

	public static void main(String[] args) throws Exception{
            JobConf conf = new JobConf(CommonFriends.class);
            conf.setJobName("Friend");
 
            conf.setMapperClass(Map.class);
            conf.setReducerClass(Reduce.class);
 
            conf.setMapOutputKeyClass(Text.class);
            conf.setMapOutputValueClass(Text.class);
 
            conf.setOutputKeyClass(Text.class);
            conf.setOutputValueClass(Text.class);
 
            FileInputFormat.setInputPaths(conf, new Path(args[0]));
            FileOutputFormat.setOutputPath(conf, new Path(args[1]));
 
            JobClient.runJob(conf);
    }	

	



	 



	/*****************
	* OLD CODE 
	******************
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		public void map(LongWritable offset, Text lineText, Context context)
			throws IOException, InterruptedException {
			
			//Get the string line
			String line = lineText.toString();

			//[0] Original user -> Rest == 
			List<String> users = Arrays.asList(line.split(" "));

			//Set important 
			String mainUser = users.get(0);
			users.remove(0);

			//Create tmp array
			tempArray = new String[2];

			for (String friend : users) {
				tempArray[0] = friend;
				tempArray[1] = mainUser;

				//Sort the for the reducer
				Arrays.sort(tempArray);

				//Send 
				context.write(new Text(tempArray[0] + " " + tempArray[1]), new Text(users.toString));
			}
		}
	}



	public static class Reduce extends Reducer<Text, Text, Text, Text> {
		@Override
		public void reduce(Text key, Iterable<Text> values, Context context)
		throws IOException, InterruptedException {
			

			//context.write(word, new IntWritable(sum));

		}
	}
	*/
}



