package net.ooder.config;

import net.ooder.common.JDSException;

import java.util.Collection;

public class ListResultModel<T extends Collection> extends ResultModel<T> {

    public Integer size = -1;

    public int getSize() {
        if (size == -1) {
            try {
                T object = this.get();
                if (object != null) {
                    size = (object).size();
                } else {
                    size = 0;
                }

            } catch (JDSException e) {
                //e.printStackTrace();
                size = 0;
            }
        }
        ;
        return size;
    }

    @Override
    public T get() throws JDSException {
        return super.get();
    }


    @Override
    public T getData() {
        return super.getData();
    }

    public void setSize(int size) {
        this.size = size;
    }

}
