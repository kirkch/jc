package com.mosaic.jc.config;

import java.util.Objects;

/**
 *
 */
public class RepositoryRef {
    private String name;
    private String url;

    public RepositoryRef(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int hashCode() {
        return Objects.hash( name, url );
    }

    public boolean equals( Object o ) {
        if ( !(o instanceof RepositoryRef) ) {
            return false;
        }

        RepositoryRef other = (RepositoryRef) o;
        return Objects.equals(this.name, other.name) && Objects.equals(this.url,other.url);
    }

    public String toString() {
        return url;
    }
}
