package fr.namu.windswept.enums;

public enum Job {
    FARMER("Fermier"),
    MINER("Mineur"),
    ;

    private final String name;

    Job(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

