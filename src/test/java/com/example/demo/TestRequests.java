package com.example.demo;

import com.example.demo.seguridad.ProteccionCsrf;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
final class TestRequests {
    private TestRequests() { }
    static MockHttpServletRequestBuilder post(String url,Object... variables){
        return MockMvcRequestBuilders.post(url,variables).with(request->{
            String token=(String)request.getSession().getAttribute(ProteccionCsrf.ATRIBUTO);
            if(token==null){token=java.util.UUID.randomUUID().toString();request.getSession().setAttribute(ProteccionCsrf.ATRIBUTO,token);}
            request.setParameter("_csrf",token);return request;
        });
    }
}
