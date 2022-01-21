package org.scical;

class ExpressionParser_OLD {
    public ExpressionParser_OLD(TokenList tokens) {
        m_tokens = tokens;
        m_it = 0;
    }

    /**
     *  STARTING SYMBOL:
     *  1   Expression      SubExpression
     *  2   Expression      NullExpression
     *
     *  NON TERMINALS:
     *  3   SubExpression   BinopLvl1
     *
     *  4   ParenBody       ParenLeft ParenExpr ParenRight
     *
     *  5   ParenExpr       ParenBody
     *  6   ParenExpr       SubExpression
     *
     *  7   SimpleExpr      Number
     *  8   SimpleExpr      ParenBody
     *
     *  9   BinopLvl1       BinopLvl2 BinopLvl1Post
     *  10  BinopLvl1Post   Add BinopLvl1
     *  11  BinopLvl1Post   Sub BinopLvl1
     *  12  BinopLvl1Post   empty
     *
     *  13  BinopLvl2       BinopLvl3 BinopLvl2Post
     *  14  BinopLvl2Post   Mul BinopLvl2Tail
     *  15  BinopLvl2Post   Div BinopLvl2Tail
     *  16  BinopLvl2Post   empty
     *  17  BinopLvl2Tail   BinopLvl2
     *
     *  18  BinopLvl3       SimpleExpr BinopLvl3Post
     *  19  BinopLvl3Post   Pow BinopLvl3Tail
     *  20  BinopLvl3Post   empty
     *  21  BinopLvl3Tail   BinopLvl3
     *
     *  TERMINALS:
     *  22  NullExpression  $
     *
     *  23  ParenLeft       (
     *  24  ParenRight      )
     *  25  Number          <double>
     *
     *  26-30
     *      Add +, Sub -, Mul *, Div /, Pow ^
     */
    public AbstractSyntaxTree parse() {
        AbstractSyntaxTree result = null;

        ASTNodeBase root;
        if ((root = matchExpression()) != null) {
            result = new AbstractSyntaxTree();
            result.m_root = root;
        }

        return result;
    }

    private ASTNodeBase matchExpression() {
        ASTNodeBase result = null;
        if ((result = matchNullExpression()) != null) {
        } else if ((result = matchSubExpression()) != null) {
        }
        return result;
    }

    private ASTNodeBase matchNullExpression() {
        if (m_it > m_tokens.size()) {
            return new ASTNodeNumber(0.0);
        }
        return null;
    }

    private ASTNodeBase matchSubExpression() {
        ASTNodeBase result = null;
        if ((result = matchBinopLvl1()) != null) {
        } else if ((result = matchSimpleExpr()) != null) {
        }
        return result;
    }

    private ASTNodeBase matchSimpleExpr() {
        ASTNodeBase result = null;
        if ((result = matchParenBody()) != null) {
        } else if ((result = matchNumber()) != null) {
        }
        return result;
    }

    private ASTNodeBase matchNumber() {
        ASTNodeBase result = null;
        TokenBase token = peekToken();
        if (token != null && token instanceof TokenNumber) {
            result = new ASTNodeNumber(((TokenNumber) token).m_value);
            m_it++;
        }
        return result;
    }

    private ASTNodeBase matchParenBody() {
        ASTNodeBase result = null;
        TokenBase token = peekToken();
        int origIt = m_it;
        if (token != null && token instanceof TokenParenLeft) {
            m_it++;
            if ((result = matchParenExpr()) != null) {
                token = peekToken();
                if (token != null && token instanceof TokenParenRight) {
                    m_it++;
                } else {
                    return null;
                }
            }
        }
        if (result == null) {
            m_it = origIt;
        }
        return result;
    }

    private ASTNodeBase matchParenExpr() {
        ASTNodeBase result = null;
        if ((result = matchSubExpression()) != null) {
        } else if ((result = matchParenBody()) != null) {
        }
        return result;
    }

    private ASTNodeBase matchBinopLvl1() {
        ASTNodeBase result = null;
        int origIt = m_it;

        ASTNodeBase op1 = null;
        if ((op1 = matchBinopLvl2()) != null) {
        } else if ((op1 = matchSimpleExpr()) != null) {
        }

        ASTNodeBinop binopNode = null;
        if (op1 != null) {
            TokenBase token = peekToken();
            if (token != null) {
                if (token instanceof TokenAdd) {
                    binopNode = new ASTNodeBinopAdd();
                    m_it++;
                } else if (token instanceof TokenSub) {
                    binopNode = new ASTNodeBinopSub();
                    m_it++;
                }
            }

            if (binopNode != null) {
                ASTNodeBase op2 = null;
                if ((op2 = matchSubExpression()) != null) {
                    binopNode.m_op1 = op1;
                    binopNode.m_op2 = op2;
                }
            }
        }
        result = binopNode;

        if (result == null) {
            m_it = origIt;
            result = op1;
        }

        return result;
    }

    private ASTNodeBase matchBinopLvl2() {
        ASTNodeBinop result = null;
        int origIt = m_it;

        ASTNodeBase op1 = null;
        if ((op1 = matchSimpleExpr()) != null) {
        }

        if (op1 != null) {
            TokenBase token = peekToken();
            if (token != null) {
                if (token instanceof TokenMul) {
                    result = new ASTNodeBinopMul();
                    m_it++;
                } else if (token instanceof TokenDiv) {
                    result = new ASTNodeBinopDiv();
                    m_it++;
                }
            }

            if (result != null) {
                ASTNodeBase op2 = null;
                if ((op2 = matchSubExpression()) != null) {
                    result.m_op1 = op1;
                    result.m_op2 = op2;
                }
            }
        }

        if (result == null) {
            m_it = origIt;
        }

        return result;
    }

    private TokenBase peekToken() {
        if (m_it < m_tokens.size()) {
            return m_tokens.get(m_it);
        }
        return null;
    }

    /*
    enum Symbol {
        NTS_EXPR, NTS_SUBEXPR, NTS_PARENBODY, NTS_PARENEXPR, NTS_BINOP, NTS_POWBINOP, NTS_MULDIVBINOP, NTS_ADDSUBBINOP,
        TS_NUM, TS_ADD, TS_SUB, TS_MUL, TS_DIV, TS_POW, TS_NULLEXPR, TS_PARENLEFT, TS_PARENRIGHT };
     */

    private TokenList m_tokens;
    private int m_it;
//    private Stack<ASTNodeBase> m_nodeStack;
}
