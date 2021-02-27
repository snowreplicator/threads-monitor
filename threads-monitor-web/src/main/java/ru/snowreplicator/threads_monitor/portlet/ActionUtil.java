package ru.snowreplicator.threads_monitor.portlet;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import ru.snowreplicator.threads_monitor.constants.ThreadsMonitorConst;
import ru.snowreplicator.threads_monitor.constants.ThreadsMonitorKeys;
import ru.snowreplicator.threads_monitor.model.ThreadTableProps;
import ru.snowreplicator.threads_monitor.model.ThreadTableSettings;
import ru.snowreplicator.threads_monitor.service.ThreadTablePropsLocalServiceUtil;
import ru.snowreplicator.threads_monitor.service.ThreadTableSettingsLocalServiceUtil;

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

    // получить ID локали
    public static String getLanguageId(Locale locale) {
        String languageId = LocaleUtil.toLanguageId(locale);
        return languageId;
    }

    // получить переводы элементов табулятора
    public static JSONObject getLangs(Locale locale) {
        JSONObject paginationParamsJsonObject = JSONFactoryUtil.createJSONObject();
        paginationParamsJsonObject.put("page_size",      ThreadsMonitorKeys.translate(locale, "pagination-page-size"));
        paginationParamsJsonObject.put("page_title",     ThreadsMonitorKeys.translate(locale, "pagination-page-title"));
        paginationParamsJsonObject.put("first",          ThreadsMonitorKeys.translate(locale, "pagination-first"));
        paginationParamsJsonObject.put("first_title",    ThreadsMonitorKeys.translate(locale, "pagination-first-title"));
        paginationParamsJsonObject.put("last",           ThreadsMonitorKeys.translate(locale, "pagination-last"));
        paginationParamsJsonObject.put("last_title",     ThreadsMonitorKeys.translate(locale, "pagination-last-title"));
        paginationParamsJsonObject.put("prev",           ThreadsMonitorKeys.translate(locale, "pagination-prev"));
        paginationParamsJsonObject.put("prev_title",     ThreadsMonitorKeys.translate(locale, "pagination-prev-title"));
        paginationParamsJsonObject.put("next",           ThreadsMonitorKeys.translate(locale, "pagination-page-next"));
        paginationParamsJsonObject.put("next_title",     ThreadsMonitorKeys.translate(locale, "pagination-page-next-title"));
        paginationParamsJsonObject.put("all",            ThreadsMonitorKeys.translate(locale, "pagination-all"));

        JSONObject groupsParamsJsonObject = JSONFactoryUtil.createJSONObject();
        groupsParamsJsonObject.put("item",           ThreadsMonitorKeys.translate(locale, "group-item"));
        groupsParamsJsonObject.put("items",          ThreadsMonitorKeys.translate(locale, "group-items"));

        JSONObject paginationJsonObject = JSONFactoryUtil.createJSONObject();
        paginationJsonObject.put("pagination", paginationParamsJsonObject);
        paginationJsonObject.put("groups", groupsParamsJsonObject);

        JSONObject langsJsonObject = JSONFactoryUtil.createJSONObject();
        langsJsonObject.put(LocaleUtil.toLanguageId(locale), paginationJsonObject);
        return langsJsonObject;
    }

    // получить столбцы табулятора
    public static JSONObject getColumnsData(long userId, Locale locale) {
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
            columnJsonObject.put("visible", threadTableProps.getShow());
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

    // получить настройку столбца табулятора по которому выполнять группировку
    public static String getGroupingColumn(long userId) {
        String groupingColumn = ThreadsMonitorConst.FIELD_GROUP_NONE;
        ThreadTableSettings threadTableSettings = ThreadTableSettingsLocalServiceUtil.fetchThreadTableSettings(userId);
        if (threadTableSettings != null) {
            groupingColumn = threadTableSettings.getGroupColumn();
        }
        return groupingColumn;
    }

    // получить объект с данными для отображения табулятора со списком процессов
    public static JSONObject threadsMonitorDataJsonObject(long userId, Locale locale) {
        ThreadTableSettings threadTableSettings = ThreadTableSettingsLocalServiceUtil.fetchThreadTableSettings(userId);
        int pageSize = threadTableSettings == null ? ThreadsMonitorConst.MIN_PAGE_SIZE : threadTableSettings.getPageSize();
        JSONArray threadsData = getThreadsData();
        JSONObject columnsData = getColumnsData(userId, locale);
        JSONObject langsJsonObject = getLangs(locale);

        JSONObject threadsMonitorDataJsonObject = JSONFactoryUtil.createJSONObject();
        threadsMonitorDataJsonObject.put("languageId",          getLanguageId(locale));
        threadsMonitorDataJsonObject.put("langs",               langsJsonObject);
        threadsMonitorDataJsonObject.put("pageSize",            pageSize);
        threadsMonitorDataJsonObject.put("threadsData",         threadsData);
        threadsMonitorDataJsonObject.put("columnsData",         columnsData);
        threadsMonitorDataJsonObject.put("groupingColumn",      getGroupingColumn(userId));
        return threadsMonitorDataJsonObject;
    }

    // получить объект с данными для отображения табулятора со списком процессов (сериализованный в строку json)
    public static String threadsMonitorDataJsonString(long userId, Locale locale) {
        JSONObject threadsMonitorDataJsonObject = threadsMonitorDataJsonObject(userId, locale);
        return threadsMonitorDataJsonObject.toJSONString();
    }

}