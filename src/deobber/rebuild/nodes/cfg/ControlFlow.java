package deobber.rebuild.nodes.cfg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deobber.ByteUtils;
import deobber.rebuild.nodes.InsnNode;
import deobber.rebuild.nodes.attributes.CodeAttribute;
import deobber.rebuild.nodes.attributes.InsnList;

public class ControlFlow extends Block {

	public final CodeAttribute code;
	Block currentBlock;
	Map<Integer, Block> blocks = new HashMap<>();

	public ControlFlow(CodeAttribute codeAttribute) {
		super(0);

		this.code = codeAttribute;

	}

	private void nextBlock(int line, boolean isChild) {

		if (blocks.containsKey(line)) {
			Block nextBlock = blocks.get(line);
			if (!isChild) {
				currentBlock.addChild(nextBlock, true);
			}
			currentBlock = nextBlock;

		} else {
			Block nextBlock = new Block(line);
			if (!isChild) {
				currentBlock.addChild(nextBlock, true);
			}
			blocks.put(line, nextBlock);
			currentBlock = nextBlock;

		}
	}

	private void checkBlock(int line) {
		if (line != 0 && blocks.containsKey(line)) {
			Block nextBlock = blocks.get(line);
			if (currentBlock.equals(nextBlock)) {
				return;
			}
			currentBlock.addChild(nextBlock, true);
			currentBlock = nextBlock;
		}
	}

	public void parse() {
		blocks.clear();
		currentBlock = this;
		blocks.put(0, currentBlock);
		int line = 0;
		InsnList nodes = code.instructions;
		for (int i = 0; i < nodes.size(); i++) {
			InsnNode iNode = nodes.get(i);
			checkBlock(line);
			switch (iNode.opcode) {
			case if_acmpeq:
			case if_icmpeq:
			case if_acmpne:
			case if_icmpge:
			case if_icmpgt:
			case if_icmple:
			case if_icmplt:
			case if_icmpne:
			case ifeq:
			case ifge:
			case ifne:
			case ifgt:
			case ifle:
			case iflt:
			case ifnonnull:
			case ifnull: {
				currentBlock.add(iNode);

				short offset = ByteUtils.toShort(iNode.args[0], iNode.args[1]);
				int jumpPos = line + offset;
				if (!blocks.containsKey(jumpPos)) {
					blocks.put(jumpPos, new Block(jumpPos));
				}
				currentBlock.addChild(blocks.get(jumpPos), false);

				nextBlock(line + 1, false);
			}
				break;

			case ret:
			case return_:
			case areturn:
			case dreturn:
			case ireturn:
			case freturn:
			case lreturn: {
				currentBlock.add(iNode);
				nextBlock(line + 1, true);
			}
				break;
			case jsr: {
				currentBlock.add(iNode);

			}
				break;
			case jsr_w: {
				currentBlock.add(iNode);

			}
				break;
			case goto_: {
				short offset = ByteUtils.toShort(iNode.args[0], iNode.args[1]);
				nextBlock(line, false);

				int jumpPos = line + offset;
				if (!blocks.containsKey(jumpPos)) {
					blocks.put(jumpPos, new Block(jumpPos));

				}
				currentBlock.add(iNode);
				currentBlock.addChild(blocks.get(jumpPos), false);

				nextBlock(line + 1, true);

			}
				break;
			case goto_w: {

			}
				break;
			default:
				currentBlock.add(iNode);
				if (currentBlock.instructions.size() == 1) {
					blocks.put(line, currentBlock);
				}
				break;
			}
			line++;
			line += iNode.argLength;
		}

		for (Block block : blocks.values()) {
			block.reorg();
		}
	}

	
}
