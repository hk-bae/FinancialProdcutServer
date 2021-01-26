package com.hkbae.recommender;

//적금 클래스
public class SavingsData extends FinancialProduct {

    public SavingsData(BaseInfo baseInfo, Option option, char rsrv_type) {
        super(baseInfo, option);
        this.rsrv_type = rsrv_type;
    }

    private char rsrv_type;// 적립유형 (자유적립식 : F, 정액정립식 :S)

    public char getRsrvType() {
        return rsrv_type;
    }

    public void setRsrvType(char rsrv_type) {
        this.rsrv_type = rsrv_type;
    }

    // 적금에 amounts만큼 납입할 때 만기에 얻을 수 있는 만기지급액 계산
    public int getMaturityPayment(int amounts) {

        int term = option.getSaveTrm(); // 저축기간
        double intrRate = option.getIntrRate(); // 이자율
        char intrRateType = option.getIntrRateType(); // 저축금리유형(단리:S 복리:M)

        // 단리와 복리에 따라 만기지급액 방식이 나눠진다.
        // 월 납입금액(amounts) : a , 저축기간(term) : n, 이자율(intrRate) : r
        // 적립 원금 : a * n
        // 이자(단리) : a * n * (n+1)/2 * r/12
        // 이자(복리) : a * (1+r/12)*{(1+r/12)^n -1)}/(r/12) -a
        // 만기지급액 : 적립 원금 + 이자
        double interest = 0.0;
        switch (intrRateType) {
            case 'S':// 단리
                interest = amounts * ((intrRate / 100) / 12) * term * (term + 1) / 2;
                return (int) (amounts * term + interest);
            case 'M':// 복리
                interest = (amounts * (1 + (intrRate / 100) / 12) * (Math.pow(1 + (intrRate / 100) / 12, term) - 1)
                        / ((intrRate / 100) / 12)) - amounts * term;
                return (int) (amounts * term + interest);
        }

        return -1;
    }

}