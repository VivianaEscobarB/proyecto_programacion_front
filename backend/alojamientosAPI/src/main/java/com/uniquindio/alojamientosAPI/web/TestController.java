package com.uniquindio.alojamientosAPI.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/public/hello")
    public String publicHello() {
        return "Hola 👋, este endpoint es PÚBLICO.";
    }

    @GetMapping("/api/private/hello")
    public String privateHello() {
        return "Hola 👤, este endpoint es PRIVADO (CLIENTE, ANFITRION o ADMINISTRADOR).";
    }

    @GetMapping("/api/admin/hello")
    public String adminHello() {
        return "Hola 👑, este endpoint es solo para ADMINISTRADOR.";
    }
}
