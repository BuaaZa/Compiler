import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    public static void main(String[] args) throws FileNotFoundException {
        Pattern p1 =Pattern.compile(";|\\(|\\)|\\{|\\}|\\+|\\*|/|<|>|(==)|=");
        Pattern p2 =Pattern.compile("[0-9]+");
        Pattern p3 =Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");
        File file = new File(args[0]);
        Scanner s = new Scanner(file);
        while(s.hasNext()){
            String str = s.next();
            Matcher m1,m2,m3;
            while(!str.equals("")){
                m1 = p1.matcher(str);
                m2 = p2.matcher(str);
                m3 = p3.matcher(str);
                if(m1.lookingAt()){
                    String token = m1.group();
                    if(token.equals(";")) System.out.println("Semicolon");
                    if(token.equals("(")) System.out.println("LPar");
                    if(token.equals(")")) System.out.println("RPar");
                    if(token.equals("{")) System.out.println("LBrace");
                    if(token.equals("}")) System.out.println("RBrace");
                    if(token.equals("+")) System.out.println("Plus");
                    if(token.equals("*")) System.out.println("Mult");
                    if(token.equals("/")) System.out.println("Div");
                    if(token.equals("<")) System.out.println("Lt");
                    if(token.equals(">")) System.out.println("Gt");
                    if(token.equals("==")) System.out.println("Eq");
                    if(token.equals("=")) System.out.println("Assign");
                    str=m1.replaceFirst("");
                }else if(m2.lookingAt()){
                    String token = m2.group();
                    System.out.println("Number(" + token + ")");
                    str=m2.replaceFirst("");
                }else if(m3.lookingAt()){
                    String token = m3.group();
                    if(token.matches("(if)|(else)|(while)|(break)|(continue)|(return)"))
                        System.out.println(token.substring(0, 1).toUpperCase() + token.substring(1));
                    else
                        System.out.println("Ident(" + token + ")");
                    str=m3.replaceFirst("");
                }else{
                    System.out.println("Err");
                    return;
                }
            }
        }
        s.close();
    }
}
