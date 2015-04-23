package com.querydsl.checkstyle.checks;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.google.common.collect.Lists;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

public class ArrayOrderCheckTest {

    Checker checker;

    @Before
    public void initialize() throws CheckstyleException {
        checker = getChecker(getChecksConfiguration(ArrayOrderCheck.class));
    }

    @After
    public void destroy() {
        checker.destroy();
    }

    @Test
    public void failOnUnorderedAnnotationValues() throws CheckstyleException {
        assertEquals(2, checker.process(getFiles(UnorderedAnnotationValues.class)));
    }

    @Test
    public void allowOrderedAnnotationValues() throws CheckstyleException {
        assertEquals(0, checker.process(getFiles(OrderedAnnotationValues.class)));
    }

    private Checker getChecker(Configuration configuration) throws CheckstyleException {
        Checker checker = new Checker();
        checker.setModuleClassLoader(Thread.currentThread().getContextClassLoader());
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