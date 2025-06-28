package com.vividverse.apigateway;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.gateway.routes[0].id=user_service_route",
    "spring.cloud.gateway.routes[0].uri=http://localhost:8081",
    "spring.cloud.gateway.routes[0].predicates[0]=Path=/users/**",
    "spring.cloud.gateway.routes[1].id=post_service_route",
    "spring.cloud.gateway.routes[1].uri=http://localhost:8082",
    "spring.cloud.gateway.routes[1].predicates[0]=Path=/posts/**",
    "spring.cloud.gateway.routes[2].id=comment_service_route",
    "spring.cloud.gateway.routes[2].uri=http://localhost:8083",
    "spring.cloud.gateway.routes[2].predicates[0]=Path=/comments/**"
})
class VividVerseApiGatewayApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring context loads successfully
        assertTrue(true);
    }

    @Test
    void testRouteLocatorBeanExists(ApplicationContext context) {
        // Test that the RouteLocator bean is created
        assertTrue(context.containsBean("customRouteLocator"));
        RouteLocator routeLocator = context.getBean(RouteLocator.class);
        assertNotNull(routeLocator);
    }

    @Test
    void testCorsConfigurationExists(ApplicationContext context) {
        // Test that the CORS configuration bean is created
        assertTrue(context.containsBean("corsWebFilter"));
    }
} 