package com.newland.tianyan.face.controller;



import com.newland.tianyan.common.api.IImageStorageApi;
import com.newland.tianyan.common.model.imagestrore.UploadReqDTO;
import com.newland.tianyan.common.model.imagestrore.UploadResDTO;
import com.newland.tianyan.common.model.vectorsearch.CreateColReqDTO;
import com.newland.tianyan.face.feign.VectorSearchFeignService;
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
    private IImageStorageApi imageStorageApi;

    @PostMapping("/test")
    public UploadResDTO addClient(@RequestBody UploadReqDTO req) {
        return imageStorageApi.upload(req);
    }

}
