package org.wilsonks.slotauthservice.service;


import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.wilsonks.slotauthservice.domain.Employee;
import org.wilsonks.slotauthservice.domain.EmployeeRole;
import org.wilsonks.slotauthservice.dto.employee.EmployeeCreateRequest;
import org.wilsonks.slotauthservice.dto.employee.EmployeeResponse;
import org.wilsonks.slotauthservice.dto.employee.EmployeeUpdateRequest;
import org.wilsonks.slotauthservice.dto.employee.EmployeeLoginResponse;
import org.wilsonks.slotauthservice.exception.ConflictException;
import org.wilsonks.slotauthservice.exception.InvalidPinException;
import org.wilsonks.slotauthservice.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class EmployeeService {

    public static final long EMPLOYEE_TOKEN_EXPIRATION = 8 * 60 * 60 * 1000; // 8 hours in milliseconds

    private final EmployeeRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    @PostConstruct
    public void init() {
        // Initialize the service, if needed
        repository.save(new Employee(null, "a3c515e1", "CDAD100001", encoder.encode("1234"), EmployeeRole.Admin, true, null, null, null));
        repository.save(new Employee(null, "73feb4df", "CDMA100001", encoder.encode("1234"), EmployeeRole.Manager, true,  null, null, null));
        repository.save(new Employee(null, "431d73e1", "CDA1000001", encoder.encode("1234"), EmployeeRole.Attendant, true, null, null, null));
        repository.save(new Employee(null, "73b8d008", "CDA1000002", encoder.encode("1234"), EmployeeRole.Attendant, true, null,null, null));
        log.info("✅ EmployeeService initialized with default employees");
        repository.findAll()
                .stream()
                .map(Employee::getUid)
                .forEach(uid -> log.info("✅ Employee Account: {}", uid));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public EmployeeResponse create(EmployeeCreateRequest request) {

        if(repository.existsByAccount(request.account())) {
            throw new ConflictException("Employee with Account " + request.account() + " already exists.");
        }

        Employee employee = new Employee();
        employee.setUid(request.uid());
        employee.setAccount(request.account());
        employee.setPin(encoder.encode(request.pin()));
        employee.setRole(EmployeeRole.valueOf(request.role()));
        Employee savedEmployee = repository.save(employee);

        log.info("✅ Employee created: {}", savedEmployee.getAccount());
        return EmployeeResponse.fromEntity(savedEmployee);

    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public EmployeeResponse update(String uid, EmployeeUpdateRequest request) {
        Employee employee = repository.findByUidForUpdate(uid)
                .orElseThrow(() -> new ConflictException("Employee with uid " + uid + " does not exist."));

        employee.setAccount(request.account());
        employee.setPin(encoder.encode(request.pin()));
        employee.setRole(request.role());

        log.info("✅ Employee updated: {}", employee.getAccount());
        return EmployeeResponse.fromEntity(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll() {
        return repository
                .findAll()
                .stream()
                .map(EmployeeResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findByAccount(String account) {
        Employee employee = repository.findByAccount(account)
                .orElseThrow(() -> new ConflictException("Employee with Account " + account + " does not exist."));
        return EmployeeResponse.fromEntity(employee);
    }

    @Transactional
    public void delete(String uid) {
        Employee employee = repository.findByUidForUpdate(uid)
                .orElseThrow(() -> new ConflictException("Employee with uid " + uid + " does not exist."));
        repository.delete(employee);
        log.info("✅ Employee deleted: {}", employee.getAccount());
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public EmployeeLoginResponse login(String account, String pin) {
        Optional<Employee> optionalEmployee = repository.findByAccountIdForUpdate(account);
        if(optionalEmployee.isEmpty()) {
            throw new InvalidPinException("Invalid Account or PIN for employee login.");
        }

        Employee employee = optionalEmployee.get();
        if(!employee.isActive()) {
            throw new ConflictException("Employee with Account " + account + " is deactivated.");
        }

        if(!encoder.matches(pin, employee.getPin())) {
            throw new InvalidPinException("Invalid Account or PIN for employee login.");
        }

        String token = jwtService.generateEmployeeToken(employee.getAccount(), employee.getRole().name(), EMPLOYEE_TOKEN_EXPIRATION); // 8 hours in milliseconds
        log.info("✅ Employee logged in: {} token {}", employee.getAccount(), token);

        return new EmployeeLoginResponse(token, employee.getAccount(), employee.getRole().name());
    }


//    @Transactional
//    public void deactivate(String account) {
//        Employee employee = repository.findByAccountIdForUpdate(account)
//                .orElseThrow(() -> new ConflictException("Employee with Account " + account + " does not exist."));
//        employee.setActive(false);
//        log.info("✅ Employee deactivated: {}", employee.getAccount());
//    }
//
//    @Transactional(readOnly = true)
//    public void activate(String account) {
//        Employee employee = repository.findByAccountIdForUpdate(account)
//                .orElseThrow(() -> new ConflictException("Employee with Account " + account + " does not exist."));
//        employee.setActive(true);
//        log.info("✅ Employee activated: {}", employee.getAccount());
//    }
}
