package com.whozm.yygh.user.client;

import com.whozm.yygh.model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author HZM
 * @date 2023/1/31
 */
@FeignClient(value = "service-user")
public interface PatientFeignClient {
    @GetMapping("/user/userinfo/patient/{patientId}")
    public Patient getPatientById(@PathVariable(value = "patientId") Long patientId);
}
