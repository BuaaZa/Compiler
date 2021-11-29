import java.util.ArrayList;

public class SyntaxTree extends Token{
    public static final String CompUnit ="CompUnit"
            ,FuncDef = "FuncDef"
            ,FuncType = "FuncType"
            ,Block = "Block"
            ,BlockItem = "BlockItem"
            ,Decl="Decl"
            ,ConstDecl="ConstDecl"
            ,ConstDef="ConstDef"
            ,ConstInitVal="ConstInitVal"
            ,ConstExp="ConstExp"
            ,BType="BType"
            ,VarDecl="VarDecl"
            ,VarDef="VarDef"
            ,InitVal="InitVal"
            ,Stmt="Stmt"
            ,LVal="LVal"
            ,Exp="Exp"
            ,AddExp="AddExp"
            ,MulExp="MulExp"
            ,UnaryExp="UnaryExp"
            ,FuncRParams="FuncRParams"
            ,PrimaryExp="PrimaryExp";

    public String name = null;
    public ArrayList<SyntaxTree> subtree;

    public SyntaxTree(String name) {
        this.name = name;
        this.subtree = new ArrayList<>();
    }

    public SyntaxTree(int type){
        super(type);
    }

    public SyntaxTree(int type,String content){
        super(type,content);
    }

    public void addSubtree(SyntaxTree tree){
        this.subtree.add(tree);
    }

    public void addSubtree(int type){
        if(Parser.token.type == type){
            if(type == Token.NUMBER || type == Token.IDENT){
                this.subtree.add(new SyntaxTree(type,Parser.token.content));
            }else
                this.subtree.add(new SyntaxTree(type));
            Parser.token = Lexer.getToken();
            //System.out.println(Parser.token.type);
        } else {
            System.exit(1);
        }
    }

    public SyntaxTree getSubtree(int index){
        return subtree.get(index);
    }

    public SyntaxTree searchSubtree(String name){
        for (SyntaxTree t:subtree) {
            if(t.name!=null && t.name.equals(name))
                return t;
        }
        return null;
    }

    public SyntaxTree searchSubtree(int type){
        for (SyntaxTree t:subtree) {
            if(t.type == type)
                return t;
        }
        return null;
    }

}
