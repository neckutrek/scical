package org.scical;

class AbstractSyntaxTree extends ASTNodeBase {
    public ASTNodeBase m_root;

    public double eval() {
        return m_root.eval();
    }
}
