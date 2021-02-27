package ru.snowreplicator.threads_monitor.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import com.liferay.portal.kernel.service.ServiceContext;

import ru.snowreplicator.threads_monitor.model.ThreadTableSettings;
import ru.snowreplicator.threads_monitor.service.base.ThreadTableSettingsServiceBaseImpl;
import ru.snowreplicator.threads_monitor.util.SessionUtil;

public class ThreadTableSettingsServiceImpl extends ThreadTableSettingsServiceBaseImpl {

    private static Log _log = LogFactoryUtil.getLog(ThreadTableSettingsServiceImpl.class);

    // сохранить настройку сортировки столбца табулятора
    public ThreadTableSettings savePageSize(int pageSize, ServiceContext serviceContext) throws PortalException {
        SessionUtil.setSessionThreadLocal(serviceContext);
        ThreadTableSettings threadTableSettings = threadTableSettingsLocalService.savePageSize(pageSize, getUserId());
        return threadTableSettings;
    }

    // сохранить настройку выбора группировки
    public ThreadTableSettings saveColumnGrouping(String  groupColumn, ServiceContext serviceContext) throws PortalException {
        SessionUtil.setSessionThreadLocal(serviceContext);
        ThreadTableSettings threadTableSettings = threadTableSettingsLocalService.saveColumnGrouping(groupColumn, getUserId());
        return threadTableSettings;
    }

}