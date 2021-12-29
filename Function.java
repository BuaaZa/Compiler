import java.util.ArrayList;

public class Function extends Symbol{


    ArrayList<Symbol> argList = new ArrayList<>();

    public Function(String name, int BType) {
        super(name, BType);
    }

    public void addArg(Symbol symbol){
        argList.add(symbol);
    }

    public Symbol getArg(int index){
        return argList.get(index);
    }

    public boolean checkArgList(ArrayList<Exp> list){
        if(list == null && argList.isEmpty())
            return true;
        if(list != null  && list.size() == argList.size()){
            for (int i = 0; i < list.size(); i++) {
                if(list.get(i).type != argList.get(i).BType)
                    return false;
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
                    if(list.get(i).type == Symbol.TypeInt)
                        str.append("i32 ").append(list.get(i));
                    if(list.get(i).type== Symbol.TypePointer)
                        str.append("i32* ").append(list.get(i));
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
                    if(list.get(i).type == Symbol.TypeInt)
                        str.append("i32 ").append(list.get(i));
                    if(list.get(i).type== Symbol.TypePointer)
                        str.append("i32* ").append(list.get(i));
                    if(i<list.size()-1)
                        str.append(", ");

                }
            }
            str.append(")\n");

        }
        return ret;
    }
}
