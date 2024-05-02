package hello.login.web.filter;

import hello.login.web.SessionConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class LoginCheckFilter implements Filter {

    private static final String[] whitelist = {"/","/member/add","/login","/logout","/css/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();

        try {
            log.info("인증 체크 필터 시작 {} ", requestURI);
            if(isLoginCheckPath(requestURI)){
                log.info("인증 체크 로직 실행 {} ", requestURI);
                HttpSession session = httpRequest.getSession();
                if(session == null || session.getAttribute(SessionConsts.LOGIN_MEMBER) == null){
                    log.info("미인증 사용자 요청 {} ", requestURI);
                    httpResponse.sendRedirect("/login?redirectURL="+requestURI);
                    return;
                }
            }
            chain.doFilter(request,response);
        } catch (Exception e){
            throw e;
        } finally {
            log.info("인증 체크 필터 종료 {} ", requestURI);
        }
    }


    /**
     * 화이트 리스트이 경우 인증 체크 안함
     */

    // 로그인을 체크해야 할 경로이다. 화이트 리스트가 아닌 것들은 로그인 체크 경로이다.
    // 함수명을 생각해보면 로그인 체크경로이다. -> 화이트 리스트가 아닌 것들이다 라는게 참이여야하니깐
    // 화이트 리스트에 포함되어 있는것을 찾는다.
    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whitelist,requestURI);
    }
}
