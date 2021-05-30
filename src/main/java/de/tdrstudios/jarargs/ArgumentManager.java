package de.tdrstudios.jarargs;

import java.beans.ParameterDescriptor;
import java.util.*;
import java.util.function.Consumer;

public class ArgumentManager {

    public ArgumentManager(String[] args) {
        for (String s : args) {
            if(!jarArguments.contains(s))
                jarArguments.add(s);
        }
    }
    private List<String> jarArguments = new ArrayList<>();
    private List<String> registerdArguments = new ArrayList<>();

    public List<String> getJarArguments() {
        return jarArguments;
    }
    public boolean hasArgument(String argument) {
        return  getJarArguments().contains(argument);
    }

    public boolean isRegisterd(JarArgument jarArgument) {
        return registerdArguments.contains(jarArgument);
    }
    public boolean isRegisterd(String jarArgument) {
        return registerdArguments.contains(jarArgument);
    }

    public void registerArgument(String argument) {
        if(registerdArguments.contains(argument))
            return;
        registerdArguments.add(argument);
    }
}
