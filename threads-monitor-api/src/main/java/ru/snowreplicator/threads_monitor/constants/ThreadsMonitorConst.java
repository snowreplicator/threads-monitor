package ru.snowreplicator.threads_monitor.constants;

import java.util.Arrays;

import com.liferay.portal.kernel.util.StringPool;

public class ThreadsMonitorConst {

    // константы полей табулятора (столбцы табулятора)
    public final static String FIELD_ID         = "id";         // id потока
    public final static String FIELD_PRIORITY   = "priority";   // приоритет
    public final static String FIELD_STATE      = "state";      // состояние
    public final static String FIELD_NAME       = "name";       //наименование

    // получить массив полей табулятора (последовательность полей по умолчанию)
    public static String[] getColumns() {
        String columns[] = { FIELD_ID, FIELD_PRIORITY, FIELD_STATE, FIELD_NAME };
        return columns;
    }

    // получить наименование поля по его id
    public static String getColumnName(String columnId) {
        switch (columnId) {
            case FIELD_ID:          return "thread-title-id";
            case FIELD_PRIORITY:    return "thread-title-priority";
            case FIELD_STATE:       return "thread-title-state";
            case FIELD_NAME:        return "thread-title-name";
        }
        return "unknown-field";
    }

    // проверка валидного задания типа столбца табулятора
    public static boolean validateColumnId(String columnId) {
        String columns[] = getColumns();
        return Arrays.asList(columns).contains(columnId);
    }

    // сортировка полей табулятора
    public final static int COLUMN_SORT_NONE    = 0;    // сортировка не задана
    public final static int COLUMN_SORT_ASC     = 1;    // по возрастанию
    public final static int COLUMN_SORT_DESC    = 2;    // по убыванию

    // константы сортировки для табулятора
    private static final String DIR_ASC     = "asc";    // по возрастанию
    private static final String DIR_DESC    = "desc";   // по убыванию

    // получить направление сортировки для поля табулятора
    public static String getColumnSortDir(int sortType) {
        switch (sortType) {
            case COLUMN_SORT_ASC:   return DIR_ASC;
            case COLUMN_SORT_DESC:  return DIR_DESC;
        }
        return StringPool.BLANK;
    }

    // получить направление сортировки из табулятора
    public static int getColumnSortDir(String dir) {
        if (dir.equalsIgnoreCase(DIR_ASC)) return COLUMN_SORT_ASC;
        if (dir.equalsIgnoreCase(DIR_DESC)) return COLUMN_SORT_DESC;
        return COLUMN_SORT_NONE;
    }

    // получить сортировальщик данных в столбце
    public static String getColumnSorter(String columnId) {
        switch (columnId) {
            case FIELD_ID:
            case FIELD_PRIORITY:
                return "number";
            case FIELD_STATE:
            case FIELD_NAME:
                return "string";
        }
        return StringPool.BLANK;
    }

    // ширина столбцов табулятора по умолчанию
    public final static int COLUMN_WIDTH_DEFAULT = 300;
    // минимально допустимая ширина столбцов табулятора
    public final static int COLUMN_MIN_WIDTH = 10;

    // получить ширину столбца табулятора
    public static int getColumnWidth(int width) {
        if (width < COLUMN_MIN_WIDTH) width = COLUMN_MIN_WIDTH;
        return width;
    }

    // получить ширину столбца табулятора по умолчанию
    public static int getColumnDefaultWidth(String columnId) {
        switch (columnId) {
            case FIELD_ID:          return 70;
            case FIELD_PRIORITY:    return 120;
            case FIELD_STATE:       return 170;
            case FIELD_NAME:        return 620;
        }
        return COLUMN_WIDTH_DEFAULT;
    }

}