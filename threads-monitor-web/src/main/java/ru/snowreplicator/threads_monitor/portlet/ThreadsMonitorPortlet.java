package ru.snowreplicator.threads_monitor.portlet;

import java.io.IOException;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

import ru.snowreplicator.threads_monitor.constants.ThreadsMonitorKeys;

@Component(
        immediate = true,
        property = {
                "com.liferay.portlet.display-category=category.snowreplicator",
                "com.liferay.portlet.css-class-wrapper=threads-monitor-portlet",
                "com.liferay.portlet.header-portlet-css=/css/main.css?v=2021_02_14___4",
                "com.liferay.portlet.instanceable=false",
                "com.liferay.portlet.add-default-resource=true",
                "javax.portlet.display-name=Threads Monitor Portlet",
                "javax.portlet.init-param.template-path=/",
                "javax.portlet.init-param.view-template=/jsp/view.jsp",
                "javax.portlet.name=" + ThreadsMonitorKeys.THREADS_MONITOR_PORTLET,
                "javax.portlet.resource-bundle=content.Language",
                "javax.portlet.security-role-ref=power-user,user"
        },
        service = Portlet.class
)
public class ThreadsMonitorPortlet extends MVCPortlet {
    private static Log _log = LogFactoryUtil.getLog(ThreadsMonitorPortlet.class);

    @Activate
    @Modified
    protected void activate() {
        _log.info("ThreadsMonitorPortlet module - activating");
        //ThreadUtil.runMonitorProcess();
        _log.info("ThreadsMonitorPortlet module - activated");
    }

    @Deactivate
    @Modified
    protected void deactivate() {
        _log.info("ThreadsMonitorPortlet module - deactivating");
        //ThreadUtil.stopMonitorProcess();
        _log.info("ThreadsMonitorPortlet module - deactivated");
    }

    @Override
    public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException, PortletException {
        String resourceID = resourceRequest.getResourceID();
        _log.info("ThreadsMonitorPortlet - resourceID = " + resourceID); // !!!!! delete
        try {
            switch (resourceID) {
                case "/threads-monitor-portlet/load-threads-data":
                    loadThreadsData(resourceRequest, resourceResponse);
                    break;

                default:
                    super.serveResource(resourceRequest, resourceResponse);
            }
        } catch (Exception e) {
            JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
            jsonObject.put("exception", e.getClass().getName());
            writeJSON(resourceRequest, resourceResponse, jsonObject);

            throw new PortletException(e);
        }
    }

    private void loadThreadsData(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException {
        JSONArray threadsData = ActionUtil.getThreadsData();
        writeJSON(resourceRequest, resourceResponse, threadsData);
    }

}