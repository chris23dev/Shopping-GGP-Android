package com.ggp.theclub.util;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.comparator.PromotionEndDateComparator;
import com.ggp.theclub.comparator.PromotionNameComparator;
import com.ggp.theclub.model.MallEvent;
import com.ggp.theclub.model.Promotion;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.Tenant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import java8.util.function.Function;
import java8.util.function.Functions;
import java8.util.function.Predicate;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class PromotionUtils {

    public static <T extends Promotion> List<T> getPromotionsWithStores(List<T> promotions, List<Tenant> tenants) {
        if (promotions == null) {
            return new ArrayList<>();
        }
        if (tenants == null) {
            return promotions;
        }

        Map<Integer, Tenant> tenantMap = StreamSupport.stream(tenants).collect(Collectors.toMap(Tenant::getId, Functions.identity()));

        StreamSupport.stream(promotions).forEach(promotion -> {
            Integer storeId = promotion.getTenantId();
            if (storeId != null) {
                promotion.setTenant(tenantMap.get(storeId));
            }
        });
        return promotions;
    }

    public static List<MallEvent> getSortedMallEvents(List<MallEvent> mallEvents) {
        if (mallEvents == null) {
            return new ArrayList<>();
        }

        Set<MallEvent> sortedMallEvents = new LinkedHashSet<>();
        sortedMallEvents.addAll(getFavoriteMallEventsByEndDate(mallEvents));
        sortedMallEvents.addAll(getMallEventsByEndDate(mallEvents));
        return StreamSupport.stream(sortedMallEvents).collect(Collectors.toList());
    }

    public static List<MallEvent> getMallEventsByStoreId(int id, List<MallEvent> mallEvents) {
        return mallEvents == null ? new ArrayList<>() : StreamSupport.stream(mallEvents).filter(mallEvent -> mallEvent.getTenant() != null && mallEvent.getTenant().getId() == id).collect(Collectors.toList());
    }

    protected static List<MallEvent> getMallEventsByEndDate(List<MallEvent> mallEvents) {
        return mallEvents == null ? new ArrayList<>() : getSortedPromotions(mallEvents, new PromotionEndDateComparator());
    }

    protected static List<MallEvent> getFavoriteMallEventsByEndDate(List<MallEvent> mallEvents) {
        if (mallEvents == null) {
            return new ArrayList<>();
        }

        List<Tenant> favoriteTenants = TenantUtils.getFavoriteTenants(StreamSupport.stream(mallEvents).map(MallEvent::getTenant).collect(Collectors.toList()));
        Set<MallEvent> favoriteMallEvents = new HashSet<>();
        StreamSupport.stream(favoriteTenants).forEach(store -> favoriteMallEvents.addAll(getMallEventsByStoreId(store.getId(), mallEvents)));
        return getSortedPromotions(favoriteMallEvents, new PromotionEndDateComparator());
    }

    public static List<Sale> getFeaturedSales(List<Sale> sales) {
        if (sales == null) {
            return new ArrayList<>();
        }

        Set<Sale> sortedSales = new LinkedHashSet<>();
        sortedSales.addAll(getDistinctFavoriteSales(sales));
        sortedSales.addAll(getSalesByTopRetailer(sales));
        return StreamSupport.stream(sortedSales).collect(Collectors.toList());
    }

    public static <T extends Promotion> List<T> getFavoritePromotions(List<T> promotions) {
        Set<Integer> favorites = MallApplication.getApp().getAccountManager().getCurrentUser().getFavorites();

        return promotions == null || favorites == null ?
                new ArrayList<>() :
                StreamSupport.stream(promotions).filter(promotion -> TenantUtils.isFavoriteTenant(promotion.getTenant(), favorites)).collect(Collectors.toList());
    }

    protected static List<Sale> getDistinctFavoriteSales(List<Sale> sales) {
        return StreamSupport.stream(getFavoritePromotions(sales)).filter(distinctPromotionByKey(sale -> sale.getTenant().getId())).collect(Collectors.toList());
    }

    protected static List<Sale> getSalesByTopRetailer(List<Sale> sales) {
        return sales == null ? new ArrayList<>() : StreamSupport.stream(sales).filter(Sale::isTopRetailer).filter(distinctPromotionByKey(s -> s.getTenant().getId())).collect(Collectors.toList());
    }

    public static List<Sale> getSalesByStoreId(int id, List<Sale> sales) {
        return sales == null ? new ArrayList<>() : StreamSupport.stream(sales).filter(sale -> sale.getTenant() != null && sale.getTenant().getId() == id).collect(Collectors.toList());
    }

    public static List<Sale> getSalesOrderedByStoreName(List<Sale> sales) {
        return getSortedPromotions(sales, new PromotionNameComparator());
    }

    public static List<Sale> getSalesByEndDate(List<Sale> sales) {
        return getSortedPromotions(sales, new PromotionEndDateComparator());
    }

    private static <T extends Promotion> List<T> getSortedPromotions(Collection<T> promotions, Comparator<Promotion> comparator) {
        if (promotions == null) {
            return new ArrayList<>();
        }
        if (comparator == null) {
            return new ArrayList<>(promotions);
        }

        return StreamSupport.stream(promotions).sorted(comparator).collect(Collectors.toList());
    }

    private static <T extends Promotion> Predicate<T> distinctPromotionByKey(Function<T, Object> keyExtractor) {
        ConcurrentHashMap<Object, Boolean> distinctMap = new ConcurrentHashMap<>();
        return promotion -> distinctMap.putIfAbsent(keyExtractor.apply(promotion), Boolean.TRUE) == null;
    }
}