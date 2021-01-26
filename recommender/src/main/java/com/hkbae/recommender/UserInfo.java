package com.hkbae.recommender;

import java.util.HashSet;

//사용자 정보를 갖는 클래스
public class UserInfo {

    private int productType; // 정기예금=0,적금=1
    private int months; // 가입기간(개월)
    private int amounts; // 월 납입금액(원) 또는 예치금
    private String[] joinPath = new String[4]; // 가입경로 0.모바일뱅킹 1.영업점 2.인터넷뱅킹 3.콜센터
    private int sex; // 1.남자 2.여자
    private int age; // 만 나이
    private HashSet<City> cities = new HashSet<>(); // 은행 방문 가능 지역
    private String[] conditions = new String[7]; // 조건
    // 0.기초생활수급자 1.소년소녀가장 2.북한이탈주민 3.결혼이민자 4.근로장려금수급자 5.한부모가족지원 보호대상자 6.차상위계층 대상자

    public enum City {
        SEOUL("서울"), BUSAN("부산"), DAEGU("대구"), INCHEON("인천"), GWANGJU("광주"), DAEJEON("대전"), ULSAN("울산"), GYEONGGI("경기"),
        GANGWON("강원"), CHUNGBUK("충북"), CHUNGNAM("충남"), JEONBUK("전북"), JEONNAM("전남"), GYEONGBUK("경북"), GYEONGNAM("경남"),
        JEJU("제주");

        private String cityName;

        City(String cityName) {
            this.cityName = cityName;
        }

        public String getCityName() {
            return cityName;
        }
    }

    // 멤버들에 대한 getter 메서드
    public int getProductType() {
        return productType;
    }

    public int getMonths() {
        return months;
    }

    public int getAmounts() {
        return amounts;
    }

    public String[] getJoinPath() {
        return joinPath;
    }

    public int getSex() {
        return sex;
    }

    public int getAge() {
        return age;
    }

    public HashSet<City> getCities() {
        return cities;
    }

    public String[] getConditions() {
        return conditions;
    }

    // 멤버들에 대한 setter 메서드
    public void setProductType(int pdtType) {
        this.productType = pdtType;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public void setAmounts(int amounts) {
        this.amounts = amounts;
    }

    public void setJoinPath(String[] joinPath) {
        this.joinPath = joinPath;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setCities(HashSet<City> cities) {
        this.cities = cities;
    }

    public void setConditions(String[] conditions) {
        this.conditions = conditions;
    }

}
