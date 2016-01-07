package com.birdswipe.mohsenraeisi.game;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mohsen raeisi on 25/12/2015.
 */
public class SortedList<E> extends AbstractList<E> {

    private ArrayList<E> internalList = new ArrayList<E>(5);

    // Note that add(E e) in AbstractList is calling this one
    @Override
    public void add(int position, E e) {
        internalList.add(e);
        Collections.sort(internalList, null);
    }

    @Override
    public E get(int i) {
        return internalList.get(i);
    }

    @Override
    public int size() {
        return internalList.size();
    }

}
