package org.scical;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestExpressionLexer {
    @Test
    public void testLex() {
        String expr = "1.2+3.4";
        ExpressionLexer lexer = new ExpressionLexer(expr);
        TokenList tokens = lexer.lex();
        System.out.println("testLex: " + tokens.size());
        //assetEquals(tokens.size(), 3);
    }
}
