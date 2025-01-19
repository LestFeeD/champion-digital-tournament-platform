package zChampions.catalogue.enumsEntities;

public enum TypeStandingsEnum {
    OLYMPIC("Олимпийская"),
    BEST_TIME("Лучшие время");

    private final String displayName;

    TypeStandingsEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
