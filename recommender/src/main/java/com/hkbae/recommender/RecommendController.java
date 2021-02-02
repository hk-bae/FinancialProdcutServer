package com.hkbae.recommender;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommend/")
public class RecommendController {

    @Autowired
    RecommendService recommender;

    @PostMapping("")
    public List<FinancialProduct> getRecommendedList(@RequestBody UserInfo userInfo) {
        return recommender.filtering(userInfo);
    }

}
