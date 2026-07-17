package org.wilsonks.slotauthservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wilsonks.slotauthservice.dto.employee.*;
import org.wilsonks.slotauthservice.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // C- Create a new employee
    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody @Valid EmployeeCreateRequest request) {
        EmployeeResponse response = employeeService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED) //201 Created
                .body(response);
    }

    // R- Read all employees
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> findAllEmployees() {
        List<EmployeeResponse> employees = employeeService.findAll();

        return ResponseEntity
                .status(HttpStatus.OK) //200 OK
                .body(employees);
    }

    //R- Read employee by account
    @GetMapping("/{account}")
    public ResponseEntity<EmployeeResponse> findEmployeeByAccount(@PathVariable String account) {
        EmployeeResponse employee = employeeService.findByAccount(account);

        return ResponseEntity
                .status(HttpStatus.OK) //200 OK
                .body(employee);
    }

    //U- Update employee by account
    @PutMapping("/{account}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable String account, @RequestBody @Valid EmployeeUpdateRequest request) {
        EmployeeResponse updatedEmployee = employeeService.update(account, request);

        return ResponseEntity
                .status(HttpStatus.OK) //200 OK
                .body(updatedEmployee);
    }

    //D- Delete employee by account
    @DeleteMapping("/{account}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String account) {
        employeeService.delete(account);
        return ResponseEntity.noContent().build(); //204 No Content
    }

    @PostMapping("/login")
    public ResponseEntity<EmployeeLoginResponse> login(@RequestBody @Valid EmployeeLoginRequest request) {
        EmployeeLoginResponse response = employeeService.login(request.account(), request.pin());
        return ResponseEntity.ok(response);
    }

}
