package org.textfighter;

public class CustomVariable {

    private String name;
    private Object value;
    private Class valueType;

    public String getName() { return name; }
    public Object getValue() { return value; }
    public void setValue(Object o) { value=o; }
    public Class getValueType() { return valueType; }

    public CustomVariable(String name, Object value, Class valueType) {
        this.name = name;
        this.value = value;
        this.valueType = valueType;
    }

}
