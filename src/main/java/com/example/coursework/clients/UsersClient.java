package com.example.coursework.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "${app.clients.users.name}",
        url = "${app.clients.users.url}" ,
        path = "${app.clients.users.user-path}",
        configuration = UsersClient.class
)
public interface UsersClient {
}
