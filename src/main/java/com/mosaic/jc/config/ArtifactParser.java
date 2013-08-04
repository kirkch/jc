package com.mosaic.jc.config;

import com.mosaic.jc.utils.Function1;
import com.mosaic.jc.utils.Function1;
import com.mosaic.jc.utils.StringUtils;

/**
 * Parses a line from the artifacts config file.<p/>
 *
 * Format:<p/>
 *
 * artifactName: artifactType(modulesToIncludeInArtifact,...)
 */
public class ArtifactParser implements Function1<String,ArtifactDeclaration> {

    public ArtifactDeclaration invoke( String declarationString ) {
        if (StringUtils.isBlank(declarationString) ) {
            return null;
        }



        return null;
    }

}
