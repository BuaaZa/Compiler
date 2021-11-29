import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Compiler {
    public static StringBuilder res = new StringBuilder();
    public static FuncList funcList = new FuncList();
    public static VarList varList = new VarList(0,null);
    public static int blockNum = 0;


    public static void main(String[] args) throws IOException {
        File input = new File(args[0]), output = new File(args[1]);
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
        res.append("declare i32 @getint()\ndeclare void @putint(i32)\ndeclare i32 @getch()\ndeclare void @putch(i32)\n");

        funcList.putFunction("getint",Symbol.TypeInt);
        funcList.putFunction("getch",Symbol.TypeInt);

        funcList.putFunction("putint",Symbol.TypeVoid);
        funcList.getFunction("putint").addArg(new Symbol(Symbol.TypeInt));

        funcList.putFunction("putch",Symbol.TypeVoid);
        funcList.getFunction("putch").addArg(new Symbol(Symbol.TypeInt));

    }

    private static void Compile() {
        SyntaxTree tree = Parser.CompUnit();
        FuncDef(tree.getSubtree(0));
        System.out.println(res);
    }

    private static void FuncDef(SyntaxTree t) {
        res.append("define dso_local ");
        FuncType(t.getSubtree(0));
        res.append(" @").append((t.getSubtree(1).type == Token.MAIN) ? "main" : (t.getSubtree(1).content)).append("()");
        Block(t.getSubtree(4));
    }

    private static void FuncType(SyntaxTree t) {
        if (t.getSubtree(0).type == Token.INT) {
            res.append("i32");
        }
    }


    private static void Block(SyntaxTree t) {
        varList = new VarList(++blockNum, varList);
        res.append("{\n");
        for (int i = 1; i < t.subtree.size()-1 ; i++) {
            BlockItem(t.getSubtree(i));
        }
        res.append("}");
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
        int type = (t.getSubtree(1).getSubtree(0).type == Token.INT)? Symbol.TypeInt:Symbol.TypeVoid;
        for (int i = 2; i <t.subtree.size(); i+=2) {
            ConstDef(t.getSubtree(i),type);
        }
    }

    private static void ConstDef(SyntaxTree t, int type) {
        String name = t.getSubtree(0).content;
        if(varList.getVariable(name)!=null)
            System.exit(1);
        Exp constInitVal = ConstInitVal(t.getSubtree(2));
        if(!constInitVal.isConstValue)
            System.exit(1);
        varList.putVariable(name,type,constInitVal.value);
    }

    private static Exp ConstInitVal(SyntaxTree t) {
        return ConstExp(t.getSubtree(0));
    }

    private static Exp ConstExp(SyntaxTree t) {
        return Exp(t.getSubtree(0));
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
        int count = 0;
        if(t.getSubtree(t.subtree.size()-1).name!=null && t.getSubtree(t.subtree.size()-1).name.equals(SyntaxTree.PrimaryExp)){
            ret = PrimaryExp(t.getSubtree(t.subtree.size()-1));
        }else{
            Function func =funcList.getFunction(t.searchSubtree(Token.IDENT).content);
            if(func != null){
                ArrayList<Exp> argList = FuncRParams(t.searchSubtree(SyntaxTree.FuncRParams));
                if(func.checkArgList(argList)){
                    //TODO:函数
                    ret = func.callFunction(argList);
                }else System.exit(1);
            }else System.exit(1);
        }

        for (int i = 0; t.subtree.get(i).type == Token.SUB || t.subtree.get(i).type == Token.ADD ; i++) {
            if(ret == null){
                System.exit(1);
            }
            if(t.subtree.get(i).type == Token.SUB){
                count++;
            }
        }
        if(count%2!=0)
            ret = Exp.ExpCompute(new Exp(0),ret,Exp.Sub);
        return ret;
    }

    private static ArrayList<Exp> FuncRParams(SyntaxTree t) {
        if(t != null){
            ArrayList<Exp> argList = new ArrayList<>();
            for (int i = 0; i < t.subtree.size(); i+=2) {
                argList.add(Exp(t.getSubtree(i)));
            }
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
            if(!v.isConst){
                //todo:
                Exp ret = v.loadVariable();
                if(ret != null){
                    return ret;
                }else System.exit(1);
            }else return new Exp(v.value);
        }else System.exit(1);
        return null;
    }

    private static void VarDecl(SyntaxTree t) {
        int type = (t.getSubtree(0).getSubtree(0).type == Token.INT)? Symbol.TypeInt:Symbol.TypeVoid;
        for (int i = 1; i <t.subtree.size(); i+=2) {
            VarDef(t.getSubtree(i),type);
        }
    }

    private static void VarDef(SyntaxTree t, int type) {
        String name = t.getSubtree(0).content;
        if(varList.getVariable(name)!=null)
            System.exit(1);
        varList.putVariable(name,type);
        if(t.subtree.size()>1){
            Exp initVal = InitVal(t.getSubtree(2));
            varList.getVariable(name).storeVariable(initVal);
        }

    }

    private static Exp InitVal(SyntaxTree t) {
        return Exp(t.getSubtree(0));
    }

    private static void Stmt(SyntaxTree t) {
        if(t.getSubtree(0).type == Token.RETURN){
            Exp ret = Exp(t.getSubtree(1));
            res.append("    ret i32 ")
                    .append(ret)
                    .append("\n");
        }else if(t.getSubtree(0).name.equals(SyntaxTree.LVal)){
            Variable v = searchVariable(t.getSubtree(0).getSubtree(0).content);
            if(v!=null){
                if(!v.isConst){
                    //todo:
                    Exp exp = Exp(t.getSubtree(2));
                    v.storeVariable(exp);
                }else System.exit(1);
            }else System.exit(1);

        }else{
            Exp(t.getSubtree(0));
        }

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


}