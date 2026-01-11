package com.net.eventservice.clients;

import com.net.eventservice.models.User;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("USER-SERVICE")
public interface UserRestClient {
    @GetMapping("/api/users/{id}")
    @CircuitBreaker(name = "userService",fallbackMethod = "getDefaultUser")
    User findUserById(@PathVariable Long id);

    default User getDefaultUser(Long id,Exception exception){
        User user=new User();
        user.setUserId(id);
        user.setUsername("Not Available");
        user.setEmail("Not Available");
        user.setPassword("Not Available");
        return user;
    }
}
