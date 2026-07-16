package org.wilsonks.slotauthservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wilsonks.slotauthservice.dto.EmployeeCreateRequest;
import org.wilsonks.slotauthservice.dto.EmployeeResponse;
import org.wilsonks.slotauthservice.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody @Valid EmployeeCreateRequest request) {
        EmployeeResponse response = employeeService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED) //201 Created
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> findAllEmployees() {
        List<EmployeeResponse> employees = employeeService.findAll();

        return ResponseEntity
                .status(HttpStatus.OK) //200 OK
                .body(employees);
    }

    @GetMapping("/{account}")
    public ResponseEntity<EmployeeResponse> findEmployeeByAccount(@PathVariable String account) {
        EmployeeResponse employee = employeeService.findByAccount(account);

        return ResponseEntity
                .status(HttpStatus.OK) //200 OK
                .body(employee);
    }

}
