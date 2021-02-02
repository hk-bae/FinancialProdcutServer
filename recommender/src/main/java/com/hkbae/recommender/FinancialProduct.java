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
    private int maturityPayment; // 만기지급액

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
    public abstract int calculateMaturityPayment(int amounts);

    public int getMaturityPayment() {
        return maturityPayment;
    }

    public void setMaturityPayment(int maturityPayment) {
        this.maturityPayment = maturityPayment;
    }
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

    public String getDcls_month() {
        return dcls_month;
    }

    public int getMax_limit() {
        return max_limit;
    }

    public void setMax_limit(int max_limit) {
        this.max_limit = max_limit;
    }

    public String getEtc_note() {
        return etc_note;
    }

    public void setEtc_note(String etc_note) {
        this.etc_note = etc_note;
    }

    public String getJoin_member() {
        return join_member;
    }

    public void setJoin_member(String join_member) {
        this.join_member = join_member;
    }

    public String getSpcl_cnd() {
        return spcl_cnd;
    }

    public void setSpcl_cnd(String spcl_cnd) {
        this.spcl_cnd = spcl_cnd;
    }

    public String getJoin_way() {
        return join_way;
    }

    public void setJoin_way(String join_way) {
        this.join_way = join_way;
    }

    public int getJoin_deny() {
        return join_deny;
    }

    public void setJoin_deny(int join_deny) {
        this.join_deny = join_deny;
    }

    public String getFin_prdt_nm() {
        return fin_prdt_nm;
    }

    public void setFin_prdt_nm(String fin_prdt_nm) {
        this.fin_prdt_nm = fin_prdt_nm;
    }

    public String getKor_co_nm() {
        return kor_co_nm;
    }

    public void setKor_co_nm(String kor_co_nm) {
        this.kor_co_nm = kor_co_nm;
    }

    public String getFin_prdt_cd() {
        return fin_prdt_cd;
    }

    public void setFin_prdt_cd(String fin_prdt_cd) {
        this.fin_prdt_cd = fin_prdt_cd;
    }

    public String getFin_co_no() {
        return fin_co_no;
    }

    public void setFin_co_no(String fin_co_no) {
        this.fin_co_no = fin_co_no;
    }

    public void setDcls_month(String dcls_month) {
        this.dcls_month = dcls_month;
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

    public String getDcls_month() {
        return dcls_month;
    }

    public double getIntr_rate2() {
        return intr_rate2;
    }

    public void setIntr_rate2(double intr_rate2) {
        this.intr_rate2 = intr_rate2;
    }

    public double getIntr_rate() {
        return intr_rate;
    }

    public void setIntr_rate(double intr_rate) {
        this.intr_rate = intr_rate;
    }

    public int getSave_trm() {
        return save_trm;
    }

    public void setSave_trm(int save_trm) {
        this.save_trm = save_trm;
    }

    public char getIntr_rate_type() {
        return intr_rate_type;
    }

    public void setIntr_rate_type(char intr_rate_type) {
        this.intr_rate_type = intr_rate_type;
    }

    public String getFin_prdt_cd() {
        return fin_prdt_cd;
    }

    public void setFin_prdt_cd(String fin_prdt_cd) {
        this.fin_prdt_cd = fin_prdt_cd;
    }

    public String getFin_co_no() {
        return fin_co_no;
    }

    public void setFin_co_no(String fin_co_no) {
        this.fin_co_no = fin_co_no;
    }

    public void setDcls_month(String dcls_month) {
        this.dcls_month = dcls_month;
    }

}