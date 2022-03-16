/*
 * 
 */
package me.gugafenix.legionmc.glad.utils;

import java.util.Comparator;
import java.util.Map;

import me.HClan.Objects.Clan;

public class ValueComparator implements Comparator<Clan> {

    private Map<Clan, Integer> base;
    
    public ValueComparator(Map<Clan, Integer> base) {
        this.base = base;
    }

    @Override
    public int compare(Clan a, Clan b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }

}