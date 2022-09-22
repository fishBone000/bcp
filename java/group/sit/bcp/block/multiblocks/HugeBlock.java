package group.sit.bcp.block.multiblocks;

public class HugeBlock extends MultiBlock{
	public HugeBlock(Properties properties) {
		super(properties);
	}
	protected int widthStart() { return 0; }
	protected int widthEnd() { return 3; }
	protected int heightStart() { return 0; }
	protected int heightEnd() { return 1; }
	protected int depthStart() { return 0; }
	protected int depthEnd() { return 2; }
}
