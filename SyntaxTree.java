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
            ,PrimaryExp="PrimaryExp"
            ,Cond = "Cond"
            ,LOrExp = "LOrExp"
            ,LAndExp ="LAndExp"
            ,EqExp="EqExp"
            ,RelExp="RelExp";

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
        //System.out.println(tree.name);
        this.subtree.add(tree);

    }

    public void addSubtree(int type){
        //System.out.println(type);
        if(Parser.token.type == type){
            if(type == Token.NUMBER || type == Token.IDENT){
                this.subtree.add(new SyntaxTree(type,Parser.token.content));
            }else
                this.subtree.add(new SyntaxTree(type));
            Parser.token = Lexer.getToken();

            //System.out.println(Parser.token.type);
        } else {
            /*if(name!=null)
                System.out.println(name);
            System.out.println(type);
            System.out.println(Parser.token.type);*/
            System.exit(1);
        }
    }

    public SyntaxTree getSubtree(int index){
        if(index<0 ||index>=subtree.size())
            return null;
        return subtree.get(index);
    }

    public int searchSubtree(String name){
        for (int i = 0; i < subtree.size(); i++) {
            if(subtree.get(i).name!=null && subtree.get(i).name.equals(name))
                return i;
        }
        return -1;
    }

    public int searchSubtree(int type){
        for (int i = 0; i < subtree.size(); i++) {
            if(subtree.get(i).type == type)
                return i;
        }
        return -1;
    }

}
