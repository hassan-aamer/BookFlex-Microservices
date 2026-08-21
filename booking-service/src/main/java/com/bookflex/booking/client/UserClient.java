package com.bookflex.booking.client;

import com.bookflex.common.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * OpenFeign client for communicating with user-service.
 */
@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/{id}/dto")
    UserDto getUserById(@PathVariable("id") String userId);
}
