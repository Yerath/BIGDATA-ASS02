package org.myorg;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Roelant Ossewaarde on 24/02/2017.
 */
public class PrettyTable {
    static private String CHARS = "abcdefghijklmnopqrstuvwxyz";
    Map<Character, HashMap<Character, Integer>> table = new HashMap<Character, HashMap<Character, Integer>>();

    public PrettyTable() {
        for (int i = 0; i<CHARS.length(); i++) {
            table.put(CHARS.charAt(i), new HashMap<Character, Integer>());
            for (int j = 0; j<CHARS.length(); j++) {
                table.get(CHARS.charAt(i)).put(CHARS.charAt(j), 0);
            }
        }
    }


    private List<String> getFilesnames(String path){
        System.out.println("======== GET FILES ========");
        List<String> results = new ArrayList<String>();
        try{
            FileSystem fs = FileSystem.get(new Configuration());
            FileStatus[] status = fs.listStatus(new Path("hdfs://localhost:9000" + path));  // you need to pass in your hdfs path

            for (int i=0;i<status.length;i++){
                BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(status[i].getPath())));
                String line;
                line=br.readLine();
                while (line != null){
                    System.out.println(line);
                    line=br.readLine();
                }
            }
        }catch(Exception e){
            System.out.println("File not found");
        }
        System.out.println("======== END FILES ========");
        return results;

    }

    public void parseMROutput(String pathname) throws Exception{
        List<String> files = getFilesnames(pathname);
        System.out.println(files);

        try {
            String line;
            Path pt             = new Path("hdfs://localhost:9000" + pathname +"/part-r-00000");
            FileSystem fs       = FileSystem.get(new Configuration());
            BufferedReader br   = new BufferedReader(new InputStreamReader(fs.open(pt))); 

            line=br.readLine();

            while(line != null){
                System.out.println(line);
                if (line.split("\t")[0].length() > 1) {
                    Character firstChar = line.split("\t")[0].charAt(0);
                    Character secondChar = line.split("\t")[0].charAt(1);
                    if ((table.containsKey(firstChar) && table.get(firstChar).containsKey(secondChar))) {
                        Integer originalValue = table.get(firstChar).get(secondChar);
                        table.get(firstChar).put(secondChar, originalValue + Integer.parseInt(line.split("\t")[1]));
                    }
                }
                line=br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printText() {
        // print first line, header.
        /*System.out.print("   ");
        for (Character firstChar : CHARS.toCharArray()) {
            System.out.print(" "+ firstChar + " ");
        }
        System.out.println();

        for (Character firstChar : CHARS.toCharArray()) {
            System.out.print(firstChar+": ");
            for (Character secondChar : CHARS.toCharArray()) {
                System.out.print(" "+table.get(firstChar).get(secondChar)+" ");

            }
            System.out.println("\n");

        }*/

    }
    public void printHTML(String filename ) {
        try{
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            // print first line, header.
            writer.println("<html>");
            writer.println("<table>");
            writer.println("<thead>");
            writer.println("<tr><th></th>");
            for (Character firstChar : CHARS.toCharArray()) {
                writer.print("<th>"+ firstChar + "</th>");
            }
            writer.println("</tr>");
            writer.println("</thead></tbody>");

            for (Character firstChar : CHARS.toCharArray()) {
                writer.println("<tr><td>");
                writer.print(firstChar+": ");
                writer.println("</td>");
                for (Character secondChar : CHARS.toCharArray()) {
                    writer.print("<td>"+table.get(firstChar).get(secondChar)+"</td>");

                }
                writer.println("</tr>");

            }
            writer.println("</tbody>");
            writer.print("</table>");
            writer.print("</html>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
