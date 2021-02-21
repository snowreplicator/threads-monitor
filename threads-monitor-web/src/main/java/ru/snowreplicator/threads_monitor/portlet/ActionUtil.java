package ru.snowreplicator.threads_monitor.portlet;

import java.util.Locale;
import java.util.Map;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ru.snowreplicator.threads_monitor.constants.ThreadsMonitorKeys;

public class ActionUtil {

    private static Log _log = LogFactoryUtil.getLog(ActionUtil.class);

    // получить список потоков
    public static JSONArray getThreadsData() {
        JSONArray threadsJsonArray = JSONFactoryUtil.createJSONArray();
        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        for (Thread thread : threads.keySet()) {
            JSONObject threadJsonObject = JSONFactoryUtil.createJSONObject();
            threadJsonObject.put("id", thread.getId());
            threadJsonObject.put("priority", thread.getPriority());
            threadJsonObject.put("state", thread.getState());
            threadJsonObject.put("name", thread.getName());
            threadsJsonArray.put(threadJsonObject);
        }
        return threadsJsonArray;
    }

    // получить столбцы табулятора
    public static JSONObject getColumnsData(long userId, long groupId, Locale locale) {

        JSONArray columnsJsonArray = JSONFactoryUtil.createJSONArray();

        // title
        {
            JSONObject columnJsonObject = JSONFactoryUtil.createJSONObject();
            columnJsonObject.put("title", ThreadsMonitorKeys.translate(locale, "thread-title-id"));
            columnJsonObject.put("field", "id");
            columnJsonObject.put("sorter", "number");
            columnsJsonArray.put(columnJsonObject);
        }
        // priority
        {
            JSONObject columnJsonObject = JSONFactoryUtil.createJSONObject();
            columnJsonObject.put("title", ThreadsMonitorKeys.translate(locale, "thread-title-priority"));
            columnJsonObject.put("field", "priority");
            columnJsonObject.put("sorter", "number");
            columnsJsonArray.put(columnJsonObject);
        }
        // state
        {
            JSONObject columnJsonObject = JSONFactoryUtil.createJSONObject();
            columnJsonObject.put("title", ThreadsMonitorKeys.translate(locale, "thread-title-state"));
            columnJsonObject.put("field", "state");
            columnJsonObject.put("sorter", "string");
            columnsJsonArray.put(columnJsonObject);
        }
        // name
        {
            JSONObject columnJsonObject = JSONFactoryUtil.createJSONObject();
            columnJsonObject.put("title", ThreadsMonitorKeys.translate(locale, "thread-title-name"));
            columnJsonObject.put("field", "name");
            columnJsonObject.put("sorter", "string");
            columnsJsonArray.put(columnJsonObject);
        }

        JSONObject columnsJsonObject = JSONFactoryUtil.createJSONObject();
        columnsJsonObject.put("columns", columnsJsonArray);
        return columnsJsonObject;
    }

    // получить объект с данными для отображения табулятора со списком процессов
    public static JSONObject threadsMonitorDataJsonObject(long userId, long groupId, Locale locale) {
        JSONArray threadsData = getThreadsData();
        JSONObject columnsData = getColumnsData(userId, groupId, locale);

        JSONObject threadsMonitorDataJsonObject = JSONFactoryUtil.createJSONObject();
        threadsMonitorDataJsonObject.put("threadsData", threadsData);
        threadsMonitorDataJsonObject.put("columnsData", columnsData);
        return threadsMonitorDataJsonObject;
    }

    // получить объект с данными для отображения табулятора со списком процессов (сериализованный в строку json)
    public static String threadsMonitorDataJsonString(long userId, long groupId, Locale locale) {
        JSONObject threadsMonitorDataJsonObject = threadsMonitorDataJsonObject(userId, groupId, locale);
        _log.info("threadsMonitorDataJsonObject = " + threadsMonitorDataJsonObject.toJSONString());
        return threadsMonitorDataJsonObject.toJSONString();
    }

}
