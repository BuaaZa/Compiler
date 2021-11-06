import java.util.ArrayList;

public class SyntaxTree extends Token{
    String name;
    ArrayList<SyntaxTree> subtree;

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
        } else System.exit(1);
    }

}
