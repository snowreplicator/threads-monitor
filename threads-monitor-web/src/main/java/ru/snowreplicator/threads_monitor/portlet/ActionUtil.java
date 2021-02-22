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

        JSONArray initialSortJsonArray = JSONFactoryUtil.createJSONArray();
        JSONArray columnsJsonArray = JSONFactoryUtil.createJSONArray();
        for (ThreadTableProps threadTableProps : ThreadTablePropsList) {
            // текущий параметр столбца
            JSONObject columnJsonObject = JSONFactoryUtil.createJSONObject();
            columnJsonObject.put("title",   ThreadsMonitorKeys.translate(locale, ThreadsMonitorConst.getColumnName(threadTableProps.getColumnId())));
            columnJsonObject.put("field",   threadTableProps.getColumnId());
            columnJsonObject.put("sorter",  ThreadsMonitorConst.getColumnSorter(threadTableProps.getColumnId()));
            columnJsonObject.put("width",   ThreadsMonitorConst.getColumnWidth(threadTableProps.getWidth()));
            columnsJsonArray.put(columnJsonObject);

            // текущий параметр начальной сортировки
            if (!ThreadsMonitorConst.getColumnSortDir(threadTableProps.getSortType()).isEmpty()) {
                JSONObject initialSortJsonObject = JSONFactoryUtil.createJSONObject();
                initialSortJsonObject.put("column", threadTableProps.getColumnId());
                initialSortJsonObject.put("dir",    ThreadsMonitorConst.getColumnSortDir(threadTableProps.getSortType()));
                initialSortJsonArray.put(initialSortJsonObject);
            }
        }

        JSONObject columnsJsonObject = JSONFactoryUtil.createJSONObject();
        columnsJsonObject.put("columns", columnsJsonArray);
        columnsJsonObject.put("initialSort", initialSortJsonArray);
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
        return threadsMonitorDataJsonObject.toJSONString();
    }

}