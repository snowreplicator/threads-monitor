package ru.snowreplicator.threads_monitor.comparator;

import com.liferay.portal.kernel.util.OrderByComparator;

import ru.snowreplicator.threads_monitor.model.ThreadTableProps;

public class ThreadTablePropsComparator extends OrderByComparator<ThreadTableProps> {
    public final static int SORT_MODE_BY_NUM = 1;

    private int sortMode;

    public ThreadTablePropsComparator(int sortMode) {
        this.sortMode = sortMode;
    }

    @Override
    public int compare(ThreadTableProps item1, ThreadTableProps item2) {
        int result = 0;

        switch (sortMode) {
            case SORT_MODE_BY_NUM:
                int num1 = item1.getNum();
                int num2 = item2.getNum();
                result = (num1 < num2) ? -1 : 1;
                break;
        }
        return result;
    }

}