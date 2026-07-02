package pers.yufiria.craftorithm.script.lex;

import org.jetbrains.annotations.NotNull;

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

        public boolean isOperator() {
            return this == EQ || this == NEQ || this == GT || this == GTE
                || this == LT || this == LTE || this == AND || this == OR
                || this == PLUS || this == MINUS || this == MULTIPLY || this == DIVIDE;
        }
    }

    @Override
    public @NotNull String toString() {
        return type + "(" + value + ")@" + line;
    }
}
