package org.derdoapp.Interceptor;

import org.derdoapp.DataModel.RequestParams;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class BaseControllerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(request.getHeader(RequestParams.REQUEST_TOKEN_KEY) == null) {
            System.out.println("BaseControllerInterceptor.MissingServletRequestParameterException");
            throw new MissingServletRequestParameterException(RequestParams.REQUEST_TOKEN_KEY, String.class.getTypeName());
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        //TODO : LOGOUT & LOGIN İŞLEMİNDE HATA ÇIKARIR!!!
        //NOTE : RENEW ACCESS TOKEN
        //String userAccessToken = TokenGenerator.GenerateAccessToken();
        //appUserRepository.setNewAccessToken(appUserBase.id, userAccessToken);
        //response.addHeader(RequestParams.REQUEST_TOKEN_KEY, userAccessToken);

        //System.out.println("Post Handle method is Calling");
    }

    @Override
    public void afterCompletion (HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {
        //System.out.println("Request and Response is completed");
    }

}
