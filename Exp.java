public class Exp {
    public static final int Add = 1 ,Sub = 2, Mult = 3,Div = 4,Mod = 5;

    int value;
    boolean isConstValue;

    int blockNum;
    int regIndex;

    public static Exp ExpCompute(Exp a,Exp b,int operator){
        Exp ret;
        if(a.isConstValue && b.isConstValue){
            switch (operator){
                case Add -> ret = new Exp(a.value + b.value);
                case Sub -> ret = new Exp(a.value - b.value);
                case Mult -> ret = new Exp(a.value * b.value);
                case Div -> ret = new Exp(a.value / b.value);
                case Mod -> ret = new Exp(a.value % b.value);
                default -> throw new IllegalStateException("Unexpected value: " + operator);
            }
        }else{
            StringBuilder str = Compiler.res;
            str.append("    ")
                    .append((Compiler.varList.blockNum == 0) ? "@" : ("%b"+Compiler.varList.blockNum))
                    .append("v")
                    .append(Compiler.varList.regNum)
                    .append(" = ");
            switch (operator){
                case Add -> str.append("add ");
                case Sub -> str.append("sub ");
                case Mult -> str.append("mul ");
                case Div -> str.append("sdiv ");
                case Mod -> str.append("srem ");
                default -> throw new IllegalStateException("Unexpected value: " + operator);
            }
            str.append("i32 ")
                .append(a)
                .append(", ")
                .append(b)
                .append("\n");

            ret = new Exp(Compiler.varList.blockNum,Compiler.varList.regNum++);
        }

        return ret;
    }

    public Exp(int value) {
        this.value = value;
        this.isConstValue = true;
    }

    public Exp(int blockNum,int regIndex){
        this.blockNum = blockNum;
        this.regIndex = regIndex;
        this.isConstValue = false;
    }

    @Override
    public String toString() {
        if(isConstValue){
            return String.valueOf(value);
        }else{
            return ((blockNum == 0) ? "@" : "%")+"b"+blockNum+"v"+regIndex;
        }
    }
}
