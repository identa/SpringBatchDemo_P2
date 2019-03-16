package com.batch_p2.processor;

import com.batch_p2.model.Campaign;
import org.springframework.batch.item.ItemProcessor;

public class BudgetCheckProcessor implements ItemProcessor<Campaign, Campaign> {
    @Override
    public Campaign process(Campaign campaign) throws Exception {
        if (campaign.getBudget() < campaign.getBid() && campaign.getStatusID()== 1){
//            campaign.setStatusID(0);
            return campaign;
        }
        else return null;
    }
}
