import java.util.ArrayList;

public class Variable extends Symbol{

    public int blockNum;
    public int regIndex;
    public int value;
    public boolean isConst ;
    public boolean isDefined;

    public ArrayList<Integer> arrayDimensions;

    public Variable(String name, int BType,int blockNum, int regIndex, ArrayList<Integer> arrayDimensions,boolean isConst) {
        super(name, BType);
        this.blockNum = blockNum;
        this.regIndex = regIndex;
        this.isConst = isConst;
        this.isDefined = true;

        this.arrayDimensions = new ArrayList<>();
        this.arrayDimensions.addAll(arrayDimensions);
        allocaArray();
    }

    public Variable(String name, int BType,int blockNum, int regIndex) {
        super(name, BType);
        this.blockNum = blockNum;
        this.regIndex = regIndex;
        this.isConst = false;
        this.isDefined = blockNum == 0;
        allocaVariable();
    }

    public Variable(String name, int BType,int value ){
        super(name, BType);
        this.value = value;
        this.isConst = true;
    }
    private void allocaArray() {
        StringBuilder str = Compiler.res;
        if(blockNum==0){
            str.append(this).append(" = dso_local ").append((isConst)?"constant ":"global ");
            StringBuilder arrayInfo= getArrayAllocaInfo(arrayDimensions.size());
            str.append(arrayInfo).append(" ");
        }else{
            str.append("    ").append(this).append(" = alloca ");
            StringBuilder arrayInfo= getArrayAllocaInfo(arrayDimensions.size());
            str.append(arrayInfo).append("\n")
                    .append("    store ").append(arrayInfo).append(" zeroinitializer, ")
                    .append(arrayInfo).append("* ").append(this).append("\n");

        }
    }

    public Exp getArrayElementPtr(ArrayList<Exp> indexList){
        Exp ret = null;

        StringBuilder str = Compiler.res;
        str.append("    ")
                .append((Compiler.varList.blockNum == 0) ? "@" : ("%b"+Compiler.varList.blockNum))
                .append("v").append(Compiler.varList.regNum)
                .append(" = getelementptr ")
                .append(getArrayAllocaInfo(arrayDimensions.size())).append(", ")
                .append(getArrayAllocaInfo(arrayDimensions.size())).append("* ")
                .append(this).append(", i32 0, ");

        for (int i = 0; i < indexList.size(); i++) {
            str.append("i32 ").append(indexList.get(i));
            if(i<indexList.size()-1)
                str.append(", ");
        }

        str.append("\n");

        ret = new Exp(Compiler.varList.blockNum,Compiler.varList.regNum++,Symbol.TypePointer);
        return ret;
    }

    public int getArrayDimension(int index){
        return arrayDimensions.get(index);
    }

    public int getArraySize(){
        return arrayDimensions.size();
    }

    public StringBuilder getArrayAllocaInfo(int dimension){
        StringBuilder arrayInfo= new StringBuilder("i32");
        int length = arrayDimensions.size();
        for (int i = length-1 ; i >=length-dimension; i--) {
            arrayInfo.insert(0,"["+arrayDimensions.get(i)+" x ");
            arrayInfo.append("]");
        }
        return arrayInfo;
    }

    public Exp loadArrayElementVariable(ArrayList<Exp> indexList){
        Exp arrayElementPtr = getArrayElementPtr(indexList);
        //res.append("    store i32 ").append(exp).append(", i32* ").append(arrayElementPtr);
        Exp ret = null;
        StringBuilder str = Compiler.res;
        //todo：函數
        str.append("    ")
                .append((Compiler.varList.blockNum == 0) ? "@" : ("%b"+Compiler.varList.blockNum))
                .append("v")
                .append(Compiler.varList.regNum)
                .append(" = load i32, i32* ")
                .append(arrayElementPtr)
                .append("\n");

        ret = new Exp(Compiler.varList.blockNum,Compiler.varList.regNum++);

        return ret;
    }

    public void allocaVariable(){
        StringBuilder str = Compiler.res;
        if(blockNum == 0){
            str.append(this).append(" = dso_local global i32 ");
        }else{
            str.append("    ")
                    .append(this)
                    .append(" = alloca i32\n");
        }

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
