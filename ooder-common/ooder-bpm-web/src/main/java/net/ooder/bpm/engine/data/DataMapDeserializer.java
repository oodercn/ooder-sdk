package net.ooder.bpm.engine.data;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.MapDeserializer;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.common.database.dao.DAOException;
import net.ooder.common.database.dao.DAOFactory;
import net.ooder.common.database.metadata.MetadataFactory;
import net.ooder.common.database.metadata.TableInfo;
import net.ooder.jds.core.esb.EsbUtil;


import java.lang.reflect.Type;
import java.util.Set;

public class DataMapDeserializer extends MapDeserializer {
    public static final DataMapDeserializer instance = new DataMapDeserializer();
    public static final String PAGECTXNAME = "PAGECTX";

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        JSONObject obj = new JSONObject();
        parser.parseObject(obj);
        Set<String> keySet = obj.keySet();
        DataMap componentMap = EsbUtil.parExpression("$DataMap", DataMap.class);
        for (String key : keySet) {
            if (!key.equals(PAGECTXNAME)) {
                JSONObject componenobj = obj.getJSONObject(key);
                String tableName = componenobj.getString("tableName");
                String configKey = componenobj.getString("configKey");
                try {
                    if (configKey != null && tableName != null && !configKey.equals("") && !tableName.equals("")) {
                        TableInfo info = this.getMetadataFactory(configKey).getTableInfo(tableName);
                        DAOFactory<T> factory = new DAOFactory(info);
                        Object dbMap = JSONObject.parseObject(componenobj.toJSONString(), factory.getDAO().getBeanClass(true));
                        componentMap.put(key, dbMap);
                    }

                } catch (DAOException e) {
                    e.printStackTrace();
                }
            }
        }
        return (T) componentMap;


    }

    public MetadataFactory getMetadataFactory(String configKey) throws DAOException {
        MetadataFactory factory = MetadataFactory.getInstance(configKey);
        if (factory == null) {
            throw new DAOException("数据库链接失败！configKey[" + configKey + "]");
        }
        return factory;
    }

}