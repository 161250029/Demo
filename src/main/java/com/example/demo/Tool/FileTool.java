package com.example.demo.Tool;

import com.example.demo.Entity.BugInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.Set;

public class FileTool {
       private ArrayList<String> Lines = new ArrayList<>();
       private String path;
       private int ClassLine;
       private int MethodStartLine;
       private int MethodEndLine;
       public FileTool(String path) throws IOException {
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
           if (lineNumber == 0)
               return;
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
            getMethodLines(lineNumber);
            if (lineNumber - 1 <= ClassLine || lineNumber -1 >= MethodEndLine)
                return false;
            if (MethodStartLine <= ClassLine) {
                return false;
            }
            return true;
       }

    /**
     *
     * @param out_path
     * @param lineNumbers
     * @param bugInfo
     * @throws IOException
     */
       public void writeWalaStatements(String out_path , Set<Integer> lineNumbers , BugInfo bugInfo) throws IOException {
           File file = new File(out_path);
           if (!file.exists())
               file.createNewFile();
           BufferedWriter bw = new BufferedWriter(new FileWriter(file , true));
           bw.write(bugInfo.getId() + " " + bugInfo.getBugType() + " " + bugInfo.getLevel() + " " + bugInfo.getStart());
           bw.newLine();
           bw.write(bugInfo.isWarning() + " Warning In method " + bugInfo.getMethodName() + " in " + bugInfo.getStart() + " line:");
           bw.newLine();
           for (Integer lineNumber : lineNumbers) {
               if (lineNumber - 1 <= Lines.size() - 1) {
                   bw.write(Lines.get(lineNumber - 1).trim());
                   bw.newLine();
               }
           }
           bw.newLine();
           bw.close();
       }
       public boolean run(String out_path , int lineNumber) throws IOException {
           if (!isMethodStatement(lineNumber)) {
               return false;
           }
//           getMethodLines(lineNumber);
//           String[] prefix = path.split("\\\\");
//           System.out.println(prefix[prefix.length - 1]);
//           String out_path = "D:\\FileSlice\\" + prefix[prefix.length - 1].split("\\.")[0] + "\\" +prefix[prefix.length - 1].split("\\.")[0]  + ".java";
//           String out_path = path.split("\\:")[1].split("\\\\") + "\\" + lineNumber + "\\" + prefix[prefix.length - 1].split("\\.")[0]  + ".java";
           File result_file = new File(out_path);
           if (!result_file.exists())
               result_file.createNewFile();
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
           return true;
       }

       public void GenerateMethodAndBugInfo(String out_method_path , String out_bug_info_path , BugInfo bugInfo ,int lineNumber) throws IOException {
           if (!run(out_method_path , lineNumber))
               return;
           File bug_info = new File(out_bug_info_path);
           if (!bug_info.exists())
               bug_info.createNewFile();
           BufferedWriter bw = new BufferedWriter(new FileWriter(bug_info));
           bw.write(bugInfo.getId() + " " + bugInfo.getBugType() + " " + bugInfo.getLevel() + " " + bugInfo.getStart());
           bw.flush();
           bw.close();
       }

       @Deprecated
       public String getMethodName(int lineNumber) {
           return Lines.get(MethodStartLine).trim().split(" ")[2];
       }

    public static void main(String[] args) throws IOException {
           System.out.println(System.getProperty("user.dir"));

    }
}
