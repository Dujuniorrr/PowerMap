package com.ifbaiano.powermap.appearance;

public class GenericAppearance {

    public static String capitalizedText(String nome) {
        if (nome != null && !nome.isEmpty()) {
            return nome.substring(0, 1).toUpperCase() + nome.substring(1);
        } else {
            return "";
        }
    }
}

