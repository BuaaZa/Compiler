import java.awt.*;

public class Token {

    public static final int IDENT=0,NUMBER=1,EOF=-1;
    /**
     * EQ: ==
     */
    public static final int EQ=2;
    /**
     * ASSIGN: =
     */
    public static final int ASSIGN=3;
    /**
     * SEMICOLON: ;
     */
    public static final int SEMICOLON=4;
    /**
     * LPAR: (
     */
    public static final int LPAR=5;
    /**
     * RPAR: )
     */
    public static final int RPAR=6;
    /**
     * LBRACE: {
     */
    public static final int LBRACE=7;
    /**
     * RBRACE: }
     */
    public static final int RBRACE=8;
    /**
     * ADD: +
     */
    public static final int ADD=9;
    /**
     * SUB: -
     */
    public static final int SUB=10;
    /**
     * MULT: *
     */
    public static final int MULT=11;
    /**
     * DIV: /
     */
    public static final int DIV=12;
    /**
     * LT: <
     */
    public static final int LT=13;
    /**
     * GT: >
     */
    public static final int GT=14;
    /**
     * MOD: %
     */
    public static final int MOD=15;
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
