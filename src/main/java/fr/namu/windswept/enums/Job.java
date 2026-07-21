package fr.namu.windswept.enums;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public enum Job {
    FARMER("Fermier"),
    MINER("Mineur"),
    ;

    private String name;

    Job(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

