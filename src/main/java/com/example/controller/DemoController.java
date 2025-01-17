package com.example.controller;
import com.example.model.Demo;
import com.example.service.DemoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
public class DemoController {
    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @PostMapping
    public ResponseEntity<Void> createDemo(@RequestBody Demo demo) throws Exception {
        demoService.createDemo(demo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Demo> getDemo(@PathVariable String id) {
        Demo demo = demoService.getDemo(id);
        if (demo != null) {
            return ResponseEntity.ok(demo);
        }
        return ResponseEntity.notFound().build();
    }
}