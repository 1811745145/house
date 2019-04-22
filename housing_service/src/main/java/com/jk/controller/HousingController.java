package com.jk.controller;

import com.jk.mapper.HousMapper;
import com.jk.service.HousingServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class HousingController implements HousingServiceApi {
    @Autowired
    private HousMapper housMapper;

}
