package org.scical;

class ExpressionEvaluator {
    public ExpressionEvaluator(AbstractSyntaxTree ast) {
        m_ast = ast;
    }

    public double eval() {
        return 1.2;
    }

    private AbstractSyntaxTree m_ast;
}
