package org.scical;

import static java.lang.Math.pow;

class ASTNodeBinopPow extends ASTNodeBinop {
    public double eval() {
        return pow(m_op1.eval(), m_op2.eval());
    }
}
