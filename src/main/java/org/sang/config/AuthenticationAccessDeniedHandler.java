package org.sang.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sang.bean.RespBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by sang on 2017/12/22.
 */
public class AuthenticationAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse resp, AccessDeniedException e) throws IOException, ServletException {
        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        resp.setCharacterEncoding("UTF-8");
        RespBean respBean = RespBean.ok("权限不足,请联系管理员!");
        PrintWriter out = resp.getWriter();
        ObjectMapper om = new ObjectMapper();
        out.write(om.writeValueAsString(respBean));
        out.flush();
        out.close();
    }
}
