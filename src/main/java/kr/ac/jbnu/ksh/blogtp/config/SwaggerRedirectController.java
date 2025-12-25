package kr.ac.jbnu.ksh.blogtp.config;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Hidden
@Controller
public class SwaggerRedirectController {

    @GetMapping({"/swagger-ui", "/swagger-ui/"})
    public String swaggerUi() {
        return "redirect:/swagger-ui/index.html";
    }
}
