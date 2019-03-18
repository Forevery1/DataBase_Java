package org.forevery.database.bean;


import java.util.HashMap;

public class SaveData {

    private HashMap<String, Object> Data = new HashMap<>();

    public HashMap<String, Object> getData() {
        return Data;
    }

    /**
     * 保存
     *
     * @param FieldName 表名
     * @param value     值
     */
    public void Save(String FieldName, Object value) {
        Data.put(FieldName, value);
    }
}
