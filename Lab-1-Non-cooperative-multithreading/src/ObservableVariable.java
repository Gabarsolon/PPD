import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ObservableVariable {
    public Lock lock = new ReentrantLock();
    public String name = "";
    public int value;
    public List<ObservableVariable> variablesToNotify;
    public List<ObservableVariable> variablesToWatch;
    public ObservableVariable(Integer value, String name){
        this.value = value;
        this.name = name;
        this.variablesToNotify = new ArrayList<>();
    }
    public ObservableVariable(List<ObservableVariable> variablesToWatch, String name){
        this.name = name;
        this.variablesToWatch = variablesToWatch;
        this.value = computeSum();
        variablesToNotify = new ArrayList<>();
    }
    private void notifyVariables(Integer value){
        variablesToNotify.stream().forEach((variable) ->
                variable.updateVariable(value)
        );
    }
    public void setVariable(Integer newValue){
        Integer differenceBetweenOldValueAndNewValue = newValue - value;
        value = newValue;
        notifyVariables(differenceBetweenOldValueAndNewValue);
    }

    public void updateVariable(Integer newValue){
        lock.lock();
        this.value += newValue;
        notifyVariables(newValue);
        lock.unlock();
    }

    public int computeSum(){

       return variablesToWatch.stream().map((variable) -> variable.value).reduce(0, Integer::sum);
    }

    public boolean consistencyCheck(){
        return value == computeSum();
    }
}