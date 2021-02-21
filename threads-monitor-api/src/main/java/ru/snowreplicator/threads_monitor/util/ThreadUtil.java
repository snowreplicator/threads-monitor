package ru.snowreplicator.threads_monitor.util;

import java.util.Map;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ThreadUtil {

    private static Log _log = LogFactoryUtil.getLog(ThreadUtil.class);

    // запуск процесса мониторинга
    public static void runMonitorProcess() {
        boolean isMonitorProcessRunning = isMonitorProcessRunning();
        _log.info("isMonitorProcessRunning = " + isMonitorProcessRunning);
        if (!isMonitorProcessRunning) {
            MonitorThread monitorThread = MonitorThread.runMonitorThread();
        }
    }

    // остановка процесса мониторинга
    public static void stopMonitorProcess() {
        MonitorThread monitorThread = getMonitorThreadProcess();
        if (monitorThread != null) {
            monitorThread.stopThread();
        }
    }

    // получить имя потока мониторинга
    private static String getThreadName() {
        String threadName = MonitorThread.getMonitorThreadName();
        return threadName;
    }

    // получить процесс мониторинга
    private static MonitorThread getMonitorThreadProcess() {
        String threadName = getThreadName();
        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        for (Thread thread : threads.keySet()) {
            if (thread.getName().equalsIgnoreCase(threadName)) {
                MonitorThread monitorThread = (MonitorThread) thread;
                return monitorThread;
            }
        }
        return null;
    }

    // проверка запущен ли процесс мониторинга
    private static boolean isMonitorProcessRunning() {
        MonitorThread monitorThread = getMonitorThreadProcess();
        return monitorThread != null;
    }

}
