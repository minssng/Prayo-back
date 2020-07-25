package com.example.parayo.config

import com.example.parayo.interceptor.TokenValidationInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration // 이 클래스가 스프링에서 사용하는 설정을 담은 빈 클래스라는 것을 나타냄. 이 애노테이션을 사용하면 스프링이 SpringBootApplication 이하의 패키지에서 모든 설정 클래스들을 검사해 자동으로 빈을 생성
class WebConfig @Autowired constructor(
    private val tokenValidationInterceptor: TokenValidationInterceptor
) : WebMvcConfigurer { // 스프링 MVC 프로젝트에서 네이티브 코드 베이스로 설정을 입력할 수 있게 해주는 여러 가지의 콜백 함수들이 정의된 인터페이스.

    // WebMvcConfigurer의 addInterceptors()는 인터셉터들을 추가할 수 있는 Interceptor Registry 객체를 파라미터로 전달. 이 객체의 addInterceptor() 함수를 이용해 앞서 만든 TokenValidationInterceptor를 추가하고 체인 함수로 제공되는 addPathPatterns() 함수로 /api/ 이하의 URI에서 인터셉터가 동작하도록 설정해줌.
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(tokenValidationInterceptor)
            .addPathPatterns("/api/**")
    }

    override fun addResourceHandlers(
        registry: ResourceHandlerRegistry // addResourceHandlers() 콜백에 파라미터로 전달되는 ResourceHandlerRegistry의 addResourceHandler() 함수를 이용해 /images 하위의 모든 URI에 대해 로컬 파일 시스템의 /parayo/images ㄷ렉토리를 참조하도록 설정함
    ) {
        registry.addResourceHandler("/images/**")
            .addResourceLocations("file:///parayo/images/")
    }
}
