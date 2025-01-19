package zChampions.catalogue.enumsEntities;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

public class SportDisciplineMapper {
    private static final EnumMap<KindOfSport, Set<Disciplines>> sportDisciplineMap = new EnumMap<>(KindOfSport.class);

    static {
        sportDisciplineMap.put(KindOfSport.SWIMMING, EnumSet.of(
                Disciplines.BREASTSTROKE_FIFTY_METERS,
                Disciplines.BREASTSTROKE_ONE_HUNDRED_METERS,
                Disciplines.BREASTSTROKE_TWO_HUNDRED_METERS
        ));

        sportDisciplineMap.put(KindOfSport.FOOTBALL, EnumSet.of(
                Disciplines.FOOTBALL,
                Disciplines.BEACH_FOOTBALL,
                Disciplines.MINIMAL_FOOTBALL
        ));

        sportDisciplineMap.put(KindOfSport.BASKETBALL, EnumSet.of(
                Disciplines.BASKETBALL,
                Disciplines.BASKETBALL_3x3,
                Disciplines.MINIMAL_BASKETBALL
        ));

        sportDisciplineMap.put(KindOfSport.HOCKEY, EnumSet.of(
                Disciplines.HOCKEY
        ));
    }

    public static boolean isValidDiscipline(KindOfSport typeOfSport, Disciplines discipline) {
        Set<Disciplines> disciplines = sportDisciplineMap.get(typeOfSport);
        return disciplines != null && disciplines.contains(discipline);
    }
}
