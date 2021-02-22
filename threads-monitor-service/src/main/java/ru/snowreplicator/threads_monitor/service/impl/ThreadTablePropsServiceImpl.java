package ru.snowreplicator.threads_monitor.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;

import ru.snowreplicator.threads_monitor.model.ThreadTableProps;
import ru.snowreplicator.threads_monitor.service.base.ThreadTablePropsServiceBaseImpl;
import ru.snowreplicator.threads_monitor.util.SessionUtil;

public class ThreadTablePropsServiceImpl extends ThreadTablePropsServiceBaseImpl {

    private static Log _log = LogFactoryUtil.getLog(ThreadTablePropsServiceImpl.class);

    // сохранить настройку сортировки столбца табулятора
    public ThreadTableProps saveColumnSortDir(String columnId, String dir, ServiceContext serviceContext) throws PortalException {
        SessionUtil.setSessionThreadLocal(serviceContext);
        ThreadTableProps threadTableProps = threadTablePropsLocalService.saveColumnSortDir(columnId, dir, getUserId());
        return threadTableProps;
    }

    // сохранить настройку ширины столбца табулятора
    public ThreadTableProps saveColumnWidth(String columnId, int width, ServiceContext serviceContext) throws PortalException {
        SessionUtil.setSessionThreadLocal(serviceContext);
        ThreadTableProps threadTableProps = threadTablePropsLocalService.saveColumnWidth(columnId, width, getUserId());
        return threadTableProps;
    }

}