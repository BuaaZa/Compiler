import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Compiler {
    public static StringBuilder res = new StringBuilder();
    public static FuncList funcList = new FuncList();
    public static VarList varList = new VarList(0,null);
    public static int blockNum = 0;
    public static WhileRecorder currentWhile = null;
    public static Function currentFunction = null;
    public static boolean allowArrayParam = false;


    public static void main(String[] args) throws IOException {
        File input = new File(args[0]),testInput = new File(args[0]), output = new File(args[1]);
        BufferedReader getTest = new BufferedReader(new FileReader(testInput));

        printTest(getTest);

        FileWriter writer = new FileWriter(output);
        Lexer.s = new Scanner(input);


        CompileInit();
        Compile();

        writer.write(res.toString());
        writer.flush();
        writer.close();
        Lexer.s.close();
    }

    private static void CompileInit() {
        Lexer.InitBuffer();
        res.append("declare i32 @getint()\n" +
                "declare void @putint(i32)\n" +
                "declare i32 @getch()\n" +
                "declare void @putch(i32)\n" +
                "declare void @memset(i32*, i32, i32)\n"+
                "declare i32 @getarray(i32*)\n"+
                "declare void @putarray(i32, i32*)\n"+"\n");

        funcList.putFunction("getint",Symbol.TypeInt);
        funcList.putFunction("getch",Symbol.TypeInt);

        funcList.putFunction("putint",Symbol.TypeVoid);
        funcList.getFunction("putint").addArg(new Variable(Symbol.TypeInt));

        funcList.putFunction("putch",Symbol.TypeVoid);
        funcList.getFunction("putch").addArg(new Variable(Symbol.TypeInt));

        funcList.putFunction("memset",Symbol.TypeVoid);
        funcList.getFunction("memset").addArg(new Variable(Symbol.TypePointer));
        funcList.getFunction("memset").addArg(new Variable(Symbol.TypeInt));
        funcList.getFunction("memset").addArg(new Variable(Symbol.TypeInt));

        funcList.putFunction("getarray",Symbol.TypeInt);
        funcList.getFunction("getarray").addArg(new Variable(Symbol.TypePointer,new ArrayList<Integer>()));

        funcList.putFunction("putarray",Symbol.TypeVoid);
        funcList.getFunction("putarray").addArg(new Variable(Symbol.TypeInt));
        funcList.getFunction("putarray").addArg(new Variable(Symbol.TypePointer,new ArrayList<Integer>()));

    }

    private static void error(){
        System.out.println(res);
        System.exit(1);
    }

    private static void Compile() {
        SyntaxTree tree = Parser.CompUnit();
        CompUnit(tree);
        System.out.println(res);
    }
    private static void CompUnit(SyntaxTree t) {
        int i;
        for (i = 0; i < t.subtree.size() ; i++) {
            if(t.getSubtree(i).name.equals(SyntaxTree.Decl)){
                Decl(t.getSubtree(i));
            }else{
                FuncDef(t.getSubtree(i));
            }
        }
    }

    private static void FuncDef(SyntaxTree t) {
        res.append("define dso_local ");
        int type = FuncType(t.getSubtree(0));

        String name = (t.getSubtree(1).type == Token.MAIN) ? "main" : (t.getSubtree(1).content);
        if(funcList.getFunction(name)!=null)
            error();

        if(name.equals("main")){
            if(type!= Symbol.TypeInt)
                error();
        }
        funcList.putFunction(name,type);

        Function function = funcList.getFunction(name);
        currentFunction = function;

        res.append(" @").append(name).append("(");

        if(t.getSubtree(3).name!=null && t.getSubtree(3).name.equals(SyntaxTree.FuncFParams)){
            if(name.equals("main"))
                error();
            FuncFParams(t.getSubtree(3),function);
        }

        res.append(") {\n");
        Block(t.getSubtree(t.subtree.size()-1),function);
        if(type == Symbol.TypeVoid)
            res.append("    ret void\n");
        else res.append("    ret i32 0\n");
        res.append("}\n\n");

        currentFunction = null;
    }

    private static void FuncFParams(SyntaxTree t,Function function) {
        for (int i = 0; i*2 < t.subtree.size(); i++) {
            FuncFParam(t.getSubtree(2*i),function,i);
            if(2*i<t.subtree.size()-1)
                res.append(", ");
        }
    }

    private static void FuncFParam(SyntaxTree t, Function function,int index) {
        String name = t.getSubtree(1).content;
        if(t.subtree.size()>2){
            ArrayList<Integer> arrayDimensions = new ArrayList<>();
            for (int i = 5; i < t.subtree.size(); i+=3) {
                Exp exp = Exp(t.getSubtree(i));
                if(!exp.isConstValue)
                    error();
                arrayDimensions.add(exp.value);
            }

            Variable param = new Variable(name,Symbol.TypePointer,blockNum+1,index,arrayDimensions,false,true);
            function.addArg(param);
            res.append(param.getArrayAllocaInfo(arrayDimensions.size())).append("* ").append(param);

        }else{
            Variable param =new Variable(name,Symbol.TypeInt,blockNum+1,index,true);
            function.addArg(param);
            res.append("i32 ").append(param);

        }
    }

    private static int FuncType(SyntaxTree t) {
        if (t.getSubtree(0).type == Token.INT) {
            res.append("i32");
            return Symbol.TypeInt;
        }else {
            res.append("void");
            return Symbol.TypeVoid;
        }
    }

    private static void Block(SyntaxTree t,Function function) {
        varList = new VarList(++blockNum, varList);
        int argSize = function.getArgSize();
        varList.setRegNum(varList.regNum+argSize);
        for (int i = 0; i < argSize; i++) {
            Variable arg = function.getArg(i);
            arg.setRegIndex(varList.regNum++);
            arg.allocaVariable();
            arg.storeVariable(new Exp(arg.blockNum,i));
            if(arg.arrayDimensions!=null){
                StringBuilder arrayAllocaInfo = arg.getArrayAllocaInfo(arg.arrayDimensions.size());
                res.append("    ").append("%b").append(varList.blockNum)
                        .append("v").append(varList.regNum)
                        .append(" = load ").append(arrayAllocaInfo).append("* ")
                        .append(", ").append(arrayAllocaInfo).append("* * ").append(arg).append("\n");
                arg.setRegIndex(varList.regNum++);
            }
            varList.putVariable(arg.name,arg);

        }
        for (int i = 1; i < t.subtree.size()-1 ; i++) {
            BlockItem(t.getSubtree(i));
        }
        varList = varList.prevVarList;
    }

    private static void Block(SyntaxTree t) {
        varList = new VarList(++blockNum, varList);
        for (int i = 1; i < t.subtree.size()-1 ; i++) {
            BlockItem(t.getSubtree(i));
        }
        varList = varList.prevVarList;
    }

    private static void BlockItem(SyntaxTree t) {
        if(t.getSubtree(0).name.equals(SyntaxTree.Decl)){
            Decl(t.getSubtree(0));
        }else Stmt(t.getSubtree(0));
    }

    private static void Decl(SyntaxTree t) {
        if(t.getSubtree(0).name.equals(SyntaxTree.ConstDecl)){
            ConstDecl(t.getSubtree(0));
        }else VarDecl(t.getSubtree(0));
    }

    private static void ConstDecl(SyntaxTree t) {
        for (int i = 2; i <t.subtree.size(); i+=2) {
            ConstDef(t.getSubtree(i));
        }
    }

    private static void ConstDef(SyntaxTree t) {
        String name = t.getSubtree(0).content;
        if(varList.getVariable(name)!=null)
            error();
        if(t.getSubtree(1).type==Token.LBRACKET){
            ArrayList<Integer> arrayDimensions = new ArrayList<>();
            for (int i = 1; i < t.subtree.size()&& t.getSubtree(i).type ==Token.LBRACKET; i+=3) {
                arrayDimensions.add(ConstExp(t.getSubtree(i + 1)).value);
            }
            varList.putVariable(name,Symbol.TypePointer,arrayDimensions,true);

            Variable array = varList.getVariable(name);
            StringBuilder initial =new StringBuilder();
            if(array.blockNum == 0){
                GlobalInitArrayVal(t.getSubtree(t.subtree.size()-1),array,arrayDimensions.size(),initial,true);
                res.append(initial).append("\n");
            }else{
                ArrayList<Exp> indexList=new ArrayList<>();
                LocalInitArrayVal(t.getSubtree(t.subtree.size()-1),array,arrayDimensions.size(),true,indexList);
            }

        }else{
            Exp constInitVal = ConstInitVal(t.getSubtree(t.subtree.size()-1));
            varList.putVariable(name,Symbol.TypeInt,constInitVal.value);
        }
    }

    private static void LocalInitArrayVal(SyntaxTree t, Variable array, int dimension,boolean isConst,ArrayList<Exp> indexList) {
        if(t.getSubtree(0).type!=Token.LBRACE || t.getSubtree(t.subtree.size()-1).type!=Token.RBRACE)
            error();
        if(t.subtree.size() == 2) {
            return;
        }else if(dimension>1){
            int length = array.getArrayDimension(array.getArraySize()-dimension);
            for (int i = 0; i < length; i++) {
                if(2*i+1<t.subtree.size()){

                    if(isConst&&!t.getSubtree(2 * i + 1).name.equals(SyntaxTree.ConstInitVal) ||!isConst&&!t.getSubtree(2 * i + 1).name.equals(SyntaxTree.InitVal))
                        error();

                    indexList.add(new Exp(i));
                    LocalInitArrayVal(t.getSubtree(2 * i + 1),array,dimension-1,isConst,indexList);
                    indexList.remove(indexList.size()-1);
                }else break;

            }
        }else{
            int length = array.getArrayDimension(array.getArraySize()-dimension);

            for (int i = 0; i < length; i++) {
                if(2*i+1<t.subtree.size()){

                    if(isConst&&!t.getSubtree(2 * i + 1).name.equals(SyntaxTree.ConstInitVal) ||!isConst&&!t.getSubtree(2 * i + 1).name.equals(SyntaxTree.InitVal))
                        error();

                    Exp exp = (isConst)?ConstExp(t.getSubtree(2 * i + 1).getSubtree(0)):Exp(t.getSubtree(2 * i + 1).getSubtree(0));
                    indexList.add(new Exp(i));

                    Exp arrayElementPtr = array.getArrayElementPtr(indexList,false);
                    res.append("    store i32 ").append(exp).append(", i32* ").append(arrayElementPtr).append("\n");
                    indexList.remove(indexList.size()-1);
                }else break;
            }

        }
    }

    private static void GlobalInitArrayVal(SyntaxTree t, Variable array,int dimension,StringBuilder initial,boolean isConst) {

        if(t.getSubtree(0).type!=Token.LBRACE || t.getSubtree(t.subtree.size()-1).type!=Token.RBRACE)
            error();
        if(t.subtree.size() == 2){
            initial.append("zeroinitializer");
        }else if(dimension>1){

            int length = array.getArrayDimension(array.getArraySize()-dimension);

            initial.append("[");
            for (int i = 0; i < length; i++) {
                if(2*i+1<t.subtree.size()){

                    if(isConst&&!t.getSubtree(2 * i + 1).name.equals(SyntaxTree.ConstInitVal)||!isConst&&!t.getSubtree(2 * i + 1).name.equals(SyntaxTree.InitVal)){
                        res.append(initial);
                        error();
                    }

                    initial.append(array.getArrayAllocaInfo(dimension-1)).append(" ");
                    GlobalInitArrayVal(t.getSubtree(2 * i + 1),array,dimension-1,initial,isConst);
                }else{
                    initial.append(array.getArrayAllocaInfo(dimension-1)).append(" zeroinitializer");
                }
                if(i<length-1)
                    initial.append(", ");
            }
            initial.append("]");

        }else{

                int length = array.getArrayDimension(array.getArraySize()-dimension);

                initial.append("[");
                for (int i = 0; i < length; i++) {
                    if(2*i+1<t.subtree.size()){
                        if(isConst&&!t.getSubtree(2 * i + 1).name.equals(SyntaxTree.ConstInitVal)||!isConst&&!t.getSubtree(2 * i + 1).name.equals(SyntaxTree.InitVal)){
                            res.append(initial);
                            error();
                        }
                        Exp constExp = (isConst)?ConstExp(t.getSubtree(2 * i + 1).getSubtree(0)):Exp(t.getSubtree(2 * i + 1).getSubtree(0));
                        if(!constExp.isConstValue)
                            error();

                        initial.append(array.getArrayAllocaInfo(dimension-1)).append(" ").append(constExp.value);
                    }else{
                        initial.append(array.getArrayAllocaInfo(dimension-1)).append(" ").append(0);
                    }
                    if(i<length-1)
                        initial.append(", ");
                }
                initial.append("]");
        }


    }

    private static Exp ConstInitVal(SyntaxTree t) {
        return ConstExp(t.getSubtree(0));
    }

    private static Exp ConstExp(SyntaxTree t) {
        Exp ret =Exp(t.getSubtree(0));
        if(!ret.isConstValue)
            error();
        return ret;
    }

    private static void VarDecl(SyntaxTree t) {
        for (int i = 1; i <t.subtree.size(); i+=2) {
            VarDef(t.getSubtree(i));
        }
    }

    private static void VarDef(SyntaxTree t) {
        String name = t.getSubtree(0).content;
        if(varList.getVariable(name)!=null)
            error();
        if(t.subtree.size()>1&&t.getSubtree(1).type==Token.LBRACKET){
            ArrayList<Integer> arrayDimensions = new ArrayList<>();
            for (int i = 1; i < t.subtree.size()&& t.getSubtree(i).type ==Token.LBRACKET; i+=3) {
                arrayDimensions.add(ConstExp(t.getSubtree(i + 1)).value);
            }
            varList.putVariable(name,Symbol.TypePointer,arrayDimensions,false);

            Variable array = varList.getVariable(name);
            StringBuilder initial =new StringBuilder();
            if(array.blockNum == 0){
                if(t.getSubtree(t.subtree.size()-1).type ==Token.RBRACKET)
                    initial.append("zeroinitializer");
                else{
                    GlobalInitArrayVal(t.getSubtree(t.subtree.size()-1),array,arrayDimensions.size(),initial,false);
                }
                res.append(initial).append("\n");
            }else{
                if(t.getSubtree(t.subtree.size()-1).type !=Token.RBRACKET){
                    ArrayList<Exp> indexList=new ArrayList<>();
                    LocalInitArrayVal(t.getSubtree(t.subtree.size()-1),array,arrayDimensions.size(),false,indexList);
                }

            }

        }else{
            varList.putVariable(name,Symbol.TypeInt);
            if(varList.blockNum == 0){
                if(t.subtree.size()>1){
                    Exp initVal = InitVal(t.getSubtree(2));
                    if(!initVal.isConstValue)
                        error();
                    res.append(initVal.value).append("\n");
                }else res.append("0\n");
            }else if(t.subtree.size()>1){
                Exp initVal = InitVal(t.getSubtree(2));
                varList.getVariable(name).storeVariable(initVal);
            }
        }

    }

    private static Exp InitVal(SyntaxTree t) {
        return Exp(t.getSubtree(0));
    }

    private static void Stmt(SyntaxTree t) {
        if(t.getSubtree(0).type == Token.RETURN){
            if(currentFunction.BType == Symbol.TypeInt){
                if(t.subtree.size()==2)
                    error();
                Exp ret = Exp(t.getSubtree(1));
                res.append("    ret i32 ")
                        .append(ret)
                        .append("\n");
            }else{
                if(t.subtree.size()==3)
                    error();
                res.append("    ret void ")
                        .append("\n");
            }

        }
        else if(t.getSubtree(0).type == Token.IF){
            Exp cond = Cond(t.getSubtree(2));
            int elseIndex;
            res.append("    ").append("br i1 ").append(cond).append(", label ")
                    .append("%b").append(varList.blockNum).append("v").append(varList.regNum++)
                    .append(", label ")
                    .append("%b").append(varList.blockNum).append("v").append(varList.regNum++)
                    .append("\n\n")

                    .append("b").append(varList.blockNum).append("v").append(cond.regIndex+1)
                    .append(":\n");

            if((elseIndex = t.searchSubtree(Token.ELSE)) != -1){
                varList.regNum++;
                Stmt(t.getSubtree(4));
                res.append("    ")
                        .append("br label %b").append(varList.blockNum).append("v").append(cond.regIndex+3).append("\n\n")
                        .append("b").append(varList.blockNum).append("v").append(cond.regIndex+2).append(":\n");
                Stmt(t.getSubtree(elseIndex+1));
                res.append("    ")
                        .append("br label %b").append(varList.blockNum).append("v").append(cond.regIndex+3).append("\n\n")
                        .append("b").append(varList.blockNum).append("v").append(cond.regIndex+3).append(":\n");
            }
            else {
                Stmt(t.getSubtree(4));
                res.append("    ")
                        .append("br label %b").append(varList.blockNum).append("v").append(cond.regIndex+2).append("\n\n")
                        .append("b").append(varList.blockNum).append("v").append(cond.regIndex+2).append(":\n");
            }
        }
        else if(t.getSubtree(0).type == Token.WHILE){
            currentWhile = new WhileRecorder(
                    new Exp(varList.blockNum,varList.regNum),
                    new Exp(varList.blockNum,varList.regNum+2),
                    currentWhile);
            varList.regNum += 3;

            res.append("    ")
                    .append("br label ").append(currentWhile.judgeBlock).append("\n\n")
                    .append(currentWhile.judgeBlock.toString().substring(1)).append(":\n");

            Exp cond = Cond(t.getSubtree(2));

            res.append("    ").append("br i1 ").append(cond).append(", label ")
                    .append("%b").append(varList.blockNum).append("v").append(currentWhile.judgeBlock.regIndex+1)
                    .append(", label ").append(currentWhile.exitBlock).append("\n\n")
                    .append("b").append(varList.blockNum).append("v").append(currentWhile.judgeBlock.regIndex+1).append(":\n");

            Stmt(t.getSubtree(4));

            res.append("    ")
                    .append("br label ").append(currentWhile.judgeBlock).append("\n\n")
                    .append(currentWhile.exitBlock.toString().substring(1)).append(":\n");

            currentWhile = currentWhile.prevWhileRecorder;
        }
        else if(t.getSubtree(0).type == Token.BREAK){
            res.append("    ").append("br label ").append(currentWhile.exitBlock).append("\n");
        }
        else if(t.getSubtree(0).type == Token.CONTINUE){
            res.append("    ").append("br label ").append(currentWhile.judgeBlock).append("\n");
        }
        else if(t.getSubtree(0).type == Token.SEMICOLON){
            return;
        }
        else if(t.getSubtree(0).name.equals(SyntaxTree.Block)){
            Block(t.getSubtree(0));
        }
        else if(t.getSubtree(0).name.equals(SyntaxTree.LVal)){
            Variable v = searchVariable(t.getSubtree(0).getSubtree(0).content);
            if(v!=null){
                if(!v.isConst){
                    Exp exp = Exp(t.getSubtree(t.subtree.size()-2));
                    if(v.arrayDimensions!=null){

                        ArrayList<Exp> indexList=new ArrayList<>();
                        for (int i = 2; i < t.getSubtree(0).subtree.size(); i+=3) {
                            indexList.add(Exp(t.getSubtree(0).getSubtree(i)));
                        }
                        if(indexList.size() == v.getArraySize()){
                            Exp arrayElementPtr = v.getArrayElementPtr(indexList,false);
                            res.append("    store i32 ").append(exp).append(", i32* ").append(arrayElementPtr).append("\n");
                        }else error();

                    }else if(t.getSubtree(0).subtree.size()==1){
                        v.storeVariable(exp);
                    }else error();
                }else error();
            }else error();

        }
        else if(t.getSubtree(0).name.equals(SyntaxTree.Exp)){
            Exp(t.getSubtree(0));
        }
    }

    private static Exp Cond(SyntaxTree t) {
        return LOrExp(t.getSubtree(0));
    }

    private static Exp LOrExp(SyntaxTree t) {
        //todo :短路求值
        Exp ret = LAndExp(t.getSubtree(0));
        for (int i = 2; i < t.subtree.size(); i+=2) {
            ret = Exp.ExpBoolCompute(ret,
                    LAndExp(t.getSubtree(i)),
                    Exp.Or);
        }
        return ret;
    }

    private static Exp LAndExp(SyntaxTree t) {
        //todo :短路求值
        Exp ret = EqExp(t.getSubtree(0));
        for (int i = 2; i < t.subtree.size(); i+=2) {
            ret = Exp.ExpBoolCompute(ret,
                    EqExp(t.getSubtree(i)),
                    Exp.And);
        }
        return ret;
    }

    private static Exp EqExp(SyntaxTree t) {
        Exp ret = RelExp(t.getSubtree(0));
        for (int i = 2; i < t.subtree.size(); i+=2) {
            switch (t.getSubtree(i-1).type){
                case Token.EQ -> ret = Exp.ExpCompute(ret, RelExp(t.getSubtree(i)),Exp.Eq);
                case Token.NEQ -> ret = Exp.ExpCompute(ret, RelExp(t.getSubtree(i)),Exp.Neq);
            }
        }
        ret = Exp.ExpTransToBool(ret);
        return ret;
    }

    private static Exp RelExp(SyntaxTree t) {
        Exp ret = AddExp(t.getSubtree(0));
        for (int i = 2; i < t.subtree.size(); i+=2) {
            switch (t.getSubtree(i-1).type){
                case Token.S -> ret = Exp.ExpCompute(ret,AddExp(t.getSubtree(i)),Exp.S);
                case Token.L -> ret = Exp.ExpCompute(ret, AddExp(t.getSubtree(i)),Exp.L);
                case Token.SE -> ret = Exp.ExpCompute(ret, AddExp(t.getSubtree(i)),Exp.Se);
                case Token.LE -> ret = Exp.ExpCompute(ret, AddExp(t.getSubtree(i)),Exp.Le);
            }
        }
        return ret;
    }

    private static Exp Exp(SyntaxTree t) {
        return AddExp(t.getSubtree(0));
    }

    private static Exp AddExp(SyntaxTree t) {
        Exp ret = MulExp(t.getSubtree(0));
        for (int i = 2; i < t.subtree.size(); i+=2) {
            ret = Exp.ExpCompute(ret,
                    MulExp(t.getSubtree(i)),
                    (t.subtree.get(i-1).type == Token.ADD)?Exp.Add:Exp.Sub);
        }
        return ret;
    }

    private static Exp MulExp(SyntaxTree t) {
        Exp ret = UnaryExp(t.getSubtree(0));
        for (int i = 2; i < t.subtree.size(); i+=2) {
            ret = Exp.ExpCompute(ret
                    , UnaryExp(t.getSubtree(i))
                    ,(t.subtree.get(i-1).type == Token.MULT)?Exp.Mult:((t.subtree.get(i-1).type == Token.DIV)?Exp.Div:Exp.Mod));
        }
        return ret;
    }

    private static Exp UnaryExp(SyntaxTree t) {
        Exp ret = null;
        if(t.getSubtree(t.subtree.size()-1).name!=null && t.getSubtree(t.subtree.size()-1).name.equals(SyntaxTree.PrimaryExp)){
            ret = PrimaryExp(t.getSubtree(t.subtree.size()-1));
        }else{
            Function func =funcList.getFunction(t.getSubtree(t.searchSubtree(Token.IDENT)).content);
            if(func != null){

                ArrayList<Exp> argList = FuncRParams(t.getSubtree(t.searchSubtree(SyntaxTree.FuncRParams)));

                if(func.checkArgList(argList)){
                    //TODO:函数
                    ret = func.callFunction(argList);
                }else error();
            }else error();
        }

        for (int i = 0; t.getSubtree(i).type == Token.SUB || t.getSubtree(i).type == Token.ADD ||t.getSubtree(i).type == Token.NOT ; i++) {
            if(t.getSubtree(i).type == Token.SUB){
                ret = Exp.ExpCompute(new Exp(0),ret,Exp.Sub);
            }else if(t.getSubtree(i).type == Token.NOT){
                ret = Exp.ExpCompute(ret,new Exp(0),Exp.Eq);
            }
        }
        return ret;
    }

    private static ArrayList<Exp> FuncRParams(SyntaxTree t) {
        if(t != null){
            ArrayList<Exp> argList = new ArrayList<>();
            allowArrayParam = true;
            for (int i = 0; i < t.subtree.size(); i+=2) {
                argList.add(Exp(t.getSubtree(i)));
            }
            allowArrayParam = false;
            return argList;
        }return null;
    }

    private static Exp PrimaryExp(SyntaxTree t) {
        if(t.getSubtree(0).type == Token.LPAR){
            return Exp(t.getSubtree(1));
        }else if(t.getSubtree(0).type == Token.NUMBER){
            return new Exp(Integer.parseInt(t.getSubtree(0).content));
        }else return LVal(t.getSubtree(0));
    }

    private static Exp LVal(SyntaxTree t) {
        Variable v = searchVariable(t.getSubtree(0).content);
        if(v!=null){
            if(v.arrayDimensions!=null){
                ArrayList<Exp> indexList=new ArrayList<>();
                for (int i = 2; i < t.subtree.size(); i+=3) {
                    indexList.add(Exp(t.getSubtree(i)));
                }
                //todo:函数
                if(allowArrayParam){
                    if(indexList.size() < v.getArraySize()){
                        return v.getArrayElementPtr(indexList,true);
                    }else if(indexList.size()== v.getArraySize()){
                        return v.loadArrayElementVariable(indexList);
                    }else error();
                }else{
                    if(indexList.size() == v.getArraySize()){
                        return v.loadArrayElementVariable(indexList);
                    }else error();
                }

            }else if(t.subtree.size()==1){
                if(!v.isConst){
                    return v.loadVariable();
                }else return new Exp(v.value);
            }else error();
        }else error();
        return null;
    }

    public static Variable searchVariable(String name){
        VarList v = varList;
        Variable ret = v.getVariable(name);
        while(ret ==null &&v.prevVarList!=null){
            v=v.prevVarList;
            ret = v.getVariable(name);
        }
        return ret;
    }

    private static void printTest(BufferedReader bufferedReader) throws IOException {
        System.out.println("Test:\n");
        String str;
        while ((str = bufferedReader.readLine())!=null){
            System.out.println(str);
        }
    }

}