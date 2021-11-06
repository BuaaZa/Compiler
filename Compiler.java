import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Compiler {
    public static String ret = "";

    public static void main(String[] args) throws IOException {
        File input = new File(args[0]), output = new File(args[1]);
        FileWriter writer = new FileWriter(output);
        Lexer.s = new Scanner(input);

        Compile();

        writer.write(ret);
        writer.flush();
        writer.close();
        Lexer.s.close();
    }

    private static void Compile() {
        SyntaxTree tree = Parser.CompUnit();
        ret = FuncDef(tree.subtree.get(0));
        System.out.println(ret);
    }

    private static String FuncDef(SyntaxTree syntaxTree) {
        return "define dso_local " + FuncType(syntaxTree.subtree.get(0)) + " " + Ident(syntaxTree.subtree.get(1)) +
                "()" + Block(syntaxTree.subtree.get(4));
//        {\n" +
//                "    ret i32 234\n" +
//                "}"
//    }
    }

    private static String FuncType(SyntaxTree syntaxTree) {
        String ret = null;
        if (syntaxTree.subtree.get(0).type == Token.INT) {
            ret = "i32";
        }
        return ret;
    }

    private static String Ident(SyntaxTree syntaxTree) {
        String ret = null;
        if (syntaxTree.subtree.get(0).type == Token.MAIN) {
            ret = "@main";
        }
        return ret;
    }

    private static String Block(SyntaxTree syntaxTree) {
        String ret = "{\n";
        ret += Stmt(syntaxTree.subtree.get(1));
        ret += "}";
        return ret;
    }

    private static String Stmt(SyntaxTree syntaxTree) {
        return "    ret i32 " + Exp(syntaxTree.subtree.get(1)) + "\n";
    }

    private static int Exp(SyntaxTree syntaxTree) {
        return AddExp(syntaxTree.subtree.get(0));
    }

    private static int AddExp(SyntaxTree syntaxTree) {
        int ret = MulExp(syntaxTree.subtree.get(0));
        for (int i = 2; i < syntaxTree.subtree.size(); i+=2) {
            int tmp = MulExp(syntaxTree.subtree.get(i));
            if(syntaxTree.subtree.get(i-1).type == Token.ADD){
                ret = ret+tmp;
            }else if(syntaxTree.subtree.get(i-1).type == Token.SUB){
                ret = ret-tmp;
            }
        }
        return ret;
    }

    private static int MulExp(SyntaxTree syntaxTree) {
        int ret = UnaryExp(syntaxTree.subtree.get(0));
        for (int i = 2; i < syntaxTree.subtree.size(); i+=2) {
            int tmp = UnaryExp(syntaxTree.subtree.get(i));
            if(syntaxTree.subtree.get(i-1).type == Token.MULT){
                ret = ret*tmp;
            }else if(syntaxTree.subtree.get(i-1).type == Token.DIV){
                ret = ret/tmp;
            }else ret = ret%tmp;
        }
        return ret;
    }

    private static int UnaryExp(SyntaxTree syntaxTree) {
        int ret = PrimaryExp(syntaxTree.subtree.get(syntaxTree.subtree.size()-1));
        for (int i = 0; i < syntaxTree.subtree.size()-1; i++) {
            if(syntaxTree.subtree.get(i).type == Token.SUB)
                ret = -ret;
        }
        return ret;
    }

    private static int PrimaryExp(SyntaxTree syntaxTree) {
        if(syntaxTree.subtree.size() == 1 ){
            return Integer.parseInt(syntaxTree.subtree.get(0).content);
        }else return Exp(syntaxTree.subtree.get(1));
    }
}