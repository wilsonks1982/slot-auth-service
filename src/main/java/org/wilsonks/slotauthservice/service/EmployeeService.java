package org.wilsonks.slotauthservice.service;


import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.wilsonks.slotauthservice.config.JwtProperties;
import org.wilsonks.slotauthservice.domain.Employee;
import org.wilsonks.slotauthservice.domain.EmployeeRole;
import org.wilsonks.slotauthservice.dto.employee.EmployeeCreateRequest;
import org.wilsonks.slotauthservice.dto.employee.EmployeeResponse;
import org.wilsonks.slotauthservice.dto.employee.EmployeeUpdateRequest;
import org.wilsonks.slotauthservice.dto.employee.EmployeeLoginResponse;
import org.wilsonks.slotauthservice.exception.ConflictException;
import org.wilsonks.slotauthservice.exception.InvalidPinException;
import org.wilsonks.slotauthservice.repository.EmployeeRepository;
import org.wilsonks.slotauthservice.seed.SeedLoader;
import org.wilsonks.slotauthservice.seed.employee.SeedEmployeeDocument;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class EmployeeService {

    private final EmployeeRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final SeedLoader seedLoader;

    @PostConstruct
    public void init() {
        if (repository.count() == 0) {
            log.info("No employees found in the database. Seeding initial employee data...");

           try {
               SeedEmployeeDocument seedEmployeeDocument = seedLoader.loadSeedEmployeesDocument();

               if(seedEmployeeDocument != null && seedEmployeeDocument.getEmployees() != null) {
                   List<Employee> employees = seedEmployeeDocument.getEmployees().stream()
                           .map(seedEmployee -> {
                               Employee employee = new Employee();
                               employee.setUid(seedEmployee.getUid());
                               employee.setRole(EmployeeRole.valueOf(seedEmployee.getRole()));
                               employee.setAccount(seedEmployee.getAccount());
                               employee.setPin(encoder.encode("1234")); // Default PIN for seeded employees
                               employee.setActive(true);
                               return employee;
                           })
                           .toList();
                   if (!employees.isEmpty()) {
                       repository.saveAll(employees);
                       log.info("✅ EmployeeService initialized with {} default employees", employees.size());
                   } else {
                       log.warn("No employees found in the seed document. Check the seed file for employee data.");
                   }
               } else {
                   log.warn("Seed document is null or does not contain any employees. Check the seed file for employee data.");
               }

           } catch (Exception e) {
               log.error("Error occurred while seeding employee data: {}", e.getMessage(), e);
           }

        }

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
            log.error("❌ Login failed for account {}: Account not found", account);
            throw new InvalidPinException("Invalid Account or PIN for employee login.");
        }

        Employee employee = optionalEmployee.get();
        if(!employee.isActive()) {
            log.error("❌ Login failed for account {}: Account is inactive", account);
            throw new ConflictException("Employee with account " + account + " is deactivated.");
        }

        if(!encoder.matches(pin, employee.getPin())) {
            log.error("❌ Login failed for account {}: Invalid PIN", account);
            throw new InvalidPinException("Invalid Account or PIN for employee login.");
        }

        String token = jwtService.generateEmployeeToken(employee.getUid(), employee.getRole().name()); // 8 hours in milliseconds
        log.info("✅ Employee logged in: {}", employee.getUid());

        return new EmployeeLoginResponse(
                token,
                employee.getUid(),
                employee.getAccount(),
                employee.getRole().name(),
                jwtProperties.getEmployeeSessionExpiration(),
                employee.getLastReset(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
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
