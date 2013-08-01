package com.mosaic.spikes;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.wagon.Wagon;
import org.eclipse.aether.*;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.file.FileRepositoryConnectorFactory;
import org.eclipse.aether.connector.wagon.WagonProvider;
import org.eclipse.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferResource;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;
import org.sonatype.maven.wagon.AhcWagon;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class AetherSpike {



    public static void main( String[] args )
            throws Exception
    {
        System.out.println( "------------------------------------------------------------" );
//        System.out.println( GetDirectDependencies.class.getSimpleName() );

        RepositorySystem system = newRepositorySystem();

        RepositorySystemSession session = newRepositorySystemSession( system );

//        Artifact artifact = new DefaultArtifact( "io.tesla.maven:maven-aether-provider:3.1.0" );
//        Artifact artifact = new DefaultArtifact( "org.sonatype.aether:aether-impl:1.13" );
        Artifact artifact = new DefaultArtifact( "junit:junit:4.11" );

        RemoteRepository repo = newCentralRepository();

        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
        descriptorRequest.setArtifact( artifact );
        descriptorRequest.addRepository( repo );

        ArtifactDescriptorResult descriptorResult = system.readArtifactDescriptor( session, descriptorRequest );

        for ( Dependency dependency : descriptorResult.getDependencies() )
        {

            System.out.println( dependency );
        }



        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot( new Dependency(artifact, "compile") );
        collectRequest.addRepository( repo );

        DependencyNode node = system.collectDependencies( session, collectRequest ).getRoot();

        DependencyRequest dependencyRequest = new DependencyRequest();
        dependencyRequest.setRoot( node );

        system.resolveDependencies( session, dependencyRequest  );

        PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
        node.accept( nlg );
        System.out.println( nlg.getClassPath() );
    }

    public static RepositorySystem newRepositorySystem()
    {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService( RepositoryConnectorFactory.class, FileRepositoryConnectorFactory.class );
        locator.addService( RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class );
        locator.setServices( WagonProvider.class, new ManualWagonProvider() );

        return locator.getService( RepositorySystem.class );
    }

    public static DefaultRepositorySystemSession newRepositorySystemSession( RepositorySystem system )
    {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        LocalRepository localRepo = new LocalRepository( "target/local-repo" );
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );

        session.setTransferListener( new ConsoleTransferListener() );
        session.setRepositoryListener( new ConsoleRepositoryListener() );

        // uncomment to generate dirty trees
        // session.setDependencyGraphTransformer( null );

        return session;
    }

    public static RemoteRepository newCentralRepository()
    {
        return new RemoteRepository.Builder( "central", "default", "http://repo1.maven.org/maven2/" ).build();
    }

}

class ManualWagonProvider
        implements WagonProvider
{

    public Wagon lookup( String roleHint )
            throws Exception
    {
        if ( "http".equals( roleHint ) )
        {
            return new AhcWagon();
        }
        return null;
    }

    public void release( Wagon wagon )
    {

    }

}



class ConsoleRepositoryListener
        extends AbstractRepositoryListener
{

    private PrintStream out;

    public ConsoleRepositoryListener()
    {
        this( null );
    }

    public ConsoleRepositoryListener( PrintStream out )
    {
        this.out = ( out != null ) ? out : System.out;
    }

    public void artifactDeployed( RepositoryEvent event )
    {
        out.println( "Deployed " + event.getArtifact() + " to " + event.getRepository() );
    }

    public void artifactDeploying( RepositoryEvent event )
    {
        out.println( "Deploying " + event.getArtifact() + " to " + event.getRepository() );
    }

    public void artifactDescriptorInvalid( RepositoryEvent event )
    {
        out.println( "Invalid artifact descriptor for " + event.getArtifact() + ": "
                + event.getException().getMessage() );
    }

    public void artifactDescriptorMissing( RepositoryEvent event )
    {
        out.println( "Missing artifact descriptor for " + event.getArtifact() );
    }

    public void artifactInstalled( RepositoryEvent event )
    {
        out.println( "Installed " + event.getArtifact() + " to " + event.getFile() );
    }

    public void artifactInstalling( RepositoryEvent event )
    {
        out.println( "Installing " + event.getArtifact() + " to " + event.getFile() );
    }

    public void artifactResolved( RepositoryEvent event )
    {
        long size = event.getArtifact().getFile().length();
        out.println( "Resolved artifact " + event.getArtifact() + " from " + event.getRepository() + "("+size+"bytes)" );
    }

    public void artifactDownloading( RepositoryEvent event )
    {
        out.println( "Downloading artifact " + event.getArtifact() + " from " + event.getRepository() );
    }

    public void artifactDownloaded( RepositoryEvent event )
    {
        out.println( "Downloaded artifact " + event.getArtifact() + " from " + event.getRepository() );
    }

    public void artifactResolving( RepositoryEvent event )
    {
        out.println( "Resolving artifact " + event.getArtifact() );
    }

    public void metadataDeployed( RepositoryEvent event )
    {
        out.println( "Deployed " + event.getMetadata() + " to " + event.getRepository() );
    }

    public void metadataDeploying( RepositoryEvent event )
    {
        out.println( "Deploying " + event.getMetadata() + " to " + event.getRepository() );
    }

    public void metadataInstalled( RepositoryEvent event )
    {
        out.println( "Installed " + event.getMetadata() + " to " + event.getFile() );
    }

    public void metadataInstalling( RepositoryEvent event )
    {
        out.println( "Installing " + event.getMetadata() + " to " + event.getFile() );
    }

    public void metadataInvalid( RepositoryEvent event )
    {
        out.println( "Invalid metadata " + event.getMetadata() );
    }

    public void metadataResolved( RepositoryEvent event )
    {
        out.println( "Resolved metadata " + event.getMetadata() + " from " + event.getRepository() );
    }

    public void metadataResolving( RepositoryEvent event )
    {
        out.println( "Resolving metadata " + event.getMetadata() + " from " + event.getRepository() );
    }

}

class ConsoleTransferListener
        extends AbstractTransferListener
{

    private PrintStream out;

    private Map<TransferResource, Long> downloads = new ConcurrentHashMap<TransferResource, Long>();

    private int lastLength;

    public ConsoleTransferListener()
    {
        this( null );
    }

    public ConsoleTransferListener( PrintStream out )
    {
        this.out = ( out != null ) ? out : System.out;
    }

    @Override
    public void transferInitiated( TransferEvent event )
    {
        String message = event.getRequestType() == TransferEvent.RequestType.PUT ? "Uploading" : "Downloading";

        out.println( message + ": " + event.getResource().getRepositoryUrl() + event.getResource().getResourceName() );
    }

    @Override
    public void transferProgressed( TransferEvent event )
    {
        TransferResource resource = event.getResource();
        downloads.put( resource, Long.valueOf( event.getTransferredBytes() ) );

        StringBuilder buffer = new StringBuilder( 64 );

        for ( Map.Entry<TransferResource, Long> entry : downloads.entrySet() )
        {
            long total = entry.getKey().getContentLength();
            long complete = entry.getValue().longValue();

            buffer.append( getStatus( complete, total ) ).append( "  " );
        }

        int pad = lastLength - buffer.length();
        lastLength = buffer.length();
        pad( buffer, pad );
        buffer.append( '\r' );

        out.print( buffer );
    }

    private String getStatus( long complete, long total )
    {
        if ( total >= 1024 )
        {
            return toKB( complete ) + "/" + toKB( total ) + " KB ";
        }
        else if ( total >= 0 )
        {
            return complete + "/" + total + " B ";
        }
        else if ( complete >= 1024 )
        {
            return toKB( complete ) + " KB ";
        }
        else
        {
            return complete + " B ";
        }
    }

    private void pad( StringBuilder buffer, int spaces )
    {
        String block = "                                        ";
        while ( spaces > 0 )
        {
            int n = Math.min( spaces, block.length() );
            buffer.append( block, 0, n );
            spaces -= n;
        }
    }

    @Override
    public void transferSucceeded( TransferEvent event )
    {
        transferCompleted( event );

        TransferResource resource = event.getResource();
        long contentLength = event.getTransferredBytes();
        if ( contentLength >= 0 )
        {
            String type = ( event.getRequestType() == TransferEvent.RequestType.PUT ? "Uploaded" : "Downloaded" );
            String len = contentLength >= 1024 ? toKB( contentLength ) + " KB" : contentLength + " B";

            String throughput = "";
            long duration = System.currentTimeMillis() - resource.getTransferStartTime();
            if ( duration > 0 )
            {
                DecimalFormat format = new DecimalFormat( "0.0", new DecimalFormatSymbols( Locale.ENGLISH ) );
                double kbPerSec = ( contentLength / 1024.0 ) / ( duration / 1000.0 );
                throughput = " at " + format.format( kbPerSec ) + " KB/sec";
            }

            out.println( type + ": " + resource.getRepositoryUrl() + resource.getResourceName() + " (" + len
                    + throughput + ")" );
        }
    }

    @Override
    public void transferFailed( TransferEvent event )
    {
        transferCompleted( event );

        event.getException().printStackTrace( out );
    }

    private void transferCompleted( TransferEvent event )
    {
        downloads.remove( event.getResource() );

        StringBuilder buffer = new StringBuilder( 64 );
        pad( buffer, lastLength );
        buffer.append( '\r' );
        out.print( buffer );
    }

    public void transferCorrupted( TransferEvent event )
    {
        event.getException().printStackTrace( out );
    }

    protected long toKB( long bytes )
    {
        return ( bytes + 1023 ) / 1024;
    }

}
