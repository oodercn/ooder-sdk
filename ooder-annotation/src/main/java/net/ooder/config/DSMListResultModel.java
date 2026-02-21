
package net.ooder.config;

import net.ooder.common.JDSException;

import java.util.Collection;

public class DSMListResultModel<T extends Collection> extends DSMResultModel<T> {
    public Integer size = -1;

    public DSMListResultModel() {
    }

    public int getSize() {
        if (this.size == -1) {
            try {
                T object = this.get();
                if (object != null) {
                    this.size = object.size();
                } else {
                    this.size = 0;
                }
            } catch (JDSException var2) {
                this.size = 0;
            }
        }

        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
