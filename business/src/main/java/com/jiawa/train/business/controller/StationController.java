package com.jiawa.train.business.controller;

import com.jiawa.train.business.service.StationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/station")
public class StationController {

    @Resource
    private StationService stationService;

//    @GetMapping("/query-all")
//    public CommonResp<List<StationQueryResp>> queryList() {
////        List<StationQueryResp> list = stationService.queryAll();
////        return new CommonResp<>(list);
//    }

}
