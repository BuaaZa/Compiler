import java.util.ArrayList;
import java.util.HashMap;

public class FuncList {
    HashMap<String,Function> functionHashMap = new HashMap<>() ;

    public void putFunction(String name, int FuncType){
        functionHashMap.put(name,new Function(name,FuncType));
    }

    public Function getFunction (String name){
        return functionHashMap.get(name);
    }
}
