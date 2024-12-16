package com.example.deploy.domain.choice.service;


import com.example.deploy.domain.campaign.model.entity.Campaign;
import com.example.deploy.domain.campaign.repository.CampaignRepository;
import com.example.deploy.domain.choice.model.entity.Choice;
import com.example.deploy.domain.choice.model.request.ChoiceInfluencerRequest;
import com.example.deploy.domain.choice.repository.ChoiceRepository;
import com.example.deploy.domain.user.model.entity.User;
import com.example.deploy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChoiceService {

    private final ChoiceRepository choiceRepository;
    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository;

    public void choiceInfluencer(ChoiceInfluencerRequest choiceInfluencerRequest) {
        User user = userRepository.getUserById(choiceInfluencerRequest.userId());
        Campaign campaign =
                campaignRepository.getCampaignById(choiceInfluencerRequest.campaignId());

        Choice choice = Choice.from(user, campaign);
        choiceRepository.save(choice);
    }
}
