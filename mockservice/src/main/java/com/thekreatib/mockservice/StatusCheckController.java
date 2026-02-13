package com.thekreatib.mockservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/response")
public class StatusCheckController {
    @GetMapping("/200")
    ResponseEntity<String> response200String(){
        return ResponseEntity.ok().body("200");
    }

    @GetMapping("/400")
    ResponseEntity<String> response400String(){
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/500")
    ResponseEntity<String> response500String(){
        return ResponseEntity.internalServerError().build();
    }
}
