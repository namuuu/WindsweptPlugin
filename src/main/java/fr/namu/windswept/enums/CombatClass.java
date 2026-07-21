package fr.namu.windswept.enums;

public enum CombatClass {
    SWORDSMAN("Epeiste"),
    LANCEBEARER("Lancier"),
    MAGE("Mage"),
    ROGUE("Voleur")
    ;

    private String name;

    CombatClass(String name) {
        this.name = name;
    }
}

