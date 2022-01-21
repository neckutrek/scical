package org.scical;

public class Expression {

    public Expression(String expression) {
        m_expr = expression;
    }

    /**
     * Performs lexing of the string expression this Expression was constructed with.
     * Calling this function before calling {@link #eval() eval} is optional, useful in an asynchronous coding style.
     *
     * @throws InvalidTokenCharacter if an invalid string character was encountered. This Expression object will be left invalid.
     */
    public void lex() {
       lexImpl();
    }

    private void lexImpl() {
        if (m_tokens == null) {
            ExpressionLexer lexer = new ExpressionLexer(m_expr);
            m_tokens = lexer.lex();
        }
    }

    /**
     * Performs parsing of the internal token list into an internal abstract syntax tree.
     * Calling this function before calling {@link #eval() eval} is optional, useful in an asynchronous coding style.
     *
     * If the string expression has not been lexed calling this function will also perform lexing (!)
     *
     * @throws InvalidTokenOrder if an invalid order of tokens was encountered.
     *
     */
    public void parse() {
        lexImpl();
        parseImpl();
    }

    private void parseImpl() {
        assert(m_tokens != null);

        if (m_ast == null) {
            ExpressionParser parser = new ExpressionParser(m_tokens);
            m_ast = parser.parse();
        }
    }

    /**
     * Evaluates the expression. If this Expression has not been lexed or parsed yet this will be performed now.
     */
    public double eval() {
        lexImpl();
        parseImpl();

        double answer = 0.0;
        if (m_ast != null) {
            ExpressionEvaluator evaluator = new ExpressionEvaluator(m_ast);
            answer = evaluator.eval();
        }
        return answer;
    }

    private String m_expr;
    private TokenList m_tokens;
    private AbstractSyntaxTree m_ast;
}
