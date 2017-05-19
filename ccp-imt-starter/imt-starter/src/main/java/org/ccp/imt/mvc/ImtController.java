package org.ccp.imt.mvc;

import org.ccp.imt.ImtEndpoint;
import org.ccp.imt.annotation.ImtMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Collection;


/**
 * @author 逍冲 [2017年05月18日 下午3:44]
 */
@Controller
@RequestMapping("/imt")
public class ImtController {


    @Resource
    private ImtEndpoint schedulerxEndpoint;

    @GetMapping
    public Object list() {

        Collection<ImtMethod> imtMethods = schedulerxEndpoint.getImtMethod().values();

        return new ModelAndView("imt/list", "messages", imtMethods);
    }

}
