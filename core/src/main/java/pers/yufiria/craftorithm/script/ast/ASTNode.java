package pers.yufiria.craftorithm.script.ast;

import java.util.List;

/**
 * AST 节点基类
 * 所有语法树节点都继承此类
 */
public abstract sealed class ASTNode permits
    ASTNode.LiteralNode,
    ASTNode.IdentifierNode,
    ASTNode.BinaryOpNode,
    ASTNode.UnaryOpNode,
    ASTNode.FunctionCallNode,
    ASTNode.IfNode,
    ASTNode.ReturnNode,
    ASTNode.BlockNode {

    private final int line;

    protected ASTNode(int line) {
        this.line = line;
    }

    public int line() {
        return line;
    }

    /**
     * 字面量: "hello", 123, true
     */
    public static final class LiteralNode extends ASTNode {
        private final Object value; // String, Double, Boolean

        public LiteralNode(Object value, int line) {
            super(line);
            this.value = value;
        }

        public Object value() { return value; }

        @Override
        public String toString() {
            return "Literal(" + value + ")";
        }
    }

    /**
     * 标识符: 变量名或未解析的函数名
     */
    public static final class IdentifierNode extends ASTNode {
        private final String name;

        public IdentifierNode(String name, int line) {
            super(line);
            this.name = name;
        }

        public String name() { return name; }

        @Override
        public String toString() {
            return "Id(" + name + ")";
        }
    }

    /**
     * 二元运算: == != > >= < <= && ||
     */
    public static final class BinaryOpNode extends ASTNode {
        private final String operator;
        private final ASTNode left;
        private final ASTNode right;

        public BinaryOpNode(String operator, ASTNode left, ASTNode right, int line) {
            super(line);
            this.operator = operator;
            this.left = left;
            this.right = right;
        }

        public String operator() { return operator; }
        public ASTNode left()    { return left; }
        public ASTNode right()   { return right; }

        @Override
        public String toString() {
            return "(" + left + " " + operator + " " + right + ")";
        }
    }

    /**
     * 一元运算: ! (取反)
     */
    public static final class UnaryOpNode extends ASTNode {
        private final String operator;
        private final ASTNode operand;

        public UnaryOpNode(String operator, ASTNode operand, int line) {
            super(line);
            this.operator = operator;
            this.operand = operand;
        }

        public String operator() { return operator; }
        public ASTNode operand() { return operand; }
    }

    /**
     * 函数调用: perm("xxx"), command("say hello")
     */
    public static final class FunctionCallNode extends ASTNode {
        private final String name;
        private final java.util.List<ASTNode> args;

        public FunctionCallNode(String name, java.util.List<ASTNode> args, int line) {
            super(line);
            this.name = name;
            this.args = args;
        }

        public String name()                      { return name; }
        public List<ASTNode> args()     { return args; }

        @Override
        public String toString() {
            return name + "(" + args + ")";
        }
    }

    /**
     * if / elseif / else / endif 块
     */
    public static final class IfNode extends ASTNode {
        private final ASTNode condition;
        private final java.util.List<ASTNode> thenBody;
        private final java.util.List<ASTNode> elseBody;

        public IfNode(ASTNode condition, java.util.List<ASTNode> thenBody,
                      java.util.List<ASTNode> elseBody, int line) {
            super(line);
            this.condition = condition;
            this.thenBody = thenBody;
            this.elseBody = elseBody;
        }

        public ASTNode condition()                  { return condition; }
        public List<ASTNode> thenBody()   { return thenBody; }
        public List<ASTNode> elseBody()   { return elseBody; }
    }

    /**
     * return 语句: return expr
     */
    public static final class ReturnNode extends ASTNode {
        private final ASTNode value; // 可以为 null（return 无参）

        public ReturnNode(ASTNode value, int line) {
            super(line);
            this.value = value;
        }

        public ASTNode value() { return value; }
    }

    /**
     * 语句块：多条语句的容器
     */
    public static final class BlockNode extends ASTNode {
        private final java.util.List<ASTNode> statements;

        public BlockNode(java.util.List<ASTNode> statements, int line) {
            super(line);
            this.statements = statements;
        }

        public List<ASTNode> statements() { return statements; }
    }
}
