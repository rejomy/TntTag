package me.rejomy.tnttag.util.person;

import java.util.Arrays;
import java.util.List;

import static me.rejomy.tnttag.util.RandomUtil.RANDOM;

public class PersonBuilder {
    private String name;
    private List<String> phrases;
    private boolean canModifyName;
    public PersonBuilder(String name, boolean canModifyName, String... phrases) {
        this.name = name;
        this.phrases = Arrays.asList(phrases);
        this.canModifyName = canModifyName;
    }

    public PersonBuilder(String name, String... phrases) {
        this.name = name;
        this.phrases = Arrays.asList(phrases);
    }

    public String getName() {
        return canModifyName && RANDOM.nextBoolean()? getNameWithPrefix(name) : name;
    }

    public String getDefaultName() {
        return name;
    }

    public String getPhrase() {
        return phrases.get(RANDOM.nextInt(phrases.size()));
    }

    private String getNameWithPrefix(String currentName) {
        int currentNameLength = currentName.length();
        String[] ADDITIONAL_NAMES = PersonManager.ADDITIONAL_NAMES;

        for(String name : ADDITIONAL_NAMES) {
            if(name.length() + currentNameLength > 16) {
                continue;
            }

            boolean shouldAddSpace = name.length() + currentNameLength + 1 > 16;

            if(RANDOM.nextBoolean()) {
                return shouldAddSpace? currentName + "_" + name : currentName + name;
            } else {
                return shouldAddSpace? name + "_" + currentName : name + currentName;
            }
        }

        return currentName;
    }
}
