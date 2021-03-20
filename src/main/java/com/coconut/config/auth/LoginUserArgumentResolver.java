package com.coconut.config.auth;

import com.coconut.config.auth.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Component
// LoginUserArgumentResolver가 스프링에서 인식될 수 있도록 WebMvcConfigurer 에 추가한다.
// WebConfig 클래스를 생성하여 추가한다.
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final HttpSession httpSession;


    // supportsParameter()
    // 컨트롤러 메소드의 특정 파라미터를 지원하는지 판단한다.
    // parameter에 @LoginUser 어노테이션이 붙어있고, 파라미터 클래스 타입이 SessionUser.class 인 경우만 true를 반환한다.
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUser.class) != null;

        boolean isUserClass = SessionUser.class.equals(parameter.getParameterType());

        return isUserClass && isLoginUserAnnotation;
    }


    // resolveArgument()
    // 파라미터에 전달할 객체를 생성한다.
    // 여기서는 세션에서 객체를 가져온다.
    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        return httpSession.getAttribute("user");
    }
}
