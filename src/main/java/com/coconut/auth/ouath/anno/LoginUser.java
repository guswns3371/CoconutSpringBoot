package com.coconut.auth.ouath.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// @Target(ElementType.PARAMETER)
// 해당 어노테이션이 생성될 수 있는 위치를 지정한다. PARAMETER로 지정했으므로 메소드의 파라미터로 선언된 객체에서만 사용가능하다.
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
// @interface
// 이 파일을 어노테이션 클래스로 지정한다.
public @interface LoginUser {
}
