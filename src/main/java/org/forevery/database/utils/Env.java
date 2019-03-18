package org.forevery.database.utils;

import org.ho.yaml.Yaml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * 配置文件获取工具
 */
public final class Env {

    private static Map ymlMap;
    private static Element root;
    private static Properties props = new Properties();
    private static int type = 0;

    static {
        InputStream inputStream = null;
        try {
            inputStream = Env.class.getResourceAsStream("/database.yml");
            ymlMap = (Map) Yaml.load(inputStream);
            Log.i("配置文件类型为 ：yml");
            type = 1;
        } catch (Exception ignore) {
        }
        if (inputStream == null) {
            try {
                inputStream = Env.class.getResourceAsStream("/database.properties");
                props.load(inputStream);
                Log.i("配置文件类型为 ：properties");
                type = 2;
            } catch (Exception ignore) {
            }
        }
        if (inputStream == null) {
            try {
                inputStream = Env.class.getResourceAsStream("/database.xml");
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(inputStream);
                root = doc.getDocumentElement();
                Log.i("配置文件类型为 ：xml");
                type = 3;
            } catch (Exception ignore) {
            }
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e("关闭文件输入流失败：");
            }
        } else {
            Log.e("读取[resources]目录下的[database.yml]配置文件失败!");
            Log.e("读取[resources]目录下的[database.properties]配置文件失败!");
            Log.e("读取[resources]目录下的[database.xml]配置文件失败!");
        }
    }

    /**
     * YML 获取值 .隔开
     *
     * @param key key
     * @return String
     */
    private static String getYmlValue(String key, String def) {
        try {
            String[] keys = key.split("[.]");
            if (keys.length == 1) return (String) ymlMap.get(key);
            Object o = ymlMap.get(keys[0]);
            for (int i = 1; i < keys.length; i++) {
                o = ((Map) o).get(keys[i]);
            }
            if (o == null) throw new Exception();
            return String.valueOf(o);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 获取配置文件值
     *
     * @param key key
     * @param def 默认值
     * @return 值
     */
    public static String getProperty(String key, String def) {
        switch (type) {
            case 1:
                return getYmlValue(key, def);
            case 2:
                return props.getProperty(key) == null ? def : props.getProperty(key);
            case 3:
                return root.getAttribute(key) == null ? def : root.getAttribute(key);
            default:
                return "";
        }
    }

    /**
     * 获取配置文件值
     *
     * @param key key
     * @return 值
     */
    public static String getProperty(String key) {
        return getProperty(key, "");
    }
}