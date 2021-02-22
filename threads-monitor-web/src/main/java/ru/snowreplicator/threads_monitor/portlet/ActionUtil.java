package ru.snowreplicator.threads_monitor.portlet;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ru.snowreplicator.threads_monitor.constants.ThreadsMonitorConst;
import ru.snowreplicator.threads_monitor.constants.ThreadsMonitorKeys;
import ru.snowreplicator.threads_monitor.model.ThreadTableProps;
import ru.snowreplicator.threads_monitor.service.ThreadTablePropsLocalServiceUtil;

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
        List<ThreadTableProps> ThreadTablePropsList = ThreadTablePropsLocalServiceUtil.getColumns(userId);

        JSONArray columnsJsonArray = JSONFactoryUtil.createJSONArray();
        for (ThreadTableProps ThreadTableProps : ThreadTablePropsList) {
            JSONObject columnJsonObject = JSONFactoryUtil.createJSONObject();
            columnJsonObject.put("title",   ThreadsMonitorKeys.translate(locale, ThreadsMonitorConst.getColumnName(ThreadTableProps.getColumnId())));
            columnJsonObject.put("field",   ThreadTableProps.getColumnId());
            columnJsonObject.put("sorter",  ThreadsMonitorConst.getColumnSorter(ThreadTableProps.getColumnId()));
            columnJsonObject.put("width",   ThreadsMonitorConst.getColumnWidth(ThreadTableProps.getWidth()));
            columnJsonObject.put("dir",     ThreadsMonitorConst.getColumnSortDir(ThreadTableProps.getSortType()));
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
        _log.info("threadsMonitorDataJsonObject = " + threadsMonitorDataJsonObject.toJSONString()); // !!!!! delete
        return threadsMonitorDataJsonObject.toJSONString();
    }

}
