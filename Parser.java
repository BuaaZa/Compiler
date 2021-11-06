public class Parser {
    public static Token token = Lexer.getToken();

    public static SyntaxTree CompUnit() {
        SyntaxTree tree = new SyntaxTree("CompUnit");
        tree.addSubtree(FuncDef());
        if(token.type != Token.EOF)
            System.exit(1);
        return tree;
    }

    private static SyntaxTree FuncDef() {
        SyntaxTree tree = new SyntaxTree("FuncDef");
        tree.addSubtree(FuncType());
        tree.addSubtree(Ident());
        tree.addSubtree(Token.LPAR);
        tree.addSubtree(Token.RPAR);
        tree.addSubtree(Block());
        return tree;
    }

    private static SyntaxTree FuncType() {
        SyntaxTree tree = new SyntaxTree("FuncType");
        tree.addSubtree(Token.INT);
        return tree;
    }

    private static SyntaxTree Ident() {
        SyntaxTree tree = new SyntaxTree("Ident");
        tree.addSubtree(Token.MAIN);
        return tree;
    }

    private static SyntaxTree Block() {
        SyntaxTree tree = new SyntaxTree("Block");
        tree.addSubtree(Token.LBRACE);
        tree.addSubtree(Stmt());
        tree.addSubtree(Token.RBRACE);
        return tree;
    }

    private static SyntaxTree Stmt() {
        SyntaxTree tree = new SyntaxTree("Stmt");
        tree.addSubtree(Token.RETURN);
        tree.addSubtree(Exp());
        tree.addSubtree(Token.SEMICOLON);
        return tree;
    }

    private static SyntaxTree Exp() {
        SyntaxTree tree = new SyntaxTree("Exp");
        tree.addSubtree(AddExp());
        return tree;
    }

    private static SyntaxTree AddExp() {
        SyntaxTree tree = new SyntaxTree("AddExp");
        tree.addSubtree(MulExp());
        while(token.type == Token.ADD || token.type == Token.SUB){
            tree.addSubtree(token.type);
            tree.addSubtree(MulExp());
        }
        return tree;
    }

    private static SyntaxTree MulExp() {
        SyntaxTree tree = new SyntaxTree("MulExp");
        tree.addSubtree(UnaryExp());
        while(token.type == Token.MULT || token.type == Token.DIV || token.type ==Token.MOD){
            tree.addSubtree(token.type);
            tree.addSubtree(UnaryExp());
        }
        return tree;
    }

    private static SyntaxTree UnaryExp() {
        SyntaxTree tree = new SyntaxTree("UnaryExp");
        while(token.type == Token.ADD || token.type == Token.SUB )
            tree.addSubtree(token.type);
        tree.addSubtree(PrimaryExp());
        return tree;
    }

    private static SyntaxTree PrimaryExp() {
        SyntaxTree tree = new SyntaxTree("PrimaryExp");
        if(token.type == Token.LPAR ){
            tree.addSubtree(Token.LPAR);
            tree.addSubtree(Exp());
            tree.addSubtree(Token.RPAR);
        }else tree.addSubtree(Token.NUMBER);
        return tree;
    }


}
