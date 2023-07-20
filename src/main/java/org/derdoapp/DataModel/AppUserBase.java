package org.derdoapp.DataModel;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Document(collection="appUser")
public class AppUserBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public String id;

    public String userName;

    public AppUserToken userAccessToken;

    public AppUserToken userAuthToken;

    public AppUserToken userAuthTokenTemp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public AppUserToken getUserAccessToken() {
        return userAccessToken;
    }

    public void setUserAccessToken(AppUserToken userAccessToken) {
        this.userAccessToken = userAccessToken;
    }

    public AppUserToken getUserAuthToken() {
        return userAuthToken;
    }

    public void setUserAuthToken(AppUserToken userAuthToken) {
        this.userAuthToken = userAuthToken;
    }

    public AppUserToken getUserAuthTokenTemp() {
        return userAuthTokenTemp;
    }

    public void setUserAuthTokenTemp(AppUserToken userAuthTokenTemp) {
        this.userAuthTokenTemp = userAuthTokenTemp;
    }
}
