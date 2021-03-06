package cn.itcast.travel.web.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BaseServlet extends HttpServlet {
        @Override
        protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            //完成方法的分发

            //1.获取请求路径
            String uri = request.getRequestURI();
            //2.获取方法名称
            String methodName = uri.substring(uri.lastIndexOf('/') + 1);


            //3.获取方法对象Method
            try {
                Method method = this.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);

                //4.执行方法
                try {
                    method.invoke(this, request, response);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

    //直接将传入的对象序列化为Json，并且写回客户端

    public void writeValue(Object obj, HttpServletResponse response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(), obj);
    }

    //将传入的对象序列化为Json，返回给调用者
    public String writeValueAsString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }


    }

