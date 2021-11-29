import java.util.ArrayList;
import java.util.HashMap;

public class VarList {
    HashMap<String,Variable> variableHashMap = new HashMap<>();

    int blockNum;
    VarList prevVarList ;
    int regNum;

    public VarList(int blockNum,VarList prevVarList) {
        this.blockNum = blockNum;
        this.prevVarList = prevVarList;
        this.regNum = 0;
    }

    public void putVariable(String name,int BType){
        variableHashMap.put(name,new Variable(name,BType,this.blockNum,this.regNum++));
    }

    public void putVariable(String name,int BType,int value){
        variableHashMap.put(name,new Variable(name,BType,value));
    }

    public Variable getVariable(String name){
        return variableHashMap.get(name);
    }

}
