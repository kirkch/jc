package com.mosaic.jk.compilers.java;

import com.mosaic.jk.config.Config;
import com.mosaic.jk.config.Dependency;
import com.mosaic.jk.config.ModuleConfig;
import com.mosaic.jk.config.RepositoryRef;
import com.mosaic.jk.env.Environment;
import com.mosaic.jk.utils.ListUtils;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.*;

/**
 *
 */
@SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
public class JavaCompiler {

    public void compile( Environment env, Config config ) {
        List<File>      sourceDirectories = new ArrayList<File>();
        Set<Dependency> dependencies      = new HashSet<Dependency>();

        for ( ModuleConfig module : config.modules ) {
            Collections.addAll( sourceDirectories, module.sourceDirectories );

            dependencies.addAll( module.dependencies );
        }

        List<String> resolvedExternalDependencies = resolveDependencies( config.downloadRepositories, dependencies );
        compile( sourceDirectories, config.destinationDirectory, resolvedExternalDependencies );

        generateManifest( env, config, resolvedExternalDependencies );
    }

    private List<String> resolveDependencies( List<RepositoryRef> downloadRepositories, Set<Dependency> dependencies ) {
        List<String> resolvedDependencies = new ArrayList<String>( dependencies.size() );

        IvyArtifactResolver resolver = new IvyArtifactResolver();
System.out.println("resolver = " + resolver);
System.out.println("downloadRepositories = " + downloadRepositories);
        resolver.addRepositories( downloadRepositories );

        for ( Dependency dependency : dependencies ) {
            if ( dependency.projectModuleFlag ) {
                continue;
            }

            String dependencyLocation = resolver.resolveArtifact( dependency ).getPath();

            resolvedDependencies.add(dependencyLocation);
        }

        return resolvedDependencies;
    }

    private void generateManifest(Environment env, Config config, List<String> dependencies) {
        File metaDirectory = new File( config.destinationDirectory, "META-INF" );
        File manifestFile  = new File( metaDirectory, "MANIFEST.MF" );

        metaDirectory.mkdirs();

        List<String> mainClasses = config.allMainFQNs();
        try {
            PrintWriter out = new PrintWriter( manifestFile );

            try {
                int numMains = mainClasses.size();
                if ( numMains > 0 ) {
                    String fqn = mainClasses.get( 0 );
                    if ( numMains > 1 ) {
                        env.warn( "Found " + numMains + " classes with Main in the name. We picked the first one to place into the POM: " + fqn);
                    }

                    String dependencyPath = ListUtils.concat( dependencies, " " );

                    out.println( "Manifest-Version: 1.0" );
                    out.println( "Build-Version: JC 0.0.1" );
                    out.println( String.format("Created-By: %s (%s)", System.getProperty("java.runtime.version"), System.getProperty("java.vm.vendor")) );
                    out.println( "Main-Class: " + fqn );
//                    out.println( "Class-Path: lib/lib1.jar lib/lib2.jar" );
                    out.println( "Class-Path: "+dependencyPath  );


                    // http://docs.oracle.com/javase/1.3/docs/guide/jar/jar.html#Notes%20on%20Manifest%20and%20Signature%20Files
                    //Line length:
                    //No line may be longer than 72 bytes (not characters), in its UTF8-encoded form. If a value would make the initial line longer than this, it should be continued on extra lines (each starting with a single SPACE).
                    // Because header names cannot be continued, the maximum length of a header name is 70 bytes


//                    out.printOnelineTag( "mainClass", fqn );
                }
            } finally {
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void compile( List<File> sourceDirectories, File destinationDirectory, List<String> dependencies ) {
        destinationDirectory.mkdirs();

        javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        DiagnosticListener diagnosticListener = new DiagnosticListener() {
            @Override
            public void report( Diagnostic diagnostic ) {
                System.out.println( "DIAGNOSTIC: " + diagnostic );
            }
        };

        JavaFileManager fileManager = new ClassFileManager( compiler.getStandardFileManager(diagnosticListener, null, null), destinationDirectory );

        List<String> compilerCommandLineArgs = new ArrayList<String>();
        compilerCommandLineArgs.add( "-target" );
        compilerCommandLineArgs.add( "6" );
        compilerCommandLineArgs.add( "-source" );
        compilerCommandLineArgs.add( "6" );

        if ( !dependencies.isEmpty() ) {
            String pathSeparator  = System.getProperty("path.separator");
            String dependencyPath = ListUtils.concat( dependencies, pathSeparator );

            compilerCommandLineArgs.add( "-classpath" );
            compilerCommandLineArgs.add(dependencyPath);
        }



        List<JavaFile> javaFiles = new ArrayList<JavaFile>(100);

        for ( File sourceDirectory : sourceDirectories ) {
            javaFiles.addAll(scanForAllJavaFiles(sourceDirectory));
        }

        javax.tools.JavaCompiler.CompilationTask task = compiler.getTask( null, fileManager, diagnosticListener, compilerCommandLineArgs, null, javaFiles );

        long startNanos = System.nanoTime();

        boolean success = task.call();

        long endNanos = System.nanoTime();

        double durationMillis = (endNanos-startNanos)/1000000.0;

        System.out.println( "compilation durationMillis = " + durationMillis + " " + success);
    }

//    private String locateDependency( Dependency dependency ) {
//        boolean useIvy = true;
//        if ( useIvy ) {
//            IvyArtifactResolver r = new IvyArtifactResolver();
//
//            return r.resolveArtifact(dependency).getPath();
//        }
//
//
//        assert dependency.isExternal();
//
//        String directorySeparator       = System.getProperty("file.separator");
//        File   mavenRepositoryDirectory = new File("~/.m2/repository");
//        File   groupDirectory           = new File( mavenRepositoryDirectory, dependency.groupId.replaceAll("\\.",directorySeparator));
//        File   artifactDirectory        = new File( groupDirectory, dependency.artifactName );
//        File   jarDirectory             = new File( artifactDirectory, dependency.versionNumber );
//        File   jar                      = new File( jarDirectory, dependency.artifactName + "-" + dependency.versionNumber + ".jar" );
//
//
//        try {
//            return jar.getCanonicalPath();  //To change body of created methods use File | Settings | File Templates.
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private static List<JavaFile> scanForAllJavaFiles( File root ) {
        Stack<File> nextDirectoryStack = new Stack<File>();
        List<JavaFile> javaFiles = new ArrayList<JavaFile>(100);

        nextDirectoryStack.push( root );

        long startNanos = System.nanoTime();

        while ( !nextDirectoryStack.isEmpty() ) {
            File f = nextDirectoryStack.pop();

            File[] children = f.listFiles();

            if ( children != null ) {
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
        super( URI.create("file:///" + file.getCanonicalPath()),Kind.SOURCE);

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

//    public byte[] getBytes() {
//        byte[] buf = new byte[(int) file.length()];
//
//        try {
//            FileInputStream in = new FileInputStream( file );
//
//            try {
//                in.read( buf );
//            } finally {
//                in.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return buf;
//    }

    public OutputStream openOutputStream() throws IOException {
        return new FileOutputStream(file);
    }
}

@SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
class ClassFileManager extends ForwardingJavaFileManager {


    private File destinationDirectory;

    public ClassFileManager(StandardJavaFileManager standardManager, File destinationDirectory) {
        super(standardManager);
        this.destinationDirectory = destinationDirectory;
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
//        System.out.println( "location = " + location );
//        System.out.println( "className = " + className );
//        System.out.println( "sibling = " + sibling );

//        JavaFileObject f = super.getJavaFileForOutput(location,className,kind,sibling);
//System.out.println("f = " + f + " " + f.toUri());

        String relativeFileName = className.replaceAll( "\\.", "/" ) + ".class";
        System.out.println( "className.replaceAll = " + relativeFileName );

        File file = new File(destinationDirectory, relativeFileName);
        file.getParentFile().mkdirs();
        return new JavaClassFile(file);
    }
}