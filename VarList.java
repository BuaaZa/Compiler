import java.util.ArrayList;
import java.util.HashMap;

public class VarList {
    HashMap<String,Variable> variableHashMap = new HashMap<>();

    public int blockNum;
    public int regNum;
    public VarList prevVarList ;

    public VarList(int blockNum,VarList prevVarList) {
        this.blockNum = blockNum;
        this.prevVarList = prevVarList;
        this.regNum = 0;
    }

    /**
     * 添加数组变量
     * @param name 变量名
     * @param BType 变量类型
     * @param arrayDimensions 数组维数列表
     */
    public void putVariable(String name,int BType,ArrayList<Integer> arrayDimensions,boolean isConst){
        variableHashMap.put(name,new Variable(name,BType,this.blockNum,this.regNum++,arrayDimensions,isConst));
    }

    /**
     * 添加普通变量
     * @param name 变量名
     * @param BType 变量类型
     */
    public void putVariable(String name,int BType){
        variableHashMap.put(name,new Variable(name,BType,this.blockNum,this.regNum++));
    }

    /**
     * 添加常量
     * @param name 变量名
     * @param BType 变量类型
     * @param value 常量值
     */
    public void putVariable(String name,int BType,int value){
        variableHashMap.put(name,new Variable(name,BType,value));
    }

    public void putVariable(String name,Variable variable){
        variableHashMap.put(name,variable);
    }

    public Variable getVariable(String name){
        return variableHashMap.get(name);
    }

    public void setRegNum(int regNum) {
        this.regNum = regNum;
    }
}
