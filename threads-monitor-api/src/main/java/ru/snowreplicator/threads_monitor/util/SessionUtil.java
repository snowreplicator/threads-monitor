package ru.snowreplicator.threads_monitor.util;

import javax.servlet.http.HttpSession;

import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.PortalSessionContext;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.util.CookieKeys;

public class SessionUtil {

    // записать сессию из ServiceContext в сессию текущего потока (если её нет)
    public static void setSessionThreadLocal(ServiceContext serviceContext) {
        HttpSession session = PortalSessionThreadLocal.getHttpSession();
        if (session == null && serviceContext != null) {
            String sessionId = CookieKeys.getCookie(serviceContext.getRequest(), CookieKeys.JSESSIONID);
            if (sessionId != null) {
                session = PortalSessionContext.get(sessionId);
                if (session != null) {
                    PortalSessionThreadLocal.setHttpSession(session);
                }
            }
        }
    }

}