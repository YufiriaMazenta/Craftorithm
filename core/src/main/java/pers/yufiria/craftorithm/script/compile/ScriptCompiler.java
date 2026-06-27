package pers.yufiria.craftorithm.script.compile;

import pers.yufiria.craftorithm.script.ScriptValue;
import pers.yufiria.craftorithm.script.ast.ASTNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 脚本编译器
 * 将 AST 编译为栈式虚拟机的指令序列
 */
public class ScriptCompiler {

    private final List<Instruction> instructions = new ArrayList<>();

    /**
     * 编译 AST 为指令序列
     * @param name 脚本名称（用于调试）
     * @param node AST 根节点
     * @return 编译后的脚本
     */
    public CompiledScript compile(String name, ASTNode node) {
        instructions.clear();
        emitNode(node);
        instructions.add(Instruction.of(OpCode.RETURN, 0));
        return new CompiledScript(name, new ArrayList<>(instructions));//不重新包装列表导致每个被编译的脚本都一样的内容,垃圾mimo
    }

    private void emitNode(ASTNode node) {
        switch (node) {
            case ASTNode.LiteralNode lit     -> emitLiteral(lit);
            case ASTNode.IdentifierNode id   -> emitIdentifier(id);
            case ASTNode.BinaryOpNode bin    -> emitBinaryOp(bin);
            case ASTNode.UnaryOpNode un      -> emitUnaryOp(un);
            case ASTNode.FunctionCallNode fn -> emitFunctionCall(fn);
            case ASTNode.IfNode ifNode       -> emitIf(ifNode);
            case ASTNode.ReturnNode ret      -> emitReturn(ret);
            case ASTNode.BlockNode block     -> emitBlock(block);
        }
    }

    private void emitLiteral(ASTNode.LiteralNode node) {
        ScriptValue value = switch (node.value()) {
            case String s  -> ScriptValue.of(s);
            case Double d  -> ScriptValue.of(d);
            case Boolean b -> ScriptValue.of(b);
            case Number n  -> ScriptValue.of(n.doubleValue());
            default        -> ScriptValue.nil();
        };
        instructions.add(Instruction.push(value, node.line()));
    }

    private void emitIdentifier(ASTNode.IdentifierNode node) {
        // 标识符作为无参函数调用
        instructions.add(Instruction.call(node.name(), 0, node.line()));
    }

    private void emitBinaryOp(ASTNode.BinaryOpNode node) {
        if (node.operator().equals("&&")) {
            // 短路求值: left 为 false 时直接返回 false，不计算 right
            emitNode(node.left());
            instructions.add(Instruction.of(OpCode.DUP, node.line()));
            int jumpIdx = instructions.size();
            instructions.add(Instruction.jump(OpCode.JUMP_IF_FALSE, 0, node.line()));
            instructions.add(Instruction.of(OpCode.POP, node.line()));
            emitNode(node.right());
            patchJump(jumpIdx);
            return;
        }

        if (node.operator().equals("||")) {
            // 短路求值: left 为 true 时直接返回 true，不计算 right
            emitNode(node.left());
            // 复制栈顶，因为 JUMP_IF_FALSE 会弹出
            instructions.add(Instruction.of(OpCode.DUP, node.line()));
            int jumpIdx = instructions.size();
            instructions.add(Instruction.jump(OpCode.JUMP_IF_FALSE, 0, node.line())); // 占位
            // left 为 true，跳过 right，此时栈顶已经是 true（从 DUP）
            int skipRightIdx = instructions.size();
            instructions.add(Instruction.jump(OpCode.JUMP, 0, node.line())); // 占位

            // left 为 false，弹出 DUP 的值，计算 right
            patchJump(jumpIdx);
            instructions.add(Instruction.of(OpCode.POP, node.line())); // 弹出 left
            emitNode(node.right());

            // right 计算完毕，跳转到此处
            patchJump(skipRightIdx);
            return;
        }

        // 其他运算符
        emitNode(node.left());
        emitNode(node.right());
        OpCode opCode = switch (node.operator()) {
            case "==" -> OpCode.CMP_EQ;
            case "!=" -> OpCode.CMP_NEQ;
            case ">"  -> OpCode.CMP_GT;
            case ">=" -> OpCode.CMP_GTE;
            case "<"  -> OpCode.CMP_LT;
            case "<=" -> OpCode.CMP_LTE;
            default -> throw new IllegalArgumentException("Unknown operator: " + node.operator());
        };
        instructions.add(Instruction.of(opCode, node.line()));
    }

    private void emitUnaryOp(ASTNode.UnaryOpNode node) {
        emitNode(node.operand());
        if (node.operator().equals("!")) {
            instructions.add(Instruction.of(OpCode.NOT, node.line()));
        }
    }

    private void emitFunctionCall(ASTNode.FunctionCallNode node) {
        for (ASTNode arg : node.args()) {
            emitNode(arg);
        }
        instructions.add(Instruction.call(node.name(), node.args().size(), node.line()));
    }

    private void emitIf(ASTNode.IfNode node) {
        // 编译条件
        emitNode(node.condition());

        // 条件为 false 时跳转到 else 分支
        int jumpToElseIdx = instructions.size();
        instructions.add(Instruction.jump(OpCode.JUMP_IF_FALSE, 0, node.line()));

        // 编译 then 分支
        for (ASTNode stmt : node.thenBody()) {
            emitNode(stmt);
        }

        if (!node.elseBody().isEmpty()) {
            // then 分支结束后跳过 else
            int jumpOverElseIdx = instructions.size();
            instructions.add(Instruction.jump(OpCode.JUMP, 0, node.line()));

            // 修正 else 跳转地址
            patchJump(jumpToElseIdx);

            // 编译 else 分支
            for (ASTNode stmt : node.elseBody()) {
                emitNode(stmt);
            }

            // 修正跳过 else 的地址
            patchJump(jumpOverElseIdx);
        } else {
            // 没有 else，修正跳转地址到 if 块之后
            patchJump(jumpToElseIdx);
        }
    }

    private void emitBlock(ASTNode.BlockNode node) {
        for (ASTNode stmt : node.statements()) {
            emitNode(stmt);
        }
    }

    private void emitReturn(ASTNode.ReturnNode node) {
        if (node.value() != null) {
            emitNode(node.value());
        } else {
            instructions.add(Instruction.push(ScriptValue.nil(), node.line()));
        }
        instructions.add(Instruction.of(OpCode.RETURN, node.line()));
    }

    /**
     * 修正跳转指令的目标地址为当前位置
     */
    private void patchJump(int instructionIndex) {
        Instruction old = instructions.get(instructionIndex);
        instructions.set(instructionIndex, Instruction.jump(old.opCode(), instructions.size(), old.line()));
    }
}
