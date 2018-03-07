package com.ggp.theclub.model.view;

import com.ggp.theclub.model.MallEvent;
import com.ggp.theclub.model.Tenant;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class HomeFeedAuthenticatedViewModel extends HomeFeedViewModel {
    @Getter @Setter List<MallEvent> favoriteEvents;
    @Getter @Setter List<Tenant> tenants;
}
