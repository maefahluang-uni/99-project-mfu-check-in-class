package th.mfu.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import th.mfu.model.*;
import th.mfu.service.*;
import th.mfu.repository.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StreamUtils;
import org.springframework.core.io.Resource;

@Controller
public class G_Controller {
    @Autowired
    private UserService userService;

    @Autowired
    private StudentRepository StudentRepo;

    @GetMapping("/login")
    public String Login(Model model, HttpServletRequest request) {
        return "Login";
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(Model model, HttpServletResponse response, @RequestParam String username, @RequestParam String password) {
        if (userService.Authenticate(username, password)) {
            // User authenticated successfully, redirect to the home page
            String jwtToken = userService.GenerateJwtToken(username);
            // System.out.println(jwtToken);
            Cookie cookie = new Cookie("accessToken", jwtToken);
            cookie.setMaxAge(60 * 15); // unit: seconds
            cookie.setPath("/");
            response.addCookie(cookie);
            return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/home")
                .body(null);
        } else {
            // Authentication failed, show an error message
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Authentication failed");
        }
    }
    
    @GetMapping("/home")
    public String Home() {
        return "Home";
    }

    // @GetMapping("/test")
    // public ResponseEntity<String> A(Model model) {
    //     return ResponseEntity.ok("This is plain text.");
    // }

    // @GetMapping("/home")
    // public String HomePage() {
    //     return "Home";
    // }
    // @PostMapping("/login")
    // public String LoginAuthentication(Model model) {
    //     return "Login";
    // }

    // @GetMapping("/login")
    // public void loginPage(HttpServletResponse response) {
    //     try {
    //         // Read the content of the three files and combine them
    //         String file1Content = readResource("templates/partials/head.ejs");
    //         String file2Content = readResource("templates/partials/Login.html");
    //         String file3Content = readResource("templates/Footer.html");
    //         String combinedHtml = file1Content + file2Content + file3Content;
    //         response.setContentType("text/html; charset=UTF-8"); // Set the character encoding
    //         try (OutputStream outputStream = response.getOutputStream()) {
    //             outputStream.write(combinedHtml.getBytes(StandardCharsets.UTF_8));
    //         }
    //     } catch (IOException e) {
    //         // Handle the exception gracefully, e.g., log it and return an error response.
    //         e.printStackTrace();
    //         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    //     }
    // }

    // private String readResource(String filePath) throws IOException {
    //     ClassPathResource resource = new ClassPathResource(filePath);
    //     try (InputStream inputStream = resource.getInputStream()) {
    //         return StreamUtils.copyToString(inputStream, Charset.defaultCharset());
    //     }
    // }


    @GetMapping("/images/{imageName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        Resource resource = new ClassPathResource("images/" + imageName);
        if (resource.exists()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/css/{fileName}.css")
    public void CSS(@PathVariable String fileName, HttpServletResponse response) {
        String filePath = ("/css/" + fileName + ".css"); // start at resources
        response.setContentType("text/css");
        try (InputStream inputStream = getClass().getResourceAsStream(filePath);
            OutputStream outputStream = response.getOutputStream()) {
            if (inputStream != null) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // @GetMapping("/concerts")
    // public String listConcerts(Model model) {
    //     model.addAttribute("concerts", repository.findAll());
    //     return "list-concert";
    // }

    // @GetMapping("/add-concert")
    // public String addAConcertForm(Model model) {
    //     model.addAttribute("concert", new Concert());
    //     return "add-concert-form";
    // }

    // @PostMapping("/concerts")
    // public String saveConcert(@ModelAttribute Concert concert) {
    //     repository.save(concert);
    //     return "redirect:/concerts";
    // }

    // @GetMapping("/concerts/{id}")
    // public String getConcert(Model model, @PathVariable Long id) {
    //     Concert concert = repository.findById(id).get();
    //     model.addAttribute("concert", concert);
    //     return "add-concert-form";
    // }

    // @GetMapping("/delete-concert/{id}")
    // public String deleteConcert(@PathVariable long id) {
    //     repository.deleteById(id);
    //     return "redirect:/concerts";
    // }

    // @GetMapping("/delete-concert")
    // public String removeAllConcerts() {
    //     repository.deleteAll();
    //     return "redirect:/concerts";
    // }
}
