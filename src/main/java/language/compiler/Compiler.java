// 
// Decompiled by Procyon v0.5.36
// 

package language.compiler;

import game.Game;
import grammar.Description;
import grammar.Report;
import grammar.Token;
import language.compiler.exceptions.CantDecomposeException;
import language.compiler.exceptions.CompilerErrorWithMessageException;
import language.compiler.exceptions.CompilerException;
import language.compiler.exceptions.NullGameException;
import language.grammar.Grammar;
import language.parser.Parser;
import main.StringRoutines;
import metadata.Metadata;
import options.UserSelections;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Compiler
{
    private Compiler() {
    }
    
    public static Game compileTest(final Description description, final boolean isVerbose) {
        final Game game = compile(description, new UserSelections(new ArrayList<>()), new Report(), isVerbose);
        if (game == null) {
            System.out.println("** Compiler.compileTest(): Game compiled but returned null after initialisation.");
        }
        return game;
    }
    
    public static Game compile(final Description description, final UserSelections userSelections, final Report report, final boolean isVerbose) {
        try {
            return compiler(description, userSelections, report, isVerbose);
        }
        catch (CompilerException e) {
            if (isVerbose) {
                e.printStackTrace();
            }
            throw new CompilerException(e.getMessageBody(description.raw()), e);
        }
        catch (Exception e2) {
            e2.printStackTrace();
            throw new IllegalArgumentException(e2);
        }
    }
    
    private static Game compiler(final Description description, final UserSelections userSelections, final Report report, final boolean isVerbose) {
        if (isVerbose) {
            System.out.println("+++++++++++++++++++++\nCompiling:\n" + description.raw());
        }
        Parser.expandAndParse(description, userSelections, report, isVerbose);
        if (report.isError()) {
            System.out.println("Failed to parse game description:");
            for (final String error : report.errors()) {
                System.out.println("* " + error);
            }
            final StringBuilder sb = new StringBuilder();
            for (final String error2 : report.errors()) {
                sb.append(error2).append("\n");
            }
            for (final String warning : report.warnings()) {
                sb.append("Warning: ").append(warning).append("\n");
            }
            for (final String note : report.notes()) {
                sb.append("Note: ").append(note).append("\n");
            }
            throw new CompilerErrorWithMessageException(sb.toString());
        }
        Game game = (Game)compileTask(description.expanded(), "game", "game.Game", report, isVerbose);
        if (game == null) {
            System.out.println("** Compiler.compiler(): Could not compile game.");
            return null;
        }


        Metadata md = null;
        try {
            if (description.metadata() != null && !description.metadata().isEmpty()) {
                md = (Metadata) compileTask(description.metadata(), "metadata", "metadata.Metadata", report, isVerbose);
            }
        } catch (Exception e) {
            //continue without having parsed metadata
            e.printStackTrace();
            md = null;
        }
        if (md == null) {
            md = new Metadata(null, null, null);
        }

        game.setDescription(description);
        game.create();
        game.setMetadata(md);
        return game;
    }
    
    public static Object compileObject(final String strIn, final String className, final Report report) {
        final String[] classNameSplit = className.split(Pattern.quote("."));
        final String symbolName = StringRoutines.lowerCaseInitial(classNameSplit[classNameSplit.length - 1]);
        return compileObject(strIn, symbolName, className, report);
    }
    
    public static Object compileObject(final String strIn, final String symbolName, final String className, final Report report) {
        return compileTask(strIn, symbolName, className, report, false);
    }
    
    private static Object compileTask(final String strIn, final String symbolName, final String className, final Report report, final boolean isVerbose) {
        final Token tokenTree = new Token(strIn, report);
        if (isVerbose) {
            System.out.println("\nCompiler.compileTask() token tree:\n" + tokenTree);
        }
        if (tokenTree.type() == null) {
            System.out.println("** Compiler.compileTask(): Null token tree.");
            if (symbolName.equals("game")) {
                throw new CantDecomposeException("CompilercompileTask()");
            }
            return null;
        }
        else {
            final ArgClass rootClass = (ArgClass)Arg.createFromToken(Grammar.grammar(), tokenTree);
            if (rootClass == null) {
                throw new NullGameException();
            }
            if (isVerbose) {
                System.out.println("\nRoot:" + rootClass);
            }
            final Grammar grammar = Grammar.grammar();
            if (!rootClass.matchSymbols(grammar, report)) {
                System.out.println("Compiler.compileTask(): Failed to match symbols.");
                throw new CompilerErrorWithMessageException("Failed to match symbols when compiling.");
            }
            Class<?> clsRoot = null;
            try {
                clsRoot = Class.forName(className);
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            final Object result = rootClass.compile(clsRoot, isVerbose ? 0 : -1);
            if (result == null) {
                System.out.println("Compiler.compileTask(): Null result from compiling root ArgClass object.");
                throw new NullGameException();
            }
            return result;
        }
    }
}
