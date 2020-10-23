// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.experiments.ludemes;

import language.grammar.ClassEnumerator;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class CountLudemes
{
    private final List<Record> records;
    private String result;
    
    public CountLudemes() {
        this.records = new ArrayList<>();
        this.result = "No count yet.";
        this.prepareCategories();
        this.countLudemes();
    }
    
    public String result() {
        return this.result;
    }
    
    void prepareCategories() {
        this.records.clear();
        try {
            this.records.add(new Record("Ludeme classes", Class.forName("util.Ludeme")));
            this.records.add(new Record("Integer functions", Class.forName("game.functions.ints.BaseIntFunction")));
            this.records.add(new Record("Boolean functions", Class.forName("game.functions.booleans.BaseBooleanFunction")));
            this.records.add(new Record("Region functions", Class.forName("game.functions.region.RegionFunction")));
            this.records.add(new Record("Equipment (total)", Class.forName("game.equipment.Item")));
            this.records.add(new Record("Containers", Class.forName("game.equipment.container.Container")));
            this.records.add(new Record("Components", Class.forName("game.equipment.component.Component")));
            this.records.add(new Record("Start rules", Class.forName("game.rules.start.StartRule")));
            this.records.add(new Record("Moves rules", Class.forName("game.rules.play.moves.Moves")));
            this.records.add(new Record("End rules", Class.forName("game.rules.end.End")));
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public String countLudemes() {
        final String rootPackage = "game.Game";
        for (final Record record : this.records) {
            record.reset();
        }
        Class<?> clsRoot = null;
        try {
            clsRoot = Class.forName("game.Game");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return "Couldn't find root package \"game\".";
        }
        final ArrayList<Class<?>> classes = (ArrayList<Class<?>>) ClassEnumerator.getClassesForPackage(clsRoot.getPackage());
        Class<?> clsLudeme = null;
        try {
            clsLudeme = Class.forName("util.Ludeme");
        }
        catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        }
        int numEnums = 0;
        int numEnumConstants = 0;
        for (final Class<?> cls : classes) {
            for (final Record record2 : this.records) {
                if (clsLudeme.isAssignableFrom(cls) && record2.category().isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers()) && !cls.getName().contains("$")) {
                    record2.increment();
                    if (!record2.label().equals("Ludeme classes")) {
                        continue;
                    }
                    System.out.println("+ " + cls.getSimpleName());
                }
            }
            if (cls.isEnum()) {
                ++numEnums;
                numEnumConstants += cls.getEnumConstants().length;
            }
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (final Record record3 : this.records) {
            sb.append(record3.label() + ": " + record3.count() + "\n");
        }
        sb.append("Enum classes: " + numEnums + "\n");
        sb.append("Enum constants: " + numEnumConstants + "\n");
        sb.append("\n");
        return this.result = sb.toString();
    }
    
    class Record
    {
        private final String label;
        private final Class<?> category;
        private int count;
        
        public Record(final String label, final Class<?> category) {
            this.count = 0;
            this.label = label;
            this.category = category;
        }
        
        public String label() {
            return this.label;
        }
        
        public Class<?> category() {
            return this.category;
        }
        
        public int count() {
            return this.count;
        }
        
        public void reset() {
            this.count = 0;
        }
        
        public void increment() {
            ++this.count;
        }
    }
}
