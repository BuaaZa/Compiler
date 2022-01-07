import java.util.ArrayList;

public class Variable extends Symbol{

    public int blockNum;
    public int regIndex;
    public int value;
    public boolean isConst ;
    public boolean isDefined;

    public boolean isParam;
    public ArrayList<Integer> arrayDimensions;

    public void setRegIndex(int regIndex) {
        this.regIndex = regIndex;
    }

    public Variable(int BType,ArrayList<Integer> arrayDimensions){
        super(BType);
        this.isParam = true;
        this.arrayDimensions = new ArrayList<>();
        this.arrayDimensions.addAll(arrayDimensions);
    }

    public Variable(String name, int BType, int blockNum, int regIndex, ArrayList<Integer> arrayDimensions, boolean isConst, boolean isParam) {
        super(name, BType);
        this.blockNum = blockNum;
        this.regIndex = regIndex;
        this.isConst = isConst;
        this.isDefined = true;
        this.isParam = isParam;

        this.arrayDimensions = new ArrayList<>();
        this.arrayDimensions.addAll(arrayDimensions);
    }



    public Variable(String name, int BType,int blockNum, int regIndex, ArrayList<Integer> arrayDimensions,boolean isConst) {
        super(name, BType);
        this.blockNum = blockNum;
        this.regIndex = regIndex;
        this.isConst = isConst;
        this.isDefined = true;
        this.isParam = false;

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
        this.isParam = false;
        allocaVariable();
    }

    public Variable(String name, int BType,int blockNum, int regIndex,boolean isParam) {
        super(name, BType);
        this.blockNum = blockNum;
        this.regIndex = regIndex;
        this.isConst = false;
        this.isDefined = true;
        this.isParam = isParam;
    }


    public Variable(String name, int BType,int value ){
        super(name, BType);
        this.value = value;
        this.isConst = true;
        this.isParam = false;
    }

    public Variable(int BType){
        super(BType);
    }


    public void allocaArray() {
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

    public Exp getArrayElementPtr(ArrayList<Exp> indexList,boolean passAsParam){

        Exp ret = null;
        StringBuilder str = Compiler.res;

        if(indexList.size() > 0 ){
            str.append("    ")
                    .append((Compiler.varList.blockNum == 0) ? "@" : ("%b"+Compiler.varList.blockNum))
                    .append("v").append(Compiler.varList.regNum)
                    .append(" = getelementptr ")
                    .append(getArrayAllocaInfo(arrayDimensions.size())).append(", ")
                    .append(getArrayAllocaInfo(arrayDimensions.size())).append("* ")
                    .append(this);

            if(!isParam)
                str.append(", i32 0");

            str.append(", ");
            for (int i = 0; i < indexList.size(); i++) {
                str.append("i32 ").append(indexList.get(i));
                if(i<indexList.size()-1)
                    str.append(", ");
            }

            str.append("\n");
        }


        if(passAsParam){
            if(indexList.size()!=getArraySize()){
                if(indexList.size()==0){
                    if(isParam){
                        ret = new Exp(blockNum,regIndex,Symbol.TypePointer,arrayDimensions);
                    }else{
                        ArrayList<Integer> tempArray = new ArrayList<>();
                        for(int i=1;i<arrayDimensions.size();i++){
                            tempArray.add(arrayDimensions.get(i));
                        }

                        str.append("    ")
                                .append((Compiler.varList.blockNum == 0) ? "@" : ("%b"+Compiler.varList.blockNum))
                                .append("v").append(Compiler.varList.regNum)
                                .append(" = getelementptr ")
                                .append(getArrayAllocaInfo(arrayDimensions.size())).append(", ")
                                .append(getArrayAllocaInfo(arrayDimensions.size())).append("* ")
                                .append(this).append(", i32 0, i32 0");

                        ret = new Exp(Compiler.varList.blockNum,Compiler.varList.regNum++,Symbol.TypePointer,tempArray);
                    }
                }else{
                    ArrayList<Integer> tempArray = new ArrayList<>();
                    for(int i=(isParam)?indexList.size():indexList.size()+1;i<arrayDimensions.size();i++){
                        tempArray.add(arrayDimensions.get(i));
                    }

                    Exp exp = new Exp(Compiler.varList.blockNum,Compiler.varList.regNum++);
                    str.append("    ")
                            .append((Compiler.varList.blockNum == 0) ? "@" : ("%b"+Compiler.varList.blockNum))
                            .append("v").append(Compiler.varList.regNum)
                            .append(" = getelementptr ")
                            .append(getArrayAllocaInfo(tempArray.size()+1)).append(", ")
                            .append(getArrayAllocaInfo(tempArray.size()+1)).append("* ")
                            .append(exp).append(", i32 0, i32 0");

                    ret = new Exp(Compiler.varList.blockNum,Compiler.varList.regNum++,Symbol.TypePointer,tempArray);
                }
            }else ret = new Exp(Compiler.varList.blockNum,Compiler.varList.regNum++);
        }else ret = new Exp(Compiler.varList.blockNum,Compiler.varList.regNum++);

        return ret;
    }

    public int getArrayDimension(int index){
        return arrayDimensions.get(index);
    }

    public int getArraySize(){
        return (isParam)?arrayDimensions.size()+1:arrayDimensions.size();
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
        Exp arrayElementPtr = getArrayElementPtr(indexList,false);
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
            if(arrayDimensions!=null){
                str.append("    ").append(this).append(" = alloca ").append(getArrayAllocaInfo(arrayDimensions.size()))
                        .append("*\n");
            }else{
                str.append("    ").append(this).append(" = alloca i32\n");
            }
        }

    }

    public void storeVariable(Exp initVal){
        StringBuilder str = Compiler.res;
        if(arrayDimensions!=null){
            StringBuilder arrayAllocaInfo = getArrayAllocaInfo(arrayDimensions.size());
            str.append("    ")
                    .append("store ").append(arrayAllocaInfo).append("* ").append(initVal)
                    .append(", ").append(arrayAllocaInfo).append("* * ").append(this).append("\n");
        }else{

            str.append("    ")
                    .append("store i32 ")
                    .append(initVal)
                    .append(", i32* ")
                    .append(this)
                    .append("\n");
            this.isDefined = true;
        }

    }

    public Exp loadVariable(){
        //todo:函数
        Exp ret = null;
        StringBuilder str = Compiler.res;

        if(isDefined){
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
