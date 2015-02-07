package org.komamitsu.clexample;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by komamitsu on 2/7/15.
 */
public class MyClassLoader extends ClassLoader {
    private final File baseDir;

    public MyClassLoader(File baseDir) throws IOException {
        this.baseDir = baseDir;
    }

    @Override
    protected Class findClass(String name) throws ClassNotFoundException {
        String path = name.replaceAll("\\.", File.separator) + ".class";
        File file = new File(baseDir, path);
        try {
            MappedByteBuffer bb = new FileInputStream(file).
                    getChannel().
                    map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            Class<?> klass = defineClass(null, bb, null);
            return klass;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load class", e);
        }
    }

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        if (args.length < 2) {
            System.err.println("usage: java " + MyClassLoader.class.getSimpleName() + " <baseDir> <className>");
        }
        String baseDir = args[0];
        String className = args[1];

        MyClassLoader cl = new MyClassLoader(new File(baseDir));
        Class<?> aClass = cl.loadClass(className);
        System.out.println("class name: " + aClass.getName());
    }
}
