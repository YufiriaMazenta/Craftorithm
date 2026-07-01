package pers.yufiria.craftorithm.script.lex;

import pers.yufiria.craftorithm.script.ScriptException;

import java.util.ArrayList;
import java.util.List;

/**
 * 词法分析器
 * 将源代码文本转换为 Token 流
 *
 * 支持：
 * - 引号字符串: "hello world"
 * - 数字: 123, 3.14, -5
 * - 布尔: true, false
 * - 运算符: == != > >= < <= && ||
 * - 标识符: perm, command, my_func
 * - 控制流关键字: if, else, elseif, endif
 * - 注释: // 行注释
 */
public class ScriptLexer {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int pos;
    private int line = 1;

    public ScriptLexer(String source) {
        this.source = source;
    }

    /**
     * 执行词法分析
     * @return Token列表
     * @throws ScriptException 词法错误
     */
    public List<Token> tokenize() {
        tokens.clear();
        pos = 0;
        line = 1;

        while (pos < source.length()) {
            char c = source.charAt(pos);

            // 跳过空白（不换行）
            if (c == ' ' || c == '\t' || c == '\r') {
                pos++;
                continue;
            }

            // 换行
            if (c == '\n') {
                // 避免连续 NEWLINE
                if (tokens.isEmpty() || tokens.getLast().type() != Token.Type.NEWLINE) {
                    tokens.add(new Token(Token.Type.NEWLINE, "\\n", line));
                }
                line++;
                pos++;
                continue;
            }

            // 注释 //
            if (c == '/' && peek() == '/') {
                skipLineComment();
                continue;
            }

            // 引号字符串
            if (c == '"') {
                readString();
                continue;
            }

            // 数字（含负号前缀）
            if (isDigit(c) || (c == '-' && peek() != '\0' && isDigit(peek()))) {
                readNumber();
                continue;
            }

            // 运算符
            if (tryReadOperator()) continue;

            // 分隔符
            if (c == '(') { tokens.add(new Token(Token.Type.LPAREN, "(", line)); pos++; continue; }
            if (c == ')') { tokens.add(new Token(Token.Type.RPAREN, ")", line)); pos++; continue; }
            if (c == ',') { tokens.add(new Token(Token.Type.COMMA, ",", line)); pos++; continue; }

            // 标识符 / 关键字
            if (isAlpha(c) || c == '_' || c == '-') {
                readIdentifier();
                continue;
            }

            throw new ScriptException("Unexpected character '" + c + "' at line " + line);
        }

        // 确保以 EOF 结尾
        if (tokens.isEmpty() || tokens.getLast().type() != Token.Type.NEWLINE) {
            tokens.add(new Token(Token.Type.NEWLINE, "\\n", line));
        }
        tokens.add(new Token(Token.Type.EOF, "", line));
        return tokens;
    }

    private void skipLineComment() {
        pos += 2;
        while (pos < source.length() && source.charAt(pos) != '\n') pos++;
    }

    private void readString() {
        int start = ++pos; // 跳过开头 "
        StringBuilder sb = new StringBuilder();
        while (pos < source.length() && source.charAt(pos) != '"') {
            char c = source.charAt(pos);
            if (c == '\\' && pos + 1 < source.length()) {
                pos++;
                c = source.charAt(pos);
                sb.append(switch (c) {
                    case 'n' -> '\n';
                    case 't' -> '\t';
                    case '"' -> '"';
                    case '\\' -> '\\';
                    default -> '\\' + String.valueOf(c);
                });
            } else {
                sb.append(c);
            }
            pos++;
        }
        if (pos >= source.length()) {
            throw new ScriptException("Unterminated string at line " + line);
        }
        pos++; // 跳过结尾 "
        tokens.add(new Token(Token.Type.STRING, sb.toString(), line));
    }

    private void readNumber() {
        int start = pos;
        if (source.charAt(pos) == '-') pos++;
        while (pos < source.length() && isDigit(source.charAt(pos))) pos++;
        if (pos < source.length() && source.charAt(pos) == '.') {
            pos++;
            while (pos < source.length() && isDigit(source.charAt(pos))) pos++;
        }
        tokens.add(new Token(Token.Type.NUMBER, source.substring(start, pos), line));
    }

    private void readIdentifier() {
        int start = pos;
        while (pos < source.length() && (isAlphaNumeric(source.charAt(pos)) || source.charAt(pos) == '_' || source.charAt(pos) == '-')) {
            pos++;
        }
        String word = source.substring(start, pos);
        Token.Type type = switch (word) {
            case "if"       -> Token.Type.IF;
            case "else"     -> Token.Type.ELSE;
            case "elseif"   -> Token.Type.ELSEIF;
            case "endif"    -> Token.Type.ENDIF;
            case "return"   -> Token.Type.RETURN;
            case "true"     -> Token.Type.BOOLEAN;
            case "false"    -> Token.Type.BOOLEAN;
            default         -> Token.Type.IDENTIFIER;
        };
        tokens.add(new Token(type, word, line));
    }

    private boolean tryReadOperator() {
        char c = source.charAt(pos);
        char next = peek();

        if (c == '=' && next == '=') { tokens.add(new Token(Token.Type.EQ, "==", line)); pos += 2; return true; }
        if (c == '!' && next == '=') { tokens.add(new Token(Token.Type.NEQ, "!=", line)); pos += 2; return true; }
        if (c == '!' && next != '=') { tokens.add(new Token(Token.Type.NOT, "!", line)); pos++; return true; }
        if (c == '>' && next == '=') { tokens.add(new Token(Token.Type.GTE, ">=", line)); pos += 2; return true; }
        if (c == '<' && next == '=') { tokens.add(new Token(Token.Type.LTE, "<=", line)); pos += 2; return true; }
        if (c == '&' && next == '&') { tokens.add(new Token(Token.Type.AND, "&&", line)); pos += 2; return true; }
        if (c == '|' && next == '|') { tokens.add(new Token(Token.Type.OR, "||", line)); pos += 2; return true; }
        if (c == '>') { tokens.add(new Token(Token.Type.GT, ">", line)); pos++; return true; }
        if (c == '<') { tokens.add(new Token(Token.Type.LT, "<", line)); pos++; return true; }

        return false;
    }

    private char peek() {
        return pos + 1 < source.length() ? source.charAt(pos + 1) : '\0';
    }

    private static boolean isDigit(char c)  { return c >= '0' && c <= '9'; }
    private static boolean isAlpha(char c)  { return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'); }
    private static boolean isAlphaNumeric(char c) { return isAlpha(c) || isDigit(c); }
}
