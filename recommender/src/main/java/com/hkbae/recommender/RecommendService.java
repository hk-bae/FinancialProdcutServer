package com.hkbae.recommender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.stereotype.Service;

@Service
public class RecommendService {

    // Json통신을 위한 서비스키
    private static final String SERVICEKEY = "ca443e810ffaef39de5ba9871162e3f8";
    private static final String URL = "http://finlife.fss.or.kr/finlifeapi";

    // 사용자 입력에 따라 적금 데이터 필터링
    public ArrayList<FinancialProduct> filtering(UserInfo userInfo) {

        // 추천상품들을 담을 리스트
        ArrayList<FinancialProduct> recommendedList = new ArrayList<>();

        int pageNo = 0; // 현재 페이지 번호
        int maxPageNo = 1; // 최대 페이지 번호

        // 전체 페이지를 모두 보도록 while문을 돈다
        while (pageNo < maxPageNo) {
            // userInfo의 productType이 0이면 정기예금api, 1이면 적금 api를 받아오고 필터링을 진행한다.

            pageNo++;

            try {
                StringBuilder urlBuilder = new StringBuilder(URL); /* URL */
                if (userInfo.getProductType() == 0) // 정기예금
                    urlBuilder.append("/" + URLEncoder.encode("depositProductsSearch", "UTF-8")); // 인증키
                else// 적금
                    urlBuilder.append("/" + URLEncoder.encode("savingProductsSearch", "UTF-8"));

                urlBuilder.append("." + URLEncoder.encode("json", "UTF-8")); // 인증키
                urlBuilder.append("?" + URLEncoder.encode("auth", "UTF-8") + "=" + SERVICEKEY); // 인증키
                urlBuilder.append("&" + URLEncoder.encode("topFinGrpNo", "UTF-8") + "=" + "020000"); // 권역코드 : 은행으로 제한
                urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + pageNo); // 불러올 페이지번호

                URL url = new URL(urlBuilder.toString());
                HttpURLConnection hConn = (HttpURLConnection) url.openConnection();
                hConn.setRequestMethod("GET");
                hConn.setRequestProperty("Content-type", "application/json");

                BufferedReader rd;

                if (hConn.getResponseCode() >= 200 && hConn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(hConn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(hConn.getErrorStream()));
                }

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }

                rd.close();
                hConn.disconnect();

                // Json String을 JsonObject로 변환
                JsonParser jParser = new JsonParser();
                JsonObject jObj = (JsonObject) jParser.parse(sb.toString());

                // Json 객체로 부터 result를 받아온다.
                JsonObject resultJson = (JsonObject) jObj.get("result");

                // maxPageNo이 초기값일 때만 1회 값을 설정한다.
                if (maxPageNo == 1) {
                    maxPageNo = resultJson.get("max_page_no").getAsInt();
                }

                // result에서 각각 baseList Array와 optionList Array를 Json Array에 담는다.
                JsonArray baseList = (JsonArray) resultJson.get("baseList");
                JsonArray optionList = (JsonArray) resultJson.get("optionList");

                // baseList의 모든 json object에 접근
                for (int i = 0; i < baseList.size(); i++) {

                    JsonObject baseInfo = (JsonObject) baseList.get(i);

                    int max_limit = 0;

                    // 최고한도가 없으면(null) Integer.MAX_VALUE로 설정하고
                    // 존재하면 그 값을 받아온다.
                    if (baseInfo.get("max_limit").isJsonNull()) {
                        max_limit = Integer.MAX_VALUE;
                    } else {
                        max_limit = baseInfo.get("max_limit").getAsInt();
                    }

                    // 사용자가 입력한 금액이 최고한도보다 크면 해당 상품을 추가하지 않는다.
                    if (userInfo.getAmounts() > max_limit)
                        continue;

                    String join_way = baseInfo.get("join_way").getAsString(); // 가입방법

                    /* 가입방법이 사용자가 입력한 가입경로 조건에 만족되는지 확인 */

                    // join_way 형식을 , 대신 공백으로 바꿔준다. (ex:영업점 스마트폰)
                    join_way = join_way.replace(',', ' ');
                    // check=0으로 두고 사용자의 가입경로 배열인 joinPath의 String중 하나라도 join_way에 포함된다면 check=1
                    // 모두 포함되지 않는다면 check=0이고 check=0이면 해당 상품을 추가하지 않는다.
                    String[] joinPath = userInfo.getJoinPath().clone(); // 사용자의 가입경로를 받아온다.

                    byte check = 0;
                    for (int m = 0; m < joinPath.length; m++) {
                        if (joinPath[m] != null && join_way.contains(joinPath[m])) {
                            check = 1;
                        } else {
                            // 만약 사용자의 가입경로에는 존재하지만 해당 상품의 가입방법에 없다면 null로 바꿔준다.
                            joinPath[m] = null;
                        }
                    }

                    if (check == 0)
                        continue;

                    String fin_co_no = baseInfo.get("fin_co_no").getAsString(); // 금융회사 코드
                    // 사용자의 가입경로에 영업점만 포함되어 있다면 해당 은행의 영업점 지역을 확인해야 한다.
                    // 사용자가 방문할 수 있는 지역에 영업점이 있지 않으면 추가하지 않고 다음 상품으로 넘어간다.
                    if (joinPath[0] == null) {
                        // 영업점이 null이면 아무일도 하지 않는다.
                    } else if (joinPath[0].equals("영업점") && joinPath[1] == null && joinPath[2] == null
                            && joinPath[3] == null) {
                        HashSet<UserInfo.City> userCities = userInfo.getCities(); // 사용자가 선택한 지역

                        // 금융회사코드와 사용자가 선택한 지역의 리스트를 전달하여 사용자가 선택한 지역 내에 해당 지점이 존재한다면 true
                        // 존재하지 않는다면 false를 return 하고 해당 상품을 추가하지 않는다
                        if (!findAvailableCity(fin_co_no, userCities))
                            continue;
                    }

                    /* 가입대상 확인 */

                    String join_member = baseInfo.get("join_member").getAsString(); // 가입대상

                    // 가입대상에 대한 문자열과 사용자의 나이를 전달하여 나이 조건을 충족하는지 확인하는 메서드
                    // 조건에 만족하지 않으면 다음 상품으로 넘어간다.
                    if (!checkAge(join_member, userInfo.getAge()))
                        continue;

                    // 여성을 위한 상품인지 확인하고 여성을 위한 상품이라면 사용자의 성별이 남성일 때 다음 상품으로 넘어간다.
                    if (onlyForWomen(join_member) && userInfo.getSex() == 0)
                        continue;

                    // 여러 조건들을 비교해보고 조건들에 만족하지 않으면 다음 상품으로 넘어간다.
                    if (!checkCondition(join_member, userInfo.getConditions()))
                        continue;

                    String dcls_month = baseInfo.get("dcls_month").getAsString(); // 공시제출월
                    String fin_prdt_cd = baseInfo.get("fin_prdt_cd").getAsString(); // 금융상품코드
                    // 해당 baseInfo에 해당하는 option을 조회하여 저축기간 조건이 사용자 입력과 일치하면
                    // Option 객체와 BaseInfo 객체를 생성
                    // 정기 예금 상품이면 Deposits 객체를 생성, 적금 상품이면 SavingsData를 생성하여
                    // recommendedList에 추가한다.
                    for (int k = 0; k < optionList.size(); k++) {

                        JsonObject option = (JsonObject) optionList.get(k);

                        // baseList의 공시제출월, 금융회사코드, 금융상품코드와 일치하는 option을 찾고
                        // 해당 option의 저축기간을 사용자가 입력한 저축기간과 비교한다.
                        if (option.get("fin_prdt_cd").getAsString().equals(fin_prdt_cd)
                                && option.get("dcls_month").getAsString().equals(dcls_month)
                                && option.get("fin_co_no").getAsString().equals(fin_co_no)) {

                            int save_trm = option.get("save_trm").getAsInt(); // 옵션의 저축기간
                            int userMonths = userInfo.getMonths(); // 사용자가 입력한 저축기간

                            // 저축기간이 사용자 입력과 같지 않으며 다음 option으로 넘어간다.
                            if (save_trm != userMonths)
                                continue;

                            char intr_rate_type = option.get("intr_rate_type").getAsCharacter(); // 저축금리유형[S:단리,M:복리]
                            double intr_rate = option.get("intr_rate").getAsDouble(); // 저축금리
                            double intr_rate2 = option.get("intr_rate2").getAsDouble(); // 최고 우대금리

                            // Option 객체 생성, set value
                            Option opt = new Option();
                            opt.setDcls_month(dcls_month);
                            opt.setFin_co_no(fin_co_no);
                            opt.setFin_prdt_cd(fin_prdt_cd);
                            opt.setSave_trm(save_trm);
                            opt.setIntr_rate_type(intr_rate_type);
                            opt.setIntr_rate(intr_rate);
                            opt.setIntr_rate2(intr_rate2);

                            // BaseInfo 객체를 생성하기 위해 필요한 정보들 가져오기
                            String kor_co_nm = baseInfo.get("kor_co_nm").getAsString(); // 금융회사명

                            String fin_prdt_nm = baseInfo.get("fin_prdt_nm").getAsString(); // 금융상품명
                            String spcl_cnd = baseInfo.get("spcl_cnd").getAsString();// 우대조건
                            String etc_note = baseInfo.get("etc_note").getAsString(); // 기타 유의사항

                            // BaseInfo 객체 생성, set value
                            BaseInfo b = new BaseInfo();
                            b.setDcls_month(dcls_month);
                            b.setFin_co_no(fin_co_no);
                            b.setFin_prdt_cd(fin_prdt_cd);
                            b.setKor_co_nm(kor_co_nm);
                            b.setFin_prdt_nm(fin_prdt_nm);
                            b.setJoin_way(join_way);
                            b.setJoin_member(join_member);
                            b.setEtc_note(etc_note);
                            b.setSpcl_cnd(spcl_cnd);
                            b.setMax_limit(max_limit);

                            char rsrv_type;// 적립 유형[S:정액적립식, F:자유적립식] 적금인 경우에 데이터를 받아온다.
                            FinancialProduct p;

                            // 정기 예금 상품이면 Deposit 객체 생성.
                            // 적금 상품이면 SavingsData 생성
                            if (userInfo.getProductType() == 0) {
                                p = new Deposit(b, opt);
                            } else {
                                // 적금일 경우 적립 유형을 받아온다.
                                rsrv_type = option.get("rsrv_type").getAsCharacter();
                                p = new SavingsData(b, opt, rsrv_type);
                            }
                            // 만기지급액 계산
                            p.setMaturityPayment(p.calculateMaturityPayment(userInfo.getAmounts()));
                            // 추천 리스트에 상품 추가
                            recommendedList.add(p);
                        }

                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return recommendedList;

    }

    // 해당 금융회사 코드를 갖는 금융회사의 점포에 ArrayList에 있는 City에 해당하는 점포가 한 곳이라도 존재하면 true를
    // return한다.
    // 금융회사 API를 활용한다.
    private boolean findAvailableCity(String fin_co_no, HashSet<UserInfo.City> userCity) throws Exception {

        // 금융회사 API에 해당 금융회사의 코드를 지정하여 json 데이터를 불러온다.
        StringBuilder urlBuilder = new StringBuilder(URL); /* URL */
        urlBuilder.append("/" + URLEncoder.encode("companySearch", "UTF-8"));
        urlBuilder.append("." + URLEncoder.encode("json", "UTF-8")); // 인증키
        urlBuilder.append("?" + URLEncoder.encode("auth", "UTF-8") + "=" + SERVICEKEY); // 인증키
        urlBuilder.append("&" + URLEncoder.encode("topFinGrpNo", "UTF-8") + "=" + "020000"); // 권역코드 : 은행으로 제한
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + 1); // 조회할 페이지 번호
        urlBuilder.append("&" + URLEncoder.encode("financeCd", "UTF-8") + "=" + fin_co_no); // 금융회사 코드

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection hConn = (HttpURLConnection) url.openConnection();
        hConn.setRequestMethod("GET");
        hConn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;

        if (hConn.getResponseCode() >= 200 && hConn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(hConn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(hConn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        rd.close();
        hConn.disconnect();

        // Json String을 JsonObject로 자르기 위해 파서 생성
        JsonParser jParser = new JsonParser();
        JsonObject jObj = (JsonObject) jParser.parse(sb.toString());

        JsonObject resultJson = (JsonObject) jObj.get("result");

        // result에서 optionList Array를 Json Array에 담는다.
        JsonArray optionList = (JsonArray) resultJson.get("optionList");

        // 각 city에 대하여 해당 도시의 점포가 존재하면 return true
        for (UserInfo.City city : userCity) {
            String cityName = city.getCityName();

            for (int j = 0; j < optionList.size(); j++) {
                JsonObject options = (JsonObject) optionList.get(j);
                if (options.get("area_nm").getAsString().equals(cityName)
                        && options.get("exis_yn").getAsString().equals("Y"))
                    return true;
            }
        }
        // 하나도 해당되지 않으면 false
        return false;
    }

    // 해당 가입대상에 허용되는 나이인지 확인하는 메서드
    private boolean checkAge(String joinMember, int age) {

        // 만 maxAge세 이상 minAge이하
        int maxAge = 100;
        int minAge = 0;
        String[] intStr = new String[2];

        // 문자열에서 만 XX세를 기준으로 XX의 문자열만 잘라내서 intStr에 담는다.
        Pattern pattern = Pattern.compile("[만](.*?)[세]");
        Matcher matcher = pattern.matcher(joinMember);

        int i = 0;

        while (matcher.find()) {
            intStr[i] = matcher.group(1);
            i++;
            if (matcher.group(1) == null)
                break;
        }

        // 정수형 변환을 위해 공백 제거
        for (int j = 0; j < intStr.length; j++) {
            if (intStr[j] != null)
                intStr[j] = intStr[j].trim();
        }

        if (intStr[1] != null) {
            // 문자열이 2개가 담겼다면 범위 값이므로 첫번째와 두번째를 minAge와 maxAge 각각에 담는다.
            minAge = Integer.parseInt(intStr[0]);
            maxAge = Integer.parseInt(intStr[1]);
            // "미만"이라는 문자열이 포함된 경우 maxAge를 1 감소한다.
            if (joinMember.contains("미만"))
                maxAge--;
            // 문자열이 1개가 담겼다면 이상,이하,미만에 따라 minAge와 maxAge를 설정한다.
        } else if (joinMember.contains("이상")) {
            minAge = Integer.parseInt(intStr[0]);
        } else if (joinMember.contains("이하")) {
            maxAge = Integer.parseInt(intStr[0]);
        } else if (joinMember.contains("미만")) {
            maxAge = Integer.parseInt(intStr[0]) - 1;
        }

        // age가 minAge와 maxAge 사이에 존재하면 true를 return 그렇지 않으면 false
        return age >= minAge && age <= maxAge;

    }

    // 해당 상품의 대상이 '여자'로 한정되는지 체크하는 메서드
    private boolean onlyForWomen(String joinMember) {
        // 여자에 한정되면 true를 return
        return joinMember.contains("여성") || joinMember.contains("여자");
    }

    // 해당 상품의 조건이 사용자의 조건과 일치하는지 확인
    private boolean checkCondition(String joinMember, String[] conditions) {

        int conditionExists = 0;// 조건이 존재하는지 확인하는 flag

        // 모든 문자사이의 공백제거
        joinMember = joinMember.replaceAll(" ", "");

        // 가입 대상에 각 키워드가 있을 경우 사용자의 조건에 해당 키워드가 존재하면 true를 return한다.
        if (joinMember.contains("기초생활수급자")) {
            conditionExists = 1;
            for (String cond : conditions) {
                if (cond != null && cond.equals("기초생활수급자"))
                    return true;
            }
        }

        if (joinMember.contains("소년소녀가장")) {
            for (String cond : conditions) {
                if (cond != null && cond.equals("소년소녀가장"))
                    return true;
            }
        }

        if (joinMember.contains("북한이탈주민")) {
            conditionExists = 1;
            for (String cond : conditions) {
                if (cond != null && cond.equals("북한이탈주민"))
                    return true;
            }
        }

        if (joinMember.contains("결혼이민자")) {
            conditionExists = 1;
            for (String cond : conditions) {
                if (cond != null && cond.equals("결혼이민자"))
                    return true;
            }
        }

        if (joinMember.contains("근로장려금")) {
            conditionExists = 1;
            for (String cond : conditions) {
                if (cond != null && cond.equals("근로장려금"))
                    return true;
            }
        }

        if (joinMember.contains("한부모가족지원")) {
            conditionExists = 1;
            for (String cond : conditions) {
                if (cond != null && cond.equals("한부모가족지원"))
                    return true;
            }
        }

        if (joinMember.contains("차상위계층")) {
            conditionExists = 1;
            for (String cond : conditions) {
                if (cond != null && cond.equals("차상위계층"))
                    return true;
            }
        }

        // 모든 조건을 check한 후 conditionExist가 1이면 return false
        // 0이면 retrun true

        if (conditionExists == 1) {
            return false;
        } else {
            return true;
        }

    }

}