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
        return (list == null && argList.isEmpty()) || (list != null && list.size() == argList.size());
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
                for (Exp s : list) {
                    //TODO:函数
                    str.append("i32 ").append(s);
                }
            }
            str.append(")\n");

            ret = new Exp(Compiler.varList.blockNum,Compiler.varList.regNum++);
        }else {
            str.append("    ")
                    .append("call void @")
                    .append(this.name)
                    .append("(");
            for (Exp s:list) {
                //TODO:函数
                str.append("i32 ").append(s);
            }
            str.append(")\n");

        }
        return ret;
    }
}
