import java.util.ArrayList;
import java.util.List;

public class ObservableVariable {
    public Integer value;
    public List<ObservableVariable> variablesToNotify;
    public List<ObservableVariable> variablesToWatch;
    public ObservableVariable(Integer value){
        this.value = value;
        this.variablesToNotify = new ArrayList<>();
    }
    public ObservableVariable(List<ObservableVariable> variablesToWatch){
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
        this.value += newValue;
        notifyVariables(newValue);
    }

    public Integer computeSum(){
       return variablesToWatch.stream().map((variable) -> variable.value).reduce(0, Integer::sum);
    }
    public boolean consistencyCheck(){
        return this.value == computeSum();
    }
}
