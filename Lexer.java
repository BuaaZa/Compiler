import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    public static HashMap<String, Integer> trans=new HashMap<>();
    public static Pattern ident = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");
    public static Pattern number = Pattern.compile("(0[xX][0-9a-fA-F]+)|(0[0-7]*)|([1-9][0-9]*)");
    public static Pattern separator = Pattern.compile(">=|<=|!=|&&|\\|\\||==|=|;|\\(|\\)|\\{|}|\\+|-|\\*|/|<|>|%|,|!|\\[|]");
    public static Pattern keyword = Pattern.compile("if|else|while|break|continue|return|int|main|const");
    public static Pattern note_single = Pattern.compile("//.*");
    public static Pattern note_multiple_head = Pattern.compile("/\\*");
    public static Pattern note_multiple_tail = Pattern.compile(".*\\*/");

    public static String input="";
    public static Scanner s;
    public static ArrayList<Token> TokenBuffer;
    public static int BufferSize = 5;

    static {
        TokenBuffer = new ArrayList<>();
        trans.put("==", Token.EQ);
        trans.put("=", Token.ASSIGN);
        trans.put(";", Token.SEMICOLON);
        trans.put("(", Token.LPAR);
        trans.put(")", Token.RPAR);
        trans.put("{", Token.LBRACE);
        trans.put("}", Token.RBRACE);
        trans.put("+", Token.ADD);
        trans.put("-", Token.SUB);
        trans.put("*", Token.MULT);
        trans.put("/", Token.DIV);
        trans.put("<", Token.S);
        trans.put(">", Token.L);
        trans.put("if", Token.IF);
        trans.put("else", Token.ELSE);
        trans.put("while", Token.WHILE);
        trans.put("break", Token.BREAK);
        trans.put("continue", Token.CONTINUE);
        trans.put("return", Token.RETURN);
        trans.put("int", Token.INT);
        trans.put("main", Token.MAIN);
        trans.put("%",Token.MOD);
        trans.put("const",Token.CONST);
        trans.put(",",Token.COMMA);
        trans.put("!",Token.NOT);
        trans.put("&&",Token.AND);
        trans.put("||",Token.OR);
        trans.put("!=",Token.NEQ);
        trans.put("<=",Token.SE);
        trans.put(">=",Token.LE);
        trans.put("[",Token.LBRACKET);
        trans.put("]",Token.RBRACKET);
    }

    public static Token getToken() {
        Token ret = TokenBuffer.get(0);
        TokenBuffer.remove(0);
        if(!TokenBuffer.isEmpty() && TokenBuffer.get(TokenBuffer.size()-1).type != Token.EOF)
            TokenBuffer.add(BufferGetToken());
        return ret;
    }

    public static void InitBuffer(){
        Token token;
        for (int i = 0; i < BufferSize; i++) {
            token = BufferGetToken();
            TokenBuffer.add(token);
            if(token.type==Token.EOF) break;
        }
    }

    public static Token BufferGetToken(){
        String token;
        Token ret = new Token();
        Matcher ident_matcher,number_matcher,separator_matcher,
                keyword_matcher,note_single_matcher,note_multiple_head_matcher,note_multiple_tail_matcher;
        if(input.equals("")){
            if(s.hasNext()){
                input = s.next();
            }else return new Token(Token.EOF,"");
        }

        //System.out.println(input);

        ident_matcher = ident.matcher(input);
        number_matcher = number.matcher(input);
        separator_matcher = separator.matcher(input);
        keyword_matcher = keyword.matcher("");
        note_single_matcher = note_single.matcher(input);
        note_multiple_head_matcher = note_multiple_head.matcher(input);
        note_multiple_tail_matcher = note_multiple_tail.matcher("");

        //System.out.println(number_matcher.lookingAt());

        if (ident_matcher.lookingAt()) {
            token = ident_matcher.group();
            if (keyword_matcher.reset(token).matches()){
                input = ident_matcher.replaceFirst("");
                ret = new Token(trans.get(token));
            }else{
                input = ident_matcher.replaceFirst("");
                ret = new Token(Token.IDENT,token);
            }
        }else if(number_matcher.lookingAt()){
            input = number_matcher.replaceFirst("");
            String number = number_matcher.group();
            if(number.matches("0[xX].*")){
                number = String.valueOf(Integer.parseInt(number.substring(2),16));
            }else if(number.matches("0.*")){
                //System.out.println(number);
                number = String.valueOf(Integer.parseInt(number,8));
                //System.out.println(number);
            }
            ret = new Token(Token.NUMBER,number);
        }else if(note_single_matcher.lookingAt()){
            s.nextLine();
            input = "";
            ret = BufferGetToken();
        }else if(note_multiple_head_matcher.lookingAt()){
            input = note_multiple_head_matcher.replaceFirst("");
            if(note_multiple_tail_matcher.reset(input).lookingAt()){
                input=note_multiple_tail_matcher.replaceFirst("");
                return BufferGetToken();
            }
            while(s.hasNext()){
                input = s.next();
                if(note_multiple_tail_matcher.reset(input).lookingAt()){
                    input=note_multiple_tail_matcher.replaceFirst("");
                    return BufferGetToken();
                }
            }
            System.exit(1);
        }else if(separator_matcher.lookingAt()){
            input = separator_matcher.replaceFirst("");
            ret = new Token(trans.get(separator_matcher.group()));
        }else{
            //System.out.println("error token :"+ Parser.token.type);
            System.exit(1);
        }
        return ret;
    }

    public static Token tokenPreview(int index){
        return TokenBuffer.get(index-1);
    }
}
