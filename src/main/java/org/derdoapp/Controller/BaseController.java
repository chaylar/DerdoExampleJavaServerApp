package org.derdoapp.Controller;

import org.derdoapp.DataModel.AppUser;
import org.derdoapp.DataModel.AppUserBase;
import org.derdoapp.DataModel.RequestParams;
import org.derdoapp.Repository.AppUserMatchPotRepository;
import org.derdoapp.Repository.AppUserRepository;
import org.derdoapp.ServerConfig.ConfigAuth;
import org.derdoapp.VO.ServiceResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

//TODO : is RestController required
public class BaseController {

    @Autowired
    protected AppUserRepository appUserRepository;

    @Autowired
    protected AppUserMatchPotRepository matchPotRepository;

    @Autowired
    protected ConfigAuth authConfig;

    protected String getClientIp() {
        String result = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return null;
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        if (request != null) {
            result = request.getHeader("X-FORWARDED-FOR");
            if (result == null || "".equals(result)) {
                result = request.getRemoteAddr();
            }
        }

        return result;
    }

    protected AppUserBase getRequestUserBase() {

        //System.out.println("getRequestUser.INIT");
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String token = null;
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return null;
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        token = request.getHeader(RequestParams.REQUEST_TOKEN_KEY);
        if(token == null || token.isEmpty()) {
            return null;
        }

        AppUserBase result = appUserRepository.findBaseByAccessToken(token);

        return result;
    }

    protected String getRemoteAddr() {
        System.out.println("getRemoteAddr.INIT");
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String remoteAddr = null;
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return null;
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        if (request != null) {
            remoteAddr = request.getHeader("Remote_Addr");
            if (StringUtils.isEmpty(remoteAddr)) {
                remoteAddr = request.getHeader("X-FORWARDED-FOR");
                if (remoteAddr == null || "".equals(remoteAddr)) {
                    remoteAddr = request.getRemoteAddr();
                }
            }
        }

        return remoteAddr;
    }

    protected AppUser getRequestUser() {

        System.out.println("getRequestUser.INIT");
        AppUser requestUser = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String token = null;
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return null;
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        token = request.getHeader(RequestParams.REQUEST_TOKEN_KEY);
        if(token == null || token.isEmpty()) {
            return null;
        }

        requestUser = appUserRepository.findByAccessToken(token);

        return requestUser;
    }

    protected ServiceResponseVO SuccessResult(Object dataObject) {

        ServiceResponseVO result = new ServiceResponseVO();
        result.success = true;
        result.data = dataObject;

        return result;
    }

    protected ServiceResponseVO FailResult() {

        ServiceResponseVO result = new ServiceResponseVO();
        result.success = false;

        return result;
    }

    protected ServiceResponseVO SuccessResult() {

        ServiceResponseVO result = new ServiceResponseVO();
        result.success = true;
        result.data = true;

        return result;
    }

}
