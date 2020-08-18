package ru.emdev.samples.googlesheets.web.portlet.action;

import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import ru.emdev.samples.googlesheets.web.constants.GoogleSheetsPortletKeys;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

@Component(
        immediate = true,
        property = {
                "javax.portlet.name=" + GoogleSheetsPortletKeys.GOOGLESHEETS
        },
        service = ConfigurationAction.class
)
public class GoogleSheetsPortletConfigurationAction extends DefaultConfigurationAction {

    @Override
    @Reference(
            target = "(osgi.web.symbolicname=ru.emdev.samples.googlesheets.web)",
            unbind = "-"
    )
    public void setServletContext(ServletContext servletContext) {
        super.setServletContext(servletContext);
    }

    @Override
    public String getJspPath(HttpServletRequest httpServletRequest) {
        return "/configuration.jsp";
    }

}
