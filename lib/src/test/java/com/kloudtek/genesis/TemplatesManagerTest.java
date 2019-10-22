package com.kloudtek.genesis;

import com.kloudtek.util.FileUtils;
import com.kloudtek.util.TempDir;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class TemplatesManagerTest {
    public static final String TEST_ONE_FILE = TemplatesManagerTest.class.getResource("/test-one-file.json").toString();
    public static final String TEST_ONE_FILE_WITH_INPUT = TemplatesManagerTest.class.getResource("/test-one-file-winputs.json").toString();
    public static final String TEST_WITH_OPTIONS = "testWithOptions";
    private TempDir tmpDir;

    @Before
    public void init() throws IOException, InvalidTemplateException {
        tmpDir = TempDir.createMavenTmpDir();
    }

    @After
    public void cleanup() {
        tmpDir.close();
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

    @Test
    public void testFullexample() throws Exception {
        executeWithVar(TemplatesManagerTest.class.getResource("/fullexample").toString(), "testval", "foo");
        assertFile("README.md","# foo");
        assertFile("bar.txt","bar");
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

    private TemplateExecutor createExecutor(String templateName) throws TemplateNotFoundException, TemplateExecutionException, InvalidTemplateException, IOException {
        TemplateExecutor executor = new TemplateExecutor(Template.create(templateName));
        executor.setNonInteractive(true);
        return executor;
    }

    private void assertFile(String path, String expectedContent) throws IOException {
        String fileContent = FileUtils.toString(new File(tmpDir.getPath()+File.separator+path.replace("/",File.separator)));
        Assert.assertEquals(expectedContent.trim(),fileContent.trim());
    }

    private void assertOneFile(String expectedContent) throws IOException {
        File[] files = tmpDir.listFiles();
        Assert.assertTrue("Only one file should exist, but found "+( (files != null && files.length > 0 )  ? Arrays.toString(files) : "none"),files != null && files.length == 1);
        String fileContent = FileUtils.toString(files[0]);
        Assert.assertEquals(expectedContent.trim(),fileContent.trim());
    }
}
