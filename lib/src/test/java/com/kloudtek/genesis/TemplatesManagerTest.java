package com.kloudtek.genesis;

import com.kloudtek.util.FileUtils;
import com.kloudtek.util.TempDir;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class TemplatesManagerTest {
    public static final String TEST_ONE_FILE = "testOneFile";
    public static final String TEST_ONE_FILE_WITH_INPUT = "testOneFileWithInput";
    public static final String TEST_WITH_OPTIONS = "testWithOptions";
    private TempDir tmpDir;
    private TemplatesManager templatesManager;

    @Before
    public void init() throws IOException, InvalidTemplateException {
        templatesManager = new TemplatesManager();
        tmpDir = TempDir.createMavenTmpDir();
    }

    @After
    public void cleanup() {
    }

    @Test
    public void testStandaloneSimpleAsUrl() throws Exception {
        executeWithVar(getClass().getResource("/standalone-testOneFile.xml").toString(), "testval", "someval");
        assertOneFile("# someval");
    }

    @Test
    public void testStandaloneSimpleAsFile() throws Exception {
        File fh = new File(getClass().getResource("/standalone-testOneFile.xml").toURI());
        executeWithVar(fh.getPath(), "testval", "someval");
        assertOneFile("# someval");
    }

    @Test
    public void testValNoInput() throws Exception {
        executeWithVar(TEST_ONE_FILE, "testval", "someval");
        assertOneFile("# someval");
    }

    @Test
    public void testValWithInput() throws Exception {
        executeWithVar(TEST_ONE_FILE_WITH_INPUT, "testval", "someval");
        assertOneFile("# someval");
    }

    @Test
    public void testValWithTwoInputs() throws Exception {
        TemplateExecutor executor = createExecutor("testOneFileWithTwoInputs");
        try {
            executor.execute(tmpDir);
            fail("didn't throw VariableMissingException");
        } catch (VariableMissingException e) {
            assertTrue(e.getMessage().contains("i1"));
        }
        try {
            executor.setVariable("i1","foo");
            executor.execute(tmpDir);
            fail("didn't throw VariableMissingException");
        } catch (VariableMissingException e) {
            assertTrue(e.getMessage().contains("i2"));
        }
        executor.setVariable("i2","bar");
        executor.execute(tmpDir);
        assertOneFile("# foo - bar");
    }

    @Test(expected = TemplateExecutionException.class)
    public void testVarNotSet() throws Exception {
        TemplateExecutor executor = createExecutor(TEST_ONE_FILE);
        executor.execute(tmpDir);
    }

    @Test(expected = VariableMissingException.class)
    public void testWithInputVarNotSet() throws Exception {
        TemplateExecutor executor = createExecutor(TEST_ONE_FILE_WITH_INPUT);
        executor.execute(tmpDir);
    }

    @Test
    public void testWithOptionVar() throws Exception {
        executeWithVar(TEST_WITH_OPTIONS, "myval", "foo");
        assertOneFile("# foo");
    }

    @Test(expected = InvalidVariableException.class)
    public void testWithInvalidOptionVar() throws Exception {
        executeWithVar(TEST_WITH_OPTIONS, "myval", "ba");
    }

//    @Test(expected = InvalidVariableException.class)
//    public void testWithInvalidOptionVar() throws Exception {
//        executeWithVar(TEST_WITH_OPTIONS, "myval", "ba");
//    }

    private TemplateExecutor executeWithVar(String testWithOptions, String myval, String foo) throws Exception {
        TemplateExecutor executor = createExecutor(testWithOptions);
        executor.setVariable(myval, foo);
        executor.execute(tmpDir);
        return executor;
    }

    private TemplateExecutor createExecutor(String templateName) throws TemplateNotFoundException, TemplateExecutionException, InvalidTemplateException {
        TemplateExecutor executor = templatesManager.createExecutor(templateName);
        executor.setNonInteractive(true);
        return executor;
    }

    private void assertOneFile(String expectedContent) throws IOException {
        File[] files = tmpDir.listFiles();
        Assert.assertTrue("Only one file should exist, but found "+( (files != null && files.length > 0 )  ? Arrays.toString(files) : "none"),files != null && files.length == 1);
        String fileContent = FileUtils.toString(files[0]);
        Assert.assertEquals(expectedContent,fileContent);
    }
}
