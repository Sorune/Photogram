package com.sorune.photogram.Photos.Controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@Log4j2
public class PhotoController {

    @GetMapping({"/","/home"})
    public String photos(){
        log.info("Welecome!");
        return "photos/photos";
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }

}
