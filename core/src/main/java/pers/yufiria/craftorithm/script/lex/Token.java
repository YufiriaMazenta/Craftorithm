package pers.yufiria.craftorithm.script.lex;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 词法分析产出的Token
 */
public record Token(Type type, String value, int line) {

    public enum Type {

        // 字面量
        STRING,     // "hello world"
        NUMBER,     // 123, 3.14
        BOOLEAN,    // true, false

        // 标识符（函数名、变量名）
        IDENTIFIER,

        // 运算符
        EQ,         // ==
        NEQ,        // !=
        GT,         // >
        GTE,        // >=
        LT,         // <
        LTE,        // <=
        AND,        // &&
        OR,         // ||
        NOT,        // !

        // 算术运算符
        PLUS,       // +
        MINUS,      // -
        MULTIPLY,   // *
        DIVIDE,     // /
        MODULO,     // %

        // 分隔符
        LPAREN,     // (
        RPAREN,     // )
        COMMA,      // ,

        // 控制流
        IF,
        ELSE,
        ELSEIF,
        ENDIF,
        RETURN,

        // 特殊
        NEWLINE,
        EOF;

        private static final Set<Type> OPERATOR_TYPES = Set.of(
            EQ, NEQ, GT, GTE, LT, LTE, AND, OR, PLUS, MINUS, MULTIPLY, DIVIDE, MODULO
        );

        public boolean isOperator() {
            return OPERATOR_TYPES.contains(this);
        }
    }

    @Override
    public @NotNull String toString() {
        return type + "(" + value + ")@" + line;
    }
}
