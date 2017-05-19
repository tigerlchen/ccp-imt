package org.ccp.imt.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ccp.imt.ImtEndpoint;
import org.ccp.imt.annotation.ImtMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author 逍冲 [2017年05月18日 下午3:44]
 */
@RestController
@RequestMapping("/imt_json")
public class ImtRestController {


    @Resource
    private ImtEndpoint imtEndpoint;

    @GetMapping
    public String imt(@ModelAttribute("id") Long id, @ModelAttribute("param") String param) {
        Map<Long, ImtMethod> imtMethodMap = imtEndpoint.getImtMethod();
        ImtMethod imtMethod = imtMethodMap.get(id);

        try {
            ObjectMapper mapper = new ObjectMapper();
            String[] myObjects = mapper.readValue(param, String[].class);
            Method method = imtMethod.getMethod();

            List<Object> params = new ArrayList<>();
            for (int i = 0; i < myObjects.length; i++) {
                try {
                    Class<?> clazz = imtMethod.getParams().get(i).getParamClass();
                    Constructor<?> cons = clazz.getConstructor(String.class);
                    if (cons != null) {
                        if (!StringUtils.isEmpty(myObjects[i])) {
                            Object object = cons.newInstance(myObjects[i]);
                            params.add(object);
                        } else {
                            params.add(null);
                        }
                    } else {
                        params.add(null);
                    }
                } catch (Exception e) {
                    params.add(null);
                }

            }

            Object result =  method.invoke(imtMethod.getBean(), params.toArray());
            return mapper.writeValueAsString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  null;
    }

}
