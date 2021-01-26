package com.hkbae.recommender;

import java.util.HashSet;
import java.util.List;

import com.hkbae.recommender.UserInfo.City;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommend/")
public class RecommendController {

    @Autowired
    RecommendService recommender;

    @GetMapping("")
    public List<FinancialProduct> getRecommendedList(@RequestBody UserInfo userInfo) {
        return recommender.filtering(userInfo);
    }

    @GetMapping("/test")
    public UserInfo test() {
        UserInfo userInfo = new UserInfo();
        userInfo.setAmounts(200000);
        userInfo.setAge(22);
        userInfo.setProductType(0);
        userInfo.setMonths(24);
        userInfo.setSex(0);
        String[] joinPath = new String[4];
        joinPath[0] = "영업점";
        userInfo.setJoinPath(joinPath);

        HashSet<City> cities = new HashSet<>();
        cities.add(City.SEOUL);
        cities.add(City.GYEONGGI);
        userInfo.setCities(cities);

        String[] conditions = new String[7];
        conditions[0] = "기초생활수급자";
        userInfo.setConditions(conditions);

        return userInfo;
    }

}
