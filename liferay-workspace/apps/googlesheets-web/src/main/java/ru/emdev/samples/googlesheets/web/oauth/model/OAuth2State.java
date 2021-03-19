package ru.emdev.samples.googlesheets.web.oauth.model;

import java.io.Serializable;

public class OAuth2State implements Serializable {

    private static final long serialVersionUID = -2827514047041322854L;

    private final String failureURL;
    private final String successURL;
    private final String state;

    private final long layoutId;
    private final String portletId;
    private final long userId;

    public OAuth2State(long plid, String portletId, long userId, String successURL, String failureURL, String state) {
        this.layoutId = plid;
        this.portletId = portletId;
        this.userId = userId;
        this.successURL = successURL;
        this.failureURL = failureURL;
        this.state = state;
    }

    public String getFailureURL() {
        return failureURL;
    }

    public String getSuccessURL() {
        return successURL;
    }

    public long getLayoutId() {
        return layoutId;
    }

    public String getPortletId() {
        return portletId;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isValid(String state) {
        return this.state.equals(state);
    }

    @Override
    public String toString() {
        return "OAuth2State{" +
                "failureURL='" + failureURL + '\'' +
                ", successURL='" + successURL + '\'' +
                ", state='" + state + '\'' +
                ", layoutId=" + layoutId +
                ", portletId='" + portletId + '\'' +
                ", userId=" + userId +
                '}';
    }
}
