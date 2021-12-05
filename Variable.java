public class Variable extends Symbol{

    public int blockNum;
    public int regIndex;
    public int value;
    public boolean isConst ;
    public boolean isDefined;

    public Variable(String name,  int BType,int blockNum, int regIndex) {
        super(name, BType);
        this.blockNum = blockNum;
        this.regIndex = regIndex;
        this.isConst = false;
        this.isDefined = false;
        allocaVariable();
    }

    public Variable(String name, int BType,int value ){
        super(name, BType);
        this.value = value;
        this.isConst = true;
    }

    public void allocaVariable(){
        StringBuilder str = Compiler.res;
        str.append("    ")
                .append(this)
                .append(" = alloca i32\n");
    }

    public void storeVariable(Exp initVal){
        StringBuilder str = Compiler.res;
        str.append("    ")
                .append("store i32 ")
                .append(initVal)
                .append(", i32* ")
                .append(this)
                .append("\n");
        this.isDefined = true;
    }

    public Exp loadVariable(){
        Exp ret = null;
        if(isDefined){
            StringBuilder str = Compiler.res;
            str.append("    ")
                    .append((Compiler.varList.blockNum == 0) ? "@" : ("%b"+Compiler.varList.blockNum))
                    .append("v")
                    .append(Compiler.varList.regNum)
                    .append(" = load i32, i32* ")
                    .append(this)
                    .append("\n");

            ret = new Exp(Compiler.varList.blockNum,Compiler.varList.regNum++);
        }else System.exit(1);
        return ret;
    }

    @Override
    public String toString() {
        return ((blockNum == 0) ? "@" : ("%b"+blockNum))+"v"+regIndex;
    }
}
