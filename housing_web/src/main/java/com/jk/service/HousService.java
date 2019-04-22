package com.jk.service;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "hous-service")
public interface HousService extends HousingServiceApi {
}
