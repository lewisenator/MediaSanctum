package com.media_sanctum.backend.config;

import com.media_sanctum.backend.entity.User;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Data
public class RequestContext {
    private User user;
}
