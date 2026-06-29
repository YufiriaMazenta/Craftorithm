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

    /**
     * 编译 AST 为指令序列
     * @param name 脚本名称（用于调试）
     * @param node AST 根节点
     * @return 编译后的脚本
     */
    public CompiledScript compile(String name, ASTNode node) {
        List<Instruction> instructions = new ArrayList<>();
        emitNode(node, instructions);
        instructions.add(Instruction.of(OpCode.RETURN, 0));
        return new CompiledScript(name, instructions);
    }

    private void emitNode(ASTNode node, List<Instruction> instructions) {
        switch (node) {
            case ASTNode.LiteralNode lit     -> emitLiteral(lit, instructions);
            case ASTNode.IdentifierNode id   -> emitIdentifier(id, instructions);
            case ASTNode.BinaryOpNode bin    -> emitBinaryOp(bin, instructions);
            case ASTNode.UnaryOpNode un      -> emitUnaryOp(un, instructions);
            case ASTNode.FunctionCallNode fn -> emitFunctionCall(fn, instructions);
            case ASTNode.IfNode ifNode       -> emitIf(ifNode, instructions);
            case ASTNode.ReturnNode ret      -> emitReturn(ret, instructions);
            case ASTNode.BlockNode block     -> emitBlock(block, instructions);
        }
    }

    private void emitLiteral(ASTNode.LiteralNode node, List<Instruction> instructions) {
        ScriptValue value = switch (node.value()) {
            case String s  -> ScriptValue.of(s);
            case Double d  -> ScriptValue.of(d);
            case Boolean b -> ScriptValue.of(b);
            case Number n  -> ScriptValue.of(n.doubleValue());
            default        -> ScriptValue.nil();
        };
        instructions.add(Instruction.push(value, node.line()));
    }

    private void emitIdentifier(ASTNode.IdentifierNode node, List<Instruction> instructions) {
        instructions.add(Instruction.call(node.name(), 0, node.line()));
    }

    private void emitBinaryOp(ASTNode.BinaryOpNode node, List<Instruction> instructions) {
        if (node.operator().equals("&&")) {
            emitNode(node.left(), instructions);
            instructions.add(Instruction.of(OpCode.DUP, node.line()));
            int jumpIdx = instructions.size();
            instructions.add(Instruction.jump(OpCode.JUMP_IF_FALSE, 0, node.line()));
            instructions.add(Instruction.of(OpCode.POP, node.line()));
            emitNode(node.right(), instructions);
            patchJump(jumpIdx, instructions);
            return;
        }

        if (node.operator().equals("||")) {
            emitNode(node.left(), instructions);
            instructions.add(Instruction.of(OpCode.DUP, node.line()));
            int jumpIdx = instructions.size();
            instructions.add(Instruction.jump(OpCode.JUMP_IF_FALSE, 0, node.line()));
            int skipRightIdx = instructions.size();
            instructions.add(Instruction.jump(OpCode.JUMP, 0, node.line()));

            patchJump(jumpIdx, instructions);
            instructions.add(Instruction.of(OpCode.POP, node.line()));
            emitNode(node.right(), instructions);

            patchJump(skipRightIdx, instructions);
            return;
        }

        emitNode(node.left(), instructions);
        emitNode(node.right(), instructions);
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

    private void emitUnaryOp(ASTNode.UnaryOpNode node, List<Instruction> instructions) {
        emitNode(node.operand(), instructions);
        if (node.operator().equals("!")) {
            instructions.add(Instruction.of(OpCode.NOT, node.line()));
        }
    }

    private void emitFunctionCall(ASTNode.FunctionCallNode node, List<Instruction> instructions) {
        for (ASTNode arg : node.args()) {
            emitNode(arg, instructions);
        }
        instructions.add(Instruction.call(node.name(), node.args().size(), node.line()));
    }

    private void emitIf(ASTNode.IfNode node, List<Instruction> instructions) {
        emitNode(node.condition(), instructions);

        int jumpToElseIdx = instructions.size();
        instructions.add(Instruction.jump(OpCode.JUMP_IF_FALSE, 0, node.line()));

        for (ASTNode stmt : node.thenBody()) {
            emitNode(stmt, instructions);
        }

        if (!node.elseBody().isEmpty()) {
            int jumpOverElseIdx = instructions.size();
            instructions.add(Instruction.jump(OpCode.JUMP, 0, node.line()));

            patchJump(jumpToElseIdx, instructions);

            for (ASTNode stmt : node.elseBody()) {
                emitNode(stmt, instructions);
            }

            patchJump(jumpOverElseIdx, instructions);
        } else {
            patchJump(jumpToElseIdx, instructions);
        }
    }

    private void emitBlock(ASTNode.BlockNode node, List<Instruction> instructions) {
        for (ASTNode stmt : node.statements()) {
            emitNode(stmt, instructions);
        }
    }

    private void emitReturn(ASTNode.ReturnNode node, List<Instruction> instructions) {
        if (node.value() != null) {
            emitNode(node.value(), instructions);
        } else {
            instructions.add(Instruction.push(ScriptValue.nil(), node.line()));
        }
        instructions.add(Instruction.of(OpCode.RETURN, node.line()));
    }

    private void patchJump(int instructionIndex, List<Instruction> instructions) {
        Instruction old = instructions.get(instructionIndex);
        instructions.set(instructionIndex, Instruction.jump(old.opCode(), instructions.size(), old.line()));
    }
}
