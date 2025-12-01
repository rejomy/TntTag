package me.rejomy.tnttag.util.person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import static me.rejomy.tnttag.util.RandomUtil.RANDOM;

public class PersonManager {

    private static final List<PersonBuilder> PERSONS = new ArrayList<>();
    public static final String[] ADDITIONAL_NAMES = {"Pvp", "Pro", "Sad", "Bro", "Top", "GOD", "Angel"};

    static {
        PERSONS.add(new PersonBuilder("halle6ja", true, "Девочки, вы упали!"));
        PERSONS.add(new PersonBuilder("Miss04ka_", "Мисок не бьют!"));
        PERSONS.add(new PersonBuilder("FISHA", "Оптимизация рыбы!"));
        PERSONS.add(new PersonBuilder("F3F5",
                "Я тебя задудосю!", "В чс кину!", "Ну блять, снова тнт," +
                " калл сервер..."));
        PERSONS.add(new PersonBuilder("hevav", true,
                "Спокойной ночи малыши!"));
        PERSONS.add(new PersonBuilder("zoomov", true,
                "Завезли свапа ребята!"));
        PERSONS.add(new PersonBuilder("Kumori", "Унфартит чето( И опять я тнт..."));
    }

    public static PersonBuilder[] getPersons(int amount) {
        List<PersonBuilder> copyPersons = new ArrayList<>(PERSONS);
        PersonBuilder[] persons = new PersonBuilder[amount];

        for (byte a = 0; a < amount; a++) {
            int personIndex = RANDOM.nextInt(copyPersons.size());
            persons[a] = copyPersons.get(personIndex);
            copyPersons.remove(personIndex);
        }

        return persons;
    }

    public static PersonBuilder getPersonByName(String personName) {
        for (PersonBuilder person : PERSONS) {
            if (personName.contains(person.getDefaultName())) {
                return person;
            }
        }
        return null;
    }
}
