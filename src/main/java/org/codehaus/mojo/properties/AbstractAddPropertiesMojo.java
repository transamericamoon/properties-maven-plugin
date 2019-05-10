package org.codehaus.mojo.properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.CommandLineUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public abstract class AbstractAddPropertiesMojo extends AbstractMojo {


    @Parameter( defaultValue = "${project}", required = true, readonly = true )
    protected MavenProject project;

    @Parameter( defaultValue = "false", required = false )
    protected boolean useNestedPropertyResolver;

    @Parameter( defaultValue = "false", required = false )
    protected boolean resolveProperties;

    //Used for resolving property placeholders.
    private IPropertyResolver resolver;


    protected void resolveProperties()
            throws MojoExecutionException, MojoFailureException
    {
        if(!resolveProperties)
            return;

        if(useNestedPropertyResolver) {
            resolver = new NestedPropertyResolver(project);
        } else {
            resolver = new PropertyResolver();
        }

        Properties environment = loadSystemEnvironmentPropertiesWhenDefined();
        Properties projectProperties = project.getProperties();

        for (Enumeration<?> n = projectProperties.propertyNames(); n.hasMoreElements(); )
        {
            String k = (String) n.nextElement();
            projectProperties.setProperty( k, getPropertyValue( k, projectProperties, environment ) );
        }
    }

    private Properties loadSystemEnvironmentPropertiesWhenDefined()
            throws MojoExecutionException
    {
        Properties projectProperties = project.getProperties();

        boolean useEnvVariables = false;
        for ( Enumeration<?> n = projectProperties.propertyNames(); n.hasMoreElements(); )
        {
            String k = (String) n.nextElement();
            String p = (String) projectProperties.get( k );
            if ( p.indexOf( "${env." ) != -1 )
            {
                useEnvVariables = true;
                break;
            }
        }
        Properties environment = null;
        if ( useEnvVariables )
        {
            try
            {
                environment = getSystemEnvVars();
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Error getting system environment variables: ", e );
            }
        }
        return environment;
    }

    /**
     * Override-able for test purposes.
     *
     * @return The shell environment variables, can be empty but never <code>null</code>.
     * @throws IOException If the environment variables could not be queried from the shell.
     */
    private Properties getSystemEnvVars()
            throws IOException
    {
        return CommandLineUtils.getSystemEnvVars();
    }

    private String getPropertyValue( String k, Properties p, Properties environment )
            throws MojoFailureException
    {
        try
        {
            return resolver.getPropertyValue( k, p, environment );
        }
        catch ( IllegalArgumentException e )
        {
            throw new MojoFailureException( e.getMessage() );
        }
    }

    /**
     * Default scope for test access.
     *
     * @param project The test project.
     */
    void setProject( MavenProject project )
    {
        this.project = project;
    }

    public void setUseNestedPropertyResolver(boolean useNestedPropertyResolver) {
        this.useNestedPropertyResolver = useNestedPropertyResolver;
    }

    public void setResolveProperties(boolean resolveProperties) {
        this.resolveProperties = resolveProperties;
    }
}
