
package org.apache.ibatis.submitted.custom_collection_handling;

import java.util.*;

public class CustomCollection<T> {

    private List<T> data = new ArrayList<>();

    public <K> K[] toArray(K[] a) {
        return data.toArray(a);
    }

    public Object[] toArray() {
        return data.toArray();
    }

    public List<T> subList(int fromIndex, int toIndex) {
        return data.subList(fromIndex, toIndex);
    }

    public int size() {
        return data.size();
    }

    public T set(int index, T element) {
        return data.set(index, element);
    }

    public boolean retainAll(Collection<?> c) {
        return data.retainAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return data.removeAll(c);
    }

    public T remove(int index) {
        return data.remove(index);
    }

    public boolean remove(Object o) {
        return data.remove(o);
    }

    public ListIterator<T> listIterator(int index) {
        return data.listIterator(index);
    }

    public ListIterator<T> listIterator() {
        return data.listIterator();
    }

    public int lastIndexOf(Object o) {
        return data.lastIndexOf(o);
    }

    public Iterator<T> iterator() {
        return data.iterator();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int indexOf(Object o) {
        return data.indexOf(o);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    public T get(int index) {
        return data.get(index);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CustomCollection)) return false;
        return data.equals(((CustomCollection)o).data);
    }

    public boolean containsAll(Collection<?> c) {
        return data.containsAll(c);
    }

    public boolean contains(Object o) {
        return data.contains(o);
    }

    public void clear() {
        data.clear();
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        return data.addAll(index, c);
    }

    public boolean addAll(Collection<? extends T> c) {
        return data.addAll(c);
    }

    public void add(int index, T element) {
        data.add(index, element);
    }

    public boolean add(T e) {
        return data.add(e);
    }

}
