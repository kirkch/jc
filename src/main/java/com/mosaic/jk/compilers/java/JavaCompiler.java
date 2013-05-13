package com.mosaic.jk.compilers.java;

import com.mosaic.jk.config.Config;
import com.mosaic.jk.config.Dependency;
import com.mosaic.jk.config.ModuleConfig;
import com.mosaic.jk.env.Environment;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.*;

/**
 *
 */
public class JavaCompiler {

    public void compile( Environment env, Config config ) {
        List<File>      sourceDirectories = new ArrayList<File>();
        Set<Dependency> dependencies      = new HashSet<Dependency>();

        for ( ModuleConfig module : config.modules ) {
            Collections.addAll( sourceDirectories, module.sourceDirectories );

            dependencies.addAll( module.dependencies );
        }

        compile( sourceDirectories, config.destinationDirectory, dependencies );

        generateManifest( env, config );
    }

    private void generateManifest( Environment env, Config config ) {
        File metaDirectory = new File( config.destinationDirectory, "META-INF" );
        File manifestFile  = new File( metaDirectory, "MANIFEST" );

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

//                    out.printOnelineTag( "mainClass", fqn );
                }
            } finally {
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void compile( List<File> sourceDirectories, File destinationDirectory, Set<Dependency> dependencies ) {
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
            String        pathSeparator    = System.getProperty("path.separator");
            StringBuilder buf              = new StringBuilder(100);
            boolean       includeSeparator = false;

            for ( Dependency dependency : dependencies ) {
                if ( dependency.projectModuleFlag ) {
                    continue;
                }

                if ( includeSeparator ) {
                    buf.append(pathSeparator);
                } else {
                    includeSeparator = true;
                }

                String dependencyLocation = locateDependency( dependency );

                buf.append( dependencyLocation );
            }

            if ( includeSeparator ) {
                compilerCommandLineArgs.add( "-classpath" );
                compilerCommandLineArgs.add( buf.toString() );
            }
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

    private String locateDependency( Dependency dependency ) {
        boolean useIvy = true;
        if ( useIvy ) {
            IvyArtifactResolver r = new IvyArtifactResolver();

            return r.resolveArtifact(dependency).getPath();
        }


        assert dependency.isExternal();

        String directorySeparator       = System.getProperty("file.separator");
        File   mavenRepositoryDirectory = new File("~/.m2/repository");
        File   groupDirectory           = new File( mavenRepositoryDirectory, dependency.groupId.replaceAll("\\.",directorySeparator));
        File   artifactDirectory        = new File( groupDirectory, dependency.artifactName );
        File   jarDirectory             = new File( artifactDirectory, dependency.versionNumber );
        File   jar                      = new File( jarDirectory, dependency.artifactName + "-" + dependency.versionNumber + ".jar" );

        // todo integrate ivy

        try {
            return jar.getCanonicalPath();  //To change body of created methods use File | Settings | File Templates.
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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