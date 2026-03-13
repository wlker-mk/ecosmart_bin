package com.ecosmart.ecosmart_bin.entity;

public enum TypePlastique {
    PET(5),    // bouteilles eau — 5 pts
    HDPE(4),   // bidons — 4 pts
    PVC(3),    // tuyaux — 3 pts
    LDPE(2),   // sacs — 2 pts
    PP(3),     // boites — 3 pts
    PS(2),     // gobelets — 2 pts
    AUTRE(1);  // plastique non classifié — 1 pt

    private final int points;

    TypePlastique(int points) { this.points = points; }

    public int getPoints() { return points; }
}