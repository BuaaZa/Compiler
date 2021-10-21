import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Parser {
    public static Token token;
    public static String ret;
    public static void main(String[] args) throws IOException {
        File input = new File(args[0]),output = new File(args[1]);
        FileWriter writer = new FileWriter(output);
        String ret = "";
        Lexer.s = new Scanner(input);
        getToken();
        if(CompUnit()){
            writer.write(ret);
            writer.flush();
            writer.close();
        }else System.exit(1);
        Lexer.s.close();
    }

    private static boolean CompUnit() {
        return FuncDef() && token.type == Token.EOF;
    }

    private static boolean FuncDef() {
        if(FuncType() && Ident() && token.type==Token.LPAR){
            ret +="(";
            getToken();
            if(token.type==Token.RPAR){
                ret +=")";
                getToken();
                return Block();
            }
        }
        return false;
    }

    private static boolean FuncType() {
        if(token.type == Token.INT){
            ret +="define dso_local i32 ";
            getToken();
            return true;
        }
        return false;
    }

    private static boolean Ident() {
        if(token.type == Token.MAIN){
            ret +="@main";
            getToken();
            return true;
        }
        return false;
    }

    private static boolean Block() {
        if(token.type == Token.LBRACE){
            ret +="{\n";
            getToken();
            if(Stmt() && token.type == Token.RBRACE){
                ret +="}";
                getToken();
                return true;
            }
        }
        return false;
    }

    private static boolean Stmt() {
        if(token.type == Token.RETURN){
            ret +="\tret i32 ";
            getToken();
            if(token.type == Token.NUMBER){
                ret += Integer.valueOf(token.content) + "\n";
                getToken();
                if(token.type == Token.SEMICOLON){
                    getToken();
                    return true;
                }
            }
        }
        return false;
    }

    private static void getToken(){
        token = Lexer.getToken();
    }

}
