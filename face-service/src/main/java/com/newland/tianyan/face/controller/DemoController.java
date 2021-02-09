package com.newland.tianyan.face.controller;

import com.newland.tianyan.face.remote.ImageStoreFeignService;
import com.newland.tianyan.face.remote.VectorSearchFeignService;
import com.newland.tianyan.face.remote.dto.image.DownloadReq;
import com.newland.tianyan.face.remote.dto.milvus.CreateColReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/2
 */
@RestController
public class DemoController {

    @Autowired
    private VectorSearchFeignService imageStoreFeignService;

    @PostMapping("/addClient")
    public void addClient(@RequestBody CreateColReq req) {
        imageStoreFeignService.createCollection(req);
    }

}
