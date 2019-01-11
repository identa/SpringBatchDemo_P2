package com.batch_p2.controller;

import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class JobLaunchingController {

    @Autowired
    private JobOperator jobOperator;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void launch(@RequestParam("name") String name) throws Exception {
//        if (name == "job")
        this.jobOperator.start("job", String.format("name=%s", name));
    }
}
