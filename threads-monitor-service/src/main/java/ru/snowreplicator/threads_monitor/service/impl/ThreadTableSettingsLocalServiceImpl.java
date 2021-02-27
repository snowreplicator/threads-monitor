package ru.snowreplicator.threads_monitor.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ru.snowreplicator.threads_monitor.constants.ThreadsMonitorConst;
import ru.snowreplicator.threads_monitor.exception.WrongGroupColumnIdException;
import ru.snowreplicator.threads_monitor.exception.WrongPageSizeException;
import ru.snowreplicator.threads_monitor.model.ThreadTableSettings;
import ru.snowreplicator.threads_monitor.service.base.ThreadTableSettingsLocalServiceBaseImpl;

public class ThreadTableSettingsLocalServiceImpl extends ThreadTableSettingsLocalServiceBaseImpl {

    private static Log _log = LogFactoryUtil.getLog(ThreadTableSettingsLocalServiceImpl.class);

    // получить пустой объект
    public ThreadTableSettings getEmptyObject() {
        ThreadTableSettings threadTableSettings = threadTableSettingsPersistence.create(0);
        return threadTableSettings;
    }

    // сохранить настройку сортировки столбца табулятора
    public ThreadTableSettings savePageSize(int pageSize, long userId) throws PortalException {
        if (pageSize < ThreadsMonitorConst.MIN_PAGE_SIZE) throw new WrongPageSizeException();

        ThreadTableSettings threadTableSettings = fetchThreadTableSettings(userId);
        if (threadTableSettings == null) {
            threadTableSettings = getEmptyObject();
            threadTableSettings.setUserId(userId);
        }

        if (threadTableSettings.getPageSize() != pageSize) {
            threadTableSettings.setPageSize(pageSize);
            threadTableSettings = threadTableSettingsPersistence.update(threadTableSettings);
        }
        return threadTableSettings;
    }

    // сохранить настройку выбора группировки
    public ThreadTableSettings saveColumnGrouping(String  groupColumn, long userId) throws PortalException {
        if (!ThreadsMonitorConst.validateGroupColumnId(groupColumn)) throw new WrongGroupColumnIdException();

        ThreadTableSettings threadTableSettings = fetchThreadTableSettings(userId);
        if (threadTableSettings == null) {
            threadTableSettings = getEmptyObject();
            threadTableSettings.setUserId(userId);
        }
        threadTableSettings.setGroupColumn(groupColumn);

        threadTableSettings = threadTableSettingsPersistence.update(threadTableSettings);
        return threadTableSettings;
    }

}