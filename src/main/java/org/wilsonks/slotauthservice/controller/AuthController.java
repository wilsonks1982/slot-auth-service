package org.wilsonks.slotauthservice.controller;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wilsonks.slotauthservice.dto.EmployeeLoginRequest;
import org.wilsonks.slotauthservice.dto.EmployeeLoginResponse;
import org.wilsonks.slotauthservice.service.EmployeeService;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final EmployeeService employeeService;

    @PostMapping("/login")
    public ResponseEntity<EmployeeLoginResponse> login(@RequestBody @Valid EmployeeLoginRequest request) {
        EmployeeLoginResponse response = employeeService.login(request.account(), request.pin());
        return ResponseEntity.ok(response);
    }
}
