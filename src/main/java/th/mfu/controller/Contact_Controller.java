package th.mfu.controller;

import javax.servlet.http.*;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class Contact_Controller {
    @GetMapping("/contact")
    public String ContactPage(Model model, HttpServletResponse response, HttpServletRequest request) {
        return "[GLOBAL] Contact";
    }
}