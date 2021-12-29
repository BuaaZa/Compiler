public class Symbol {
    public static final int TypeVoid = 0, TypeInt =  1,TypePointer = 2;

    public String name;
    public int BType;

    public Symbol(String name,int BType) {
        this.name = name;
        this.BType = BType;
    }

    public Symbol(int BType){
        this.BType = BType;
    }
}
