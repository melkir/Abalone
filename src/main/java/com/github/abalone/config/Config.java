package com.github.abalone.config;

import com.github.abalone.util.listeners.ValueListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author melkir
 */
public class Config {
    static private Config singleton;
    private final HashMap<String, Value> conf;

    private Config() {
        this.conf = new HashMap<String, Value>();
        this.conf.put("AI", new Value<Boolean>("Human vs AI", true));
        this.conf.put("theme", new Theme("Theme", "glossy"));
        // TODO Ajouter le choix d'un algo IA et de sa profondeur
        this.conf.put("algo", new IA());
        this.conf.put("max_depth", new Value<String>("Profondeur Max.", "3"));
    }

    static private HashMap<String, Value> getConf() {
        return (Config.singleton == null) ? new Config().conf : Config.singleton.conf;
    }

    static public Object get(String name) {
        return (name == null || !getConf().containsKey(name)) ? null : getConf().get(name).get();
    }

    static public Map<String, Value> getConfig() {
        return Collections.unmodifiableMap(getConf());
    }

    static public void addValueListener(ValueListener listener) {
        getConf().get("theme").addValueListener(listener);
    }
}
