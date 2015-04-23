package com.querydsl.checkstyle.checks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

import org.junit.*;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DefaultLogger;
import com.puppycrawl.tools.checkstyle.api.*;

public class ArrayOrderCheckTest {

    private static final Locale defaultLocale = Locale.getDefault();

    Checker checker;

    private String errorOutput;
    private OutputStream errorOutputStream = new ByteArrayOutputStream() {

        @Override
        public void close() throws IOException {
            errorOutput = this.toString();
            reset();
        }
    };

    @BeforeClass
    public static void ensureEnglishLocale() {
        Locale.setDefault(Locale.ENGLISH);
    }

    @Before
    public void initialize() throws CheckstyleException {
        checker = getChecker(getChecksConfiguration(ArrayOrderCheck.class));
    }

    @After
    public void destroy() {
        checker.destroy();
    }

    @AfterClass
    public static void resetDefaultLocale() {
        Locale.setDefault(defaultLocale);
    }

    @Test
    public void failOnUnorderedAnnotationValues() throws CheckstyleException {
        assertEquals(2, checker.process(getFiles(UnorderedAnnotationValues.class)));
        assertTrue(errorOutput.contains(
                "[\"unchecked\", \"rawtypes\"] is not ordered correctly, "
                + "expected [\"rawtypes\", \"unchecked\"]"));
    }

    @Test
    public void allowOrderedAnnotationValues() throws CheckstyleException {
        assertEquals(0, checker.process(getFiles(OrderedAnnotationValues.class)));
        assertTrue(errorOutput.isEmpty());
    }

    private Checker getChecker(Configuration configuration) throws CheckstyleException {
        Checker checker = new Checker();
        checker.setModuleClassLoader(Thread.currentThread().getContextClassLoader());
        checker.addListener(new DefaultLogger(ByteStreams.nullOutputStream(), true, errorOutputStream, true));
        checker.configure(configuration);
        return checker;
    }

    private static Configuration getChecksConfiguration(Class<?>... classes) {
        DefaultConfiguration checks = new DefaultConfiguration("Checks");
        DefaultConfiguration treeWalker = new DefaultConfiguration("TreeWalker");

        for (Class<?> clazz : classes) {
            treeWalker.addChild(new DefaultConfiguration(clazz.getCanonicalName()));
        }

        checks.addChild(treeWalker);
        return checks;
    }

    private static List<File> getFiles(Class<?>... classes) {
        List<File> files = Lists.newArrayList();

        for (Class<?> clazz : classes) {
            files.add(getFile(clazz));
        }

        return files;
    }

    private static File getFile(Class<?> clazz) {
        return new File("src/test/java/" + clazz.getCanonicalName().replace(".", "/") + ".java");
    }

}
