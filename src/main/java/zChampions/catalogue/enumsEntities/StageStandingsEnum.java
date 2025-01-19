package zChampions.catalogue.enumsEntities;

public enum StageStandingsEnum {
    ONE_EIGHTH_FINAL("1/8"),
    ONE_FOURTH_FINAL("1/4"),
    SEMIFINAL("полуфинал"),
    FINAL("финал");

    private final String displayName;

    StageStandingsEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
