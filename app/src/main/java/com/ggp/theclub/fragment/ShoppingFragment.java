package com.ggp.theclub.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.activity.SalesListActivity;
import com.ggp.theclub.adapter.SaleCategoriesAdapter;
import com.ggp.theclub.manager.AccountManager;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.util.CategoryUtils;
import com.ggp.theclub.view.ExpandableCategoryView;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.OnClick;
import java8.util.stream.StreamSupport;

public class ShoppingFragment extends BaseFragment {

    private AccountManager accountManager = MallApplication.getApp().getAccountManager();
    private List<Integer> categoryViewIds = Arrays.asList(R.id.sports, R.id.shoes, R.id.specialty, R.id.department_stores,
            R.id.fashion, R.id.food, R.id.housewares, R.id.electronics, R.id.beauty, R.id.all);

    private BiMap<String, Integer> categoryCodeMap = HashBiMap.create();
    private String mLoadImageLabel;

    public static ShoppingFragment newInstance() {
        return new ShoppingFragment();
    }

    @Override
    public void onFragmentVisible() {
        super.onFragmentVisible();
        analyticsManager.trackScreen(AnalyticsManager.Screens.Sales);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, R.layout.shopping_fragment, container);
        mLoadImageLabel = getDensityToLabel(getActivity());
        return view;
    }

    @Override
    protected void configureView() {
        setupCategoryCodeMap();
        setupCategories();
    }

    private void setupCategoryCodeMap() {
        categoryCodeMap.put(CategoryUtils.CATEGORY_FOOD, R.id.food);
        categoryCodeMap.put(CategoryUtils.CATEGORY_FASHION, R.id.fashion);
        categoryCodeMap.put(CategoryUtils.CATEGORY_SPORTS, R.id.sports);
        categoryCodeMap.put(CategoryUtils.CATEGORY_HEALTH_BEAUTY, R.id.beauty);
        categoryCodeMap.put(CategoryUtils.CATEGORY_ELECTRONICS, R.id.electronics);
        categoryCodeMap.put(CategoryUtils.CATEGORY_HOUSEWARES, R.id.housewares);
        categoryCodeMap.put(CategoryUtils.CATEGORY_SHOES, R.id.shoes);
        categoryCodeMap.put(CategoryUtils.CATEGORY_SPECIALTY, R.id.specialty);
        categoryCodeMap.put(CategoryUtils.CATEGORY_DEPARTMENT_STORES, R.id.department_stores);
        categoryCodeMap.put(CategoryUtils.CATEGORY_BLACK_FRIDAY, R.id.black_friday);
        categoryCodeMap.put(CategoryUtils.CATEGORY_EASTER, R.id.easter);
        categoryCodeMap.put(CategoryUtils.CATEGORY_HOLIDAY, R.id.holiday);
        categoryCodeMap.put(CategoryUtils.CATEGORY_VALENTINES, R.id.valentines);
        categoryCodeMap.put(CategoryUtils.CATEGORY_ALL_SALES, R.id.all);
    }

    private void setupCategories() {
        mallRepository.queryForSaleCategories(saleCategories -> {
            StreamSupport.stream(saleCategories).forEach(saleCategory -> {
                ExpandableCategoryView categoryView = getViewByCategoryCode(saleCategory.getCode());
                if (categoryView != null && !saleCategory.getSales().isEmpty()) {
                    categoryView.setTitle(saleCategory.getLabel());
                    RecyclerView expandedList = categoryView.getExpandedListView();
                    expandedList.setLayoutManager(new LinearLayoutManager(getActivity()));
                    expandedList.setAdapter(new SaleCategoriesAdapter(getActivity(), saleCategory.getChildCategories()));
                    categoryView.setVisibility(View.VISIBLE);

//                        Picasso with = Picasso.with(MallApplication.getApp());
//
//                        with.load(createUrl(saleCategory.getCode(), true))
//                            .networkPolicy(NetworkPolicy.OFFLINE)
//                                .into(categoryView.getExpandedBackground());
//
//                        with.load(createUrl(saleCategory.getCode(), false))
//                            .networkPolicy(NetworkPolicy.OFFLINE)
//                                .into(categoryView.getCollapsedBackground());

                    ImageLoader.getInstance()
                            .displayImage(
                                    createUrl(saleCategory.getCode(), true),
                                    categoryView.getExpandedBackground());

                    ImageLoader.getInstance()
                            .displayImage(
                                    createUrl(saleCategory.getCode(), false),
                                    categoryView.getCollapsedBackground());
                }
            });
        });
    }

    private ExpandableCategoryView getViewByCategoryCode(String code) {
        Integer id = categoryCodeMap.get(code);
        return id != null ? (ExpandableCategoryView) getActivity().findViewById(id) : null;
    }

    private void collapseExpandedViews(Integer excludedViewId) {
        StreamSupport.stream(categoryViewIds)
            .filter(id -> id != excludedViewId.intValue())
            .forEach(id -> {
                ExpandableCategoryView categoryView = (ExpandableCategoryView) getActivity().findViewById(id);
                if(categoryView != null) {
                    categoryView.collapse();
                }
            });
    }

    private void trackSelectedCategory(String categoryName) {
        HashMap<String, Object> contextData = new HashMap<>();
        analyticsManager.safePut(AnalyticsManager.ContextDataKeys.ShoppingCategoryName, categoryName, contextData);
        analyticsManager.trackAction(AnalyticsManager.Actions.ShoppingCategory, contextData);
    }

    @OnClick({R.id.sports, R.id.shoes, R.id.specialty, R.id.department_stores, R.id.fashion,
            R.id.food, R.id.housewares, R.id.electronics, R.id.beauty, R.id.black_friday, R.id.easter,
            R.id.holiday, R.id.valentines, R.id.all})
    public void expandableViewClicked(ExpandableCategoryView categoryView) {
        RecyclerView.Adapter adapter = categoryView.getExpandedListView().getAdapter();
        if(adapter != null && adapter.getItemCount() > 0) {
            categoryView.toggleExpansion();
        } else {
            String categoryCode = categoryCodeMap.inverse().get(categoryView.getId());
            startActivity(SalesListActivity.buildIntent(getActivity(), categoryCode));
        }

        collapseExpandedViews(categoryView.getId());
        trackSelectedCategory(categoryView.getTitle());
    }

    private String createUrl(String saleCategoryCode, boolean expanded) {
//        if(true){
//            return "https://s3.amazonaws.com/ggp-mobile/category-images/SPECIALTY_C.jpg";
//        }
        if(expanded){
            String format = "https://s3.amazonaws.com/ggp-mobile/category-images/%s_%s_%s.jpg";
            return String.format(format, saleCategoryCode, "expanded", mLoadImageLabel);
        } else {
            String format = "https://s3.amazonaws.com/ggp-mobile/category-images/%s_%s.jpg";
            return String.format(format, saleCategoryCode, mLoadImageLabel);
        }
    }

    private String getDensityToLabel(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        if (density >= 4.0) {
            return "D";
        }
        if (density >= 3.0) {
            return "C";
        }
        if (density >= 2.0) {
            return "B";
        }
        if (density >= 1.5) {
            return "A";
        }
        if (density >= 1.0) {
            return "A";
        }
        return "A";
    }
}