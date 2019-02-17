package org.textfighter.method;

import org.textfighter.method.TFMethod;

import java.lang.reflect.*;

@SuppressWarnings("unchecked")

public class DeathMethod {

    /**
     * Stores the indentifier for omitting.
     */
    private String id;
    /**
     * Stores the method.
     */
    private TFMethod method;
    /**
     * Returns the {@link #id}.
     * @return      {@link #id}
     */
    public String getId() { return id; }
    /**
     * Returns the {@link #method}.
     * @return      {@link #method}
     */
    public TFMethod getMethod() { return method; }

    /*** Calls invokeMethod() on the {@link #method}*/
    public void invokeMethod() { method.invokeMethod(); }

    public DeathMethod(TFMethod method, String id) {
        this.method = method;
        this.id = id;
    }
}
