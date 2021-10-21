public class Token {
    public static final int IDENT=0,NUMBER=1,EOF=-1;
    public static final int EQ=2,ASSIGN=3,SEMICOLON=4,LPAR=5,RPAR=6,LBRACE=7;
    public static final int RBRACE=8,PLUS=9,MINUS=10,MULT=11,DIV=12,LT=13,GT=14;
    public static final int IF=20,ELSE=21,WHILE=22,BREAK=23,CONTINUE=24,RETURN=25,INT=26,MAIN=27;
    public int type;
    public String content;

    public Token(){

    }

    public Token(int type) {
        this.type = type;
        this.content = "";
    }

    public Token(int type, String content) {
        this.type = type;
        this.content = content;
    }


}
