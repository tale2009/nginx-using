package kendo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NginxController {
    @RequestMapping("/nginx")
    public String nginx(){
        return "welcome nginxTest";
    }
}
