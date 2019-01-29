package org.textfighter;

public class CustomVariable {

    private String name;
    private Object value;
    private Class valueType;
    private boolean inOutput = true;

    public String getName() { return name; }
    public Object getValue() { return value; }
    public void setValue(Object o) { value=o; }
    public Class getValueType() { return valueType; }
    public boolean getInOutput() { return inOutput; }

    public CustomVariable(String name, Object value, Class valueType, boolean inOutput) {
        this.name = name;
        this.value = value;
        this.valueType = valueType;
        this.inOutput = inOutput;
    }

}
