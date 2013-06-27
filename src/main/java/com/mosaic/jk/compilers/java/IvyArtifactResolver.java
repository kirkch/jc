package com.mosaic.jk.compilers.java;

import com.mosaic.jk.config.Dependency;
import com.mosaic.jk.config.RepositoryRef;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor;
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorWriter;
import org.apache.ivy.plugins.resolver.ChainResolver;
import org.apache.ivy.plugins.resolver.URLResolver;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 *
 */
public class IvyArtifactResolver {

    private IvySettings ivySettings;

    public IvyArtifactResolver() {
        ivySettings = createDefaultIvySettings();
    }

    public void addRepositories( List<RepositoryRef> repositories ) {
        for ( RepositoryRef repo : repositories ) {
            addRepository( repo );
        }
    }

    private void addRepository(RepositoryRef repo) {
        URLResolver resolver = new URLResolver();

        resolver.setM2compatible(true);
        resolver.setName( repo.getName() );
        resolver.addArtifactPattern( repo.getUrl() + "/[organisation]/[module]/[revision]/[artifact](-[revision]).[ext]" );

        ivySettings.addResolver(resolver);
        chainedResolver.add(resolver);
    }

    public File resolveArtifact( Dependency dependency ) {
        try {
            return resolveArtifact( dependency.groupId, dependency.artifactName, dependency.versionNumber );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File resolveArtifact(String groupId, String artifactId, String version) throws IOException {
        Ivy ivy = Ivy.newInstance(ivySettings);

        File ivyFile = File.createTempFile("ivy", ".xml");
        ivyFile.deleteOnExit();

        String[] dep = null;
        dep = new String[]{groupId, artifactId, version};

        DefaultModuleDescriptor md =
        DefaultModuleDescriptor.newDefaultInstance(ModuleRevisionId.newInstance(dep[0],
                dep[1] + "-caller", "working"));

        DefaultDependencyDescriptor dd = new DefaultDependencyDescriptor(md,

                ModuleRevisionId.newInstance(dep[0], dep[1], dep[2]), false, false, true);

        md.addDependency(dd);

        //creates an ivy configuration file

        XmlModuleDescriptorWriter.write(md, ivyFile);

        String[] confs = new String[]{"default"};

        ResolveOptions resolveOptions = new ResolveOptions().setConfs(confs);




        try {
            ResolveReport report = ivy.resolve(ivyFile.toURL(), resolveOptions);

            File jarArtifactFile = report.getAllArtifactsReports()[0].getLocalFile();

            return jarArtifactFile;
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    ChainResolver chainedResolver = new ChainResolver();

    private IvySettings createDefaultIvySettings() {
        IvySettings ivySettings = new IvySettings();

        URLResolver resolver = new URLResolver();
        resolver.setM2compatible(true);
        resolver.setName("central");
        resolver.addArtifactPattern( "http://repo1.maven.org/maven2/[organisation]/[module]/[revision]/[artifact](-[revision]).[ext]" );


        chainedResolver.setName("chained");
        chainedResolver.setReturnFirst(true);
        chainedResolver.add(resolver);
        chainedResolver.setName("chained");

        ivySettings.addResolver(resolver);
        ivySettings.addResolver(chainedResolver);
        ivySettings.setDefaultResolver("chained");

        return ivySettings;
    }

}
