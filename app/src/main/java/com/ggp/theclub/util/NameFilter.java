package com.ggp.theclub.util;

import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Use this to filter any list by name efficiently
 * takes advantage of the fact that if the string contains the previous search string, the results
 * will be a subset of the previous results.
 */
public class NameFilter<T> {
    private List<T> initialList;
    private List<T> previousResultList;
    private String previousString;
    private GetNameFunctional<T> getNameFunctional;

    public NameFilter(List<T> intialList, GetNameFunctional<T> getNameFunctional) {
        this.getNameFunctional = getNameFunctional;
        this.initialList = intialList;
    }

    public List<T> filterByName(String s) {
        List<T> searchList =  !StringUtils.isEmpty(previousString) && !StringUtils.isEmpty(s) && s.contains(previousString) ? previousResultList : initialList;
        List<T> resultList = StreamSupport.stream(searchList).filter(item -> getNameFunctional.getName(item).
                toUpperCase().contains(s.toUpperCase())).collect(Collectors.toList());

        previousResultList = resultList;
        previousString = s;
        return resultList;
    }

    public interface GetNameFunctional<S> {
        String getName(S item);
    }
}
