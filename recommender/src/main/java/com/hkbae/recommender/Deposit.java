package com.hkbae.recommender;

//정기 예금 클래스
public class Deposit extends FinancialProduct {

    public Deposit(BaseInfo baseInfo, Option option) {
        super(baseInfo, option);
    }

    // 정기예금에 amounts만큼 예치할 때 만기에 얻을 수 있는 만기지급액 계산
    public int getMaturityPayment(int amounts) {

        int term = option.getSaveTrm(); // 저축기간
        double intrRate = option.getIntrRate(); // 이자율
        char intrRateType = option.getIntrRateType(); // 저축금리유형(단리:S 복리:M)

        // 단리와 복리에 따라 만기지급액 방식이 나눠진다.
        // 예치금(amounts) : a , 저축기간(term) : n, 이자율(intrRate) : r
        // 적립원금 : a
        // 원금+이자(단리) : a * (1+r*n/12)
        // 원금+이자(복리) : a * (1+r/12)^n
        // 만기지급액 : 적립 원금 + 이자
        switch (intrRateType) {
            case 'S':
                return (int) (amounts * (1 + (intrRate / 100) * (term / 12)));
            case 'M':
                return (int) (amounts * Math.pow((1 + (intrRate / 100) / 12), term));
        }

        return -1;
    }

}
