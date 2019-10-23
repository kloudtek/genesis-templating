package com.kloudtek.genesis;

import com.kloudtek.util.FileUtils;
import com.kloudtek.util.TempDir;
import com.kloudtek.util.TempFile;
import org.apache.commons.compress.archivers.examples.Archiver;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class TemplatesManagerTest {
    public static final String TEST_ONE_FILE = "/test-one-file.json";
    public static final String TEST_ONE_FILE_WITH_INPUT = "/test-one-file-with-inputs.json";
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
        TemplateExecutor executor = createExecutor("/test-one-file-with-2-inputs.json");
        try {
            executor.execute(tmpDir);
            fail("didn't throw VariableMissingException");
        } catch (VariableMissingException e) {
            assertTrue(e.getMessage().contains("i1"));
        }
        try {
            executor.setVariable("i1", "foo");
            executor.execute(tmpDir);
            fail("didn't throw VariableMissingException");
        } catch (VariableMissingException e) {
            assertTrue(e.getMessage().contains("i2"));
        }
        executor.setVariable("i2", "bar");
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
        executeWithVar("/test-one-file-with-inputs-options.json", "testval", "op1");
        assertOneFile("# op1");
    }

    @Test(expected = InvalidVariableException.class)
    public void testWithInvalidOptionVar() throws Exception {
        executeWithVar("/test-one-file-with-inputs-options.json", "testval", "someval");
    }

    @Test
    public void testFullexample() throws Exception {
        executeWithVar("/fullexample", "testval", "foo");
        assertFullExample();
    }

    private void assertFullExample() throws IOException {
        assertFileCount(3);
        assertFile("README.md", "# foo");
        assertFile("bar.txt", "bar");
        assertFile("slc/plane.txt", "bird");
    }


    @Test
    public void testFullexampleZip() throws Exception {
        try (TempFile file = new TempFile("gentemptest", ".zip", new File("target"))) {
            try (ZipArchiveOutputStream zios = new ZipArchiveOutputStream(file)) {
                new Archiver().create(zios, new File(TemplatesManagerTest.class.getResource("/fullexample").toURI()));
            }
            TemplateExecutor executor = createExecutor(file.getPath());
            executor.setVariable("testval", "foo");
            executor.execute(tmpDir);
            assertFullExample();
        }
    }


//    @Test(expected = InvalidVariableException.class)
//    public void testWithInvalidOptionVar() throws Exception {
//        executeWithVar(TEST_WITH_OPTIONS, "myval", "ba");
//    }

    private TemplateExecutor executeWithVar(String name, String myval, String foo) throws Exception {
        TemplateExecutor executor = createExecutor(getTestFileUrl(name));
        executor.setVariable(myval, foo);
        executor.execute(tmpDir);
        return executor;
    }

    private TemplateExecutor createExecutor(String path) throws TemplateNotFoundException, TemplateExecutionException, InvalidTemplateException, IOException {
        TemplateExecutor executor = new TemplateExecutor(Template.create(getTestFileUrl(path)));
        executor.setNonInteractive(true);
        return executor;
    }


    private void assertFile(String path, String expectedContent) throws IOException {
        String fileContent = FileUtils.toString(new File(tmpDir.getPath() + File.separator + path.replace("/", File.separator)));
        Assert.assertEquals(expectedContent.trim(), fileContent.trim());
    }

    private void assertOneFile(String expectedContent) throws IOException {
        assertFileCount(1);
        String fileContent = FileUtils.toString(Objects.requireNonNull(tmpDir.listFiles())[0]);
        Assert.assertEquals(expectedContent.trim(), fileContent.trim());
    }

    private void assertFileCount(int expected) {
        int files = 0;
        LinkedList<File> dirs = new LinkedList<>();
        dirs.add(tmpDir);
        while (!dirs.isEmpty()) {
            File file = dirs.removeFirst();
            if (file.isDirectory()) {
                dirs.addAll(Arrays.asList(Objects.requireNonNull(file.listFiles())));
            } else {
                files++;
            }
        }
        Assert.assertEquals(expected, files);
    }

    private static String getTestFileUrl(@NotNull String name) {
        URL resource = TemplatesManagerTest.class.getResource(name);
        if( resource != null ) {
            return resource.toString();
        } else {
            return name;
        }
    }
}
