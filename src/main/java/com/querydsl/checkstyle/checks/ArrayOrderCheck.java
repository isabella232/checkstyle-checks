package com.querydsl.checkstyle.checks;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.puppycrawl.tools.checkstyle.api.*;

public class ArrayOrderCheck extends Check {

    private static final int[] DEFAULT_TOKENS = {
        TokenTypes.ANNOTATION_ARRAY_INIT,
    };

    @Override
    @SuppressWarnings("ReturnOfCollectionOrArrayField") //checkstyle API
    public int[] getDefaultTokens() {
        return DEFAULT_TOKENS;
    }

    @Override
    public void visitToken(DetailAST ast) {
        List<String> identifiers = Lists.newArrayList();

        for (DetailAST child = ast.getFirstChild();
                child != null;
                child = child.getNextSibling()) {

            if (child.getType() == TokenTypes.EXPR) {
                FullIdent identifier = FullIdent.createFullIdentBelow(child);
                identifiers.add(identifier.getText());
            }
        }
        Ordering<String> naturalOrdering = Ordering.natural();
        boolean ordered = naturalOrdering.isOrdered(identifiers);
        if (!ordered) {
            log(ast, "ArrayOrder.unordered", identifiers, naturalOrdering.sortedCopy(identifiers));
        }
    }
}
