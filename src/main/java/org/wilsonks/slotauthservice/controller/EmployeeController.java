package org.wilsonks.slotauthservice.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wilsonks.slotauthservice.dto.employee.*;
import org.wilsonks.slotauthservice.service.EmployeeService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // C- Create a new employee
    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody @Valid EmployeeCreateRequest request) {
        EmployeeResponse response = employeeService.create(request);


        log.info("Employee created successfully: {}", response);
        return ResponseEntity
                .status(HttpStatus.CREATED) //201 Created
                .body(response);
    }

    // R- Read all employees
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> findAllEmployees() {
        List<EmployeeResponse> employees = employeeService.findAll();

        log.info("Retrieved all employees");
        return ResponseEntity
                .status(HttpStatus.OK) //200 OK
                .body(employees);
    }

    //U- Update employee by uid
    @PutMapping("/{uid}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable String uid, @RequestBody @Valid EmployeeUpdateRequest request) {
        EmployeeResponse updatedEmployee = employeeService.update(uid, request);

        log.info("Employee updated successfully: {}", updatedEmployee);
        return ResponseEntity
                .status(HttpStatus.OK) //200 OK
                .body(updatedEmployee);
    }

    //D- Delete employee by uid
    @DeleteMapping("/{uid}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String uid) {
        employeeService.delete(uid);

        log.info("Employee deleted successfully: {}", uid);
        return ResponseEntity.noContent().build(); //204 No Content
    }

    @PostMapping("/login")
    public ResponseEntity<EmployeeLoginResponse> login(@RequestBody @Valid EmployeeLoginRequest request) {
        EmployeeLoginResponse response = employeeService.login(request.account(), request.pin());

        log.info("Employee login successful for account: {}", request.account());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/headers")
    public ResponseEntity<?> getHeaders(@RequestHeader("X-Correlation-Id") String correlationId,
                                        @RequestHeader("X-User-Id") String userId,
                                        @RequestHeader("X-User-Role") String role,
                                        @RequestHeader("X-User-Type") String type) {
        log.info("Received headers - UserId: {}, Role: {}, Type: {}", userId, role, type);
        return ResponseEntity.ok().body(
                Map.of(
                        "X-User-Id", userId,
                        "X-User-Role", role,
                        "X-User-Type", type,
                        "X-Correlation-Id", correlationId
                )
        );
    }

}
