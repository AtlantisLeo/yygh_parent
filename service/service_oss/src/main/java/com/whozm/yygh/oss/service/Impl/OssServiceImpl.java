package com.whozm.yygh.oss.service.Impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.whozm.yygh.oss.prop.Ossporperties;
import com.whozm.yygh.oss.service.OssService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * @author HZM
 * @date 2023/1/28
 */
@Service
public class OssServiceImpl implements OssService{


    @Autowired
    private Ossporperties ossporperties;

    @Override
    public String upload(MultipartFile file) {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = ossporperties.getEndpoint();
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = ossporperties.getKeyid();
        String accessKeySecret = ossporperties.getKeysecret();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = ossporperties.getBucketname();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        String filename = new DateTime().toString("yyyy/MM/dd")+UUID.randomUUID().toString().replaceAll("-","")+file.getOriginalFilename();
        try {
            // 上传字符串。
            PutObjectResult result = ossClient.putObject(bucketName,filename,file.getInputStream());
            return "https://"+ossporperties.getBucketname()+"."+ossporperties.getEndpoint()+"/"+filename;
        }catch (Exception ce) {
            System.out.println("Error Message:" + ce.getMessage());
            return null;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
