package com.mosaic.jk;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 */
public class Main {
//    http://www.accordess.com/wpblog/an-overview-of-java-compilation-api-jsr-199/
//    http://stackoverflow.com/questions/12173294/compiling-fully-in-memory-with-javax-tools-javacompiler


    public static void main( String[] args ) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String source = "package test; public class Test { static { System.out.println(\"hello\"); } public Test() { System.out.println(\"world\"); } }";

// Save source in .java file.
        File root = new File("/tmp/java"); // On Windows running on C:\, this is C:\java.
        File sourceFile = new File(root, "test/Test.java");
        sourceFile.getParentFile().mkdirs();
        new FileWriter(sourceFile).append(source).close();

// Compile source file.
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        DiagnosticListener diagnosticListener = new DiagnosticListener() {
            @Override
            public void report( Diagnostic diagnostic ) {
                System.out.println( "DIAGNOSTIC: " + diagnostic );
            }
        };

        JavaFileManager fileManager = new ClassFileManager( compiler.getStandardFileManager( diagnosticListener, null, null ) );

        List<String> compilerCommandLineArgs = new ArrayList<String>();
        compilerCommandLineArgs.add( "-target" );
        compilerCommandLineArgs.add( "6" );
        compilerCommandLineArgs.add( "-source" );
        compilerCommandLineArgs.add( "6" );


//        List<JavaFile> javaFiles = scanForAllJavaFiles( "/tmp/Mosaic" );
        List<JavaFile> javaFiles = scanForAllJavaFiles( "/tmp/java" );

        JavaCompiler.CompilationTask task = compiler.getTask( null, fileManager, diagnosticListener, compilerCommandLineArgs, null, javaFiles );

        long startNanos = System.nanoTime();

        boolean success = task.call();

        long endNanos = System.nanoTime();

        double durationMillis = (endNanos-startNanos)/1000000.0;

        System.out.println( "compilation durationMillis = " + durationMillis + " " + success);



// Load and instantiate compiled class.
        System.out.println( "root.toURI().toURL() = " + root.toURI().toURL() );

        URLClassLoader classLoader = URLClassLoader.newInstance( new URL[] {root.toURI().toURL()} );
        Class<?> cls = Class.forName("test.Test", true, classLoader); // Should print "hello".
        Object instance = cls.newInstance(); // Should print "world".
        System.out.println(instance); // Should print "test.Test@hashcode".


    }



    private static List<JavaFile> scanForAllJavaFiles( String root ) {
        Stack<File> nextDirectoryStack = new Stack<File>();
        List<JavaFile> javaFiles = new ArrayList<JavaFile>(100);

        nextDirectoryStack.push( new File( root ) );

        long startNanos = System.nanoTime();

        while ( !nextDirectoryStack.isEmpty() ) {
            File f = nextDirectoryStack.pop();

            File[] children = f.listFiles();

            for ( File c : children ) {
                if ( c.isHidden() ) {
                    continue;
                }

                if ( c.isFile() && c.getName().endsWith(".java") ) {
                    try {
                        javaFiles.add( new JavaFile(c) );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if ( c.isDirectory()  ) {
                    nextDirectoryStack.push(c);
                }
            }
        }

        long endNanos = System.nanoTime();

        double durationMillis = (endNanos-startNanos)/1000000.0;

        System.out.println( "filescan durationMillis = " + durationMillis );

        return javaFiles;
    }

}


class JavaFile extends SimpleJavaFileObject {

    private File file;

    public JavaFile( File file ) throws IOException {
        super( URI.create( "file:///" + file.getCanonicalPath() ),Kind.SOURCE);

        this.file = file;
    }

    @Override
    public CharSequence getCharContent( boolean ignoreEncodingErrors ) {
        try {
            CharBuffer buf = CharBuffer.allocate( (int) file.length() );

            FileReader in = new FileReader(file);
            try {
                in.read( buf );
            } finally {
                in.close();
            }

            buf.flip();

            return buf;
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

}


class JavaClassFile extends SimpleJavaFileObject {

    private File file;

    public JavaClassFile( File file ) throws IOException {
        super(URI.create("file:///" + file.getCanonicalPath()), Kind.CLASS);
        this.file = file;
    }

    public byte[] getBytes() {
        byte[] buf = new byte[(int) file.length()];

        try {
            FileInputStream in = new FileInputStream( file );

            try {
                in.read( buf );
            } finally {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buf;
    }

    public OutputStream openOutputStream() throws IOException {
        return new FileOutputStream(file);
    }
}

class ClassFileManager extends ForwardingJavaFileManager {

//    private JavaClassObject jclassObject;

    public ClassFileManager(StandardJavaFileManager
                                standardManager) {
        super(standardManager);
    }

//    public ClassLoader getClassLoader(Location location) {
//        return new SecureClassLoader() {
//            @Override
//            protected Class<?> findClass(String name)
//                throws ClassNotFoundException {
//                byte[] b = jclassObject.getBytes();
//                return super.defineClass(name, jclassObject
//                    .getBytes(), 0, b.length);
//            }
//        };
//    }

    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling)
        throws IOException {
        System.out.println( "location = " + location );
        System.out.println( "className = " + className );
        System.out.println( "sibling = " + sibling );

//        jclassObject = new JavaClassFile(className, kind);
//        return jclassObject;


        String relativeFileName = className.replaceAll( "\\.", "/" ) + ".class";
        System.out.println( "className.replaceAll = " + relativeFileName );
//        return null;
        return new JavaClassFile( new File(new File("/tmp/java"), relativeFileName) );
    }
}