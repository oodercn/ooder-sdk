package net.ooder.bpm.client.data;

public interface DataFactory {

    public DataMap getFormMap();

    public Object getDataByFormId(String formId);

    public void update(DataMap formdata);

    public void delete(DataMap map);
}
