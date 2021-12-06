public class WhileRecorder {
    public Exp judgeBlock;
    public Exp exitBlock;

    public WhileRecorder prevWhileRecorder = null;

    public WhileRecorder(Exp judgeBlock, Exp exitBlock,WhileRecorder prev) {
        this.judgeBlock = judgeBlock;
        this.exitBlock = exitBlock;
        this.prevWhileRecorder = prev;
    }
}
