package net.ooder.vfs.sync;

public class TaskResult<T> {
        int result = 0;
        String des = "";
        T data;


        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }