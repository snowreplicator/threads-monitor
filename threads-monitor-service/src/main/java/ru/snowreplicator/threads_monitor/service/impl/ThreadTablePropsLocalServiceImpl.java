package ru.snowreplicator.threads_monitor.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;

import ru.snowreplicator.threads_monitor.comparator.ThreadTablePropsComparator;
import ru.snowreplicator.threads_monitor.constants.ThreadsMonitorConst;
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

    // получить настройки полей в разрезе пользователя
    public List<ThreadTableProps> getThreadTablePropsByUserId(long userId) {
        List<ThreadTableProps> threadTablePropsList = threadTablePropsPersistence.findByUserId(userId);
        return threadTablePropsList;
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
                threadTableProps.setUserId(userId);
                threadTableProps.setColumnId(column);
                threadTableProps.setShow(true);
                threadTableProps.setSortType(ThreadsMonitorConst.COLUMN_SORT_NONE);
                threadTableProps.setWidth(ThreadsMonitorConst.getColumnDefaultWidth(column));
                threadTableProps.setNum(0);
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

}