package com.example.demo.Tool;
import java.io.*;

public class SerialTool {
    public static void writeFiles(String path , Object o) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(path);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(o);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    public static Object readFiles(String path) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream =new FileInputStream(path);
        ObjectInputStream objectInputStream =new ObjectInputStream(fileInputStream);
        objectInputStream.close();
        return objectInputStream.readObject();
    }
}
