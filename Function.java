import java.util.ArrayList;

public class Function extends Symbol{


    ArrayList<Variable> argList = new ArrayList<>();

    public Function(String name, int FuncType) {
        super(name, FuncType);
    }

    public void addArg(Variable variable){
        argList.add(variable);
    }

    public Variable getArg(int index){
        return argList.get(index);
    }

    public int getArgSize(){
        return argList.size();
    }

    public boolean checkArgList(ArrayList<Exp> list){
        if(list == null && argList.isEmpty())
            return true;
        if(list != null  && list.size() == argList.size()){
            for (int i = 0; i < list.size(); i++) {
                Exp exp = list.get(i);
                Variable param = argList.get(i);
                if(exp.type != param.BType) {
                    Compiler.res.append("\n类型不同\n");
                    return false;
                }
                else if(exp.type == Symbol.TypePointer){
                    if(exp.getArraySize()!=param.getArraySize()) {

                        Compiler.res.append("\n"+exp.getArraySize()).append(param.getArraySize()).append("数组维数不同\n");
                        return false;
                    }
                    else{
                        for (int j = 0; j < exp.arrayDimensions.size(); j++) {
                            if(exp.arrayDimensions.get(i)!=param.getArrayDimension(i)) {
                                Compiler.res.append("\n数组某一维不匹配\n");
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    public Exp callFunction(ArrayList<Exp> list) {
        StringBuilder str = Compiler.res;
        Exp ret = null;
        if(BType == Symbol.TypeInt ){
            str.append("    ")
                    .append((Compiler.varList.blockNum == 0) ? "@" : ("%b"+Compiler.varList.blockNum))
                    .append("v")
                    .append(Compiler.varList.regNum)
                    .append(" = call i32 @")
                    .append(this.name)
                    .append("(");
            if(list !=null){
                for (int i = 0; i < list.size(); i++) {
                    Exp exp = list.get(i);
                    if(exp.type == Symbol.TypeInt)
                        str.append("i32 ").append(list.get(i));
                    if(exp.type== Symbol.TypePointer)
                        str.append(exp.getArrayAllocaInfo(exp.arrayDimensions.size())).append("* ").append(exp);
                    if(i<list.size()-1)
                        str.append(", ");
                }
            }
            str.append(")\n");

            ret = new Exp(Compiler.varList.blockNum,Compiler.varList.regNum++);
        }else {
            str.append("    ")
                    .append("call void @")
                    .append(this.name)
                    .append("(");
            if(list !=null){
                for (int i = 0; i < list.size(); i++) {
                    Exp exp = list.get(i);
                    if(exp.type == Symbol.TypeInt)
                        str.append("i32 ").append(list.get(i));
                    if(exp.type== Symbol.TypePointer)
                        str.append(exp.getArrayAllocaInfo(exp.arrayDimensions.size())).append("* ").append(exp);
                    if(i<list.size()-1)
                        str.append(", ");
                }
            }
            str.append(")\n");

        }
        return ret;
    }
}
