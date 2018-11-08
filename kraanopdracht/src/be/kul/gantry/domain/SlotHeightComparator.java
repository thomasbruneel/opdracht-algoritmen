package be.kul.gantry.domain;

import java.util.Comparator;

public class SlotHeightComparator implements Comparator<Slot> {

    @Override
    public int compare(Slot o1, Slot o2) {
        return Integer.compare(o2.getZ(), o1.getZ());
    }
}
