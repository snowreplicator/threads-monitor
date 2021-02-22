package ru.snowreplicator.threads_monitor.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import ru.snowreplicator.threads_monitor.comparator.ThreadTablePropsComparator;
import ru.snowreplicator.threads_monitor.constants.ThreadsMonitorConst;
import ru.snowreplicator.threads_monitor.exception.NoSuchThreadTablePropsException;
import ru.snowreplicator.threads_monitor.exception.WrongColumnIdException;
import ru.snowreplicator.threads_monitor.exception.WrongColumnsOrderException;
import ru.snowreplicator.threads_monitor.model.ThreadTableProps;
import ru.snowreplicator.threads_monitor.service.base.ThreadTablePropsLocalServiceBaseImpl;
import ru.snowreplicator.threads_monitor.service.persistence.ThreadTablePropsPK;

public class ThreadTablePropsLocalServiceImpl extends ThreadTablePropsLocalServiceBaseImpl {

    private static Log _log = LogFactoryUtil.getLog(ThreadTablePropsLocalServiceImpl.class);

    // получить пустой объект
    public ThreadTableProps getEmptyObject() {
        ThreadTablePropsPK threadTablePropsPK = new ThreadTablePropsPK(0, "");
        ThreadTableProps threadTableProps = threadTablePropsPersistence.create(threadTablePropsPK);
        return threadTableProps;
    }

    // профетчить настройку по pri key
    public ThreadTableProps fetchThreadTableProps(long userId, String columnId) {
        ThreadTablePropsPK threadTablePropsPK = new ThreadTablePropsPK(userId, columnId);
        ThreadTableProps threadTableProps = threadTablePropsPersistence.fetchByPrimaryKey(threadTablePropsPK);
        return threadTableProps;
    }

    // получить настройку по pri key
    public ThreadTableProps getThreadTableProps(long userId, String columnId) throws NoSuchThreadTablePropsException {
        ThreadTableProps threadTableProps = fetchThreadTableProps(userId, columnId);
        if (threadTableProps == null) throw new NoSuchThreadTablePropsException();
        return threadTableProps;
    }

    // получить настройки полей в разрезе пользователя
    public List<ThreadTableProps> getThreadTablePropsByUserId(long userId) {
        List<ThreadTableProps> threadTablePropsList = threadTablePropsPersistence.findByUserId(userId);
        return threadTablePropsList;
    }

    private ThreadTableProps fillColumnWithDefaultValues(ThreadTableProps threadTableProps, long userId, String columnId) {
        threadTableProps.setUserId(userId);
        threadTableProps.setColumnId(columnId);
        threadTableProps.setShow(true);
        threadTableProps.setSortType(ThreadsMonitorConst.COLUMN_SORT_NONE);
        threadTableProps.setWidth(ThreadsMonitorConst.getColumnDefaultWidth(columnId));
        threadTableProps.setNum(0);
        return threadTableProps;
    }

    // получить список столбцов таблуятора
    public List<ThreadTableProps> getColumns(long userId) {
        String columns[] = ThreadsMonitorConst.getColumns();
        List<ThreadTableProps> threadTablePropsList = new ArrayList<>(getThreadTablePropsByUserId(userId));

        // дополняем список столбцов из базы теми стобцами которых там еще нет
        for (String column : columns) {
            Optional<ThreadTableProps> matchingThreadTablePropsObject = threadTablePropsList.stream().
                    filter(threadTableProps -> threadTableProps.getColumnId().equalsIgnoreCase(column)).
                    findFirst();

            if (!matchingThreadTablePropsObject.isPresent()) {
                ThreadTableProps threadTableProps = getEmptyObject();
                threadTableProps = fillColumnWithDefaultValues(threadTableProps, userId, column);
                threadTablePropsList.add(threadTableProps);
            }
        }

        // масимальный номер столбца который есть в базе
        Comparator<ThreadTableProps> comparator = Comparator.comparing(ThreadTableProps::getNum);
        ThreadTableProps maxNumInThreadTableProps = threadTablePropsList.stream().max(comparator).get();

        // добавляем номера больше существующих в базе тем столбцам которых в базе нет
        int num = maxNumInThreadTableProps.getNum();
        for (ThreadTableProps threadTableProps : threadTablePropsList) {
            if (threadTableProps.getNum() > 0) continue;

            num++;
            threadTableProps.setNum(num);
        }

        // сортировка списка столбцов по увеличению номера
        threadTablePropsList = ListUtil.sort(threadTablePropsList, new ThreadTablePropsComparator(ThreadTablePropsComparator.SORT_MODE_BY_NUM));

        return threadTablePropsList;
    }

    // сохранить настройку сортировки столбца табулятора
    public ThreadTableProps saveColumnSortDir(String columnId, String dir, long userId) throws PortalException {
        if (!ThreadsMonitorConst.validateColumnId(columnId)) throw new WrongColumnIdException();

        saveDefaultColumnParamsForAbsentColumns(userId);

        // сохранение настройки сортировки
        ThreadTableProps threadTableProps = fetchThreadTableProps(userId, columnId);
        if (threadTableProps == null) {
            threadTableProps = getEmptyObject();
            threadTableProps = fillColumnWithDefaultValues(threadTableProps, userId, columnId);
        }
        threadTableProps.setSortType(ThreadsMonitorConst.getColumnSortDir(dir));
        threadTableProps = threadTablePropsPersistence.update(threadTableProps);

        // сброс параметров сортировки по остальным столбцам
        List<ThreadTableProps> threadTablePropsList = getThreadTablePropsByUserId(userId);
        for (ThreadTableProps item : threadTablePropsList) {
            if (item.equals(threadTableProps)) continue;

            item.setSortType(ThreadsMonitorConst.COLUMN_SORT_NONE);
            threadTablePropsPersistence.update(item);
        }

        return threadTableProps;
    }

    // сохранить настройку ширины столбца табулятора
    public ThreadTableProps saveColumnWidth(String columnId, int width, long userId) throws PortalException {
        if (!ThreadsMonitorConst.validateColumnId(columnId)) throw new WrongColumnIdException();

        saveDefaultColumnParamsForAbsentColumns(userId);

        // сохранение настройки ширины
        ThreadTableProps threadTableProps = fetchThreadTableProps(userId, columnId);
        if (threadTableProps == null) {
            threadTableProps = getEmptyObject();
            threadTableProps = fillColumnWithDefaultValues(threadTableProps, userId, columnId);
        }
        threadTableProps.setWidth(ThreadsMonitorConst.getColumnWidth(width));
        threadTableProps = threadTablePropsPersistence.update(threadTableProps);

        return threadTableProps;
    }

    // сохранить значения по умолчанию для столбцов отсутствующих в БД
    private void saveDefaultColumnParamsForAbsentColumns(long userId) {
        List<ThreadTableProps> threadTablePropsList = getColumns(userId);
        for (ThreadTableProps item : threadTablePropsList) {
            ThreadTableProps threadTableProps = fetchThreadTableProps(item.getUserId(), item.getColumnId());
            if (threadTableProps == null) {
                threadTableProps = getEmptyObject();
                threadTableProps = fillColumnWithDefaultValues(threadTableProps, item.getUserId(), item.getColumnId());
                threadTableProps.setNum(item.getNum());
                threadTablePropsPersistence.update(threadTableProps);
            }
        }
    }

    // сохранить настройку порядка следования столбцов табулятора
    public List<ThreadTableProps> saveColumnsOrder(String columnsOrder, long userId) throws PortalException {
        String columnsNewOrder[] = StringUtil.split(columnsOrder, StringPool.COMMA);
        String columnsDefaultOrder[] = ThreadsMonitorConst.getColumns();

        if (columnsDefaultOrder.length != columnsNewOrder.length) throw new WrongColumnsOrderException();
        for (String defaultColumn : columnsDefaultOrder) {
            boolean contains = Arrays.stream(columnsNewOrder).anyMatch(defaultColumn::equalsIgnoreCase);
            if (!contains) throw new WrongColumnsOrderException();
        }

        saveDefaultColumnParamsForAbsentColumns(userId);

        int num = 0;
        List<ThreadTableProps> threadTablePropsList = new ArrayList<>();
        for (String columnId : columnsNewOrder) {
            num++;
            ThreadTableProps threadTableProps = getThreadTableProps(userId, columnId);
            threadTableProps.setNum(num);
            threadTableProps = threadTablePropsPersistence.update(threadTableProps);
            threadTablePropsList.add(threadTableProps);
        }
        return threadTablePropsList;
    }

}