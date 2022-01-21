package org.scical;

class ASTNodeBinopSub extends ASTNodeBinop {
    public double eval() {
        return m_op1.eval() - m_op2.eval();
    }
}
