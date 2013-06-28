# JC  (Java Compiler - pronounced JayCee)

Putting the Caffeine back into Java.

After getting tired with writing Maven POM files, which just seem to go on for ever and even when one follows the
conventions there is another 30 lines of xml required to add another plugin.  Ant has its place, but at the end of the
day sometimes I just want to throw some Java code at the wall and have it compile then and there.  No faffing about,
no ritual dances and definitely no blood letting.  So JC was born.

NB: JC is currently under early development. Please feel free to take a look and have a play. Any feedback
on what is good, what is not and how to make it more useful for you would be great. Contact me at kirkch@gmail.com.


## What JC does

Given a directory of java files, jc will compile them.  In time it will package them too.  For now I have JC
boot strapping itself by generating a Maven POM file.  This simply gets a useful tool out of the door faster.  Maven
excels at tool integration, gone are the days of having to manage class paths in multiple places now as many tools will
read it from the POM files. So this is a good place to start.

## What JC does not do

JC takes the view that the heart of the development cycle revolves around compiling, unit test and packaging.  It is not
about deployments, integrating, or releasing.  This keeps JC lean and mean.  There are so many ways that different
projects may want to manage releases and deployments, this is where Maven gets especially painful.  JC focuses on
mastering the development cycle, and once mastered it will focus on speeding it up.  Other tools should be used for the
other stages, such as Python or good ol' reliable Bash scripts.  As an industry lets go back to having small power tools
with a core focus, rather than big mammoth tools that try to consume and own everything.

## Core JC Values

* Supports zero configuration
* Supports simple overrides for what matters
* Is opinionated as to what matters (specifically compilation, unit testing and packaging only)
* Supports Continuous Deployment
* Declarative
* Fast

## Where JC will go

* Anything that will make development faster is fair game.
* I often mix languages based on the particulars of the job at hand, as such JC is likely to depart from only handling
Java files and enter the world of multiple JVM languages such as Scala, Groovy, Clojure and Kotlin. Custom DSLs
are also fair game. But without having to add ten lines of config per language, in fact no lines will be about right.
* Did I mention speed? I am passionate about that.
* Validation. I want to know what could go wrong, sooner.
* And more speed.


# JC Directory Structure

    /project                    project configuration directory (optional)
            /meta               config overrides, java version, project name and so forth
            /dependencies       lists jar file and inter module dependencies
            /repositories       where to download dependencies from
            /buildstats         overwritten every build; contains stats about the build run
    /src                        may contain java code or module directories
        /module1
        /module2
    /tests                      may contain unit tests directly or directories for module unit tests
          /module1
          /module2

For a range of examples, see the [examples directory](examples).


# Using JC

## Example 1: As simple as it gets

Run the following commands from a Unix prompt:

    mkdir -p src/com/funky
    echo 'package com.funky;'                          >> src/com/funky/Main.java
    echo 'public class Main {'                         >> src/com/funky/Main.java
    echo '  public static void main( String[] args) {' >> src/com/funky/Main.java
    echo '    System.out.println("Hello");'            >> src/com/funky/Main.java
    echo '  }'                                         >> src/com/funky/Main.java
    echo '}'                                           >> src/com/funky/Main.java
    jc
    java -cp target/classes com.funky.Main

Things to note:

* JC will have detected the java code under `src` and compiled it to `target/classes`
* JC will have generated a META-INF file.  In that file com.funky.Main will have been declared so when packaged as a jar it will be easy to execute.
* JC will have used Ivy behind the scenes to manage and download dependencies.
* A Maven pom.xml file was generated. It suggests groupId, artifactId and version. As well as added JUnit and Mockito to the classpath by default.
* The default version number is '0.0.1-{username}_{incrementingNumber}.
* The default project name is the name of the directory containing the `src` directory.
* The default groupId is taken from the java source code's directory structure.
* The default artifactId comes from the project name.

Perfect for getting up and running fast.  If you are using IntelliJ, you can open the pom directly or run mvn idea:idea to generate
the IDE files.

## Example 2: Unit Tests

Run the following commands from a Unix prompt:

    mkdir -p tests/com/funky
    echo 'package com.funky;'                                     >> tests/com/funky/MainTest.java
    echo 'import org.junit.Test;'                                 >> tests/com/funky/MainTest.java
    echo 'public class MainTest {'                                >> tests/com/funky/MainTest.java
    echo '  @Test'                                                >> tests/com/funky/MainTest.java
    echo '  public void failingTest() {'                          >> tests/com/funky/MainTest.java
    echo '    throw new RuntimeException("intentional error");'   >> tests/com/funky/MainTest.java
    echo '  }'                                                    >> tests/com/funky/MainTest.java
    echo '}'                                                      >> tests/com/funky/MainTest.java

TODO JC currently does not run tests (but it will soon). However until then mvn test will run the tests.



## Example 3: Multiple modules

JC can tell whether a package is directly under the src directory or a collection
of modules.  To see this in action, create modules under the src directory.  Consider:

    src/client/com/funky/ClientV1.java
    src/server/com/funky/Main.java

JC will detect two separate modules, one called client and the other called server. Each
module can be referenced when declaring dependencies as well as what artefacts will
be generated.


## Example 4: Declaring dependencies, simple

JC stores custom configuration under a directory called 'project'.

    mkdir project
    echo 'com.netflix.rxjava:rxjava-core:0.8.4' >> project/dependencies

JC will now use Ivy to lookup the specified jar files, and it will not include
its default of JUnit and Mockito.


## Example 5: Declaring dependencies, full fat

To declare dependencies, create a file called 'project/dependencies'. The format
of the dependencies file is as follows


    globalDependencies

    [moduleName]

    mavenRepositoryDependency
    localProjectModuleName


Where the dependencies are either paths to the base module directory or references to a Maven repository.

    mavenGroupId:mavenArtifactId:version  <compile|test|runtime|provided>

The scope of the dependency defaults to 'compile', and thus can be left out.
If it is included then the angle brackets are required.  Thus a more fuller
example would be:


    junit:junit:4.8.2         <test>


    [client]
      com.netflix.rxjava : rxjava-core : 0.8.4
      io.netty           : netty-all   : 4.0.0.CR2

    [server]
      client

The format does ignore whitespace, thus the declarations can be lined up to help
readability.


## Example 6: Declaring download repositories

jc supports the same repositories as Maven and Ivy.  To add a new repository to
download artifacts from, list them in 'project/repositories'.

Example file:

                         http://nexus.sonatype.com/repository
    My Release Repo:     http://nexus.mycompany.com/repository
    Akka Repo:           http://akka.io/repository

The format of the file is:

    OptionalRepoName: RepositoryURL

If the repository name is not specified, then one will be generated automatically
from the url. For example, http://nexus.sonatype.com/repository will be named
'nexus.sonatype.com Repository'.

## Example 7: Build statistics

Every time jc is run, project/buildstats will be overwritten.  It contains
a capture of how long different parts of the build took, as well as a count
of some interesting facets of the project that was detected during the build. All
timings are displayed in seconds, and a history of the last ten runs are kept. With
the most recent run being the last number at the end of the comma separated line.

An example of the buildstats file after a few runs.

    buildcount_ck=5
    javafile_count=1
    build_duration_compilejava=0.605,0.604,0.611
    build_duration_filescan=0.0010,0.0011,0.0010
    build_duration_pomwriter=0.011,0.013,0.011
    build_duration_total=0.636,0.633,0.638,0.636


## Example 8: Controlling the version numbering

The format of the build version string used by JC is: `{versionNumber}-{buildType}_{buildCount}`.

Where `version number` defaults to `0.0.1` and can be specified in `project/meta`
by setting `version=1.0.0`.

The `buildType` defaults to the username of the account running the build, but
can be specified from the command line with `-DbuildType=release`.

And lastly the `buildCount` is a number persisted in `project/buildstats` and
is incremented each time jc is run.  This gives each build a unique version
number useful for continuous deployment environments.  If an older artefact needs
to be rebuilt, the `buildCount` can also be specified from the command line
with `-DbuildCount=11`.


## Example 9: Turning on snapshot dependency support

JC prefers reproducable and tracable builds which support continuous deployment
environments, however you may have to make use of a snapshot build or two when
working with other teams who use different tools and processes.  By default
JC will not check for snapshots from repositories, but add `supportSnapshots = true`
to `project/meta` and you will be golden.




