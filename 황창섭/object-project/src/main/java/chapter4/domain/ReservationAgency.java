package chapter4.domain;

import chapter4.domain.enumeration.DiscountConditionType;

public class ReservationAgency {
    public Reservation reserve(Screening screening, Customer customer, int audienceCount){
        Movie movie = screening.getMovie();

        boolean discountable = false;
        for(DiscountCondition condition : movie.getDiscountConditionList()){
            if(condition.getType() == DiscountConditionType.PERIPD){

            }
        }

        /*

        ...


         */
    }
}
