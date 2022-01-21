package org.scical;

import java.nio.CharBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ExpressionLexer {
    public ExpressionLexer(String expression) {
        m_expr = expression;
    }

    public TokenList lex() {
        TokenList tokenList = new TokenList();

        CharBuffer buff = CharBuffer.wrap(m_expr.toCharArray(), 0, m_expr.length());

        Pattern numberPattern = Pattern.compile("(^([0-9]*[.])?[0-9]+)");
        Matcher numberMatcher = numberPattern.matcher(buff);

        Pattern singleCharPattern = Pattern.compile("(^[-+*/^()])");
        Matcher singleCharMatcher = singleCharPattern.matcher(buff);

        while (buff.length() > 0) {
            if (singleCharMatcher.find()) {
                switch (buff.charAt(0)) {
                    case '+': tokenList.add(new TokenAdd()); break;
                    case '-': tokenList.add(new TokenSub()); break;
                    case '*': tokenList.add(new TokenMul()); break;
                    case '/': tokenList.add(new TokenDiv()); break;
                    case '^': tokenList.add(new TokenPow()); break;
                    case '(': tokenList.add(new TokenParenLeft()); break;
                    case ')': tokenList.add(new TokenParenRight()); break;
                    default:
                        System.out.println("Character '" + buff.charAt(0) + "' not supported yet!");
                        break;
                }

                buff = CharBuffer.wrap(buff, 1, buff.length());
                singleCharMatcher = singleCharPattern.matcher(buff);
                numberMatcher = numberPattern.matcher(buff);
            } else if (numberMatcher.find()) {
                String tokenString = numberMatcher.group(1);
                double tokenValue = Double.parseDouble(tokenString);
                tokenList.add(new TokenNumber(tokenValue));

                buff = CharBuffer.wrap(buff, numberMatcher.end(), buff.length());
                singleCharMatcher = singleCharPattern.matcher(buff);
                numberMatcher = numberPattern.matcher(buff);
            } else {
                System.out.println("Lexing error: " + buff);
                buff = CharBuffer.wrap(buff, 1, buff.length());
            }
        }

        return tokenList;
    }

    private String m_expr;
}