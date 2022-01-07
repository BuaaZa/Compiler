import java.util.ArrayList;

public class Exp {
    public static final int Add = 1 ,Sub = 2, Mult = 3,
            Div = 4,Mod = 5,Or = 6,
            And = 7,Eq = 8, Neq = 9,
            S = 10,L = 11,Se = 12,
            Le =13;

    public int value;
    public boolean isConstValue;

    public int blockNum;
    public int regIndex;

    public int type;
    public ArrayList<Integer> arrayDimensions;

    public StringBuilder getArrayAllocaInfo(int dimension){
        StringBuilder arrayInfo= new StringBuilder("i32");
        int length = arrayDimensions.size();
        for (int i = length-1 ; i >=length-dimension; i--) {
            arrayInfo.insert(0,"["+arrayDimensions.get(i)+" x ");
            arrayInfo.append("]");
        }
        return arrayInfo;
    }

    public int getArraySize(){
        return arrayDimensions.size()+1;
    }

    private static void ExpZext(StringBuilder str) {
        Compiler.varList.regNum++;
        str.append("    ")
                .append((Compiler.varList.blockNum == 0) ? "@" : ("%b"+Compiler.varList.blockNum))
                .append("v").append(Compiler.varList.regNum)
                .append(" = ")
                .append("zext i1 ")
                .append((Compiler.varList.blockNum == 0) ? "@" : ("%b"+Compiler.varList.blockNum))
                .append("v").append(Compiler.varList.regNum-1)
                .append(" to i32\n");
    }

    public static Exp ExpCompute(Exp a,Exp b,int operator){
        if(a == null || b == null){
            System.exit(1);
        }
        Exp ret;
        StringBuilder str = Compiler.res;
        if(a.isConstValue && b.isConstValue){
            switch (operator){
                case Add -> ret = new Exp(a.value + b.value);
                case Sub -> ret = new Exp(a.value - b.value);
                case Mult -> ret = new Exp(a.value * b.value);
                case Div -> ret = new Exp(a.value / b.value);
                case Mod -> ret = new Exp(a.value % b.value);
                case Eq -> ret = new Exp((a.value == b.value)?1:0);
                case Neq -> ret = new Exp((a.value != b.value)?1:0);
                case S -> ret = new Exp((a.value < b.value)?1:0);
                case L -> ret = new Exp((a.value > b.value)?1:0);
                case Se -> ret = new Exp((a.value <= b.value)?1:0);
                case Le -> ret = new Exp((a.value >= b.value)?1:0);
                default -> throw new IllegalStateException("Unexpected value: " + operator);
            }
        }else{

            str.append("    ")
                    .append((Compiler.varList.blockNum == 0) ? "@" : ("%b"+Compiler.varList.blockNum))
                    .append("v").append(Compiler.varList.regNum)
                    .append(" = ");
            switch (operator){
                case Add -> str.append("add i32 ").append(a).append(", ").append(b).append("\n");
                case Sub -> str.append("sub i32 ").append(a).append(", ").append(b).append("\n");
                case Mult -> str.append("mul i32 ").append(a).append(", ").append(b).append("\n");
                case Div -> str.append("sdiv i32 ").append(a).append(", ").append(b).append("\n");
                case Mod -> str.append("srem i32 ").append(a).append(", ").append(b).append("\n");
                case Eq -> {
                    str.append("icmp eq i32 ").append(a).append(", ").append(b).append("\n");
                    ExpZext(str);
                }
                case Neq -> {
                    str.append("icmp ne i32 ").append(a).append(", ").append(b).append("\n");
                    ExpZext(str);
                }
                case S -> {
                    str.append("icmp slt i32 ").append(a).append(", ").append(b).append("\n");
                    ExpZext(str);
                }
                case L -> {
                    str.append("icmp sgt i32 ").append(a).append(", ").append(b).append("\n");
                    ExpZext(str);
                }
                case Se -> {
                    str.append("icmp sle i32 ").append(a).append(", ").append(b).append("\n");
                    ExpZext(str);
                }
                case Le -> {
                    str.append("icmp sge i32 ").append(a).append(", ").append(b).append("\n");
                    ExpZext(str);
                }
                default -> throw new IllegalStateException("Unexpected value: " + operator);
            }

            ret = new Exp(Compiler.varList.blockNum,Compiler.varList.regNum++);
        }

        return ret;
    }

    public static Exp ExpBoolCompute(Exp a,Exp b,int operator){
        //todo :短路求值
        if(a == null || b == null){
            System.exit(1);
        }
        Exp ret;
        StringBuilder str = Compiler.res;

        str.append("    ")
                .append((Compiler.varList.blockNum == 0) ? "@" : ("%b"+Compiler.varList.blockNum))
                .append("v")
                .append(Compiler.varList.regNum)
                .append(" = ");
        switch (operator){
            case And -> str.append("and ");
            case Or -> str.append("or ");
        }
        str.append("i1 ")
                .append(a)
                .append(", ")
                .append(b)
                .append("\n");

        ret = new Exp(Compiler.varList.blockNum,Compiler.varList.regNum++);

        return ret;
    }

    public static Exp ExpTransToBool(Exp a){
        if(a==null) System.exit(1);
        Exp ret;
        StringBuilder str = Compiler.res;

        str.append("    ")
                .append((Compiler.varList.blockNum == 0) ? "@" : ("%b"+Compiler.varList.blockNum))
                .append("v")
                .append(Compiler.varList.regNum)
                .append(" = ")
                .append("icmp ne i32 ")
                .append(a)
                .append(", 0\n");

        ret = new Exp(Compiler.varList.blockNum,Compiler.varList.regNum++);
        return ret;
    }

    public Exp(int value) {
        this.value = value;
        this.isConstValue = true;
        this.type = Symbol.TypeInt;
    }

    public Exp(int blockNum,int regIndex){
        this.blockNum = blockNum;
        this.regIndex = regIndex;
        this.isConstValue = false;
        this.type = Symbol.TypeInt;
    }

    public Exp(int blockNum,int regIndex,int type,ArrayList<Integer> arrayDimensions){
        this.blockNum = blockNum;
        this.regIndex = regIndex;
        this.isConstValue = false;
        this.type = type;
        this.arrayDimensions = new ArrayList<>();
        this.arrayDimensions.addAll(arrayDimensions);
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
