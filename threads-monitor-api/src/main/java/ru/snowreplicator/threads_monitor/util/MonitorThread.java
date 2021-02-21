package ru.snowreplicator.threads_monitor.util;

import java.util.Map;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class MonitorThread extends Thread {

    private static Log _log = LogFactoryUtil.getLog(MonitorThread.class);

    // название потока мониторинга
    private final static String MONITOR_THREAD_NAME = "THREADS_MONITOR_PROCESS";
    // време между сканированием процессов заданное по умолчанию
    private final static long DEFAULT_THREAD_SLEEP_TIME = 3000;

    // признак для остановки потока
    private volatile boolean stopThread;
    // време между сканированием процессов
    private long threadSleepTime;

    public MonitorThread() {
        this.stopThread = false;
        this.threadSleepTime = DEFAULT_THREAD_SLEEP_TIME;
    }

    // получить имя потока мониторинга
    public static String getMonitorThreadName() {
        return MONITOR_THREAD_NAME;
    }

    // создание и запуск потока
    public static MonitorThread runMonitorThread() {
        MonitorThread monitorThread = new MonitorThread();
        monitorThread.setName(MONITOR_THREAD_NAME);
        monitorThread.start();
        return monitorThread;
    }

    // остановка потока
    public void stopThread() {
        stopThread = true;
    }

    @Override
    public void run() {
        try {
            _log.info("--- running monitor thread ---");
            while (!stopThread) {
                monitorThreadFunc();
                Thread.sleep(threadSleepTime);
            }
            _log.info("--- monitor thread is stopped ---");
        } catch (Exception e) {
            _log.error("Thread run exception: " + e.toString());
            if (_log.isErrorEnabled()) _log.error(e, e);
        }
    }

    // код выполняемый в потоке
    private void monitorThreadFunc() throws InterruptedException {
        _log.info("monitorThreadFunc tick");

        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        for (Thread thread : threads.keySet()) {
            //_log.info("id: " + thread.getId() + "   priority: " + thread.getPriority() + "   state: " + thread.getState() + "   name: " + thread.getName());

        }
    }

}
