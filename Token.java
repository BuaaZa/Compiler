import java.awt.*;

public class Token {

    public static final int IDENT=0,NUMBER=1,EOF=-1;
    /**
     * EQ :  ==
     */
    public static final int EQ=2;
    /**
     * ASSIGN :  =
     */
    public static final int ASSIGN=3;
    /**
     * SEMICOLON :  ;
     */
    public static final int SEMICOLON=4;
    /**
     * LPAR :  (
     */
    public static final int LPAR=5;
    /**
     * RPAR :  )
     */
    public static final int RPAR=6;
    /**
     * LBRACE :  {
     */
    public static final int LBRACE=7;
    /**
     * RBRACE :  }
     */
    public static final int RBRACE=8;
    /**
     * ADD :  +
     */
    public static final int ADD=9;
    /**
     * SUB :  -
     */
    public static final int SUB=10;
    /**
     * MULT :  *
     */
    public static final int MULT=11;
    /**
     * DIV :  /
     */
    public static final int DIV=12;
    /**
     * S :  <
     */
    public static final int S=13;
    /**
     * L :  >
     */
    public static final int L=14;
    /**
     * MOD :  %
     */
    public static final int MOD=15;
    /**
     * COMMA :  ,
     */
    public static final int COMMA=16;
    /**
     * NOT :  !
     */
    public static final int NOT=17;
    /**
     * OR :  ||
     */
    public static final int OR=18;
    /**
     * AND :  &&
     */
    public static final int AND=19;
    /**
     * NEQ :  !=
     */
    public static final int NEQ=20;
    /**
     * SE :  <=
     */
    public static final int SE=21;
    /**
     * LE :  >=
     */
    public static final int LE=22;

    public static final int IF=50,ELSE=51,WHILE=52,BREAK=53,CONTINUE=54,RETURN=55,INT=56,MAIN=57,CONST=58;
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
