package jp.naist.cl.srparser.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * jp.naist.cl.srparser.util
 *
 * @author Hiroki Teranishi
 */
public class CmdLineArgs {
    private final List<String> params = new ArrayList<>();
    private final LinkedHashMap<String, String> options = new LinkedHashMap<>();
    private final String[] args;
    private final int argc;

    public CmdLineArgs(String args[]) {
        this.args = args;
        if (args != null) {
            this.argc = args.length;
            parse();
        } else {
            argc = 0;
        }
    }

    private void parse() {
        Pattern p = Pattern.compile("^-(-)?([^=\\s]+)(?:[=\\s](.*))?$");
        for (int i = 0; i < argc; i++) {
            String input = args[i];
            Matcher m = p.matcher(input);
            if (!m.find()) {
                params.add(input);
                continue;
            }
            String prefix = m.group(1);
            String name = m.group(2);
            ArrayDeque<String> names;
            if (prefix != null && prefix.equals("-")) {
                names = new ArrayDeque<>(Arrays.asList(new String[]{name}));
            } else {
                names = new ArrayDeque<>(Arrays.asList(name.split("")));
            }
            name = names.removeLast();
            String value;
            if (m.groupCount() == 3 && (value = m.group(3)) != null) {
                options.put(name, value);
            } else if (i + 1 < argc) {
                String next = args[i + 1];
                if (!next.startsWith("-")) {
                    options.put(name, next);
                    i++;
                } else {
                    options.put(name, "");
                }
            } else {
                options.put(name, "");
            }
            for (String n : names) {
                options.put(n, "");
            }
        }
    }

    public int getParamSize() {
        return params.size();
    }

    public String getParam(int index) {
        return params.get(index);
    }

    public String getParamOrDefalut(int index, String defaultValue) {
        try {
            return params.get(index);
        } catch (IndexOutOfBoundsException e) {
            return defaultValue;
        }
    }

    public boolean hasOption(String key) {
        return options.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getOptions() {
        return (Map<String, String>) options.clone();
    }

    public String getOption(String key) {
        return options.get(key);
    }

    public String getOptionOrDefault(String key, String defaultValue) {
        return options.getOrDefault(key, defaultValue);
    }

    public int size() {
        return argc;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("params=").append(params);
        sb.append(", options=").append(options);
        return sb.toString();
    }
}
