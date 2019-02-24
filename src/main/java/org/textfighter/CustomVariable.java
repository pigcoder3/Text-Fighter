package org.textfighter;

public class CustomVariable {

    /*** Stores the name of the custom variable.*/
    private String name;
    /*** Stores the value of the custom variable.*/
    private Object value;
    /*** Stores the class of the value.*/
    private Class valueType;
    /*** Stores whether or not the custom variable is included in stat outputs. */
    private boolean inOutput;
    /*** Stores whether or not the custom variable is saved in the save file. */
    private boolean isSaved;

    /**
     * Returns the {@link #name}.
     * @return      {@link #name}
     */
    public String getName() { return name; }
    /**
     * Returns the {@link #value}.
     * @return      {@link #value}
     */
    public Object getValue() { return value; }
    /**
     * Sets the {@link #value} to the value given.
     * <p>The new value can be null.</p>
     * @param o     The new value.
     */
    public void setValue(Object o) { value=o; }
    /**
     * Returns the value's class ({@link #valueType}.
     * @return      {@link #valueType}
     */
    public Class getValueType() { return valueType; }
    /**
     * Returns {@link #inOutput}.
     * @return      {@link #inOutput}
     */
    public boolean getInOutput() { return inOutput; }
    /**
     * Returns {@link #isSaved}.
     * @return      {@link #isSaved}
     */
    public boolean getIsSaved() { return isSaved; }

    public CustomVariable(String name, Object value, Class valueType, boolean inOutput, boolean isSaved) {
        this.name = name;
        this.value = value;
        this.valueType = valueType;
        this.inOutput = inOutput;
        this.isSaved = isSaved;
    }

}
