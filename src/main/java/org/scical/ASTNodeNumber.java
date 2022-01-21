package org.scical;

class ASTNodeNumber extends ASTNodeBase {
    public ASTNodeNumber(double value) {
        m_value = value;
    }

    public double eval() {
        return m_value;
    }

    private double m_value;
}
