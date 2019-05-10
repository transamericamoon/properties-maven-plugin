package org.codehaus.mojo.properties;

import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.*;

public class SetProjectPropertiesMojoTest {
    private static final String NEW_LINE = System.getProperty("line.separator");

    private MavenProject projectStub;
    private SetProjectPropertiesMojo setProjectPropertiesMojo;

    @Before
    public void setUp() {
        projectStub = new MavenProject();
        setProjectPropertiesMojo = new SetProjectPropertiesMojo();
        setProjectPropertiesMojo.setProject(projectStub);
    }


    @Test
    public void setPropertyWithResolver() throws Exception {

        // load properties directly for comparison later
        Properties testProperties = new Properties();
        testProperties.put("i-want-a", "${soda}");

        projectStub.getProperties().put("soda", "${cola}");
        projectStub.getProperties().put("cola", "cherry-coke");

        // do the work

        setProjectPropertiesMojo.setProperties(testProperties);
        setProjectPropertiesMojo.setResolveProperties(true);
        setProjectPropertiesMojo.setUseNestedPropertyResolver(true);
        setProjectPropertiesMojo.execute();

        // check results
        Properties projectProperties = projectStub.getProperties();
        assertNotNull(projectProperties);
        // it should not be empty
        assertEquals(3, projectProperties.size());

        // we are not adding prefix, so properties should be same as in file
        assertEquals(projectProperties.get("i-want-a"), "cherry-coke");

    }

    @Test
    public void setPropertyWithoutResolver() throws Exception {

        // load properties directly for comparison later
        Properties testProperties = new Properties();
        testProperties.put("i-want-a", "${soda}");

        projectStub.getProperties().put("soda", "${cola}");
        projectStub.getProperties().put("cola", "cherry-coke");

        // do the work

        setProjectPropertiesMojo.setProperties(testProperties);
        setProjectPropertiesMojo.execute();

        // check results
        Properties projectProperties = projectStub.getProperties();
        assertNotNull(projectProperties);
        // it should not be empty
        assertEquals(3, projectProperties.size());

        // we are not adding prefix, so properties should be same as in file
        assertEquals(projectProperties.get("i-want-a"), "${soda}");

    }

}
