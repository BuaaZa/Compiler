public class Parser {
    public static Token token = Lexer.getToken();

    public static SyntaxTree CompUnit() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.CompUnit);
        while(token.type!=Token.EOF){
            if(!((token.type == Token.INT || token.type == Token.VOID )&& Lexer.tokenPreview(2).type == Token.LPAR)){
                tree.addSubtree(Decl());
            }else tree.addSubtree(FuncDef());
        }
        return tree;
    }

    private static SyntaxTree FuncDef() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.FuncDef);
        tree.addSubtree(FuncType());
        if(token.type == Token.MAIN){
            tree.addSubtree(Token.MAIN);
        }else tree.addSubtree(Token.IDENT);
        tree.addSubtree(Token.LPAR);
        if(token.type !=Token.RPAR){
            tree.addSubtree(FuncFParams());
        }
        tree.addSubtree(Token.RPAR);
        tree.addSubtree(Block());
        return tree;
    }

    private static SyntaxTree FuncType() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.FuncType);
        if(token.type == Token.INT){
            tree.addSubtree(Token.INT);
        }else tree.addSubtree(Token.VOID);

        return tree;
    }

    private static SyntaxTree FuncFParams() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.FuncFParams);
        tree.addSubtree(FuncFParam());
        while(token.type == Token.COMMA ){
            tree.addSubtree(Token.COMMA);
            tree.addSubtree(FuncFParam());
        }
        return tree;
    }

    private static SyntaxTree FuncFParam() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.FuncFParam);
        tree.addSubtree(BType());
        tree.addSubtree(Token.IDENT);
        if(token.type == Token.LBRACKET){
            tree.addSubtree(Token.LBRACKET);
            tree.addSubtree(Token.RBRACKET);
            while(token.type == Token.LBRACKET){
                tree.addSubtree(Token.LBRACKET);
                tree.addSubtree(Exp());
                tree.addSubtree(Token.RBRACKET);
            }
        }
        return tree;
    }

    private static SyntaxTree Block() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.Block);
        tree.addSubtree(Token.LBRACE);
        while (token.type != Token.RBRACE){
            tree.addSubtree(BlockItem());
        }
        tree.addSubtree(Token.RBRACE);
        return tree;
    }

    private static SyntaxTree BlockItem() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.BlockItem);
        if(token.type == Token.CONST || token.type == Token.INT){
            tree.addSubtree(Decl());
        }else tree.addSubtree(Stmt());
        return tree;
    }

    private static SyntaxTree Decl() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.Decl);
        if(token.type == Token.CONST){
            tree.addSubtree(ConstDecl());
        }else tree.addSubtree(VarDecl());

        return tree;
    }

    private static SyntaxTree ConstDecl() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.ConstDecl);
        tree.addSubtree(Token.CONST);
        tree.addSubtree(BType());
        tree.addSubtree(ConstDef());
        while(token.type == Token.COMMA){
            tree.addSubtree(Token.COMMA);
            tree.addSubtree(ConstDef());
        }
        tree.addSubtree(Token.SEMICOLON);
        return tree;
    }

    private static SyntaxTree ConstDef() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.ConstDef);
        tree.addSubtree(Token.IDENT);
        while(token.type == Token.LBRACKET){
            tree.addSubtree(Token.LBRACKET);
            tree.addSubtree(ConstExp());
            tree.addSubtree(Token.RBRACKET);
        }
        tree.addSubtree(Token.ASSIGN);
        tree.addSubtree(ConstInitVal());
        return tree;
    }

    private static SyntaxTree ConstInitVal() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.ConstInitVal);
        if(token.type == Token.LBRACE){
            tree.addSubtree(Token.LBRACE);
            if(token.type != Token.RBRACE){
                tree.addSubtree(ConstInitVal());
                while(token.type == Token.COMMA){
                    tree.addSubtree(Token.COMMA);
                    tree.addSubtree(ConstInitVal());
                }
            }
            tree.addSubtree(Token.RBRACE);
        }else tree.addSubtree(ConstExp());
        return tree;
    }

    private static SyntaxTree ConstExp() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.ConstExp);
        tree.addSubtree(Exp());
        return tree;
    }

    private static SyntaxTree BType() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.BType);
        tree.addSubtree(Token.INT);
        return tree;
    }

    private static SyntaxTree VarDecl() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.VarDecl);
        tree.addSubtree(BType());
        tree.addSubtree(VarDef());
        while(token.type == Token.COMMA){
            tree.addSubtree(Token.COMMA);
            tree.addSubtree(VarDef());
        }
        tree.addSubtree(Token.SEMICOLON);
        return tree;
    }

    private static SyntaxTree VarDef() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.VarDef);
        tree.addSubtree(Token.IDENT);
        while(token.type == Token.LBRACKET){
            tree.addSubtree(Token.LBRACKET);
            tree.addSubtree(ConstExp());
            tree.addSubtree(Token.RBRACKET);
        }
        if(token.type == Token.ASSIGN){
            tree.addSubtree(Token.ASSIGN);
            tree.addSubtree(InitVal());
        }
        return tree;
    }

    private static SyntaxTree InitVal() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.InitVal);
        if(token.type == Token.LBRACE){
            tree.addSubtree(Token.LBRACE);
            if(token.type != Token.RBRACE){
                tree.addSubtree(InitVal());
                while(token.type == Token.COMMA){
                    tree.addSubtree(Token.COMMA);
                    tree.addSubtree(InitVal());
                }
            }
            tree.addSubtree(Token.RBRACE);
        }else tree.addSubtree(Exp());
        return tree;
    }

    private static SyntaxTree Stmt() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.Stmt);
        if(token.type==Token.RETURN){
            tree.addSubtree(Token.RETURN);
            if(token.type!= Token.SEMICOLON){
                tree.addSubtree(Exp());
            }
            tree.addSubtree(Token.SEMICOLON);
        }
        else if(token.type == Token.IF){
            tree.addSubtree(Token.IF);
            tree.addSubtree(Token.LPAR);
            tree.addSubtree(Cond());
            tree.addSubtree(Token.RPAR);
            tree.addSubtree(Stmt());
            if(token.type == Token.ELSE){
                tree.addSubtree(Token.ELSE);
                tree.addSubtree(Stmt());
            }
        }
        else if(token.type == Token.WHILE){
            tree.addSubtree(Token.WHILE);
            tree.addSubtree(Token.LPAR);
            tree.addSubtree(Cond());
            tree.addSubtree(Token.RPAR);
            tree.addSubtree(Stmt());
        }
        else if(token.type == Token.BREAK){
            tree.addSubtree(Token.BREAK);
            tree.addSubtree(Token.SEMICOLON);
        }
        else if(token.type == Token.CONTINUE){
            tree.addSubtree(Token.CONTINUE);
            tree.addSubtree(Token.SEMICOLON);
        }
        else if(token.type == Token.LBRACE){
            tree.addSubtree(Block());
        }
        else if(token.type == Token.IDENT){
            SyntaxTree exp = Exp();
            if(token.type == Token.ASSIGN){
                if(exp.getSubtree(0).subtree.size()==1 //AddExp
                        &&exp.getSubtree(0).getSubtree(0).subtree.size()==1 //MulExp
                        &&exp.getSubtree(0).getSubtree(0).getSubtree(0).subtree.size()==1) { //UnaryExp
                    tree.addSubtree(exp.getSubtree(0).getSubtree(0).getSubtree(0).getSubtree(0).getSubtree(0));//LVal
                    tree.addSubtree(Token.ASSIGN);
                    tree.addSubtree(Exp());
                    tree.addSubtree(Token.SEMICOLON);
                }
            }else{
                tree.addSubtree(exp);
                tree.addSubtree(Token.SEMICOLON);
            }
        }
        else if(token.type != Token.SEMICOLON){
            tree.addSubtree(Exp());
            tree.addSubtree(Token.SEMICOLON);
        }
        else{
            tree.addSubtree(Token.SEMICOLON);
        }
        return tree;
    }

    private static SyntaxTree Cond() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.Cond);
        tree.addSubtree(LOrExp());
        return tree;
    }

    private static SyntaxTree LOrExp() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.LOrExp);
        tree.addSubtree(LAndExp());
        while(token.type == Token.OR){
            tree.addSubtree(token.type);
            tree.addSubtree(LAndExp());
        }
        return tree;
    }

    private static SyntaxTree LAndExp() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.LAndExp);
        tree.addSubtree(EqExp());
        while(token.type == Token.AND){
            tree.addSubtree(token.type);
            tree.addSubtree(EqExp());
        }
        return tree;
    }

    private static SyntaxTree EqExp() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.EqExp);
        tree.addSubtree(RelExp());
        while(token.type == Token.EQ || token.type == Token.NEQ){
            tree.addSubtree(token.type);
            tree.addSubtree(RelExp());
        }
        return tree;
    }

    private static SyntaxTree RelExp() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.RelExp);
        tree.addSubtree(AddExp());
        while(token.type == Token.S|| token.type == Token.L||token.type== Token.SE || token.type==Token.LE){
            tree.addSubtree(token.type);
            tree.addSubtree(AddExp());
        }
        return tree;
    }

    private static SyntaxTree LVal() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.LVal);
        tree.addSubtree(Token.IDENT);
        while(token.type == Token.LBRACKET){
            tree.addSubtree(Token.LBRACKET);
            tree.addSubtree(Exp());
            tree.addSubtree(Token.RBRACKET);
        }
        return tree;
    }

    private static SyntaxTree Exp() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.Exp);
        tree.addSubtree(AddExp());
        return tree;
    }

    private static SyntaxTree AddExp() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.AddExp);
        tree.addSubtree(MulExp());
        while(token.type == Token.ADD || token.type == Token.SUB){
            tree.addSubtree(token.type);
            tree.addSubtree(MulExp());
        }
        return tree;
    }

    private static SyntaxTree MulExp() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.MulExp);
        tree.addSubtree(UnaryExp());
        while(token.type == Token.MULT || token.type == Token.DIV || token.type ==Token.MOD){
            tree.addSubtree(token.type);
            tree.addSubtree(UnaryExp());
        }
        return tree;
    }

    private static SyntaxTree UnaryExp() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.UnaryExp);
        while(token.type == Token.ADD || token.type == Token.SUB || token.type == Token.NOT )
            tree.addSubtree(token.type);
        if(token.type == Token.IDENT && Lexer.tokenPreview(1).type == Token.LPAR){
            tree.addSubtree(Token.IDENT);
            tree.addSubtree(Token.LPAR);
            if(token.type != Token.RPAR)
                tree.addSubtree(FuncRParams());
            tree.addSubtree(Token.RPAR);
        }else tree.addSubtree(PrimaryExp());
        return tree;
    }

    private static SyntaxTree FuncRParams() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.FuncRParams);
        tree.addSubtree(Exp());
        while(token.type == Token.COMMA){
            tree.addSubtree(Token.COMMA);
            tree.addSubtree(Exp());
        }
        return tree;
    }

    private static SyntaxTree PrimaryExp() {
        SyntaxTree tree = new SyntaxTree(SyntaxTree.PrimaryExp);
        if(token.type == Token.LPAR ){
            tree.addSubtree(Token.LPAR);
            tree.addSubtree(Exp());
            tree.addSubtree(Token.RPAR);
        }else if(token.type == Token.NUMBER){
            tree.addSubtree(Token.NUMBER);
        }else tree.addSubtree(LVal());
        return tree;
    }


}
