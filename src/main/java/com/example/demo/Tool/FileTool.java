package com.example.demo.Tool;

import java.io.*;
import java.util.ArrayList;

public class FileTool {
       private ArrayList<String> Lines = new ArrayList<>();
       private String path;
       private int ClassLine;
       private int MethodStartLine;
       private int MethodEndLine;
       public void init(String path) throws IOException {
           this.path = path;
           read(path);
           getClassLine();
       }

       public void read(String path) throws IOException {
           File file = new File(path);
           BufferedReader br = new BufferedReader(new FileReader(file));
           String line = "";
           while((line = br.readLine())!= null) {
               Lines.add(line);
           }
           br.close();
       }

       public void getClassLine() {
           for (int i = 0 ; i < Lines.size() ; i ++) {
               String line = Lines.get(i);
               String[] words = line.split(" ");
               for (String word : words) {
                   if (word.equals("class")) {
                       this.ClassLine = i;
                       return;
                   }
               }
           }
       }

       public void getMethodLines(int lineNumber) {
           int start = lineNumber - 1;
           int end = Lines.size() - 1;
           for (int i = lineNumber - 1 ; i >= 0 ; i --) {
               String line = Lines.get(i);
               String[] words = line.split(" ");
               for (String word : words) {
                   if (word.equals("public") || word.equals("private")) {
                       start = i;
                       i = -1;
                       break;
                   }
               }
           }
           for (int i = lineNumber - 1 ; i < Lines.size() ; i ++) {
               String line = Lines.get(i);
               String[] words = line.split(" ");
               for (String word : words) {
                   if (word.equals("public") || word.equals("private")) {
                       end = i - 1;
                       i = Lines.size();
                       break;
                   }
               }
           }
           this.MethodStartLine = start;
           this.MethodEndLine = end;
       }

       public boolean isMethodStatement(int lineNumber) {
            if (lineNumber - 1 <= ClassLine || lineNumber -1 >= MethodEndLine)
                return false;
            getMethodLines(lineNumber);
            if (MethodStartLine <= ClassLine) {
                return false;
            }
            return true;
       }
       public String getMethodName(int lineNumber) {
               return Lines.get(MethodStartLine).trim().split(" ")[2];
       }
       public void run(int lineNumber) throws IOException {
           getMethodLines(lineNumber);
           String[] prefix = path.split("\\\\");
           System.out.println(prefix[prefix.length - 1]);
           String out_path = "D:\\FileSlice\\" + prefix[prefix.length - 1].split("\\.")[0]  + ".java";
           File result_file = new File(out_path);
           BufferedWriter writer = new BufferedWriter(new FileWriter(result_file));
           for(int i = 0 ; i <= ClassLine + 1 ; i ++) {
               writer.write(Lines.get(i));
               writer.newLine();
               writer.flush();
           }
           for (int i = MethodStartLine; i <= MethodEndLine ; i ++) {
               writer.write(Lines.get(i));
               writer.newLine();
               writer.flush();
           }
           if (MethodEndLine != Lines.size() - 1 )
               writer.write("}");
           writer.close();
       }

    public static void main(String[] args) throws IOException {
           System.out.println(System.getProperty("user.dir"));

    }
}
