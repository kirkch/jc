package com.mosaic.jc.compilers.java;

import com.mosaic.jc.config.Dependency;
import com.mosaic.jc.env.Environment;
import com.mosaic.jc.utils.Function0;
import com.mosaic.jc.utils.VoidFunction0;
import com.mosaic.jc.config.Config;
import com.mosaic.jc.config.Dependency;
import com.mosaic.jc.config.ModuleConfig;
import com.mosaic.jc.config.RepositoryRef;
import com.mosaic.jc.env.Environment;
import com.mosaic.jc.utils.Function0;
import com.mosaic.jc.utils.ListUtils;
import com.mosaic.jc.utils.VoidFunction0;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileOutputStream;
import de.schlichtherle.truezip.file.TFileWriter;

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

    public void compile( final Environment env ) {
        VoidFunction0 compileJavaJob = new VoidFunction0() {
            public void invoke() {
                Config          config          = env.fetchConfig();
                List<JavaSourceCodeFile>  sourceJavaFiles = scanModulesForJavaSourceFiles(env, config.modules);
                List<String>    dependencies    = locateAndOptionallyDownloadDependencies(config);

                env.setCount("javafile", sourceJavaFiles.size());

                // config.destinationDirectory

                TFile targetJar = new TFile(config.rootDirectory, "target/"+config.projectName.replaceAll(" ","_").toLowerCase()+"-"+config.versionFull+".jar");
                targetJar.getParentFile().mkdirs();

                compile(sourceJavaFiles, targetJar, dependencies);

                generateManifest( env, config, dependencies, targetJar );
            }
        };

        env.invokeAndTimeJob("compilejava", compileJavaJob);
    }

    private List<String> locateAndOptionallyDownloadDependencies(Config config) {
        Set<Dependency> dependencies      = new HashSet<Dependency>();

        for ( ModuleConfig module : config.modules ) {
            dependencies.addAll( module.dependencies );
        }

        return resolveDependencies( config.downloadRepositories, dependencies );
    }

    private List<JavaSourceCodeFile> scanModulesForJavaSourceFiles( Environment env, List<ModuleConfig> modules ) {
        List<JavaSourceCodeFile> sourceJavaFiles = new ArrayList<JavaSourceCodeFile>(100);

        for ( ModuleConfig module : modules ) {
            for ( File sourceDirectory : module.sourceDirectories ) {
                sourceJavaFiles.addAll(scanForAllJavaFiles(env, sourceDirectory));
            }
        }

        return sourceJavaFiles;
    }

    private List<String> resolveDependencies( List<RepositoryRef> downloadRepositories, Set<Dependency> dependencies ) {
        List<String> resolvedDependencies = new ArrayList<String>( dependencies.size() );

        IvyArtifactResolver resolver = new IvyArtifactResolver();
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

    private void generateManifest(Environment env, Config config, List<String> dependencies, TFile targetJar) {
        TFile metaDirectory = new TFile( targetJar, "META-INF" );
        TFile manifestFile  = new TFile( metaDirectory, "MANIFEST.MF" );

        metaDirectory.mkdirs();

        List<String> mainClasses = config.allMainFQNs();
        try {
            PrintWriter out = new PrintWriter( new TFileWriter(manifestFile) );

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

    private void compile( List<JavaSourceCodeFile> sourceFiles, File destination, List<String> dependencies ) {
        javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        DiagnosticListener diagnosticListener = new DiagnosticListener() {
            @Override
            public void report( Diagnostic diagnostic ) {
                System.out.println( "DIAGNOSTIC: " + diagnostic );
            }
        };

        JavaFileManager fileManager = new ClassFileManager( compiler.getStandardFileManager(diagnosticListener, null, null), destination );

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



        javax.tools.JavaCompiler.CompilationTask task = compiler.getTask( null, fileManager, diagnosticListener, compilerCommandLineArgs, null, sourceFiles );

        boolean success = task.call();
        if ( !success ) {
            throw new IllegalStateException("compilation failed");
        }
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
//        File   jarDirectory             = new File( artifactDirectory, dependency.versionFull );
//        File   jar                      = new File( jarDirectory, dependency.artifactName + "-" + dependency.versionFull + ".jar" );
//
//
//        try {
//            return jar.getCanonicalPath();  //To change body of created methods use File | Settings | File Templates.
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private static List<JavaSourceCodeFile> scanForAllJavaFiles( final Environment env, final File sourceFileRootDirectory ) {
        Function0<List<JavaSourceCodeFile>> job = new Function0<List<JavaSourceCodeFile>>() {
            public List<JavaSourceCodeFile> invoke() {
                Stack<File> nextDirectoryStack = new Stack<File>();
                List<JavaSourceCodeFile> javaFiles = new ArrayList<JavaSourceCodeFile>(100);

                nextDirectoryStack.push( sourceFileRootDirectory );

                long mostRecentMod = 0L;

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
                                    long lastModifiedMillis = c.lastModified();

                                    mostRecentMod = Math.max( lastModifiedMillis, mostRecentMod );

                                    javaFiles.add( new JavaSourceCodeFile(c) );
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if ( c.isDirectory()  ) {
                                nextDirectoryStack.push(c);
                            }
                        }
                    }
                }

                return javaFiles;
            }
        };

        return env.invokeAndTimeJob("filescan", job);
    }

}

class JavaSourceCodeFile extends SimpleJavaFileObject {

    private File file;

    public JavaSourceCodeFile(File file) throws IOException {
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

    private TFile file;

    public JavaClassFile( TFile file ) throws IOException {
        super(URI.create("file:///" + file.getCanonicalPath()), Kind.CLASS);
        this.file = file;
    }

    public OutputStream openOutputStream() throws IOException {
        System.out.println("file = " + file);
        return new BufferedOutputStream(new TFileOutputStream(file));
    }
}

@SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
class ClassFileManager extends ForwardingJavaFileManager {

    private File  destinationJarFile;

    public ClassFileManager( StandardJavaFileManager standardManager, File destination ) {
        super(standardManager);
        this.destinationJarFile = destination;
    }

    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling)
            throws IOException {
//        System.out.println( "location = " + location );
//        System.out.println( "className = " + className );
//        System.out.println( "sibling = " + sibling );

//        JavaFileObject f = super.getJavaFileForOutput(location,className,kind,sibling);
//System.out.println("f = " + f + " " + f.toUri());

        String relativeFileName = className.replaceAll( "\\.", "/" ) + ".class";

        TFile file = new TFile(destinationJarFile, relativeFileName);

//        file.getParentFile().mkdirs();
        return new JavaClassFile(file);
    }
}