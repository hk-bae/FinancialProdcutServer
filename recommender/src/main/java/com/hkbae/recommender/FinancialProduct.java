package com.hkbae.recommender;

//금융상품에 대한 abstract class
//Deposit(정기예금), SavingsData(적금)이 이를 상속받는다.
//멤버 필드로 BaseInfo(기본정보), Option(추가정보)를 갖는다.
public abstract class FinancialProduct {

    public FinancialProduct(BaseInfo baseInfo, Option option) {
        this.baseInfo = baseInfo;
        this.option = option;
    }

    BaseInfo baseInfo;
    Option option;

    // 멤버에 대한 getter 메서드
    public BaseInfo getBaseInfo() {
        return baseInfo;
    }

    public Option getOption() {
        return option;
    }

    // 멤버에 대한 setter 메서드
    public void setBaseInfo(BaseInfo b) {
        this.baseInfo = b;
    }

    public void setOption(Option o) {
        this.option = o;
    }

    // 월 납입금액 또는 예치금에 대하여 만기 시 만기지급액을 계산하는 메서드
    public abstract int getMaturityPayment(int amounts);

}

// 상품에 대한 기본정보 클래스
// 금융감독원 금융상품 한눈에 홈페이지의 정기예금API 및 적금API의 baseInfo 정보들을 저장하는 클래스이다.
class BaseInfo {

    // 아래 3개의 멤버들은 option과 연결되는 멤버들이다.
    // 하나의 (dcls_month,fin_co_no,fin_prdt_cd) 쌍에 대하여 여러 개의 옵션을 가질 수 있다.
    private String dcls_month; // 공시제출월[YYYYMM]
    private String fin_co_no; // 금융회사코드
    private String fin_prdt_cd; // 금융상품코드

    private String kor_co_nm; // 금융회사명
    private String fin_prdt_nm; // 금융상품명
    private String join_way; // 가입방법
    private String spcl_cnd; // 우대조건
    private int join_deny; // 가입제한 (1:제한없음 2:서민전용 3:일부제한)
    private String join_member; // 가입대상(키워드 : 만 XX세 이상, 여성)
    private String etc_note; // 기타유의사항
    private int max_limit; // 최고한도

    // getter 메서드
    public String getDclsMonth() {
        return dcls_month;
    }

    public String getFinCoNo() {
        return fin_co_no;
    }

    public String getFinPrdtCd() {
        return fin_prdt_cd;
    }

    public String getKorCoNm() {
        return kor_co_nm;
    }

    public String getFinPrdtNm() {
        return fin_prdt_nm;
    }

    public int getJoinDeny() {
        return join_deny;
    }

    public String getJoinWay() {
        return join_way;
    }

    public String getSpclCnd() {
        return spcl_cnd;
    }

    public String getJoinMember() {
        return join_member;
    }

    public String getEtcNote() {
        return etc_note;
    }

    public int getMaxLimit() {
        return max_limit;
    }

    // setter 메서드
    public void setDclsMonth(String dcls_month) {
        this.dcls_month = dcls_month;
    }

    public void setFinCoNo(String fin_co_no) {
        this.fin_co_no = fin_co_no;
    }

    public void setFinPrdtCd(String fin_prdt_cd) {
        this.fin_prdt_cd = fin_prdt_cd;
    }

    public void setKorCoNm(String kor_co_nm) {
        this.kor_co_nm = kor_co_nm;
    }

    public void setFinPrdtNm(String fin_prdt_nm) {
        this.fin_prdt_nm = fin_prdt_nm;
    }

    public void setJoinDeny(int join_deny) {
        this.join_deny = join_deny;
    }

    public void setJoinWay(String join_way) {
        this.join_way = join_way;
    }

    public void setSpclCnd(String spcl_cnd) {
        this.spcl_cnd = spcl_cnd;
    }

    public void setJoinMember(String join_member) {
        this.join_member = join_member;
    }

    public void setEtcNote(String etc_note) {
        this.etc_note = etc_note;
    }

    public void setMaxLimit(int max_limit) {
        this.max_limit = max_limit;
    }
}

class Option {

    private String dcls_month; // 공시제출월[YYYYMM]
    private String fin_co_no; // 금융회사코드
    private String fin_prdt_cd; // 금융상품코드

    private char intr_rate_type;// 저축금리유형(단리 :S 복리 :M)
    private int save_trm; // 저축 기간[단위:개월]
    private double intr_rate; // 저축금리[소수점 2자리]
    private double intr_rate2; // 최고 우대금리[소수점 2자리]

    // getter메서드
    public String getDclsMonth() {
        return dcls_month;
    }

    public String getFinCoNo() {
        return fin_co_no;
    }

    public String getFinPrdtCd() {
        return fin_prdt_cd;
    }

    public char getIntrRateType() {
        return intr_rate_type;
    }

    public int getSaveTrm() {
        return save_trm;
    }

    public double getIntrRate() {
        return intr_rate;
    }

    public double getIntrRate2() {
        return intr_rate2;
    }

    // setter메서드

    public void setDclsMonth(String dcls_month) {
        this.dcls_month = dcls_month;
    }

    public void setFinCoNo(String fin_co_no) {
        this.fin_co_no = fin_co_no;
    }

    public void setFinPrdtCd(String fin_prdt_cd) {
        this.fin_prdt_cd = fin_prdt_cd;
    }

    public void setIntrRateType(char intr_rate_type) {
        this.intr_rate_type = intr_rate_type;
    }

    public void setSaveTrm(int save_trm) {
        this.save_trm = save_trm;
    }

    public void setIntrRate(double intr_rate) {
        this.intr_rate = intr_rate;
    }

    public void setIntrRate2(double intr_rate2) {
        this.intr_rate2 = intr_rate2;
    }
}