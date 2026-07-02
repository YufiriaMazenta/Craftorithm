package pers.yufiria.craftorithm.script.ast;

import pers.yufiria.craftorithm.script.ScriptException;
import pers.yufiria.craftorithm.script.lex.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * 语法分析器
 * 将 Token 流解析为 AST
 *
 * 语法（EBNF）:
 *   program       = statement*
 *   statement     = if_stmt | expression NEWLINE
 *   if_stmt       = "if" expression NEWLINE block
 *                   ("elseif" expression NEWLINE block)*
 *                   ("else" NEWLINE block)?
 *                   "endif" NEWLINE
 *   block         = statement*
 *   expression    = or_expr
 *   or_expr       = and_expr ("||" and_expr)*
 *   and_expr      = comparison ("&&" comparison)*
 *   comparison    = additive (("==" | "!=" | ">" | ">=" | "<" | "<=") additive)?
 *   additive      = multiplicative (("+" | "-") multiplicative)*
 *   multiplicative = unary (("*" | "/") unary)*
 *   unary         = ("!" | "-") unary | call
 *   call          = IDENTIFIER "(" args ")" | IDENTIFIER bare_args | atom
 *   bare_args     = atom+
 *   args          = (expression ("," expression)*)?
 *   atom          = STRING | NUMBER | BOOLEAN | "(" expression ")"
 */
public class ScriptParser {

    private final List<Token> tokens;
    private int pos;

    public ScriptParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * 解析为 AST
     * @return 程序的根节点（BlockNode）
     */
    public ASTNode.BlockNode parse() {
        List<ASTNode> statements = new ArrayList<>();
        skipNewlines();
        while (!isAtEnd()) {
            ASTNode stmt = parseStatement();
            if (stmt != null) {
                statements.add(stmt);
            }
            skipNewlines();
        }
        int line = tokens.isEmpty() ? 1 : tokens.getLast().line();
        return new ASTNode.BlockNode(statements, line);
    }

    // ======================== 语句 ========================

    private ASTNode parseStatement() {
        if (check(Token.Type.IF)) {
            return parseIf();
        }
        if (check(Token.Type.RETURN)) {
            return parseReturn();
        }
        if (check(Token.Type.NEWLINE) || check(Token.Type.EOF)) {
            advance();
            return null;
        }
        ASTNode expr = parseExpression();
        expectNewlineOrEOF();
        return expr;
    }

    /**
     * return [expr]
     */
    private ASTNode parseReturn() {
        int line = advance().line(); // 消费 "return"
        // 如果直接换行或 EOF，return 无值
        if (check(Token.Type.NEWLINE) || check(Token.Type.EOF)) {
            expectNewlineOrEOF();
            return new ASTNode.ReturnNode(null, line);
        }
        ASTNode value = parseExpression();
        expectNewlineOrEOF();
        return new ASTNode.ReturnNode(value, line);
    }

    /**
     * if / elseif / else / endif
     *
     * if <cond>
     *     <then-body>
     * elseif <cond2>
     *     <elseif-body>
     * else
     *     <else-body>
     * endif
     */
    private ASTNode.IfNode parseIf() {
        return parseIf(true);
    }

    private ASTNode.IfNode parseIf(boolean consumeKeyword) {
        int line;
        if (consumeKeyword) {
            line = advance().line(); // 消费 "if"
        } else {
            line = previous().line(); // "elseif" 已被 match() 消费，取其行号
        }
        ASTNode condition = parseExpression();
        expectNewlineOrEOF();

        List<ASTNode> thenBody = parseBlock();

        List<ASTNode> elseBody = new ArrayList<>();
        if (match(Token.Type.ELSEIF)) {
            // elseif → 递归解析为嵌套的 if，放在 else 分支里
            ASTNode.IfNode elif = parseIf(false);
            elseBody.add(elif);
        } else if (match(Token.Type.ELSE)) {
            expectNewlineOrEOF();
            elseBody = parseBlock();
            expect(Token.Type.ENDIF, "Expected 'endif'");
            expectNewlineOrEOF();
        } else {
            expect(Token.Type.ENDIF, "Expected 'endif'");
            expectNewlineOrEOF();
        }

        return new ASTNode.IfNode(condition, thenBody, elseBody, line);
    }

    /**
     * 解析块内的语句，直到遇到 else/elseif/endif
     */
    private List<ASTNode> parseBlock() {
        List<ASTNode> statements = new ArrayList<>();
        skipNewlines();
        while (!isAtEnd() && !check(Token.Type.ELSE) && !check(Token.Type.ELSEIF) && !check(Token.Type.ENDIF)) {
            ASTNode stmt = parseStatement();
            if (stmt != null) {
                statements.add(stmt);
            }
            skipNewlines();
        }
        return statements;
    }

    // ======================== 表达式（优先级递增） ========================

    private ASTNode parseExpression() {
        return parseOr();
    }

    private ASTNode parseOr() {
        ASTNode left = parseAnd();
        while (match(Token.Type.OR)) {
            int line = previous().line();
            ASTNode right = parseAnd();
            left = new ASTNode.BinaryOpNode("||", left, right, line);
        }
        return left;
    }

    private ASTNode parseAnd() {
        ASTNode left = parseComparison();
        while (match(Token.Type.AND)) {
            int line = previous().line();
            ASTNode right = parseComparison();
            left = new ASTNode.BinaryOpNode("&&", left, right, line);
        }
        return left;
    }

    private ASTNode parseComparison() {
        ASTNode left = parseAdditive();
        if (matchAny(Token.Type.EQ, Token.Type.NEQ, Token.Type.GT, Token.Type.GTE, Token.Type.LT, Token.Type.LTE)) {
            String op = previous().value();
            int line = previous().line();
            ASTNode right = parseAdditive();
            return new ASTNode.BinaryOpNode(op, left, right, line);
        }
        return left;
    }

    private ASTNode parseAdditive() {
        ASTNode left = parseMultiplicative();
        while (matchAny(Token.Type.PLUS, Token.Type.MINUS)) {
            String op = previous().value();
            int line = previous().line();
            ASTNode right = parseMultiplicative();
            left = new ASTNode.BinaryOpNode(op, left, right, line);
        }
        return left;
    }

    private ASTNode parseMultiplicative() {
        ASTNode left = parseUnary();
        while (matchAny(Token.Type.MULTIPLY, Token.Type.DIVIDE)) {
            String op = previous().value();
            int line = previous().line();
            ASTNode right = parseUnary();
            left = new ASTNode.BinaryOpNode(op, left, right, line);
        }
        return left;
    }

    private ASTNode parseUnary() {
        if (check(Token.Type.NOT)) {
            advance();
            int line = previous().line();
            ASTNode operand = parseUnary();
            return new ASTNode.UnaryOpNode("!", operand, line);
        }
        if (check(Token.Type.MINUS)) {
            advance();
            int line = previous().line();
            ASTNode operand = parseUnary();
            return new ASTNode.UnaryOpNode("-", operand, line);
        }
        return parseCall();
    }

    /**
     * 解析函数调用或原子表达式
     *
     * 三种情况：
     *   1. IDENTIFIER "(" args ")"  → 有括号的函数调用
     *   2. IDENTIFIER atom+         → 裸参数的函数调用 (如 perm "vip")
     *   3. IDENTIFIER（后面是运算符或换行） → 无参函数调用 (如 level)
     *   4. atom                     → 字面量或括号表达式
     */
    private ASTNode parseCall() {
        if (check(Token.Type.IDENTIFIER)) {
            Token name = advance();

            // 情况1: 有括号的函数调用 name(...)
            if (match(Token.Type.LPAREN)) {
                List<ASTNode> args = new ArrayList<>();
                if (!check(Token.Type.RPAREN)) {
                    args.add(parseExpression());
                    while (match(Token.Type.COMMA)) {
                        args.add(parseExpression());
                    }
                }
                expect(Token.Type.RPAREN, "Expected ')'");
                return new ASTNode.FunctionCallNode(name.value(), args, name.line());
            }

            // 情况2/3: 判断后面是否跟着可作为参数的 token（STRING/NUMBER/BOOLEAN/IDENTIFIER）
            // 如果是，收集为裸参数；否则是无参调用
            // IDENTIFIER 作为参数时会递归解析为函数调用（如 papi "%player_name%"）
            List<ASTNode> args = new ArrayList<>();
            while (isBareArgToken()) {
                if (check(Token.Type.IDENTIFIER)) {
                    args.add(parseCall());
                } else {
                    args.add(parseAtom());
                }
            }
            return new ASTNode.FunctionCallNode(name.value(), args, name.line());
        }

        return parseAtom();
    }

    /**
     * 判断当前 token 是否可以作为裸函数调用的参数
     * STRING / NUMBER / BOOLEAN / IDENTIFIER（函数调用）都可以
     */
    private boolean isBareArgToken() {
        if (isAtEnd()) return false;
        Token.Type type = tokens.get(pos).type();
        return type == Token.Type.STRING || type == Token.Type.NUMBER || type == Token.Type.BOOLEAN || type == Token.Type.IDENTIFIER;
    }

    private ASTNode parseAtom() {
        Token tok = advance();
        return switch (tok.type()) {
            case STRING  -> new ASTNode.LiteralNode(tok.value(), tok.line());
            case NUMBER  -> new ASTNode.LiteralNode(Double.parseDouble(tok.value()), tok.line());
            case BOOLEAN -> new ASTNode.LiteralNode(Boolean.parseBoolean(tok.value()), tok.line());
            case IDENTIFIER -> new ASTNode.IdentifierNode(tok.value(), tok.line());
            case LPAREN -> {
                ASTNode expr = parseExpression();
                expect(Token.Type.RPAREN, "Expected ')'");
                yield expr;
            }
            default -> throw new ScriptException("Unexpected token " + tok + " at line " + tok.line());
        };
    }

    // ======================== 工具方法 ========================

    private Token advance() {
        return tokens.get(pos++);
    }

    private Token previous() {
        return tokens.get(pos - 1);
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private boolean check(Token.Type type) {
        return !isAtEnd() && tokens.get(pos).type() == type;
    }

    private boolean match(Token.Type type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }

    private boolean matchAny(Token.Type... types) {
        for (Token.Type type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private void expect(Token.Type type, String message) {
        if (!check(type)) {
            int line = isAtEnd() ? tokens.getLast().line() : tokens.get(pos).line();
            throw new ScriptException(message + " at line " + line);
        }
        advance();
    }

    private void expectNewlineOrEOF() {
        if (!isAtEnd() && !check(Token.Type.NEWLINE) && !check(Token.Type.EOF)) {
            throw new ScriptException("Expected end of line at line " + tokens.get(pos).line());
        }
        if (check(Token.Type.NEWLINE)) advance();
    }

    private void skipNewlines() {
        while (check(Token.Type.NEWLINE)) advance();
    }

    private boolean isAtEnd() {
        return pos >= tokens.size() || tokens.get(pos).type() == Token.Type.EOF;
    }
}
