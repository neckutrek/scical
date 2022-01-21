package org.scical;

import java.util.*;

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
 *  9   BinopLvl1       BinopLvl2 BinopLvl1Post GENNODE
 *  10  BinopLvl1Post   Add BinopLvl1
 *  11  BinopLvl1Post   Sub BinopLvl1
 *  12  BinopLvl1Post   empty
 *
 *  13  BinopLvl2       BinopLvl3 BinopLvl2Post GENNODE
 *  14  BinopLvl2Post   Mul BinopLvl2Tail
 *  15  BinopLvl2Post   Div BinopLvl2Tail
 *  16  BinopLvl2Post   empty
 *  17  BinopLvl2Tail   BinopLvl2
 *
 *  18  BinopLvl3       SimpleExpr BinopLvl3Post GENNODE
 *  19  BinopLvl3Post   Pow BinopLvl3Tail
 *  20  BinopLvl3Post   empty
 *  21  BinopLvl3Tail   BinopLvl3
 *
 *  22  GENNODE         callGennode()
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
class ExpressionParser {

    private     TokenList               m_tokens;
    private     int                     m_it;
    private     Map<Symbol, List<Rule>> m_parseTable;
    private     Stack<Symbol>           m_symbolStack;
    private     Stack<ASTNodeBase>      m_semanticStack;

    enum Symbol {
        NTS_EXPR, NTS_SUBEXPR, NTS_PARENBODY, NTS_PARENEXPR, NTS_SIMPLEEXPR,
        NTS_BINOP, NTS_BINOPLVL1, NTS_BINOPLVL1POST, NTS_BINOPLVL1TAIL,
        NTS_BINOPLVL2, NTS_BINOPLVL2POST, NTS_BINOPLVL2TAIL,
        NTS_BINOPLVL3, NTS_BINOPLVL3POST, NTS_BINOPLVL3TAIL,

        TS_NUM, TS_ADD, TS_SUB, TS_MUL, TS_DIV, TS_POW, TS_NULLEXPR,
        TS_PARENLEFT, TS_PARENRIGHT, TS_EMPTY, TS_GENNODE
    }

    class Rule {
        public Rule(Symbol symbol, int ruleId) {
            m_symbol = symbol;
            m_ruleId = ruleId;
        }
        public Symbol m_symbol;
        public int m_ruleId;
    }

    public ExpressionParser(TokenList tokens) {
        m_tokens = tokens;
        m_it = 0;
        generateParseTable();
    }

    public AbstractSyntaxTree parse() {
        AbstractSyntaxTree result = null;

        m_symbolStack = new Stack<Symbol>();
        m_symbolStack.push(Symbol.NTS_EXPR);

        m_semanticStack = new Stack<ASTNodeBase>();

        while (!m_symbolStack.empty()) {
            System.out.println("Symbol stack: " + m_symbolStack.toString());

            TokenBase currentToken = null;
            if (m_it < m_tokens.size()) {
                m_tokens.get(m_it);
            }
            Symbol currentSymbol = m_symbolStack.pop();

            var rules = m_parseTable.get(currentSymbol);
            if (rules != null) {
                // we should process a non-terminating symbol
                Rule rule = null;
                for (Rule r : rules) {
                    if (r.m_symbol == tokenToSymbol(currentToken)) {
                        rule = r;
                        break;
                    }
                }
                if (rule == null || !processRule(rule)) {
                    System.out.println("Not able to parse token '" + currentToken.toString() + "'");
                }
            } else {
                // we should eat a token or determine an invalid expression
                if (currentSymbol == Symbol.TS_GENNODE) {
                    genNode();
                } else if (eatToken(currentToken)) {
                    m_it++;
                } else {
                    System.out.println("Unable to eat token: '" + currentToken.toString() + "'");
                }
            }
        }

        if (m_semanticStack.size() == 1) {
            ASTNodeBase head = m_semanticStack.pop();
            result = new AbstractSyntaxTree();
            result.m_root = head;
        } else {
            System.out.println("Invalid semantic stack!");
        }

        return result;
    }

    private void genNode() {
        System.out.println("genNode, semstack = " + m_semanticStack.toString());
        if (m_semanticStack.size() < 3) {
            System.out.println("Trying to reduce semantic stack but size is too small!");
            return;
        }

        ASTNodeBase op1 = m_semanticStack.pop();
        ASTNodeBase op = m_semanticStack.pop();
        ASTNodeBase op2 = m_semanticStack.pop();
        if (op instanceof ASTNodeBinop) {
            ASTNodeBinop bn = (ASTNodeBinop) op;
            bn.m_op1 = op1;
            bn.m_op2 = op2;
            m_semanticStack.push(bn);
        } else {
            System.out.println("Huston we have a problem!");
        }
    }

    private Symbol tokenToSymbol(TokenBase token) {
        if (token instanceof TokenNumber) { return Symbol.TS_NUM; }
        else if (token instanceof TokenAdd) { return Symbol.TS_ADD; }
        else if (token instanceof TokenSub) { return Symbol.TS_SUB; }
        else if (token instanceof TokenMul) { return Symbol.TS_MUL; }
        else if (token instanceof TokenDiv) { return Symbol.TS_DIV; }
        else if (token instanceof TokenPow) { return Symbol.TS_POW; }
        else if (token instanceof TokenParenLeft) { return Symbol.TS_PARENLEFT; }
        else if (token instanceof TokenParenRight) { return Symbol.TS_PARENRIGHT; }
        else {
            System.out.println("Unable to translate token '" + token.toString() + "'");
        }
        return null;
    }

    private boolean processRule(Rule rule) {
        boolean result = true;
        switch (rule.m_ruleId) {
            case  1: m_symbolStack.push(Symbol.NTS_SUBEXPR); break;
            case  2: m_symbolStack.push(Symbol.TS_NULLEXPR); break;
            case  3: m_symbolStack.push(Symbol.NTS_BINOPLVL1); break;
            case  4:
                m_symbolStack.push(Symbol.TS_PARENRIGHT);
                m_symbolStack.push(Symbol.NTS_PARENEXPR);
                m_symbolStack.push(Symbol.TS_PARENLEFT);
                break;
            case  5: m_symbolStack.push(Symbol.NTS_PARENBODY); break;
            case  6: m_symbolStack.push(Symbol.NTS_SUBEXPR); break;
            case  7:
                m_symbolStack.push(Symbol.TS_NUM);
                break;
            case  8: m_symbolStack.push(Symbol.NTS_PARENBODY); break;
            case  9:
                m_symbolStack.push(Symbol.TS_GENNODE);
                m_symbolStack.push(Symbol.NTS_BINOPLVL1POST);
                m_symbolStack.push(Symbol.NTS_BINOPLVL2);
                break;
            case 10:
                m_symbolStack.push(Symbol.NTS_BINOPLVL1);
                m_symbolStack.push(Symbol.TS_ADD);
                break;
            case 11:
                m_symbolStack.push(Symbol.NTS_BINOPLVL1);
                m_symbolStack.push(Symbol.TS_SUB);
                break;
            case 12: break;
            case 13:
                m_symbolStack.push(Symbol.TS_GENNODE);
                m_symbolStack.push(Symbol.NTS_BINOPLVL2POST);
                m_symbolStack.push(Symbol.NTS_BINOPLVL3);
                break;
            case 14:
                m_symbolStack.push(Symbol.NTS_BINOPLVL2TAIL);
                m_symbolStack.push(Symbol.TS_MUL);
                break;
            case 15:
                m_symbolStack.push(Symbol.NTS_BINOPLVL2TAIL);
                m_symbolStack.push(Symbol.TS_DIV);
                break;
            case 16: break;
            case 17: m_symbolStack.push(Symbol.NTS_BINOPLVL2); break;
            case 18:
                m_symbolStack.push(Symbol.TS_GENNODE);
                m_symbolStack.push(Symbol.NTS_BINOPLVL3POST);
                m_symbolStack.push(Symbol.NTS_SIMPLEEXPR);
                break;
            case 19:
                m_symbolStack.push(Symbol.NTS_BINOPLVL3TAIL);
                m_symbolStack.push(Symbol.TS_POW);
                break;
            case 20: break;
            case 21:
                m_symbolStack.push(Symbol.NTS_BINOPLVL3);
                break;
            case 22:

                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    private boolean eatToken(TokenBase token) {
        ASTNodeBase newNode = null;
        if (token instanceof TokenNumber) {
            TokenNumber tn = (TokenNumber) token;
            newNode = new ASTNodeNumber(tn.m_value);
        } else if (token instanceof TokenAdd) {
            newNode = new ASTNodeBinopAdd();
        }
        else if (token instanceof TokenSub) {
            newNode = new ASTNodeBinopSub();
        }
        else if (token instanceof TokenMul) {
            newNode = new ASTNodeBinopMul();
        }
        else if (token instanceof TokenDiv) {
            newNode = new ASTNodeBinopDiv();
        }
        else if (token instanceof TokenPow) {
            newNode = new ASTNodeBinopPow();
        }

        System.out.println("eatToken, semstack = " + m_semanticStack.toString());
        m_semanticStack.push(newNode);

        return true;
    }

    /*
    private boolean appendHead(ASTNodeBase node) {
        boolean result = true;
        if (m_head == null) {
            m_head = node;
        } else {
            ASTNodeBinop binopHead = null;
            ASTNodeBinop binopNode = null;
            if (m_head instanceof ASTNodeBinop) { binopHead = (ASTNodeBinop) m_head; }
            if (node instanceof ASTNodeBinop) { binopNode = (ASTNodeBinop) node; }

            if (binopHead != null) {
                if (binopHead.m_op2 == null) {
                    binopHead.m_op2 = node;
                } else if (binopNode != null) {
                    binopNode.m_op1 = m_head;
                    m_head = binopNode;
                } else {
                    System.out.println("Semantic error 1!");
                    result = false;
                }
            } else if (m_head instanceof ASTNodeNumber) {
                if (binopNode != null) {
                    binopNode.m_op1 = m_head;
                    m_head = binopNode;
                } else {
                    System.out.println("Semantic error 3!");
                    result = false;
                }
            } else {
                System.out.println("Semantic error 2!");
                result = false;
            }
        }
        return result;
    }
*/
    private void generateParseTable() {
        m_parseTable = new HashMap<Symbol, List<Rule>>();

        m_parseTable.put(Symbol.NTS_EXPR, new ArrayList<Rule>(Arrays.asList(
                new Rule(Symbol.TS_NUM, 1),
                new Rule(Symbol.TS_PARENLEFT, 1),
                new Rule(Symbol.TS_NULLEXPR, 2)
        )));

        m_parseTable.put(Symbol.NTS_SUBEXPR, new ArrayList<Rule>(Arrays.asList(
                new Rule(Symbol.TS_NUM, 3),
                new Rule(Symbol.TS_PARENLEFT, 3)
        )));

        m_parseTable.put(Symbol.NTS_PARENBODY, new ArrayList<Rule>(Arrays.asList(
                new Rule(Symbol.TS_PARENLEFT, 4)
        )));

        m_parseTable.put(Symbol.NTS_PARENEXPR, new ArrayList<Rule>(Arrays.asList(
                new Rule(Symbol.TS_PARENLEFT, 5),
                new Rule(Symbol.TS_NUM, 6)
        )));

        m_parseTable.put(Symbol.NTS_SIMPLEEXPR, new ArrayList<Rule>(Arrays.asList(
                new Rule(Symbol.TS_NUM, 7),
                new Rule(Symbol.TS_PARENLEFT, 8)
        )));

        m_parseTable.put(Symbol.NTS_BINOPLVL1, new ArrayList<Rule>(Arrays.asList(
                new Rule(Symbol.TS_NUM, 9),
                new Rule(Symbol.TS_PARENLEFT, 9)
        )));

        m_parseTable.put(Symbol.NTS_BINOPLVL1POST, new ArrayList<Rule>(Arrays.asList(
                new Rule(Symbol.TS_ADD, 10),
                new Rule(Symbol.TS_SUB, 11),
                new Rule(Symbol.TS_EMPTY, 12)
        )));

        m_parseTable.put(Symbol.NTS_BINOPLVL2, new ArrayList<Rule>(Arrays.asList(
                new Rule(Symbol.TS_NUM, 13),
                new Rule(Symbol.TS_PARENLEFT, 13)
        )));

        m_parseTable.put(Symbol.NTS_BINOPLVL2POST, new ArrayList<Rule>(Arrays.asList(
                new Rule(Symbol.TS_MUL, 14),
                new Rule(Symbol.TS_DIV, 15),
                new Rule(Symbol.TS_EMPTY, 16)
        )));

        m_parseTable.put(Symbol.NTS_BINOPLVL2TAIL, new ArrayList<Rule>(Arrays.asList(
                new Rule(Symbol.TS_NUM, 17),
                new Rule(Symbol.TS_PARENLEFT, 17)
        )));

        m_parseTable.put(Symbol.NTS_BINOPLVL3, new ArrayList<Rule>(Arrays.asList(
                new Rule(Symbol.TS_NUM, 18),
                new Rule(Symbol.TS_PARENLEFT, 18)
        )));

        m_parseTable.put(Symbol.NTS_BINOPLVL3POST, new ArrayList<Rule>(Arrays.asList(
                new Rule(Symbol.TS_POW, 19),
                new Rule(Symbol.TS_EMPTY, 20)
        )));

        m_parseTable.put(Symbol.NTS_BINOPLVL3TAIL, new ArrayList<Rule>(Arrays.asList(
                new Rule(Symbol.TS_NUM, 21),
                new Rule(Symbol.TS_PARENLEFT, 21)
        )));
    }
}
